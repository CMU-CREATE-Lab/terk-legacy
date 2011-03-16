package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ProgramExecutorFactory
   {
   private static final ProgramExecutorFactory INSTANCE = new ProgramExecutorFactory();

   static ProgramExecutorFactory getInstance()
      {
      return INSTANCE;
      }

   private ProgramExecutorFactory()
      {
      // private to prevent instantiation
      }

   ProgramExecutor createProgramExecutor(final GridProgram program, final GridCoordinate startingCoordinate)
      {
      return new DefaultProgramExecutor(program, startingCoordinate);
      }
   }
