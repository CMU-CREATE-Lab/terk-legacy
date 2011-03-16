package edu.cmu.ri.createlab.TeRK.motor;

import java.util.Arrays;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class BackEMFMotorBuffer
   {
   private final int[] values;

   public BackEMFMotorBuffer(final int[] values)
      {
      this.values = values == null ? new int[0] : values.clone();
      }

   /** Returns a copy of the values. */
   public int[] getValues()
      {
      return values.clone();
      }

   /** Returns the number of values in this motor buffer. */
   public int size()
      {
      return values.length;
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

      final BackEMFMotorBuffer that = (BackEMFMotorBuffer)o;

      return Arrays.equals(values, that.values);
      }

   public int hashCode()
      {
      return (values != null ? Arrays.hashCode(values) : 0);
      }

   public String toString()
      {
      return "BackEMFMotorBuffer{" +
             "num values=" + values.length +
             '}';
      }
   }
