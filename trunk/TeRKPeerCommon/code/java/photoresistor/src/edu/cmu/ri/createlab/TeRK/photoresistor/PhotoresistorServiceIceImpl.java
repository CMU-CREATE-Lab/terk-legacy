package edu.cmu.ri.createlab.TeRK.photoresistor;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.photoresistor.PhotoresistorServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PhotoresistorServiceIceImpl extends BasePhotoresistorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(PhotoresistorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static PhotoresistorServiceIceImpl create(final PhotoresistorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("PhotoresistorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new PhotoresistorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final PhotoresistorServicePrx proxy;

   private PhotoresistorServiceIceImpl(final PhotoresistorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public Integer getPhotoresistorValue(final int id)
      {
      try
         {
         return proxy.getValue(id);
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to get the photoresistor state", e);
         }

      return null;
      }

   public int[] getPhotoresistorValues()
      {
      try
         {
         return proxy.getValues();
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to get the photoresistor state", e);
         }

      return null;
      }
   }