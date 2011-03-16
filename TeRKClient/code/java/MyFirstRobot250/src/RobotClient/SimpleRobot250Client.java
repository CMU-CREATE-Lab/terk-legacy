package RobotClient;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import RSSReaders.FakeNewsReader;
import RSSReaders.FakeSevereWeatherReader;
import RSSReaders.FakeStockComparisonReader;
import RSSReaders.KeywordMatchingNewsReader;
import RSSReaders.NewsHeadlineCounter;
import RSSReaders.SevereWeatherEventCounter;
import RSSReaders.StockComparisonChangeStatus;
import RSSReaders.StockComparisonReader;
import RSSReaders.StockPriceChangeStatusComparator;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;

/**
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimpleRobot250Client
   {
   // stock symbols for the stock comparison reader
   private static final String STOCK_SYMBOL_1 = "MCD";// MCD = McDonalds
   private static final String STOCK_SYMBOL_2 = "PFE";// PFE = Pfizer

   /** An unmodifiable set of violence-related keywords. */
   private static final Set<String> VIOLENCE_KEYWORDS;

   static
      {
      final Set<String> defaultKeywords = new HashSet<String>();
      defaultKeywords.add("assault");
      defaultKeywords.add("assaults");
      defaultKeywords.add("assaulted");
      defaultKeywords.add("assaulting");
      defaultKeywords.add("attack");
      defaultKeywords.add("attacks");
      defaultKeywords.add("attacked");
      defaultKeywords.add("attacker");
      defaultKeywords.add("bomb");
      defaultKeywords.add("bombs");
      defaultKeywords.add("bombed");
      defaultKeywords.add("bomber");
      defaultKeywords.add("bombing");
      defaultKeywords.add("dead");
      defaultKeywords.add("death");
      defaultKeywords.add("deaths");
      defaultKeywords.add("gun");
      defaultKeywords.add("gunned");
      defaultKeywords.add("weapon");
      defaultKeywords.add("weapons");
      defaultKeywords.add("murder");
      defaultKeywords.add("murders");
      defaultKeywords.add("murdered");
      defaultKeywords.add("rape");
      defaultKeywords.add("raped");
      defaultKeywords.add("rapes");
      defaultKeywords.add("raping");
      defaultKeywords.add("manslaughter");
      defaultKeywords.add("kill");
      defaultKeywords.add("kills");
      defaultKeywords.add("killed");
      defaultKeywords.add("killing");
      defaultKeywords.add("killings");
      defaultKeywords.add("stabbed");
      defaultKeywords.add("stabbing");
      defaultKeywords.add("beat");
      defaultKeywords.add("mugger");
      defaultKeywords.add("mugged");
      defaultKeywords.add("mugging");
      defaultKeywords.add("homicide");
      defaultKeywords.add("homicides");
      defaultKeywords.add("slayed");
      defaultKeywords.add("slaying");
      defaultKeywords.add("slayings");
      defaultKeywords.add("massacre");
      defaultKeywords.add("massacres");
      defaultKeywords.add("shot");
      defaultKeywords.add("shooting");
      defaultKeywords.add("shootings");
      VIOLENCE_KEYWORDS = Collections.unmodifiableSet(defaultKeywords);
      }

   private Robot250Client robot250Client;

   // instatiate the readers, default to using the fake ones
   private NewsHeadlineCounter violenceNewsReader = new FakeNewsReader();
   private SevereWeatherEventCounter severeWeatherEventCounter = new FakeSevereWeatherReader();
   private StockPriceChangeStatusComparator stockPriceChangeStatusComparator = new FakeStockComparisonReader();

   /** Starts the SimpleRobot250Client by running a GUI with the APPLICATION_NAME (Currently "My First Robot Program").  */
   public SimpleRobot250Client()
      {
      robot250Client = new Robot250Client();
      }

   /** Starts the SimpleRobot250Client by running a GUI titled by the String applicationName.
    *
    * @param applicationName String text which sets the title of the Robot250Client GUI
    */
   public SimpleRobot250Client(String applicationName)
      {
      robot250Client = new Robot250Client(applicationName);
      }

   /** Returns the value of the play/stop button.  If 'Play' was most recently pressed,
    *  it will return true.  If 'Stop' was most recently pressed, it will return false.
    *
    * @return the state of the Play/Stop button */
   public boolean buttonState()
      {
      return robot250Client.buttonState();
      }

   /**
    *
    * @return true if 'stop' was most recently pressed, false otherwise. */
   public boolean isStopped()
      {
      return robot250Client.isStopped();
      }

   /**
    *
    * @return true if 'play' was most recently pressed, false otherwise.
    */
   public boolean isPlaying()
      {
      return robot250Client.isPlaying();
      }

   /** Blocks any further program operation until the play button is pressed. */
   public void waitForPlay()
      {
      robot250Client.waitForPlay();
      }

   /** Blocks any further program operation until the stop button is pressed. */
   public void waitForStop()
      {
      robot250Client.waitForStop();
      }

   /** Sleeps the program for a given number of milliseconds.  If the Stop button is pressed, this method
    * immediately exits without sleeping.
    *
    * @param ms Number of milliseconds to sleep the program for */
   public boolean sleepUnlessStop(final int ms)
      {
      return robot250Client.sleepUnlessStop(ms);
      }

   /** Writes the message string to the GUI's textbox.  The message is always preceded by a timestamp.
    *
    * @param message String containing information to be written to the GUI textbox*/
   public final void writeToTextBox(final String message)
      {
      robot250Client.writeToTextBox(message);
      }

   /** Clears the text box area. */
   public final void clearTextBox()
      {
      robot250Client.clearTextBox();
      }

   /**
    * Returns the contents of the text field as an int.  Returns 0 if the text field is empty or the value cannot be
    * converted to an integer.
    *
    * @return The value of the text field as an integer
    */
   public final int getTextFieldValueAsInt()
      {
      return robot250Client.getTextFieldValueAsInt();
      }

   /** Returns the contents of the text field as a String.
    *
    * @return The value of the text field as a String
    */
   public final String getTextFieldValueAsString()
      {
      return robot250Client.getTextFieldValueAsString();
      }

   /** Moves the motor specified by the given <code>motorId</code> at the given <code>velocity</code>.
    *
    * @param motorId The id of the motor to command - valid range is 0 to 3
    * @param velocity The velocity of the motor.*/
   public void moveMotor(int motorId, int velocity)
      {
      robot250Client.moveMotor(motorId, velocity);
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
      robot250Client.moveMotors(leftMotorVelocity, rightMotorVelocity);
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
      robot250Client.moveMotors(leftMotorVelocity, rightMotorVelocity, runningTime);
      }

   /**
    *	Sets all four Qwerk motor ports to 0 velocity.
    */
   public void stopMotors()
      {
      robot250Client.stopMotors();
      }

   /** Sets the servo specified by the given <code>servoId</code> to the given <code>position</code>.
    *
    * @param servoId The ID of the servo to be commanded - valid range is 0 to 15
    * @param position The position to set the servo to - valid range is 0 to 255*/
   public void setServo(int servoId, int position)
      {
      robot250Client.setServo(servoId, position);
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
      return robot250Client.analog(analogInputPortId);
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
      return robot250Client.digital(digitalInputPortId);
      }

   /**
    * Sets the given digital output to a given state.
    *
    * @param state The state to set the ouput to - true corresponds to a high output signal and false to a low signal.
    * @param digitalOutputPortId The output port to set
    */
   public void setDigital(boolean state, int digitalOutputPortId)
      {
      robot250Client.setDigital(state, digitalOutputPortId);
      }

   /**
    * Sets the given digital output to on (or high) using setDigital.
    */
   public void setDigitalOn(int digitalOutputPortId)
      {
      robot250Client.setDigital(true, digitalOutputPortId);
      }

   /**
    * Sets the given digital output to off (or low) using setDigital.
    */
   public void setDigitalOff(int digitalOutputPortId)
      {
      robot250Client.setDigital(false, digitalOutputPortId);
      }

   /** Sets the LED specified by the given id to on.
    *
    * @param ledId The ID of the LED to turn on, valid range is 0 to 9
    */
   public void setLEDOn(int ledId)
      {
      robot250Client.setLEDOn(ledId);
      }

   /** Sets the LED specified by the given id to off.
    *
    * @param ledId The ID of the LED to turn off, valid range is 0 to 9
    */
   public void setLEDOff(int ledId)
      {
      robot250Client.setLEDOff(ledId);
      }

   /** Sets the LED specified by the given id to on if <code>state</code> is <code>true</code>; off otherwise.
    *
    * @param state Command the LED to on (true) or off (false)
    * @param ledId The ID of the LED to set
    */
   public void setLEDState(boolean state, int ledId)
      {
      if (state)
         {
         robot250Client.setLEDOn(ledId);
         }
      else
         {
         robot250Client.setLEDOff(ledId);
         }
      }

   /** Returns the voltage of the Qwerk's main power source in millivolts.
    *
    * @return Voltage of the Qwerk power source in millivolts */
   public int batteryVoltage()
      {
      return robot250Client.batteryVoltage();
      }

   /** Returns the current state of the Qwerk's config button.
    *
    * @return True if the button is currently depressed, false otherwise.  */
   public boolean button()
      {
      return robot250Client.button();
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
      return robot250Client.waitForButtonOrStop();
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
      robot250Client.playSound(filePath);
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
      robot250Client.saySomething(text);
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
      robot250Client.playTone(frequency, amplitude, duration);
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
      robot250Client.playRoboticon(filename);
      }

   /** Enables the user to toggle between using fake RSS Readers (the default) and real ones. */
   public void useFakeRSSReaders(final boolean willUseFake)
      {
      violenceNewsReader = willUseFake ? new FakeNewsReader() : new KeywordMatchingNewsReader(VIOLENCE_KEYWORDS);
      stockPriceChangeStatusComparator = willUseFake ? new FakeStockComparisonReader() : new StockComparisonReader(STOCK_SYMBOL_1, STOCK_SYMBOL_2);
      severeWeatherEventCounter = new FakeSevereWeatherReader();// todo: enable usage of real reader once it's finished
      }

   /**
    * Returns the number of new (since the last time this method was called) headlines containing violence-related words.
    */
   public int getNumberOfNewViolenceRelatedHeadlines()
      {
      return violenceNewsReader.getHeadlineCount();
      }

   /** Returns the number of new (since the last time this method was called) severe weather events. */
   public int getNumberOfNewSevereWeatherEvents()
      {
      return severeWeatherEventCounter.getEventCount();
      }

   /**
    * Returns the {@link StockComparisonChangeStatus} representing the status change of the two stocks since the last
    * time this method was called.
    */
   public StockComparisonChangeStatus getStockComparisonChangeStatus()
      {
      return stockPriceChangeStatusComparator.getChangeStatus();
      }
   }