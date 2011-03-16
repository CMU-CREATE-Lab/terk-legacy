package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RoboticonMessengerController
   {
   void sendPublicMessage(final String parentMessageId, final ClientRoboticonMessage clientRoboticonMessage);

   void sendPrivateMessage(final String parentMessageId, final String recipientUserId, final ClientRoboticonMessage clientRoboticonMessage);
   }
