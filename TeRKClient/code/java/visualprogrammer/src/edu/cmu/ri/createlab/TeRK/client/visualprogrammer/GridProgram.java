package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class GridProgram
   {
   private final Map<GridCoordinate, ProgramStep> programSteps = new HashMap<GridCoordinate, ProgramStep>();

   ProgramStep getProgramStepAt(final GridCoordinate coordinate)
      {
      return programSteps.get(coordinate);
      }

   void addProgramStep(final GridCoordinate gridCoordinate, final ProgramStep programStep)
      {
      programSteps.put(gridCoordinate, programStep);
      }
   }
