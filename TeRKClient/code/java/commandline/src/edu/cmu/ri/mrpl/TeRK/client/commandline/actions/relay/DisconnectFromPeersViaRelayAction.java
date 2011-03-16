package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class DisconnectFromPeersViaRelayAction extends RelayAction
   {
   public DisconnectFromPeersViaRelayAction(final RelayCommunicationHelper relayCommunicationHelper)
      {
      super(relayCommunicationHelper);
      }

   public void execute()
      {
      // make sure the relay communicator is running
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            getRelayCommunicator().disconnectFromPeers();
            setSimpleCommandLineQwerkPrx(null);
            println("   Disconnect successful!");
            }
         else
            {
            println("You must log in to the relay and connect to a peer before you can disconnect.");
            }
         }
      else
         {
         println("Relay communicator is not running, so you can't disconnect from a peer.");
         }
      }
   }
