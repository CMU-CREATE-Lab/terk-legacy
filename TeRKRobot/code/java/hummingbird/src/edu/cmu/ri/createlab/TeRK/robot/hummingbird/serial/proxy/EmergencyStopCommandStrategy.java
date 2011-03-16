package edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceNoReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class EmergencyStopCommandStrategy extends CreateLabSerialDeviceNoReturnValueCommandStrategy
   {
   /** The command character used to trigger the emergency stop. */
   private static final byte COMMAND_PREFIX = 'X';

   private final byte[] command;

   EmergencyStopCommandStrategy()
      {
      this.command = new byte[]{COMMAND_PREFIX};
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }