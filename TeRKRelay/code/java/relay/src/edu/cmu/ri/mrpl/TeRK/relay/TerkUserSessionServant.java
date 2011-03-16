package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import Ice.Current;
import Ice.Identity;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelper;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.AsynchronousForcedLogoutNotificationServant;
import edu.cmu.ri.mrpl.peer.AsynchronousPeerConnectedNoProxyServant;
import edu.cmu.ri.mrpl.peer.AsynchronousPeerConnectedServant;
import edu.cmu.ri.mrpl.peer.AsynchronousPeerDisconnectedServant;
import edu.cmu.ri.mrpl.peer.ConnectionEventHandlerPrx;
import edu.cmu.ri.mrpl.peer.ConnectionEventHandlerPrxHelper;
import edu.cmu.ri.mrpl.peer.ConnectionEventSource;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.peer._UserSessionDisp;
import org.apache.log4j.Logger;

// todo: add code to prevent execution of methods until registerCallbacks() has been called first

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TerkUserSessionServant extends _UserSessionDisp implements ConnectionEventSource
   {
   private static final Logger LOG = Logger.getLogger(TerkUserSessionServant.class);

   private final TerkUser terkUser;
   private final RelayConnectionManager relayConnectionManager;
   private ConnectionEventHandlerPrx connectionEventHandlerPrx;

   TerkUserSessionServant(final TerkUser terkUser, final RelayConnectionManager relayConnectionManager)
      {
      this.terkUser = terkUser;
      this.relayConnectionManager = relayConnectionManager;
      }

   public void fireRelayLoginEvent()
      {
      // Do nothing here since it's an explicit request from the user.  That is, they KNOW they tried to login, and
      // whether it was successful, so there's no point in notifying them here.
      }

   public void fireFailedRelayLoginEvent()
      {
      // Do nothing here since it's an explicit request from the user.  That is, they KNOW they tried to login, and
      // whether it was successful, so there's no point in notifying them here.
      }

   public void fireRelayRegistrationEvent()
      {
      // Do nothing here since it's an explicit request from the user.  That is, they KNOW they tried to register, and
      // whether it was successful, so there's no point in notifying them here.
      }

   public void fireRelayLogoutEvent()
      {
      // Do nothing here since it's an explicit request from the user.  That is, they KNOW they tried to logout, so
      // there's no point in notifying them here.
      }

   public void fireForcedLogoutNotificationEvent()
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.fireForcedLogoutNotificationEvent() of user [" + terkUser.getUserId() + "]");
         }

      if (connectionEventHandlerPrx != null)
         {
         final AsynchronousForcedLogoutNotificationServant callback = new AsynchronousForcedLogoutNotificationServant(terkUser.getUserId());
         connectionEventHandlerPrx.forcedLogoutNotification_async(callback);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Done notifying " + terkUser.getUserId() + " about the forced logout.");
            }
         }
      else
         {
         LOG.warn("The ConnectionEventHandlerPrx is null!  This can happen if registerCallbacks() is not called first!");
         }
      }

   public void firePeerConnectedEvent(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.firePeerConnectedEvent(" + peerId + "," + peerAccessLevel + ")");
         }

      if (connectionEventHandlerPrx != null)
         {
         final AsynchronousPeerConnectedServant callback = new AsynchronousPeerConnectedServant(terkUser.getUserId(), peerId);
         connectionEventHandlerPrx.peerConnected_async(callback, peerId, peerAccessLevel, peerProxy);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Done notifying " + terkUser.getUserId() + " about " + peerId);
            }
         }
      else
         {
         LOG.warn("The ConnectionEventHandlerPrx is null!  This can happen if registerCallbacks() is not called first!");
         }
      }

   public void firePeerConnectedNoProxyEvent(final String peerId, final PeerAccessLevel peerAccessLevel)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.firePeerConnectedNoProxyEvent(" + peerId + "," + peerAccessLevel + ")");
         }

      if (connectionEventHandlerPrx != null)
         {
         final AsynchronousPeerConnectedNoProxyServant callback = new AsynchronousPeerConnectedNoProxyServant(terkUser.getUserId(), peerId);
         connectionEventHandlerPrx.peerConnectedNoProxy_async(callback, peerId, peerAccessLevel);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Done notifying " + terkUser.getUserId() + " about " + peerId);
            }
         }
      else
         {
         LOG.warn("The ConnectionEventHandlerPrx is null!  This can happen if registerCallbacks() is not called first!");
         }
      }

   public void firePeerDisconnectedEvent(final String peerId)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.firePeerDisconnectedEvent(" + peerId + ")");
         }

      if (connectionEventHandlerPrx != null)
         {
         final AsynchronousPeerDisconnectedServant callback = new AsynchronousPeerDisconnectedServant(terkUser.getUserId(), peerId);
         connectionEventHandlerPrx.peerDisconnected_async(callback, peerId);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Done notifying " + terkUser.getUserId() + " about " + peerId);
            }
         }
      else
         {
         LOG.warn("The ConnectionEventHandlerPrx is null!  This can happen if registerCallbacks() is not called first!");
         }
      }

   /**
    * This method does nothing since there's no need for the relay to notify the user of a failed connection since an
    * exception will be thrown instead.
    */
   public void firePeerConnectionFailedEvent(final String peerId)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.firePeerConnectionFailedEvent(" + peerId + ")");
         }
      }

   public void registerCallbacks(final ObjectPrx selfCallbackProxy, final ConnectionEventHandlerPrx connectionEventHandlerProxy, final Current current) throws RegistrationException
      {
      // todo: add code to prevent registration more than once
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.registerCallbacks()" + IceUtil.dumpCurrentToString(current));
         }

      final ObjectPrx objectCallbackProxy = ObjectPrxHelper.uncheckedCast(selfCallbackProxy.ice_context(IceUtil.TWOWAY_COMPRESSED_CALLBACK_CONTEXT_MAP));
      connectionEventHandlerPrx = ConnectionEventHandlerPrxHelper.checkedCast(connectionEventHandlerProxy.ice_context(IceUtil.TWOWAY_COMPRESSED_CALLBACK_CONTEXT_MAP));

      relayConnectionManager.registerUser(terkUser.getUserId(), current.id, objectCallbackProxy, this);
      }

   public void registerProxy(final ObjectPrx proxy, final Current current) throws RegistrationException
      {
      LOG.trace("TerkUserSessionServant.registerProxy()");
      relayConnectionManager.registerProxy(terkUser.getUserId(), proxy);
      }

   public void registerProxies(final List<ObjectPrx> proxies, final Current current) throws RegistrationException
      {
      LOG.trace("TerkUserSessionServant.registerProxies()");
      relayConnectionManager.registerProxies(terkUser.getUserId(), proxies);
      }

   public ObjectPrx getPeerProxy(final String peerUserId, final Identity privateProxyIdentity, final Current current) throws InvalidIdentityException, PeerAccessException
      {
      LOG.trace("TerkUserSessionServant.getPeerProxy()");
      return relayConnectionManager.getPeerProxy(terkUser.getUserId(), peerUserId, privateProxyIdentity);
      }

   public Map<Identity, ObjectPrx> getPeerProxies(final String peerUserId, final List<Identity> privateProxyIdentities, final Current current) throws InvalidIdentityException, PeerAccessException
      {
      LOG.trace("TerkUserSessionServant.getPeerProxies()");
      return relayConnectionManager.getPeerProxies(terkUser.getUserId(), peerUserId, privateProxyIdentities);
      }

   public void destroy(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.destroy()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("Destroying session for user [" + terkUser.getUserId() + "] with session identity [" + Util.identityToString(current.id) + "]");
         }
      relayConnectionManager.unregister(terkUser.getUserId(), current.id);
      current.adapter.remove(current.id);
      }

   public List<PeerIdentifier> getMyPeers(final Current current) throws PeerException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.getMyPeers()" + IceUtil.dumpCurrentToString(current));
         }
      return new ArrayList<PeerIdentifier>(relayConnectionManager.getMyPeers(terkUser.getUserId()));
      }

   public List<PeerIdentifier> getMyAvailablePeers(final Current current) throws PeerException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.getMyAvailablePeers()" + IceUtil.dumpCurrentToString(current));
         }
      return new ArrayList<PeerIdentifier>(relayConnectionManager.getMyAvailablePeers(terkUser.getUserId()));
      }

   public List<PeerIdentifier> getMyUnavailablePeers(final Current current) throws PeerException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.getMyUnavailablePeers()" + IceUtil.dumpCurrentToString(current));
         }
      return new ArrayList<PeerIdentifier>(relayConnectionManager.getMyUnavailablePeers(terkUser.getUserId()));
      }

   public ObjectPrx connectToPeer(final String peerUserId, final Current current) throws PeerAccessException,
                                                                                         PeerUnavailableException,
                                                                                         PeerConnectionFailedException,
                                                                                         DuplicateConnectionException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.connectToPeer()" + IceUtil.dumpCurrentToString(current));
         }
      return relayConnectionManager.connectToPeer(terkUser.getUserId(), peerUserId);
      }

   public List<PeerIdentifier> getConnectedPeers(final Current current) throws PeerException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.getConnectedPeers()" + IceUtil.dumpCurrentToString(current));
         }
      return new ArrayList<PeerIdentifier>(relayConnectionManager.getConnectedPeers(terkUser.getUserId()));
      }

   public void disconnectFromPeer(final String peerUserId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.disconnectFromPeer(" + peerUserId + ")" + IceUtil.dumpCurrentToString(current));
         }
      relayConnectionManager.disconnectFromPeer(terkUser.getUserId(), peerUserId);
      }

   public void disconnectFromPeers(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserSessionServant.disconnectFromPeers()" + IceUtil.dumpCurrentToString(current));
         }
      relayConnectionManager.disconnectFromPeers(terkUser.getUserId());
      }

   public String toString()
      {
      return "TerkUserSessionServant{" +
             "userId=" + (terkUser == null ? null : terkUser.getUserId()) +
             ", connectionEventHandlerPrx=" + (connectionEventHandlerPrx == null ? null : Util.identityToString(connectionEventHandlerPrx.ice_getIdentity())) +
             "}";
      }
   }
