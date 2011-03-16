package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchController;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchControllerFactory;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchProxy implements SerialDeviceProxy
   {
   private static final Logger LOG = Logger.getLogger(FinchProxy.class);

   public static final String APPLICATION_NAME = "FinchProxy";
   private static final int DELAY_BETWEEN_PEER_PINGS = 2;

   /**
    * Tries to create a <code>FinchProxy</code> by using {@link FinchControllerFactory} to create a
    * {@link FinchController} for the the serial port specified by the given <code>serialPortName</code>. Returns
    * <code>null</code> if the connection could not be established.
    *
    * @param serialPortName - the name of the serial port device which should be used to establish the connection
    *
    * @throws IllegalArgumentException if the <code>serialPortName</code> is <code>null</code>
    */
   public static FinchProxy create(final String serialPortName)
      {
      LOG.debug("FinchProxy.create()");

      // a little error checking...
      if (serialPortName == null)
         {
         throw new IllegalArgumentException("FinchProxy.create(): The serial port name may not be null");
         }

      // use the factory to create a FinchController
      final FinchController finchController;
      try
         {
         finchController = FinchControllerFactory.getInstance().create(serialPortName);

         // if the FinchController was created successfully, then create the FinchProxy
         if (finchController != null)
            {
            return new FinchProxy(finchController);
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to create a FinchController", e);
         }

      return null;
      }

   private final ScheduledExecutorService peerPingScheduler = Executors.newScheduledThreadPool(1, new DaemonThreadFactory("FinchProxy.peerPingScheduler"));
   private final ScheduledFuture<?> peerPingScheduledFuture;
   private final Collection<SerialDevicePingFailureEventListener> serialDevicePingFailureEventListeners = new HashSet<SerialDevicePingFailureEventListener>();

   private final FinchController finchController;

   /**
    * Creates a new <code>FinchProxy</code> using the given {@link FinchController}.
    */
   private FinchProxy(final FinchController finchController)
      {
      this.finchController = finchController;

      // schedule periodic peer pings
      peerPingScheduledFuture = peerPingScheduler.scheduleAtFixedRate(new FinchPinger(),
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
    * Gets the finch's state.  Returns <code>null</code> if an error occurred while getting the state.
    */
   public FinchState getState()
      {
      return finchController.getState();
      }

   /**
    * Sets the full-color LED to the given red, green, and blue intensities. Returns <code>true</code> if the command
    * succeeded, <code>false</code> otherwise.
    *
    * @param red the intensity of the LED's red component [0 to 255]
    * @param green the intensity of the LED's green component [0 to 255]
    * @param blue the intensity of the LED's blue component [0 to 255]
    */
   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return finchController.setFullColorLED(red, green, blue);
      }

   /**
    * Sets the full-color LED to the given {@link Color color}.  Returns the current {@link Color} if the command
    * succeeded, <code>null</code> otherwise.
    */
   public Color setFullColorLED(final Color color)
      {
      return finchController.setFullColorLED(color);
      }

   /**
    * Returns the state of the accelerometer; returns <code>null</code> if an error occurred while trying to read the
    * state.
    */
   public AccelerometerState getAccelerometerState()
      {
      return finchController.getAccelerometerState();
      }

   /**
    * Returns the state of the accelerometer in g's; returns <code>null</code> if an error occurred while trying to read
    * the state.
    */
   public AccelerometerGs getAccelerometerGs()
      {
      return finchController.getAccelerometerGs();
      }

   /**
    * Returns the state of the obstacle detector specified by the given <code>id</code> where 0 denotes the left
    * obstacle detector and 1 denotes the right obstacle detector.  Returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   public Boolean isObstacleDetected(final int id)
      {
      return finchController.isObstacleDetected(id);
      }

   /**
    * Returns the state of the obstacle detectors as an array of <code>boolean</code>s where element 0 denotes the left
    * obstacle detector and element 1 denotes the right obstacle detector.  Returns <code>null</code> if an error
    * occurred while trying to read the values.
    */
   public boolean[] areObstaclesDetected()
      {
      return finchController.areObstaclesDetected();
      }

   /**
    * Returns the current values of the photoresistors as an array of <code>int</code>s where element 0 denotes the left
    * photoresistor and element 1 denotes the right photoresistor.  Returns <code>null</code> if an error occurred while
    * trying to read the values.
    */
   public int[] getPhotoresistors()
      {
      return finchController.getPhotoresistors();
      }

   /**
    * Returns the current value of the thermistor specified by the given <code>id</code>.  Invalid thermistor ids cause
    * this method to return <code>null</code>.  This method also returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   public Integer getThermistor(final int id)
      {
      return finchController.getThermistor(id);
      }

   /**
    * Returns the current value of the thermistor.  Returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   public Integer getThermistor()
      {
      return finchController.getThermistor();
      }

   /**
    * Returns the current value of the thermistor.  Returns <code>null</code> if an error occurred while
    * trying to read the values.
    */
   public Double getThermistorCelsiusTemperature()
      {
      return finchController.getThermistorCelsiusTemperature();
      }

   /**
    * Returns the current position of the motor specified by the given <code>motorId</code>.  Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   public Integer getCurrentMotorPosition(final int motorId)
      {
      return finchController.getCurrentMotorPosition(motorId);
      }

   /**
    * Returns the specified position of the motor specified by the given <code>motorId</code>. Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   public Integer getSpecifiedMotorPosition(final int motorId)
      {
      return finchController.getSpecifiedMotorPosition(motorId);
      }

   /**
    * Returns the specified speed of the motor specified by the given <code>motorId</code>. Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   public Integer getSpecifiedMotorSpeed(final int motorId)
      {
      return finchController.getSpecifiedMotorSpeed(motorId);
      }

   /**
    * Returns the {@link PositionControllableMotorState} of the motor specified by the given <code>motorId</code>.
    * Returns <code>null</code> if an error occurred while trying to read the state.
    */
   public PositionControllableMotorState getMotorState(final int motorId)
      {
      return finchController.getMotorState(motorId);
      }

   /**
    * Returns the {@link PositionControllableMotorState}s of the motors. Returns <code>null</code> if an error occurred
    * while trying to read the states.
    */
   public PositionControllableMotorState[] getMotorStates()
      {
      return finchController.getMotorStates();
      }

   /**
    * Returns the current positions of the motors as an array of <code>int</code>s where element 0 denotes the left
    * motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying to read
    * the values.
    */
   public int[] getCurrentMotorPositions()
      {
      return finchController.getCurrentMotorPositions();
      }

   /**
    * Returns the current positions of the motors in centimeters as an array of <code>doubles</code>s where element 0
    * denotes the left motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred
    * while trying to read the values.
    */
   public double[] getCurrentMotorPositionsInCentimeters()
      {
      return finchController.getCurrentMotorPositionsInCentimeters();
      }

   /**
    * Returns the current velocities of the motors as an array of <code>int</code>s where element 0 denotes the left
    * motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying to read
    * the values.
    */
   public int[] getCurrentMotorVelocities()
      {
      return finchController.getCurrentMotorVelocities();
      }

   /**
    * Returns the current velocities (in cm/s) of the motors as an array of <code>int</code>s where element 0 denotes
    * the left motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying
    * to read the values.
    */
   public double[] getCurrentMotorVelocitiesInCentimetersPerSecond()
      {
      return finchController.getCurrentMotorVelocitiesInCentimetersPerSecond();
      }

   /**
    * Directs the motors to move to the given (relative) positions at the given speeds.  Returns <code>true</code> if
    * the command succeeded, <code>false</code> otherwise.
    *
    * @param leftPositionDelta desired position of the left motor [-32768 to 32767]
    * @param rightPositionDelta desired position of the left motor [-32768 to 32767]
    * @param leftSpeed speed at which to move the left motor in order to reach the desired left position [0 to 20]
    * @param rightSpeed speed at which to move the right motor in order to reach the desired right position [0 to 20]
    */
   public boolean setMotorPositions(final int leftPositionDelta,
                                    final int rightPositionDelta,
                                    final int leftSpeed,
                                    final int rightSpeed)
      {
      return finchController.setMotorPositions(leftPositionDelta,
                                               rightPositionDelta,
                                               leftSpeed,
                                               rightSpeed);
      }

   /**
    * Directs the motors to move to the given (relative) distances (in cm) at the given speeds.  Returns  
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param leftDistanceDelta desired distance (in cm) of the left motor
    * @param rightDistanceDelta desired position (in cm) of the left motor
    * @param leftSpeed speed at which to move the left motor in order to reach the desired left position (in cm/s)
    * @param rightSpeed speed at which to move the right motor in order to reach the desired right position (in cm/s)
    */
   public boolean setMotorPositions(final double leftDistanceDelta,
                                    final double rightDistanceDelta,
                                    final double leftSpeed,
                                    final double rightSpeed)
      {
      return finchController.setMotorPositions(leftDistanceDelta, rightDistanceDelta, leftSpeed, rightSpeed);
      }

   /**
    * Directs the motors specified by the given mask to move to the given (relative) positions at the given speeds.
    * Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    */
   public boolean setMotorPositions(final boolean[] motorMask, final int[] motorPositionDeltas, final int[] motorSpeeds)
      {
      return finchController.setMotorPositions(motorMask, motorPositionDeltas, motorSpeeds);
      }

   /**
    * Directs the motors specified by the given mask to move to the given (relative) distances (in cm) at the given
    * speeds. Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    */
   public boolean setMotorPositions(final boolean[] motorMask, final double[] motorDistanceDeltas, final double[] motorSpeeds)
      {
      return finchController.setMotorPositions(motorMask, motorDistanceDeltas, motorSpeeds);
      }

   /**
    * Sets the motors to the given velocities.  Returns <code>true</code> if the command succeeded, <code>false</code>
    * otherwise.
    *
    * @param leftVelocity velocity of the left motor [-20 to 20]
    * @param rightVelocity velocity of the left motor [-20 to 20]
    */
   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return finchController.setMotorVelocities(leftVelocity, rightVelocity);
      }

   /**
    * Sets the motors to the given velocities (in cm/s).  Returns <code>true</code> if the command succeeded,
    * <code>false</code>
    * otherwise.
    *
    * @param leftVelocity velocity of the left motor (in cm/s)
    * @param rightVelocity velocity of the left motor (in cm/s)
    */
   public boolean setMotorVelocities(final double leftVelocity, final double rightVelocity)
      {
      return finchController.setMotorVelocities(leftVelocity, rightVelocity);
      }

   /**
    * Sets the motors specified by the given mask to the given velocities (in cm/s).  Returns the <i>current</i>
    * velocities (in native units) if the command succeeded (which will likely differ from the <i>given</i> velocities),
    * <code>null</code> otherwise.
    */
   public int[] setMotorVelocities(final boolean[] motorMask, final double[] motorVelocities)
      {
      return finchController.setMotorVelocities(motorMask, motorVelocities);
      }

   /**
    * Sets the motors specified by the given mask to the given velocities.  Returns the <i>current</i> velocities if the
    * command succeeded (which will likely differ from the <i>given</i> velocities), <code>null</code> otherwise.
    */
   public int[] setMotorVelocities(final boolean[] motorMask, final int[] motorVelocities)
      {
      return finchController.setMotorVelocities(motorMask, motorVelocities);
      }

   /**
    * Sets the buzzer to the given <code>frequency</code> for the given <code>durationInMilliseconds</code>. Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param frequency the frequency of the tone [0 to 32767]
    * @param durationInMilliseconds the duration of the tone in milliseconds [0 to 32767]
    */
   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return finchController.playBuzzerTone(frequency, durationInMilliseconds);
      }

   /** Plays a tone having the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>. */
   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      finchController.playTone(frequency, amplitude, duration);
      }

   /** Plays the sound clip contained in the given <code>byte</code> array. */
   public void playClip(final byte[] data)
      {
      finchController.playClip(data);
      }

   /**
    * Turns off both motors and the full-color LED. Returns <code>true</code> if the command succeeded,
    * <code>false</code> otherwise.
    */
   public boolean emergencyStop()
      {
      return finchController.emergencyStop();
      }

   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willAddDisconnectCommandToQueue)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FinchProxy.disconnect(" + willAddDisconnectCommandToQueue + ")");
         }

      // turn off the peer pinger
      try
         {
         peerPingScheduledFuture.cancel(false);
         peerPingScheduler.shutdownNow();
         LOG.debug("FinchProxy.disconnect(): Successfully shut down finch pinger.");
         }
      catch (Exception e)
         {
         LOG.error("FinchProxy.disconnect(): Exception caught while trying to shut down peer pinger", e);
         }

      // optionally send goodbye command to the finch
      if (willAddDisconnectCommandToQueue)
         {
         finchController.disconnect();
         }

      // perform any required shutdown tasks
      finchController.shutdown();
      }

   private class FinchPinger implements Runnable
      {
      public void run()
         {
         try
            {
            LOG.trace("FinchProxy$FinchPinger.run()");

            final boolean pingSuccessful = finchController.ping();

            // if the ping failed, then we know we have a problem so disconnect (which
            // probably won't work) and then notify the listeners
            if (!pingSuccessful)
               {
               try
                  {
                  LOG.error("FinchProxy$FinchPinger.run(): Peer ping failed (received a null state).  Attempting to disconnect...");
                  disconnect(false);
                  LOG.error("FinchProxy$FinchPinger.run(): Done disconnecting from the finch");
                  }
               catch (Exception e)
                  {
                  LOG.error("FinchProxy$FinchPinger.run(): Exeption caught while trying to disconnect from the finch", e);
                  }

               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("FinchProxy$FinchPinger.run(): Notifying " + serialDevicePingFailureEventListeners.size() + " listeners of ping failure...");
                  }
               for (final SerialDevicePingFailureEventListener listener : serialDevicePingFailureEventListeners)
                  {
                  try
                     {
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("   FinchProxy$FinchPinger.run(): Notifying " + listener);
                        }
                     listener.handlePingFailureEvent();
                     }
                  catch (Exception e)
                     {
                     LOG.error("FinchProxy$FinchPinger.run(): Exeption caught while notifying SerialDevicePingFailureEventListener", e);
                     }
                  }
               }
            }
         catch (Exception e)
            {
            LOG.error("FinchProxy$FinchPinger.run(): Exception caught while executing the peer pinger", e);
            }
         }
      }
   }