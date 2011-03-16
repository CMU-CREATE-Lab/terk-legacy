package edu.cmu.ri.createlab.TeRK.motor;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.OperationExecutor;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SpeedControllableMotorService extends Service, DeviceController, OperationExecutor
   {
   String TYPE_ID = "::TeRK::motor::SpeedControllableMotorService";

   String OPERATION_NAME_SET_SPEED = "setSpeed";
   String PARAMETER_NAME_SPEED = "speed";

   String PROPERTY_NAME_MIN_SPEED = TYPE_ID + "::min-speed";
   String PROPERTY_NAME_MAX_SPEED = TYPE_ID + "::max-speed";

   /** Sets the given motor to a speed equal to the absolute value of the given <code>speed</code>. */
   void setSpeed(final int motorId, final int speed);

   /** Sets the motor speeds to the given <code>speeds</code>. */
   void setSpeeds(final int[] speeds);

   /**
    * Sets the motor(s) specified by the given <code>motorMask</code> to the speeds specified by the given
    * <code>speeds</code>.
    */
   void setSpeeds(final boolean[] motorMask, final int[] speeds);

   /** Stops the given motor(s) (specified by motor ID).  Halts all the motors if none are specified. */
   void stop(final int... motorIds);

   /** Returns the current motor speeds. */
   int[] getSpeeds();
   }