package edu.cmu.ri.createlab.TeRK.led;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.led.SimpleLEDCommand;
import edu.cmu.ri.mrpl.TeRK.led.SimpleLEDServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SimpleLEDServiceIceImpl extends BaseSimpleLEDServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(SimpleLEDServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   private final SimpleLEDServicePrx proxy;

   public static SimpleLEDServiceIceImpl create(final SimpleLEDServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("SimpleLEDServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new SimpleLEDServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private SimpleLEDServiceIceImpl(final SimpleLEDServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   protected int[] execute(final boolean[] mask, final int[] intensities)
      {
      return proxy.execute(new SimpleLEDCommand(mask, intensities));
      }
   }