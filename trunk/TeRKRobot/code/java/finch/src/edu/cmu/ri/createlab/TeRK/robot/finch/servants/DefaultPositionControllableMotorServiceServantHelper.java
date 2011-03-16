package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultPositionControllableMotorServiceServantHelper extends AbstractServiceServant implements PositionControllableMotorServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultPositionControllableMotorServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.MOTOR_DEVICE_COUNT);
      this.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MOTOR_DEVICE_ID, FinchConstants.POSITION_CONTROLLABLE_MOTOR_DEVICE_ID);
      this.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MIN_POSITION_DELTA, FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA);
      this.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MAX_POSITION_DELTA, FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA);
      this.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MIN_SPEED, FinchConstants.MOTOR_DEVICE_MIN_SPEED);
      this.setReadOnlyProperty(PositionControllableMotorService.PROPERTY_NAME_MAX_SPEED, FinchConstants.MOTOR_DEVICE_MAX_SPEED);
      }

   public PositionControllableMotorState[] getState()
      {
      final FinchState state = finchProxy.getState();
      if (state != null)
         {
         final edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState[] motorStates = state.getPositionControllableMotorStates();
         if (motorStates != null && motorStates.length > 0)
            {
            final PositionControllableMotorState[] iceStates = new PositionControllableMotorState[motorStates.length];
            for (int i = 0; i < motorStates.length; i++)
               {
               final edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState motorState = motorStates[i];
               iceStates[i] = FinchStateConverter.convert(motorState);
               }
            return iceStates;
            }
         }
      return null;
      }

   public void execute(final PositionControllableMotorCommand command)
      {
      if (command != null)
         {
         finchProxy.setMotorPositions(command.motorMask, command.motorPositionDeltas, command.motorSpeeds);
         }
      }
   }