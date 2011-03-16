package edu.cmu.ri.createlab.TeRK.robot.finch;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface FinchEventPublisher
   {
   void addFinchEventListener(final FinchEventListener listener);

   void removeFinchEventListener(final FinchEventListener listener);
   }

