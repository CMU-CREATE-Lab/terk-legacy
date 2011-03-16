package edu.cmu.ri.mrpl.TeRK.peerinformation;

import java.awt.Image;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface PeerImageFactory
   {
   /**
    * Returns an {@link Image} associated with the peer specified by the given <code>peerUserId</code>.   May return
    * <code>null</code> if no image exists, or if an error occurred while obtaining the image.
    */
   Image createImage(final String peerUserId);
   }