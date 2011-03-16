package edu.cmu.ri.createlab.TeRK.motor;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.motor.SpeedControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor.SpeedControllableMotorServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SpeedControllableMotorServiceIceImpl extends BaseSpeedControllableMotorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(SpeedControllableMotorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static SpeedControllableMotorServiceIceImpl create(final SpeedControllableMotorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("SpeedControllableMotorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new SpeedControllableMotorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final SpeedControllableMotorServicePrx proxy;

   private SpeedControllableMotorServiceIceImpl(final SpeedControllableMotorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   protected int[] execute(final boolean[] mask, final int[] speeds)
      {
      return proxy.execute(new SpeedControllableMotorCommand(mask, speeds));
      }
   }