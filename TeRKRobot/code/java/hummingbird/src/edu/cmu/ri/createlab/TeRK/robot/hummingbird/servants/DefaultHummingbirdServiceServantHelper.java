package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultHummingbirdServiceServantHelper extends AbstractServiceServant implements HummingbirdServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultHummingbirdServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, HummingbirdConstants.HARDWARE_TYPE);
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, HummingbirdConstants.HARDWARE_VERSION);
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.HUMMINGBIRD_DEVICE_COUNT);
      }

   public HummingbirdState getState()
      {
      return HummingbirdStateConverter.convert(hummingbirdProxy.getState());
      }

   public void emergencyStop()
      {
      hummingbirdProxy.emergencyStop();
      }
   }
