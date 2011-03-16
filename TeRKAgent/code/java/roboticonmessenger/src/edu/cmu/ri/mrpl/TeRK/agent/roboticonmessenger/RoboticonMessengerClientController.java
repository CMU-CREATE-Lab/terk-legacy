package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger;

import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.agent.peerinformation.PeerInfoClientService;
import edu.cmu.ri.mrpl.TeRK.agent.peerinformation.PeerInfoClientServiceImpl;
import edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging.RoboticonMessagingClientService;
import edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging.RoboticonMessagingClientServiceImpl;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceCreator;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RoboticonMessengerClientController
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerClientController.class);

   private final RoboticonMessagingClientService roboticonMessagingService;
   private final PeerInfoClientService peerInfoService;

   RoboticonMessengerClientController(final String peerIdentifier, final TerkUserPrx terkUserPrx, final TerkCommunicator terkCommunicator)
      {
      final ServiceManager serviceManager = new IceServiceManager(peerIdentifier,
                                                                  terkUserPrx,
                                                                  terkCommunicator,
                                                                  new MyServiceFactory());

      roboticonMessagingService = (RoboticonMessagingClientService)serviceManager.getServiceByTypeId(RoboticonMessagingClientService.TYPE_ID);
      peerInfoService = (PeerInfoClientService)serviceManager.getServiceByTypeId(PeerInfoClientService.TYPE_ID);
      }

   RoboticonMessagingClientService getRoboticonMessengerService()
      {
      return roboticonMessagingService;
      }

   PeerInfoClientService getPeerInfoService()
      {
      return peerInfoService;
      }

   boolean isRoboticonMessagingSupported()
      {
      return roboticonMessagingService != null;
      }

   boolean isPeerInfoSupported()
      {
      return peerInfoService != null;
      }

   private final class MyServiceFactory implements IceServiceFactory
      {
      private final Map<String, IceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, IceServiceCreator>();

      MyServiceFactory()
         {
         typeIdToServiceCreatorsMap.put(RoboticonMessagingClientService.TYPE_ID,
                                        new IceServiceCreator()
                                        {
                                        public Service create(final ObjectPrx serviceProxy)
                                           {
                                           return new RoboticonMessagingClientServiceImpl(RoboticonMessagingClientServicePrxHelper.uncheckedCast(serviceProxy));
                                           }
                                        });
         typeIdToServiceCreatorsMap.put(PeerInfoClientService.TYPE_ID,
                                        new IceServiceCreator()
                                        {
                                        public Service create(final ObjectPrx serviceProxy)
                                           {
                                           return new PeerInfoClientServiceImpl(PeerInfoClientServicePrxHelper.uncheckedCast(serviceProxy));
                                           }
                                        });
         }

      public final Service createService(final String serviceTypeId, final ObjectPrx serviceProxy)
         {
         if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("RoboticonMessengerClientController.createService(" + serviceTypeId + ")");
               }
            return typeIdToServiceCreatorsMap.get(serviceTypeId).create(serviceProxy);
            }
         return null;
         }
      }
   }
