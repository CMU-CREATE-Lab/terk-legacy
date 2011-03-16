package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VisualProgrammer
   {
   private static final Logger LOG = Logger.getLogger(VisualProgrammer.class);

   public static void main(final String[] args) throws InterruptedException
      {
      final GridProgram program = new GridProgram();
      final GridCoordinate startingCoordinate = new GridCoordinate(0, 0);
      program.addProgramStep(startingCoordinate, new MessagePrintingProgramStep(GridDirection.SOUTH, "Step 1"));
      program.addProgramStep(new GridCoordinate(0, -1), new MessagePrintingProgramStep(GridDirection.EAST, "Step 2"));
      program.addProgramStep(new GridCoordinate(1, -1), new MessagePrintingProgramStep(GridDirection.WEST, "Step 3"));
      ProgramExecutorManager.getInstance().createProgramExecutor(program, startingCoordinate);
      ProgramExecutorManager.getInstance().run();
      Thread.sleep(2000);
      ProgramExecutorManager.getInstance().stop();

      // todo: deal with RejectedExecutionException caused by running the above code!
      }
   }
