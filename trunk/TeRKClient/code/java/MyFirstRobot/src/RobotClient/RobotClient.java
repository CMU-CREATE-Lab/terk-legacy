package RobotClient;

import java.io.File;
import java.io.IOException;
import TTS.TTS;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.digitalin.DigitalInService;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.createlab.TeRK.led.LEDMode;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.mrpl.TeRK.QwerkState;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class RobotClient implements RobotClientEventHandler
   {

   /* Declare the object which will access GUI methods */
   private final RobotClientGUI robotClientGUI;

   /* The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = "My First Robot Program";

   /* Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/RobotClient/RobotClient.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/RobotClient/RobotClient.relay.ice.properties";

   /* State variable which indicates the state of the Play/Stop button */
   public boolean playMode = false;

   /* Create a variable to send error messages to a log file */
   private static final Logger LOG = Logger.getLogger(RobotClient.class);

   /* Create variables for the left and right motors */
   public static final short leftMotor = 0;
   public static final short rightMotor = 1;

   /** Starts the RobotClient by running a GUI with the APPLICATION_NAME (Currently "My First Robot Program").  */
   public RobotClient()
      {
      this(APPLICATION_NAME);
      }

   /** Starts the RobotClient by running a GUI titled by the String applicationName.
    *
    * @param applicationName String text which sets the title of the RobotClient GUI
    */
   public RobotClient(final String applicationName)
      {
      robotClientGUI = new RobotClientGUI(applicationName,
                                          ICE_RELAY_PROPERTIES_FILE,
                                          ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                          this);
      }

   /**
    * Starts the RobotClient by running a GUI titled by the String applicationName and attempts to automatically
    * connect (using Direct Connect) to a robot specified by the given <code>peerHostname</code>.  This constructor
    * allows you to quickly connect to a robot without having to use the connection wizard.
    *
    * @param applicationName String text which sets the title of the RobotClient GUI
    * @param peerHostname Hostname or IP address of the robot to connect to.
    */
   public RobotClient(final String applicationName, final String peerHostname)
      {
      robotClientGUI = new RobotClientGUI(applicationName,
                                          ICE_RELAY_PROPERTIES_FILE,
                                          ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                          this,
                                          peerHostname);
      }

   /* If you hit the 'Play' button, set the playMode to true */
   public void executeUponPlay()
      {
      playMode = true;
      }

   /* If you hit the 'Stop' button, set the playMode to false */
   public void executeUponStop()
      {
      playMode = false;
      }

   /** Returns the value of the play/stop button.  If 'Play' was most recently pressed,
    *  it will return true.  If 'Stop' was most recently pressed, it will return false.
    *
    * @return the state of the Play/Stop button */
   public boolean buttonState()
      {
      return playMode;
      }

   /**
    *
    * @return true if 'stop' was most recently pressed, false otherwise. */
   public boolean isStopped()
      {
      return !playMode;
      }

   /**
    *
    * @return true if 'play' was most recently pressed, false otherwise.
    */
   public boolean isPlaying()
      {
      return playMode;
      }

   /** Blocks any further program operation until the play button is pressed. */
   public void waitForPlay()
      {
      // catch an exception in case the thread.sleep method gets interrupted
      try
         {
         // As long as the program is stopped, sleep.  This loop sleeps for 10ms
         // and then checks program status, so it will exit within 10ms of someone
         // hitting the play button.
         while (isStopped())
            {
            Thread.sleep(10);
            }
         }
      // If there is an interrupt, print an error message
      catch (InterruptedException e1)
         {
         System.out.println("InterruptedException while sleeping");
         }
      }

   /** Blocks any further program operation until the stop button is pressed. */
   public void waitForStop()
      {
      // catch an exception in case the thread.sleep method gets interrupted
      try
         {
         // As long as the program is playing, sleep.  This loop sleeps for 10ms
         // and then checks program status, so it will exit within 10ms of someone
         // hitting the stop button.
         while (isPlaying())
            {
            Thread.sleep(10);
            }
         }
      // If there is an interrupt, print an error message
      catch (InterruptedException e1)
         {
         System.out.println("InterruptedException while sleeping");
         }
      }

   /** Sleeps the program for a given number of milliseconds.  If the Stop button is pressed, this method
    * immediately exits without sleeping.
    *
    * @param ms Number of milliseconds to sleep the program for */
   public boolean sleepUnlessStop(final int ms)
      {
      // msElapsed holds the number of milliseconds already elapsed
      int msElapsed = 0;

      // sleepInc holds the number of milliseconds to sleep during
      // one loop operation
      int sleepInc;

      // try-catch statement here to catch exceptions that could be caused
      // by interruptions to the Thread.sleep method
      try
         {

         // Do the following loop as long as no one presses the stop button AND
         // the requested time hasn't elapsed
         while (buttonState() && ms > msElapsed)
            {

            // figure out the sleep increment by seeing how much time is left
            sleepInc = ms - msElapsed;

            // If the increment is more than 50ms, then hold it to 50.  This way
            // the buttonState will be checked every 50 ms.
            if (sleepInc > 50)
               {
               sleepInc = 50;
               }

            // Sleep for the number of milliseconds specified by sleepInc
            Thread.sleep(sleepInc);

            // Increment msElapsed by sleepInc
            msElapsed += sleepInc;
            }
         }
      // If something goes wrong, catch the exception and print out an error message
      catch (InterruptedException e1)
         {
         System.out.println("InterruptedException while sleeping");
         }

      // Return the inverse of the button state.  This effectively returns true
      // if the method ended because someone hit the stop button, and false
      // if the method completed by sleeping for the set number of milliseconds.
      return !buttonState();
      }

   /*
    * Returns the QwerkController for the Qwerk to which the client is currently connected; returns
    * <code>null</code> if not connected.
    */
   public final QwerkController getQwerkController()
      {
      return robotClientGUI.qwerkController();
      }

   /** Writes the message string to the GUI's textbox.  The message is always preceded by a timestamp.
    *
    * @param message String containing information to be written to the GUI textbox*/
   public final void writeToTextBox(final String message)
      {
      robotClientGUI.writeToTextBox(message);
      }

   /** Clears the text box area. */
   public final void clearTextBox()
      {
      robotClientGUI.clearTextBox();
      }

   /**
    * Returns the contents of the text field as an int.  Returns 0 if the text field is empty or the value cannot be
    * converted to an integer.
    *
    * @return The value of the text field as an integer
    */
   public final int getTextFieldValueAsInt()
      {
      return robotClientGUI.getTextFieldValueAsInt();
      }

   /** Returns the contents of the text field as a String.
    *
    * @return The value of the text field as a String
    */
   public final String getTextFieldValueAsString()
      {
      return robotClientGUI.getTextFieldValueAsString();
      }

   /** Moves the motor specified by the given <code>motorId</code> at the given <code>velocity</code>.
    *
    * @param motorId The id of the motor to command - valid range is 0 to 3
    * @param velocity The velocity of the motor.*/
   public void moveMotor(int motorId, int velocity)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();

      /** If the controller is not null, you can access the motors.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the motor control service within qwerkcontroller */
         final BackEMFMotorService service = qwerkController.getMotorService();
         if (service != null)
            {/** Check to make sure that motorId is in a valid range */
            if (motorId <= 3 && motorId >= 0)
               {
               service.setMotorVelocity(velocity, motorId);
               }
            else
               {
               System.out.println("Incorrect motorId, only IDs between 0 and 3 are permitted. You sent ID #" + motorId);
               }
            }
         else
            {
            throw new NullPointerException("Failed to move motor " + motorId + " since the BackEMFMotorService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to move motor " + motorId + " since the QwerkController is null");
         }
      }

   /**
    * Moves motor 0 at the velocity specified by <code>leftMotorVelocity</code> and motor 1 at the velocity specified by
    * <code>rightMotorVelocity</code>.
    *
    * @param leftMotorVelocity Velocity to set motor 0 to
    * @param rightMotorVelocity Velocity to set motor 1 to
    */
   public void moveMotors(int leftMotorVelocity, int rightMotorVelocity)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();

      /** If the controller is not null, you can access the motors.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the motor control service within qwerkcontroller */
         final BackEMFMotorService service = qwerkController.getMotorService();

         /** If the motor service is connected (not null), then send velocity commands to the robot */
         if (service != null)
            {
            service.setMotorVelocitiesByIds(0, leftMotorVelocity, 1, rightMotorVelocity);
            }
         else
            {
            throw new NullPointerException("Failed to move motors since the BackEMFMotorService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to move motors since the QwerkController is null");
         }
      }

   /**
    * Moves motor 0 at the velocity specified by <code>leftMotorVelocity</code> and motor 1 at the velocity specified by
    * <code>rightMotorVelocity</code> for the amount of time specified by <code>runningTime</code>, after which
    * motor 0 and motor 1 are set to 0 velocity.
    *
    * @param leftMotorVelocity Velocity to set motor 0 to
    * @param rightMotorVelocity Velocity to set motor 1 to
    * @param runningTime Time in milliseconds to run motors for
    */
   public void moveMotors(int leftMotorVelocity, int rightMotorVelocity, int runningTime)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();

      /** If the controller is not null, you can access the motors.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the motor control service within qwerkcontroller */
         final BackEMFMotorService service = qwerkController.getMotorService();

         /** If the motor service is connected (not null), then send velocity commands to the robot */
         if (service != null)
            {
            service.setMotorVelocitiesByIds(0, leftMotorVelocity, 1, rightMotorVelocity);
            sleepUnlessStop(runningTime);
            stopMotors();
            }
         else
            {
            throw new NullPointerException("Failed to move motors since the BackEMFMotorService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to move motors since the QwerkController is null");
         }
      }

   /**
    *	Sets all four Qwerk motor ports to 0 velocity.
    */
   public void stopMotors()
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();

      /** If the controller is not null, you can access the motors.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the motor control service within qwerkcontroller */
         final BackEMFMotorService service = qwerkController.getMotorService();
         /** If the motor service is connected (not null), then send stop command to the robot */
         if (service != null)
            {
            /** Send the stop motors command */
            service.stopMotors();
            }
         else
            {
            throw new NullPointerException("Failed to stop motors since the BackEMFMotorService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to stop motors since the QwerkController is null");
         }
      }

   /** Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.
    *
    * @param servoId The ID of the servo to be commanded - valid range is 0 to 15
    * @param position The position to set the servo to - valid range is 0 to 255*/
   public void setServo(int servoId, int position)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      /** If the controller is not null, you can access the servos.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the servo control service within qwerkcontroller */
         final ServoService service = qwerkController.getServoService();
         /** If the servo service is connected (not null), then send servo position command to the robot */
         if (service != null)
            {
            /** Check to make sure that both position and servoId are in a valid range */
            if (position <= 255 && position >= 0 && servoId <= 15 && servoId >= 0)
               {
               service.setPosition(servoId, position);
               }
            else
               {
               System.out.println("Incorrect servo command, position must be 0-255, and servo Id must 0-15.  You entered: Position " + position + " servoId " + servoId);
               }
            }
         else
            {
            throw new NullPointerException("Failed to set servo " + servoId + " to position " + position + " since the ServoService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to set servo " + servoId + " to position " + position + " since the QwerkController is null");
         }
      }

   /**
    * Returns the value of the analog input specified by the given port id.  The value returned
    * is the measured voltage in millivolts.  For example, a reading of 2.5V will come back as
    * 2500.
    *
    * @param analogInputPortId The analog port ID from which to get a sensor value from - valid range is 0 to 7
    * @return The value of the voltage at analog port ID in millivolts
    */
   public short analog(int analogInputPortId)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      /** If the controller is not null, you can access the analog ports.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the analog input service within qwerkcontroller */
         final AnalogInputsService service = qwerkController.getAnalogInputsService();
         if (service != null)
            {
            if (analogInputPortId >= 0 && analogInputPortId <= 7)
               {
               return service.getAnalogInputValue(analogInputPortId);
               }
            else
               {
               System.out.println("Analog input port must be from 0-7.  You entered " + analogInputPortId);
               return -1;
               }
            }
         throw new NullPointerException("Failed to retrieve analog input value for port " + analogInputPortId + " since the AnalogInputsService is null");
         }
      System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
      throw new NullPointerException("Failed to retrieve analog input value for port " + analogInputPortId + " since the QwerkController is null");
      }

   /**
    * Returns the value of the digital input specified by the given port id.  The method returns true if a high
    * signal is detected at the input port, and false if a low signal is detected.
    *
    * @param digitalInputPortId The ID of the digital input port to read from
    * @return The state of the input port specified by ID
    */
   public boolean digital(int digitalInputPortId)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      /** If the controller is not null, you can access the digital input ports.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the digital input service within qwerkcontroller */
         final DigitalInService service = qwerkController.getDigitalInService();
         if (service != null)
            {
            if (digitalInputPortId >= 0 && digitalInputPortId <= 3)
               {
               return service.getDigitalInputValue(digitalInputPortId);
               }
            else
               {
               System.out.println("Digital input port must be from 0-3.  You entered " + digitalInputPortId);
               return false;
               }
            }
         throw new NullPointerException("Failed to retrieve digital input value for port " + digitalInputPortId + " since the DigitalInService is null");
         }
      System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
      throw new NullPointerException("Failed to retrieve digital input value for port " + digitalInputPortId + " since the QwerkController is null");
      }

   /**
    * Sets the given digital output to a given state.
    *
    * @param state The state to set the ouput to - true corresponds to a high output signal and false to a low signal.
    * @param digitalOutputPortId The output port to set
    */
   public void setDigital(boolean state, int digitalOutputPortId)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();

      /** If the controller is not null, you can access the digital output ports.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the digital output service within qwerkcontroller */
         final DigitalOutService service = qwerkController.getDigitalOutService();
         if (service != null)
            {
            if (digitalOutputPortId >= 0 && digitalOutputPortId <= 3)
               {
               service.setOutputs(state, digitalOutputPortId);
               }
            else
               {
               System.out.println("Digital output port must be from 0-3.  You entered " + digitalOutputPortId);
               }
            }
         else
            {
            throw new NullPointerException("Failed to set digital output values since the DigitalOutService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to set digital output values since the QwerkController is null");
         }
      }

   /**
    * Sets the given digital output to on (or high) using setDigital.
    */
   public void setDigitalOn(int digitalOutputPortId)
      {
      setDigital(true, digitalOutputPortId);
      }

   /**
    * Sets the given digital output to off (or low) using setDigital.
    */
   public void setDigitalOff(int digitalOutputPortId)
      {
      setDigital(false, digitalOutputPortId);
      }

   /**
    * Sets the given LED to a given state.  The modes are defined by the <code>mode</code> variable,
    * and include LEDMode.LEDOn, LEDMode.LEDOff, and LEDMode.LEDBlinking.
    *
    * @param mode The mode to set the LED to (LEDMode.LEDOn, LEDMode.LEDOff, and LEDMode.LEDBlinking)
    * @param ledId The ID of the LED to set.  The valid range is 0 to 9.
    */
   public void setLED(LEDMode mode, int ledId)
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      /** If the controller is not null, you can access the LEDs.  If it is null, there is no connection to the robot*/
      if (qwerkController != null)
         {
         /** Instantiate the LED service within qwerkcontroller */
         final LEDService service = qwerkController.getLEDService();
         if (service != null)
            {
            if (ledId >= 0 && ledId <= 9)
               {
               service.set(mode, ledId);
               }
            else
               {
               System.out.println("LED ID incorrected, require value from 0-7.  You entered " + ledId);
               }
            }
         else
            {
            throw new NullPointerException("Failed to set LEDs since the LEDService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to set LEDs since the QwerkController is null");
         }
      }

   /** Sets the LED specified by the given id to on.
    *
    * @param ledId The ID of the LED to turn on, valid range is 0 to 9
    */
   public void setLEDOn(int ledId)
      {
      setLED(LEDMode.On, ledId);
      }

   /** Sets the LED specified by the given id to off.
    *
    * @param ledId The ID of the LED to turn off, valid range is 0 to 9
    */
   public void setLEDOff(int ledId)
      {
      setLED(LEDMode.Off, ledId);
      }

   /** Sets the LED specified by the given id to blinking.
    *
    * @param ledId The ID of the LED to blink, valid range is 0 to 9
    */
   public void setLEDBlinking(int ledId)
      {
      setLED(LEDMode.Blinking, ledId);
      }

   /** Sets the LED specified by the given id to on if <code>state</code> is <code>true</code>; off otherwise.
    *
    * @param state Command the LED to on (true) or off (false)
    * @param ledId The ID of the LED to set
    */
   public void setLEDState(boolean state, int ledId)
      {
      setLED(state ? LEDMode.On : LEDMode.Off, ledId);
      }

   /** Returns the voltage of the Qwerk's main power source in millivolts.
    *
    * @return Voltage of the Qwerk power source in millivolts */
   public int batteryVoltage()
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      if (qwerkController != null)
         {
         /** If the controller is not null, you can access the current state of the robot*/
         final QwerkState qwerkState = qwerkController.getQwerkState();
         if (qwerkState != null)
            {
            // Return the battery voltage in millivolts
            return qwerkState.battery.batteryVoltage;
            }
         throw new NullPointerException("Failed to retrieve the battery voltage since the QwerkState is null");
         }
      System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
      throw new NullPointerException("Failed to retrieve the battery voltage since the QwerkController is null");
      }

   /** Returns the current state of the Qwerk's config button.
    *
    * @return True if the button is currently depressed, false otherwise.  */
   public boolean button()
      {
      /** First instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      if (qwerkController != null)
         {
         /** If the controller is not null, you can access the current state of the robot*/
         final QwerkState qwerkState = qwerkController.getQwerkState();
         if (qwerkState != null)
            {
            /** Return the state of the congif button, true = depressed, false = open */
            return qwerkState.button.buttonStates[0];
            }
         throw new NullPointerException("Failed to retrieve the button state since the QwerkState is null");
         }
      System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
      throw new NullPointerException("Failed to retrieve the button state since the QwerkController is null");
      }

   /**
    * Blocks until the config button is pressed and released or the user cancels by pressing the Stop button. Returns
    * false if the method returns because the button was pressed, but returns true if it
    * returns because the user pressed the Stop button during program execution.
    *      <br><br>
    * NOTE: This method determines whether the button is pressed (or released) by checking the state 20 times per second (i.e.
    * every 50 milliseconds).  Thus, if the button is pressed (or released) for less than 50 milliseconds, the change in
    * button state may not be detected.
    *
    * @return True if the method returns because the GUI stop button is pressed, false if the config button is pressed.

    */
   public boolean waitForButtonOrStop()
      {
      // block until pressed
      while (playMode && !button())
         {
         sleepUnlessStop(50);
         }

      // block until released
      while (playMode && button())
         {
         sleepUnlessStop(50);
         }

      return !playMode;
      }

   /**
    *
    * Plays the sound file specified by the given <code>filePath</code>.  Only supports playback of PCM-encoded WAV
    * files.
    *    <br><br>
    * Sounds are played asynchronously, meaning that this method will not block and wait while the sound is playing.
    * Instead, it returns as soon as the sound file is transmitted to the Qwerk.
    *      <br><br>
    *Does nothing (other than log a message and print out an error) if the file cannot be read or played.
    *
    * @param filePath The path to the audio file to be played by the Qwerk.  For example, "C:/sounds/monkey.wav".
    */
   public void playSound(String filePath)
      {
      /** As long as the filePath is not equal to null, do the following */
      if (filePath != null)
         {
         try
            {
            /** Look for the file at the given filePath and convert it to bytes */
            final File file = new File(filePath);
            final byte[] fileBytes = FileUtils.getFileAsBytes(file);
            /** Do the following if a file was actually found, and you didn't get a null result */
            if (fileBytes != null)
               {
               /** Instantiate an object of the robot controller. */
               final QwerkController qwerkController = getQwerkController();
               if (qwerkController != null)
                  {
                  /** Instantiate the audio service and make sure it is not null */
                  final AudioService service = qwerkController.getAudioService();
                  if (service != null)
                     {
                     /** Stream the data to the robot and play it out of its speaker port */
                     service.playSoundAsynchronously(fileBytes, null);
                     }
                  else
                     {
                     throw new NullPointerException("Failed to play the sound since the AudioService is null");
                     }
                  }
               else
                  {
                  System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
                  throw new NullPointerException("Failed to play the sound since the QwerkController is null");
                  }
               }
            else
               {
               LOG.error("The file [" + file.getAbsolutePath() + "] does not exist!");
               System.out.println("The file [" + file.getAbsolutePath() + "] does not exist!");
               }
            }
         catch (IOException e)
            {
            LOG.error("IOException while trying to read the file", e);
            }
         }
      }

   /** Uses the Text To Speech library to synthesize audio from the <code>text</code> variable, and play that
    * audio on the Qwerk using the playSound command.  Note that this program blocks for the amount of time that
    * it takes to synthesize the audio - for example, synthesizing the sentence "Hello world" may block further program
    * execution for two seconds.
    *
    * @param text Speech to synthesize and speak
    */
   public void saySomething(String text)
      {
      TTS ttsObject = new TTS();

      ttsObject.saveTextAsWav(text, "temp.wav");
      playSound("temp.wav");
      }

   /**
    *
    * Plays the tone specified by the given <code>frequency</code>, <code>amplitude</code>, and <code>duration</code>.
    *
    * Tones are played asynchronously, meaning that this method will not block and wait while the tone is playing.
    * Instead, it returns as soon as the command is transmitted to the Qwerk.
    *
    * @param frequency Frequency of the tone in Hz.  Human hearing can detect frequencies between 20 and 20000 Hz, though frequencies above 15000 Hz just annoy teenagers and can't be heard by adults.  Human speech is around 200-1000 Hz.
    * @param amplitude The volume of the tone to be played - 0 is no volume, 100 is maximum volume
    * @param duration The time for which to play the tone in milliseconds
    */
   public void playTone(int frequency, int amplitude, int duration)
      {
      /** Instantiate an object of the robot controller. */
      final QwerkController qwerkController = getQwerkController();
      if (qwerkController != null)
         {
         /** Instantiate the audio service and make sure it is not null */
         final AudioService service = qwerkController.getAudioService();
         if (service != null)
            {
            service.playToneAsynchronously(frequency,
                                           amplitude,
                                           duration,
                                           null);
            }
         else
            {
            throw new NullPointerException("Failed to play the tone since the AudioService is null");
            }
         }
      else
         {
         System.out.println("Uh oh, looks like we are not connected to the Robot - check your internet or network connection");
         throw new NullPointerException("Failed to play the tone since the QwerkController is null");
         }
      }

   /**
    * Loads the roboticon (sequence or expression) having the given filename and plays it.  If there exists both a
    * sequence and an expression having the given filename, the sequence is played and the expression is ignored. Does
    * nothing if the filename does not match any existing sequence or expression.  Expressions are played at the speed
    * defined by {@link ExpressionSpeed#MEDIUM_VELOCITY}.
    *
    * @param filename the name of the sequence or expression to be played.
    */
   public void playRoboticon(final String filename)
      {
      writeToTextBox("Attempting to play roboticon '" + filename + "'...");
      RoboticonPlayer.getInstance().play(filename,
                                         getQwerkController(),
                                         new MessageHandler()
                                         {
                                         public final void handleMessage(final String message)
                                            {
                                            writeToTextBox(message);
                                            }
                                         });
      }
   }
