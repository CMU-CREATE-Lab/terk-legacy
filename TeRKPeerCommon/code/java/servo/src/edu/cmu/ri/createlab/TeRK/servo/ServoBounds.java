package edu.cmu.ri.createlab.TeRK.servo;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoBounds
   {
   private final int min;
   private final int max;

   public ServoBounds(final int min, final int max)
      {
      this.min = min;
      this.max = max;
      }

   public int getMin()
      {
      return min;
      }

   public int getMax()
      {
      return max;
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

      final ServoBounds that = (ServoBounds)o;

      return max == that.max && min == that.min;
      }

   public int hashCode()
      {
      int result;
      result = min;
      result = 31 * result + max;
      return result;
      }

   public String toString()
      {
      return "ServoBounds{" +
             "min=" + min +
             ", max=" + max +
             '}';
      }
   }
