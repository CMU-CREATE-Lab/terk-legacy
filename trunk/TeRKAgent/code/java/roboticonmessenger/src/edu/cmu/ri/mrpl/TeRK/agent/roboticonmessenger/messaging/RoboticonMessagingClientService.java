package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging;

import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RoboticonMessagingClientService extends Service
   {
   String TYPE_ID = "::TeRK::roboticonmessenger::RoboticonMessagingClientService";

   void handleRoboticonMessage(final RoboticonMessage message);
   }
