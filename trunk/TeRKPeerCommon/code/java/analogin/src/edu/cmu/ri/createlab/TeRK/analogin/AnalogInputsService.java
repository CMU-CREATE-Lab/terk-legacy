package edu.cmu.ri.createlab.TeRK.analogin;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface AnalogInputsService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::AnalogInController";

   /**
    * Returns the value of all analog inputs or <code>null</code> if the values could not be retrieved.
    */
   short[] getAnalogInputValues();

   /**
    * Returns the value of the given port id.
    *
    * @throws NullPointerException if values could not be retrieved
    * @throws IndexOutOfBoundsException if the <code>analogInputPortId</code> specifies an invalid port
    */
   short getAnalogInputValue(final int analogInputPortId);
   }
