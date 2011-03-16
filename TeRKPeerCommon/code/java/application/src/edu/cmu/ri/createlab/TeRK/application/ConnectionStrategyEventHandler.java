package edu.cmu.ri.createlab.TeRK.application;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ConnectionStrategyEventHandler
   {
   void handleAttemptingConnectionEvent();

   void handleConnectionEvent();

   void handleFailedConnectionEvent();

   void handleAttemptingDisconnectionEvent();

   void handleDisconnectionEvent();
   }