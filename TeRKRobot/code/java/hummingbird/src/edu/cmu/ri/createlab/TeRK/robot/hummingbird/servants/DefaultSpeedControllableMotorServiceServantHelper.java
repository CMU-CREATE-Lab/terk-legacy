package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.mrpl.TeRK.motor.SpeedControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultSpeedControllableMotorServiceServantHelper extends AbstractServiceServant implements SpeedControllableMotorServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultSpeedControllableMotorServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_COUNT);
      this.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MIN_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MIN_SPEED);
      this.setReadOnlyProperty(SpeedControllableMotorService.PROPERTY_NAME_MAX_SPEED, HummingbirdConstants.VIBRATION_MOTOR_DEVICE_MAX_SPEED);
      }

   public int[] execute(final SpeedControllableMotorCommand command)
      {
      return hummingbirdProxy.setVibrationMotorSpeeds(command.motorMask, command.motorSpeeds);
      }
   }