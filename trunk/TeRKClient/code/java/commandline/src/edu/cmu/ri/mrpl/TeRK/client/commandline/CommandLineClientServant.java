package edu.cmu.ri.mrpl.TeRK.client.commandline;

import Ice.Current;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.commandline._SimpleCommandLineClientDisp;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineClientServant extends _SimpleCommandLineClientDisp
   {
   private static final Logger LOG = Logger.getLogger(CommandLineClientServant.class);

   private boolean wasLogoutForced = false;

   boolean wasLogoutForced()
      {
      return wasLogoutForced;
      }

   public void printMessage(final String data, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineClientServant.printMessage()" + IceUtil.dumpCurrentToString(current));
         }
      LOG.info(data);
      }

   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineClientServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }
      LOG.info("You have been logged out from the relay because another session is in use with the same login.");
      wasLogoutForced = true;
      }

   public void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineClientServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         }
      LOG.info("The robot [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
      }

   public void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineClientServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         }
      LOG.info("The robot [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
      }

   public void peerDisconnected(final String peerId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineClientServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         }
      // todo: set the robot proxy in the TerkClient to null
      LOG.info("The robot [" + peerId + "] just disconnected from me.");
      }
   }
