package edu.cmu.ri.createlab.TeRK.digitalout;

import java.util.Arrays;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.DigitalOutCommand;
import edu.cmu.ri.mrpl.TeRK.DigitalOutCommandException;
import edu.cmu.ri.mrpl.TeRK.DigitalOutControllerPrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DigitalOutServiceImpl extends ServicePropertyManager implements DigitalOutService
   {
   private static final Logger LOG = Logger.getLogger(DigitalOutServiceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 8;

   private final DigitalOutControllerPrx proxy;
   private final boolean[] on;
   private final boolean[] off;
   private final boolean[] maskAllOn;
   private final int deviceCount;

   public DigitalOutServiceImpl(final DigitalOutControllerPrx proxy)
      {
      super(proxy);
      this.proxy = proxy;

      // try to get the device count from the property
      final Integer numDevices = getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      this.deviceCount = (numDevices == null) ? DEFAULT_DEVICE_COUNT : numDevices;

      this.maskAllOn = new boolean[deviceCount];
      this.on = new boolean[deviceCount];
      this.off = new boolean[deviceCount];
      Arrays.fill(maskAllOn, true);
      Arrays.fill(on, true);
      Arrays.fill(off, false);
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   private void execute(final DigitalOutCommand command)
      {
      try
         {
         proxy.execute(command);
         }
      catch (DigitalOutCommandException e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting a command.", e);
         }
      }

   public void execute(final boolean[] mask, final boolean[] values)
      {
      execute(new DigitalOutCommand(mask, values));
      }

   public void setOutputsOn(final int... digitalOutIds)
      {
      setOutputs(true, digitalOutIds);
      }

   public void setOutputsOff(final int... digitalOutIds)
      {
      setOutputs(false, digitalOutIds);
      }

   public void setOutputs(final boolean state, final int... digitalOutIds)
      {
      final boolean[] mask;
      if (digitalOutIds == null || digitalOutIds.length == 0)
         {
         mask = maskAllOn;
         }
      else
         {
         mask = new boolean[deviceCount];
         Arrays.fill(mask, false);
         for (final int i : digitalOutIds)
            {
            mask[i] = true;
            }
         }

      execute(mask, (state ? on : off));
      }

   public int getDeviceCount()
      {
      return deviceCount;
      }
   }
