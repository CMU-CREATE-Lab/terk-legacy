package edu.cmu.ri.mrpl.TeRK.agent.peerinformation;

import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface PeerInfoClientService extends Service
   {
   String TYPE_ID = "::TeRK::peerinformation::PeerInfoClientService";

   void peerConnected(final PeerInfo peer);

   void peerUpdated(final PeerInfo peer);

   void peerDisconnected(final PeerInfo peer);
   }
