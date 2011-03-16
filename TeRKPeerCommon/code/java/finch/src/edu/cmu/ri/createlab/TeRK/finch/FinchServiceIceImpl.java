package edu.cmu.ri.createlab.TeRK.finch;

import java.awt.Color;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.finch.FinchServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchServiceIceImpl extends BaseFinchServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(FinchServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   public static FinchServiceIceImpl create(final FinchServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("FinchServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new FinchServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private final FinchServicePrx proxy;

   private FinchServiceIceImpl(final FinchServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public FinchState getFinchState()
      {
      return new TerkFinchState(proxy.getState());
      }

   public void emergencyStop()
      {
      proxy.emergencyStop();
      }

   private static final class TerkFinchState implements FinchState
      {
      private static final String EOL = System.getProperty("line.separator", "\n");

      private final edu.cmu.ri.mrpl.TeRK.finch.FinchState iceState;

      private TerkFinchState(final edu.cmu.ri.mrpl.TeRK.finch.FinchState iceState)
         {
         this.iceState = iceState;
         }

      public Color getFullColorLED()
         {
         if (iceState.fullColorLedColor != null)
            {
            return new Color(iceState.fullColorLedColor.red,
                             iceState.fullColorLedColor.green,
                             iceState.fullColorLedColor.blue);
            }
         return null;
         }

      public AccelerometerState getAccelerometerState()
         {
         if (iceState.accelerometerValues != null)
            {
            return new AccelerometerState(iceState.accelerometerValues.x,
                                          iceState.accelerometerValues.y,
                                          iceState.accelerometerValues.z);
            }
         return null;
         }

      public PositionControllableMotorState[] getPositionControllableMotorStates()
         {
         final PositionControllableMotorState[] states = new PositionControllableMotorState[iceState.positionControllableMotorStates.length];

         for (int i = 0; i < iceState.positionControllableMotorStates.length; i++)
            {
            states[i] = new PositionControllableMotorState(iceState.positionControllableMotorStates[i].currentPosition,
                                                           iceState.positionControllableMotorStates[i].specifiedPosition,
                                                           iceState.positionControllableMotorStates[i].specifiedSpeed);
            }

         return states;
         }

      public int[] getMotorVelocities()
         {
         return iceState.velocityControllableMotorStates.clone();
         }

      public int getThermistor()
         {
         return iceState.thermistorValue;
         }

      public int getLeftPhotoresistor()
         {
         return iceState.photoresistors[0];
         }

      public int getRightPhotoresistor()
         {
         return iceState.photoresistors[1];
         }

      public boolean isLeftObstacleDetected()
         {
         return iceState.isObstacleDetected[0];
         }

      public boolean isRightObstacleDetected()
         {
         return iceState.isObstacleDetected[1];
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

         final TerkFinchState that = (TerkFinchState)o;

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
         final StringBuffer s = new StringBuffer("FinchState" + EOL);
         s.append("   Orb").append(":                     (").append(iceState.fullColorLedColor.red).append(",").append(iceState.fullColorLedColor.green).append(",").append(iceState.fullColorLedColor.blue).append(")").append(EOL);
         s.append("   Accelerometer").append(":           (").append(iceState.accelerometerValues.x).append(",").append(iceState.accelerometerValues.y).append(",").append(iceState.accelerometerValues.z).append(")").append(EOL);
         for (int i = 0; i < iceState.positionControllableMotorStates.length; i++)
            {
            s.append("   Motor ").append(i).append(":").append(EOL);
            s.append("      Current Position").append(":     ").append(iceState.positionControllableMotorStates[i].currentPosition).append(EOL);
            s.append("      Specified Position").append(":   ").append(iceState.positionControllableMotorStates[i].specifiedPosition).append(EOL);
            s.append("      Specified Speed").append(":      ").append(iceState.positionControllableMotorStates[i].specifiedSpeed).append(EOL);
            s.append("      Specified Velocity").append(":   ").append(iceState.velocityControllableMotorStates[i]).append(EOL);
            }
         s.append("   Thermistor").append(":               ").append(iceState.thermistorValue).append(EOL);
         s.append("   Photoresistor").append(":           (").append(iceState.photoresistors[0]).append(",").append(iceState.photoresistors[1]).append(")").append(EOL);
         s.append("   Obstacle").append(":                (").append(iceState.isObstacleDetected[0]).append(",").append(iceState.isObstacleDetected[1]).append(")").append(EOL);

         return s.toString();
         }
      }
   }