package edu.cmu.ri.createlab.TeRK.digitalout;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface DigitalOutService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::DigitalOutController";

   void execute(final boolean[] digitalOutMask, final boolean[] digitalOutValues);

   /** Turns the given digital output(s) on.  Turns all outputs on if no <code>digitalOutIds</code> are specified. */
   void setOutputsOn(final int... digitalOutIds);

   /** Turns the given digital output(s) off.  Turns all outputs off if no <code>digitalOutIds</code> are specified. */
   void setOutputsOff(final int... digitalOutIds);

   /**
    * Sets the given digital output(s) to the given state.  Sets all outputs to the given state if no
    * <code>digitalOutIds</code> are specified.
    */
   void setOutputs(final boolean state, final int... digitalOutIds);
   }
