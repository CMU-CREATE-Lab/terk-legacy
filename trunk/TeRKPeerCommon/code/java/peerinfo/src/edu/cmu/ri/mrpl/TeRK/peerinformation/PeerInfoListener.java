package edu.cmu.ri.mrpl.TeRK.peerinformation;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface PeerInfoListener
   {
   void peerConnected(final PeerInfo peerInfo);

   void peerUpdated(final PeerInfo peerInfo);

   void peerDisconnected(final PeerInfo peerInfo);

   void peersChanged();
   }
