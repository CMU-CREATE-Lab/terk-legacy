package finch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Polygon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandlerAdapter;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.robot.finch.application.BaseFinchApplication;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.TeRK.userinterface.DatasetPlotter;
import org.apache.log4j.Logger;

/**
 * Contains all methods necessary to program for the Finch robot
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 * @author Chris Bartley
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class Finch extends BaseFinchApplication
   {
   private static final Logger LOG = Logger.getLogger(Finch.class);

   private static final String DEFAULT_CONNECTION_STRATEGY_IMPLEMENTATION_CLASS = "edu.cmu.ri.createlab.TeRK.robot.finch.application.LocalFinchConnectionStrategy";

   private final Semaphore connectionCompleteSemaphore = new Semaphore(1);

   // Instantiate a SoundPlayer to allow the Finch class to have wrapper methods for playing sounds
   private final SoundPlayer sound = new SoundPlayer();

   private VideoPlayer video;

   private boolean videoOn = false;
   private boolean videoScreenOn = false;

   // set new plotters to graph sensor values
   private final DatasetPlotter<Double> accelerometerPlotter = new DatasetPlotter<Double>(-1.7, 1.7, 340, 340, 10, TimeUnit.MILLISECONDS);
   private final DatasetPlotter<Integer> lightPlotter = new DatasetPlotter<Integer>(-10, 270, 340, 340, 10, TimeUnit.MILLISECONDS);
   private final DatasetPlotter<Double> temperaturePlotter = new DatasetPlotter<Double>(0.0, 40.0, 340, 340, 10, TimeUnit.MILLISECONDS);

   // create accelerometer, temperature, and light sensor jFrames
   private JFrame jFrameAccel;
   private JFrame jFrameTemp;
   private JFrame jFrameLight;

   public Finch()
      {

      super(DEFAULT_CONNECTION_STRATEGY_IMPLEMENTATION_CLASS);

      System.out.println("Connecting to Finch...this may take a few seconds...");

      this.addConnectionStrategyEventHandler(
            new ConnectionStrategyEventHandlerAdapter()
            {
            public void handleConnectionEvent()
               {
               LOG.trace("Finch.handleConnectionEvent()");

               // connection complete, so release the lock
               connectionCompleteSemaphore.release();
               }

            public void handleFailedConnectionEvent()
               {
               LOG.trace("Finch.handleFailedConnectionEvent()");

               // connection failed, so release the lock
               connectionCompleteSemaphore.release();
               }
            });

      LOG.trace("Finch.Finch(): 1) aquiring connection lock");

      // acquire the lock, which will be released once the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("Finch.Finch(): 2) connecting");

      // try to connect
      connect();

      LOG.trace("Finch.Finch(): 3) waiting for connection to complete");

      // try to acquire the lock again, which will block until the connection is not complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("Finch.Finch(): 4) releasing lock");

      // we know the connection has completed (i.e. either connected or the connection failed) at this point, so just release the lock
      connectionCompleteSemaphore.release();

      LOG.trace("Finch.Finch(): 5) make sure we're actually connected");

      // if we're not connected, then throw an exception
      if (!isConnected())
         {
         LOG.error("Finch.Finch(): Failed to connect to the finch!  Aborting.");
         System.exit(1);
         }

      LOG.trace("Finch.Finch(): 6) All done!");
      }

   /**
    * Sets the color of the LED in the Finch's beak.  The LED can be any color that can be
    * created by mixing red, green, and blue; turning on all three colors in equal amounts results
    * in white light.  Valid ranges for the red, green, and blue elements are 0 to 255.
    *
    * @param     color is a Color object that determines the beaks color
    */
   public void setLED(final Color color)
      {
      if (color != null)
         {
         final FullColorLEDService service = getFullColorLEDService();
         if (service != null)
            {
            service.set(0, color);
            }
         else
            {
            System.out.println("LED not responding, check Finch connection");
            }
         }
      else
         {
         System.out.println("Color object was null, LED could not be set");
         }
      }

   /**
    * Sets the color of the LED in the Finch's beak.  The LED can be any color that can be
    * created by mixing red, green, and blue; turning on all three colors in equal amounts results
    * in white light.  Valid ranges for the red, green, and blue elements are 0 to 255.
    *
    * @param     red sets the intensity of the red element of the LED
    * @param     green sets the intensity of the green element of the LED
    * @param     blue sets the intensity of the blue element of the LED
    */

   public void setLED(final int red, final int green, final int blue)
      {
      boolean inRange = true;
      if (red > 255)
         {
         inRange = false;
         System.out.println("Red value exceeds appropriate values (0-255), LED will not be set");
         }
      if (red < 0)
         {
         inRange = false;
         System.out.println("Red value is negative, LED will not be set");
         }

      if (green > 255)
         {
         inRange = false;
         System.out.println("Green value exceeds appropriate values (0-255), LED will not be set");
         }
      if (green < 0)
         {
         inRange = false;
         System.out.println("Green value is negative, LED will not be set");
         }

      if (blue > 255)
         {
         inRange = false;
         System.out.println("Blue value exceeds appropriate values (0-255), LED will not be set");
         }
      if (blue < 0)
         {
         inRange = false;
         System.out.println("Blue value is negative, LED will not be set");
         }

      if (inRange)
         {
         setLED(new Color(red, green, blue));
         }
      }

   /**
    * Sets the Finch to travel to the left and right wheel distances specified by the variables leftDistance
    * and rightDistance at the speeds set by leftSpeed and rightSpeed.  Position is in centimeters and speed
    * is in centimeters/second.
    * The following are some example distance commands:
    * Travel 100 cm forward on both wheels at 5 cm/second:  finch.setWheelDistancesToTravel(100, 100, 5, 5);
    * Travel in an arc with left wheel going further at higher speed:   finch.setWheelDistancesToTravel(150, 50, 10, 5);
    * Rotate in place:  finch.setWheelDistancesToTravel(100, -100, 5, 5);
    * Move backwards in a straight line:  finch.setWheelDistancesToTravel(-100, -100, 5, 5);
    *
    * Note that using subsequent methods that set different distances to travel before the Finch has completed traveling
    * the distance set by calling this method will cause the Finch to immediately accept the new distance to travel.
    * @param  leftDistance the distance for the left wheel to travel in centimeters
    * @param  rightDistance the distance for the right wheel to travel in centimeters
    * @param leftSpeed the speed of the left wheel in centimeters/second
    * @param rightSpeed the speed of the right wheel in centimeters/second
    */

   public void setWheelDistancesToTravel(double leftDistance, double rightDistance, double leftSpeed, double rightSpeed)
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         if (leftSpeed < 36 && leftSpeed >= 0 && rightSpeed < 36 && rightSpeed >= 0)
            {
            double[] positions = {leftDistance, rightDistance};
            double[] velocities = {leftSpeed, rightSpeed};
            service.setPositions(positions, velocities);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * Stops both wheels in their current position
    */
   public void stopWheels()
      {
      setWheelDistancesToTravel(0, 0, 0, 0);
      }

   /**
    * Sets the robot to drive in a straight line by the specified distance in centimeters
    * at the specified speed in centimeters/second.  This method blocks further program execution
    * until it reaches its desired position or times out.  To use nonblocking motions, look at
    * setWheelDistancesToTravel or setWheelVelocities.  Use negative distance values to drive backwards.
    * speed sets the speed with which the line will be completed - valid values range from 1 to 35.
    *
    * @param distance the distance to travel in cm
    * @param speed   the speed at which to travel in cm/s
    */
   public void straight(double distance, double speed)
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         double leftPosition = service.getCurrentPosition(0);
         double rightPosition = service.getCurrentPosition(1);

         double leftTarget = leftPosition + distance;
         double rightTarget = rightPosition + distance;

         if (speed < 36 && speed >= 1)
            {
            long timeToComplete;

            if (distance < 0)
               {
               timeToComplete = (long)((-distance / speed) * 6000);
               }
            else
               {
               timeToComplete = (long)((distance / speed) * 6000);
               }

            if (timeToComplete < 6000)
               {
               timeToComplete = 6000;
               }

            long timeAtStart = System.currentTimeMillis();

            boolean notTimedOut = true;

            double[] positions = {distance, distance};
            double[] velocities = {speed, speed};
            service.setPositions(positions, velocities);

            if (distance < 0)
               {
               while (((leftTarget < (leftPosition)) || (rightTarget < (rightPosition))) && notTimedOut)
                  {
                  leftPosition = service.getCurrentPosition(0);
                  rightPosition = service.getCurrentPosition(1);
                  if ((System.currentTimeMillis() - timeAtStart) > timeToComplete)
                     {
                     System.out.println("Robot did not finish the straight line because it ran out of time");
                     stopWheels();
                     notTimedOut = false;
                     }
                  }
               }
            else
               {
               while (((leftTarget > (leftPosition)) || (rightTarget > (rightPosition))) && notTimedOut)
                  {
                  leftPosition = service.getCurrentPosition(0);
                  rightPosition = service.getCurrentPosition(1);
                  if ((System.currentTimeMillis() - timeAtStart) > timeToComplete)
                     {
                     System.out.println("Robot did not finish the straight line because it ran out of time");
                     stopWheels();
                     notTimedOut = false;
                     }
                  }
               }
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * Sets a distance for the left wheel to travel in centimeters.
    * Speed is the speed at which to move in cm/s, valid values range from 1 to 235.  Note that if another method call sets
    * a new distance for the left wheel to travel after this method call then this distance setting is discarded
    * regardless of whether the wheel traveled that distance.
    *
    * @param  distance distance to travel on left wheel
    * @param  leftSpeed speed at which to travel
    */

   public void setLeftWheelToTravel(double distance, double leftSpeed)
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         if (leftSpeed < 36 && leftSpeed > 0)
            {
            service.setPosition(0, distance, leftSpeed);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * Sets a distance for the right wheel to travel in centimeters.
    * Speed is the speed at which to move, valid values range from 1 to 35.  Note that if another method call sets
    * a new distance for the right wheel to travel after this method call then this distance setting is discarded
    * regardless of whether the wheel traveled that distance.
    *
    * @param  distance distance to travel on right wheel
    * @param  rightSpeed speed at which to travel
    */
   public void setRightWheelToTravel(double distance, double rightSpeed)
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         if (rightSpeed < 36 && rightSpeed > 0)
            {
            service.setPosition(1, distance, rightSpeed);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * Sets the robot to turn by the specified number of degrees at the specified velocity.  Left turns require
    * negative degree values; for example, to turn left 90 degrees, the degrees argument should be set to -90.
    * speed sets the speed at which the rotation will be completed in cm/s - valid values range from 1 to 35.
    * This method blocks further program execution until it reaches its desired position or times out.
    * To use nonblocking motions, look at setWheelDistancesToTravel or setWheelVelocities.
    *
    * @param degrees the number of degrees to rotate
    * @param speed   the speed at which to rotate in cm/s
    */
   public void turn(double degrees, double speed)
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         if (speed < 36 && speed >= 1)
            {
            double cm = degrees / 11.2787;
            long timeToComplete;

            if (cm < 0)
               {
               timeToComplete = (long)((-cm / speed) * 6000);
               }
            else
               {
               timeToComplete = (long)((cm / speed) * 6000);
               }
            if (timeToComplete < 6000)
               {
               timeToComplete = 6000;
               }

            long timeAtStart = System.currentTimeMillis();

            boolean notTimedOut = true;

            double leftPosition = service.getCurrentPosition(0);
            double rightPosition = service.getCurrentPosition(1);

            double leftTarget = leftPosition + cm;
            double rightTarget = rightPosition - cm;

            double positions[] = {cm, -cm};
            double velocities[] = {speed, speed};
            service.setPositions(positions, velocities);

            if (cm < 0)
               {
               while (((leftTarget < (leftPosition)) || (rightTarget > (rightPosition))) && notTimedOut)
                  {
                  leftPosition = service.getCurrentPosition(0);
                  rightPosition = service.getCurrentPosition(1);
                  if ((System.currentTimeMillis() - timeAtStart) > timeToComplete)
                     {
                     System.out.println("Robot did not finish the turn because it ran out of time");
                     stopWheels();
                     notTimedOut = false;
                     }
                  }
               }
            else
               {
               while (((leftTarget > (leftPosition)) || (rightTarget < (rightPosition))) && notTimedOut)
                  {
                  leftPosition = service.getCurrentPosition(0);
                  rightPosition = service.getCurrentPosition(1);
                  if ((System.currentTimeMillis() - timeAtStart) > timeToComplete)
                     {
                     System.out.println("Robot did not finish the turn because it ran out of time");
                     stopWheels();
                     notTimedOut = false;
                     }
                  }
               }
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * This method simultaneously sets the velocities of both wheels in centimeters/second.
    * Current valid values range from -35 to 35.
    *
    * @param     leftVelocity The velocity in cm/s at which to move the left wheel
    * @param     rightVelocity The velocity in cm/s at which to move the right wheel
    */
   public void setWheelVelocities(double leftVelocity, double rightVelocity)
      {
      VelocityControllableMotorService service = getVelocityControllableMotorService();
      if (service != null)
         {
         if (leftVelocity < 36 && leftVelocity > -36 && rightVelocity < 36 && rightVelocity > -36)
            {
            double[] velocities = {leftVelocity, rightVelocity};
            service.setVelocities(velocities);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * This method simultaneously sets the velocities of both wheels in centimeters/second.
    * Current valid values range from -35 to 35.  This method blocks further program execution
    * for the amount of time specified by timeToHold, and then stops the wheels once time has elapsed.
    *
    * @param     leftVelocity The velocity in cm/s at which to move the left wheel
    * @param     rightVelocity The velocity in cm/s at which to move the right wheel
    * @param     timeToHold The amount of time in milliseconds to hold the velocity for
    */
   public void setWheelVelocities(double leftVelocity, double rightVelocity, int timeToHold)
      {
      VelocityControllableMotorService service = getVelocityControllableMotorService();
      if (service != null)
         {
         if (leftVelocity < 36 && leftVelocity > -36 && rightVelocity < 36 && rightVelocity > -36)
            {
            double[] velocities = {leftVelocity, rightVelocity};
            service.setVelocities(velocities);
            sleep(timeToHold);
            stopWheels();
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * This method sets the velocity of the left wheel only in centimeters/second.
    * Current valid values range from -20 to 20.
    *
    * @param     leftVelocity The velocity in cm/s at which to move the left wheel
    */
   public void setLeftWheelVelocity(double leftVelocity)
      {
      VelocityControllableMotorService service = getVelocityControllableMotorService();
      if (service != null)
         {
         if (leftVelocity < 36 && leftVelocity > -36)
            {
            service.setVelocity(0, leftVelocity);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * This method sets the velocity of the right wheel in centimeters/second.
    * Current valid values range from -35 to 35.
    *
    * @param     rightVelocity The velocity at which to move the right wheel
    */
   public void setRightWheelVelocity(double rightVelocity)
      {
      VelocityControllableMotorService service = getVelocityControllableMotorService();
      if (service != null)
         {
         if (rightVelocity < 36 && rightVelocity > -36)
            {
            service.setVelocity(1, rightVelocity);
            }
         else
            {
            System.out.println("Velocity values out of range");
            }
         }
      else
         {
         System.out.println("Couldn't set motors, check Finch connection");
         }
      }

   /**
    * This method uses Thread.sleep to cause the currently running program to sleep for the
    * specified number of seconds.
    *
    * @param ms - the number of milliseconds to sleep for.  Valid values are all positive integers.
    */
   public void sleep(int ms)
      {

      if (ms < 0)
         {
         System.out.println("Program sent a negative time to sleep for");
         }
      else
         {
         try
            {
            Thread.sleep(ms);
            }
         catch (InterruptedException ie)
            {
            System.out.println("Error:  sleep was interrupted for some reason");
            }
         }
      }

   /**
    * This method returns the position of the left wheel in centimeters.  This position is the amount
    * the wheel has traveled since the robot object was instantiated.
    *
    * @return The position of the robot in centimeters
    */
   public double getLeftWheelDistance()
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         return service.getCurrentPosition(0);
         }
      else
         {
         System.out.println("Couldn't get current position, check Finch connection");
         return 0;
         }
      }

   /**
    * This method returns the position of the right wheel in centimeters.  This position is the amount
    * the wheel has traveled since the robot object was instantiated.
    *
    * @return The position of the robot in centimeters
    */
   public double getRightWheelDistance()
      {
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         return service.getCurrentPosition(1);
         }
      else
         {
         System.out.println("Couldn't get current position, check Finch connection");
         return 0;
         }
      }

   /**
    * This method returns the distance the wheels have traveled in centimeters since the robot started up or since it
    * was last reset.
    *
    * @return The position of the robot in encoder ticks; valid ranges are from -32768 to 32767.
    */
   public double[] getWheelDistances()
      {
      double[] distances = new double[2];
      PositionControllableMotorService service = getPositionControllableMotorService();
      if (service != null)
         {
         distances[0] = service.getCurrentPosition(0);
         distances[1] = service.getCurrentPosition(1);
         return distances;
         }
      else
         {
         System.out.println("Couldn't get current position, check Finch connection");
         return null;
         }
      }

   /**
    * This method returns the current X acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.
    *
    * @return The X acceleration value
    */
   public double getXAcceleration()
      {
      final AccelerometerService service = getAccelerometerService();
      if (service != null)
         {
         return service.getAccelerometerGs(0).getX();
         }
      else
         {
         System.out.println("Accelerometer not responding, check Finch connection");
         return 0.0;
         }
      }

   /**
    * This method returns the current Y acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.
    *
    * @return The Y acceleration value
    */
   public double getYAcceleration()
      {
      final AccelerometerService service = getAccelerometerService();
      if (service != null)
         {
         return service.getAccelerometerGs(0).getY();
         }
      else
         {
         System.out.println("Accelerometer not responding, check Finch connection");
         return 0.0;
         }
      }

   /**
    * This method returns the current Z acceleration value experienced by the robot.  Values for acceleration
    * range from -1.5 to +1.5g.
    *
    * @return The Z acceleration value
    */
   public double getZAcceleration()
      {
      final AccelerometerService service = getAccelerometerService();
      if (service != null)
         {
         return service.getAccelerometerGs(0).getZ();
         }
      else
         {
         System.out.println("Accelerometer not responding, check Finch connection");
         return 0.0;
         }
      }

   /**
    * Use this method to simultanesouly return the current X, Y, and Z accelerations experienced by the robot.
    * Values for acceleration can be in the range of -1.5g to +1.5g.  When the robot is on a flat surface,
    * X and Y should be close to 0g, and Z should be near +1.0g.
    *
    * @return a an array of 3 doubles containing the X, Y, and Z acceleration values
    */
   public double[] getAccelerations()
      {
      final AccelerometerService service = getAccelerometerService();
      double[] accelerations = new double[3];
      if (service != null)
         {
         accelerations[0] = service.getAccelerometerGs(0).getX();
         accelerations[1] = service.getAccelerometerGs(0).getY();
         accelerations[2] = service.getAccelerometerGs(0).getZ();
         return accelerations;
         }
      else
         {
         System.out.println("Accelerometer not responding, check Finch connection");
         return null;
         }
      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds.  Middle C is about 262Hz.  Visit http://www.phy.mtu.edu/~suits/notefreqs.html for
    * frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param duration The time to play the tone in milliseconds
    */
   public void playTone(int frequency, int duration)
      {
      sound.playTone(frequency, duration);
      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds at a specified volume.  Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param volume The volume of the tone on a 1 to 10 scale
    * @param duration The time to play the tone in milliseconds
    */
   public void playTone(int frequency, int volume, int duration)
      {
      sound.playTone(frequency, volume, duration);
      }

   /**
    * Plays a wav or mp3 file at the specificied fileLocation path.  If you place the audio
    * file in the same path as your source, you can just specify the name of the file.
    *
    * @param     fileLocation Absolute path of the file or name of the file if located in some directory as source code
    */
   public void playClip(String fileLocation)
      {
      sound.playClip(fileLocation);
      }

   /**
    * Takes the text of 'sayThis' and synthesizes it into a sound file.  Plays the sound file over
    * computer speakers.  sayThis can be arbitrarily long and can include variable arguments.
    *
    * Example:
    *   finch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
    *
    * @param     sayThis The string of text that will be spoken by the computer
    */
   public void saySomething(String sayThis)
      {
      sound.saySomething(sayThis);
      }

   /**
    * Plays a tone at the specified frequency for the specified duration on the Finch's internal buzzer.
    * Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    * Note that this is different from playTone, which plays a tone on the computer's speakers.
    *
    * @param     frequency Frequency in Hertz of the tone to be played
    * @param     duration  Duration in milliseconds of the tone
    */
   public void buzz(int frequency, int duration)
      {
      final BuzzerService service = getBuzzerService();
      if (service != null)
         {
         service.playTone(0, frequency, duration);
         }
      else
         {
         System.out.println("Buzzer not responding, check Finch connection");
         }
      }

   /**
    * Returns the value of the left light sensor.  Valid values range from 0 to 255, with higher
    * values indicating more light is being detected by the sensor.
    *
    *
    * @return The current light level at the left light sensor
    */
   public int getLeftLightSensor()
      {
      final PhotoresistorService service = getPhotoresistorService();
      if (service != null)
         {
         return service.getPhotoresistorValues()[0];
         }
      else
         {
         System.out.println("Light sensor not responding, check Finch connection");
         return 0;
         }
      }

   /**
    * Returns the value of the right light sensor.  Valid values range from 0 to 255, with higher
    * values indicating more light is being detected by the sensor.
    *
    *
    * @return The current light level at the right light sensor
    */
   public int getRightLightSensor()
      {
      final PhotoresistorService service = getPhotoresistorService();
      if (service != null)
         {
         return service.getPhotoresistorValues()[1];
         }
      else
         {
         System.out.println("Light sensor not responding, check Finch connection");
         return 0;
         }
      }

   /**
    * Returns a 2 integer array containing the current values of both light sensors.
    * The left sensor is the 0th array element, and the right sensor is the 1st element.
    *
    *
    * @return A 2 int array containing both light sensor readings.
    */
   public int[] getLightSensors()
      {
      final PhotoresistorService service = getPhotoresistorService();
      if (service != null)
         {
         return service.getPhotoresistorValues();
         }
      else
         {
         System.out.println("Light sensor not responding, check Finch connection");
         return null;
         }
      }

   /**
    * Returns true if the left light sensor is great than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the light sensor needs to exceed
    * @return whether the light sensor exceeds the value specified by limit
    */
   public boolean isLeftLightSensor(int limit)
      {
      return (limit > getLeftLightSensor());
      }

   /**
    * Returns true if the right light sensor is greater than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the light sensor needs to exceed
    * @return true if the light sensor exceeds the value specified by limit
    */
   public boolean isRightLightSensor(int limit)
      {
      return (limit > getRightLightSensor());
      }

   /**
    * Returns true if there is an obstruction in front of the left side of the robot.
    *
    *
    * @return Whether an obstacle exists in front of the left side of the robot.
    */
   public boolean isObstacleLeftSide()
      {
      final SimpleObstacleDetectorService service = getSimpleObstacleDetectorService();
      if (service != null)
         {
         return service.isObstacleDetected(0);
         }
      else
         {
         System.out.println("Obstacle sensor not responding, check Finch connection");
         return false;
         }
      }

   /**
    * Returns true if there is an obstruction in front of the right side of the robot.
    *
    *
    * @return Whether an obstacle exists in front of the right side of the robot.
    */
   public boolean isObstacleRightSide()
      {
      final SimpleObstacleDetectorService service = getSimpleObstacleDetectorService();
      if (service != null)
         {
         return service.isObstacleDetected(1);
         }
      else
         {
         System.out.println("Obstacle sensor not responding, check Finch connection");
         return false;
         }
      }

   /**
    * Returns true if either left or right obstacle sensor detect an obstacle.
    *
    *
    * @return Whether an obstacle exists in front of the robot.
    */
   public boolean isObstacle()
      {
      final SimpleObstacleDetectorService service = getSimpleObstacleDetectorService();
      if (service != null)
         {
         return (service.isObstacleDetected(0) || service.isObstacleDetected(1));
         }
      else
         {
         System.out.println("Obstacle sensor not responding, check Finch connection");
         return false;
         }
      }

   /**
    * Returns the value of both obstacle sensors as 2 element boolean array.
    * The left sensor is the 0th element, and the right sensor is the 1st element.
    *
    *
    * @return The values of left and right obstacle sensors in a 2 element array
    */
   public boolean[] getObstacleSensors()
      {
      final SimpleObstacleDetectorService service = getSimpleObstacleDetectorService();
      if (service != null)
         {
         return service.areObstaclesDetected();
         }
      else
         {
         System.out.println("Obstacle sensors not responding, check Finch connection");
         return null;
         }
      }

   /**
    * The current temperature reading at the temperature probe.  The value
    * returned is in Celsius.  To get Fahrenheit from Celsius, multiply the number
    * by 1.8 and then add 32.
    *
    * @return The current temperature in degrees Celsius
    */
   public double getTemperature()
      {
      final ThermistorService service = getThermistorService();
      if (service != null)
         {
         return service.getCelsiusTemperature(0);
         }
      else
         {
         System.out.println("Temperature sensor not responding, check Finch connection");
         return 0;
         }
      }

   /**
    * Returns true if the temperature is greater than the value specified
    * by limit, false otherwise.
    *
    * @param limit The value the temperature needs to exceed
    * @return true if the temperature exceeds the value specified by limit
    */
   public boolean isTemperature(double limit)
      {
      return (limit > getTemperature());
      }

   /**
    * Displays a graph of the X, Y, and Z accelerometer values.  Note that this graph
    * does not update on its own - you need to call updateAccelerometerGraph to
    * do so.
    *
    */

   public void showAccelerometerGraph()
      {
      accelerometerPlotter.addDataset(Color.RED);
      accelerometerPlotter.addDataset(Color.GREEN);
      accelerometerPlotter.addDataset(Color.BLUE);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = accelerometerPlotter.getComponent();

               // create the main frame
               jFrameAccel = new JFrame("Accelerometer Values");

               // add the root panel to the JFrame
               jFrameAccel.add(plotComponent);

               // set various properties for the JFrame
               jFrameAccel.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameAccel.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameAccel.setVisible(false);
                        jFrameAccel.dispose();
                        }
                     });
               jFrameAccel.setBackground(Color.WHITE);
               jFrameAccel.setResizable(false);
               jFrameAccel.pack();
               jFrameAccel.setLocation(400, 200);// center the window on the screen
               jFrameAccel.setVisible(true);
               }
            });
      }

   /**
    * updates the accelerometer graph with accelerometer data specified by xVal,
    * yVal, and zVal.
    *
    * @param xVal  The X axis acceleration value
    * @param yVal  The Y axis acceleration value
    * @param zVal  The Z axis acceleration value
    */
   public void updateAccelerometerGraph(double xVal, double yVal, double zVal)
      {
      accelerometerPlotter.setCurrentValues(xVal, yVal, zVal);
      }

   /**
    * Closes the opened Accelerometer Graph
    */
   public void closeAccelerometerGraph()
      {
      jFrameAccel.setVisible(false);
      jFrameAccel.dispose();
      }

   /**
    * Displays a graph of the left and right light sensor values.  Note that this graph
    * does not update on its own - you need to call updateLightSensorGraph to
    * do so.
    *
    */

   public void showLightSensorGraph()
      {
      lightPlotter.addDataset(Color.RED);
      lightPlotter.addDataset(Color.BLUE);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = lightPlotter.getComponent();

               // create the main frame
               jFrameLight = new JFrame("Light Sensor Values");

               // add the root panel to the JFrame
               jFrameLight.add(plotComponent);

               // set various properties for the JFrame
               jFrameLight.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameLight.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameLight.setVisible(false);
                        jFrameLight.dispose();
                        }
                     });
               jFrameLight.setBackground(Color.WHITE);
               jFrameLight.setResizable(false);
               jFrameLight.pack();
               jFrameLight.setLocation(20, 200);// center the window on the screen
               jFrameLight.setVisible(true);
               }
            });
      }

   /**
    * Updates the light sensor graph with the left and right light sensor data.
    *
    * @param leftSensor  Variable containing left light sensor value
    * @param rightSensor  Variable containing right light sensor value
    */
   public void updateLightSensorGraph(int leftSensor, int rightSensor)
      {
      lightPlotter.setCurrentValues(leftSensor, rightSensor);
      }

   /**
    * Closes the opened Light sensor Graph
    */
   public void closeLightSensorGraph()
      {
      jFrameLight.setVisible(false);
      jFrameLight.dispose();
      }

   /**
    * Displays a graph of the temperature value.  Note that this graph
    * does not update on its own - you need to call updateTemperatureGraph to
    * do so.
    *
    */

   public void showTemperatureGraph()
      {
      temperaturePlotter.addDataset(Color.GREEN);

      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final Component plotComponent = temperaturePlotter.getComponent();

               // create the main frame
               jFrameTemp = new JFrame("Temperature Values");

               // add the root panel to the JFrame
               jFrameTemp.add(plotComponent);

               // set various properties for the JFrame
               jFrameTemp.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameTemp.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameTemp.setVisible(false);
                        jFrameTemp.dispose();
                        }
                     });
               jFrameTemp.setBackground(Color.WHITE);
               jFrameTemp.setResizable(false);
               jFrameTemp.pack();
               jFrameTemp.setLocation(780, 200);// center the window on the screen
               jFrameTemp.setVisible(true);
               }
            });
      }

   /**
    * Updates the temperature graph with the most recent temperature data.
    *
    * @param temp   variable containing a temperature value
    */

   public void updateTemperatureGraph(double temp)
      {
      temperaturePlotter.setCurrentValues(temp);
      }

   /**
    * Closes the opened temperature Graph
    */
   public void closeTemperatureGraph()
      {
      jFrameTemp.setVisible(false);
      jFrameTemp.dispose();
      }

   /** Initializes and starts a new video stream, which can be used to track objects, react
    * to colors placed in the field of view of the camera, and to bring up a window of what
    * the camera is seeing.  Note that this does NOT automatically bring up the window showing
    * the camera image - call showVideoScreen() to show that.
    */
   public void initVideo()
      {
      video = new VideoPlayer();
      video.startVideoStream();
      videoOn = true;
      }

   /** Closes the video stream, done automatically in finch.quit but can also be performed earlier
    *  by the user.
    */
   public void closeVideo()
      {
      video.stopVideoStream();
      video.closeVideoStream();
      videoOn = false;
      }

   /**
    * Returns as a BufferedImage object the most recent image retrieved from the camera
    * @return The image data
    */
   public BufferedImage getImage()
      {
      return video.getImage();
      }

   /**
    * Get the image height
    * @return image height as an int
    */
   public int getImageHeight()
      {
      return video.getImageHeight();
      }

   /**
    * Get the image width
    * @return image width as an int
    */
   public int getImageWidth()
      {
      return video.getImageWidth();
      }

   /**
    * Gets the Red, Green, and Blue values of the pixel at the coordinate specified by x,y
    * @param x The row of the pixel
    * @param y The column of the pixel
    * @return An 3-int array of the red, green, and blue values of the pixel.  Values are 0 to 255 and
    * represent the intensity of color.
    */
   public int[] getPixelRGBValues(int x, int y)
      {
      return video.getPixelRGBValues(x, y);
      }

   /**
    * Gets the Color of a given pixel at the coordinate specified by x,y
    * @param x The row of the pixel
    * @param y The column of the pixel
    * @return A Color object representing the color of the pixel
    */
   public Color getPixelColor(int x, int y)
      {
      return video.getPixelColor(x, y);
      }

   /**
    * Gets the AVERAGE RGB values of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average values in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a 3 element array holding the red, green, and blue intensities of the area
    */
   public int[] getAreaRGBValues(int minX, int minY, int maxX, int maxY)
      {
      return video.getAreaRGBValues(minX, minY, maxX, maxY);
      }

   /**
    * Gets the AVERAGE Color value of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average color in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a Color object holding the average color of the area
    */

   public Color getAreaColor(int minX, int minY, int maxX, int maxY)
      {
      return video.getAreaColor(minX, minY, maxX, maxY);
      }

   /**
    * Method for getting back calibration values for the blob detector method.
    * Draws a rectangle on the screen and holds it there for five seconds.  To calibrate on an
    * object, make sure that it is entirely within the rectangle.  Calibration occurs at
    * the end of the method, so it is only necessary to have the object positioned properly
    * at the end of the five seconds.
    *
    * @return a 3 element array of red, green, and blue color values of the blob to be tracked
    */
   public int[] blobCalibration()
      {
      return video.blobCalibration();
      }

   /**
    * The blob detector detects all of the pixels that are within a certain range of the CalibrationVals,
    * where the width of the range is determined by the value sensitivity.  What the algorithm does is:
    * 1.  For every pixel, it compares the RGB values of that pixel to the calibration values; if the pixel's
    * R, G, AND B values are within the calibration values +/- the sensitivity, then the pixel is counted.
    * 2.  Take the average of all the counted pixels' coordinates to get the center of the blob.
    * 3.  Finds the edges of the blob by traversing the rows and columns of the image and setting an edge
    * when 1/10 of the total counted pixels have been seen.  Traversal is from top to bottom and left to right
    * to find the top and left edges respectively, and from bottom to top and right to left to find the bottom
    * and right edges.
    * The detector returns an array of six ints - elements 0 and 1 are the x,y coordinates of the center of the
    * blob, elements 2 and 3 are the minimum and maximum x coordinates, while elements 4 and 5 are the min and
    * max y coordinates.
    *
    * @param calibrationVals  An array containing the RGB values of the pixel to look for
    * @param sensitivity  The sensitivity of the detector - higher values lead to more noise, while low values
    * might not pick up very much of the object being tracked.  A suggested value for a brightly colored object is 10.
    * @return An array containing the center, top left, and bottom right x,y coordinates of the blob.
    */
   public int[] blobDetector(int[] calibrationVals, int sensitivity)
      {
      return video.blobDetector(calibrationVals, sensitivity);
      }

   /**
    * Displays a window that shows the camera image.  Note that the image must be updated
    * through program calls to the method updateVideoScreen().
    * @param name the name to give the window
    */
   public void showVideoScreen(String name)
      {
      video.drawVideo(name);
      videoScreenOn = true;
      }

   /**
    * Updates the image in the video window.  Note that this method also updates the image data
    * in the same way that getImage() does, so it is not necessary to call both getImage and updateVideoScreen.
    * Rather, call getImage() if your program does not display a video window, and use updateVideoScreen() if
    * it does display a window.
    */
   public void updateVideoScreen()
      {
      video.updateVideo();
      }

   /**
    * Closes the video window
    */
   public void closeVideoScreen()
      {
      video.closeVideo();
      videoScreenOn = false;
      }

   /**
    * Draws a rectangle in the video window showing the camera image.  Note
    * that once called, the rectangle will be persistent across all calls
    * of updateVideoScreen.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param minX minimum X coordinate of rectangle
    * @param minY maximum X coordinate of rectangle
    * @param maxX minimum Y coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    */
   public void drawRectangle(int minX, int minY, int maxX, int maxY)
      {
      video.drawRectangle(minX, minY, maxX, maxY);
      }

   /**
    * Draws a circle on the camera image
    * @param radius The radius of the circle in pixels
    * @param centerX The X coordinate of the center of the circle
    * @param centerY The Y coordinate of the center of the circle
    */
   public void drawCircle(int radius, int centerX, int centerY)
      {
      video.drawCircle(radius, centerX, centerY);
      }

   /**
    * Call this if you want to no longer display a polygon on the
    * camera image.
    */
   public void drawNothing()
      {
      video.drawNothing();
      }

   /**
    * Sets the color of any polygon, rectangle, or circle drawn into
    * the image.
    * @param polyColor The color to set the polygon to.
    */
   public void setPolygonColor(Color polyColor)
      {
      video.setPolygonColor(polyColor);
      }

   /**
    * Sets whether the polygon is filled in or an outline.
    * @param setting true sets the polygon to be filled in, false sets it to outline
    */
   public void setFillPolygon(boolean setting)
      {
      video.setFillPolygon(setting);
      }

   /**
    * Draws a generic polygon into the image.  Note
    * that once called, the polygon will be persistent across all calls
    * of updateVideo.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param poly The polygon object to draw into the image
    */
   public void drawPolygon(Polygon poly)
      {
      video.drawPolygon(poly);
      }

   /**
    * This method properly closes the connection with the Finch and resets the Finch so that
    * it is immediately ready to be controlled by subsequent programs.  Note that if this
    * method is not called at the end of the program, the Finch will continue to act on its
    * most recent command (such as drive forward) for 30 seconds before automatically timing
    * out and resetting.  This is why we recommend you always call the quit method at the end
    * of your program.
    */
   public void quit()
      {
      if (videoOn)
         {
         closeVideo();
         }
      if (jFrameAccel != null)
         {
         closeAccelerometerGraph();
         }
      if (jFrameLight != null)
         {
         closeLightSensorGraph();
         }
      if (jFrameTemp != null)
         {
         closeTemperatureGraph();
         }
      if (videoScreenOn)
         {
         closeVideoScreen();
         }
      shutdown();
      }
   }


