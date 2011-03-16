package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.BaseVelocityControllableMotorServiceImpl;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class VelocityControllableMotorServiceSerialImpl extends BaseVelocityControllableMotorServiceImpl
   {
   static VelocityControllableMotorServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MOTOR_DEVICE_ID, FinchConstants.VELOCITY_CONTROLLABLE_MOTOR_DEVICE_ID);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, FinchConstants.MOTOR_DEVICE_MIN_VELOCITY);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, FinchConstants.MOTOR_DEVICE_MAX_VELOCITY);

      return new VelocityControllableMotorServiceSerialImpl(finchProxy,
                                                            basicPropertyManager,
                                                            FinchConstants.MOTOR_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private VelocityControllableMotorServiceSerialImpl(final FinchProxy finchProxy,
                                                      final PropertyManager propertyManager,
                                                      final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] velocities)
      {
      return finchProxy.setMotorVelocities(mask, velocities);
      }
   }