package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.buzzer.BaseBuzzerServiceImpl;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BuzzerServiceSerialImpl extends BaseBuzzerServiceImpl
   {
   static BuzzerServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.BUZZER_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MIN_DURATION, FinchConstants.BUZZER_DEVICE_MIN_DURATION);
      basicPropertyManager.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MAX_DURATION, FinchConstants.BUZZER_DEVICE_MAX_DURATION);
      basicPropertyManager.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MIN_FREQUENCY, FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY);
      basicPropertyManager.setReadOnlyProperty(BuzzerService.PROPERTY_NAME_MAX_FREQUENCY, FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY);

      return new BuzzerServiceSerialImpl(finchProxy,
                                         basicPropertyManager,
                                         FinchConstants.BUZZER_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private BuzzerServiceSerialImpl(final FinchProxy finchProxy,
                                   final PropertyManager propertyManager,
                                   final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public void playTone(final int id, final int frequency, final int durationInMilliseconds)
      {
      finchProxy.playBuzzerTone(frequency, durationInMilliseconds);
      }
   }