package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.led.BaseSimpleLEDServiceImpl;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleLEDServiceSerialImpl extends BaseSimpleLEDServiceImpl
   {
   static SimpleLEDServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY);
      basicPropertyManager.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY);

      return new SimpleLEDServiceSerialImpl(hummingbirdProxy,
                                            basicPropertyManager,
                                            HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private SimpleLEDServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                      final PropertyManager propertyManager,
                                      final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   protected int[] execute(final boolean[] mask, final int[] intensities)
      {
      return hummingbirdProxy.setLEDs(mask, intensities);
      }
   }
