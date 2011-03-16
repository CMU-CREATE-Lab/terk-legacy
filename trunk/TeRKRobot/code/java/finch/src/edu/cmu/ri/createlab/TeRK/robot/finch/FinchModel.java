package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchModel extends FinchEventPublisher
   {
   /**
    * Gets the finch's state.  Returns <code>null</code> if an error occurred while getting the state.
    */
   FinchState getFinchState();

   /**
    * Returns the state of the accelerometer; returns <code>null</code> if an error occurred while trying to read the
    * state.
    */
   AccelerometerState getAccelerometerState();

   /**
    * Returns the state of the obstacle detectors as an array of <code>boolean</code>s where element 0 denotes the left
    * obstacle detector and element 1 denotes the right obstacle detector.  Returns <code>null</code> if an error
    * occurred while trying to read the values.
    */
   boolean[] areObstaclesDetected();

   /**
    * Returns the current values of the photoresistors as an array of <code>int</code>s where element 0 denotes the left
    * photoresistor and element 1 denotes the right photoresistor.  Returns <code>null</code> if an error occurred while
    * trying to read the values.
    */
   int[] getPhotoresistors();

   /**
    * Returns the current value of the thermistor specified by the given <code>id</code>.  Invalid thermistor ids cause
    * this method to return <code>null</code>.  This method also returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   Integer getThermistor(final int id);

   /**
    * Returns the current positions of the motors as an array of <code>int</code>s where element 0 denotes the left
    * motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying to read
    * the values.
    */
   int[] getCurrentMotorPositions();

   /**
    * Sets the full-color LED to the given red, green, and blue intensities. Returns <code>true</code> if the command
    * succeeded, <code>false</code> otherwise.
    *
    * @param red the intensity of the LED's red component [0 to 255]
    * @param green the intensity of the LED's green component [0 to 255]
    * @param blue the intensity of the LED's blue component [0 to 255]
    */
   boolean setFullColorLED(final int red, final int green, final int blue);

   /**
    * Directs the motors to move to the given (relative) positions at the given speeds.  Returns <code>true</code> if
    * the command succeeded, <code>false</code> otherwise.
    *
    * @param leftPositionDelta desired position of the left motor [-32768 to 32767]
    * @param rightPositionDelta desired position of the left motor [-32768 to 32767]
    * @param leftSpeed speed at which to move the left motor in order to reach the desired left position [0 to 20]
    * @param rightSpeed speed at which to move the right motor in order to reach the desired right position [0 to 20]
    */
   boolean setMotorPositions(final int leftPositionDelta,
                             final int rightPositionDelta,
                             final int leftSpeed,
                             final int rightSpeed);

   /**
    * Sets the motors to the given velocities.  Returns <code>true</code> if the command succeeded, <code>false</code>
    * otherwise.
    *
    * @param leftVelocity velocity of the left motor [-20 to 20]
    * @param rightVelocity velocity of the left motor [-20 to 20]
    */
   boolean setMotorVelocities(final int leftVelocity, final int rightVelocity);

   /**
    * Sets the buzzer to the given <code>frequency</code> for the given <code>durationInMilliseconds</code>. Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param frequency the frequency of the tone [0 to 32767]
    * @param durationInMilliseconds the duration of the tone in milliseconds [0 to 32767]
    */
   boolean playBuzzerTone(final int frequency, final int durationInMilliseconds);

   /** Plays a tone having the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>. */
   void playTone(final int frequency, final int amplitude, final int duration);

   /** Plays the sound clip contained in the given <code>byte</code> array. */
   void playClip(final byte[] data);

   /**
    * Turns off both motors and the full-color LED. Returns <code>true</code> if the command succeeded,
    * <code>false</code> otherwise.
    */
   boolean emergencyStop();

   /**
    * Pings the finch, returning <code>true</code> if successful, <code>false</code> otherwise.
    */
   boolean ping();
   }