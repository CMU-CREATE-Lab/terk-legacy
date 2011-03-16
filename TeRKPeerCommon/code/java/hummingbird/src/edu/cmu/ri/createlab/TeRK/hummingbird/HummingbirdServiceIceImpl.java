package edu.cmu.ri.createlab.TeRK.hummingbird;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HummingbirdServiceIceImpl extends BaseHummingbirdServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static HummingbirdServiceIceImpl create(final HummingbirdServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("HummingbirdServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new HummingbirdServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final HummingbirdServicePrx proxy;

   private HummingbirdServiceIceImpl(final HummingbirdServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public HummingbirdState getHummingbirdState()
      {
      return new TerkHummingbirdState(proxy.getState());
      }

   public void emergencyStop()
      {
      proxy.emergencyStop();
      }

   private static final class TerkHummingbirdState implements HummingbirdState
      {
      private static final String EOL = System.getProperty("line.separator", "\n");

      private final edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdState iceState;

      private TerkHummingbirdState(final edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdState iceState)
         {
         this.iceState = iceState;
         }

      public Color[] getFullColorLEDs()
         {
         if (iceState.fullColorLedColors != null)
            {
            final Color[] colors = new Color[iceState.fullColorLedColors.length];

            for (int i = 0; i < colors.length; i++)
               {
               colors[i] = new Color(iceState.fullColorLedColors[i].red,
                                     iceState.fullColorLedColors[i].green,
                                     iceState.fullColorLedColors[i].blue);
               }
            return colors;
            }
         return null;
         }

      public int[] getLedIntensities()
         {
         return (iceState.ledIntensities == null ? null : iceState.ledIntensities.clone());
         }

      public int[] getServoPositions()
         {
         return (iceState.servoPositions == null ? null : iceState.servoPositions.clone());
         }

      public int[] getMotorVelocities()
         {
         return (iceState.motorVelocities == null ? null : iceState.motorVelocities.clone());
         }

      public int[] getVibrationMotorSpeeds()
         {
         return (iceState.vibrationMotorSpeeds == null ? null : iceState.vibrationMotorSpeeds.clone());
         }

      public short[] getAnalogInputValues()
         {
         if (iceState.analogIn != null && iceState.analogIn.analogInValues != null)
            {
            final short[] values = new short[iceState.analogIn.analogInValues.length];
            for (int i = 0; i < values.length; i++)
               {
               values[i] = iceState.analogIn.analogInValues[i];
               }

            return values;
            }
         return null;
         }

      public boolean equals(final Object o)
         {
         if (this == o)
            {
            return true;
            }
         if (o == null || getClass() != o.getClass())
            {
            return false;
            }

         final TerkHummingbirdState that = (TerkHummingbirdState)o;

         if (iceState != null ? !iceState.equals(that.iceState) : that.iceState != null)
            {
            return false;
            }

         return true;
         }

      public int hashCode()
         {
         return (iceState != null ? iceState.hashCode() : 0);
         }

      public String toString()
         {
         final StringBuffer s = new StringBuffer("HummingbirdState" + EOL);
         for (int i = 0; i < iceState.fullColorLedColors.length; i++)
            {
            s.append("   Orb ").append(i + 1).append(":        (").append(iceState.fullColorLedColors[i].red).append(",").append(iceState.fullColorLedColors[i].green).append(",").append(iceState.fullColorLedColors[i].blue).append(")").append(EOL);
            }
         for (int i = 0; i < iceState.ledIntensities.length; i++)
            {
            s.append("   LED ").append(i + 1).append(":        ").append(iceState.ledIntensities[i]).append(EOL);
            }
         for (int i = 0; i < iceState.servoPositions.length; i++)
            {
            s.append("   Servo ").append(i + 1).append(":      ").append(iceState.servoPositions[i]).append(EOL);
            }
         for (int i = 0; i < iceState.motorVelocities.length; i++)
            {
            s.append("   Motor ").append(i + 1).append(":      ").append(iceState.motorVelocities[i]).append(EOL);
            }
         for (int i = 0; i < iceState.vibrationMotorSpeeds.length; i++)
            {
            s.append("   Vibe Motor ").append(i + 1).append(": ").append(iceState.vibrationMotorSpeeds[i]).append(EOL);
            }
         for (int i = 0; i < iceState.analogIn.analogInValues.length; i++)
            {
            s.append("   Sensor ").append(i + 1).append(":     ").append(iceState.analogIn.analogInValues[i]).append(EOL);
            }

         return s.toString();
         }
      }
   }
