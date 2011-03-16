package edu.cmu.ri.createlab.TeRK.serial.services;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.TeRK.services.AbstractServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SerialDeviceServiceManager extends AbstractServiceManager
   {
   private static final Logger LOG = Logger.getLogger(SerialDeviceServiceManager.class);

   private final SerialDeviceProxy serialDeviceProxy;
   private final SerialDeviceServiceFactory serviceFactory;
   private final Map<String, Service> loadedServices = Collections.synchronizedMap(new HashMap<String, Service>());

   public SerialDeviceServiceManager(final SerialDeviceProxy serialDeviceProxy, final SerialDeviceServiceFactory serviceFactory)
      {
      this.serialDeviceProxy = serialDeviceProxy;
      this.serviceFactory = serviceFactory;
      }

   protected final Service loadService(final String typeId)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("SerialDeviceServiceManager.loadService(" + typeId + ")");
         }

      if (serviceFactory != null)
         {
         Service service;

         synchronized (loadedServices)
            {
            // see whether we've already loaded this service
            service = loadedServices.get(typeId);

            // load the service
            if (service == null)
               {
               LOG.debug("SerialDeviceServiceManager.loadService() needs to load the [" + typeId + "] service");

               service = serviceFactory.createService(typeId, serialDeviceProxy);

               // cache this service so future calls won't have to create it
               loadedServices.put(typeId, service);
               }
            }

         return service;
         }

      return null;
      }
   }
