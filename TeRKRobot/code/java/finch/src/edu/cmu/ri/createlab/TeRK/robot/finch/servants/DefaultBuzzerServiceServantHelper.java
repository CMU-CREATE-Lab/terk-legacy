package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.buzzer.BuzzerCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultBuzzerServiceServantHelper extends AbstractServiceServant implements BuzzerServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultBuzzerServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.BUZZER_DEVICE_COUNT);
      this.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MIN_DURATION, FinchConstants.BUZZER_DEVICE_MIN_DURATION);
      this.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MAX_DURATION, FinchConstants.BUZZER_DEVICE_MAX_DURATION);
      this.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MIN_FREQUENCY, FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY);
      this.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MAX_FREQUENCY, FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY);
      }

   public void execute(final int id, final BuzzerCommand command)
      {
      if (command != null)
         {
         finchProxy.playBuzzerTone(command.frequency, command.durationInMilliseconds);
         }
      }
   }