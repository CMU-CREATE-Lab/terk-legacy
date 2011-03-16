package edu.cmu.ri.createlab.TeRK.servo;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ServoService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::ServoController";
   int SERVO_MIN_BOUND = 10;
   int SERVO_MAX_BOUND = 245;
   int SERVO_MIN_POSITION = 0;
   int SERVO_DEFAULT_POSITION = 127;
   int SERVO_MAX_POSITION = 255;

   /**
    * Sets the minimum and maximum bounds for the servos specified by the given mask.  Implementations may persist these
    * settings across power-cycles.
    */
   void setBounds(final boolean[] servoMask, final int[] minimumPositions, final int[] maximumPositions);

   /**
    * Sets the minimum and maximum bounds for the servo with the given id.  Implementations may persist these settings
    * across power-cycles. Throws an {@link IndexOutOfBoundsException} if the given <code>servoId</code> does not
    * specify a valid servo.
    */
   void setBounds(final int servoId, final int minimumPosition, final int maximumPosition);

   /** Returns the bounds for all servos. Returns <code>null</code> if the bounds could not be retrieved. */
   ServoBounds[] getBounds();

   /**
    * Sets the minimum and maximum bounds and initial positions for the servos specified by the given mask.
    * Implementations may persist these settings across power-cycles.
    */
   void setConfigs(final boolean[] servoMask, final int[] minimumPositions, final int[] maximumPositions, final int[] initialPositions);

   /**
    * Sets the minimum and maximum bounds and initial position for the servo with the given id.  Implementations may
    * persist these settings across power-cycles. Throws an {@link IndexOutOfBoundsException} if the given
    * <code>servoId</code> does not specify a valid servo.
    */
   void setConfig(final int servoId, final int minimumPosition, final int maximumPosition, final int initialPosition);

   /** Returns the config for all servos.  Returns <code>null</code> if the configs could not be retrieved. */
   ServoConfig[] getConfigs();

   /**
    * Returns the current servo position of the servo specified by the given id. Returns -1 if the position could not
    * be obtained.  Throws an {@link IndexOutOfBoundsException} if the given <code>servoId</code> does not specify a
    * valid servo.
    */
   int getPosition(final int servoId);

   /** Returns the current servo positions. */
   int[] getPositions();

   /**
    *	Sets the servo specified by the given id to the given position.
    *
    *   @param servoId the servo number in [0, getDeviceCount() )
    * @param position a position in the range [0,255]
    */
   void setPosition(final int servoId, final int position);

   /**
    *	Sets the servo specified by the id to the given position at the given speed.
    *
    *   @param servoId the servo number in [0, getDeviceCount() )
    * @param position a position in the range [0,255]
    * @param speed the speed
    */
   void setPositionWithSpeed(final int servoId, final int position, final int speed);

   /** Sets the servos specified by the masks to the given positions at the given speeds. */
   void setPositionsWithSpeeds(final boolean[] servoMask, final int[] positions, final int[] speeds);

   void setVelocities(final int[] velocities);

   /**
    *	Stops the servo specified by the given <code>servoId</code>.
    *
    *   @param servoId the servo number in [0, getDeviceCount() )
    */
   void stopServo(final int servoId);

   void stopServos();
   }