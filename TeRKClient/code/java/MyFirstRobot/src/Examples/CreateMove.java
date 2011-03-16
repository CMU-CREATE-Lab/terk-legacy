import RobotClient.CreateClient;

public class CreateMove
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      CreateClient myRobot = new CreateClient("Create Dumb Wanderer");

      // It is strongly recommended that you use the following line.
      // This method blocks all further program execution until after the 'Start'
      // button has been pressed.

      myRobot.waitForPlay();

      // In order to run the Create, you must use the initialize method after play button has been pressed
      myRobot.initialize();

      myRobot.unDockRobot();

      // Do the following until the stop button is pressed
      while (myRobot.isPlaying())
         {

         // If the right bumper is pressed, back up, turn left, and apologize
         if (myRobot.bumpRight())
            {
            myRobot.moveMotors(-300, -300, 600);
            myRobot.moveMotors(-150, 150, 1000);
            myRobot.saySomething("Sorry Dude");
            }

         // If the left bumper is pressed, back up, turn right, and apologize
         if (myRobot.bumpLeft())
            {
            myRobot.moveMotors(-300, -300, 600);
            myRobot.moveMotors(150, -150, 1000);
            myRobot.saySomething("Sorry Man");
            }

         // Just move the motors at 150 mm/s, and sleep for 200 ms
         myRobot.moveMotors(150, 150);
         myRobot.sleepUnlessStop(200);
         }

      // Once the stop button is pressed, stop moving and have the robot dock itself
      myRobot.stopMoving();
      myRobot.dockRobot();

      // Always run close robot after the last command is sent - this closes down the communication between the
      // Qwerk and the create
      myRobot.closeRobot();
      }
   }