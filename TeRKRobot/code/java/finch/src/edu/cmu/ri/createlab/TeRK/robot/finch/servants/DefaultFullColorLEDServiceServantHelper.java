package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.color.ColorUtils;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;
import edu.cmu.ri.mrpl.TeRK.led.FullColorLEDCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultFullColorLEDServiceServantHelper extends AbstractServiceServant implements FullColorLEDServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultFullColorLEDServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.FULL_COLOR_LED_DEVICE_COUNT);
      this.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY);
      this.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY);
      }

   public RGBColor[] execute(final FullColorLEDCommand command)
      {
      final Color[] colors;

      if (command.mask[0])
         {
         colors = new Color[]{finchProxy.setFullColorLED(ColorUtils.convert(command.colors[0]))};
         }
      else
         {
         colors = new Color[]{finchProxy.getState().getFullColorLED()};
         }

      return ColorUtils.convert(colors);
      }
   }