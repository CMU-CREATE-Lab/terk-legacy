package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.finch.FinchServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.finch.FinchState;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultFinchServiceServantHelper extends AbstractServiceServant implements FinchServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultFinchServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, FinchConstants.HARDWARE_TYPE);
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, FinchConstants.HARDWARE_VERSION);
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.FINCH_DEVICE_COUNT);
      }

   public FinchState getState()
      {
      return FinchStateConverter.convert(finchProxy.getState());
      }

   public void emergencyStop()
      {
      finchProxy.emergencyStop();
      }
   }