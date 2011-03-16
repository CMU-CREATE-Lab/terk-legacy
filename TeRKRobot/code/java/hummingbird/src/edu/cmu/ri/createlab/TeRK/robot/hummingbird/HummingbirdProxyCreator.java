package edu.cmu.ri.createlab.TeRK.robot.hummingbird;

import java.io.IOException;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxyCreator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdProxyCreator implements SerialDeviceProxyCreator
   {
   public SerialDeviceProxy createSerialDeviceProxy(final String serialPortName) throws IOException, SerialPortException
      {
      return HummingbirdProxy.create(serialPortName);
      }
   }
