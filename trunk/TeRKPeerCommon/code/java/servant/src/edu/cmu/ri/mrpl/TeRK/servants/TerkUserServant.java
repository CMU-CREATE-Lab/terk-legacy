package edu.cmu.ri.mrpl.TeRK.servants;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import Ice.Current;
import Ice.Identity;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.TerkUser;
import edu.cmu.ri.mrpl.TeRK._TerkUserDisp;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * <code>TerkUserServant</code> is a generic servant for the {@link TerkUser} interface.  It delegates operations to
 * the given {@link TerkUserServantHelper}.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public final class TerkUserServant extends _TerkUserDisp implements ServiceServantRegistrar
   {
   private static final Logger LOG = Logger.getLogger(TerkUserServant.class);

   // amount of time to wait before the forced logout notification is sent
   private static final int FORCED_LOGOUT_NOTIFICATION_DELAY_MILLIS = 500;

   private final TerkUserServantHelper helper;
   private final ScheduledExecutorService executorPool = Executors.newSingleThreadScheduledExecutor();

   /**
    * Constructs a <code>TerkUserServant</code> using a {@link DefaultTerkUserServantHelper} as the
    * {@link TerkUserServantHelper}.  If you need to specify your own {@link TerkUserServantHelper}, use
    * the {@link #TerkUserServant(TerkUserServantHelper)} constructor instead.
    */
   public TerkUserServant(final TerkCommunicator terkCommunicator)
      {
      this.helper = new DefaultTerkUserServantHelper(terkCommunicator);
      }

   /**
    * Constructs a <code>TerkUserServant</code> using the given {@link TerkUserServantHelper}.
    */
   public TerkUserServant(final TerkUserServantHelper helper)
      {
      this.helper = helper;
      }

   public String getProperty(final String key, final Current current)
      {
      return helper.getProperty(key, current);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return helper.getProperties(current);
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return helper.getPropertyKeys(current);
      }

   public void setProperty(final String key, final String value, final Current current) throws ReadOnlyPropertyException
      {
      helper.setProperty(key, value, current);
      }

   public void registerServiceServant(final ObjectImpl serviceServant, final ObjectPrx serviceServantProxy)
      {
      helper.registerServiceServant(serviceServant, serviceServantProxy);
      }

   public void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("TerkUserServant.peerConnected(): The peer [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
         }
      helper.peerConnected(peerId, peerAccessLevel, peerProxy, current);
      }

   public void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("TerkUserServant.peerConnectedNoProxy(): The peer [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
         }
      helper.peerConnectedNoProxy(peerId, peerAccessLevel, current);
      }

   public void peerDisconnected(final String peerId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("TerkUserServant.peerDisconnected(): The peer [" + peerId + "] just disconnected from me.");
         }
      helper.peerDisconnected(peerId, current);
      }

   /**
    * Asynchronously calls the {@link TerkUserServantHelper#forcedLogoutNotification(Current) forcedLogoutNotification()}
    * method in the given helper.
    */
   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkUserServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }

      final Runnable runnable =
            new Runnable()
            {
            public void run()
               {
               helper.forcedLogoutNotification(current);
               }
            };

      // Execute the handler in a separate thread so this method can return and the relay can proceed.  The notification
      // is delayed so that the relay is given a chance to log the user out.  Yeah, yeah, I know that this might cause
      // a race condition, but this is ok for now.
      executorPool.schedule(runnable,
                            FORCED_LOGOUT_NOTIFICATION_DELAY_MILLIS,
                            TimeUnit.MILLISECONDS);
      }

   public Map<String, Identity> getSupportedServices(final Current current)
      {
      return helper.getSupportedServices(current);
      }
   }
