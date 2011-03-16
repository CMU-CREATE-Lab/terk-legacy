package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class MessagePrintingProgramStep extends ForwardingProgramStep
   {
   private static final Logger LOG = Logger.getLogger(MessagePrintingProgramStep.class);

   private final String message;

   MessagePrintingProgramStep(final GridDirection directionOfNextStep, final String message)
      {
      super(directionOfNextStep);
      this.message = message;
      }

   public GridDirection execute()
      {
      LOG.debug("MessagePrintingProgramStep.execute(): [" + message + "]");
      return getDirectionOfNextStep();
      }
   }
