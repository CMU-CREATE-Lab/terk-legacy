package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.util.concurrent.atomic.AtomicBoolean;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategy;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceFactory;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimulatedFinchConnectionStrategy extends ConnectionStrategy
   {
   private final AtomicBoolean isConnected = new AtomicBoolean(false);
   private final SerialDeviceServiceFactory serviceFactory = new FinchServiceFactory();
   private ServiceManager serviceManager = null;

   public synchronized ServiceManager getServiceManager()
      {
      return serviceManager;
      }

   public synchronized boolean isConnected()
      {
      return isConnected.get();
      }

   public boolean isConnecting()
      {
      return false;
      }

   public synchronized void connect()
      {
      notifyListenersOfAttemptingConnectionEvent();
      serviceManager = new FinchServiceManager(FinchProxy.create("SimulatedSerialPort"), serviceFactory);
      isConnected.set(true);
      notifyListenersOfConnectionEvent();
      }

   public void cancelConnect()
      {
      // do nothing since we don't need support for cancelling
      }

   public synchronized void disconnect()
      {
      notifyListenersOfAttemptingDisconnectionEvent();
      isConnected.set(false);
      serviceManager = null;
      notifyListenersOfDisconnectionEvent();
      }

   public void prepareForShutdown()
      {
      disconnect();
      }
   }
