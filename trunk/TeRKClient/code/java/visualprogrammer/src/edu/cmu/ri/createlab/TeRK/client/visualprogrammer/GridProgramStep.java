package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class GridProgramStep implements ProgramStep
   {
   private final GridCoordinate gridCoordinate;

   public GridProgramStep(final GridCoordinate gridCoordinate)
      {
      this.gridCoordinate = gridCoordinate;
      }

   final GridCoordinate getGridCoordinate()
      {
      return gridCoordinate;
      }
   }
