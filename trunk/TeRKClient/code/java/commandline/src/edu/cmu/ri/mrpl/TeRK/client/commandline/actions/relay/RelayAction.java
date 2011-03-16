package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.AbstractAction;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class RelayAction extends AbstractAction
   {
   RelayAction(final RelayCommunicationHelper relayCommunicationHelper)
      {
      super(relayCommunicationHelper);
      }

   RelayCommunicator getRelayCommunicator()
      {
      return (RelayCommunicator)getTerkCommunicator();
      }
   }
