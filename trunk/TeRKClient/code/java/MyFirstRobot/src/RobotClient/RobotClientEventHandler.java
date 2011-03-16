package RobotClient;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RobotClientEventHandler
   {
   /** Code to execute when the user presses the Play button. */
   void executeUponPlay();

   /** Code to execute when the user presses the Stop button. */
   void executeUponStop();
   }