package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;

/**
 * Created by IntelliJ IDEA.
 * User: astyler
 * Date: Feb 3, 2009
 * Time: 11:38:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class SimulatedFinchModel implements FinchModel
   {
   public FinchState getFinchState()
      {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public AccelerometerState getAccelerometerState()
      {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean[] areObstaclesDetected()
      {
      return new boolean[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

   public int[] getPhotoresistors()
      {
      return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

   public Integer getThermistor(final int id)
      {
      return null;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public int[] getCurrentMotorPositions()
      {
      return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean setFullColorLED(final int red, final int green, final int blue)
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }

   public void playClip(final byte[] data)
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean emergencyStop()
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public boolean ping()
      {
      return false;  //To change body of implemented methods use File | Settings | File Templates.
      }

   public void addFinchEventListener(final FinchEventListener listener)
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }

   public void removeFinchEventListener(final FinchEventListener listener)
      {
      //To change body of implemented methods use File | Settings | File Templates.
      }
   }
