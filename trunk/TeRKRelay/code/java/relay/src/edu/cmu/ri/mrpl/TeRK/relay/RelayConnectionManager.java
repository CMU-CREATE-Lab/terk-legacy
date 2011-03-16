package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import Ice.Identity;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.peer.ConnectionEventSource;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;
import edu.cmu.ri.mrpl.peer.RegistrationException;

/**
 * <p>
 * <code>RelayConnectionManager</code> defines the interface for managing connections between peers.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface RelayConnectionManager
   {
   void registerUser(final String userId, final Identity sessionIdentity, final ObjectPrx mainCallbackProxy, final ConnectionEventSource connectionEventSource) throws RegistrationException;

   void registerProxy(final String userId, final ObjectPrx proxy) throws RegistrationException;

   void registerProxies(final String userId, final Collection<ObjectPrx> proxies) throws RegistrationException;

   ObjectPrx getPeerProxy(final String userId, final String peerUserId, final Identity privateProxyIdentity) throws InvalidIdentityException, PeerAccessException;

   Map<Identity, ObjectPrx> getPeerProxies(final String userId, final String peerUserId, final Collection<Identity> privateProxyIdentities) throws InvalidIdentityException, PeerAccessException;

   Set<PeerIdentifier> getMyPeers(final String userId) throws PeerException;

   Set<PeerIdentifier> getMyAvailablePeers(final String userId) throws PeerException;

   Set<PeerIdentifier> getMyUnavailablePeers(final String userId) throws PeerException;

   ObjectPrx connectToPeer(final String userId, final String peerUserId) throws PeerAccessException, PeerUnavailableException, PeerConnectionFailedException, DuplicateConnectionException;

   Set<PeerIdentifier> getConnectedPeers(final String userId) throws PeerException;

   void disconnectFromPeer(final String userId, final String peerUserId);

   void disconnectFromPeers(final String userId);

   void unregister(final String userId, final Identity sessionIdentity);

   void forceUnregister(final String userId);
   }