package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import Ice.Identity;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK._TerkUserDisp;
import edu.cmu.ri.mrpl.TeRK.servants.ServiceServantRegistrar;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.ConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RoboticonMessengerServerServant extends _TerkUserDisp implements ServiceServantRegistrar
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerServerServant.class);

   private final Map<String, String> properties = new HashMap<String, String>();

   private final ConnectionEventListener connectionEventListener;

   /** Map of supported services */
   private final Map<String, Identity> commandControllerTypeToProxyIdentityMap = Collections.synchronizedMap(new HashMap<String, Identity>());

   RoboticonMessengerServerServant(final ConnectionEventListener connectionEventListener)
      {
      this.connectionEventListener = connectionEventListener;
      }

   public String getProperty(final String key, final Current current)
      {
      return properties.get(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return Collections.unmodifiableMap(properties);
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return new ArrayList<String>(properties.keySet());
      }

   public void setProperty(final String key, final String value, final Current current)
      {
      properties.put(key, value);
      }

   public HashMap<String, Identity> getSupportedServices(final Current current)
      {
      return new HashMap<String, Identity>(commandControllerTypeToProxyIdentityMap);
      }

   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RoboticonMessengerServerServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }
      connectionEventListener.handleForcedLogoutNotificationEvent();
      }

   public void peerConnected(final String peerUserId, final PeerAccessLevel accessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RoboticonMessengerServerServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         if (LOG.isInfoEnabled())
            {
            LOG.info("The peer [" + peerUserId + "|" + accessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
            }
         }
      connectionEventListener.handlePeerConnectedEvent(peerUserId, accessLevel, peerProxy);
      }

   public void peerConnectedNoProxy(final String peerUserId, final PeerAccessLevel accessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RoboticonMessengerServerServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         if (LOG.isInfoEnabled())
            {
            LOG.info("The peer [" + peerUserId + "|" + accessLevel + "] just connected to me (and I didn't get a proxy).");
            }
         }
      connectionEventListener.handlePeerConnectedNoProxyEvent(peerUserId, accessLevel);
      }

   public void peerDisconnected(final String peerUserId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("RoboticonMessengerServerServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         if (LOG.isInfoEnabled())
            {
            LOG.info("The peer [" + peerUserId + "] just disconnected from me.");
            }
         }
      connectionEventListener.handlePeerDisconnectedEvent(peerUserId);
      }

   public void registerServiceServant(final ObjectImpl serviceServant, final ObjectPrx serviceServantProxy)
      {
      if ((serviceServant != null) && (serviceServantProxy != null))
         {
         final String typeId = serviceServant.ice_id();
         final Identity identity = serviceServantProxy.ice_getIdentity();
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RoboticonMessengerServerServant.registerServiceServant() is registering type id [" + typeId + "] to identity [" + Util.identityToString(identity) + "]");
            }
         commandControllerTypeToProxyIdentityMap.put(typeId, identity);
         }
      }
   }
