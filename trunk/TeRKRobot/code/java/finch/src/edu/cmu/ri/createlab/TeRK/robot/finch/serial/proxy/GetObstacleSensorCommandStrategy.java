package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class GetObstacleSensorCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy
   {
   /** The command character used to request the value of the finch's obstacle sensors. */
   private static final byte COMMAND_PREFIX = 'I';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = 2;

   private final byte[] command;

   GetObstacleSensorCommandStrategy()
      {
      this.command = new byte[]{COMMAND_PREFIX};
      }

   protected int getSizeOfExpectedResponse()
      {
      return SIZE_IN_BYTES_OF_EXPECTED_RESPONSE;
      }

   protected byte[] getCommand()
      {
      return command.clone();
      }
   }