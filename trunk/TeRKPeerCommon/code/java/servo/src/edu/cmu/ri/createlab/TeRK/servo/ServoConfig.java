package edu.cmu.ri.createlab.TeRK.servo;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServoConfig
   {
   private final ServoBounds bounds;
   private final int initialPosition;

   public ServoConfig(final ServoBounds bounds, final int initialPosition)
      {
      this.bounds = bounds;
      this.initialPosition = initialPosition;
      }

   public ServoBounds getBounds()
      {
      return bounds;
      }

   public int getInitialPosition()
      {
      return initialPosition;
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

      final ServoConfig that = (ServoConfig)o;

      if (initialPosition != that.initialPosition)
         {
         return false;
         }
      if (bounds != null ? !bounds.equals(that.bounds) : that.bounds != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (bounds != null ? bounds.hashCode() : 0);
      result = 31 * result + initialPosition;
      return result;
      }

   public String toString()
      {
      return "ServoConfig{" +
             "bounds=" + bounds +
             ", initialPosition=" + initialPosition +
             '}';
      }
   }