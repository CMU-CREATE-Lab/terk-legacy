package edu.cmu.ri.createlab.TeRK.video;

import java.awt.Image;
import java.awt.Toolkit;
import Ice.LocalException;
import Ice.UserException;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.AMI_VideoStreamerServer_startCamera;
import edu.cmu.ri.mrpl.TeRK.AMI_VideoStreamerServer_startVideoStream;
import edu.cmu.ri.mrpl.TeRK.AMI_VideoStreamerServer_stopCamera;
import edu.cmu.ri.mrpl.TeRK.AMI_VideoStreamerServer_stopVideoStream;
import edu.cmu.ri.mrpl.TeRK.VideoStreamerServerPrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VideoStreamServiceImpl extends ServicePropertyManager implements VideoStreamService
   {
   private static final Logger LOG = Logger.getLogger(VideoStreamServiceImpl.class);

   private final VideoStreamerServerPrx proxy;
   private final int numDevices;

   public VideoStreamServiceImpl(final VideoStreamerServerPrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      this.numDevices = 1;// todo: this should be retrieved from the properties
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void startVideoStream()
      {
      try
         {
         proxy.startVideoStream_async(
               new AMI_VideoStreamerServer_startVideoStream()
               {
               public void ice_response()
                  {
                  // do nothing
                  }

               public void ice_exception(final LocalException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("LocalException while trying to start the video stream", ex);
                  }

               public void ice_exception(final UserException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("UserException while trying to start the video stream", ex);
                  }
               });
         }
      catch (Exception e)
         {
         // todo: handle this better and allow it to propogate to the caller
         LOG.error("Exception while trying to start the video stream", e);
         }
      }

   public void stopVideoStream()
      {
      try
         {
         proxy.stopVideoStream_async(
               new AMI_VideoStreamerServer_stopVideoStream()
               {
               public void ice_response()
                  {
                  // do nothing
                  }

               public void ice_exception(final LocalException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("LocalException while trying to stop the video stream", ex);
                  }

               public void ice_exception(final UserException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("UserException while trying to stop the video stream", ex);
                  }
               });
         }
      catch (Exception e)
         {
         // todo: handle this better and allow it to propogate to the caller
         LOG.error("Exception while trying to stop the video stream", e);
         }
      }

   public void startCamera()
      {
      try
         {
         proxy.startCamera_async(
               new AMI_VideoStreamerServer_startCamera()
               {
               public void ice_response(final int i)
                  {
                  LOG.debug("startCamera returned [" + i + "]");
                  }

               public void ice_exception(final LocalException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("LocalException while trying to start the camera", ex);
                  }

               public void ice_exception(final UserException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("UserException while trying to start the camera", ex);
                  }
               });
         }
      catch (Exception e)
         {
         // todo: handle this better and allow it to propogate to the caller
         LOG.error("Exception while trying to start the camera", e);
         }
      }

   public void stopCamera()
      {
      try
         {
         proxy.stopCamera_async(
               new AMI_VideoStreamerServer_stopCamera()
               {
               public void ice_response(final int i)
                  {
                  LOG.debug("stopCamera returned [" + i + "]");
                  }

               public void ice_exception(final LocalException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("LocalException while trying to stop the camera", ex);
                  }

               public void ice_exception(final UserException ex)
                  {
                  // todo: handle this better and allow it to propogate to the caller (e.g. by requiring callers to provide a listener)
                  LOG.error("UserException while trying to stop the camera", ex);
                  }
               });
         }
      catch (Exception e)
         {
         // todo: handle this better and allow it to propogate to the caller
         LOG.error("Exception while trying to stop the camera", e);
         }
      }

   public Image getFrame() throws VideoException
      {
      // get the current frame
      final edu.cmu.ri.mrpl.TeRK.Image frame;
      try
         {
         frame = proxy.getFrame(0);
         }
      catch (edu.cmu.ri.mrpl.TeRK.VideoException e)
         {
         throw new VideoException(e);
         }

      // convert it to an AWT Image
      if (frame != null && frame.data != null && frame.data.length > 0)
         {
         return Toolkit.getDefaultToolkit().createImage(frame.data);
         }

      return null;
      }

   public int getDeviceCount()
      {
      return numDevices;
      }
   }
