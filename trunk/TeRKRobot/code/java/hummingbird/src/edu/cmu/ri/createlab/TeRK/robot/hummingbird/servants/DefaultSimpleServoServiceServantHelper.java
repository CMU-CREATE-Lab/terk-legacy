package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoServiceServantHelper;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;
import edu.cmu.ri.mrpl.TeRK.servo.SimpleServoCommand;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultSimpleServoServiceServantHelper extends AbstractServiceServant implements SimpleServoServiceServantHelper
   {
   private final HummingbirdProxy hummingbirdProxy;

   DefaultSimpleServoServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.SIMPLE_SERVO_DEVICE_COUNT);
      this.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MIN_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MIN_POSITION);
      this.setReadOnlyProperty(SimpleServoService.PROPERTY_NAME_MAX_POSITION, HummingbirdConstants.SIMPLE_SERVO_DEVICE_MAX_POSITION);
      }

   public int[] execute(final SimpleServoCommand command)
      {
      return hummingbirdProxy.setServoPositions(command.mask, command.positions);
      }
   }