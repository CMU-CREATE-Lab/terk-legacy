package edu.cmu.ri.createlab.TeRK.thermistor;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.mrpl.TeRK.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseThermistorServiceImpl extends BaseDeviceControllingService implements ThermistorService
   {
   private final ThermistorUnitConversionStrategy unitConversionStrategy;

   public BaseThermistorServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);

      final String deviceId = propertyManager.getProperty(ThermistorService.PROPERTY_NAME_THERMISTOR_DEVICE_ID);
      unitConversionStrategy = ThermistorUnitConversionStrategyFinder.getInstance().lookup(deviceId);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }

   public final Double getCelsiusTemperature(final int id)
      {
      final Integer rawValue = getThermistorValue(id);
      if (rawValue != null)
         {
         return convertToCelsius(rawValue);
         }
      return null;
      }

   public final Double convertToCelsius(final Integer rawValue)
      {
      if (unitConversionStrategy != null)
         {
         return unitConversionStrategy.convertToCelsius(rawValue);
         }
      throw new UnsupportedOperationException("Method not supported since no ThermistorUnitConversionStrategy is defined for this implementation.");
      }

   public final boolean isUnitConversionSupported()
      {
      return (unitConversionStrategy != null);
      }
   }