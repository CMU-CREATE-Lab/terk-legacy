package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.CommunicationHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RelayCommunicationHelper extends CommunicationHelper
   {
   RelayCommunicator getRelayCommunicator();
   }