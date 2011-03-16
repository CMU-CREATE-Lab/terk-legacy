package edu.cmu.ri.mrpl.TeRK.color;

import java.awt.Color;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class ColoredObject
   {
   private final Color color;

   protected ColoredObject(final Color color)
      {
      this.color = color;
      }

   public final Color getColor()
      {
      return color;
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

      final ColoredObject that = (ColoredObject)o;

      return color.equals(that.color);
      }

   public int hashCode()
      {
      return color.hashCode();
      }
   }
