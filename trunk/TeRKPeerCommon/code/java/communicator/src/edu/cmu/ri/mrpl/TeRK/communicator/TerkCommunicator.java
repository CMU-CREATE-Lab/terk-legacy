package edu.cmu.ri.mrpl.TeRK.communicator;

import java.util.Map;
import java.util.Set;
import Ice.Communicator;
import Ice.Identity;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventDistributor;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventDistributorHelper;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventSource;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkCommunicator extends PeerConnectionManager,
                                          PeerConnectionEventDistributor,
                                          PeerConnectionEventSource
   {
   String MAIN_SERVANT_PROXY_IDENTITY_NAME = "::TeRK::TerkUser";

   /** Returns the Object Adapter name used when creating and destroying servants. */
   String getObjectAdapterName();

   PeerConnectionEventDistributorHelper getPeerConnectionEventDistributorHelper();

   /** Blocks and waits for the underlying Ice {@link Communicator} to shut down. */
   void waitForShutdown();

   /**
    * Creates a proxy for the given {@link ObjectImpl servant} using the object adapter name returned by
    * {@link #getObjectAdapterName()}.  The identity name used is chosen based on the the servant's Ice id.  If the
    * servant is a TerkUser (i.e. <code>servant.ice_isA("::TeRK::TerkUser") == true</code>), then the identity name
    * used is "::TeRK::TerkUser"; otherwise, the identity name is obtained via {@link ObjectImpl#ice_id()}.  This scheme
    * ensures that proxies of main servants are always created with the proper, standard identity name, so that
    * direct-connect peers can obtain proxies knowing only the host.
    *
    * @see #getObjectAdapterName()
    */
   ObjectPrx createServantProxy(final ObjectImpl servant);

   /**
    * Removes the given {@link ObjectPrx servantProxy} from the adapter specified by the name returned from
    * {@link #getObjectAdapterName()}.
    *
    * @see #getObjectAdapterName()
    */
   void destroyServantProxy(final ObjectPrx servantProxy);

   /**
    * Returns the specified {@link ObjectPrx proxy} for the specified peer, where the proxy returned is specified by the
    * given of proxy identity.  It is up to the particular implementation to specifically define the format for the
    * <code>peerIdentifier</code> and <code>proxyIdentity</code>.
    */
   ObjectPrx getPeerProxy(final String peerIdentifier, final Identity proxyIdentity) throws PeerAccessException, InvalidIdentityException;

   /**
    * Returns a collection of {@link ObjectPrx proxies} for the specified peer, where the proxies returned are those
    * specified by the given {@link Set} of proxy identities.  It is up to the particular implementation to specifically
    * define the format for the <code>peerIdentifier</code> and <code>proxyIdentities</code>.
    */
   Map<Identity, ObjectPrx> getPeerProxies(final String peerIdentifier, final Set<Identity> proxyIdentities) throws PeerAccessException, InvalidIdentityException;

   /** Shuts down the underlying Ice {@link Communicator}. */
   void shutdown();
   }