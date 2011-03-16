package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ProgramStep
   {
   /**
    * Executes this program step and returns the next step.  Returns <code>null</code> if there is no next step.
    * @return
    */
   GridDirection execute();
   }