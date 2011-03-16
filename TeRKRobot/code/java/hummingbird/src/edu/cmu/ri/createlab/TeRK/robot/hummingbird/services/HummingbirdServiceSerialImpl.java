package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.hummingbird.BaseHummingbirdServiceImpl;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdServiceSerialImpl extends BaseHummingbirdServiceImpl
   {
   static HummingbirdServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.HUMMINGBIRD_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, HummingbirdConstants.HARDWARE_TYPE);
      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, HummingbirdConstants.HARDWARE_VERSION);

      return new HummingbirdServiceSerialImpl(hummingbirdProxy,
                                              basicPropertyManager,
                                              HummingbirdConstants.HUMMINGBIRD_DEVICE_COUNT);
      }

   private final HummingbirdProxy hummingbirdProxy;

   private HummingbirdServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                        final PropertyManager propertyManager,
                                        final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   public HummingbirdState getHummingbirdState()
      {
      return hummingbirdProxy.getState();
      }

   public void emergencyStop()
      {
      hummingbirdProxy.emergencyStop();
      }
   }
