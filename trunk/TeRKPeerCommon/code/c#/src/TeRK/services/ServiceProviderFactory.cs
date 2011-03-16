using peer;
using TeRK.communications;

namespace TeRK.services
   {
   public abstract class ServiceProviderFactory
      {
      // todo: read config file to decide which kind of ServiceProviderFactory to create
      private readonly static ServiceProviderFactory SERVICE_PROVIDER_FACTORY = new TerkServiceProviderFactory();

      public static ServiceProviderFactory getFactory()
         {
         return SERVICE_PROVIDER_FACTORY;
         }

      public abstract ServiceProvider createServiceProvider(string peerUserId, TerkUserPrx peerObjectPrx, TerkCommunicator communicator);
      }
   }
