package edu.cmu.ri.mrpl.TeRK.model;

import java.io.Serializable;

/**
 * <p>
 * <code>UserType</code> defines the various event log types.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum EventType implements Serializable
   {
      LOGIN("Login"),
      LOGOUT("Logout"),
      FORCED_LOGOUT("Forced Logout"),
      CONNECTION_ESTABLISHED("Connection Established"),
      CONNECTION_DESTROYED("Connection Destroyed");

   private final String type;

   private EventType(final String type)
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
