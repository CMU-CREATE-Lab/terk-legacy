package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.finch.BaseFinchServiceImpl;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FinchServiceSerialImpl extends BaseFinchServiceImpl
   {
   static FinchServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.FINCH_DEVICE_COUNT);

      return new FinchServiceSerialImpl(finchProxy,
                                        basicPropertyManager,
                                        FinchConstants.FINCH_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private FinchServiceSerialImpl(final FinchProxy finchProxy,
                                  final PropertyManager propertyManager,
                                  final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public FinchState getFinchState()
      {
      return finchProxy.getState();
      }

   public void emergencyStop()
      {
      finchProxy.emergencyStop();
      }
   }