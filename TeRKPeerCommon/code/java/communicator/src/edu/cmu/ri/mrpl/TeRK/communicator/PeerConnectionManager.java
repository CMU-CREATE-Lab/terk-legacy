package edu.cmu.ri.mrpl.TeRK.communicator;

import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface PeerConnectionManager
   {
   ObjectPrx connectToPeer(final String peerIdentifier) throws PeerAccessException,
                                                               PeerUnavailableException,
                                                               PeerConnectionFailedException,
                                                               DuplicateConnectionException;

   Set<PeerIdentifier> getConnectedPeers() throws PeerException;

   void disconnectFromPeer(final String peerIdentifier);

   void disconnectFromPeers();
   }