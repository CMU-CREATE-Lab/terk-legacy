package edu.cmu.ri.createlab.TeRK.accelerometer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AccelerometerState
   {
   private final int x;
   private final int y;
   private final int z;

   public AccelerometerState(final int x, final int y, final int z)
      {
      this.x = x;
      this.y = y;
      this.z = z;
      }

   public int getX()
      {
      return x;
      }

   public int getY()
      {
      return y;
      }

   public int getZ()
      {
      return z;
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

      final AccelerometerState that = (AccelerometerState)o;

      if (x != that.x)
         {
         return false;
         }
      if (y != that.y)
         {
         return false;
         }
      if (z != that.z)
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
      result = 31 * result + z;
      return result;
      }

   public String toString()
      {
      return "AccelerometerState{" +
             "x=" + x +
             ", y=" + y +
             ", z=" + z +
             '}';
      }
   }