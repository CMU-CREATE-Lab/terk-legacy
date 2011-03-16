package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultVelocityControllableMotorServiceServantHelper extends AbstractServiceServant implements VelocityControllableMotorServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultVelocityControllableMotorServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.MOTOR_DEVICE_COUNT);
      this.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MIN_VELOCITY);
      this.setReadOnlyProperty(VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, HummingbirdConstants.MOTOR_DEVICE_MAX_VELOCITY);
      }

   public int[] execute(final VelocityControllableMotorCommand command)
      {
      return hummingbirdProxy.setMotorVelocities(command.motorMask, command.motorVelocities);
      }
   }