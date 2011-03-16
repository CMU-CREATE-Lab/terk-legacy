package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.util.ArrayList;
import java.util.Collection;
import java.util.PropertyResourceBundle;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategy;
import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.NoServicesServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.UserConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.UserConnectionEventListener;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class NetworkedFinchConnectionStrategy extends ConnectionStrategy
   {
   private static final Logger LOG = Logger.getLogger(NetworkedFinchConnectionStrategy.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(NetworkedFinchConnectionStrategy.class.getName());

   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/finch/application/NetworkedFinchConnectionStrategy.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/finch/application/NetworkedFinchConnectionStrategy.relay.ice.properties";
   private static final String ICE_OBJECT_ADAPTER_NAME = "Terk.User";

   private final UserConnectionEventListener userConnectionEventListener =
         new UserConnectionEventAdapter()
         {
         public void handleRelayLogoutEvent()
            {
            LOG.debug("NetworkedFinchConnectionStrategy.handleRelayLogoutEvent()");
            isConnectedToPeer = false;
            serviceManager = null;
            }

         public void handleForcedLogoutNotificationEvent()
            {
            LOG.debug("NetworkedFinchConnectionStrategy.handleForcedLogoutNotificationEvent()");
            isConnectedToPeer = false;
            serviceManager = null;
            notifyListenersOfDisconnectionEvent();
            }
         };

   private final IceServiceFactory serviceFactory = new NetworkedFinchServiceFactory();
   private TerkCommunicator terkCommunicator = null;
   private final Collection<PeerConnectionEventListener> peerConnectionEventListeners;
   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;
   private boolean isConnectedToPeer = false;
   private ServiceManager serviceManager = null;

   NetworkedFinchConnectionStrategy()
      {
      // create the ServantFactory instances
      final ServantFactory servantFactory = new NoServicesServantFactory();

      // create the direct-connect manager
      directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(APPLICATION_NAME,
                                                                                  ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                                  servantFactory);

      // create the relay manager
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(APPLICATION_NAME,
                                                                  ICE_RELAY_PROPERTIES_FILE,
                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                  servantFactory);

      // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
      // when various direct-connect-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(null);
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(relayCommunicatorManager));
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(directConnectCommunicatorManager));

      peerConnectionEventListeners = new ArrayList<PeerConnectionEventListener>();

      peerConnectionEventListeners.add(
            new PeerConnectionEventAdapter()
            {
            public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
               {
               LOG.debug("NetworkedFinchConnectionStrategy.handlePeerConnectedEvent()");

               isConnectedToPeer = true;
               serviceManager = new IceServiceManager(peerUserId,
                                                      TerkUserPrxHelper.uncheckedCast(peerObjectProxy),
                                                      terkCommunicator,
                                                      serviceFactory);
               notifyListenersOfConnectionEvent();
               }

            public void handlePeerConnectionFailedEvent(final String peerUserId)
               {
               LOG.debug("NetworkedFinchConnectionStrategy.handlePeerConnectionFailedEvent()");
               isConnectedToPeer = false;
               serviceManager = null;
               notifyListenersOfFailedConnectionEvent();
               }

            public void handlePeerDisconnectedEvent(final String peerUserId)
               {
               LOG.debug("NetworkedFinchConnectionStrategy.handlePeerDisconnectedEvent()");
               isConnectedToPeer = false;
               serviceManager = null;
               notifyListenersOfDisconnectionEvent();
               }
            });
      }

   protected final DirectConnectCommunicatorManager getDirectConnectCommunicatorManager()
      {
      return directConnectCommunicatorManager;
      }

   protected final RelayCommunicatorManager getRelayCommunicatorManager()
      {
      return relayCommunicatorManager;
      }

   public final boolean isConnected()
      {
      return isConnectedToPeer;
      }

   public final boolean isConnecting()
      {
      // there's not really any way to tell whether we're currently connecting, so just return false.
      return false;
      }

   public final ServiceManager getServiceManager()
      {
      return serviceManager;
      }

   public final void cancelConnect()
      {
      LOG.debug("NetworkedFinchConnectionStrategy.cancelConnect()");
      }

   public final void disconnect()
      {
      LOG.debug("NetworkedFinchConnectionStrategy.disconnect()");
      notifyListenersOfAttemptingDisconnectionEvent();
      // disconnect from peers
      if (terkCommunicator != null)
         {
         terkCommunicator.disconnectFromPeers();
         }
      }

   public final void prepareForShutdown()
      {
      LOG.debug("NetworkedFinchConnectionStrategy.prepareForShutdown()");
      disconnect();
      terkCommunicator.shutdown();
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      private final TerkCommunicatorManager otherTerkCommunicatorManager;

      private MyTerkCommunicatorCreationEventListener(final TerkCommunicatorManager otherTerkCommunicatorManager)
         {
         this.otherTerkCommunicatorManager = otherTerkCommunicatorManager;
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // add the peer connection event listener

         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            terkCommunicator.addPeerConnectionEventListener(listener);
            }

         // If this is a RelayCommunicator, then register the user connection event listener (so we can properly disable
         // peer connections when logging out of the relay without having disconnected from peers first)
         if (terkCommunicator instanceof RelayCommunicator)
            {
            ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(userConnectionEventListener);
            }

         // creation of this communicator means we should shut down the other communicator since, for this app at least,
         // we only ever want to be able to connect via one mode at a time.
         otherTerkCommunicatorManager.shutdownCommunicator();

         // set the current TerkCommunicator
         NetworkedFinchConnectionStrategy.this.terkCommunicator = terkCommunicator;
         }
      }
   }