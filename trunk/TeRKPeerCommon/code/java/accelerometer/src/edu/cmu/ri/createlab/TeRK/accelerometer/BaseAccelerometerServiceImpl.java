package edu.cmu.ri.createlab.TeRK.accelerometer;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.mrpl.TeRK.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseAccelerometerServiceImpl extends BaseDeviceControllingService implements AccelerometerService
   {
   private final AccelerometerUnitConversionStrategy unitConversionStrategy;

   public BaseAccelerometerServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);

      final String deviceId = propertyManager.getProperty(AccelerometerService.PROPERTY_NAME_ACCELEROMETER_DEVICE_ID);
      unitConversionStrategy = AccelerometerUnitConversionStrategyFinder.getInstance().lookup(deviceId);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }

   public final AccelerometerGs getAccelerometerGs(final int id)
      {
      final AccelerometerState state = getAccelerometerState(id);
      if (state != null)
         {
         return convertToGs(state);
         }
      return null;
      }

   public final AccelerometerGs convertToGs(final AccelerometerState state)
      {
      if (unitConversionStrategy != null)
         {
         return unitConversionStrategy.convert(state);
         }
      throw new UnsupportedOperationException("Method not supported since no AccelerometerUnitConversionStrategy is defined for this implementation.");
      }

   public final boolean isUnitConversionSupported()
      {
      return (unitConversionStrategy != null);
      }

   public final AccelerometerUnitConversionStrategy getAccelerometerUnitConversionStrategy()
      {
      return unitConversionStrategy;
      }
   }