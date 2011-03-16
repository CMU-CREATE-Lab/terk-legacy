using TeRK.services;
using System.Drawing;

namespace TeRK.components.services
{
    public abstract class VideoStreamService : Service
    {
      public const string TYPE_ID = "::TeRK::VideoStreamerServer";

      public string getTypeId()
         {
         return TYPE_ID;
         }

      /** start the camera */
      public abstract void startCamera();
    
        /** get a frame from the camera */
      public abstract VideoFrame getFrame();

        /** stop the camera */
      public abstract void stopCamera();      
    }
}
