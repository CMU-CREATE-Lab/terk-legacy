package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ListCurrentlyConnectedPeersAction extends RelayAction
   {
   private static final Logger LOG = Logger.getLogger(ListCurrentlyConnectedPeersAction.class);

   private static final Comparator<PeerIdentifier> PEER_IDENTIFIER_COMPARATOR = new PeerIdentifierComparator();

   public ListCurrentlyConnectedPeersAction(final RelayCommunicationHelper relayCommunicationHelper)
      {
      super(relayCommunicationHelper);
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            final SortedSet<PeerIdentifier> currentPeers = new TreeSet<PeerIdentifier>(PEER_IDENTIFIER_COMPARATOR);
            try
               {
               currentPeers.addAll(getRelayCommunicator().getConnectedPeers());
               }
            catch (PeerException e)
               {
               LOG.error("PeerException while trying to get the set of connected peers", e);
               }
            if (currentPeers.isEmpty())
               {
               println("   No connected peers.");
               }
            else
               {
               for (final PeerIdentifier peerIdentifier : currentPeers)
                  {
                  println("   [" + peerIdentifier.userId + "|" + peerIdentifier.firstName + "|" + peerIdentifier.lastName + "]");
                  }
               }
            }
         else
            {
            println("You must log in to the relay before you can list available peers.");
            }
         }
      else
         {
         println("Relay communicator is not running, so you can't log out.");
         }
      }
   }
