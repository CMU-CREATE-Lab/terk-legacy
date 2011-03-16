package edu.cmu.ri.createlab.TeRK.analogin;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.AnalogInControllerPrx;
import edu.cmu.ri.mrpl.TeRK.AnalogInState;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class AnalogInputsServiceIceImpl extends BaseAnalogInputsServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AnalogInputsServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static AnalogInputsServiceIceImpl create(final AnalogInControllerPrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("AnalogInputsServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new AnalogInputsServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final AnalogInControllerPrx proxy;

   private AnalogInputsServiceIceImpl(final AnalogInControllerPrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public short[] getAnalogInputValues()
      {
      final AnalogInState state = proxy.getState();
      if (state != null)
         {
         return state.analogInValues;
         }
      return null;
      }
   }
