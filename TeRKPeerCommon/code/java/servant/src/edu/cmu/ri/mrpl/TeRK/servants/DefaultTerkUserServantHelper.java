package edu.cmu.ri.mrpl.TeRK.servants;

import Ice.Current;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>DefaultTerkUserServantHelper</code> provides a default implementation of a {@link TerkUserServantHelper} which
 * delegates much functionality to the communicator's <code>PeerConnectionEventDistributorHelper</code> or, for the
 * forced logout notification, to the relay communicator's <code>ConnectionEventDistributorHelper</code>.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class DefaultTerkUserServantHelper extends AbstractTerkUserServantHelper
   {
   private static final Logger LOG = Logger.getLogger(DefaultTerkUserServantHelper.class);

   private final TerkCommunicator terkCommunicator;

   public DefaultTerkUserServantHelper(final TerkCommunicator terkCommunicator)
      {
      this.terkCommunicator = terkCommunicator;
      }

   public final void forcedLogoutNotification(final Current current)
      {
      LOG.debug("DefaultTerkUserServantHelper.forcedLogoutNotification()");
      // forced logout can only come from the relay, so we can safely cast to RelayCommunicator here
      final RelayCommunicator relayCommunicator = (RelayCommunicator)terkCommunicator;
      relayCommunicator.getConnectionEventDistributorHelper().publishForcedLogoutNotificationEvent();
      }

   public final void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx objectPrx, final Current current)
      {
      LOG.debug("DefaultTerkUserServantHelper.peerConnected()");
      terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedEvent(peerId, peerAccessLevel, objectPrx);
      }

   public final void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      LOG.debug("DefaultTerkUserServantHelper.peerConnectedNoProxy()");
      terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedNoProxyEvent(peerId, peerAccessLevel);
      }

   public final void peerDisconnected(final String peerId, final Current current)
      {
      LOG.debug("DefaultTerkUserServantHelper.peerDisconnected()");
      terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerDisconnectedEvent(peerId);
      }
   }
