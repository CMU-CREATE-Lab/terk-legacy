package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface MessageEventSource
   {
   void addMessageEventListener(final MessageEventListener messageEventListener);

   void removeMessageEventListener(final MessageEventListener messageEventListener);
   }
