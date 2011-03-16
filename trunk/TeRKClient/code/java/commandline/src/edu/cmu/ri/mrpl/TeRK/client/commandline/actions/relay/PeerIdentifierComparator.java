package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import java.util.Comparator;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;

/**
 * A {@link Comparator} that sorts {@link PeerIdentifier} objects by {@link PeerIdentifier#userId userId}.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
class PeerIdentifierComparator implements Comparator<PeerIdentifier>
   {
   public int compare(final PeerIdentifier o1, final PeerIdentifier o2)
      {
      return o1.userId.compareTo(o2.userId);
      }
   }
