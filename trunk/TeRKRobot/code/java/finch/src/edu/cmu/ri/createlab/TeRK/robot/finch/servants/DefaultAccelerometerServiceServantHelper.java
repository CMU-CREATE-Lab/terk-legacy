package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultAccelerometerServiceServantHelper extends AbstractServiceServant implements AccelerometerServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultAccelerometerServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.ACCELEROMETER_DEVICE_COUNT);
      this.setReadOnlyProperty(AccelerometerService.PROPERTY_NAME_ACCELEROMETER_DEVICE_ID, FinchConstants.ACCELEROMETER_DEVICE_ID);
      }

   /**
    * Returns the finch's accelerometer state.  Since the finch only has a single accelerometer, the <code>id</code> is
    * ignored.
    */
   public AccelerometerState getState(final int id)
      {
      final edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState state = finchProxy.getAccelerometerState();
      if (state != null)
         {
         return new AccelerometerState(state.getX(),
                                       state.getY(),
                                       state.getZ());
         }
      return null;
      }

   public AccelerometerState[] getStates()
      {
      final AccelerometerState state = getState(0);
      if (state != null)
         {
         return new AccelerometerState[]{state};
         }
      return null;
      }
   }