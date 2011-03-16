package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.mrpl.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;
import edu.cmu.ri.mrpl.TeRK.finch.FinchState;

/**
 * <p>
 * <code>FinchStateConverter</code> converts between the TeRK
 * {@link edu.cmu.ri.createlab.TeRK.finch.FinchState FinchState} and the Ice
 * {@link FinchState FinchState}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FinchStateConverter
   {
   static FinchState convert(final edu.cmu.ri.createlab.TeRK.finch.FinchState state)
      {
      if (state == null)
         {
         return null;
         }

      return new FinchState(createFullColorLEDState(state.getFullColorLED()),
                            createAccelerometerState(state.getAccelerometerState()),
                            createPositionControllableMotorStates(state.getPositionControllableMotorStates()),
                            state.getMotorVelocities(),
                            state.getThermistor(),
                            new int[]{state.getLeftPhotoresistor(), state.getRightPhotoresistor()},
                            new boolean[]{state.isLeftObstacleDetected(), state.isRightObstacleDetected()});
      }

   static edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState convert(final PositionControllableMotorState motorState)
      {
      return new edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState(motorState.getCurrentPosition(),
                                                                           motorState.getSpecifiedPosition(),
                                                                           motorState.getSpecifiedSpeed());
      }

   private static RGBColor createFullColorLEDState(final Color color)
      {
      if (color != null)
         {
         return new RGBColor(color.getRed(),
                             color.getGreen(),
                             color.getBlue());
         }
      return null;
      }

   private static AccelerometerState createAccelerometerState(final edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState accelerometerState)
      {
      if (accelerometerState != null)
         {
         return new AccelerometerState(accelerometerState.getX(),
                                       accelerometerState.getY(),
                                       accelerometerState.getZ());
         }
      return null;
      }

   private static edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[] createPositionControllableMotorStates(final PositionControllableMotorState[] motorStates)
      {
      if ((motorStates != null) && (motorStates.length > 0))
         {
         final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[] states = new edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[motorStates.length];
         for (int i = 0; i < motorStates.length; i++)
            {
            final PositionControllableMotorState motorState = motorStates[i];
            states[i] = convert(motorState);
            }
         return states;
         }
      return new edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[]{};
      }

   private FinchStateConverter()
      {
      // private to prevent instantiation
      }
   }