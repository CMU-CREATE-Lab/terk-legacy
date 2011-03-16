using Ice;
using TeRK.services;

namespace TeRK.components.services
   {
   internal class QwerkServiceFactory : DefaultServiceFactory
      {
      internal QwerkServiceFactory(QwerkPrx qwerkProxy)
         {
         getTypeIdToServiceCreatorsMap().Add(AnalogInputsService.TYPE_ID, new AnalogInputServiceCreator(qwerkProxy));
         getTypeIdToServiceCreatorsMap().Add(DigitalIOService.TYPE_ID, new DigitalIOServiceCreator(qwerkProxy));
         getTypeIdToServiceCreatorsMap().Add(VideoStreamService.TYPE_ID, new VideoStreamServiceCreator());
         }
      }

   internal class AnalogInputServiceCreator : ServiceCreator
      {
      private readonly QwerkPrx qwerkProxy;

      internal AnalogInputServiceCreator(QwerkPrx qwerkProxy)
         {
         this.qwerkProxy = qwerkProxy;
         }

      public Service create(ObjectPrx serviceProxy)
         {
         return new AnalogInputsServiceImpl(qwerkProxy);
         }
      }

   internal class DigitalIOServiceCreator : ServiceCreator
   {
       private readonly QwerkPrx qwerkProxy;

       internal DigitalIOServiceCreator(QwerkPrx qwerkProxy)
       {
           this.qwerkProxy = qwerkProxy;
       }

       public Service create(ObjectPrx serviceProxy)
       {
           return new DigitalIOServiceImpl(DigitalOutControllerPrxHelper.uncheckedCast(serviceProxy), qwerkProxy);
       }
   }

   internal class VideoStreamServiceCreator : ServiceCreator
   {
       public Service create(ObjectPrx serviceProxy)
       {
           return new VideoStreamServiceImpl(VideoStreamerServerPrxHelper.uncheckedCast(serviceProxy));
       }
   }
   }