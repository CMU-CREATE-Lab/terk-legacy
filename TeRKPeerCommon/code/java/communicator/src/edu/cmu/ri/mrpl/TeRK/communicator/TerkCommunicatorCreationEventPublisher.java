package edu.cmu.ri.mrpl.TeRK.communicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkCommunicatorCreationEventPublisher
   {
   void addTerkCommunicatorCreationEventListener(final TerkCommunicatorCreationEventListener listener);

   void removeTerkCommunicatorCreationEventListener(final TerkCommunicatorCreationEventListener listener);
   }