import RobotClient.SimpleRobotClient;

public class MyFirstRobot
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      SimpleRobotClient myRobot = new SimpleRobotClient("My First Robot");

      // It is strongly recommended that you use the following line.
      // This method blocks all further program execution until after the 'Start'
      // button has been pressed.
      myRobot.waitForPlay();

      // Write some code here!
      }

   // Or Create some additional methods here!
   }