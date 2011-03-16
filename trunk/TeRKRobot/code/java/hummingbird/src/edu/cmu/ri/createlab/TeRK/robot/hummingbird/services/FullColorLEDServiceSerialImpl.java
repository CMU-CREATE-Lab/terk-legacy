package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.led.BaseFullColorLEDServiceImpl;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDServiceSerialImpl extends BaseFullColorLEDServiceImpl
   {
   static FullColorLEDServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      final int deviceCount = HummingbirdConstants.FULL_COLOR_LED_DEVICE_COUNT;

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY);
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY);

      return new FullColorLEDServiceSerialImpl(hummingbirdProxy,
                                               basicPropertyManager,
                                               deviceCount);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private FullColorLEDServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                         final PropertyManager propertyManager,
                                         final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   public Color[] set(final boolean[] mask, final Color[] colors)
      {
      return hummingbirdProxy.setFullColorLEDs(mask, colors);
      }
   }