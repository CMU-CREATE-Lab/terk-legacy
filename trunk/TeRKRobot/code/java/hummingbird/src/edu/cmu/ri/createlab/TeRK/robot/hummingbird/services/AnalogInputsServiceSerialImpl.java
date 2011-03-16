package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.analogin.BaseAnalogInputsServiceImpl;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AnalogInputsServiceSerialImpl extends BaseAnalogInputsServiceImpl
   {
   static AnalogInputsServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();
      final int deviceCount = HummingbirdConstants.ANALOG_INPUT_DEVICE_COUNT;

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, deviceCount);

      return new AnalogInputsServiceSerialImpl(hummingbirdProxy,
                                               basicPropertyManager,
                                               deviceCount);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private AnalogInputsServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                         final PropertyManager propertyManager,
                                         final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   public short[] getAnalogInputValues()
      {
      final HummingbirdState hummingbirdState = hummingbirdProxy.getState();
      if (hummingbirdState != null)
         {
         return hummingbirdState.getAnalogInputValues();
         }

      return null;
      }
   }