package edu.cmu.ri.createlab.TeRK.digitalin;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.DigitalInControllerPrx;
import edu.cmu.ri.mrpl.TeRK.DigitalInState;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DigitalInServiceImpl extends ServicePropertyManager implements DigitalInService
   {
   private static final int DEFAULT_DEVICE_COUNT = 8;

   private final DigitalInControllerPrx digitalInProxy;
   private final int deviceCount;

   public DigitalInServiceImpl(final DigitalInControllerPrx digitalInProxy)
      {
      super(digitalInProxy);
      this.digitalInProxy = digitalInProxy;

      // try to get the device count from the property
      final Integer numDevices = getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      this.deviceCount = (numDevices == null) ? DEFAULT_DEVICE_COUNT : numDevices;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public boolean[] getDigitalInState()
      {
      final DigitalInState digitalInState = digitalInProxy.getState();
      if (digitalInState != null)
         {
         return digitalInState.digitalInStates;
         }
      return null;
      }

   public boolean getDigitalInputValue(final int digitalInputPortId)
      {
      // check id
      if (digitalInputPortId < 0 || digitalInputPortId >= deviceCount)
         {
         throw new IndexOutOfBoundsException("The digital input port id " + digitalInputPortId + " is not valid.  Ids must be within the range [0," + deviceCount + ")");
         }

      final boolean[] state = getDigitalInState();
      if (state != null)
         {
         return state[digitalInputPortId];
         }
      throw new NullPointerException("Failed to retrieve digital input value for port " + digitalInputPortId + " since the DigitalInState is null");
      }

   public int getDeviceCount()
      {
      return deviceCount;
      }
   }
