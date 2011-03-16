package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchController
   {
   /**
    * Gets the finch's state.  Returns <code>null</code> if an error occurred while getting the state.
    */
   FinchState getState();

   /**
    * Returns the state of the accelerometer; returns <code>null</code> if an error occurred while trying to read the
    * state.
    */
   AccelerometerState getAccelerometerState();

   /**
    * Returns the state of the accelerometer in g's; returns <code>null</code> if an error occurred while trying to read
    * the state.
    */
   AccelerometerGs getAccelerometerGs();

   /**
    * Returns the state of the obstacle detector specified by the given <code>id</code> where 0 denotes the left
    * obstacle detector and 1 denotes the right obstacle detector.  Returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   Boolean isObstacleDetected(final int id);

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
    * Returns the current value of the thermistor.  Returns <code>null</code> if an error occurred while
    * trying to read the value.
    */
   Integer getThermistor();

   /**
    * Returns the current value of the thermistor.  Returns <code>null</code> if an error occurred while
    * trying to read the values.
    */
   Double getThermistorCelsiusTemperature();

   /**
    * Returns the current position of the motor specified by the given <code>motorId</code>.  Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   Integer getCurrentMotorPosition(final int motorId);

   /**
    * Returns the specified position of the motor specified by the given <code>motorId</code>. Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   Integer getSpecifiedMotorPosition(final int motorId);

   /**
    * Returns the specified speed of the motor specified by the given <code>motorId</code>. Returns <code>null</code>
    * if an error occurred while trying to read the value.
    */
   Integer getSpecifiedMotorSpeed(final int motorId);

   /**
    * Returns the {@link PositionControllableMotorState} of the motor specified by the given <code>motorId</code>.
    * Returns <code>null</code> if an error occurred while trying to read the state.
    */
   PositionControllableMotorState getMotorState(final int motorId);

   /**
    * Returns the {@link PositionControllableMotorState}s of the motors. Returns <code>null</code> if an error occurred
    * while trying to read the states.
    */
   PositionControllableMotorState[] getMotorStates();

   /**
    * Returns the current positions of the motors as an array of <code>int</code>s where element 0 denotes the left
    * motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying to read
    * the values.
    */
   int[] getCurrentMotorPositions();

   /**
    * Returns the current positions of the motors in centimeters as an array of <code>doubles</code>s where element 0
    * denotes the left motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred
    * while trying to read the values.
    */
   double[] getCurrentMotorPositionsInCentimeters();

   /**
    * Returns the current velocities of the motors as an array of <code>int</code>s where element 0 denotes the left
    * motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying to read
    * the values.
    */
   int[] getCurrentMotorVelocities();

   /**
    * Returns the current velocities (in cm/s) of the motors as an array of <code>int</code>s where element 0 denotes
    * the left motor and element 1 denotes the right motor.  Returns <code>null</code> if an error occurred while trying
    * to read the values.
    */
   double[] getCurrentMotorVelocitiesInCentimetersPerSecond();

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
    * Sets the full-color LED to the given {@link Color color}.  Returns the current {@link Color} if the command
    * succeeded, <code>null</code> otherwise.
    */
   Color setFullColorLED(final Color color);

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
    * Directs the motors to move to the given (relative) distances (in cm) at the given speeds.  Returns
    * <code>true</code> if the command succeeded, <code>false</code> otherwise.
    *
    * @param leftDistanceDelta desired distance (in cm) of the left motor
    * @param rightDistanceDelta desired position (in cm) of the left motor
    * @param leftSpeed speed at which to move the left motor in order to reach the desired left position (in cm/s)
    * @param rightSpeed speed at which to move the right motor in order to reach the desired right position (in cm/s)
    */
   boolean setMotorPositions(final double leftDistanceDelta,
                             final double rightDistanceDelta,
                             final double leftSpeed,
                             final double rightSpeed);

   /**
    * Directs the motors specified by the given mask to move to the given (relative) positions at the given speeds.
    * Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    */
   boolean setMotorPositions(final boolean[] motorMask, final int[] motorPositionDeltas, final int[] motorSpeeds);

   /**
    * Directs the motors specified by the given mask to move to the given (relative) distances (in cm) at the given
    * speeds. Returns <code>true</code> if the command succeeded, <code>false</code> otherwise.
    */
   boolean setMotorPositions(final boolean[] motorMask, final double[] motorDistanceDeltas, final double[] motorSpeeds);

   /**
    * Sets the motors to the given velocities.  Returns <code>true</code> if the command succeeded, <code>false</code>
    * otherwise.
    *
    * @param leftVelocity velocity of the left motor [-20 to 20]
    * @param rightVelocity velocity of the left motor [-20 to 20]
    */
   boolean setMotorVelocities(final int leftVelocity, final int rightVelocity);

   /**
    * Sets the motors to the given velocities (in cm/s).  Returns <code>true</code> if the command succeeded,
    * <code>false</code>
    * otherwise.
    *
    * @param leftVelocity velocity of the left motor (in cm/s)
    * @param rightVelocity velocity of the left motor (in cm/s)
    */
   boolean setMotorVelocities(final double leftVelocity, final double rightVelocity);

   /**
    * Sets the motors specified by the given mask to the given velocities (in cm/s).  Returns the <i>current</i>
    * velocities (in native units) if the command succeeded (which will likely differ from the <i>given</i> velocities),
    * <code>null</code> otherwise.
    */
   int[] setMotorVelocities(final boolean[] motorMask, final double[] motorVelocities);

   /**
    * Sets the motors specified by the given mask to the given velocities.  Returns the <i>current</i> velocities if the
    * command succeeded (which will likely differ from the <i>given</i> velocities), <code>null</code> otherwise.
    */
   int[] setMotorVelocities(final boolean[] motorMask, final int[] motorVelocities);

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

   void disconnect();

   void shutdown();

   /**
    * Pings the finch, returning <code>true</code> if successful, <code>false</code> otherwise.
    */
   boolean ping();
   }