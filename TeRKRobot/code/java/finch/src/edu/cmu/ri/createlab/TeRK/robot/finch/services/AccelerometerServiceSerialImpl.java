package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.accelerometer.BaseAccelerometerServiceImpl;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AccelerometerServiceSerialImpl extends BaseAccelerometerServiceImpl
   {
   static AccelerometerServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.ACCELEROMETER_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(AccelerometerService.PROPERTY_NAME_ACCELEROMETER_DEVICE_ID, FinchConstants.ACCELEROMETER_DEVICE_ID);

      return new AccelerometerServiceSerialImpl(finchProxy,
                                                basicPropertyManager,
                                                FinchConstants.ACCELEROMETER_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private AccelerometerServiceSerialImpl(final FinchProxy finchProxy,
                                          final PropertyManager propertyManager,
                                          final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public AccelerometerState getAccelerometerState(final int id)
      {
      return finchProxy.getAccelerometerState();
      }
   }