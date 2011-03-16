package edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.TeRK.audio.AudioHelper;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.serial.BaudRate;
import edu.cmu.ri.createlab.TeRK.serial.CharacterSize;
import edu.cmu.ri.createlab.TeRK.serial.FlowControl;
import edu.cmu.ri.createlab.TeRK.serial.Parity;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOConfiguration;
import edu.cmu.ri.createlab.TeRK.serial.StopBits;
import edu.cmu.ri.createlab.serial.SerialPortCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdProxy implements SerialDeviceProxy
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdProxy.class);

   public static final String APPLICATION_NAME = "HummingbirdProxy";
   private static final int DELAY_BETWEEN_PEER_PINGS = 2;

   /**
    * Tries to create a <code>HummingbirdProxy</code> by connecting to a hummingbird on the serial port specified by
    * the given <code>serialPortName</code>.  Returns <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   public static HummingbirdProxy create(final String serialPortName) throws IOException, SerialPortException
      {
      // a little error checking...
      if (serialPortName == null)
         {
         throw new IllegalArgumentException("The serial port name may not be null");
         }

      // create the serial port configuration
      final SerialIOConfiguration config = new SerialIOConfiguration(serialPortName,
                                                                     BaudRate.BAUD_19200,
                                                                     CharacterSize.EIGHT,
                                                                     Parity.NONE,
                                                                     StopBits.ONE,
                                                                     FlowControl.NONE);

      // create the serial port command queue
      final SerialPortCommandExecutionQueue commandQueue = SerialPortCommandExecutionQueue.create(APPLICATION_NAME, config);

      // see whether its creation was successful
      if (commandQueue == null)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Failed to open serial port '" + serialPortName + "'");
            }
         }
      else
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Serial port '" + serialPortName + "' opened.");
            }

         // now try to do the handshake with the hummingbird to establish communication
         final boolean wasHandshakeSuccessful = commandQueue.executeAndReturnStatus(new HandshakeCommandStrategy());

         // see if the handshake was a success
         if (wasHandshakeSuccessful)
            {
            LOG.info("Hummingbird handshake successful!");

            // now create and return the proxy
            return new HummingbirdProxy(commandQueue);
            }
         else
            {
            LOG.error("Failed to handshake with hummingbird");
            }

         // the handshake failed, so shutdown the command queue to release the serial port
         commandQueue.shutdown();
         }

      return null;
      }

   private final SerialPortCommandExecutionQueue commandQueue;
   private final DisconnectCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final GetStateCommandStrategy getStateCommandStrategy = new GetStateCommandStrategy();
   private final EmergencyStopCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();
   private final Map<Integer, GetAnalogInputCommandStrategy> analogInputCommandStategyMap = new HashMap<Integer, GetAnalogInputCommandStrategy>();
   private final ScheduledExecutorService peerPingScheduler = Executors.newScheduledThreadPool(1);
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final HummingbirdPinger hummingbirdPinger = new HummingbirdPinger();
   private final Collection<SerialDevicePingFailureEventListener> serialDevicePingFailureEventListeners = new HashSet<SerialDevicePingFailureEventListener>();

   /**
    * Creates a new <code>HummingbirdProxy</code> using the given {@link SerialPortCommandExecutionQueue}.
    */
   private HummingbirdProxy(final SerialPortCommandExecutionQueue commandQueue)
      {
      this.commandQueue = commandQueue;

      // initialize the analog input command strategy map
      for (int i = 0; i < HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT; i++)
         {
         analogInputCommandStategyMap.put(i, new GetAnalogInputCommandStrategy(i));
         }

      // schedule periodic peer pings
      peerPingScheduledFuture = peerPingScheduler.scheduleAtFixedRate(hummingbirdPinger,
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay before first ping
                                                                      DELAY_BETWEEN_PEER_PINGS, // delay between pings
                                                                      TimeUnit.SECONDS);
      }

   public void addSerialDevicePingFailureEventListener(final SerialDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         serialDevicePingFailureEventListeners.add(listener);
         }
      }

   public void removeSerialDevicePingFailureEventListener(final SerialDevicePingFailureEventListener listener)
      {
      if (listener != null)
         {
         serialDevicePingFailureEventListeners.remove(listener);
         }
      }

   /**
    * Gets the hummingbird's state.  Returns <code>null</code> if an error occurred while getting the state.
    */
   public HummingbirdState getState()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getStateCommandStrategy);

      if (response != null)
         {
         return HummingbirdStateImpl.create(response.getData());
         }

      return null;
      }

   /**
    * Returns the value of the given port id; returns <code>-1</code> if an error occurred while trying to read the value.
    *
    * @throws IllegalArgumentException if the <code>analogInputPortId</code> specifies an invalid port
    */
   public int getAnalogInputValue(final int analogInputPortId)
      {
      final GetAnalogInputCommandStrategy strategy = analogInputCommandStategyMap.get(analogInputPortId);

      if (strategy != null)
         {
         final SerialPortCommandResponse response = commandQueue.execute(strategy);

         if (response.wasSuccessful())
            {
            return ByteUtils.unsignedByteToInt(response.getData()[0]);
            }

         return -1;
         }

      throw new IllegalArgumentException("Invalid analog input port id: [" + analogInputPortId + "]");
      }

   /**
    * Sets the motor specified by the given <code>motorId</code> to the given (signed) <code>velocity</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param motorId the motor to control [0 or 1]
    * @param velocity the signed velocity [-255 to 255]
    */
   public boolean setMotorVelocity(final int motorId, final int velocity)
      {
      return commandQueue.executeAndReturnStatus(new MotorCommandStrategy(motorId, velocity));
      }

   /**
    * Sets the motors specified by the given <code>mask</code> to the given <code>velocities</code>.  Returns
    * the current velocities as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   public int[] setMotorVelocities(final boolean[] mask, final int[] velocities)
      {
      if (commandQueue.executeAndReturnStatus(new MotorCommandStrategy(mask, velocities)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getMotorVelocities();
            }
         }

      return null;
      }

   /**
    * Sets the vibration motor specified by the given <code>motorId</code> to the given <code>speed</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param motorId the motor to control [0 or 1]
    * @param speed the speed [0 to 255]
    */
   public boolean setVibrationMotorSpeed(final int motorId, final int speed)
      {
      return commandQueue.executeAndReturnStatus(new VibrationMotorCommandStrategy(motorId, speed));
      }

   /**
    * Sets the vibration motors specified by the given <code>mask</code> to the given <code>speeds</code>.  Returns
    * the current speeds as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   public int[] setVibrationMotorSpeeds(final boolean[] mask, final int[] speeds)
      {
      if (commandQueue.executeAndReturnStatus(new VibrationMotorCommandStrategy(mask, speeds)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getVibrationMotorSpeeds();
            }
         }

      return null;
      }

   /**
    * Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param servoId the servo to control [0 to 3]
    * @param position the position [0 to 255]
    */
   public boolean setServoPosition(final int servoId, final int position)
      {
      return commandQueue.executeAndReturnStatus(new ServoCommandStrategy(servoId, position));
      }

   /**
    * Sets the servo motors specified by the given <code>mask</code> to the given <code>positions</code>.  Returns
    * the current positions as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   public int[] setServoPositions(final boolean[] mask, final int[] positions)
      {
      if (commandQueue.executeAndReturnStatus(new ServoCommandStrategy(mask, positions)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getServoPositions();
            }
         }

      return null;
      }

   /**
    * Sets the LED specified by the given <code>ledId</code> to the given <code>intensity</code>.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param ledId the LED to control [0 or 1]
    * @param intensity the intensity [0 to 255]
    */
   public boolean setLED(final int ledId, final int intensity)
      {
      return commandQueue.executeAndReturnStatus(new LEDCommandStrategy(ledId, intensity));
      }

   /**
    * Sets the LEDs specified by the given <code>mask</code> to the given <code>intensities</code>.
    * Returns the current intensities as an array of integers if the command succeeded, <code>null</code> otherwise.
    */
   public int[] setLEDs(final boolean[] mask, final int[] intensities)
      {
      if (commandQueue.executeAndReturnStatus(new LEDCommandStrategy(mask, intensities)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getLedIntensities();
            }
         }

      return null;
      }

   /**
    * Sets the full-color LED specified by the given <code>ledId</code> to the given red, green, and blue intensities.
    * Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param ledId the LED to control [0 or 1]
    * @param red the intensity of the LED's red component [0 to 255]
    * @param green the intensity of the LED's green component [0 to 255]
    * @param blue the intensity of the LED's blue component [0 to 255]
    */
   public boolean setFullColorLED(final int ledId, final int red, final int green, final int blue)
      {
      return commandQueue.executeAndReturnStatus(new FullColorLEDCommandStrategy(ledId, red, green, blue));
      }

   /**
    * Sets the full-color LEDs specified by the given <code>mask</code> to the given {@link Color colors}. Returns the
    * current colors as an array of {@link Color colors} if the command succeeded, <code>null</code> otherwise.
    */
   public Color[] setFullColorLEDs(final boolean[] mask, final Color[] colors)
      {
      if (commandQueue.executeAndReturnStatus(new FullColorLEDCommandStrategy(mask, colors)))
         {
         final HummingbirdState state = getState();
         if (state != null)
            {
            return state.getFullColorLEDs();
            }
         }

      return null;
      }

   /** Plays a tone having the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>. */
   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   /** Plays the sound clip contained in the given <code>byte</code> array. */
   public void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   /**
    * Turns off all motors, vibrations motors, LEDs, and full-color LEDs. Returns <code>true</code> if the command
    * succeeded, <code>false</code> otherwise.
    */
   public boolean emergencyStop()
      {
      return commandQueue.executeAndReturnStatus(emergencyStopCommandStrategy);
      }

   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("HummingbirdProxy.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the peer pinger
      try
         {
         peerPingScheduledFuture.cancel(false);
         peerPingScheduler.shutdownNow();
         LOG.debug("HummingbirdProxy.disconnect(): Successfully shut down hummingbird pinger.");
         }
      catch (Exception e)
         {
         LOG.error("HummingbirdProxy.disconnect(): Exception caught while trying to shut down peer pinger", e);
         }

      // optionally send goodbye command to the hummingbird
      if (willAddDisconnectCommandToQueue)
         {
         LOG.debug("HummingbirdProxy.disconnect(): Now attempting to send the disconnect command to the hummingbird");
         if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
            {
            LOG.debug("HummingbirdProxy.disconnect(): Successfully disconnected from the hummingbird.");
            }
         else
            {
            LOG.error("HummingbirdProxy.disconnect(): Failed to disconnect from the hummingbird.");
            }
         }

      // shut down the command queue, which closes the serial port
      commandQueue.shutdown();
      }

   private class HummingbirdPinger implements Runnable
      {
      public void run()
         {
         try
            {
            // for pings, we simply get the state
            final HummingbirdState state = getState();

            // if the state is null, then we know we have a problem so disconnect (which probably won't work) and then
            // notify the listeners
            if (state == null)
               {
               try
                  {
                  LOG.debug("HummingbirdPinger.run(): Peer ping failed (received a null state).  Attempting to disconnect...");
                  disconnect(false);
                  LOG.debug("HummingbirdPinger.run(): Done disconnecting from the hummingbird");
                  }
               catch (Exception e)
                  {
                  LOG.error("HummingbirdPinger.run(): Exeption caught while trying to disconnect from the hummingbird", e);
                  }

               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("HummingbirdPinger.run(): Notifying " + serialDevicePingFailureEventListeners.size() + " listeners of ping failure...");
                  }
               for (final SerialDevicePingFailureEventListener listener : serialDevicePingFailureEventListeners)
                  {
                  try
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("   HummingbirdPinger.run(): Notifying " + listener);
                        }
                     listener.handlePingFailureEvent();
                     }
                  catch (Exception e)
                     {
                     LOG.error("HummingbirdPinger.run(): Exeption caught while notifying SerialDevicePingFailureEventListener", e);
                     }
                  }
               }
            }
         catch (Exception e)
            {
            LOG.error("HummingbirdPinger.run(): Exception caught while executing the peer pinger", e);
            }
         }
      }
   }