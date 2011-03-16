package edu.cmu.ri.createlab.TeRK.digitalin;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface DigitalInService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::DigitalInController";

   /** Returns the state of the digital inputs. */
   boolean[] getDigitalInState();

   /**
    * Returns the value of the given port id.
    *
    * @throws NullPointerException if the retrieved DigitalInState is null
    * @throws IndexOutOfBoundsException if the <code>digitalInputPortId</code> specifies an invalid port
    */
   boolean getDigitalInputValue(final int digitalInputPortId);
   }
