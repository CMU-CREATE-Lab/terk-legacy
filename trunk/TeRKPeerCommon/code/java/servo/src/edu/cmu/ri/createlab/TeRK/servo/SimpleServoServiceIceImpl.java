package edu.cmu.ri.createlab.TeRK.servo;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.servo.SimpleServoCommand;
import edu.cmu.ri.mrpl.TeRK.servo.SimpleServoServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimpleServoServiceIceImpl extends BaseSimpleServoServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(SimpleServoServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static SimpleServoServiceIceImpl create(final SimpleServoServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("SimpleServoServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new SimpleServoServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final SimpleServoServicePrx proxy;

   private SimpleServoServiceIceImpl(final SimpleServoServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   protected int[] execute(final boolean[] mask, final int[] positions)
      {
      return proxy.execute(new SimpleServoCommand(mask, positions));
      }
   }