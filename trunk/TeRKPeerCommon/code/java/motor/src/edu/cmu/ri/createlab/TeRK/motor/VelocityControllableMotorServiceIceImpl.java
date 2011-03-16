package edu.cmu.ri.createlab.TeRK.motor;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class VelocityControllableMotorServiceIceImpl extends BaseVelocityControllableMotorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(VelocityControllableMotorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static VelocityControllableMotorServiceIceImpl create(final VelocityControllableMotorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("VelocityControllableMotorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new VelocityControllableMotorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final VelocityControllableMotorServicePrx proxy;

   private VelocityControllableMotorServiceIceImpl(final VelocityControllableMotorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   protected int[] execute(final boolean[] mask, final int[] velocities)
      {
      return proxy.execute(new VelocityControllableMotorCommand(mask, velocities));
      }
   }