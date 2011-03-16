package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class MotorVelocityCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set the motor velocities. */
   private static final byte COMMAND_PREFIX = 'V';

   private final byte[] command;

   MotorVelocityCommandStrategy(final int leftVelocity, final int rightVelocity)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                (byte)leftVelocity,
                                (byte)rightVelocity};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }