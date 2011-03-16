package edu.cmu.ri.mrpl.TeRK.client.components.framework;

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
import edu.cmu.ri.mrpl.TeRK.Image;
import edu.cmu.ri.mrpl.TeRK._TerkClientDisp;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamEventPublisher;
import edu.cmu.ri.mrpl.TeRK.servants.ServiceServantRegistrar;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.ConnectionEventDistributorHelper;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class GUIClientServant extends _TerkClientDisp implements ServiceServantRegistrar
   {
   private static final Logger LOG = Logger.getLogger(GUIClientServant.class);

   private final ConnectionEventDistributorHelper connectionEventDistributorHelper;
   private final VideoStreamEventPublisher videoStreamEventPublisher;
   private final Runnable forcedLogoutNotificationRunnable = new GUIClientServant.ForcedLogoutNotificationRunnable();
   private final Map<String, String> properties = new HashMap<String, String>();

   /** Map of supported services */
   private final Map<String, Identity> commandControllerTypeToProxyIdentityMap = Collections.synchronizedMap(new HashMap<String, Identity>());

   GUIClientServant(final ConnectionEventDistributorHelper connectionEventDistributorHelper, final VideoStreamEventPublisher videoStreamEventPublisher)
      {
      this.connectionEventDistributorHelper = connectionEventDistributorHelper;
      this.videoStreamEventPublisher = videoStreamEventPublisher;
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

   public void registerServiceServant(final ObjectImpl serviceServant, final ObjectPrx serviceServantProxy)
      {
      if ((serviceServant != null) && (serviceServantProxy != null))
         {
         final String typeId = serviceServant.ice_id();
         final Identity identity = serviceServantProxy.ice_getIdentity();
         if (LOG.isDebugEnabled())
            {
            LOG.debug("GUIClientServant.registerServiceServant() is registering type id [" + typeId + "] to identity [" + Util.identityToString(identity) + "]");
            }
         commandControllerTypeToProxyIdentityMap.put(typeId, identity);
         }
      }

   public HashMap<String, Identity> getSupportedServices(final Current current)
      {
      return new HashMap<String, Identity>(commandControllerTypeToProxyIdentityMap);
      }

   public void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("GUIClientServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("The robot [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
         }
      connectionEventDistributorHelper.publishPeerConnectedEvent(peerId, peerAccessLevel, peerProxy);
      }

   public void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("GUIClientServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("The robot [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
         }
      connectionEventDistributorHelper.publishPeerConnectedNoProxyEvent(peerId, peerAccessLevel);
      }

   public void peerDisconnected(final String peerId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("GUIClientServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("The robot [" + peerId + "] just disconnected from me.");
         }
      connectionEventDistributorHelper.publishPeerDisconnectedEvent(peerId);
      }

   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("GUIClientServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }

      // execute the handler in a separate thread so this method can return and the relay can proceed
      new Thread(forcedLogoutNotificationRunnable).start();
      }

   public void newFrame(final Image frame, final Current current)
      {
      if (videoStreamEventPublisher != null)
         {
         if (frame != null && frame.data != null)
            {
            videoStreamEventPublisher.publishFrame(frame.data);
            }
         else
            {
            LOG.warn("GUIClientServant.newFrame(): ingoring null frame");
            }
         }
      }

   private final class ForcedLogoutNotificationRunnable implements Runnable
      {
      public void run()
         {
         try
            {
            // sleep for a half-second to give the relay a chance to log me out
            Thread.sleep(500);
            }
         catch (InterruptedException e)
            {
            // log but otherwise ignore
            LOG.error("InterruptedException while sleeping before calling publishForcedLogoutNotificationEvent()", e);
            }
         connectionEventDistributorHelper.publishForcedLogoutNotificationEvent();
         }
      }
   }
