package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import java.io.IOException;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.audio.AudioHelper;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.robot.finch.AbstractFinchController;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.serial.BaudRate;
import edu.cmu.ri.createlab.TeRK.serial.CharacterSize;
import edu.cmu.ri.createlab.TeRK.serial.FlowControl;
import edu.cmu.ri.createlab.TeRK.serial.Parity;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOConfiguration;
import edu.cmu.ri.createlab.TeRK.serial.StopBits;
import edu.cmu.ri.createlab.serial.SerialPortCommandExecutionQueue;
import edu.cmu.ri.createlab.serial.SerialPortCommandResponse;
import edu.cmu.ri.createlab.serial.SerialPortCommandStrategy;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.mrpl.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultFinchController extends AbstractFinchController
   {
   private static final Logger LOG = Logger.getLogger(DefaultFinchController.class);

   private final SerialPortCommandExecutionQueue commandQueue;
   private final SerialPortCommandStrategy disconnectCommandStrategy = new DisconnectCommandStrategy();
   private final SerialPortCommandStrategy getStateCommandStrategy = new GetStateCommandStrategy();
   private final SerialPortCommandStrategy emergencyStopCommandStrategy = new EmergencyStopCommandStrategy();
   private final SerialPortCommandStrategy getAccelerometerCommandStrategy = new GetAccelerometerCommandStrategy();
   private final SerialPortCommandStrategy getMotorPositionCommandStrategy = new GetMotorPositionCommandStrategy();
   private final SerialPortCommandStrategy getObstacleSensorCommandStrategy = new GetObstacleSensorCommandStrategy();
   private final SerialPortCommandStrategy getPhotoresistorCommandStrategy = new GetPhotoresistorCommandStrategy();
   private final SerialPortCommandStrategy getThermistorCommandStrategy = new GetThermistorCommandStrategy();

   public DefaultFinchController(final String serialPortName) throws IOException, SerialPortException
      {
      super(serialPortName);

      // create the serial port configuration
      final SerialIOConfiguration config = new SerialIOConfiguration(serialPortName,
                                                                     BaudRate.BAUD_57600,
                                                                     CharacterSize.EIGHT,
                                                                     Parity.NONE,
                                                                     StopBits.ONE,
                                                                     FlowControl.NONE);

      // create the serial port command queue
      final SerialPortCommandExecutionQueue commandQueueTemp = SerialPortCommandExecutionQueue.create(this.getClass().getName(), config);

      // see whether its creation was successful
      if (commandQueueTemp == null)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("DefaultFinchController.create(): Failed to open serial port '" + serialPortName + "'");
            }
         throw new SerialPortException("Failed to open serial port '" + serialPortName + "'");
         }
      else
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("DefaultFinchController.create(): Serial port '" + serialPortName + "' opened.");
            }

         // now try to do the handshake with the finch to establish communication
         final boolean wasHandshakeSuccessful = commandQueueTemp.executeAndReturnStatus(new HandshakeCommandStrategy());

         // see if the handshake was a success
         if (wasHandshakeSuccessful)
            {
            LOG.info("DefaultFinchController.create(): Finch handshake successful!");

            this.commandQueue = commandQueueTemp;
            }
         else
            {
            LOG.error("DefaultFinchController.create(): Failed to handshake with finch");

            // the handshake failed, so shutdown the command queue to release the serial port
            commandQueueTemp.shutdown();

            throw new SerialPortException("Failed to handshake with finch");
            }
         }
      }

   public FinchState getState()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getStateCommandStrategy);

      if (response != null)
         {
         return FinchStateImpl.create(response.getData());
         }

      return null;
      }

   public AccelerometerState getAccelerometerState()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getAccelerometerCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new AccelerometerState(ByteUtils.unsignedByteToInt(responseData[0]),
                                       ByteUtils.unsignedByteToInt(responseData[1]),
                                       ByteUtils.unsignedByteToInt(responseData[2]));
         }

      return null;
      }

   public boolean[] areObstaclesDetected()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getObstacleSensorCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new boolean[]{ByteUtils.unsignedByteToInt(responseData[0]) == 1,
                              ByteUtils.unsignedByteToInt(responseData[1]) == 1};
         }

      return null;
      }

   public int[] getPhotoresistors()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getPhotoresistorCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new int[]{ByteUtils.unsignedByteToInt(responseData[0]),
                          ByteUtils.unsignedByteToInt(responseData[1])};
         }

      return null;
      }

   public Integer getThermistor(final int id)
      {
      if (id >= 0 && id < FinchConstants.THERMISTOR_DEVICE_COUNT)
         {
         final SerialPortCommandResponse response = commandQueue.execute(getThermistorCommandStrategy);

         if (response.wasSuccessful())
            {
            return ByteUtils.unsignedByteToInt(response.getData()[0]);
            }
         }

      return null;
      }

   public int[] getCurrentMotorPositions()
      {
      final SerialPortCommandResponse response = commandQueue.execute(getMotorPositionCommandStrategy);

      if (response.wasSuccessful())
         {
         final byte[] responseData = response.getData();
         return new int[]{ByteUtils.bytesToShort(responseData[0], responseData[1]),
                          ByteUtils.bytesToShort(responseData[2], responseData[3])};
         }

      return null;
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return commandQueue.executeAndReturnStatus(new FullColorLEDCommandStrategy(red, green, blue));
      }

   public boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      return commandQueue.executeAndReturnStatus(new MotorPositionCommandStrategy(leftPositionDelta,
                                                                                  rightPositionDelta,
                                                                                  leftSpeed,
                                                                                  rightSpeed));
      }

   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return commandQueue.executeAndReturnStatus(new MotorVelocityCommandStrategy(leftVelocity, rightVelocity));
      }

   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return commandQueue.executeAndReturnStatus(new BuzzerCommandStrategy(frequency, durationInMilliseconds));
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      AudioHelper.playTone(frequency, amplitude, duration);
      }

   public void playClip(final byte[] data)
      {
      AudioHelper.playClip(data);
      }

   public boolean emergencyStop()
      {
      return commandQueue.executeAndReturnStatus(emergencyStopCommandStrategy);
      }

   public void disconnect()
      {
      LOG.debug("DefaultFinchController.disconnect(): Now attempting to send the disconnect command to the finch");
      if (commandQueue.executeAndReturnStatus(disconnectCommandStrategy))
         {
         LOG.debug("DefaultFinchController.disconnect(): Successfully disconnected from the finch.");
         }
      else
         {
         LOG.error("DefaultFinchController.disconnect(): Failed to disconnect from the finch.");
         }
      }

   public void shutdown()
      {
      commandQueue.shutdown();
      }
   }
