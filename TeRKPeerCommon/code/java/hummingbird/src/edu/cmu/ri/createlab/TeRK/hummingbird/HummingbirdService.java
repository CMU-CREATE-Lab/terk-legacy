package edu.cmu.ri.createlab.TeRK.hummingbird;

import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdService extends Service
   {
   String TYPE_ID = "::TeRK::hummingbird::HummingbirdService";

   /** Returns the hummingbird's current state. */
   HummingbirdState getHummingbirdState();

   /** Sets all motors, vibration motors, and LEDs to off. */
   void emergencyStop();
   }