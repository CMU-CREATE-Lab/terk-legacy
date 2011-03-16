package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.led.SimpleLEDCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultSimpleLEDServiceServantHelper extends AbstractServiceServant implements SimpleLEDServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultSimpleLEDServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_LED_DEVICE_COUNT);
      this.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MIN_INTENSITY);
      this.setReadOnlyProperty(SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, HummingbirdConstants.SIMPLE_LED_DEVICE_MAX_INTENSITY);
      }

   public int[] execute(final SimpleLEDCommand command)
      {
      return hummingbirdProxy.setLEDs(command.mask, command.intensities);
      }
   }