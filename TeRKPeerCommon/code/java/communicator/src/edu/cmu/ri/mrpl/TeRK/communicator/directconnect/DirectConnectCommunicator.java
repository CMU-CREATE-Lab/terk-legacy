package edu.cmu.ri.mrpl.TeRK.communicator.directconnect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import Ice.Identity;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.AbstractTerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.ice.communicator.DefaultIceCommunicator;
import edu.cmu.ri.mrpl.ice.communicator.IceCommunicator;
import edu.cmu.ri.mrpl.ice.util.HostInformation;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventDistributorHelper;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventSource;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.util.net.HostAndPort;
import edu.cmu.ri.mrpl.util.thread.DaemonThreadFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DirectConnectCommunicator extends AbstractTerkCommunicator implements PeerConnectionEventSource
   {
   private static final Logger LOG = Logger.getLogger(DirectConnectCommunicator.class);

   public static final int DEFAULT_PORT = 10101;
   private static final String DEFAULT_PORT_STR = String.valueOf(DEFAULT_PORT);
   private static final String PROTOCOL_PROPERTY_KEY = "TeRK.direct-connect.protocol";
   private static final String PORT_PROPERTY_KEY = "TeRK.direct-connect.port";
   private static final String DEFAULT_PROTOCOL = "tcp";
   private static final String CONTEXT_MAP_KEY_PEER_IDENTITY = "__peerProxyIdentity";
   private static final String CONTEXT_MAP_KEY_PEER_USERID = "__peerUserId";
   private static final String CONTEXT_MAP_KEY_IS_DIRECT_CONNECT = "__isDirectConnect";
   private static final String PIPE_SEPARATOR = "|";
   private static final int PING_INTERVAL_IN_SECONDS = 5;
   private static final int PINGER_THREAD_POOL_SIZE = 10;

   /**
    * Creates a DirectConnectCommunicator in a worker thread and fires events to the given
    * {@link TerkCommunicatorCreationEventListener}s.  This is useful for GUI applications which don't want to wait for
    * the DirectConnectCommunicator to be created, but which also want to be notified of creation and initialization
    * events and any errors which might occur.
    */
   public static void createAsynchronously(final String name,
                                           final String configFileClasspath,
                                           final String objectAdapterName,
                                           final Collection<TerkCommunicatorCreationEventListener> communicatorCreationEventListeners,
                                           final ServantFactory servantFactory)
      {
      // the listeners collectoin is a list, so we can ensure that our listener to create the servants gets executed first
      final List<TerkCommunicatorCreationEventListener> listeners = new ArrayList<TerkCommunicatorCreationEventListener>();

      // add a listener which will cause the creation of the servants
      listeners.add(
            new TerkCommunicatorCreationEventAdapater()
            {
            public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
               {
               final Servants servants = servantFactory.createServants(terkCommunicator);
               ((DirectConnectCommunicator)terkCommunicator).setServants(servants);
               }
            });

      // add the remaining listeners
      if (communicatorCreationEventListeners != null)
         {
         listeners.addAll(communicatorCreationEventListeners);
         }

      createTerkCommunicatorAsynchronously(
            new Callable<TerkCommunicator>()
            {
            public TerkCommunicator call() throws Exception
               {
               return new DirectConnectCommunicator(name, configFileClasspath, objectAdapterName);
               }
            },
            listeners);
      }

   private final String uuid = Util.generateUUID();
   private final DefaultIceCommunicator defaultIceCommunicator;
   private final List<PeerConnectionEventListener> peerConnectionEventListeners = new ArrayList<PeerConnectionEventListener>();
   private final PeerConnectionEventDistributorHelper peerConnectionEventDistributorHelper = new MyPeerConnectionEventDistributorHelper();
   private final Map<HostAndPort, TerkUserPrx> peerHostAndPortToProxyMap = Collections.synchronizedMap(new HashMap<HostAndPort, TerkUserPrx>());
   private final Map<HostAndPort, ScheduledFuture<?>> peerHostAndPortToPingerMap = Collections.synchronizedMap(new HashMap<HostAndPort, ScheduledFuture<?>>());
   private final ExecutorService executorPool = Executors.newCachedThreadPool(new DaemonThreadFactory("DirectConnectCommunicator.executorPool"));
   private final ScheduledExecutorService pingSchedulingService = Executors.newScheduledThreadPool(PINGER_THREAD_POOL_SIZE, new DaemonThreadFactory("DirectConnectCommunicator.pingSchedulingService"));
   private Servants servants;

   /**
    * Creates a DirectConnectCommunicator.  The caller is responsible for calling {@link #waitForShutdown()}.
    */
   public DirectConnectCommunicator(final String name,
                                    final String configFileClasspath,
                                    final String objectAdapterName)
      {
      super(objectAdapterName);
      defaultIceCommunicator = new DefaultIceCommunicator(name + " (direct-connect)", configFileClasspath);
      addPeerConnectionEventListener(new PingHandlingPeerConnectionEventListener());
      }

   public Servants getServants()
      {
      return servants;
      }

   private void setServants(final Servants servants)
      {
      this.servants = servants;
      }

   protected IceCommunicator getIceCommunicator()
      {
      return defaultIceCommunicator;
      }

   public void addPeerConnectionEventListener(final PeerConnectionEventListener listener)
      {
      if (listener != null)
         {
         peerConnectionEventListeners.add(listener);
         }
      }

   public void removePeerConnectionEventListener(final PeerConnectionEventListener listener)
      {
      if (listener != null)
         {
         peerConnectionEventListeners.remove(listener);
         }
      }

   public PeerConnectionEventDistributorHelper getPeerConnectionEventDistributorHelper()
      {
      return peerConnectionEventDistributorHelper;
      }

   public void firePeerConnectedEvent(final String peerIdentifier, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.firePeerConnectedEvent(" + peerIdentifier + "," + peerAccessLevel + "," + peerObjectProxy + ")");
         }

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
                     listener.handlePeerConnectedEvent(peerIdentifier, peerAccessLevel, peerObjectProxy);
                     }
                  });
            }
         }
      }

   public void firePeerConnectedNoProxyEvent(final String peerIdentifier, final PeerAccessLevel peerAccessLevel)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.firePeerConnectedNoProxyEvent(" + peerIdentifier + "," + peerAccessLevel + ")");
         }

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
                     listener.handlePeerConnectedNoProxyEvent(peerIdentifier, peerAccessLevel);
                     }
                  });
            }
         }
      }

   public void firePeerDisconnectedEvent(final String peerIdentifier)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.firePeerDisconnectedEvent(" + peerIdentifier + ")");
         }

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
                     listener.handlePeerDisconnectedEvent(peerIdentifier);
                     }
                  });
            }
         }
      }

   public void firePeerConnectionFailedEvent(final String peerIdentifier)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.firePeerConnectionFailedEvent(" + peerIdentifier + ")");
         }

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
                     listener.handlePeerConnectionFailedEvent(peerIdentifier);
                     }
                  });
            }
         }
      }

   private void firePeerConnectedEvent(final HostAndPort peerHostAndPort, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
      {
      firePeerConnectedEvent(peerHostAndPort.getHostAndPort(), peerAccessLevel, peerObjectProxy);
      }

   private void firePeerDisconnectedEvent(final HostAndPort peerHostAndPort)
      {
      firePeerDisconnectedEvent(peerHostAndPort.getHostAndPort());
      }

   private void firePeerConnectionFailedEvent(final HostAndPort peerHostAndPort)
      {
      firePeerConnectionFailedEvent(peerHostAndPort.getHostAndPort());
      }

   /**
    * Creates a direct connection to the given peer identifier, which, in the case of direct connect, is simply the
    * peer's hostname or IP address.
    */
   public ObjectPrx connectToPeer(final String peerIdentifier) throws DuplicateConnectionException, PeerConnectionFailedException
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.connectToPeer(" + peerIdentifier + ")");
         }

      final HostAndPort hostAndPort = createHostAndPort(peerIdentifier);

      if (peerHostAndPortToProxyMap.containsKey(hostAndPort))
         {
         firePeerConnectionFailedEvent(hostAndPort);
         throw new DuplicateConnectionException("Already connected to peer [" + hostAndPort + "]");
         }

      final TerkUserPrx peerProxy;

      try
         {
         final ObjectPrx objectPrx = getPeerProxyForMainServant(hostAndPort);

         if (objectPrx != null)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("DirectConnectCommunicator.connectToPeer() connected to peer [" + Util.identityToString(objectPrx.ice_getIdentity()) + PIPE_SEPARATOR + objectPrx.ice_toString() + "]");
               }
            peerProxy = TerkUserPrxHelper.uncheckedCast(objectPrx);

            registerPeerProxy(hostAndPort, peerProxy);
            }
         else
            {
            firePeerConnectionFailedEvent(hostAndPort);
            throw new PeerConnectionFailedException("getPeerProxy() returned null peer proxy for peer [" + hostAndPort + "]");
            }
         }
      catch (Exception e)
         {
         firePeerConnectionFailedEvent(hostAndPort);
         throw new PeerConnectionFailedException("Failed to create connection to peer [" + hostAndPort + "]: " + e);
         }

      // enable the peer to use callback methods on me (see the section on setting up bidirectional manually in the Ice manual)
      peerProxy.ice_getConnection().setAdapter(defaultIceCommunicator.getAdapter(getObjectAdapterName()));

      // notify the peer of the connection
      notifyPeerOfConnection(peerProxy);

      // notify "self" of the connection
      firePeerConnectedEvent(hostAndPort, PeerAccessLevel.AccessLevelOwner, peerProxy);

      return peerProxy;
      }

   private HostAndPort createHostAndPort(final String peerHostAndPortStr)
      {
      if (peerHostAndPortStr != null)
         {
         // convert to all lowercase
         final String peerHostAndPortString = peerHostAndPortStr.toLowerCase();

         // First just try to parse the given string represents a valid host and port
         HostAndPort hostAndPort = HostAndPort.createHostAndPort(peerHostAndPortString);

         // the hostAndPort will be null if the parse failed.  If it is, then see whether it's in
         // the format returned by getMyUserId() and, if so, then extract just the host and port part
         if (hostAndPort == null)
            {
            final int pipeIndex = peerHostAndPortString.indexOf(PIPE_SEPARATOR);
            if (pipeIndex >= 0)
               {
               final String hostAndPortStr = peerHostAndPortString.substring(0, pipeIndex);
               hostAndPort = HostAndPort.createHostAndPort(hostAndPortStr);
               }

            if (hostAndPort == null)
               {
               throw new IllegalArgumentException("Cannot extract host and port from [" + peerHostAndPortStr + "] since the format is invalid");
               }
            }

         return hostAndPort;
         }
      throw new NullPointerException("Peer host and port cannot be null!");
      }

   /**
    * Sends a peer connected notification to the directly-connected peer, sending the proxy (if one exists) and an
    * access level of {@link PeerAccessLevel#AccessLevelOwner}. This MUST be called this after
    * {@link #connectToPeer(String)} in order for the peer to be notified of the connection.
    *
    * @see TerkUserPrx#peerConnectedNoProxy(String, PeerAccessLevel)
    * @see TerkUserPrx#peerConnected(String, PeerAccessLevel, ObjectPrx)
    */
   private void notifyPeerOfConnection(final TerkUserPrx peerTerkUserPrx)
      {
      final String myUserId = getMyUserId(peerTerkUserPrx);

      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.notifyPeerOfConnection(): sending peer [" + peerTerkUserPrx + "] my user id: [" + myUserId + "]");
         }

      if (servants == null)
         {
         peerTerkUserPrx.peerConnectedNoProxy(myUserId,
                                              PeerAccessLevel.AccessLevelOwner);
         }
      else
         {
         peerTerkUserPrx.peerConnected(myUserId,
                                       PeerAccessLevel.AccessLevelOwner,
                                       servants.getMainServantProxy());
         }
      }

   public Set<PeerIdentifier> getConnectedPeers()
      {
      final HashSet<PeerIdentifier> peers = new HashSet<PeerIdentifier>();

      synchronized (peerHostAndPortToProxyMap)
         {
         if (!peerHostAndPortToProxyMap.isEmpty())
            {
            for (final HostAndPort peerHostAndPort : peerHostAndPortToProxyMap.keySet())
               {
               peers.add(new PeerIdentifier(peerHostAndPort.getHostAndPort(), "", ""));
               }
            }
         }

      return peers;
      }

   public void disconnectFromPeer(final String peerIdentifier)
      {
      disconnectFromPeer(createHostAndPort(peerIdentifier), true);
      }

   private void disconnectFromPeer(final HostAndPort peerHostAndPort, final boolean willNotifyPeer)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("DirectConnectCommunicator.disconnectFromPeer(" + peerHostAndPort + ")");
         }

      if (peerHostAndPortToProxyMap.containsKey(peerHostAndPort))
         {
         if (willNotifyPeer)
            {
            notifyPeerOfDisconnection(peerHostAndPort);
            }
         unregisterPeerProxy(peerHostAndPort);
         firePeerDisconnectedEvent(peerHostAndPort);
         }
      }

   public void disconnectFromPeers()
      {
      LOG.trace("DirectConnectCommunicator.disconnectFromPeers()");

      synchronized (peerHostAndPortToProxyMap)
         {
         if (!peerHostAndPortToProxyMap.isEmpty())
            {
            for (final Iterator<HostAndPort> iterator = peerHostAndPortToProxyMap.keySet().iterator(); iterator.hasNext();)
               {
               final HostAndPort peerHostAndPort = iterator.next();
               notifyPeerOfDisconnection(peerHostAndPort);
               iterator.remove();
               firePeerDisconnectedEvent(peerHostAndPort);
               }
            }
         }
      }

   /** Prepares for {@link #shutdown()} by calling {@link #disconnectFromPeers()}. */
   protected void prepareForShutdown()
      {
      disconnectFromPeers();
      }

   protected void executeBeforeCommunicatorShutdown()
      {
      try
         {
         LOG.debug("DirectConnectCommunicator.executeBeforeCommunicatorShutdown(): shutting down the executor pool...");
         executorPool.shutdownNow();
         executorPool.awaitTermination(5, TimeUnit.SECONDS);
         LOG.debug("DirectConnectCommunicator.executeBeforeCommunicatorShutdown(): executor pool shutdown complete!");
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while waiting for the executorPool to shut down", e);
         }

      try
         {
         LOG.debug("DirectConnectCommunicator.executeBeforeCommunicatorShutdown(): shutting down the ping scheduler...");
         pingSchedulingService.shutdownNow();
         pingSchedulingService.awaitTermination(PING_INTERVAL_IN_SECONDS * 2, TimeUnit.SECONDS);
         LOG.debug("DirectConnectCommunicator.executeBeforeCommunicatorShutdown(): ping scheduler shutdown complete!");
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while waiting for the pingSchedulingService to shut down", e);
         }
      }

   private void notifyPeerOfDisconnection(final HostAndPort peerHostAndPort)
      {
      final TerkUserPrx terkUserPrx = peerHostAndPortToProxyMap.get(peerHostAndPort);
      if (terkUserPrx != null)
         {
         try
            {
            terkUserPrx.peerDisconnected(getMyUserId(terkUserPrx));
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to notify peer [" + peerHostAndPort + "] of the disconnection", e);
            }
         }
      }

   private String getMyUserId(final ObjectPrx objectPrx)
      {
      final HostInformation hostInformation = extractHostInformation(objectPrx);
      final String prefix = hostInformation != null ? hostInformation.getLocalHostAndPort() : "direct_connect_user";
      return prefix + PIPE_SEPARATOR + uuid;
      }

   public boolean isConnectedToPeer()
      {
      return !peerHostAndPortToProxyMap.isEmpty();
      }

   private ObjectPrx getPeerProxyForMainServant(final HostAndPort hostAndPort)
      {
      return getPeerProxy(hostAndPort, new Identity(TerkCommunicator.MAIN_SERVANT_PROXY_IDENTITY_NAME, ""));
      }

   /**
    * Returns the specified {@link ObjectPrx proxy} for the specified peer.  In this implementation, the
    * <code>peerIdentifier</code> and the <code>proxyIdentity</code> specified by the interface are, respectively,
    * the peer's host (and optional port) and the identity that the peer used when creating the proxy.
    *
    * @param peerIdentifier the peer's host (and optional port)
    * @param proxyIdentity the identity that the peer used when creating the proxy
    */
   public ObjectPrx getPeerProxy(final String peerIdentifier, final Identity proxyIdentity)
      {
      return getPeerProxy(createHostAndPort(peerIdentifier), proxyIdentity);
      }

   private ObjectPrx getPeerProxy(final HostAndPort peerHostAndPort, final Identity proxyIdentity)
      {
      final String protocol = defaultIceCommunicator.getProperty(PROTOCOL_PROPERTY_KEY, DEFAULT_PROTOCOL);
      final String port = peerHostAndPort.getPort() != null ? peerHostAndPort.getPort() : defaultIceCommunicator.getProperty(PORT_PROPERTY_KEY, DEFAULT_PORT_STR);
      final String proxyString = "'" + Util.identityToString(proxyIdentity) + "':" + protocol + " -h " + peerHostAndPort.getHost() + " -p " + port;

      if (LOG.isDebugEnabled())
         {
         LOG.debug("DirectConnectCommunicator.getPeerProxy(" + proxyString + ")");
         }

      final ObjectPrx objectPrx = defaultIceCommunicator.stringToProxy(proxyString);

      // ensure that our custom context entries are passed along with every call on the proxy
      final Map<String, String> context = new HashMap<String, String>();
      context.put(CONTEXT_MAP_KEY_PEER_IDENTITY, Util.identityToString(objectPrx.ice_getIdentity()));
      context.put(CONTEXT_MAP_KEY_PEER_USERID, getMyUserId(objectPrx));
      context.put(CONTEXT_MAP_KEY_IS_DIRECT_CONNECT, "true");
      return objectPrx.ice_context(context);
      }

   /**
    * Returns the specified {@link ObjectPrx proxies} for the specified peer.  In this implementation, the
    * <code>peerIdentifier</code> and the <code>proxyIdentities</code> specified by the interface are, respectively,
    * the peer's host (and optional port) and the identities that the peer used when creating the proxies.  Returns an
    * empty {@link Map} if the given peer identifier is <code>null</code> or if the proxy identity {@link Set} is
    * <code>null</code> or empty.
    *
    * @param peerIdentifier the peer's host (and optional port)
    * @param proxyIdentities the identities that the peer used when creating the proxies
    */
   public Map<Identity, ObjectPrx> getPeerProxies(final String peerIdentifier, final Set<Identity> proxyIdentities)
      {
      return getPeerProxies(createHostAndPort(peerIdentifier), proxyIdentities);
      }

   private Map<Identity, ObjectPrx> getPeerProxies(final HostAndPort peerHostAndPort, final Set<Identity> proxyIdentities)
      {
      final Map<Identity, ObjectPrx> proxyMap = new HashMap<Identity, ObjectPrx>();
      if ((proxyIdentities != null) && (!proxyIdentities.isEmpty()))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("DirectConnectCommunicator.getPeerProxies(" + peerHostAndPort + ", " + proxyIdentities.size() + " proxyIdentities)");
            }

         for (final Identity identity : proxyIdentities)
            {
            if (identity != null)
               {
               final ObjectPrx proxy = getPeerProxy(peerHostAndPort, identity);

               if (proxy != null)
                  {
                  if (LOG.isDebugEnabled())
                     {
                     LOG.debug("   Obtained proxy [" + proxy.ice_toString() + "] with identity [" + Util.identityToString(identity) + "] from host [" + peerHostAndPort + "]");
                     }
                  proxyMap.put(identity, proxy);
                  }
               else
                  {
                  if (LOG.isEnabledFor(Level.WARN))
                     {
                     LOG.warn("   Ignoring null proxy returned for identity [" + Util.identityToString(identity) + "]");
                     }
                  }
               }
            else
               {
               LOG.warn("   ignoring null identity");
               }
            }
         }
      else
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("DirectConnectCommunicator.getPeerProxies(" + peerHostAndPort + ", null)");
            }
         }

      return proxyMap;
      }

   private void registerPeerProxy(final HostAndPort peerHostAndPort, final ObjectPrx peerProxy)
      {
      peerHostAndPortToProxyMap.put(peerHostAndPort, TerkUserPrxHelper.uncheckedCast(peerProxy));
      }

   private void unregisterPeerProxy(final HostAndPort peerHostAndPort)
      {
      peerHostAndPortToProxyMap.remove(peerHostAndPort);
      }

   private HostInformation extractHostInformation(final ObjectPrx proxy)
      {
      HostInformation hostInformation = null;
      try
         {
         hostInformation = HostInformation.extractHostInformation(proxy.ice_getConnection().toString());
         }
      catch (Exception e)
         {
         LOG.debug("DirectConnectCommunicator.extractHostInformation(): failed to extract host information from proxy, returning null", e);
         }

      return hostInformation;
      }

   private class MyPeerConnectionEventDistributorHelper implements PeerConnectionEventDistributorHelper
      {
      public void publishPeerConnectedEvent(final String peerUserId, final PeerAccessLevel accessLevel, final ObjectPrx peerProxy)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$MyPeerConnectionEventDistributorHelper.publishPeerConnectedEvent(" + peerUserId + ")");
            }

         // this will get executed if the owner of this DirectConnectCommunicator gets connected to by another user
         // (e.g. this instance is being used by a server), so we need to update our set of connected peers
         try
            {
            final HostAndPort hostAndPort = createHostAndPort(peerUserId);
            registerPeerProxy(hostAndPort, peerProxy);
            }
         catch (Exception e)
            {
            LOG.error("Failed to register connection from peer [" + peerUserId + "]: " + e);
            }

         // fire the event
         firePeerConnectedEvent(peerUserId, accessLevel, peerProxy);
         }

      public void publishPeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel accessLevel)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$MyPeerConnectionEventDistributorHelper.publishPeerConnectedNoProxyEvent(" + peerUserId + ")");
            }

         firePeerConnectedNoProxyEvent(peerUserId, accessLevel);
         }

      public void publishPeerDisconnectedEvent(final String peerUserId)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$MyPeerConnectionEventDistributorHelper.publishPeerDisconnectedEvent(" + peerUserId + ")");
            }

         // this will get executed if the owner of this DirectConnectCommunicator gets connected to by another user
         // who then later disconnects (e.g. this instance is being used by a server), so we need to update our set of
         // connected peers
         try
            {
            final HostAndPort hostAndPort = createHostAndPort(peerUserId);
            unregisterPeerProxy(hostAndPort);
            }
         catch (Exception e)
            {
            LOG.error("Failed to handle disconnection from peer [" + peerUserId + "]: " + e);
            }

         firePeerDisconnectedEvent(peerUserId);
         }

      public void publishPeerConnectionFailedEvent(final String peerUserId)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$MyPeerConnectionEventDistributorHelper.publishPeerConnectionFailedEvent(" + peerUserId + ")");
            }

         firePeerConnectionFailedEvent(peerUserId);
         }
      }

   private final class PingHandlingPeerConnectionEventListener extends PeerConnectionEventAdapter
      {

      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$PingHandlingPeerConnectionEventListener.handlePeerConnectedEvent(" + peerUserId + ")");
            }

         final HostAndPort hostAndPort = createHostAndPort(peerUserId);

         final TerkUserPrx peerProxy = peerHostAndPortToProxyMap.get(hostAndPort);
         if (peerProxy == null)
            {
            LOG.error("DirectConnectCommunicator$PingHandlingPeerConnectionEventListener.handlePeerConnectedEvent() failed to " +
                      "retrieve the peerProxy for peer [" + peerUserId + "], so a pinger could not be created.");
            }
         else
            {
            final ScheduledFuture<?> scheduledFuture = pingSchedulingService.scheduleWithFixedDelay(new MyPeerPinger(hostAndPort, peerProxy),
                                                                                                    0,
                                                                                                    PING_INTERVAL_IN_SECONDS,
                                                                                                    TimeUnit.SECONDS);
            peerHostAndPortToPingerMap.put(hostAndPort, scheduledFuture);
            if (LOG.isTraceEnabled())
               {
               LOG.trace("DirectConnectCommunicator$PingHandlingPeerConnectionEventListener.handlePeerConnectedEvent(): added pinger for [" + hostAndPort + "]");
               }
            }
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$PingHandlingPeerConnectionEventListener.handlePeerDisconnectedEvent(" + peerUserId + ")");
            }

         try
            {
            final HostAndPort hostAndPort = createHostAndPort(peerUserId);

            final ScheduledFuture<?> scheduledFuture = peerHostAndPortToPingerMap.remove(hostAndPort);
            if (scheduledFuture != null)
               {
               scheduledFuture.cancel(true);
               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("DirectConnectCommunicator$PingHandlingPeerConnectionEventListener.handlePeerDisconnectedEvent(): canceled and removed pinger for [" + hostAndPort + "]");
                  }
               }
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to create the peer HostAndPort from peer user ID [" + peerUserId + "]", e);
            }
         }
      }

   private final class MyPeerPinger extends IceProxyPingerRunnable
      {
      private final HostAndPort hostAndPort;

      private MyPeerPinger(final HostAndPort hostAndPort, final ObjectPrx objectProxy)
         {
         super(hostAndPort.getHostAndPort(), objectProxy);
         this.hostAndPort = hostAndPort;
         }

      protected void handlePingFailure()
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("DirectConnectCommunicator$MyPeerPinger.handlePingFailure() for [" + hostAndPort + "]");
            }

         // if the ping fails, then we want to disconnect
         disconnectFromPeer(hostAndPort, false);
         }
      }
   }
