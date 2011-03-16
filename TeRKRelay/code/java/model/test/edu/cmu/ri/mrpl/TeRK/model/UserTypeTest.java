package edu.cmu.ri.mrpl.TeRK.model;

import junit.framework.TestCase;

/**
 * <p>
 * <code>UserTypeTest</code> tests the {@link UserType} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class UserTypeTest extends TestCase
   {
   public UserTypeTest(final String test)
      {
      super(test);
      }

   public void testGetType()
      {
      assertEquals("Human", UserType.HUMAN.getType());
      assertEquals("Robot", UserType.ROBOT.getType());
      }
   }