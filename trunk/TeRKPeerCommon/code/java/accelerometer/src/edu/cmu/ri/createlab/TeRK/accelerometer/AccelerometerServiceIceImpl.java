package edu.cmu.ri.createlab.TeRK.accelerometer;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.accelerometer.AccelerometerServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class AccelerometerServiceIceImpl extends BaseAccelerometerServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AccelerometerServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static AccelerometerServiceIceImpl create(final AccelerometerServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("AccelerometerServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new AccelerometerServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final AccelerometerServicePrx proxy;

   private AccelerometerServiceIceImpl(final AccelerometerServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public AccelerometerState getAccelerometerState(final int id)
      {
      try
         {
         final edu.cmu.ri.mrpl.TeRK.accelerometer.AccelerometerState accelerometerState = proxy.getState(id);
         if (accelerometerState != null)
            {
            return new AccelerometerState(accelerometerState.x,
                                          accelerometerState.y,
                                          accelerometerState.z);
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to get the accelerometer state", e);
         }

      return null;
      }
   }