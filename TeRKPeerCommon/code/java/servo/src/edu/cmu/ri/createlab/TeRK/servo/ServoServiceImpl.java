package edu.cmu.ri.createlab.TeRK.servo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.Bounds;
import edu.cmu.ri.mrpl.TeRK.ServoCommand;
import edu.cmu.ri.mrpl.TeRK.ServoControllerPrx;
import edu.cmu.ri.mrpl.TeRK.ServoMode;
import edu.cmu.ri.mrpl.TeRK.ServoState;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoServiceImpl extends ServicePropertyManager implements ServoService
   {
   private static final Logger LOG = Logger.getLogger(ServoServiceImpl.class);

   private static final int SERVO_DEFAULT_SPEED = 1000;
   private static final int DEFAULT_DEVICE_COUNT = 16;

   private final ServoControllerPrx proxy;
   private final int deviceCount;
   private final boolean[] maskAllOn;
   private final boolean[] maskAllOff;
   private final Map<Integer, boolean[]> servoIdToMaskArrayMap;
   private final Map<ServoMode, ServoMode[]> servoModeToModeArrayMap;
   private final int[] allZeros;

   public ServoServiceImpl(final ServoControllerPrx proxy)
      {
      super(proxy);
      this.proxy = proxy;

      // try to get the device count from the property
      final Integer numDevices = getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      this.deviceCount = (numDevices == null) ? DEFAULT_DEVICE_COUNT : numDevices;

      // create and initialize the all-on and all-off mask arrays
      maskAllOn = new boolean[deviceCount];
      maskAllOff = new boolean[deviceCount];
      Arrays.fill(maskAllOn, true);
      Arrays.fill(maskAllOff, false);

      // create the array used for zero velocity or acceleration
      allZeros = new int[deviceCount];
      Arrays.fill(allZeros, 0);

      // build the mask arrays for each servo and store them in a map indexed on servo id
      final Map<Integer, boolean[]> servoIdToMaskMapTemp = new HashMap<Integer, boolean[]>(deviceCount);
      for (int i = 0; i < deviceCount; i++)
         {
         final boolean[] mask = new boolean[deviceCount];
         mask[i] = true;
         servoIdToMaskMapTemp.put(i, mask);
         }
      servoIdToMaskArrayMap = Collections.unmodifiableMap(servoIdToMaskMapTemp);

      // build the servo mode arrays and store them in a map indexed on servo mode
      final Map<ServoMode, ServoMode[]> servoModeToModeArrayMapTemp = new HashMap<ServoMode, ServoMode[]>(deviceCount);
      final ServoMode[] servoModeOffArray = new ServoMode[deviceCount];
      final ServoMode[] servoModePositionControlArray = new ServoMode[deviceCount];
      final ServoMode[] servoModeSpeedControlArray = new ServoMode[deviceCount];
      Arrays.fill(servoModeOffArray, ServoMode.ServoOff);
      Arrays.fill(servoModePositionControlArray, ServoMode.ServoMotorPositionControl);
      Arrays.fill(servoModeSpeedControlArray, ServoMode.ServoMotorSpeedControl);
      servoModeToModeArrayMapTemp.put(ServoMode.ServoOff, servoModeOffArray);
      servoModeToModeArrayMapTemp.put(ServoMode.ServoMotorPositionControl, servoModePositionControlArray);
      servoModeToModeArrayMapTemp.put(ServoMode.ServoMotorSpeedControl, servoModeSpeedControlArray);
      servoModeToModeArrayMap = Collections.unmodifiableMap(servoModeToModeArrayMapTemp);
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void setBounds(final boolean[] servoMask, final int[] minimumPositions, final int[] maximumPositions)
      {
      if (servoMask != null &&
          minimumPositions != null &&
          maximumPositions != null &&
          servoMask.length > 0 &&
          minimumPositions.length > 0 &&
          maximumPositions.length > 0)
         {
         final Bounds[] servoBounds = createBoundsArray(servoMask, minimumPositions, maximumPositions);

         try
            {
            // set the bounds
            proxy.setBounds(servoMask, servoBounds);
            }
         catch (Exception e)
            {
            // todo: allow this to propogate to the caller
            LOG.error("Error in executing setBounds()", e);
            }
         }
      }

   private Bounds[] createBoundsArray(final boolean[] servoMask, final int[] minimumPositions, final int[] maximumPositions)
      {
      // figure out how many bounds will be set
      final int numBoundsToSet = Math.min(getDeviceCount(),
                                          Math.min(servoMask.length,
                                                   Math.min(minimumPositions.length, maximumPositions.length)));

      // create the bounds array
      final Bounds[] servoBounds = new Bounds[numBoundsToSet];
      for (int i = 0; i < servoBounds.length; i++)
         {
         servoBounds[i] = new Bounds(minimumPositions[i], maximumPositions[i]);
         }
      return servoBounds;
      }

   public void setBounds(final int servoId, final int minimumPosition, final int maximumPosition)
      {
      if (servoId >= 0 && servoId < getDeviceCount())
         {
         // build the arrays
         final boolean[] mask = getMask(servoId);
         final int[] minimumPositions = new int[servoId + 1];
         final int[] maximumPositions = new int[servoId + 1];
         minimumPositions[servoId] = minimumPosition;
         maximumPositions[servoId] = maximumPosition;

         // set the bounds
         setBounds(mask, minimumPositions, maximumPositions);
         }
      else
         {
         throw new IndexOutOfBoundsException("Illegal servo id [" + servoId + "].  Servo id must be less than " + getDeviceCount());
         }
      }

   public ServoBounds[] getBounds()
      {
      try
         {
         // get the bounds
         final Bounds[] bounds = proxy.getBounds();

         if (bounds != null)
            {
            // convert the bounds to ServoBounds and return
            final ServoBounds[] servoBounds = new ServoBounds[bounds.length];
            for (int i = 0; i < bounds.length; i++)
               {
               servoBounds[i] = createServoBounds(bounds[i]);
               }
            return servoBounds;
            }
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Error in executing getBounds()", e);
         }
      return null;
      }

   private ServoBounds createServoBounds(final Bounds bounds)
      {
      return new ServoBounds(bounds.min, bounds.max);
      }

   public void setConfigs(final boolean[] servoMask, final int[] minimumPositions, final int[] maximumPositions, final int[] initialPositions)
      {
      if (servoMask != null &&
          minimumPositions != null &&
          maximumPositions != null &&
          initialPositions != null &&
          servoMask.length > 0 &&
          minimumPositions.length > 0 &&
          maximumPositions.length > 0 &&
          initialPositions.length > 0)
         {
         final Bounds[] servoBounds = createBoundsArray(servoMask, minimumPositions, maximumPositions);
         // figure out how many configs will be set
         final int numConfigsToSet = Math.min(getDeviceCount(),
                                              Math.min(servoMask.length,
                                                       Math.min(minimumPositions.length,
                                                                Math.min(maximumPositions.length, initialPositions.length))));
         // create the config array
         final edu.cmu.ri.mrpl.TeRK.ServoConfig[] servoConfigs = new edu.cmu.ri.mrpl.TeRK.ServoConfig[numConfigsToSet];
         for (int i = 0; i < servoConfigs.length; i++)
            {
            servoConfigs[i] = new edu.cmu.ri.mrpl.TeRK.ServoConfig(servoBounds[i], initialPositions[i]);
            }

         try
            {
            // set the configs
            proxy.setConfigs(servoMask, servoConfigs);
            }
         catch (Exception e)
            {
            // todo: allow this to propogate to the caller
            LOG.error("Error in executing setConfigs()", e);
            }
         }
      }

   public void setConfig(final int servoId, final int minimumPosition, final int maximumPosition, final int initialPosition)
      {
      if (servoId >= 0 && servoId < getDeviceCount())
         {
         // build the arrays
         final boolean[] mask = getMask(servoId);
         final int[] minimumPositions = new int[servoId + 1];
         final int[] maximumPositions = new int[servoId + 1];
         final int[] initialPositions = new int[servoId + 1];
         minimumPositions[servoId] = minimumPosition;
         maximumPositions[servoId] = maximumPosition;
         initialPositions[servoId] = initialPosition;

         // set the configs
         setConfigs(mask, minimumPositions, maximumPositions, initialPositions);
         }
      else
         {
         throw new IndexOutOfBoundsException("Illegal servo id [" + servoId + "].  Servo id must be less than " + getDeviceCount());
         }
      }

   public ServoConfig[] getConfigs()
      {
      try
         {
         final edu.cmu.ri.mrpl.TeRK.ServoConfig[] servoConfigs = proxy.getConfigs();
         if (servoConfigs != null)
            {
            // convert the config to ServoConfig and return
            final ServoConfig[] newServoConfigs = new ServoConfig[servoConfigs.length];
            for (int i = 0; i < servoConfigs.length; i++)
               {
               final ServoBounds servoBounds = createServoBounds(servoConfigs[i].rangeBounds);
               newServoConfigs[i] = new ServoConfig(servoBounds, servoConfigs[i].initialPosition);
               }
            return newServoConfigs;
            }
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Error in executing getConfigs()", e);
         }
      return null;
      }

   private ServoState execute(final boolean[] servoMask, final ServoMode[] servoModes, final int[] servoPositions, final int[] servoSpeeds)
      {
      try
         {
         return proxy.execute(new ServoCommand(servoMask, servoModes, servoPositions, servoSpeeds));
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Error in executing servo command", e);
         }
      return null;
      }

   public int getPosition(final int servoId)
      {
      if (servoId >= 0 && servoId < getDeviceCount())
         {
         final int[] positions = getPositions();
         if (positions != null)
            {
            return positions[servoId];
            }
         return -1;
         }
      else
         {
         throw new IndexOutOfBoundsException("Illegal servo id [" + servoId + "].  Servo id must be less than " + getDeviceCount());
         }
      }

   public int[] getPositions()
      {
      final ServoState servoState = execute(maskAllOff,
                                            servoModeToModeArrayMap.get(ServoMode.ServoMotorPositionControl),
                                            allZeros,
                                            allZeros);

      if (servoState != null)
         {
         return servoState.servoPositions;
         }
      return null;
      }

   public void setPosition(final int servoId, final int position)
      {
      setPositionWithSpeed(servoId, position, SERVO_DEFAULT_SPEED);
      }

   public void setPositionWithSpeed(final int servoId, final int position, final int speed)
      {
      final int[] positions = new int[deviceCount];
      final int[] speeds = new int[deviceCount];
      positions[servoId] = position;
      speeds[servoId] = speed;
      execute(getMask(servoId),
              servoModeToModeArrayMap.get(ServoMode.ServoMotorPositionControl),
              positions,
              speeds);
      }

   public void setPositionsWithSpeeds(final boolean[] servoMask, final int[] positions, final int[] speeds)
      {
      execute(servoMask,
              servoModeToModeArrayMap.get(ServoMode.ServoMotorPositionControl),
              positions,
              speeds);
      }

   public void setVelocities(final int[] velocities)
      {
      execute(maskAllOn,
              servoModeToModeArrayMap.get(ServoMode.ServoMotorPositionControl),
              determineTargetPositionsFromVelocities(velocities),
              velocities);
      }

   /** Looks at each velocity and determines the position to go towards based on the velocity's sign. */
   private int[] determineTargetPositionsFromVelocities(final int[] velocities)
      {
      final int[] positions = new int[velocities.length];

      for (int i = 0; i < velocities.length; i++)
         {
         switch ((int)Math.signum(velocities[i]))
            {
            case -1:
               positions[i] = SERVO_MIN_POSITION;
               break;
            case 1:
               positions[i] = SERVO_MAX_POSITION;
               break;
            default:
               positions[i] = 0;
            }
         }

      return positions;
      }

   public void stopServo(final int servoId)
      {
      execute(getMask(servoId),
              servoModeToModeArrayMap.get(ServoMode.ServoOff),
              allZeros,
              allZeros);
      }

   public void stopServos()
      {
      execute(maskAllOn,
              servoModeToModeArrayMap.get(ServoMode.ServoOff),
              allZeros,
              allZeros);
      }

   public int getDeviceCount()
      {
      return deviceCount;
      }

   private boolean[] getMask(final int servoId)
      {
      return servoIdToMaskArrayMap.get(servoId);
      }
   }
