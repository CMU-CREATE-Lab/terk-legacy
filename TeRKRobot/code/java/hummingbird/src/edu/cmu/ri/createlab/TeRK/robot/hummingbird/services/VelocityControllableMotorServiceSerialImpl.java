package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.BaseVelocityControllableMotorServiceImpl;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class VelocityControllableMotorServiceSerialImpl extends BaseVelocityControllableMotorServiceImpl
   {
   static VelocityControllableMotorServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MIN_VELOCITY);
      basicPropertyManager.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MAX_VELOCITY);

      return new VelocityControllableMotorServiceSerialImpl(hummingbirdProxy,
                                                            basicPropertyManager,
                                                            HummingbirdConstants.MOTOR_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private VelocityControllableMotorServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                                      final PropertyManager propertyManager,
                                                      final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] velocities)
      {
      return hummingbirdProxy.setMotorVelocities(mask, velocities);
      }
   }