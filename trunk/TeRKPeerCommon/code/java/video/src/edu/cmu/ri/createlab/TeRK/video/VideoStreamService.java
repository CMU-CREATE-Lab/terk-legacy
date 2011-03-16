package edu.cmu.ri.createlab.TeRK.video;

import java.awt.Image;
import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VideoStreamService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::VideoStreamerServer";

   /** Starts the video stream using an asynchronous call (thus, this method will not block). */
   void startVideoStream();

   /** Stops the video stream using an asynchronous call (thus, this method will not block). */
   void stopVideoStream();

   /**
    * Starts the camera using an asynchronous call (thus, this method will not block).  This method is part of the
    * "pull model" for video streaming, where clients pull images.  It is intended to be used in conjunction with
    * {@link #stopCamera()} and {@link #getFrame()}.
    */
   void startCamera();

   /**
    * Stops the camera using an asynchronous call (thus, this method will not block).  This method is part of the
    * "pull model" for video streaming, where clients pull images.  It is intended to be used in conjunction with
    * {@link #startCamera()} ()} and {@link #getFrame()}.
    */
   void stopCamera();

   /**
    * Returns the current frame.  This method is part of the "pull model" for video streaming, where clients pull
    * images. It is intended to be used in conjunction with {@link #startCamera()} ()} and {@link #stopCamera()}.
    */
   Image getFrame() throws VideoException;
   }