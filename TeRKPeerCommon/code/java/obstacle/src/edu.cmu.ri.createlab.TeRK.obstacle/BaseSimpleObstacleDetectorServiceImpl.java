package edu.cmu.ri.createlab.TeRK.obstacle;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.mrpl.TeRK.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseSimpleObstacleDetectorServiceImpl extends BaseDeviceControllingService implements SimpleObstacleDetectorService
   {
   public BaseSimpleObstacleDetectorServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }
   }