package edu.cmu.ri.createlab.TeRK.robot.finch;

import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchProxyProvider
   {
   private final SerialDeviceConnectivityManager serialDeviceConnectivityManager;

   FinchProxyProvider(final SerialDeviceConnectivityManager serialDeviceConnectivityManager)
      {
      this.serialDeviceConnectivityManager = serialDeviceConnectivityManager;
      }

   public FinchProxy getFinchProxy()
      {
      return (FinchProxy)serialDeviceConnectivityManager.getSerialDeviceProxy();
      }
   }