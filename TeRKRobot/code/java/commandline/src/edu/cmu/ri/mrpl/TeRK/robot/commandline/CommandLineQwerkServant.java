package edu.cmu.ri.mrpl.TeRK.robot.commandline;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import Ice.Current;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineClientPrx;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineClientPrxHelper;
import edu.cmu.ri.mrpl.TeRK.commandline._SimpleCommandLineQwerkDisp;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class CommandLineQwerkServant extends _SimpleCommandLineQwerkDisp
   {
   private static final Logger LOG = Logger.getLogger(CommandLineQwerkServant.class);
   private static final Random RANDOM_NUMBER_GENERATOR = new Random();

   private final Map<String, SimpleCommandLineClientPrx> terkClientProxies = Collections.synchronizedMap(new HashMap<String, SimpleCommandLineClientPrx>());
   private boolean wasLogoutForced = false;

   boolean wasLogoutForced()
      {
      return wasLogoutForced;
      }

   public int getRandomInt(final int upperBound, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.getRandomInt()" + IceUtil.dumpCurrentToString(current));
         }
      final int randomNumber = RANDOM_NUMBER_GENERATOR.nextInt(upperBound);
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant is returning randomNumber = [" + randomNumber + "]");
         }
      return randomNumber;
      }

   public void printMessage(final String data, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.printMessage()" + IceUtil.dumpCurrentToString(current));
         }
      LOG.info(data);
      }

   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }
      wasLogoutForced = true;
      }

   public void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         }
      terkClientProxies.put(peerId, SimpleCommandLineClientPrxHelper.uncheckedCast(peerProxy));
      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
         }
      }

   public void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
         }
      }

   public void peerDisconnected(final String peerId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CommandLineQwerkServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         }
      terkClientProxies.remove(peerId);
      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "] just disconnected from me.");
         }
      }

   Collection<SimpleCommandLineClientPrx> getTerkClientProxies()
      {
      return Collections.unmodifiableCollection(terkClientProxies.values());
      }
   }
