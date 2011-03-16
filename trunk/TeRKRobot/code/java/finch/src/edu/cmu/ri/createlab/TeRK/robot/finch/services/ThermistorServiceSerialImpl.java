package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.thermistor.BaseThermistorServiceImpl;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ThermistorServiceSerialImpl extends BaseThermistorServiceImpl
   {
   static ThermistorServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.THERMISTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_THERMISTOR_DEVICE_ID, FinchConstants.THERMISTOR_DEVICE_ID);

      return new ThermistorServiceSerialImpl(finchProxy,
                                             basicPropertyManager,
                                             FinchConstants.THERMISTOR_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private ThermistorServiceSerialImpl(final FinchProxy finchProxy,
                                       final PropertyManager propertyManager,
                                       final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public Integer getThermistorValue(final int id)
      {
      return finchProxy.getThermistor(id);
      }
   }