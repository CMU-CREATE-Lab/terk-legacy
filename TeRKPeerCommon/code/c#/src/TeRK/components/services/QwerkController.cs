using peer;
using TeRK.services;
using TeRK.communications;

namespace TeRK.components.services
   {
   public class QwerkController
      {
      private readonly QwerkPrx qwerkProxy;
      private readonly AnalogInputsService analogInputsService;
      private readonly DigitalIOService digitalIOService;
      private readonly MotorService motorService;
      private readonly ServoService servoService;
      private readonly VideoStreamService videoStreamService;

      public QwerkController(string qwerkUserId, QwerkPrx qwerkProxy, TerkCommunicator communicator)
         {
         this.qwerkProxy = qwerkProxy;
         ServiceProvider serviceProvider = ServiceProviderFactory.getFactory().createServiceProvider(qwerkUserId,
                                                                                                     qwerkProxy,
                                                                                                     communicator);

         ServiceFactory serviceFactory = new QwerkServiceFactory(qwerkProxy);
         // note: the AnalogInputsService is created differently since there isn't really a service of type
         // AnalogInputsService.TYPE_ID.  Currently, the only way to get analog data is from Qwerk's getState() method. So,
         // we use a special service factory (QwerkServiceFactory) which has a reference to the Qwerk proxy which is used
         // to create the AnalogInputsService.  This hackishness will change once we fix the API to fully support all
         // services (if you poke through the code, you'll see that the DigitalIOService is a bit of a hack, too).
         analogInputsService = (AnalogInputsService) serviceFactory.createService(AnalogInputsService.TYPE_ID, null);
         digitalIOService = (DigitalIOService) serviceProvider.getService(DigitalIOService.TYPE_ID, serviceFactory);
         motorService = (MotorService)serviceProvider.getService(MotorService.TYPE_ID, serviceFactory);
         servoService = (ServoService)serviceProvider.getService(ServoService.TYPE_ID, serviceFactory);
         videoStreamService = (VideoStreamService)serviceProvider.getService(VideoStreamService.TYPE_ID, serviceFactory);
         }

      public AnalogInputsService getAnalogInputsService()
         {
         return analogInputsService;
         }

      public DigitalIOService getDigitalIOService()
         {
         return digitalIOService;
         }

      public MotorService getMotorService()
         {
         return motorService;
         }

      public ServoService getServoService()
         {
         return servoService;
         }

      public VideoStreamService getVideoStreamService()
      {
          return videoStreamService;
      }

      public QwerkState getQwerkState()
         {
         return qwerkProxy.getState();
         }
      }
   }