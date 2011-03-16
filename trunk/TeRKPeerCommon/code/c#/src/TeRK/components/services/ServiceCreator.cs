using Ice;
using TeRK.services;

namespace TeRK.components.services
   {
   internal interface ServiceCreator
      {
      Service create(ObjectPrx serviceProxy);
      }
   }