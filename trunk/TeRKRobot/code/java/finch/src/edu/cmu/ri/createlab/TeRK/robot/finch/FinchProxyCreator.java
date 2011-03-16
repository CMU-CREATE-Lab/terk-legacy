package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxyCreator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchProxyCreator implements SerialDeviceProxyCreator
   {
   public SerialDeviceProxy createSerialDeviceProxy(final String serialPortName)
      {
      return FinchProxy.create(serialPortName);
      }
   }