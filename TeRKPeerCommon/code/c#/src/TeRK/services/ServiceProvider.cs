using System.Collections.Generic;

namespace TeRK.services
   {
   public abstract class ServiceProvider : ServiceLocator
      {
      public abstract Service getService(string serviceTypeId, ServiceFactory serviceFactory);

      /** 
       * <summary>
       * Returns <c>true</c> if the given Ice type ID is supported by this service provider; <c>false</c>
       * otherwise.  The Ice type ID is the value returned by ObjectPrx.ice_id().
       * </summary>
       */
      public abstract bool isServiceSupported(string serviceTypeId);

      /**
       * Returns list of the Ice type IDs of the services supported by this service provider.  May
       * return an empty list, but guaranteed to not return <code>null</code>.  The Ice type ID is the value
       * returned by ObjectPrx.ice_id().
       */
      public abstract IList<string> getTypeIdsOfSupportedServices();
      }
   }