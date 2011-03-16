import RobotClient.SimpleRobotClient;

public class photo_speak
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      SimpleRobotClient myRobot = new SimpleRobotClient();

      // It is strongly recommended that you use the following line.
      // This method blocks all further program execution until after the 'Start'
      // button has been pressed.
      myRobot.waitForPlay();

      // Write some code here!

      /* MODIFIED PHOTOVORE TO LIGHT-SPEAKER PROGRAM*/

      // Declaring variables to hold sensor values
      short leftPhoto;
      short rightPhoto;

      while (myRobot.isPlaying())
         {
         // Read in the sensor values from ports 2 and 3
         leftPhoto = myRobot.analog(2);
         rightPhoto = myRobot.analog(3);

         //The following if statements cause the speaker to play different wav
         //files based on the sensor value. Sensor values should be tested
         //to calibrate for different situations/configurations.

         if (leftPhoto < 2000 || rightPhoto < 2000)
            {
            myRobot.saySomething("Bright!");
            }
         else if (leftPhoto < 3000 || rightPhoto < 3000)
            {
            myRobot.saySomething("Normal");
            }
         else if (leftPhoto < 4400 || rightPhoto < 4400)
            {
            myRobot.saySomething("Dim");
            }
         else
            {
            myRobot.saySomething("Dark");
            }

         // Sleep for 8 seconds, so this loop occurs at about ? Hz
         myRobot.sleepUnlessStop(8000);

         // At each execution of the loop, print the sensor values to the text box
         // and reset the counter to 0

         myRobot.writeToTextBox("left light sensor: " + leftPhoto + "right light sensor: " + rightPhoto);
         }
      }

   // Or create some additional methods here!
   }