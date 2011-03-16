package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

import Ice.LocalException;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class IceVideoStreamSubscriber implements VideoStreamSubscriber
   {
   private static final Logger LOG = Logger.getLogger(IceVideoStreamSubscriber.class);

   private VideoStreamService videoStreamService;

   public void startVideoStream()
      {
      LOG.debug("IceVideoStreamSubscriber.startVideoStream()");
      SwingUtils.warnIfEventDispatchThread("startVideoStream()");

      if (videoStreamService != null)
         {
         try
            {
            LOG.debug("Calling startVideoStream() on the VideoStreamService...");
            videoStreamService.startVideoStream();
            LOG.debug("Done calling startVideoStream() on the VideoStreamService!");
            }
         catch (LocalException e)
            {
            LOG.error("LocalException while starting the camera or starting the video stream.", e);
            }
         }
      }

   public void pauseVideoStream()
      {
      LOG.debug("IceVideoStreamSubscriber.pauseVideoStream()");
      SwingUtils.warnIfEventDispatchThread("pauseVideoStream()");

      if (videoStreamService != null)
         {
         try
            {
            LOG.debug("Pausing video stream by calling stopVideoStream() on the VideoStreamService...");
            videoStreamService.stopVideoStream();
            LOG.debug("Done pausing video stream by calling stopVideoStream() on the VideoStreamService!");
            }
         catch (LocalException e)
            {
            LOG.error("LocalException while pausing the video stream.", e);
            }
         }
      }

   public void resumeVideoStream()
      {
      LOG.debug("IceVideoStreamSubscriber.resumeVideoStream()");
      SwingUtils.warnIfEventDispatchThread("resumeVideoStream()");

      if (videoStreamService != null)
         {
         try
            {
            LOG.debug("Resuming video stream by calling startVideoStream() on the VideoStreamService...");
            videoStreamService.startVideoStream();
            LOG.debug("Done resuming video stream by calling startVideoStream() on the VideoStreamService!");
            }
         catch (LocalException e)
            {
            LOG.error("LocalException while resuming the video stream.", e);
            }
         }
      }

   public void stopVideoStream()
      {
      LOG.debug("IceVideoStreamSubscriber.stopVideoStream()");
      SwingUtils.warnIfEventDispatchThread("stopVideoStream()");

      if (videoStreamService != null)
         {
         try
            {
            LOG.debug("Calling stopVideoStream() on the VideoStreamService...");
            videoStreamService.stopVideoStream();
            LOG.debug("Done calling stopVideoStream() on the VideoStreamService!");
            }
         catch (LocalException e)
            {
            LOG.error("LocalException while stopping the video stream.", e);
            }
         }
      }

   public void setVideoStreamService(final VideoStreamService videoStreamService)
      {
      this.videoStreamService = videoStreamService;
      }
   }
