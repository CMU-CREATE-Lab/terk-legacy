package edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy;

import edu.cmu.ri.createlab.serial.CreateLabSerialDeviceHandshakeCommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HandshakeCommandStrategy extends CreateLabSerialDeviceHandshakeCommandStrategy
   {
   /** The pattern of characters to look for in the finch's startup mode "song" */
   private static final byte[] STARTUP_MODE_SONG_CHARACTERS = {'F', 'N', 0, 0};

   /** The pattern of characters to send to put the finch into receive mode. */
   private static final byte[] RECEIVE_MODE_CHARACTERS = {'C', 'S'};

   protected byte[] getReceiveModeCharacters()
      {
      return RECEIVE_MODE_CHARACTERS.clone();
      }

   protected byte[] getStartupModeCharacters()
      {
      return STARTUP_MODE_SONG_CHARACTERS.clone();
      }
   }