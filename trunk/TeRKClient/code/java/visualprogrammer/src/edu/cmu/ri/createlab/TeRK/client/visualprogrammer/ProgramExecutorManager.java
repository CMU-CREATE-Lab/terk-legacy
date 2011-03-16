package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ProgramExecutorManager
   {
   private static final ProgramExecutorManager INSTANCE = new ProgramExecutorManager();

   static ProgramExecutorManager getInstance()
      {
      return INSTANCE;
      }

   private final Set<ProgramExecutor> programExecutors = new HashSet<ProgramExecutor>();

   private ProgramExecutorManager()
      {
      // private to prevent instantiation
      }

   void createProgramExecutor(final GridProgram program, final GridCoordinate startingCoordinate)
      {
      final ProgramExecutor programExecutor = ProgramExecutorFactory.getInstance().createProgramExecutor(program, startingCoordinate);
      if (programExecutor != null)
         {
         programExecutors.add(programExecutor);
         }
      }

   void run()
      {
      for (final ProgramExecutor programExecutor : programExecutors)
         {
         programExecutor.run();
         }
      }

   void stop()
      {
      for (final ProgramExecutor programExecutor : programExecutors)
         {
         programExecutor.stop();
         }
      }
   }
