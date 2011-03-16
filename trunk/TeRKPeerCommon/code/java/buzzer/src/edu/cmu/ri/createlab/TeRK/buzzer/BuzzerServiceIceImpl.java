package edu.cmu.ri.createlab.TeRK.buzzer;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.buzzer.BuzzerCommand;
import edu.cmu.ri.mrpl.TeRK.buzzer.BuzzerServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class BuzzerServiceIceImpl extends BaseBuzzerServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(BuzzerServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static BuzzerServiceIceImpl create(final BuzzerServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("BuzzerServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new BuzzerServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final BuzzerServicePrx proxy;

   private BuzzerServiceIceImpl(final BuzzerServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public void playTone(final int id, final int frequency, final int durationInMilliseconds)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("BuzzerServiceIceImpl.playTone(" + id + "," + frequency + "," + durationInMilliseconds + ")");
         }

      try
         {
         proxy.execute(id, new BuzzerCommand(frequency, durationInMilliseconds));
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to set buzzer " + id + " to frequency " + frequency + " for duration " + durationInMilliseconds, e);
         }
      }
   }