package edu.cmu.ri.mrpl.TeRK.model;

import junit.framework.TestCase;

/**
 * <p>
 * <code>EventTypeTest</code> tests the {@link EventType} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class EventTypeTest extends TestCase
   {
   public EventTypeTest(final String test)
      {
      super(test);
      }

   public void testGetType()
      {
      assertEquals("Login", EventType.LOGIN.getType());
      assertEquals("Logout", EventType.LOGOUT.getType());
      assertEquals("Forced Logout", EventType.FORCED_LOGOUT.getType());
      assertEquals("Connection Established", EventType.CONNECTION_ESTABLISHED.getType());
      assertEquals("Connection Destroyed", EventType.CONNECTION_DESTROYED.getType());
      }
   }
