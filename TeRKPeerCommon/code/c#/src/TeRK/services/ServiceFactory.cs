using Ice;

namespace TeRK.services
   {
   public interface ServiceFactory
      {
      Service createService(string serviceTypeId, ObjectPrx serviceProxy);
      }
   }