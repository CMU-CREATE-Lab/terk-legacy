package edu.cmu.ri.createlab.TeRK.client.visualprogrammer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class GridCoordinate
   {
   private final int x;
   private final int y;

   GridCoordinate(final int x, final int y)
      {
      this.x = x;
      this.y = y;
      }

   int getX()
      {
      return x;
      }

   int getY()
      {
      return y;
      }

   public GridCoordinate getNeighbor(final GridDirection direction)
      {
      if (direction != null)
         {
         return new GridCoordinate(x + direction.getDeltaX(),
                                   y + direction.getDeltaY());
         }
      return null;
      }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final GridCoordinate that = (GridCoordinate)o;

      if (x != that.x)
         {
         return false;
         }
      if (y != that.y)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = x;
      result = 31 * result + y;
      return result;
      }

   public String toString()
      {
      return "GridCoordinate(" + x + "," + y + ')';
      }
   }
