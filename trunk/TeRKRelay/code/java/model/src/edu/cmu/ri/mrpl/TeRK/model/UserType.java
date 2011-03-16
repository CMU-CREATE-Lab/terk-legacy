package edu.cmu.ri.mrpl.TeRK.model;

import java.io.Serializable;

/**
 * <p>
 * <code>UserType</code> defines the various user types.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum UserType implements Serializable
   {
      HUMAN("Human"),
      ROBOT("Robot");

   private final String type;

   private UserType(final String type)
      {
      this.type = type;
      }

   public String getType()
      {
      return type;
      }

   public String toString()
      {
      return type;
      }
   }