package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.AnalogInState;
import edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultAnalogInputsServiceServantHelper extends AbstractServiceServant implements AnalogInputsServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultAnalogInputsServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT);
      }

   public AnalogInState getState()
      {
      final HummingbirdState hummingbirdState = HummingbirdStateConverter.convert(hummingbirdProxy.getState());
      return (hummingbirdState == null ? null : hummingbirdState.analogIn);
      }
   }