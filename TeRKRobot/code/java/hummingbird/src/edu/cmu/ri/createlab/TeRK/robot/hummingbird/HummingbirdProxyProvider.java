package edu.cmu.ri.createlab.TeRK.robot.hummingbird;

import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdProxyProvider
   {
   private final SerialDeviceConnectivityManager serialDeviceConnectivityManager;

   HummingbirdProxyProvider(final SerialDeviceConnectivityManager serialDeviceConnectivityManager)
      {
      this.serialDeviceConnectivityManager = serialDeviceConnectivityManager;
      }

   public HummingbirdProxy getHummingbirdProxy()
      {
      return (HummingbirdProxy)serialDeviceConnectivityManager.getSerialDeviceProxy();
      }
   }