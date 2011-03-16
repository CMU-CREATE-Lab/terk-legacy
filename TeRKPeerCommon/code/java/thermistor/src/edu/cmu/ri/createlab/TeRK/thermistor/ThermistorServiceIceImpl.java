package edu.cmu.ri.createlab.TeRK.thermistor;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.thermistor.ThermistorServicePrx;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ThermistorServiceIceImpl extends BaseThermistorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(ThermistorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static ThermistorServiceIceImpl create(final ThermistorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("ThermistorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new ThermistorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final ThermistorServicePrx proxy;

   private ThermistorServiceIceImpl(final ThermistorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public Integer getThermistorValue(final int id)
      {
      try
         {
         return proxy.getValue(id);
         }
      catch (Exception e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Exception while trying to get the value of thermistor [" + id + "]", e);
            }
         }

      return null;
      }
   }