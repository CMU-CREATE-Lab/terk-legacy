/*  hearingTest.java - uses the RobotClient class and a Qwerkbot to conduct a hearing
 *  test for the user.  THIS PROGRAM DOES NOT WORK WELL DUE TO A BUG IN THE AUDIO 
 *  IMPLEMENTATION. 
 *  Author:  Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

import RobotClient.RobotClient;

public class hearingTest
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      RobotClient myRobot = new RobotClient();

      // Set the flag which decides to increase or decrease the sound
      boolean increase = true;
      int freq = 0;

      // Do the following forever
      while (true)
         {
         myRobot.writeToTextBox("This is a hearing test");
         myRobot.writeToTextBox("When you hit play, the speaker will emit a tone");
         myRobot.writeToTextBox("During the first cycle, the tone will increase in frequency");
         myRobot.writeToTextBox("During the second cycle, the tone will decrease in frequency");
         myRobot.writeToTextBox("Hit Stop when you can no longer hear it");

         // Wait until play is hit
         myRobot.waitForPlay();

         myRobot.playTone(10000, 9, 1000);

         myRobot.writeToTextBox("Playing Test");

         // Do the following as long as the robot is in play mode
         while (myRobot.isPlaying())
            {

            if (increase == true)
               {
               for (freq = 12000; freq < 30000; freq += 200)
                  {
                  myRobot.playTone(freq, 9, 100);
                  /*if(myRobot.sleepUnlessStop(100))
                           break;*/
                  }
               increase = false;
               }
            else
               {
               for (freq = 100; freq > 0; freq--)
                  {
                  myRobot.playTone(freq, 9, 100);
                  if (myRobot.sleepUnlessStop(100))
                     {
                     break;
                     }
                  }
               increase = true;
               }
            }

         myRobot.writeToTextBox("The Frequency you stopped at was: " + freq + " Hz");
         }
      }

   // Or create some additional methods here!
   }