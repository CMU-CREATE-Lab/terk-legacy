package RobotClient;

import edu.cmu.ri.createlab.TeRK.led.LEDMode;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;

/**
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimpleRobotClient
   {
   private RobotClient robotClient;

   /** Starts the SimpleRobotClient by running a GUI with the APPLICATION_NAME (Currently "My First Robot Program").  */
   public SimpleRobotClient()
      {
      robotClient = new RobotClient();
      }

   /** Starts the SimpleRobotClient by running a GUI titled by the String applicationName.
    *
    * @param applicationName String text which sets the title of the RobotClient GUI
    */
   public SimpleRobotClient(String applicationName)
      {
      robotClient = new RobotClient(applicationName);
      }

   /**
    * Starts the SimpleRobotClient by running a GUI titled by the String applicationName and attempts to automatically
    * connect (using Direct Connect) to a robot specified by the given <code>peerHostname</code>.  This constructor
    * allows you to quickly connect to a robot without having to use the connection wizard.
    *
    * @param applicationName String text which sets the title of the RobotClient GUI
    * @param peerHostname Hostname or IP address of the robot to connect to.
    */
   public SimpleRobotClient(final String applicationName, final String peerHostname)
      {
      robotClient = new RobotClient(applicationName, peerHostname);
      }

   /** Returns the value of the play/stop button.  If 'Play' was most recently pressed,
    *  it will return true.  If 'Stop' was most recently pressed, it will return false.
    *
    * @return the state of the Play/Stop button */
   public boolean buttonState()
      {
      return robotClient.buttonState();
      }

   /**
    *
    * @return true if 'stop' was most recently pressed, false otherwise. */
   public boolean isStopped()
      {
      return robotClient.isStopped();
      }

   /**
    *
    * @return true if 'play' was most recently pressed, false otherwise.
    */
   public boolean isPlaying()
      {
      return robotClient.isPlaying();
      }

   /** Blocks any further program operation until the play button is pressed. */
   public void waitForPlay()
      {
      robotClient.waitForPlay();
      }

   /** Blocks any further program operation until the stop button is pressed. */
   public void waitForStop()
      {
      robotClient.waitForStop();
      }

   /** Sleeps the program for a given number of milliseconds.  If the Stop button is pressed, this method
    * immediately exits without sleeping.
    *
    * @param ms Number of milliseconds to sleep the program for */
   public boolean sleepUnlessStop(final int ms)
      {
      return robotClient.sleepUnlessStop(ms);
      }

   /** Writes the message string to the GUI's textbox.  The message is always preceded by a timestamp.
    *
    * @param message String containing information to be written to the GUI textbox*/
   public final void writeToTextBox(final String message)
      {
      robotClient.writeToTextBox(message);
      }

   /** Clears the text box area. */
   public final void clearTextBox()
      {
      robotClient.clearTextBox();
      }

   /**
    * Returns the contents of the text field as an int.  Returns 0 if the text field is empty or the value cannot be
    * converted to an integer.
    *
    * @return The value of the text field as an integer
    */
   public final int getTextFieldValueAsInt()
      {
      return robotClient.getTextFieldValueAsInt();
      }

   /** Returns the contents of the text field as a String.
    *
    * @return The value of the text field as a String
    */
   public final String getTextFieldValueAsString()
      {
      return robotClient.getTextFieldValueAsString();
      }

   /** Moves the motor specified by the given <code>motorId</code> at the given <code>velocity</code>.
    *
    * @param motorId The id of the motor to command - valid range is 0 to 3
    * @param velocity The velocity of the motor.*/
   public void moveMotor(int motorId, int velocity)
      {
      robotClient.moveMotor(motorId, velocity);
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
      robotClient.moveMotors(leftMotorVelocity, rightMotorVelocity);
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
      robotClient.moveMotors(leftMotorVelocity, rightMotorVelocity, runningTime);
      }

   /**
    *	Sets all four Qwerk motor ports to 0 velocity.
    */
   public void stopMotors()
      {
      robotClient.stopMotors();
      }

   /** Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.
    *
    * @param servoId The ID of the servo to be commanded - valid range is 0 to 15
    * @param position The position to set the servo to - valid range is 0 to 255*/
   public void setServo(int servoId, int position)
      {
      robotClient.setServo(servoId, position);
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
      return robotClient.analog(analogInputPortId);
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
      return robotClient.digital(digitalInputPortId);
      }

   /**
    * Sets the given digital output to a given state.
    *
    * @param state The state to set the ouput to - true corresponds to a high output signal and false to a low signal.
    * @param digitalOutputPortId The output port to set
    */
   public void setDigital(boolean state, int digitalOutputPortId)
      {
      robotClient.setDigital(state, digitalOutputPortId);
      }

   /**
    * Sets the given digital output to on (or high) using setDigital.
    */
   public void setDigitalOn(int digitalOutputPortId)
      {
      robotClient.setDigital(true, digitalOutputPortId);
      }

   /**
    * Sets the given digital output to off (or low) using setDigital.
    */
   public void setDigitalOff(int digitalOutputPortId)
      {
      robotClient.setDigital(false, digitalOutputPortId);
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
      robotClient.setLED(mode, ledId);
      }

   /** Sets the LED specified by the given id to on.
    *
    * @param ledId The ID of the LED to turn on, valid range is 0 to 9
    */
   public void setLEDOn(int ledId)
      {
      robotClient.setLED(LEDMode.On, ledId);
      }

   /** Sets the LED specified by the given id to off.
    *
    * @param ledId The ID of the LED to turn off, valid range is 0 to 9
    */
   public void setLEDOff(int ledId)
      {
      robotClient.setLED(LEDMode.Off, ledId);
      }

   /** Sets the LED specified by the given id to blinking.
    *
    * @param ledId The ID of the LED to blink, valid range is 0 to 9
    */
   public void setLEDBlinking(int ledId)
      {
      robotClient.setLED(LEDMode.Blinking, ledId);
      }

   /** Sets the LED specified by the given id to on if <code>state</code> is <code>true</code>; off otherwise.
    *
    * @param state Command the LED to on (true) or off (false)
    * @param ledId The ID of the LED to set
    */
   public void setLEDState(boolean state, int ledId)
      {
      robotClient.setLED(state ? LEDMode.On : LEDMode.Off, ledId);
      }

   /** Returns the voltage of the Qwerk's main power source in millivolts.
    *
    * @return Voltage of the Qwerk power source in millivolts */
   public int batteryVoltage()
      {
      return robotClient.batteryVoltage();
      }

   /** Returns the current state of the Qwerk's config button.
    *
    * @return True if the button is currently depressed, false otherwise.  */
   public boolean button()
      {
      return robotClient.button();
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
      return robotClient.waitForButtonOrStop();
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
      robotClient.playSound(filePath);
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
      robotClient.saySomething(text);
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
   public final void playTone(final int frequency, final int amplitude, final int duration)
      {
      robotClient.playTone(frequency, amplitude, duration);
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
      robotClient.playRoboticon(filename);
      }
   }