package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.BaseSpeedControllableMotorServiceImpl;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SpeedControllableMotorServiceSerialImpl extends BaseSpeedControllableMotorServiceImpl
   {
   static SpeedControllableMotorServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MIN_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED);
      basicPropertyManager.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED);

      return new SpeedControllableMotorServiceSerialImpl(hummingbirdProxy,
                                                         basicPropertyManager,
                                                         HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private SpeedControllableMotorServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                                   final PropertyManager propertyManager,
                                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] speeds)
      {
      return hummingbirdProxy.setVibrationMotorSpeeds(mask, speeds);
      }
   }