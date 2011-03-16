package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class LogoutAction extends RelayAction
   {
   public LogoutAction(final RelayCommunicationHelper relayCommunicationHelper)
      {
      super(relayCommunicationHelper);
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            getRelayCommunicator().logout();
            setSimpleCommandLineQwerkPrx(null);
            println("Logout successful.");
            }
         else
            {
            println("You are not logged in!");
            }
         }
      else
         {
         println("Relay communicator is not running, so you can't log out.");
         }
      }
   }
