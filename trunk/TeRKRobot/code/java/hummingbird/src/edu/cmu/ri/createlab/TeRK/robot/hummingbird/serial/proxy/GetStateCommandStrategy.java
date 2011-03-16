package edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy;

import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceReturnValueCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class GetStateCommandStrategy extends CreateLabSerialDeviceReturnValueCommandStrategy
   {
   /** The command character used to request the hummingbird's state. */
   private static final byte COMMAND_PREFIX = 'G';

   /** The size of the expected response, in bytes */
   private static final int SIZE_IN_BYTES_OF_EXPECTED_RESPONSE = HummingbirdConstants.SIZE_IN_BYTES_OF_STATE_ARRAY;

   private final byte[] command;

   GetStateCommandStrategy()
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