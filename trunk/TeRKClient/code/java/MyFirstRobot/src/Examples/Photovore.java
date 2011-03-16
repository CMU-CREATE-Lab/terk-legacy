/*  Photovore.java - uses the RobotClient class and a Qwerkbot to track
 *  a light source. 
 *  Author:  Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

import RobotClient.RobotClient;

public class Photovore
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      RobotClient myRobot = new RobotClient();

      // Declare some variables for the light sensor values
      short leftPhoto;
      short rightPhoto;

      // Do the following forever
      while (true)
         {
         // Wait until play is hit
         myRobot.waitForPlay();

         // Counter variable to count loops
         int count = 0;

         // Do the following as long as the robot is in play mode
         while (myRobot.isPlaying())
            {
            // Read in the sensor values from ports 2 and 3
            leftPhoto = myRobot.analog(2);
            rightPhoto = myRobot.analog(3);

            /* Move the robot according to the sensor data.  Tie the right sensor
                  * input to the left motor and vice verca, so that when light intensity
                  * on the right sensor increases, the leftmotor spins faster.  The reason
                  * for the sensor value being subtracted from 5000 is because the sensor
                  * value actually declines with increased light intensity.  The reason the
                  * left motor is negative is because the motors are mirrored on the robot.
                  */
            myRobot.moveMotors(-(5000 - rightPhoto) * 2, (5000 - leftPhoto) * 2);

            // Sleep for 50 ms, so this loop occurs at about 20 Hz
            myRobot.sleepUnlessStop(50);
            count++;

            // If you've done 10 loops, print the sensor values to the text box
            // and reset the counter to 0
            if (count == 10)
               {
               myRobot.writeToTextBox("left light sensor: " + leftPhoto + "right light sensor: " + rightPhoto);
               count = 0;
               }
            }
         // Stop the motors when the loop is over/the stop button has been hit
         myRobot.stopMotors();
         }
      }

   // Or create some additional methods here!
   }