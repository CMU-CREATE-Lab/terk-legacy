package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class ForwardingProgramStep implements ProgramStep
   {
   private final GridDirection directionOfNextStep;

   ForwardingProgramStep(final GridDirection directionOfNextStep)
      {
      this.directionOfNextStep = directionOfNextStep;
      }

   protected final GridDirection getDirectionOfNextStep()
      {
      return directionOfNextStep;
      }

   public GridDirection execute()
      {
      return getDirectionOfNextStep();
      }
   }
