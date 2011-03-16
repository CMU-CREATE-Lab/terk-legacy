package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DefaultProgramExecutor implements ProgramExecutor
   {
   private boolean isRunning = false;
   private final GridProgram program;
   private GridCoordinate currentCoordinate;
   private ExecutorService executor;

   DefaultProgramExecutor(final GridProgram program, final GridCoordinate startingCoordinate)
      {
      this.program = program;
      this.currentCoordinate = startingCoordinate;
      }

   public void run()
      {
      if (!isRunning)
         {
         isRunning = true;
         executor = Executors.newSingleThreadExecutor();
         executor.execute(new ProgramStepExecutionRunnable());
         }
      }

   public void stop()
      {
      isRunning = false;
      if (executor != null)
         {
         executor.shutdown();
         while (executor.isTerminated())
            {
            executor = null;
            break;
            }
         }
      }

   private class ProgramStepExecutionRunnable implements Runnable
      {
      public void run()
         {
         final ProgramStep currentProgramStep = program.getProgramStepAt(currentCoordinate);
         if (currentProgramStep != null)
            {
            final GridDirection directionOfNextStep = currentProgramStep.execute();
            currentCoordinate = currentCoordinate.getNeighbor(directionOfNextStep);

            // todo: this feels weird
            executor.execute(new ProgramStepExecutionRunnable());
            }
         else
            {
            stop();
            }
         }
      }
   }
