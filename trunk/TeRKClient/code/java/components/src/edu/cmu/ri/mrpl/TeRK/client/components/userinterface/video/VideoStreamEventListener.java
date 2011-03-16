package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VideoStreamEventListener
   {
   void handleFrame(final byte[] frameData);
   }
