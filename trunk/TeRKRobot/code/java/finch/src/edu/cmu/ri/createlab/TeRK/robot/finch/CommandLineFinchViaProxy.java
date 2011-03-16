package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineFinchViaProxy extends BaseCommandLineFinch
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineFinchViaProxy(in).run();
      }

   private FinchProxy finchProxy;

   public CommandLineFinchViaProxy(final BufferedReader in)
      {
      super(in);
      }

   protected boolean connect(final String serialPortName)
      {
      finchProxy = FinchProxy.create(serialPortName);

      if (finchProxy == null)
         {
         println("Connection failed.");
         return false;
         }
      else
         {
         println("Connection successful.");
         finchProxy.addSerialDevicePingFailureEventListener(
               new SerialDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("Finch ping failure detected.  You will need to reconnect.");
                  finchProxy = null;
                  }
               });
         return true;
         }
      }

   protected FinchState getState()
      {
      return finchProxy.getState();
      }

   protected void setFullColorLED(final int r, final int g, final int b)
      {
      finchProxy.setFullColorLED(r, g, b);
      }

   protected AccelerometerState getAccelerometer()
      {
      return finchProxy.getAccelerometerState();
      }

   protected AccelerometerGs getAccelerometerGs()
      {
      return finchProxy.getAccelerometerGs();
      }

   protected boolean[] getObstacleDetectors()
      {
      return finchProxy.areObstaclesDetected();
      }

   protected int[] getPhotoresistors()
      {
      return finchProxy.getPhotoresistors();
      }

   protected int getThermistor()
      {
      return finchProxy.getThermistor();
      }

   protected double getThermistorCelsiusTemperature()
      {
      return finchProxy.getThermistorCelsiusTemperature();
      }

   protected int[] getCurrentMotorPositions()
      {
      return finchProxy.getCurrentMotorPositions();
      }

   protected double[] getCurrentMotorPositionsInCentimeters()
      {
      return finchProxy.getCurrentMotorPositionsInCentimeters();
      }

   protected int[] getCurrentMotorVelocities()
      {
      return finchProxy.getCurrentMotorVelocities();
      }

   protected double[] getCurrentMotorVelocitiesInCentimetersPerSecond()
      {
      return finchProxy.getCurrentMotorVelocitiesInCentimetersPerSecond();
      }

   protected PositionControllableMotorState[] getCurrentMotorStates()
      {
      return finchProxy.getMotorStates();
      }

   protected boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      return finchProxy.setMotorPositions(leftPositionDelta, rightPositionDelta, leftSpeed, rightSpeed);
      }

   protected boolean setMotorPositions(final boolean[] mask, final int[] positionDeltas, final int[] speeds)
      {
      return finchProxy.setMotorPositions(mask, positionDeltas, speeds);
      }

   protected boolean setMotorPositions(final double leftDistanceDelta, final double rightDistanceDelta, final double leftSpeed, final double rightSpeed)
      {
      return finchProxy.setMotorPositions(leftDistanceDelta, rightDistanceDelta, leftSpeed, rightSpeed);
      }

   protected boolean setMotorPositions(final boolean[] mask, final double[] distanceDeltas, final double[] speeds)
      {
      return finchProxy.setMotorPositions(mask, distanceDeltas, speeds);
      }

   protected boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return finchProxy.setMotorVelocities(leftVelocity, rightVelocity);
      }

   protected boolean setMotorVelocities(final double leftVelocity, final double rightVelocity)
      {
      return finchProxy.setMotorVelocities(leftVelocity, rightVelocity);
      }

   protected boolean setMotorVelocity(final boolean[] mask, final int[] velocities)
      {
      return finchProxy.setMotorVelocities(mask, velocities) != null;
      }

   protected boolean setMotorVelocity(final boolean[] mask, final double[] velocities)
      {
      return finchProxy.setMotorVelocities(mask, velocities) != null;
      }

   protected void playBuzzerTone(final int frequency, final int duration)
      {
      finchProxy.playBuzzerTone(frequency, duration);
      }

   protected void playTone(final int frequency, final int amplitude, final int duration)
      {
      finchProxy.playTone(frequency, amplitude, duration);
      }

   protected void playClip(final byte[] data)
      {
      finchProxy.playClip(data);
      }

   protected void emergencyStop()
      {
      finchProxy.emergencyStop();
      }

   protected boolean isInitialized()
      {
      return finchProxy != null;
      }

   protected void disconnect()
      {
      if (finchProxy != null)
         {
         finchProxy.disconnect();
         finchProxy = null;
         }
      }
   }
