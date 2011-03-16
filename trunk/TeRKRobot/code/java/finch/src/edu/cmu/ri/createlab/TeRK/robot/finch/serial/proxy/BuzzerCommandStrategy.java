package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BuzzerCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to set the motor velocities. */
   private static final byte COMMAND_PREFIX = 'B';

   private final byte[] command;

   BuzzerCommandStrategy(final int frequency, final int durationInMilliseconds)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                getHighByteFromInt(durationInMilliseconds),
                                getLowByteFromInt(durationInMilliseconds),
                                getHighByteFromInt(frequency),
                                getLowByteFromInt(frequency)};
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