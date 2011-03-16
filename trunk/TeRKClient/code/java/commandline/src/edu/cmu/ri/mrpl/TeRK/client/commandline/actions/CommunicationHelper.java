package edu.cmu.ri.mrpl.TeRK.client.commandline.actions;

import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CommunicationHelper
   {
   boolean isCommunicatorRunning();

   SimpleCommandLineQwerkPrx getSimpleCommandLineQwerkPrx();

   void setSimpleCommandLineQwerkPrx(final SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrx);

   TerkCommunicator getTerkCommunicator();
   }