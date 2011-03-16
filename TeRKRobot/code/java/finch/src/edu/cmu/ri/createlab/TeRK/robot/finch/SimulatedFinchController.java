package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;

/**
 * Created by IntelliJ IDEA.
 * User: astyler
 * Date: Feb 3, 2009
 * Time: 11:09:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimulatedFinchController extends AbstractFinchController
   {
   private SimulatedFinchModel model;

   public SimulatedFinchController(final String serialPortName)
      {
      super(serialPortName);
      model = new SimulatedFinchModel();
      }

   public FinchState getState()
      {
      return null;
      }

   public FinchModel getModel()
      {
      return model;
      }

   public AccelerometerState getAccelerometerState()
      {
      return model.getAccelerometerState();
      }

   public boolean[] areObstaclesDetected()
      {
      return model.areObstaclesDetected();
      }

   public int[] getPhotoresistors()
      {
      return model.getPhotoresistors();
      }

   public Integer getThermistor(final int id)
      {
      return model.getThermistor(id);
      }

   public int[] getCurrentMotorPositions()
      {
      return model.getCurrentMotorPositions();
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return model.setFullColorLED(red, green, blue);
      }

   public boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      return model.setMotorPositions(leftPositionDelta, rightPositionDelta, leftSpeed, rightSpeed);
      }

   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return model.setMotorVelocities(leftVelocity, rightVelocity);
      }

   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return model.playBuzzerTone(frequency, durationInMilliseconds);
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      model.playTone(frequency, amplitude, duration);
      }

   public void playClip(final byte[] data)
      {
      model.playClip(data);
      }

   public boolean emergencyStop()
      {
      return model.emergencyStop();
      }

   public void disconnect()
      {
      }

   public void shutdown()
      {
      }
   }
