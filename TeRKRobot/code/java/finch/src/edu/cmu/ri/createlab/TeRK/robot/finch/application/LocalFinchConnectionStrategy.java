package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategy;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchProxyCreator;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceFactory;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionEventListener;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionState;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerImpl;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LocalFinchConnectionStrategy extends ConnectionStrategy
   {
   private static final Logger LOG = Logger.getLogger(LocalFinchConnectionStrategy.class);

   private ServiceManager serviceManager = null;
   private final SerialDeviceServiceFactory serviceFactory = new FinchServiceFactory();
   private final SerialDeviceConnectivityManager serialDeviceConnectivityManager = new SerialDeviceConnectivityManagerImpl(new FinchProxyCreator());

   public LocalFinchConnectionStrategy()
      {
      serialDeviceConnectivityManager.addConnectionEventListener(
            new SerialDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final SerialDeviceConnectionState oldState, final SerialDeviceConnectionState newState, final String serialPortName)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("LocalFinchConnectionStrategy.handleConnectionStateChange(): OLD [" + oldState.getStateName() + "]  NEW [" + newState.getStateName() + "]");
                  }
               switch (newState)
                  {
                  case CONNECTED:
                     serviceManager = new FinchServiceManager(serialDeviceConnectivityManager.getSerialDeviceProxy(), serviceFactory);
                     notifyListenersOfConnectionEvent();
                     break;
                  case DISCONNECTED:
                     serviceManager = null;
                     notifyListenersOfDisconnectionEvent();
                     break;
                  case SCANNING:
                     notifyListenersOfAttemptingConnectionEvent();
                     break;
                  default:
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error("Unexpected SerialDeviceConnectionState [" + newState + "]");
                        }
                  }
               }
            }
      );
      }

   public boolean isConnected()
      {
      return SerialDeviceConnectionState.CONNECTED.equals(serialDeviceConnectivityManager.getConnectionState());
      }

   public boolean isConnecting()
      {
      return SerialDeviceConnectionState.SCANNING.equals(serialDeviceConnectivityManager.getConnectionState());
      }

   public ServiceManager getServiceManager()
      {
      return serviceManager;
      }

   public void connect()
      {
      serialDeviceConnectivityManager.scanAndConnect();
      }

   public void cancelConnect()
      {
      serialDeviceConnectivityManager.cancelScanning();
      }

   public void disconnect()
      {
      LOG.debug("LocalFinchConnectionStrategy.disconnect()");
      notifyListenersOfAttemptingDisconnectionEvent();
      serialDeviceConnectivityManager.disconnect();
      }

   public void prepareForShutdown()
      {
      LOG.debug("LocalFinchConnectionStrategy.prepareForShutdown()");
      disconnect();
      }
   }
