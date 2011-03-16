package edu.cmu.ri.mrpl.TeRK.model;

import java.io.Serializable;

/**
 * <p>
 * <code>AccessLevel</code> defines the various peer association access levels.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum AccessLevel implements Serializable
   {
      OWNER("Owner"),
      OWNER_RESTRICTED("OwnerRestricted"),

      NORMAL_ENHANCED("NormalEnhanced"),
      NORMAL("Normal"),
      NORMAL_RESTRICTED("NormalRestricted"),

      GUEST_ENHANCED("GuestEnhanced"),
      GUEST("Guest"),
      GUEST_RESTRICTED("GuestRestricted"),

      NONE("None");

   private final String level;

   private AccessLevel(final String level)
      {
      this.level = level;
      }

   public String getLevel()
      {
      return level;
      }

   public String toString()
      {
      return level;
      }
   }
