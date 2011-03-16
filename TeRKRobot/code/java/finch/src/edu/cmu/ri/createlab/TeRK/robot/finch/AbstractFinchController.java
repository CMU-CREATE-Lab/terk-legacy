package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorUnitConversionStrategyFinder;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorUnitConversionStrategyFinder;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractFinchController implements FinchController
   {
   private static final Logger LOG = Logger.getLogger(AbstractFinchController.class);

   private final AccelerometerUnitConversionStrategy accelerometerUnitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.ACCELEROMETER_DEVICE_ID);
   private final PositionControllableMotorUnitConversionStrategy positionControllableMotorUnitConversionStrategy = PositionControllableMotorUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.POSITION_CONTROLLABLE_MOTOR_DEVICE_ID);
   private final VelocityControllableMotorUnitConversionStrategy velocityControllableMotorUnitConversionStrategy = VelocityControllableMotorUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.VELOCITY_CONTROLLABLE_MOTOR_DEVICE_ID);
   private final ThermistorUnitConversionStrategy thermistorUnitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(FinchConstants.THERMISTOR_DEVICE_ID);
   private final String serialPortName;

   protected AbstractFinchController(final String serialPortName)
      {
      this.serialPortName = serialPortName;
      }

   public final String getSerialPortName()
      {
      return serialPortName;
      }

   public abstract FinchState getState();

   public abstract AccelerometerState getAccelerometerState();

   /**
    * Returns the state of the accelerometer in g's; returns <code>null</code> if an error occurred while trying to read
    * the state.
    */
   public final AccelerometerGs getAccelerometerGs()
      {
      if (accelerometerUnitConversionStrategy != null)
         {
         return accelerometerUnitConversionStrategy.convert(getAccelerometerState());
         }

      return null;
      }

   public final Boolean isObstacleDetected(final int id)
      {
      if (id >= 0 && id < FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT)
         {
         final boolean[] isDetected = areObstaclesDetected();
         if (isDetected != null && id < isDetected.length)
            {
            return isDetected[id];
            }
         }
      return null;
      }

   public abstract boolean[] areObstaclesDetected();

   public abstract int[] getPhotoresistors();

   public abstract Integer getThermistor(final int id);

   public final Integer getThermistor()
      {
      return getThermistor(0);
      }

   public final Double getThermistorCelsiusTemperature()
      {
      if (thermistorUnitConversionStrategy != null)
         {
         return thermistorUnitConversionStrategy.convertToCelsius(getThermistor(0));
         }

      return null;
      }

   public final Integer getCurrentMotorPosition(final int motorId)
      {
      if (motorId >= 0 && motorId < FinchConstants.MOTOR_DEVICE_COUNT)
         {
         final int[] positions = getCurrentMotorPositions();
         if (positions != null && motorId < positions.length)
            {
            return positions[motorId];
            }
         }
      return null;
      }

   public final Integer getSpecifiedMotorPosition(final int motorId)
      {
      final PositionControllableMotorState motorState = getMotorState(motorId);
      if (motorState != null)
         {
         return motorState.getSpecifiedPosition();
         }
      return null;
      }

   public final Integer getSpecifiedMotorSpeed(final int motorId)
      {
      final PositionControllableMotorState motorState = getMotorState(motorId);
      if (motorState != null)
         {
         return motorState.getSpecifiedSpeed();
         }
      return null;
      }

   public final PositionControllableMotorState getMotorState(final int motorId)
      {
      if (motorId >= 0 && motorId < FinchConstants.MOTOR_DEVICE_COUNT)
         {
         final PositionControllableMotorState[] motorStates = getMotorStates();

         if (motorStates != null && motorId < motorStates.length)
            {
            final PositionControllableMotorState motorState = motorStates[motorId];
            if (motorState != null)
               {
               return motorState;
               }
            }
         }
      return null;
      }

   public final PositionControllableMotorState[] getMotorStates()
      {
      final FinchState finchState = getState();
      if (finchState != null)
         {
         final PositionControllableMotorState[] motorStates = finchState.getPositionControllableMotorStates();
         if (motorStates != null && motorStates.length > 0)
            {
            return motorStates;
            }
         }
      return null;
      }

   public abstract int[] getCurrentMotorPositions();

   public final double[] getCurrentMotorPositionsInCentimeters()
      {
      if (positionControllableMotorUnitConversionStrategy != null)
         {
         return positionControllableMotorUnitConversionStrategy.convertToCentimeters(getCurrentMotorPositions());
         }
      return null;
      }

   public final int[] getCurrentMotorVelocities()
      {
      final FinchState finchState = getState();
      if (finchState == null)
         {
         LOG.debug("AbstractFinchController.getCurrentMotorVelocities(): Could not retrieve the current finch state (needed for getCurrentMotorVelocities()). Returning null.");
         return null;
         }

      return finchState.getMotorVelocities();
      }

   public final double[] getCurrentMotorVelocitiesInCentimetersPerSecond()
      {
      if (velocityControllableMotorUnitConversionStrategy != null)
         {
         return velocityControllableMotorUnitConversionStrategy.convertToCentimetersPerSecond(getCurrentMotorVelocities());
         }
      return null;
      }

   public abstract boolean setFullColorLED(final int red, final int green, final int blue);

   /**
    * Sets the full-color LED to the given {@link Color color}.  Returns the current {@link Color} if the command
    * succeeded, <code>null</code> otherwise.
    */
   public final Color setFullColorLED(final Color color)
      {
      if (setFullColorLED(color.getRed(),
                          color.getGreen(),
                          color.getBlue()))
         {
         final FinchState state = getState();
         if (state != null)
            {
            return state.getFullColorLED();
            }
         }

      return null;
      }

   public abstract boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed);

   public final boolean setMotorPositions(final double leftDistanceDelta,
                                          final double rightDistanceDelta,
                                          final double leftSpeed,
                                          final double rightSpeed)
      {
      if (positionControllableMotorUnitConversionStrategy != null)
         {
         return setMotorPositions(positionControllableMotorUnitConversionStrategy.convertToTicks(leftDistanceDelta),
                                  positionControllableMotorUnitConversionStrategy.convertToTicks(rightDistanceDelta),
                                  positionControllableMotorUnitConversionStrategy.convertToNativeSpeed(leftSpeed),
                                  positionControllableMotorUnitConversionStrategy.convertToNativeSpeed(rightSpeed));
         }
      return false;
      }

   public final boolean setMotorPositions(final boolean[] motorMask, final int[] motorPositionDeltas, final int[] motorSpeeds)
      {
      if (motorMask != null && motorPositionDeltas != null && motorSpeeds != null)
         {
         int numMotorsToConsider = Math.min(FinchConstants.MOTOR_DEVICE_COUNT, motorMask.length);
         numMotorsToConsider = Math.min(numMotorsToConsider, motorPositionDeltas.length);
         numMotorsToConsider = Math.min(numMotorsToConsider, motorSpeeds.length);
         final int[] positionDeltas = new int[FinchConstants.MOTOR_DEVICE_COUNT];
         final int[] speeds = new int[FinchConstants.MOTOR_DEVICE_COUNT];

         // First check to see whether either motor is not to be changed.
         boolean isAtLeastOneMotorToBeLeftUnchanged = (numMotorsToConsider < FinchConstants.MOTOR_DEVICE_COUNT);
         if (!isAtLeastOneMotorToBeLeftUnchanged)
            {
            for (int i = 0; i < numMotorsToConsider; i++)
               {
               if (!motorMask[i])
                  {
                  isAtLeastOneMotorToBeLeftUnchanged = true;
                  break;
                  }
               }
            }

         // If a motor is not to be changed, then we need to get the current desired position and speed of that motor.
         if (isAtLeastOneMotorToBeLeftUnchanged)
            {
            final FinchState finchState = getState();
            if (finchState == null)
               {
               LOG.error("AbstractFinchController.setMotorPositions(): Could not retrieve the current finch state (needed for masked motor).  Aborting command to set motor positions.");
               return false;
               }

            final PositionControllableMotorState[] motorStates = finchState.getPositionControllableMotorStates();
            for (int i = 0; i < FinchConstants.MOTOR_DEVICE_COUNT; i++)
               {
               // if the ID specifies a motor which is not covered by the mask, or is covered by the mask and set to false,
               // then get its current state
               if (i >= motorMask.length || !motorMask[i])
                  {
                  final PositionControllableMotorState motorState = motorStates[i];
                  positionDeltas[i] = motorState.getSpecifiedPosition() - motorState.getCurrentPosition();
                  speeds[i] = motorState.getSpecifiedSpeed();
                  }
               }
            }

         // now go through the specified motors and set the positions and speeds
         for (int i = 0; i < numMotorsToConsider; i++)
            {
            if (motorMask[i])
               {
               positionDeltas[i] = motorPositionDeltas[i];
               speeds[i] = motorSpeeds[i];
               }
            }

         // execute it
         return setMotorPositions(positionDeltas[0],
                                  positionDeltas[1],
                                  speeds[0],
                                  speeds[1]);
         }

      return false;
      }

   public final boolean setMotorPositions(final boolean[] motorMask, final double[] motorDistanceDeltas, final double[] motorSpeeds)
      {
      if (positionControllableMotorUnitConversionStrategy != null)
         {
         return setMotorPositions(motorMask,
                                  positionControllableMotorUnitConversionStrategy.convertToTicks(motorDistanceDeltas),
                                  positionControllableMotorUnitConversionStrategy.convertToNativeSpeed(motorSpeeds));
         }
      return false;
      }

   public abstract boolean setMotorVelocities(final int leftVelocity, final int rightVelocity);

   public final boolean setMotorVelocities(final double leftVelocity, final double rightVelocity)
      {
      if (velocityControllableMotorUnitConversionStrategy != null)
         {
         return setMotorVelocities(velocityControllableMotorUnitConversionStrategy.convertToNativeVelocity(leftVelocity),
                                   velocityControllableMotorUnitConversionStrategy.convertToNativeVelocity(rightVelocity));
         }
      return false;
      }

   public final int[] setMotorVelocities(final boolean[] motorMask, final double[] motorVelocities)
      {
      if (velocityControllableMotorUnitConversionStrategy != null)
         {
         return setMotorVelocities(motorMask,
                                   velocityControllableMotorUnitConversionStrategy.convertToNativeVelocity(motorVelocities));
         }
      return null;
      }

   public final int[] setMotorVelocities(final boolean[] motorMask, final int[] motorVelocities)
      {
      if (motorMask != null && motorVelocities != null)
         {
         int numMotorsToConsider = Math.min(FinchConstants.MOTOR_DEVICE_COUNT, motorMask.length);
         numMotorsToConsider = Math.min(numMotorsToConsider, motorVelocities.length);
         final int[] velocities = new int[FinchConstants.MOTOR_DEVICE_COUNT];

         // First check to see whether either motor is not to be changed.
         boolean isAtLeastOneMotorToBeLeftUnchanged = (numMotorsToConsider < FinchConstants.MOTOR_DEVICE_COUNT);
         if (!isAtLeastOneMotorToBeLeftUnchanged)
            {
            for (int i = 0; i < numMotorsToConsider; i++)
               {
               if (!motorMask[i])
                  {
                  isAtLeastOneMotorToBeLeftUnchanged = true;
                  break;
                  }
               }
            }

         // If a motor is not to be changed, then we need to get the current desired velocity of that motor.
         if (isAtLeastOneMotorToBeLeftUnchanged)
            {
            final FinchState finchState = getState();
            if (finchState == null)
               {
               LOG.error("AbstractFinchController.setMotorVelocities(): Could not retrieve the current finch state (needed for masked motor).  Aborting command to set motor positions.");
               return null;
               }

            final int[] motorStates = finchState.getMotorVelocities();
            for (int i = 0; i < FinchConstants.MOTOR_DEVICE_COUNT; i++)
               {
               // if the ID specifies a motor which is not covered by the mask, or is covered by the mask and set to false,
               // then get its current state
               if (i >= motorMask.length || !motorMask[i])
                  {
                  velocities[i] = motorStates[i];
                  }
               }
            }

         // now go through the specified motors and set the velocities
         for (int i = 0; i < numMotorsToConsider; i++)
            {
            if (motorMask[i])
               {
               velocities[i] = motorVelocities[i];
               }
            }

         // execute it
         if (setMotorVelocities(velocities[0], velocities[1]))
            {
            final FinchState finchState = getState();
            if (finchState != null)
               {
               return finchState.getMotorVelocities();
               }
            }
         }

      return null;
      }

   public abstract boolean playBuzzerTone(final int frequency, final int durationInMilliseconds);

   public abstract void playTone(final int frequency, final int amplitude, final int duration);

   public abstract void playClip(final byte[] data);

   public abstract boolean emergencyStop();

   public abstract void disconnect();

   public abstract void shutdown();

   public final boolean ping()
      {
      // for pings, we simply get the state of the thermistor (it's a quick operation with a small return value)
      return getThermistor() != null;
      }
   }
