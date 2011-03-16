import RobotClient.CreateClient;

public class MyFirstCreate
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      CreateClient myRobot = new CreateClient("My First Create Robot");

      // It is strongly recommended that you use the following line.
      // This method blocks all further program execution until after the 'Start'
      // button has been pressed.
      myRobot.waitForPlay();

      myRobot.initialize();
      // Write some code here!
      }

   // Or create some additional methods here!
   }