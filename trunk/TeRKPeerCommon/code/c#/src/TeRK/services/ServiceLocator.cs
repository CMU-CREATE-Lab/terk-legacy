namespace TeRK.services
   {
   public interface ServiceLocator
      {
      Service getService(string serviceTypeId, ServiceFactory serviceFactory);
      }
   }