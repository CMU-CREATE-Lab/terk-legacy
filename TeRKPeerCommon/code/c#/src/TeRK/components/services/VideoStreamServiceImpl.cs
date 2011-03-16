using Ice;
using TeRK.services;
using TeRK;
using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;

namespace TeRK.components.services
{
internal class VideoStreamServiceImpl : VideoStreamService
   {
   private readonly VideoStreamerServerPrx proxy;
   private readonly int numDevices;

   internal VideoStreamServiceImpl(VideoStreamerServerPrx proxy)
      {
      this.proxy = proxy;
      this.numDevices = 1;// todo: this should be retrieved from the qwerk
      } 

   public override void startCamera()
      {
      try
         {
         proxy.startCamera_async(new StartCamera());
         }
      catch (Ice.Exception e)
         {
           throw e;
         }
      }
    
   public override void stopCamera()
      {
      try
         {
           proxy.stopCamera_async(new StopCamera());            
         }
      catch (Ice.Exception e)
         {
         throw e;
         }
      }

   public override VideoFrame getFrame()
   {       
       TeRK.Image image = proxy.getFrame(0);

       VideoFrame frame = new VideoFrame();
       frame.ImageData = image.data;
       frame.Width = image.width;
       frame.Height = image.height;
           
       return frame;
   }

   public int getDeviceCount()
      {
      return numDevices;
      }

   class StartCamera : AMI_VideoStreamerServer_startCamera
   {
       public override void ice_response(int i) { }

       public override void ice_exception(Ice.Exception ex) { }
   }

   class StopCamera : AMI_VideoStreamerServer_stopCamera
   {
       public override void ice_response(int i) { }

       public override void ice_exception(Ice.Exception ex) { }
   }

   }

}
