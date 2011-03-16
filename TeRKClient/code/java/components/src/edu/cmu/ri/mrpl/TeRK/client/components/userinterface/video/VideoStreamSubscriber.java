package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VideoStreamSubscriber
   {
   void startVideoStream();

   void pauseVideoStream();

   void resumeVideoStream();

   void stopVideoStream();
   }