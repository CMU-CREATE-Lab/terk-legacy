package edu.cmu.ri.createlab.TeRK.obstacle;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.obstacle.SimpleObstacleDetectorServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimpleObstacleDetectorServiceIceImpl extends BaseSimpleObstacleDetectorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(SimpleObstacleDetectorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static SimpleObstacleDetectorServiceIceImpl create(final SimpleObstacleDetectorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("SimpleObstacleDetectorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new SimpleObstacleDetectorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final SimpleObstacleDetectorServicePrx proxy;

   private SimpleObstacleDetectorServiceIceImpl(final SimpleObstacleDetectorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public Boolean isObstacleDetected(final int id)
      {
      try
         {
         return proxy.isObstacleDetected(id);
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to get the obstacle detector state", e);
         }

      return null;
      }

   public boolean[] areObstaclesDetected()
      {
      try
         {
         return proxy.areObstaclesDetected();
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to get the obstacle detector states", e);
         }

      return null;
      }
   }