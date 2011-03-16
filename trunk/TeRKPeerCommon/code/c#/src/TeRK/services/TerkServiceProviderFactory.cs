using System;
using System.Collections.Generic;
using System.Text;
using TeRK.communications;
namespace TeRK.services
   {
   public class TerkServiceProviderFactory : ServiceProviderFactory
      {
      public override ServiceProvider createServiceProvider(string peerUserId, TerkUserPrx peerObjectPrx, TerkCommunicator communicator)
         {
         return new TerkServiceProvider(peerUserId, peerObjectPrx, communicator);
         }
      }
   }
