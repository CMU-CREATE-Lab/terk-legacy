using System;
using System.Collections.Generic;
using Ice;
using TeRK.services;

namespace TeRK.components.services
   {
   internal class DefaultServiceFactory : ServiceFactory
      {
      private readonly IDictionary<String, ServiceCreator> typeIdToServiceCreatorsMap = new Dictionary<String, ServiceCreator>();

      internal DefaultServiceFactory()
         {
         typeIdToServiceCreatorsMap.Add(MotorService.TYPE_ID, new MotorServiceCreator());
         typeIdToServiceCreatorsMap.Add(ServoService.TYPE_ID, new ServoServiceCreator());
         }

      protected IDictionary<String, ServiceCreator> getTypeIdToServiceCreatorsMap()
         {
         return typeIdToServiceCreatorsMap;
         }

      public Service createService(string serviceTypeId, ObjectPrx serviceProxy)
         {
         if (typeIdToServiceCreatorsMap.ContainsKey(serviceTypeId))
            {
            Console.WriteLine("DEBUG: DefaultServiceFactory.createService(" + serviceTypeId + ")");
            ServiceCreator serviceCreator;
            typeIdToServiceCreatorsMap.TryGetValue(serviceTypeId, out serviceCreator);
            return serviceCreator.create(serviceProxy);
            }
         return null;
         }
      }

   internal class MotorServiceCreator : ServiceCreator
      {
      public Service create(ObjectPrx serviceProxy)
         {
         return new MotorServiceImpl(MotorControllerPrxHelper.uncheckedCast(serviceProxy));
         }
      }

   internal class ServoServiceCreator : ServiceCreator
      {
      public Service create(ObjectPrx serviceProxy)
         {
         return new ServoServiceImpl(ServoControllerPrxHelper.uncheckedCast(serviceProxy));
         }
      }
   }