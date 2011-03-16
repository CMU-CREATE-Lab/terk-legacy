package edu.cmu.ri.createlab.TeRK.motor;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PositionControllableMotorServiceIceImpl extends BasePositionControllableMotorServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(PositionControllableMotorServiceIceImpl.class);

   private static final int DEFAULT_DEVICE_COUNT = 0;

   private final PositionControllableMotorServicePrx proxy;

   public static PositionControllableMotorServiceIceImpl create(final PositionControllableMotorServicePrx proxy)
      {
      final PropertyManager propertyManager = new ServicePropertyManager(proxy);

      Integer numDevices = propertyManager.getPropertyAsInteger(TerkConstants.PropertyKeys.DEVICE_COUNT);
      if (numDevices == null)
         {
         LOG.warn("PositionControllableMotorServiceIceImpl failed to retrieve the " + TerkConstants.PropertyKeys.DEVICE_COUNT + " property.  Using default device count of " + DEFAULT_DEVICE_COUNT);
         numDevices = DEFAULT_DEVICE_COUNT;
         }

      return new PositionControllableMotorServiceIceImpl(proxy, propertyManager, numDevices);
      }

   private PositionControllableMotorServiceIceImpl(final PositionControllableMotorServicePrx proxy, final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.proxy = proxy;
      }

   public Integer getCurrentPosition(final int motorId)
      {
      final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState state = getIceState(motorId);
      if (state != null)
         {
         return state.currentPosition;
         }

      return null;
      }

   public Integer getSpecifiedPosition(final int motorId)
      {
      final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState state = getIceState(motorId);
      if (state != null)
         {
         return state.specifiedPosition;
         }

      return null;
      }

   public Integer getSpecifiedSpeed(final int motorId)
      {
      final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState state = getIceState(motorId);
      if (state != null)
         {
         return state.specifiedSpeed;
         }

      return null;
      }

   public PositionControllableMotorState getState(final int motorId)
      {
      final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState state = getIceState(motorId);
      if (state != null)
         {
         return new PositionControllableMotorState(state.currentPosition,
                                                   state.specifiedPosition,
                                                   state.specifiedSpeed);
         }

      return null;
      }

   private edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState getIceState(final int motorId)
      {
      if (motorId >= 0)
         {
         final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[] states = proxy.getState();
         if (states != null)
            {
            if (motorId < states.length)
               {
               return states[motorId];
               }
            }
         }

      return null;
      }

   public PositionControllableMotorState[] getStates()
      {
      final edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState[] iceStates = proxy.getState();
      if (iceStates != null && iceStates.length > 0)
         {
         final PositionControllableMotorState[] states = new PositionControllableMotorState[iceStates.length];

         for (int id = 0; id < iceStates.length; id++)
            {
            if (iceStates[id] != null)
               {
               states[id] = new PositionControllableMotorState(iceStates[id].currentPosition,
                                                               iceStates[id].specifiedPosition,
                                                               iceStates[id].specifiedSpeed);
               }
            }

         return states;
         }

      return null;
      }

   protected void execute(final boolean[] mask, final int[] positionDeltas, final int[] speeds)
      {
      try
         {
         proxy.execute(new PositionControllableMotorCommand(mask, positionDeltas, speeds));
         }
      catch (Exception e)
         {
         LOG.error("Exception while executing a PositionControllableMotorCommand", e);
         }
      }
   }