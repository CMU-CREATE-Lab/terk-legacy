package edu.cmu.ri.mrpl.TeRK.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import Ice.Identity;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class IceServiceManager extends AbstractServiceManager
   {
   private static final Logger LOG = Logger.getLogger(IceServiceManager.class);

   private final String peerIdentifier;
   private final TerkCommunicator terkCommunicator;
   private final IceServiceFactory serviceFactory;
   private final Map<String, Identity> supportedServices;
   private final Map<String, Service> loadedServices = Collections.synchronizedMap(new HashMap<String, Service>());

   public IceServiceManager(final String peerIdentifier,
                            final TerkUserPrx peerObjectPrx,
                            final TerkCommunicator terkCommunicator,
                            final IceServiceFactory serviceFactory)
      {
      this.peerIdentifier = peerIdentifier;
      this.terkCommunicator = terkCommunicator;
      this.serviceFactory = serviceFactory;

      // get the collection of supported services from the peer's proxy
      final Map<String, Identity> tempSupportedServices = new HashMap<String, Identity>();

      Map<String, Identity> typeIdToProxyIdentityMap;
      try
         {
         typeIdToProxyIdentityMap = peerObjectPrx.getSupportedServices();
         }
      catch (Exception e)
         {
         LOG.warn("Exception while calling getSupportedServices()--maybe this TerkUser doesn't support the new API?", e);

         typeIdToProxyIdentityMap = null;
         }

      if (typeIdToProxyIdentityMap != null)
         {
         tempSupportedServices.putAll(typeIdToProxyIdentityMap);
         }
      supportedServices = Collections.unmodifiableMap(tempSupportedServices);

      // register the supported services with the superclass
      registerSupportedServices(supportedServices.keySet());
      }

   protected Service loadService(final String typeId)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("IceServiceManager.loadService(" + typeId + ")");
         }
      if (serviceFactory != null)
         {
         // get the identity for this service--this is a simple way to both check whether this service is supported (it
         // will be null if not supported) and to go ahead and get the identity in case we need to load the service
         final Identity servicePrivateProxyIdentity = supportedServices.get(typeId);

         // see whether the service is supported
         if (servicePrivateProxyIdentity != null)
            {
            Service service;

            synchronized (loadedServices)
               {
               // see whether we've already loaded this service
               service = loadedServices.get(typeId);

               // load the service
               if (service == null)
                  {
                  LOG.debug("IceServiceManager.loadService() needs to load the [" + typeId + "] service");

                  try
                     {
                     // get the proxy for this service
                     final ObjectPrx servicePrx = terkCommunicator.getPeerProxy(peerIdentifier, servicePrivateProxyIdentity);
                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("IceServiceManager.loadService() obtained service proxy [" + (servicePrx == null ? null : servicePrx.ice_toString()) + "]");
                        }

                     // create the service
                     service = serviceFactory.createService(typeId, servicePrx);

                     // cache this service so future calls won't have to create it
                     loadedServices.put(typeId, service);
                     }
                  catch (PeerAccessException e)
                     {
                     LOG.error("PeerAccessException while trying to get the peer proxy", e);
                     }
                  catch (InvalidIdentityException e)
                     {
                     LOG.error("InvalidIdentityException while trying to get the peer proxies", e);
                     }
                  }
               }

            return service;
            }
         }
      return null;
      }
   }
