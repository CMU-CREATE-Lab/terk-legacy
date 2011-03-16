package edu.cmu.ri.createlab.TeRK.finch;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchState
   {
   Color getFullColorLED();

   AccelerometerState getAccelerometerState();

   /**
    * Returns an array of {@link PositionControllableMotorState}s.  Returns <code>null</code> if the states could not
    * be retrieved.
    */
   PositionControllableMotorState[] getPositionControllableMotorStates();

   int[] getMotorVelocities();

   int getThermistor();

   int getLeftPhotoresistor();

   int getRightPhotoresistor();

   boolean isLeftObstacleDetected();

   boolean isRightObstacleDetected();
   }