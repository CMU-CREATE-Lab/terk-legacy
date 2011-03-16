package edu.cmu.ri.mrpl.TeRK.communicator.relay;

import java.util.Set;
import edu.cmu.ri.mrpl.TeRK.communicator.PeerConnectionManager;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RelayPeerConnectionManager extends PeerConnectionManager
   {
   Set<PeerIdentifier> getMyPeers() throws PeerException;

   Set<PeerIdentifier> getMyAvailablePeers() throws PeerException;

   Set<PeerIdentifier> getMyUnavailablePeers() throws PeerException;
   }