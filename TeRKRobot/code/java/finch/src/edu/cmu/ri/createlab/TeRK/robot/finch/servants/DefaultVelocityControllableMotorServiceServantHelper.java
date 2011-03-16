package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultVelocityControllableMotorServiceServantHelper extends AbstractServiceServant implements VelocityControllableMotorServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultVelocityControllableMotorServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.MOTOR_DEVICE_COUNT);
      this.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MOTOR_DEVICE_ID, FinchConstants.VELOCITY_CONTROLLABLE_MOTOR_DEVICE_ID);
      this.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, FinchConstants.MOTOR_DEVICE_MIN_VELOCITY);
      this.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, FinchConstants.MOTOR_DEVICE_MAX_VELOCITY);
      }

   public int[] execute(final VelocityControllableMotorCommand command)
      {
      return finchProxy.setMotorVelocities(command.motorMask, command.motorVelocities);
      }
   }