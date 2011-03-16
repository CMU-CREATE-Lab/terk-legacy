using System;
using System.Diagnostics;
using System.Collections;
using System.Collections.Generic;
using Ice;
using peer;
using Exception=Ice.Exception;
using TeRK.communications;

namespace TeRK.services
   {
   public class TerkServiceProvider : ServiceProvider
      {
      private readonly string peerUserId;
      private readonly TerkCommunicator communicator;
      private readonly Hashtable supportedServices = new Hashtable();
      private readonly Hashtable loadedServices = Hashtable.Synchronized(new Hashtable());

      public TerkServiceProvider(string peerUserId, TerkUserPrx peerObjectPrx, TerkCommunicator communicator)
         {
         this.peerUserId = peerUserId;
         this.communicator = communicator;

         // get the collection of supported services from the peer's proxy
         //
         // Ugly hack to handle peers who don't support the getSupportedServices() method in the API.  This hack assumes
         // that the peer is a Qwerk, but that's ok for now since all non-qwerk TerkUsers already do support the new API.
         // TODO: remove this ugliness once we can assume that all TerkUsers support the new API.
         ProxyTypeIdToIdentityMap typeIdToProxyIdentityMap;
         try
            {
            typeIdToProxyIdentityMap = peerObjectPrx.getSupportedServices();
            }
         catch (Exception e)
            {
            Trace.TraceError("Exception while calling getSupportedServices()--maybe this TerkUser doesn't support the new API?", e);

            // assume we're dealing with a qwerk
            typeIdToProxyIdentityMap = QwerkPrxHelper.uncheckedCast(peerObjectPrx).getCommandControllerTypeToProxyIdentityMap();
            }

         if (typeIdToProxyIdentityMap != null)
            {
            foreach (DictionaryEntry entry in typeIdToProxyIdentityMap)
               {
               string key = (string) entry.Key;
               Identity value = (Identity) entry.Value;
               supportedServices.Add(key, value);
               }
            }
         }

      public override Service getService(string serviceTypeId, ServiceFactory serviceFactory)
         {
         if (serviceFactory != null)
            {
            // get the identity for this service--this is a simple way to both check whether this service is supported (it
            // be null if not supported) and to go ahead and get the identity in case we need to load the service
            Identity servicePrivateProxyIdentity = (Identity) supportedServices[serviceTypeId];

            if (servicePrivateProxyIdentity != null)
               {
               Service service;

               lock (loadedServices.SyncRoot)
                  {
                  // see whether we've already loaded this service
                  service = (Service) loadedServices[serviceTypeId];

                  // load the service
                  if (service == null)
                     {
                     Trace.TraceInformation("DEBUG: TerkServiceProvider.getService() needs to load the [" + serviceTypeId + "] service");

                     try
                        {
                        // get the proxy for this service
                        ObjectPrx servicePrx = communicator.getPeerProxy(peerUserId, servicePrivateProxyIdentity);

                        // create the service
                        service = serviceFactory.createService(serviceTypeId, servicePrx);

                        // cache this service so future calls won't have to create it
                        loadedServices.Add(serviceTypeId, service);
                        }
                     catch (PeerAccessException e)
                        {
                            Trace.TraceError("PeerAccessException while trying to get the peer proxy: {0}", e.reason);
                        }
                     catch (InvalidIdentityException e)
                        {
                            Trace.TraceError("InvalidIdentityException while trying to get the peer proxies: {0}", e.reason);
                        }
                     }
                  }

               return service;
               }
            }
         return null;
         }

      public override bool isServiceSupported(string serviceTypeId)
         {
         if (serviceTypeId == null)
            {
            return false;
            }
         return supportedServices.ContainsKey(serviceTypeId);
         }

      public override IList<string> getTypeIdsOfSupportedServices()
         {
         IList<string> typeIds = new List<string>(supportedServices.Keys.Count);
         foreach (string typeId in supportedServices.Keys)
            {
            typeIds.Add(typeId);
            }
         return typeIds;
         }
      }
   }