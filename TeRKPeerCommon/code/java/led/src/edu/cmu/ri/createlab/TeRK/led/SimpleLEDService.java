package edu.cmu.ri.createlab.TeRK.led;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.OperationExecutor;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SimpleLEDService extends Service, DeviceController, OperationExecutor
   {
   String TYPE_ID = "::TeRK::led::SimpleLEDService";

   String OPERATION_NAME_SET_INTENSITY = "setIntensity";
   String PARAMETER_NAME_INTENSITY = "intensity";

   String PROPERTY_NAME_MIN_INTENSITY = TYPE_ID + "::min-intensity";
   String PROPERTY_NAME_MAX_INTENSITY = TYPE_ID + "::max-intensity";

   /** Sets the LED specified by the given <code>id</code> to the given <code>intensity</code>. */
   void set(final int id, final int intensity);

   /**
    * Sets the given LEDs to off.  Sets all LEDs off if no index is specified.
    *
    * @param ids the list of values in the range [0,getDeviceCount()) indicating which LEDs should be set off
    */
   void setOff(int... ids);

   /** Returns the current LED intensities. */
   int[] getIntensities();
   }