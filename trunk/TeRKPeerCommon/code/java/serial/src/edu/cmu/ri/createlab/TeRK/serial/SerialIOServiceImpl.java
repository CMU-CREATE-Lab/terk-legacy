package edu.cmu.ri.createlab.TeRK.serial;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.SerialIOServicePrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialIOServiceImpl extends ServicePropertyManager implements SerialIOService
   {
   private final SerialIOServicePrx proxy;
   private final Map<String, SerialPort> serialPorts = Collections.synchronizedMap(new HashMap<String, SerialPort>());

   public SerialIOServiceImpl(final SerialIOServicePrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public SerialPort getSerialPort(final String serialPortDeviceName)
      {
      synchronized (serialPorts)
         {
         SerialPort port = serialPorts.get(serialPortDeviceName);
         if (port == null)
            {
            port = new SerialPortImpl(serialPortDeviceName, proxy);
            serialPorts.put(serialPortDeviceName, port);
            }
         return port;
         }
      }
   }
