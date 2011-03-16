/*  Headturner.java - uses the RobotClient class and a Qwerkbot to turn
 *  until an object is close by.  To sense distance, it uses two sharp IR
 *  rangefinders point forward.  
 *  Author:  Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

import RobotClient.RobotClient;

public class Headturner
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      RobotClient myRobot = new RobotClient();

      // Write some code here!

      short leftIR;
      short rightIR;

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
            // Read in the sensor values from ports 1 and 0
            leftIR = myRobot.analog(1);
            rightIR = myRobot.analog(0);

            // If an object is close, stop
            if (leftIR > 1400 || rightIR > 1400)
               {
               myRobot.stopMotors();
               }
            // Else, rotate
            else
               {
               myRobot.moveMotors(10000, 10000);
               }

            // Sleep for 50 ms, so this loop occurs at about 20 Hz
            myRobot.sleepUnlessStop(50);

            count++;

            // If you've done ten loops, print the sensor values to the text box
            // and reset the counter to 0
            if (count == 10)
               {
               myRobot.writeToTextBox("leftIR: " + leftIR + "rightIR: " + rightIR);
               count = 0;
               }
            }
         // Stop the motors when the loop is over
         myRobot.stopMotors();
         }
      }

   // Or create some additional methods here!
   }