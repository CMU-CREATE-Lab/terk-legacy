package edu.cmu.ri.createlab.TeRK.motor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.MotorBuffer;
import edu.cmu.ri.mrpl.TeRK.MotorCommand;
import edu.cmu.ri.mrpl.TeRK.MotorControllerPrx;
import edu.cmu.ri.mrpl.TeRK.MotorMode;
import edu.cmu.ri.mrpl.TeRK.MotorState;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BackEMFMotorServiceImpl extends ServicePropertyManager implements BackEMFMotorService
   {
   private static final Logger LOG = Logger.getLogger(BackEMFMotorServiceImpl.class);

   private static final int DEFAULT_ACCELERATION = 16000;// todo: verify this value (and get it from somewhere else)
   private static final int DEFAULT_DEVICE_COUNT = 4;

   private final MotorControllerPrx proxy;
   private final int deviceCount;
   private final boolean[] maskAllOn;
   private final Map<Integer, boolean[]> motorIdToMaskArrayMap;
   private final Map<MotorMode, MotorMode[]> motorModeToModeArrayMap;
   private final int[] allZeros;
   private final int[] defaultAccelerationArray;

   public BackEMFMotorServiceImpl(final MotorControllerPrx proxy)
      {
      super(proxy);
      this.proxy = proxy;

      // try to get the device count from the property
      final Integer numDevices = getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      this.deviceCount = (numDevices == null) ? DEFAULT_DEVICE_COUNT : numDevices;

      // create and initialize the all-on mask array
      maskAllOn = new boolean[deviceCount];
      Arrays.fill(maskAllOn, true);

      // create the array used for zero velocity/position and default acceleration
      allZeros = new int[deviceCount];
      defaultAccelerationArray = new int[deviceCount];
      Arrays.fill(allZeros, 0);
      Arrays.fill(defaultAccelerationArray, DEFAULT_ACCELERATION);

      // build the mask arrays for each motor and store them in a map indexed on motor id
      final Map<Integer, boolean[]> motorIdToMaskMapTemp = new HashMap<Integer, boolean[]>(deviceCount);
      for (int i = 0; i < deviceCount; i++)
         {
         final boolean[] mask = new boolean[deviceCount];
         mask[i] = true;
         motorIdToMaskMapTemp.put(i, mask);
         }
      motorIdToMaskArrayMap = Collections.unmodifiableMap(motorIdToMaskMapTemp);

      // build the motor mode arrays and store them in a map indexed on motor mode
      final Map<MotorMode, MotorMode[]> motorModeToModeArrayMapTemp = new HashMap<MotorMode, MotorMode[]>(deviceCount);
      final MotorMode[] motorModeOffArray = new MotorMode[deviceCount];
      final MotorMode[] motorModePositionControlArray = new MotorMode[deviceCount];
      final MotorMode[] motorModeSpeedControlArray = new MotorMode[deviceCount];
      Arrays.fill(motorModeOffArray, MotorMode.MotorOff);
      Arrays.fill(motorModePositionControlArray, MotorMode.MotorPositionControl);
      Arrays.fill(motorModeSpeedControlArray, MotorMode.MotorSpeedControl);
      motorModeToModeArrayMapTemp.put(MotorMode.MotorOff, motorModeOffArray);
      motorModeToModeArrayMapTemp.put(MotorMode.MotorPositionControl, motorModePositionControlArray);
      motorModeToModeArrayMapTemp.put(MotorMode.MotorSpeedControl, motorModeSpeedControlArray);
      motorModeToModeArrayMap = Collections.unmodifiableMap(motorModeToModeArrayMapTemp);
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void setMotorVelocities(final int[] velocities)
      {
      execute(maskAllOn,
              motorModeToModeArrayMap.get(MotorMode.MotorSpeedControl),
              allZeros,
              velocities,
              defaultAccelerationArray);
      }

   public void setMotorVelocities(final boolean[] motorMask, final int[] velocities)
      {
      execute(motorMask,
              motorModeToModeArrayMap.get(MotorMode.MotorSpeedControl),
              allZeros,
              velocities,
              defaultAccelerationArray);
      }

   public void setMotorVelocitiesByIds(final int... motorIdsAndVelocities)
      {
      if (motorIdsAndVelocities != null && motorIdsAndVelocities.length > 0)
         {
         if (motorIdsAndVelocities.length % 2 == 0)
            {
            final boolean[] mask = new boolean[deviceCount];
            final int[] velocities = new int[deviceCount];
            for (int i = 0; i < motorIdsAndVelocities.length; i += 2)
               {
               final int id = motorIdsAndVelocities[i];
               final int velocity = motorIdsAndVelocities[i + 1];
               mask[id] = true;
               velocities[id] = velocity;
               }
            execute(mask,
                    motorModeToModeArrayMap.get(MotorMode.MotorSpeedControl),
                    allZeros,
                    velocities,
                    defaultAccelerationArray);
            }
         else
            {
            throw new IllegalArgumentException("Number of arguments to setMotorVelocitiesByIds() must be even!");
            }
         }
      }

   public void stopMotors(final int... motorIds)
      {
      final boolean[] mask;
      if (motorIds == null || motorIds.length == 0)
         {
         mask = maskAllOn;
         }
      else
         {
         mask = new boolean[deviceCount];
         Arrays.fill(mask, false);
         for (final int i : motorIds)
            {
            mask[i] = true;
            }
         }

      execute(mask,
              motorModeToModeArrayMap.get(MotorMode.MotorOff),
              allZeros,
              allZeros,
              defaultAccelerationArray);
      }

   private MotorState execute(final boolean[] mask, final MotorMode[] modes, final int[] positions, final int[] velocities, final int[] accelerations)
      {
      try
         {
         return proxy.execute(new MotorCommand(mask, modes, positions, velocities, accelerations));
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting a command.", e);
         }
      return null;
      }

   /**
    *	<p>Halts the motor and prevents further movement.</p>
    *   @param motorid the number in [0, getDeviceCount() ) which references the motor
    *	@see #stopMotors
    */
   public void emergencyStopMotor(final int motorid)
      {
      setMotorVelocity(0, motorid);
      }

   public void setMotorVelocity(final int velocity, final int motorid)
      {
      final int[] velocities = new int[deviceCount];
      velocities[motorid] = velocity;

      execute(getMask(motorid),
              motorModeToModeArrayMap.get(MotorMode.MotorSpeedControl),
              allZeros,
              velocities,
              defaultAccelerationArray);
      }

   public void setMotorVelocityUntil(final int velocity, final int position, final int motorid)
      {
      setPosition(velocity, position, motorid);
      }

   public void setPosition(final int speed, final int position, final int motorid)
      {
      final int[] velocities = new int[deviceCount];
      final int[] positions = new int[deviceCount];
      velocities[motorid] = speed;
      positions[motorid] = position;
      execute(getMask(motorid),
              motorModeToModeArrayMap.get(MotorMode.MotorPositionControl),
              positions,
              velocities,
              defaultAccelerationArray);
      }

   private boolean[] getMask(final int motorid)
      {
      return motorIdToMaskArrayMap.get(motorid);
      }

   public void startMotorBufferRecord(final boolean[] mask) throws Exception
      {
      proxy.startMotorBufferRecord(mask);
      }

   public void stopMotorBufferRecord(final boolean[] mask) throws Exception
      {
      proxy.stopMotorBufferRecord(mask);
      }

   public BackEMFMotorBuffer[] getMotorBuffers(final boolean[] mask) throws Exception
      {
      // get the buffers from the proxy
      final MotorBuffer[] sourceBuffers = proxy.getMotorBuffers(mask);

      // convert the Ice buffers to TeRK ones
      final BackEMFMotorBuffer[] destinationBuffers = new BackEMFMotorBuffer[sourceBuffers.length];
      for (int i = 0; i < sourceBuffers.length; i++)
         {
         destinationBuffers[i] = new BackEMFMotorBuffer(sourceBuffers[i].values);
         }
      return destinationBuffers;
      }

   public void setMotorBuffer(final boolean[] mask, final BackEMFMotorBuffer[] buffers) throws Exception
      {
      // convert the TeRK buffers to Ice ones
      final MotorBuffer[] iceMotorBuffers = new MotorBuffer[buffers.length];
      for (int i = 0; i < buffers.length; i++)
         {
         iceMotorBuffers[i] = new MotorBuffer(buffers[i].getValues());
         }

      proxy.setMotorBuffer(mask, iceMotorBuffers);
      }

   public void playMotorBuffer(final boolean[] mask) throws Exception
      {
      proxy.playMotorBuffer(mask);
      }

   public int getDeviceCount()
      {
      return this.deviceCount;
      }
   }
