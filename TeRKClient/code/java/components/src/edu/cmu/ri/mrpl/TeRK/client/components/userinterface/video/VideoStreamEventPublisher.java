package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VideoStreamEventPublisher
   {
   /**
    * Publishes the frame represented by the given <code>frameData</code> to its collection of
    * {@link VideoStreamEventListener}s.
    *
    * @see #addVideoStreamEventListener(VideoStreamEventListener)
    * @see #removeVideoStreamEventListener(VideoStreamEventListener)
    */
   void publishFrame(final byte[] frameData);

   void addVideoStreamEventListener(VideoStreamEventListener listener);

   void removeVideoStreamEventListener(VideoStreamEventListener listener);
   }