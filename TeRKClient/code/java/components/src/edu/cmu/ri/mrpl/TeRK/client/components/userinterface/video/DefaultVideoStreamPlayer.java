package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class DefaultVideoStreamPlayer implements VideoStreamPlayer
   {
   private final VideoStreamSubscriber videoStreamSubscriber;
   private Set<VideoStreamEventListener> videoStreamEventListeners = new HashSet<VideoStreamEventListener>();

   /**
    * Creates a {@link DefaultVideoStreamPlayer} using the given {@link VideoStreamSubscriber}.
    */
   public DefaultVideoStreamPlayer(final VideoStreamSubscriber videoStreamSubscriber)
      {
      this(videoStreamSubscriber, null);
      }

   /**
    * Convenience constructor which also adds the given <code>videoStreamEventListener</code> to the internal collection
    * of {@link VideoStreamEventListener}s.
    */
   public DefaultVideoStreamPlayer(final VideoStreamSubscriber videoStreamSubscriber, final VideoStreamEventListener videoStreamEventListener)
      {
      this.videoStreamSubscriber = videoStreamSubscriber;
      addVideoStreamEventListener(videoStreamEventListener);
      }

   public void startVideoStream()
      {
      videoStreamSubscriber.startVideoStream();
      }

   public void pauseVideoStream()
      {
      videoStreamSubscriber.pauseVideoStream();
      }

   public void resumeVideoStream()
      {
      videoStreamSubscriber.resumeVideoStream();
      }

   public void stopVideoStream()
      {
      videoStreamSubscriber.stopVideoStream();
      }

   /**
    * Displays the image described by the given array of bytes which were read from an image file containing a supported
    * image format, such as GIF, JPEG, or PNG.
    */
   public final void publishFrame(final byte[] frameData)
      {
      for (VideoStreamEventListener videoStreamEventListener : videoStreamEventListeners)
         {
         videoStreamEventListener.handleFrame(frameData);
         }
      }

   public final void addVideoStreamEventListener(final VideoStreamEventListener listener)
      {
      if (listener != null)
         {
         videoStreamEventListeners.add(listener);
         }
      }

   public final void removeVideoStreamEventListener(final VideoStreamEventListener listener)
      {
      if (listener != null)
         {
         videoStreamEventListeners.remove(listener);
         }
      }
   }
