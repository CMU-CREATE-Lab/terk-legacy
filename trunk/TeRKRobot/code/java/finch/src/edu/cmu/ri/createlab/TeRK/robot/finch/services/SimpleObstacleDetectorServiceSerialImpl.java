package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.obstacle.BaseSimpleObstacleDetectorServiceImpl;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleObstacleDetectorServiceSerialImpl extends BaseSimpleObstacleDetectorServiceImpl
   {
   static SimpleObstacleDetectorServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT);

      return new SimpleObstacleDetectorServiceSerialImpl(finchProxy,
                                                         basicPropertyManager,
                                                         FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private SimpleObstacleDetectorServiceSerialImpl(final FinchProxy finchProxy,
                                                   final PropertyManager propertyManager,
                                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public Boolean isObstacleDetected(final int id)
      {
      return finchProxy.isObstacleDetected(id);
      }

   public boolean[] areObstaclesDetected()
      {
      return finchProxy.areObstaclesDetected();
      }
   }