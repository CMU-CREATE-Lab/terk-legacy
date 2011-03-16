package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class MotorPositionCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set the motor positions. */
   private static final byte COMMAND_PREFIX = 'P';

   private final byte[] command;

   MotorPositionCommandStrategy(final int leftPositionDelta, final int rightPositionDelta,
                                final int leftSpeed, final int rightSpeed)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(leftPositionDelta),
                                getLowByteFromInt(leftPositionDelta),
                                getHighByteFromInt(rightPositionDelta),
                                getLowByteFromInt(rightPositionDelta),
                                (byte)Math.abs(leftSpeed),
                                (byte)Math.abs(rightSpeed)};
      }

   private byte getHighByteFromInt(final int val)
      {
      return (byte)((val << 16) >> 24);
      }

   private byte getLowByteFromInt(final int val)
      {
      return (byte)((val << 24) >> 24);
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }