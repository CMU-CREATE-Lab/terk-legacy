package edu.cmu.ri.createlab.TeRK.communicator.manager;

import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventPublisher;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorProvider;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkCommunicatorManager extends TerkCommunicatorCreationEventPublisher,
                                                 TerkCommunicatorProvider
   {
   void addTerkCommunicatorManagerListener(final TerkCommunicatorManagerListener listener);

   void removeTerkCommunicatorManagerListener(final TerkCommunicatorManagerListener listener);

   boolean isSupported();

   void setIsSupported(boolean isEnabled);

   boolean isCreated();

   void createCommunicator();

   void shutdownCommunicator();

   TerkCommunicator getTerkCommunicator();
   }