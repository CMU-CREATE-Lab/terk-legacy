package edu.cmu.ri.mrpl.TeRK.communicator.relay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Glacier2.CannotCreateSessionException;
import Glacier2.PermissionDeniedException;
import Glacier2.RouterPrxHelper;
import Glacier2.SessionNotExistException;
import Ice.ConnectionLostException;
import Ice.Identity;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.communicator.AbstractTerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.ice.communicator.GlacierIceCommunicator;
import edu.cmu.ri.mrpl.ice.communicator.IceCommunicator;
import edu.cmu.ri.mrpl.ice.session.IceSessionPinger;
import edu.cmu.ri.mrpl.peer.ConnectionEventDistributor;
import edu.cmu.ri.mrpl.peer.ConnectionEventDistributorHelper;
import edu.cmu.ri.mrpl.peer.ConnectionEventHandlerPrx;
import edu.cmu.ri.mrpl.peer.ConnectionEventListener;
import edu.cmu.ri.mrpl.peer.ConnectionEventSource;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventDistributorHelper;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.peer.UserConnectionEventListener;
import edu.cmu.ri.mrpl.peer.UserSessionPrx;
import edu.cmu.ri.mrpl.peer.UserSessionPrxHelper;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import edu.cmu.ri.mrpl.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RelayCommunicator extends AbstractTerkCommunicator implements ConnectionEventSource,
                                                                                 ConnectionEventDistributor,
                                                                                 RelaySessionManager,
                                                                                 RelayPeerConnectionManager
   {
   private static final Logger LOG = Logger.getLogger(RelayCommunicator.class);

   private final IceCommunicator glacierIceCommunicator;
   private final Set<UserConnectionEventListener> userConnectionEventListeners = new HashSet<UserConnectionEventListener>();
   private final Set<PeerConnectionEventListener> peerConnectionEventListeners = new HashSet<PeerConnectionEventListener>();
   private final ExecutorService executorPool = Executors.newCachedThreadPool(new DaemonThreadFactory("RelayCommunicator.executorPool"));
   private UserSessionPrx userSessionPrx;
   private IceSessionPinger iceSessionPinger;
   private final ConnectionEventDistributorHelper connectionEventDistributorHelper;
   private final RouterPrxHelper glacierRouter;
   private String userId = "";

   private abstract static class GetPeersStrategy
      {
      private final String name;

      protected GetPeersStrategy(final String name)
         {
         this.name = name;
         }

      protected abstract List<PeerIdentifier> getPeers() throws PeerException;

      protected final String getName()
         {
         return name;
         }
      }

   private final GetPeersStrategy getMyPeersStrategy =
         new GetPeersStrategy("getMyPeers()")
         {
         protected List<PeerIdentifier> getPeers() throws PeerException
            {
            return userSessionPrx.getMyPeers();
            }
         };

   private final GetPeersStrategy getAvailablePeersStrategy =
         new GetPeersStrategy("getMyAvailablePeers()")
         {
         protected List<PeerIdentifier> getPeers() throws PeerException
            {
            return userSessionPrx.getMyAvailablePeers();
            }
         };

   private final GetPeersStrategy getMyUnavailablePeersStrategy =
         new GetPeersStrategy("getMyUnavailablePeers()")
         {
         protected List<PeerIdentifier> getPeers() throws PeerException
            {
            return userSessionPrx.getMyUnavailablePeers();
            }
         };

   /**
    * Creates a RelayCommunicator in a worker thread and fires events to the given
    * {@link TerkCommunicatorCreationEventListener}.  This is useful for GUI applications which don't want to wait for
    * the RelayCommunicator to be created, but which also want to be notified of creation and initialization events and
    * any errors which might occur.
    */
   public static void createAsynchronously(final String name,
                                           final String configFileClasspath,
                                           final String objectAdapterName,
                                           final TerkCommunicatorCreationEventListener listener)
      {
      final Collection<TerkCommunicatorCreationEventListener> listeners = new ArrayList<TerkCommunicatorCreationEventListener>(1);
      listeners.add(listener);

      createAsynchronously(name,
                           configFileClasspath,
                           objectAdapterName,
                           listeners);
      }

   /**
    * Creates a RelayCommunicator in a worker thread and fires events to the given
    * {@link TerkCommunicatorCreationEventListener}s.  This is useful for GUI applications which don't want to wait for
    * the RelayCommunicator to be created, but which also want to be notified of creation and initialization events and
    * any errors which might occur.
    */
   public static void createAsynchronously(final String name,
                                           final String configFileClasspath,
                                           final String objectAdapterName,
                                           final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      createTerkCommunicatorAsynchronously(
            new Callable<TerkCommunicator>()
            {
            public TerkCommunicator call() throws Exception
               {
               return new RelayCommunicator(name, configFileClasspath, objectAdapterName);
               }
            },
            listeners);
      }

   /**
    * Creates a RelayCommunicator for use with Glacier2 and the TeRK relay server.  Throws an
    * {@link IllegalStateException} if the configured router is not a Glacier2 router.  The caller is responsible for
    * calling {@link #waitForShutdown()}.
    */
   public RelayCommunicator(final String name, final String configFileClasspath, final String objectAdapterName)
      {
      super(objectAdapterName);
      glacierIceCommunicator = new GlacierIceCommunicator(name + " (relay)", configFileClasspath);
      glacierRouter = (RouterPrxHelper)glacierIceCommunicator.getRouter();

      connectionEventDistributorHelper =
            new ConnectionEventDistributorHelper()
            {
            public void publishRelayLoginEvent()
               {
               fireRelayLoginEvent();
               }

            public void publishFailedRelayLoginEvent()
               {
               fireFailedRelayLoginEvent();
               }

            public void publishRelayRegistrationEvent()
               {
               fireRelayRegistrationEvent();
               }

            public void publishRelayLogoutEvent()
               {
               fireRelayLogoutEvent();
               }

            public void publishForcedLogoutNotificationEvent()
               {
               // log the user out, but don't send the logout event since we're going to send the forced logout event
               logout(false);

               // fire the notification event
               fireForcedLogoutNotificationEvent();
               }

            public void publishPeerConnectedEvent(final String peerUserId, final PeerAccessLevel accessLevel, final ObjectPrx peerProxy)
               {
               firePeerConnectedEvent(peerUserId, accessLevel, peerProxy);
               }

            public void publishPeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel accessLevel)
               {
               firePeerConnectedNoProxyEvent(peerUserId, accessLevel);
               }

            public void publishPeerDisconnectedEvent(final String peerUserId)
               {
               firePeerDisconnectedEvent(peerUserId);
               }

            public void publishPeerConnectionFailedEvent(final String peerUserId)
               {
               firePeerConnectionFailedEvent(peerUserId);
               }
            };
      }

   public final void addConnectionEventListener(final ConnectionEventListener listener)
      {
      if (listener != null)
         {
         userConnectionEventListeners.add(listener);
         peerConnectionEventListeners.add(listener);
         }
      }

   public final void removeConnectionEventListener(final ConnectionEventListener listener)
      {
      if (listener != null)
         {
         userConnectionEventListeners.remove(listener);
         peerConnectionEventListeners.remove(listener);
         }
      }

   public final void addPeerConnectionEventListener(final PeerConnectionEventListener listener)
      {
      if (listener != null)
         {
         peerConnectionEventListeners.add(listener);
         }
      }

   public final void removePeerConnectionEventListener(final PeerConnectionEventListener listener)
      {
      if (listener != null)
         {
         peerConnectionEventListeners.remove(listener);
         }
      }

   public void addUserConnectionEventListener(final UserConnectionEventListener listener)
      {
      if (listener != null)
         {
         userConnectionEventListeners.add(listener);
         }
      }

   public void removeUserConnectionEventListener(final UserConnectionEventListener listener)
      {
      if (listener != null)
         {
         userConnectionEventListeners.remove(listener);
         }
      }

   public final void fireRelayLoginEvent()
      {
      if (!userConnectionEventListeners.isEmpty())
         {
         for (final UserConnectionEventListener listener : userConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handleRelayLoginEvent();
                     }
                  });
            }
         }
      }

   public final void fireFailedRelayLoginEvent()
      {
      if (!userConnectionEventListeners.isEmpty())
         {
         for (final UserConnectionEventListener listener : userConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handleFailedRelayLoginEvent();
                     }
                  });
            }
         }
      }

   public void fireRelayRegistrationEvent()
      {
      if (!userConnectionEventListeners.isEmpty())
         {
         for (final UserConnectionEventListener listener : userConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handleRelayRegistrationEvent();
                     }
                  });
            }
         }
      }

   public final void fireRelayLogoutEvent()
      {
      if (!userConnectionEventListeners.isEmpty())
         {
         for (final UserConnectionEventListener listener : userConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handleRelayLogoutEvent();
                     }
                  });
            }
         }
      }

   public final void fireForcedLogoutNotificationEvent()
      {
      if (!userConnectionEventListeners.isEmpty())
         {
         for (final UserConnectionEventListener listener : userConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handleForcedLogoutNotificationEvent();
                     }
                  });
            }
         }
      }

   public final void firePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
      {
      if (!peerConnectionEventListeners.isEmpty())
         {
         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handlePeerConnectedEvent(peerUserId, peerAccessLevel, peerObjectProxy);
                     }
                  });
            }
         }
      }

   public final void firePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
      {
      if (!peerConnectionEventListeners.isEmpty())
         {
         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handlePeerConnectedNoProxyEvent(peerUserId, peerAccessLevel);
                     }
                  });
            }
         }
      }

   public final void firePeerDisconnectedEvent(final String peerUserId)
      {
      if (!peerConnectionEventListeners.isEmpty())
         {
         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handlePeerDisconnectedEvent(peerUserId);
                     }
                  });
            }
         }
      }

   public void firePeerConnectionFailedEvent(final String peerUserId)
      {
      if (!peerConnectionEventListeners.isEmpty())
         {
         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            // run each of these in a separate thread
            executorPool.execute(
                  new Runnable()
                  {
                  public void run()
                     {
                     listener.handlePeerConnectionFailedEvent(peerUserId);
                     }
                  });
            }
         }
      }

   public final ConnectionEventDistributorHelper getConnectionEventDistributorHelper()
      {
      return connectionEventDistributorHelper;
      }

   public PeerConnectionEventDistributorHelper getPeerConnectionEventDistributorHelper()
      {
      return connectionEventDistributorHelper;
      }

   public final boolean isLoggedIn()
      {
      return userSessionPrx != null;
      }

   public final boolean login(final String userId, final String password)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RelayCommunicator.login(" + userId + ")");
         }

      try
         {
         userSessionPrx = UserSessionPrxHelper.uncheckedCast(glacierRouter.createSession(userId, password));
         this.userId = userId;

         // start the session pinger
         startPinger();

         fireRelayLoginEvent();

         return true;
         }
      catch (CannotCreateSessionException e)
         {
         LOG.error("CannotCreateSessionException while user " + userId + " tried to log in to the relay", e);
         fireFailedRelayLoginEvent();
         }
      catch (PermissionDeniedException e)
         {
         LOG.error("PermissionDeniedException while user " + userId + " tried to log in to the relay", e);
         fireFailedRelayLoginEvent();
         }

      return false;
      }

   protected IceCommunicator getIceCommunicator()
      {
      return glacierIceCommunicator;
      }

   /**
    * Returns the specified {@link ObjectPrx proxy} for the specified peer.  In this implementation, the
    * <code>peerIdentifier</code> and the <code>proxyIdentity</code> specified by the interface are, respectively,
    * the peer's user id and the private proxy identity that the peer used to register the proxy with the relay.
    *
    * @param peerIdentifier the peer's user id
    * @param proxyIdentity the private proxy identity that the peer used to register the proxy with the relay
    */
   public ObjectPrx getPeerProxy(final String peerIdentifier, final Identity proxyIdentity) throws PeerAccessException, InvalidIdentityException
      {
      if (userSessionPrx != null)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RelayCommunicator.getPeerProxy(" + peerIdentifier + ", " + Util.identityToString(proxyIdentity) + ")");
            }
         return userSessionPrx.getPeerProxy(peerIdentifier, proxyIdentity);
         }
      else
         {
         LOG.trace("RelayCommunicator.getPeerProxy() returning null since the session proxy is null");
         return null;
         }
      }

   /**
    * Returns a collection of {@link ObjectPrx proxies} for the specified peer, where the proxies returned are those
    * specified by the given {@link Set} of private proxy identities.  In this implementation, the
    * <code>peerIdentifier</code> is the peer's user id and the <code>proxyIdentities</code> is a {@link Set} of
    * private proxy identity that the peer used to register the proxy with the relay.
    *
    * @param peerIdentifier the peer's user id
    * @param proxyIdentities a {@link Set} of private proxy identities that the peer used to register the proxies with the relay
    */
   public Map<Identity, ObjectPrx> getPeerProxies(final String peerIdentifier, final Set<Identity> proxyIdentities) throws PeerAccessException, InvalidIdentityException
      {
      if (userSessionPrx != null)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RelayCommunicator.getPeerProxies(" + peerIdentifier + ") is retrieving proxies for:");
            for (final Identity identity : proxyIdentities)
               {
               LOG.debug("   [" + Util.identityToString(identity) + "]");
               }
            }
         final Map<Identity, ObjectPrx> peerProxies = userSessionPrx.getPeerProxies(peerIdentifier, new ArrayList<Identity>(proxyIdentities));
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RelayCommunicator.getPeerProxies() got [" + peerProxies.size() + "] proxies back from the relay:");
            for (final Identity identity : peerProxies.keySet())
               {
               LOG.debug("   [" + Util.identityToString(identity) + "] --> [" + peerProxies.get(identity).ice_toString() + "]");
               }
            }
         return peerProxies;
         }
      else
         {
         LOG.trace("RelayCommunicator.getPeerProxies() returning null since the session proxy is null");
         return null;
         }
      }

   public final void registerCallbacks(final ObjectPrx selfCallbackProxy, final ConnectionEventHandlerPrx connectionEventHandlerProxy) throws RegistrationException
      {
      if (userSessionPrx != null)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RelayCommunicator.registerCallbacks() is registering proxy: " + Util.identityToString(selfCallbackProxy.ice_getIdentity()));
            }
         userSessionPrx.registerCallbacks(selfCallbackProxy, connectionEventHandlerProxy);

         fireRelayRegistrationEvent();
         }
      else
         {
         LOG.trace("registerCallbacks() did nothing since the session proxy is null");
         }
      }

   public void registerProxy(final ObjectPrx proxy) throws RegistrationException
      {
      if (userSessionPrx != null)
         {
         if (proxy != null)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("RelayCommunicator.registerProxy() is registering proxy: " + Util.identityToString(proxy.ice_getIdentity()));
               }
            userSessionPrx.registerProxy(proxy);
            }
         else
            {
            LOG.trace("registerProxy() did nothing since the proxy to register is null");
            }
         }
      else
         {
         LOG.trace("registerProxy() did nothing since the session proxy is null");
         }
      }

   public void registerProxies(final Set<ObjectPrx> proxies) throws RegistrationException
      {
      LOG.trace("RelayCommunicator.registerProxies()");
      if (userSessionPrx != null)
         {
         if ((proxies != null) && (!proxies.isEmpty()))
            {
            userSessionPrx.registerProxies(new ArrayList<ObjectPrx>(proxies));
            }
         else
            {
            LOG.trace("registerProxies() did nothing since the set of proxies to register is null or empty");
            }
         }
      else
         {
         LOG.trace("registerProxies() did nothing since the session proxy is null");
         }
      }

   public final Set<PeerIdentifier> getMyPeers() throws PeerException
      {
      return getPeers(getMyPeersStrategy);
      }

   public final Set<PeerIdentifier> getMyAvailablePeers() throws PeerException
      {
      return getPeers(getAvailablePeersStrategy);
      }

   public final Set<PeerIdentifier> getMyUnavailablePeers() throws PeerException
      {
      return getPeers(getMyUnavailablePeersStrategy);
      }

   private Set<PeerIdentifier> getPeers(final GetPeersStrategy strategy) throws PeerException
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("RelayCommunicator." + strategy.getName());
         }
      SwingUtils.warnIfEventDispatchThread(strategy.getName());

      if (userSessionPrx != null)
         {
         final HashSet<PeerIdentifier> peers = new HashSet<PeerIdentifier>();
         final List<PeerIdentifier> peersList = strategy.getPeers();
         if (peersList != null)
            {
            peers.addAll(peersList);
            }
         if (LOG.isDebugEnabled())
            {
            LOG.debug(strategy.getName() + " is returning " + peers.size() + " peers.");
            }
         return peers;
         }
      else
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace(strategy.getName() + " returning empty Set since the session proxy is null");
            }
         }
      return new HashSet<PeerIdentifier>();
      }

   public final ObjectPrx connectToPeer(final String peerUserId) throws PeerAccessException,
                                                                        PeerUnavailableException,
                                                                        PeerConnectionFailedException,
                                                                        DuplicateConnectionException
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("RelayCommunicator.connectToPeer(" + peerUserId + ")");
         }
      SwingUtils.warnIfEventDispatchThread("connectToPeer()");

      if (userSessionPrx != null)
         {
         try
            {
            return userSessionPrx.connectToPeer(peerUserId);
            }
         catch (DuplicateConnectionException e)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("Firing peer connection failed event since a DuplicateConnectionException occurred when trying to connect to peer [" + peerUserId + "]", e);
               }
            firePeerConnectionFailedEvent(peerUserId);
            throw e;
            }
         catch (PeerAccessException e)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("Firing peer connection failed event since a PeerAccessException occurred when trying to connect to peer [" + peerUserId + "]", e);
               }
            firePeerConnectionFailedEvent(peerUserId);
            throw e;
            }
         catch (PeerConnectionFailedException e)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("Firing peer connection failed event since a PeerConnectionFailedException occurred when trying to connect to peer [" + peerUserId + "]", e);
               }
            firePeerConnectionFailedEvent(peerUserId);
            throw e;
            }
         catch (PeerUnavailableException e)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("Firing peer connection failed event since a PeerUnavailableException occurred when trying to connect to peer [" + peerUserId + "]", e);
               }
            firePeerConnectionFailedEvent(peerUserId);
            throw e;
            }
         }
      else
         {
         LOG.trace("connectToPeer() returning null since the session proxy is null");
         }
      return null;
      }

   public final Set<PeerIdentifier> getConnectedPeers() throws PeerException
      {
      LOG.trace("RelayCommunicator.getConnectedPeers()");
      SwingUtils.warnIfEventDispatchThread("getConnectedPeers()");

      if (userSessionPrx != null)
         {
         final HashSet<PeerIdentifier> peers = new HashSet<PeerIdentifier>();
         final List<PeerIdentifier> peersList = userSessionPrx.getConnectedPeers();
         if (peersList != null)
            {
            peers.addAll(peersList);
            }
         if (LOG.isDebugEnabled())
            {
            LOG.debug("   getConnectedPeers() is returning " + peers.size() + " peers.");
            }
         return peers;
         }
      else
         {
         LOG.trace("getConnectedPeers() returning empty Set since the session proxy is null");
         }
      return new HashSet<PeerIdentifier>();
      }

   public final void disconnectFromPeer(final String peerUserId)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("RelayCommunicator.disconnectFromPeer(" + peerUserId + ")");
         }
      SwingUtils.warnIfEventDispatchThread("disconnectFromPeer()");

      if (userSessionPrx != null)
         {
         userSessionPrx.disconnectFromPeer(peerUserId);
         }
      else
         {
         LOG.trace("disconnectFromPeer() did nothing since the session proxy is null");
         }
      }

   public final void disconnectFromPeers()
      {
      LOG.trace("RelayCommunicator.disconnectFromPeers()");
      SwingUtils.warnIfEventDispatchThread("disconnectFromPeers()");

      if (userSessionPrx != null)
         {
         userSessionPrx.disconnectFromPeers();
         }
      else
         {
         LOG.trace("disconnectFromPeers() did nothing since the session proxy is null");
         }
      }

   public final void logout()
      {
      logout(true);
      }

   private void logout(final boolean willFireRelayLogoutEvent)
      {
      LOG.trace("RelayCommunicator.logout()");

      try
         {
         userSessionPrx = null;
         userId = null;
         stopPinger();
         try
            {
            glacierRouter.destroySession();
            }
         catch (ConnectionLostException cle)
            {
            // Ignore this, since it's expected.  The exception occurs because the router forcefully closes
            // the client's connection to indicate that the session is no longer valid.  For more information,
            // see the Ice 3.1.1 manual, section 41.3, page 1389.
            }
         if (willFireRelayLogoutEvent)
            {
            fireRelayLogoutEvent();
            }
         }
      catch (SessionNotExistException e)
         {
         // log, but otherwise ignore
         LOG.trace("SessionNotExistException while trying to destroy the session");
         }
      catch (LocalException e)
         {
         LOG.trace("LocalException while trying to destroy the session.  Rethrowing it.", e);
         throw e;
         }
      }

   /** Prepares for {@link #shutdown()} by calling {@link #disconnectFromPeers()} and {@link #logout()}. */
   protected final void prepareForShutdown()
      {
      LOG.trace("RelayCommunicator.prepareForShutdown()");
      SwingUtils.warnIfEventDispatchThread("prepareForShutdown()");

      try
         {
         LOG.trace("RelayCommunicator: disconnecting from peers...");
         disconnectFromPeers();
         LOG.trace("RelayCommunicator: successfully disconnected from peers!");
         }
      catch (Exception e)
         {
         // log, but otherwise ignore
         LOG.error("Exception while trying to disconnect from peers", e);
         }

      LOG.trace("RelayCommunicator: logging out from relay...");
      logout();
      LOG.trace("RelayCommunicator: successfully logged out from relay!");
      }

   private void startPinger()
      {
      // stop any existing pinger
      stopPinger();

      LOG.trace("Creating new session pinger");
      // create a new pinger and start it up (todo: figure out a way to store the pinger sleep time--IceGrid?)
      iceSessionPinger = new IceSessionPinger(50, userSessionPrx);
      iceSessionPinger.start();
      }

   private void stopPinger()
      {
      // stop any existing pinger
      if (iceSessionPinger != null)
         {
         iceSessionPinger.stop();
         iceSessionPinger = null;
         LOG.trace("Stopped session pinger");
         }
      }

   protected void executeBeforeCommunicatorShutdown()
      {
      stopPinger();
      }

   /**
    * If {@link #isLoggedIn()} would return <code>true</code>, then this method returns the user ID; otherwise, this
    * method returns <code>null</code>.
    */
   public String getUserId()
      {
      return userId;
      }
   }
