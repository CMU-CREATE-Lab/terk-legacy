package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultSimpleObstacleDetectorServiceServantHelper extends AbstractServiceServant implements SimpleObstacleDetectorServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultSimpleObstacleDetectorServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT);
      }

   public boolean isObstacleDetected(final int id)
      {
      final boolean[] isDetected = areObstaclesDetected();

      return (isDetected != null) &&
             (isDetected.length > 0) &&
             (id >= 0) &&
             (id < isDetected.length) &&
             isDetected[id];
      }

   public boolean[] areObstaclesDetected()
      {
      return finchProxy.areObstaclesDetected();
      }
   }