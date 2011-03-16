package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import java.io.BufferedReader;
import java.io.IOException;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrxHelper;
import edu.cmu.ri.mrpl.peer.PeerException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ConnectToPeerViaRelayAction extends RelayAction
   {
   private static final Logger LOG = Logger.getLogger(ConnectToPeerViaRelayAction.class);

   private final BufferedReader in;

   public ConnectToPeerViaRelayAction(final RelayCommunicationHelper relayCommunicationHelper, final BufferedReader in)
      {
      super(relayCommunicationHelper);
      this.in = in;
      }

   public void execute()
      {
      // make sure the relay communicator is running
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            try
               {
               print("   robot id: ");
               final String robotId = in.readLine();

               if (robotId == null)
                  {
                  println("Robot id was null, aborting.");
                  }
               else
                  {
                  try
                     {
                     final ObjectPrx objectPrx = getRelayCommunicator().connectToPeer(robotId);
                     if (objectPrx != null)
                        {
                        setSimpleCommandLineQwerkPrx(SimpleCommandLineQwerkPrxHelper.checkedCast(objectPrx));
                        if (getSimpleCommandLineQwerkPrx() != null)
                           {
                           println("   Connection successful! (" + Util.identityToString(getSimpleCommandLineQwerkPrx().ice_getIdentity()) + ")");
                           }
                        else
                           {
                           println("   Connection failed!");
                           }
                        }
                     else
                        {
                        LOG.error("connectToPeer() returned a null peer.  Bummer.");
                        }
                     }
                  catch (PeerException e)
                     {
                     LOG.error("Connection failed", e);
                     }
                  }
               }
            catch (IOException ex)
               {
               ex.printStackTrace();
               }
            }
         else
            {
            println("You must log in to the relay before you can connect to a peer.");
            }
         }
      else
         {
         println("Relay communicator is not running, so you can't log in.");
         }
      }
   }
