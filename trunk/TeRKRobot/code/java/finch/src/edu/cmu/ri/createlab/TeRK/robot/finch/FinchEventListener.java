package edu.cmu.ri.createlab.TeRK.robot.finch;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchEventListener
   {
   /**
    * For handling full-color LED events.
    *
    * @param red the intensity of the LED's red component [0 to 255]
    * @param green the intensity of the LED's green component [0 to 255]
    * @param blue the intensity of the LED's blue component [0 to 255]
    */
   void handleSetFullColorLEDEvent(final int red, final int green, final int blue);

   /**
    * For handling motor position events.
    *
    * @param leftPositionDelta desired position of the left motor [-32768 to 32767]
    * @param rightPositionDelta desired position of the left motor [-32768 to 32767]
    * @param leftSpeed speed at which to move the left motor in order to reach the desired left position [0 to 20]
    * @param rightSpeed speed at which to move the right motor in order to reach the desired right position [0 to 20]
    */
   void handleSetMotorPositionsEvent(final int leftPositionDelta,
                                     final int rightPositionDelta,
                                     final int leftSpeed,
                                     final int rightSpeed);

   /**
    * For handling motor velocity events.
    *
    * @param leftVelocity velocity of the left motor [-20 to 20]
    * @param rightVelocity velocity of the left motor [-20 to 20]
    */
   void handleSetMotorVelocities(final int leftVelocity, final int rightVelocity);

   /**
    * For handling buzzer events.
    *
    * @param frequency the frequency of the tone [0 to 32767]
    * @param durationInMilliseconds the duration of the tone in milliseconds [0 to 32767]
    */
   void handlePlayBuzzerToneEvent(final int frequency, final int durationInMilliseconds);

   /** For handling tone playing events. */
   void handlePlayToneEvent(final int frequency, final int amplitude, final int duration);

   /** For handling sound clip playing events. */
   void handlePlayClipEvent(final byte[] data);

   /** For handling emergency stop events. */
   void handleEmergencyStopEvent();

   /** For handling ping events. */
   void handlePingEvent();
   }