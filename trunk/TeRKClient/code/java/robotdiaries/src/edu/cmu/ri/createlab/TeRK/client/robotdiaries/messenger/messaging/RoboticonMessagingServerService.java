package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.messaging;

import java.util.List;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RoboticonMessagingServerService extends Service
   {
   String TYPE_ID = "::TeRK::roboticonmessenger::RoboticonMessagingServerService";

   void sendPublicMessage(final String parentMessageId, final ClientRoboticonMessage clientRoboticonMessage);

   void sendPrivateMessage(final String parentMessageId, final String recipientUserId, final ClientRoboticonMessage clientRoboticonMessage);

   List<RoboticonMessage> getMessageHistory();

   long getLastLogoutTimestamp();
   }
