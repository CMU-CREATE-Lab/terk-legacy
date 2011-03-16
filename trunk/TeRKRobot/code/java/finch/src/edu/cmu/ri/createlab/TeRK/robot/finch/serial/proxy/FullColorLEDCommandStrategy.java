package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;
import edu.cmu.ri.mrpl.util.ByteUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to turn on a full-color LED. */
   private static final byte COMMAND_PREFIX = 'O';

   private final byte[] command;

   FullColorLEDCommandStrategy(final int red, final int green, final int blue)
      {
      this.command = new byte[]{COMMAND_PREFIX,
                                ByteUtils.intToUnsignedByte(red),
                                ByteUtils.intToUnsignedByte(green),
                                ByteUtils.intToUnsignedByte(blue)};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }