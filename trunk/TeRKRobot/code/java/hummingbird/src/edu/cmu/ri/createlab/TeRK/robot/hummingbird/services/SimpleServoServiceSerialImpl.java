package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.TeRK.servo.BaseSimpleServoServiceImpl;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleServoServiceSerialImpl extends BaseSimpleServoServiceImpl
   {
   static SimpleServoServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MIN_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MIN_POSITION);
      basicPropertyManager.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MAX_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MAX_POSITION);

      return new SimpleServoServiceSerialImpl(hummingbirdProxy,
                                              basicPropertyManager,
                                              HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private SimpleServoServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                        final PropertyManager propertyManager,
                                        final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] positions)
      {
      return hummingbirdProxy.setServoPositions(mask, positions);
      }
   }