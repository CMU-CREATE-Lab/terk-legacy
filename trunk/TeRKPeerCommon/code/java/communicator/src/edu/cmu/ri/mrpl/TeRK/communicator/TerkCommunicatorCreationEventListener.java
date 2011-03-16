package edu.cmu.ri.mrpl.TeRK.communicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkCommunicatorCreationEventListener
   {
   /** Called before the {@link TerkCommunicator} is constructed. */
   void beforeConstruction();

   /**
    * Called after the successful attempt to create the {@link TerkCommunicator}, but before
    * {@link TerkCommunicator#waitForShutdown()} is called.
    */
   void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator);

   /**
    * Called after the failed attempt to create the {@link TerkCommunicator}, but before system shutdown in order to
    * give the application a chance to notify the user.
    */
   void afterFailedConstruction();

   /** Called immediately before {@link TerkCommunicator#waitForShutdown()} is called. */
   void beforeWaitForShutdown();

   /** Called immediately after {@link TerkCommunicator#waitForShutdown()} was called. */
   void afterWaitForShutdown();
   }
