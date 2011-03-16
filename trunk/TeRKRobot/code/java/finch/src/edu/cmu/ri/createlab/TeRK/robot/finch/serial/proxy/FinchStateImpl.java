package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import java.awt.Color;
import java.util.Arrays;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.mrpl.util.ByteUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FinchState</code> represents the internal state of the finch.  Instances of this class are immutable.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FinchStateImpl implements FinchState
   {
   private static final Logger LOG = Logger.getLogger(FinchStateImpl.class);

   private static final String EOL = System.getProperty("line.separator", "\n");

   private final Orb orb;
   private final AccelerometerState accelerometerState;
   private final PositionControllableMotorState[] motorStates = new PositionControllableMotorState[FinchConstants.MOTOR_DEVICE_COUNT];
   private final int[] motorVelocities = new int[FinchConstants.MOTOR_DEVICE_COUNT];
   private final int thermistor;
   private final int[] photoresistors = new int[FinchConstants.PHOTORESISTOR_DEVICE_COUNT];
   private final boolean[] obstacleSensors = new boolean[FinchConstants.SIMPLE_OBSTACLE_SENSOR_DEVICE_COUNT];

   /**
    * Creates a new <code>FinchState</code> using the given state array.  Returns <code>null</code> if the given
    * array is null or of a size other than {@link FinchConstants#SIZE_IN_BYTES_OF_STATE_ARRAY}.
    */
   static FinchStateImpl create(final byte[] state)
      {
      if (state == null)
         {
         LOG.error("Invalid state array.  The state array cannot be null.");
         return null;
         }
      if (state.length != FinchConstants.SIZE_IN_BYTES_OF_STATE_ARRAY)
         {
         LOG.error("Invalid state array.  Array must be exactly " + FinchConstants.SIZE_IN_BYTES_OF_STATE_ARRAY + " bytes.  Received array was " + state.length + " byte(s).");
         return null;
         }

      return new FinchStateImpl(state);
      }

   private FinchStateImpl(final byte[] state)
      {
      // read state positions 0-2 for the full-color LED
      orb = new Orb(ByteUtils.unsignedByteToInt(state[0]),
                    ByteUtils.unsignedByteToInt(state[1]),
                    ByteUtils.unsignedByteToInt(state[2]));

      final int leftCurrentPosition = ByteUtils.bytesToShort(state[3], state[4]);
      final int rightCurrentPosition = ByteUtils.bytesToShort(state[5], state[6]);
      final int leftSpecifiedPosition = ByteUtils.bytesToShort(state[7], state[8]);
      final int rightSpecifiedPosition = ByteUtils.bytesToShort(state[9], state[10]);
      motorVelocities[0] = (int)(state[11]);
      motorVelocities[1] = (int)(state[12]);

      motorStates[0] = new PositionControllableMotorState(leftCurrentPosition,
                                                          leftSpecifiedPosition,
                                                          motorVelocities[0]);
      motorStates[1] = new PositionControllableMotorState(rightCurrentPosition,
                                                          rightSpecifiedPosition,
                                                          motorVelocities[1]);

      thermistor = ByteUtils.unsignedByteToInt(state[13]);
      photoresistors[0] = ByteUtils.unsignedByteToInt(state[14]);
      photoresistors[1] = ByteUtils.unsignedByteToInt(state[15]);

      accelerometerState = new AccelerometerState(ByteUtils.unsignedByteToInt(state[16]),
                                                  ByteUtils.unsignedByteToInt(state[17]),
                                                  ByteUtils.unsignedByteToInt(state[18]));

      obstacleSensors[0] = ByteUtils.unsignedByteToInt(state[19]) == 1;
      obstacleSensors[1] = ByteUtils.unsignedByteToInt(state[20]) == 1;
      }

   public Color getFullColorLED()
      {
      return orb.getColor();
      }

   public AccelerometerState getAccelerometerState()
      {
      return accelerometerState;
      }

   public PositionControllableMotorState[] getPositionControllableMotorStates()
      {
      final PositionControllableMotorState[] motorStatesClone = new PositionControllableMotorState[motorStates.length];

      for (int i = 0; i < motorStates.length; i++)
         {
         motorStatesClone[i] = new PositionControllableMotorState(motorStates[i]);
         }

      return motorStatesClone;
      }

   public int[] getMotorVelocities()
      {
      return motorVelocities.clone();
      }

   public int getThermistor()
      {
      return thermistor;
      }

   public int getLeftPhotoresistor()
      {
      return photoresistors[0];
      }

   public int getRightPhotoresistor()
      {
      return photoresistors[1];
      }

   public boolean isLeftObstacleDetected()
      {
      return obstacleSensors[0];
      }

   public boolean isRightObstacleDetected()
      {
      return obstacleSensors[1];
      }

   public String toString()
      {
      final StringBuffer s = new StringBuffer("FinchState" + EOL);
      s.append("   Orb").append(":                     (").append(orb.getR()).append(",").append(orb.getG()).append(",").append(orb.getB()).append(")").append(EOL);
      s.append("   Accelerometer").append(":           (").append(accelerometerState.getX()).append(",").append(accelerometerState.getY()).append(",").append(accelerometerState.getZ()).append(")").append(EOL);
      for (int i = 0; i < motorStates.length; i++)
         {
         final PositionControllableMotorState state = motorStates[i];
         s.append("   Motor ").append(i).append(":").append(EOL);
         s.append("      Current Position").append(":     ").append(state.getCurrentPosition()).append(EOL);
         s.append("      Specified Position").append(":   ").append(state.getSpecifiedPosition()).append(EOL);
         s.append("      Specified Speed").append(":      ").append(state.getSpecifiedSpeed()).append(EOL);
         s.append("      Specified Velocity").append(":   ").append(motorVelocities[i]).append(EOL);
         }
      s.append("   Thermistor").append(":              ").append(thermistor).append(EOL);
      s.append("   Photoresistor").append(":           (").append(photoresistors[0]).append(",").append(photoresistors[1]).append(")").append(EOL);
      s.append("   Obstacle").append(":                (").append(obstacleSensors[0]).append(",").append(obstacleSensors[1]).append(")").append(EOL);

      return s.toString();
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

      final FinchStateImpl that = (FinchStateImpl)o;

      if (thermistor != that.thermistor)
         {
         return false;
         }
      if (accelerometerState != null ? !accelerometerState.equals(that.accelerometerState) : that.accelerometerState != null)
         {
         return false;
         }
      if (!Arrays.equals(motorStates, that.motorStates))
         {
         return false;
         }
      if (!Arrays.equals(motorVelocities, that.motorVelocities))
         {
         return false;
         }
      if (!Arrays.equals(obstacleSensors, that.obstacleSensors))
         {
         return false;
         }
      if (orb != null ? !orb.equals(that.orb) : that.orb != null)
         {
         return false;
         }
      if (!Arrays.equals(photoresistors, that.photoresistors))
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result = orb != null ? orb.hashCode() : 0;
      result = 31 * result + (accelerometerState != null ? accelerometerState.hashCode() : 0);
      result = 31 * result + (motorStates != null ? Arrays.hashCode(motorStates) : 0);
      result = 31 * result + (motorVelocities != null ? Arrays.hashCode(motorVelocities) : 0);
      result = 31 * result + thermistor;
      result = 31 * result + (photoresistors != null ? Arrays.hashCode(photoresistors) : 0);
      result = 31 * result + (obstacleSensors != null ? Arrays.hashCode(obstacleSensors) : 0);
      return result;
      }

   private static final class Orb
      {
      private final int r;
      private final int g;
      private final int b;

      private Orb(final int r, final int g, final int b)
         {
         this.r = r;
         this.g = g;
         this.b = b;
         }

      public int getR()
         {
         return r;
         }

      public int getG()
         {
         return g;
         }

      public int getB()
         {
         return b;
         }

      public Color getColor()
         {
         return new Color(r, g, b);
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

         final Orb that = (Orb)o;

         if (b != that.b)
            {
            return false;
            }
         if (g != that.g)
            {
            return false;
            }
         if (r != that.r)
            {
            return false;
            }

         return true;
         }

      public int hashCode()
         {
         int result;
         result = r;
         result = 31 * result + g;
         result = 31 * result + b;
         return result;
         }
      }
   }