package edu.cmu.ri.mrpl.TeRK.communicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkCommunicatorCreator
   {
   /** Creates the communicator, if necessary; otherwise, does nothing. */
   void createCommunicator(final TerkCommunicatorCreationEventListener listener);

   /** Returns <code>true</code> if the communicator exists; <code>false</code> otherwise. */
   boolean isCommunicatorRunning();
   }