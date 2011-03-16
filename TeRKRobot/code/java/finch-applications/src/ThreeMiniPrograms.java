/**
 * @author Victor M. Marmol G. (vmarmol@andrew.cmu.edu)
 * @author Tilak Sharma (tilaks@andrew.cmu.edu)
 *
 * http://www.viberobotics.com/finch/
 *
 * Version 1.0
 * February 14-16, 2008
 *
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import finch.Finch;

public class ThreeMiniPrograms
   {

   //Private global values, our finch, calibration, and what we use to read text
   private static Finch myFinch;
   private static Calibration myCalibration;
   private static Scanner in;

   public static void main(final String[] args)
      {
      in = new Scanner(System.in);

      // Instantiating the Finch object
      myFinch = new Finch();

      //Fade In
      for (int i = 0; i <= 255; i += 3)
         {
         //Make it i bright
         myFinch.setLED(0, i, 0);
         }

      //Check if the user wants to manually calibrate
      boolean blocked[] = myFinch.getObstacleSensors();
      if (blocked[0] == true || blocked[1] == true)
         {
         //Lets go ahead and calibrate
         //Instantiate our calibration class
         myCalibration = new Calibration(myFinch);

         //Call the function that handles calibration
         calibrate();
         }
      else //See if we can do default calibration
         {
         //Check if we have a calibration file
         try
            {
            double[][] calibration = new double[3][3];

            //If this file isn't found then a FileNotFoundException is thrown so we will catch it below
            BufferedReader filein = new BufferedReader(new FileReader("calibration.txt"));

            //Read three lines with three doubles each
            for (int i = 0; i < 3; i++)
               {
               String line = filein.readLine();
               Scanner linein = new Scanner(line);
               calibration[i][0] = linein.nextDouble();
               calibration[i][1] = linein.nextDouble();
               calibration[i][2] = linein.nextDouble();
               }

            //Close the file
            filein.close();

            //Instantiate our calibration class, and give it the saved values
            myCalibration = new Calibration(myFinch, calibration[0], calibration[1], calibration[2]);
            }
         catch (FileNotFoundException fnf)
            {
            //No calibration file. Lets make one
            //Instantiate our calibration class
            myCalibration = new Calibration(myFinch);

            //Call the function that handles calibration
            calibrate();
            }
         catch (IOException e)
            {
            //OMG!!!
            }
         }

      //Enter our interactive menu!
      menu();

      //Fade out
      for (int i = 255; i >= 0; i -= 3)
         {
         //Make it i bright
         myFinch.setLED(0, i, 0);
         }
      // Always end your program with finch.quit()
      myFinch.quit();
      System.exit(0);
      }

   /**
    * Menu displaying the robot's 3 mini-programs and allowing the user to select between them
    */
   private static void menu()
      {
      //Green Menu light
      myFinch.setLED(0, 255, 0);

      //Call the tipping menu to get what mini program we are going to run
      System.out.println("Menu options:");
      System.out.println("Beak Down = Position ColorBlend");
      System.out.println("Left = Light Follow");
      System.out.println("Right = GoTo string");
      System.out.println("Beak Up = Quit");
      System.out.println("Tip the robot to pick an option.");
      System.out.println("Press enter when ready. Hold in position until the light turns blue.");
      in.nextLine(); //Waits for a space

      //Get option and
      int run = getTipping();

      //Run the selected option
      switch (run)
         {
         case 1:
            //Go to color option
            System.out.println("Time for some ColorBlending!  To exit, breathe on the thermistor.");
            myFinch.sleep(3000);
            colorOption();
            break;
         case 2:
            //Time to go
            System.out.println("See you next time!");
            break;
         case 3:
            //Go to light follow
            System.out.println("Time to follow some lights!  To exit, breathe on the thermistor.");
            myFinch.sleep(3000);
            lightFollow();
            break;
         case 4:
            move();
            break;
         default:
            System.out.println("I didn't get that, try again");
            menu();
            break;
         }
      }

   /**
    * Figures out which way the robot was tipped and returns its integer meaning:
    * 1 - FRONT
    * 2 - BACK
    * 3 - LEFT
    * 4 - RIGHT
    *
    * @return = which of the 4 options the robot was tipped to
    */
   private static int getTipping()
      {
      int option = 0;

      //Give instructions and wait 1 second to give the user time
      myFinch.sleep(2000);

      //Number of readings we will average
      int numReadings = 10;

      //How much we will allow the user to be off by on the tipping
      double error = 0.2;

      double readX = 0;
      double readY = 0;
      double readZ = 0;

      //Take numReadings readings while Finch is being tipped
      for (int i = 0; i < numReadings; i++)
         {
         //Take readings for X, Y, and Z
         readX += myCalibration.getXAcceleration();
         readY += myCalibration.getYAcceleration();
         readZ += myCalibration.getZAcceleration();
         }

      //Divide totals by numReadings to find the average (we just took numReadings readings)
      readX = readX / numReadings;
      readY = readY / numReadings;
      readZ = readZ / numReadings;

      /*
      * Find out which of the 4 options the user picked
      * FRONT (Option 1) = Negative X
      * BACK (Option 2) = Positive X
      * LEFT (Option 3) = Positive Y
      * RIGHT (Option 4) = Negative Y
      */
      if (readX < -error)
         {
         option = 1;
         }
      else if (readX > error)
         {
         option = 2;
         }
      else if (readY > error)
         {
         option = 3;
         }
      else if (readY < -error)
         {
         option = 4;
         }

      //Done calibrating, turn light blue
      myFinch.setLED(0, 0, 255);

      return (option);
      }

   /**
    * Function that takes the user through the calibration procedure and stores the result in a text file
    */
   private static void calibrate()
      {
      //There is no default calibration make a new one!
      System.out.println("Hey! Let's calibrate our finch.\nPlace it horizontally in a flat surface. Press enter when ready.");
      in.nextLine();
      myCalibration.calibrateHorizontal();
      System.out.println("Now put it vertically (nose-up). Press enter when ready.");
      in.nextLine();
      myCalibration.calibrateVertical();
      System.out.println("Now put it flat against a wall (left side down). Press enter when ready.");
      in.nextLine();
      myCalibration.calibrateWall();
      System.out.println("Calibrated!\n" + myCalibration.toString());

      //Save the calibration to a file
      BufferedWriter out;
      try
         {
         out = new BufferedWriter(new FileWriter("calibration.txt"));
         out.write(myCalibration.horizontalCalibration[0] + " " + myCalibration.horizontalCalibration[1] + " " + myCalibration.horizontalCalibration[2] + "\n");
         out.write(myCalibration.verticalCalibration[0] + " " + myCalibration.verticalCalibration[1] + " " + myCalibration.verticalCalibration[2] + "\n");
         out.write(myCalibration.wallCalibration[0] + " " + myCalibration.wallCalibration[1] + " " + myCalibration.wallCalibration[2]);
         out.close();
         }
      catch (IOException e)
         {
         //OMG!!
         }
      }

   /**
    * Mode where the robot displays different intensities of red and blue depending on its pitch and yaw
    */
   private static void colorOption()
      {
      //Get the initial temperature and add 1.5 as a margin of error
      double initialTemp = myFinch.getTemperature() + 1.5;

      //We only exit when we blow hot breath on the finch
      while (initialTemp >= myFinch.getTemperature())
         {
         //Get the calibrated angles
         double xAngle = myCalibration.getXAngle();
         double yAngle = myCalibration.getYAngle();

         //Scale it (turn 0 - 90 degree angle to a 0 - 255 LED value)
         int scaleX = (int)Math.abs(xAngle / 90 * 255);
         int scaleY = (int)Math.abs(yAngle / 90 * 255);

         //If the scaled value is more than 255 make it 255 (to account for scattered > 90 degree angles)
         if (scaleX > 255)
            {
            scaleX = 255;
            }
         if (scaleY > 255)
            {
            scaleY = 255;
            }

         //Set the LED and wait a bit
         myFinch.setLED(scaleY, 0, scaleX);
         myFinch.sleep(100);
         }

      //Return to the menu
      menu();
      }

   /**
    * Mode where the robot follows light
    */
   private static void lightFollow()
      {
      //Get the initial temperature and add 0.5 as a margin of error
      double initialTemp = myFinch.getTemperature() + 0.5;

      //Acceptable margin of error between the sensors
      int error = 5;

      //We only exit when we blow hot breath on the finch
      while (initialTemp >= myFinch.getTemperature())
         {
         //Get sensor readings
         int left = myFinch.getLeftLightSensor();
         int right = myFinch.getRightLightSensor();

         if (left > (right + error)) //If there is more light to the left
            {
            //Turn a bit to the left
            myFinch.turn(-10, 10);
            }
         else if (right > (left + 5)) //There is more light to the right
            {
            //Turn a bit to the right
            myFinch.turn(10, 10);
            }
         else //Light is straight ahead
            {
            //Go straight
            myFinch.straight(2, 15);
            }
         }

      //Return to the menu
      menu();
      }

   /**
    * move()
    * Mode where the robot takes a series of points and goes to them in sequential order
    */
   private static void move()
      {
      //Get the GoTo points
      System.out.println("Please input the points where you would like the robot to go in the format (x,y) (x,y) (x,y). Separated by spaces.");
      System.out.println("Press enter when done:");
      String input = in.nextLine();

      //Separate the string by spaces
      String points[] = input.split("( )");

      //We start at (0,0)
      int x = 0;
      int y = 0;

      //GoTo for each of the ones we were told to go to
      for (int i = 0; i < points.length; i++)
         {
         //Get X and Y by parsing the string
         int thisX = Integer.parseInt(points[i].substring(points[i].indexOf("(") + 1, points[i].indexOf(",")));
         int thisY = Integer.parseInt(points[i].substring(points[i].indexOf(",") + 1, points[i].indexOf(")")));

         //Handle the GoTo with the GoTo function (It accepts points from the robot's point of view. aka the robot is always (0,0) so we account for that)
         goTo(thisX - x, thisY - y, 15);

         //Set this point as our current position
         x = thisX;
         y = thisY;
         }

      //Return to the menu
      menu();
      }

   /**
    * goTo()
    * Sends the robot to the specified position at the specified speed.
    * The position is relative to the robot's current position. Aka the robot thinks its always at (0,0).
    * The robot turns towards the point and then drives straight to it.
    *
    * @param x = x coordinate to travel to
    * @param y = y coordinate to travel to
    * @param speed = speed to travel at in cm/s
    *
    * BUG:
    * The function doesn't work as specified under certain conditions. Will be fixed in version 1.1
    */
   public static void goTo(int x, int y, double speed)
      {
      //Calculate how much the robot has to turn (in degrees)
      double turnAmount = Math.toDegrees(Math.atan2(y, x));

      //Distance robot has to travel straight (in cm)
      double distance = Math.sqrt(y * y + x * x);

      //Tell the robot to turn and drive straight
      myFinch.turn(turnAmount, 10);
      myFinch.straight(distance, speed);
      }
   }