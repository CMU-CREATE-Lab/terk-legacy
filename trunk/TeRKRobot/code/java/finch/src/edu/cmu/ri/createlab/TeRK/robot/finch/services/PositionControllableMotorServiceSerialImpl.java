package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.BasePositionControllableMotorServiceImpl;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class PositionControllableMotorServiceSerialImpl extends BasePositionControllableMotorServiceImpl
   {
   static PositionControllableMotorServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.MOTOR_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MOTOR_DEVICE_ID, FinchConstants.POSITION_CONTROLLABLE_MOTOR_DEVICE_ID);
      basicPropertyManager.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MIN_POSITION_DELTA, FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA);
      basicPropertyManager.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MAX_POSITION_DELTA, FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA);
      basicPropertyManager.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MIN_SPEED, FinchConstants.MOTOR_DEVICE_MIN_SPEED);
      basicPropertyManager.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MAX_SPEED, FinchConstants.MOTOR_DEVICE_MAX_SPEED);

      return new PositionControllableMotorServiceSerialImpl(finchProxy,
                                                            basicPropertyManager,
                                                            FinchConstants.MOTOR_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private PositionControllableMotorServiceSerialImpl(final FinchProxy finchProxy,
                                                      final PropertyManager propertyManager,
                                                      final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public Integer getCurrentPosition(final int motorId)
      {
      return finchProxy.getCurrentMotorPosition(motorId);
      }

   public Integer getSpecifiedPosition(final int motorId)
      {
      return finchProxy.getSpecifiedMotorPosition(motorId);
      }

   public Integer getSpecifiedSpeed(final int motorId)
      {
      return finchProxy.getSpecifiedMotorSpeed(motorId);
      }

   public PositionControllableMotorState getState(final int motorId)
      {
      return finchProxy.getMotorState(motorId);
      }

   public PositionControllableMotorState[] getStates()
      {
      return finchProxy.getMotorStates();
      }

   protected void execute(final boolean[] mask, final int[] positionDeltas, final int[] speeds)
      {
      finchProxy.setMotorPositions(mask, positionDeltas, speeds);
      }
   }