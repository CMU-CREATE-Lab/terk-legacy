package edu.cmu.ri.createlab.TeRK.analogin;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.mrpl.TeRK.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseAnalogInputsServiceImpl extends BaseDeviceControllingService implements AnalogInputsService
   {
   public BaseAnalogInputsServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }

   public abstract short[] getAnalogInputValues();

   public final short getAnalogInputValue(final int analogInputPortId)
      {
      // check id
      if (analogInputPortId < 0 || analogInputPortId >= getDeviceCount())
         {
         throw new IndexOutOfBoundsException("The analog input port id " + analogInputPortId + " is not valid.  Ids must be within the range [0," + getDeviceCount() + ")");
         }

      final short[] values = getAnalogInputValues();
      if (values != null)
         {
         return values[analogInputPortId];
         }
      throw new NullPointerException("Failed to retrieve analog input value for port " + analogInputPortId + " since the returned state is null");
      }
   }