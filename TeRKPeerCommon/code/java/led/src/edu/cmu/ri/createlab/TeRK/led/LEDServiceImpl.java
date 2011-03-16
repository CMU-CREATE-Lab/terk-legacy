package edu.cmu.ri.createlab.TeRK.led;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.LEDCommand;
import edu.cmu.ri.mrpl.TeRK.LEDCommandException;
import edu.cmu.ri.mrpl.TeRK.LEDControllerPrx;
import org.apache.log4j.Logger;

/**
 * @author Michael Safyan (michaelsafyan@wustl.edu)
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class LEDServiceImpl extends ServicePropertyManager implements LEDService
   {
   private static final Logger LOG = Logger.getLogger(LEDServiceImpl.class);
   private static final int DEFAULT_DEVICE_COUNT = 8;// be safe and assume an 8-LED qwerk

   private final int deviceCount;
   private final LEDControllerPrx proxy;
   private final Map<LEDMode, LEDMode[]> ledModeToModeArrayMap;

   public LEDServiceImpl(final LEDControllerPrx proxy)
      {
      super(proxy);
      this.proxy = proxy;

      // try to get the device count from the property
      final Integer numDevices = getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      this.deviceCount = (numDevices == null) ? DEFAULT_DEVICE_COUNT : numDevices;

      //initialize mode arrays
      final LEDMode[] allOn = new LEDMode[this.deviceCount];
      final LEDMode[] allOff = new LEDMode[this.deviceCount];
      final LEDMode[] allBlink = new LEDMode[this.deviceCount];
      Arrays.fill(allOn, LEDMode.On);
      Arrays.fill(allOff, LEDMode.Off);
      Arrays.fill(allBlink, LEDMode.Blinking);

      //initialize mode mapping
      final Map<LEDMode, LEDMode[]> ledModeToModeArrayMapTemp = new HashMap<LEDMode, LEDMode[]>();
      ledModeToModeArrayMapTemp.put(LEDMode.On, allOn);
      ledModeToModeArrayMapTemp.put(LEDMode.Off, allOff);
      ledModeToModeArrayMapTemp.put(LEDMode.Blinking, allBlink);
      ledModeToModeArrayMap = Collections.unmodifiableMap(ledModeToModeArrayMapTemp);
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void execute(final boolean[] mask, final LEDMode[] modes)
      {
      try
         {
         // convert the LEDMode into an Ice LEDMode
         final edu.cmu.ri.mrpl.TeRK.LEDMode[] newModes = new edu.cmu.ri.mrpl.TeRK.LEDMode[modes.length];
         for (int i = 0; i < modes.length; i++)
            {
            switch (modes[i])
               {
               case Blinking:
                  newModes[i] = edu.cmu.ri.mrpl.TeRK.LEDMode.LEDBlinking;
                  break;
               case Off:
                  newModes[i] = edu.cmu.ri.mrpl.TeRK.LEDMode.LEDOff;
                  break;
               case On:
                  newModes[i] = edu.cmu.ri.mrpl.TeRK.LEDMode.LEDOn;
                  break;
               default:
                  LOG.warn("Unexpected LEDMode: " + modes[i]);
                  newModes[i] = null;
               }
            }
         proxy.execute(new LEDCommand(mask, newModes));
         }
      catch (LEDCommandException e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting a command.", e);
         }
      }

   public void set(final LEDMode mode, final int... ledIds)
      {
      final LEDMode[] modearray = ledModeToModeArrayMap.get(mode);
      final boolean[] mask = new boolean[deviceCount];
      Arrays.fill(mask, false);
      for (final int i : ledIds)
         {
         mask[i] = true;
         }
      execute(mask, modearray);
      }

   public void setOn(final int... ledIds)
      {
      set(LEDMode.On, ledIds);
      }

   public void setOff(final int... ledIds)
      {
      set(LEDMode.Off, ledIds);
      }

   public void setBlinking(final int... ledIds)
      {
      set(LEDMode.Blinking, ledIds);
      }

   /** Reports the number of LEDs. */
   public int getDeviceCount()
      {
      return deviceCount;
      }
   }