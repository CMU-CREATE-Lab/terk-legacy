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
public class ListAvailablePeersAction extends RelayAction
   {
   private static final Logger LOG = Logger.getLogger(ListAvailablePeersAction.class);

   private static final Comparator<PeerIdentifier> PEER_IDENTIFIER_COMPARATOR = new PeerIdentifierComparator();

   public ListAvailablePeersAction(final RelayCommunicationHelper relayCommunicationHelper)
      {
      super(relayCommunicationHelper);
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            final SortedSet<PeerIdentifier> availablePeers = new TreeSet<PeerIdentifier>(PEER_IDENTIFIER_COMPARATOR);
            try
               {
               availablePeers.addAll(getRelayCommunicator().getMyAvailablePeers());
               }
            catch (PeerException e)
               {
               LOG.error("PeerException while trying to get the set of available peers", e);
               }
            if (availablePeers.isEmpty())
               {
               println("   No available peers.");
               }
            else
               {
               for (final PeerIdentifier peerIdentifier : availablePeers)
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
