package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.color.ColorUtils;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;
import edu.cmu.ri.mrpl.TeRK.led.FullColorLEDCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultFullColorLEDServiceServantHelper extends AbstractServiceServant implements FullColorLEDServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultFullColorLEDServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.FULL_COLOR_LED_DEVICE_COUNT);
      this.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY);
      this.setReadOnlyProperty(FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY);
      }

   public RGBColor[] execute(final FullColorLEDCommand command)
      {
      final Color[] colors = hummingbirdProxy.setFullColorLEDs(command.mask,
                                                               ColorUtils.convert(command.colors));

      return ColorUtils.convert(colors);
      }
   }