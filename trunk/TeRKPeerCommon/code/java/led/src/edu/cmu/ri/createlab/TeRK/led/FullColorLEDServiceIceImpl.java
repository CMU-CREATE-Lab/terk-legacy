package edu.cmu.ri.createlab.TeRK.led;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.color.ColorUtils;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;
import edu.cmu.ri.mrpl.TeRK.led.FullColorLEDCommand;
import edu.cmu.ri.mrpl.TeRK.led.FullColorLEDServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class FullColorLEDServiceIceImpl extends BaseFullColorLEDServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(FullColorLEDServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static FullColorLEDServiceIceImpl create(final FullColorLEDServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("FullColorLEDServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new FullColorLEDServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final FullColorLEDServicePrx proxy;

   private FullColorLEDServiceIceImpl(final FullColorLEDServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public Color[] set(final boolean[] mask, final Color[] colors)
      {
      final RGBColor[] inputRGBColors = ColorUtils.convert(colors);
      final RGBColor[] outputRGBColors = proxy.execute(new FullColorLEDCommand(mask, inputRGBColors));

      if (outputRGBColors != null)
         {
         return ColorUtils.convert(outputRGBColors);
         }
      return null;
      }
   }