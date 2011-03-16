package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class DisconnectFromPeerViaRelayAction extends RelayAction
   {
   private static final Logger LOG = Logger.getLogger(DisconnectFromPeerViaRelayAction.class);

   private final BufferedReader in;

   public DisconnectFromPeerViaRelayAction(final RelayCommunicationHelper relayCommunicationHelper, final BufferedReader in)
      {
      super(relayCommunicationHelper);
      this.in = in;
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            if (getSimpleCommandLineQwerkPrx() != null)
               {
               try
                  {
                  print("   robot id: ");
                  final String robotId = in.readLine();
                  if (robotId == null)
                     {
                     println("Robot to disconnect from was null, aborting.");
                     }
                  else
                     {
                     getRelayCommunicator().disconnectFromPeer(robotId);
                     setSimpleCommandLineQwerkPrx(null);
                     }
                  }
               catch (IOException e)
                  {
                  LOG.error("IOException while reading user input", e);
                  }
               }
            else
               {
               println("SimpleCommandLineQwerkPrx is null (you need to connect to a peer first)");
               }
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
