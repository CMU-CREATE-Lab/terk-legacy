package edu.cmu.ri.createlab.TeRK.motor;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Michael Safyan (michaelsafyan@wustl.edu)
 */
public interface BackEMFMotorService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::MotorController";

   /**
    *	<p>Sets the velocity of the given motor.</p>
    *	<p>The motor given by <code>motorid</code> will run on a duty-cycle at the given <code>velocity</code> until the
    * next call to <code>setMotorVelocity</code> or {@link #stopMotors}/{@link #emergencyStopMotor(int)} or
    * another non-const function.</p>
    * <p>Motor is specified in "ticks" (made-up term) which is an unknown in distance and rotation.  It allows us to
    * talk about back-EMF position and encoder position interchangeably and independent of the motor.  Velocity is in
    * ticks/sec.</p>
    *   @param velocity the signed velocity in ticks/second
    *	@param motorid the number in [0, getDeviceCount() ) which references the motor
    */
   void setMotorVelocity(final int velocity, final int motorid);

   /**
    *	<p>Sets the velocity of the given motor until the position is reached.</p>
    *	<p>The motor given by <code>motorid</code> will move with the given <code>velocity</code> until the number
    *	of ticks has been turned or until the next command for the same motor has been executed.</p>
    * <p>Motor is specified in "ticks" (made-up term) which is an unknown in distance and rotation.  It allows us to
    * talk about back-EMF position and encoder position interchangeably and independent of the motor.  Velocity is in
    * ticks/sec.</p>
    *	<p>NOTE: setMotorVelocityUntil(v,p,id) and setMotorVelocityUntil(-v,p,id) are equivalent</p>
    *
    *   @param velocity the velocity in ticks/second (sign does not matter)
    *	@param position the signed position in ticks
    *	@param motorid the number in [0, getDeviceCount() ) which references the motor
    * @deprecated use {@link #setPosition} instead
    */
   void setMotorVelocityUntil(final int velocity, final int position, final int motorid);

   /**
    *	<p>Moves the motor specified by <code>motorid</code> at the given <code>speed</code> until the given
    * <code>position</code> is reached (or until the next command for the same motor has been executed).</p>
    * <p>Motor is specified in "ticks" (a made-up term) which is an unknown in distance and rotation.  It allows us to
    * talk about back-EMF position and encoder position interchangeably and independent of the motor.  Speed is in
    * ticks/sec.</p>
    *	<p>NOTE: The sign of the speed does not matter, so <code>setPosition(s,p,id)</code> and
    * <code>setPosition(-s,p,id)</code> are equivalent.</p>
    *
    *   @param speed the speed in ticks/second (sign does not matter)
    *	@param position the signed position in ticks
    *	@param motorid the number in [0, getDeviceCount() ) which references the motor
    */
   void setPosition(final int speed, final int position, final int motorid);

   /**
    *	<p>Sets the given motors to the given velocities.  The variable-length argument list is assumed to:</p>
    * <ol>
    *    <li>have an even number of arguments (and throws an {@link IllegalArgumentException} otherwise); and</li>
    *    <li>have values which alternatingly specify motor ids and motor velocities.</li>
    * </ol>
    * <p>
    * The order of the pairs of values does not matter.  For example, to set motor 0 to 5000, motor 1 to 17000, and
    * motor 2 to 10000 you could write any of the following:
    * <blockquote>
    * <code>setMotorVelocitiesByIds(0, 5000, 2, 10000, 1, 17000);</code><br>
    * <code>setMotorVelocitiesByIds(0, 5000, 1, 17000, 2, 10000);</code><br>
    * <code>setMotorVelocitiesByIds(2, 10000, 0, 5000, 1, 17000);</code><br>
    * <code>setMotorVelocitiesByIds(2, 10000, 1, 17000, 0, 5000);</code><br>
    * <code>setMotorVelocitiesByIds(1, 17000, 0, 5000, 2, 10000);</code><br>
    * <code>setMotorVelocitiesByIds(1, 17000, 2, 10000, 0, 5000);</code>
    * </blockquote>
    * </p>
    *
    *   @param motorIdsAndVelocities the ids and velocities of the motors to set
    * @throws IllegalArgumentException if the number of arguments is not even
    */
   void setMotorVelocitiesByIds(final int... motorIdsAndVelocities);

   void setMotorVelocities(final int[] velocities);

   void setMotorVelocities(final boolean[] motorMask, final int[] velocities);

   /**
    *	Halts the given motors (specified by motor ID) and puts them in neutral.  Halts all the motors if none are
    * specified.
    */
   void stopMotors(final int... motorIds);

   /**
    *	Halts the motor and prevents further movement.
    *
    *   @param motorid the number in [0, getDeviceCount() ) which references the motor
    *	@see #stopMotors
    */
   void emergencyStopMotor(final int motorid);

   void startMotorBufferRecord(final boolean[] mask) throws Exception;

   void stopMotorBufferRecord(final boolean[] mask) throws Exception;

   BackEMFMotorBuffer[] getMotorBuffers(final boolean[] mask) throws Exception;

   void setMotorBuffer(final boolean[] mask, final BackEMFMotorBuffer[] buffers) throws Exception;

   void playMotorBuffer(final boolean[] mask) throws Exception;
   }