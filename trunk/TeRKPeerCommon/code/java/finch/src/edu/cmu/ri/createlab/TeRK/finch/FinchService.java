package edu.cmu.ri.createlab.TeRK.finch;

import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchService extends Service
   {
   String TYPE_ID = "::TeRK::finch::FinchService";

   /** Returns the finch's current state. */
   FinchState getFinchState();

   /** Sets all motors, vibration motors, and LEDs to off. */
   void emergencyStop();
   }