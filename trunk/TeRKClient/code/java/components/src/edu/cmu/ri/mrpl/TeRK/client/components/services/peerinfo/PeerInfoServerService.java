package edu.cmu.ri.mrpl.TeRK.client.components.services.peerinfo;

import java.util.List;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface PeerInfoServerService extends Service
   {
   String TYPE_ID = "::TeRK::peerinformation::PeerInfoServerService";

   void setAttribute(final String key, final String value);

   /** Returns a list of all possible peers (i.e. users which have access to the server) */
   List<PeerInfo> getPeerInfo();

   /** Returns a list of all connected peers. */
   List<PeerInfo> getConnectedPeerInfo();

   /** Returns a list of all disconnected peers. */
   List<PeerInfo> getDisconnectedPeerInfo();
   }
