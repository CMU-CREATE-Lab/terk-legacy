package edu.cmu.ri.createlab.TeRK.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum FlowControl
   {
      NONE("None"),
      HARDWARE("Hardware"),
      SOFTWARE("Software");

   private final String name;

   FlowControl(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   public String toString()
      {
      return name;
      }
   }
