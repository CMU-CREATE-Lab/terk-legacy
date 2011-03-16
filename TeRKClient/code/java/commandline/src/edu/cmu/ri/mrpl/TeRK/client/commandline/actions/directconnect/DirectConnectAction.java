package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect;

import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.AbstractAction;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class DirectConnectAction extends AbstractAction
   {
   DirectConnectAction(final DirectConnectCommunicationHelper directConnectCommunicationHelper)
      {
      super(directConnectCommunicationHelper);
      }

   DirectConnectCommunicator getDirectConnectCommunicator()
      {
      return (DirectConnectCommunicator)getTerkCommunicator();
      }
   }
