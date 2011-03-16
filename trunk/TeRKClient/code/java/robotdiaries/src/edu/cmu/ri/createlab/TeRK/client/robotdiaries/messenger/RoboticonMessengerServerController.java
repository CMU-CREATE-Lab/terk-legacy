package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger;

import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.messaging.RoboticonMessagingServerService;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.messaging.RoboticonMessagingServerServiceImpl;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.client.components.services.peerinfo.PeerInfoServerService;
import edu.cmu.ri.mrpl.TeRK.client.components.services.peerinfo.PeerInfoServerServiceImpl;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoServerServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingServerServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceCreator;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RoboticonMessengerServerController
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerServerController.class);

   private final RoboticonMessagingServerService roboticonMessagingService;
   private final PeerInfoServerService peerInfoServerService;

   RoboticonMessengerServerController(final String peerIdentifier, final TerkUserPrx terkUserPrx, final TerkCommunicator terkCommunicator)
      {
      final ServiceManager serviceManager = new IceServiceManager(peerIdentifier,
                                                                  terkUserPrx,
                                                                  terkCommunicator,
                                                                  new RoboticonMessengerServiceFactory());

      roboticonMessagingService = (RoboticonMessagingServerService)serviceManager.getServiceByTypeId(RoboticonMessagingServerService.TYPE_ID);
      peerInfoServerService = (PeerInfoServerService)serviceManager.getServiceByTypeId(PeerInfoServerService.TYPE_ID);
      }

   RoboticonMessagingServerService getRoboticonMessengerService()
      {
      return roboticonMessagingService;
      }

   public PeerInfoServerService getPeerInfoService()
      {
      return peerInfoServerService;
      }

   boolean isRoboticonMessagingSupported()
      {
      return roboticonMessagingService != null;
      }

   boolean isPeerInfoSupported()
      {
      return peerInfoServerService != null;
      }

   private final class RoboticonMessengerServiceFactory implements IceServiceFactory
      {
      private final Map<String, IceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, IceServiceCreator>();

      RoboticonMessengerServiceFactory()
         {
         typeIdToServiceCreatorsMap.put(RoboticonMessagingServerService.TYPE_ID,
                                        new IceServiceCreator()
                                        {
                                        public Service create(final ObjectPrx serviceProxy)
                                           {
                                           return new RoboticonMessagingServerServiceImpl(RoboticonMessagingServerServicePrxHelper.uncheckedCast(serviceProxy));
                                           }
                                        });
         typeIdToServiceCreatorsMap.put(PeerInfoServerService.TYPE_ID,
                                        new IceServiceCreator()
                                        {
                                        public Service create(final ObjectPrx serviceProxy)
                                           {
                                           return new PeerInfoServerServiceImpl(PeerInfoServerServicePrxHelper.uncheckedCast(serviceProxy));
                                           }
                                        });
         }

      public final Service createService(final String serviceTypeId, final ObjectPrx serviceProxy)
         {
         if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("RoboticonMessengerServiceFactory.createService(" + serviceTypeId + ")");
               }
            return typeIdToServiceCreatorsMap.get(serviceTypeId).create(serviceProxy);
            }
         return null;
         }
      }
   }
