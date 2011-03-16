package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.led.BaseFullColorLEDServiceImpl;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDServiceSerialImpl extends BaseFullColorLEDServiceImpl
   {
   static FullColorLEDServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.FULL_COLOR_LED_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY);
      basicPropertyManager.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY);

      return new FullColorLEDServiceSerialImpl(finchProxy,
                                               basicPropertyManager,
                                               FinchConstants.FULL_COLOR_LED_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private FullColorLEDServiceSerialImpl(final FinchProxy finchProxy,
                                         final PropertyManager propertyManager,
                                         final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public Color[] set(final boolean[] mask, final Color[] colors)
      {
      if (mask != null && colors != null)
         {
         if (mask.length > 0 && colors.length > 0)
            {
            if (mask[0])
               {
               return new Color[]{finchProxy.setFullColorLED(colors[0])};
               }
            }
         }
      return null;
      }
   }