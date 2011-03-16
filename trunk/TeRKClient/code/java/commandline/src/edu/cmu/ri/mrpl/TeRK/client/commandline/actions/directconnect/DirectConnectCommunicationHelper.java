package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect;

import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.CommunicationHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface DirectConnectCommunicationHelper extends CommunicationHelper
   {
   DirectConnectCommunicator getDirectConnectCommunicator();
   }
