package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;

/**
 * <p>
 * <code>CompositeFinchController</code> is a {@link FinchController} which simply delegates commands to all
 * {@link FinchController}s which it contains.  It is contructed given a {@link FinchController} which is considered
 * the "primary" controller, and which is used for methods which have return values.
 * </p>
 * <p>
 * Note that all methods always
 * delegate to contained {@link FinchController}s, even for methods which have return values (in which case the return
 * value for non-primary {@link FinchController}s is ignored).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CompositeFinchController extends AbstractFinchController
   {
   private final Set<FinchController> finchControllers = Collections.synchronizedSet(new HashSet<FinchController>());
   private final FinchController primaryFinchController;

   protected CompositeFinchController(final String serialPortName, final FinchController primaryFinchController)
      {
      super(serialPortName);
      this.primaryFinchController = primaryFinchController;
      }

   public final void addFinchController(final FinchController finchController)
      {
      if (finchController != null)
         {
         finchControllers.add(finchController);
         }
      }

   public final void removeFinchController(final FinchController finchController)
      {
      if (finchController != null)
         {
         finchControllers.remove(finchController);
         }
      }

   public final FinchState getState()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.getState();
            }
         }
      return primaryFinchController.getState();
      }

   public final AccelerometerState getAccelerometerState()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.getAccelerometerState();
            }
         }
      return primaryFinchController.getAccelerometerState();
      }

   public final boolean[] areObstaclesDetected()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.areObstaclesDetected();
            }
         }
      return primaryFinchController.areObstaclesDetected();
      }

   public final int[] getPhotoresistors()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.getPhotoresistors();
            }
         }
      return primaryFinchController.getPhotoresistors();
      }

   public final Integer getThermistor(final int id)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.getThermistor(id);
            }
         }
      return primaryFinchController.getThermistor(id);
      }

   public final int[] getCurrentMotorPositions()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.getCurrentMotorPositions();
            }
         }
      return primaryFinchController.getCurrentMotorPositions();
      }

   public final boolean setFullColorLED(final int red, final int green, final int blue)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.setFullColorLED(red, green, blue);
            }
         }
      return primaryFinchController.setFullColorLED(red, green, blue);
      }

   public final boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.setMotorPositions(leftPositionDelta, rightPositionDelta, leftSpeed, rightSpeed);
            }
         }
      return primaryFinchController.setMotorPositions(leftPositionDelta, rightPositionDelta, leftSpeed, rightSpeed);
      }

   public final boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.setMotorVelocities(leftVelocity, rightVelocity);
            }
         }
      return primaryFinchController.setMotorVelocities(leftVelocity, rightVelocity);
      }

   public final boolean playBuzzerTone(final int frequency, final int durationInMilliseconds)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.playBuzzerTone(frequency, durationInMilliseconds);
            }
         }
      return primaryFinchController.playBuzzerTone(frequency, durationInMilliseconds);
      }

   public final void playTone(final int frequency, final int amplitude, final int duration)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.playTone(frequency, amplitude, duration);
            }
         }
      primaryFinchController.playTone(frequency, amplitude, duration);
      }

   public final void playClip(final byte[] data)
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.playClip(data);
            }
         }
      primaryFinchController.playClip(data);
      }

   public final boolean emergencyStop()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.emergencyStop();
            }
         }
      return primaryFinchController.emergencyStop();
      }

   public final void disconnect()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.disconnect();
            }
         }
      primaryFinchController.disconnect();
      }

   public final void shutdown()
      {
      synchronized (finchControllers)
         {
         for (final FinchController finchController : finchControllers)
            {
            finchController.shutdown();
            }
         }
      primaryFinchController.shutdown();
      }
   }
