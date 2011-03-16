package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.accelerometer.unitconversionstrategies.AccelerometerUnitConversionStrategyMMA7260Q;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.motor.unitconversionstrategies.PositionControllableMotorUnitConversionStrategyFinch;
import edu.cmu.ri.createlab.TeRK.motor.unitconversionstrategies.VelocityControllableMotorUnitConversionStrategyFinch;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorUnitConversionStrategy;
import edu.cmu.ri.createlab.TeRK.thermistor.unitconversionstrategies.ThermistorUnitConversionStrategyMF52A103F3380;

/**
 * <p>
 * <code>FinchConstants</code> defines various constants for finches.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class FinchConstants
   {
   /** The size, in bytes, of the state array */
   public static final int SIZE_IN_BYTES_OF_STATE_ARRAY = 21;

   /** The number of accelerometers */
   public static final int ACCELEROMETER_DEVICE_COUNT = 1;

   /**
    * The unique device id for the accelerometer used by all finches.  This value is used to lookup the appropriate
    * {@link AccelerometerUnitConversionStrategy} for converting acclerometer values into g's.
    */
   public static final String ACCELEROMETER_DEVICE_ID = AccelerometerUnitConversionStrategyMMA7260Q.DEVICE_ID;

   /** The number of audio outputs */
   public static final int AUDIO_DEVICE_COUNT = 1;

   /** The minimum supported tone frequency */
   public static final int AUDIO_DEVICE_MIN_AMPLITUDE = 0;

   /** The maximum supported tone frequency */
   public static final int AUDIO_DEVICE_MAX_AMPLITUDE = 10;

   /** The minimum supported tone duration */
   public static final int AUDIO_DEVICE_MIN_DURATION = 0;

   /** The maximum supported tone duration */
   public static final int AUDIO_DEVICE_MAX_DURATION = Integer.MAX_VALUE;

   /** The minimum supported tone frequency */
   public static final int AUDIO_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported tone frequency */
   public static final int AUDIO_DEVICE_MAX_FREQUENCY = Integer.MAX_VALUE;

   /** The number of buzzers */
   public static final int BUZZER_DEVICE_COUNT = 1;

   /** The minimum supported buzzer duration */
   public static final int BUZZER_DEVICE_MIN_DURATION = 0;

   /** The maximum supported buzzer duration */
   public static final int BUZZER_DEVICE_MAX_DURATION = 32767;

   /** The minimum supported buzzer frequency */
   public static final int BUZZER_DEVICE_MIN_FREQUENCY = 0;

   /** The maximum supported buzzer frequency */
   public static final int BUZZER_DEVICE_MAX_FREQUENCY = 32767;

   /** The number of finches */
   public static final int FINCH_DEVICE_COUNT = 1;

   /** The number of full-color LEDS */
   public static final int FULL_COLOR_LED_DEVICE_COUNT = 1;

   /** The minimum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MIN_INTENSITY = 0;

   /** The maximum supported full-color LED intensity */
   public static final int FULL_COLOR_LED_DEVICE_MAX_INTENSITY = 255;

   /** The number of motors */
   public static final int MOTOR_DEVICE_COUNT = 2;

   /** The minimum supported change in position, in ticks (for position control) */
   public static final int MOTOR_DEVICE_MIN_POSITION_DELTA = -32768;

   /** The maximum supported change in position, in ticks (for position control) */
   public static final int MOTOR_DEVICE_MAX_POSITION_DELTA = 32767;

   /** The minimum supported change in position, in centimeters (for position control) */
   public static final double MOTOR_DEVICE_MIN_DISTANCE_DELTA = PositionControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimeters(MOTOR_DEVICE_MIN_POSITION_DELTA);

   /** The maximum supported change in position, in centimeters (for position control) */
   public static final double MOTOR_DEVICE_MAX_DISTANCE_DELTA = PositionControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimeters(MOTOR_DEVICE_MAX_POSITION_DELTA);

   /** The minimum supported speed (for position control) */
   public static final int MOTOR_DEVICE_MIN_SPEED = 0;

   /** The maximum supported speed (for position control) */
   public static final int MOTOR_DEVICE_MAX_SPEED = 20;

   /** The minimum supported speed in cm/s (for speed control) */
   public static final double MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC = PositionControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimetersPerSecond(MOTOR_DEVICE_MIN_SPEED);

   /** The maximum supported speed in cm/s (for speed control) */
   public static final double MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC = PositionControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimetersPerSecond(MOTOR_DEVICE_MAX_SPEED);

   /** The minimum supported velocity (for velocity control) */
   public static final int MOTOR_DEVICE_MIN_VELOCITY = -20;

   /** The maximum supported velocity (for velocity control) */
   public static final int MOTOR_DEVICE_MAX_VELOCITY = 20;

   /** The minimum supported velocity in cm/s (for velocity control) */
   public static final double MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC = VelocityControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimetersPerSecond(MOTOR_DEVICE_MIN_VELOCITY);

   /** The maximum supported velocity in cm/s (for velocity control) */
   public static final double MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC = VelocityControllableMotorUnitConversionStrategyFinch.getInstance().convertToCentimetersPerSecond(MOTOR_DEVICE_MAX_VELOCITY);

   /**
    * The unique device id for the position-controlled motors used by all finches.  This value is used to lookup the
    * appropriate {@link PositionControllableMotorUnitConversionStrategy} for converting between ticks and centimeters.
    */
   public static final String POSITION_CONTROLLABLE_MOTOR_DEVICE_ID = PositionControllableMotorUnitConversionStrategyFinch.DEVICE_ID;

   /** The number of photoresistors */
   public static final int PHOTORESISTOR_DEVICE_COUNT = 2;

   /** The number of simple obstacle sensors */
   public static final int SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT = 2;

   /** The number of thermistors */
   public static final int THERMISTOR_DEVICE_COUNT = 1;

   /**
    * The unique device id for the thermistor used by all finches.  This value is used to lookup the appropriate
    * {@link ThermistorUnitConversionStrategy} for converting thermistor values into temperatures.
    */
   public static final String THERMISTOR_DEVICE_ID = ThermistorUnitConversionStrategyMF52A103F3380.DEVICE_ID;

   public static final String HARDWARE_TYPE = "finch";

   public static final String HARDWARE_VERSION = "0.3";

   /**
    * The unique device id for the velocity-controlled motors used by all finches.  This value is used to lookup the
    * appropriate {@link VelocityControllableMotorUnitConversionStrategy} for converting between native velocities and
    * cm/s.
    */
   public static final String VELOCITY_CONTROLLABLE_MOTOR_DEVICE_ID = VelocityControllableMotorUnitConversionStrategyFinch.DEVICE_ID;

   private FinchConstants()
      {
      // private to prevent instantiation
      }
   }