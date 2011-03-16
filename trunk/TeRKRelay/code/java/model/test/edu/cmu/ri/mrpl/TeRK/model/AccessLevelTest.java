package edu.cmu.ri.mrpl.TeRK.model;

import junit.framework.TestCase;

/**
 * <p>
 * <code>AccessLevelTest</code> tests the {@link AccessLevel} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AccessLevelTest extends TestCase
   {
   public AccessLevelTest(final String test)
      {
      super(test);
      }

   public void testGetType()
      {
      assertEquals("Owner", AccessLevel.OWNER.getLevel());
      assertEquals("OwnerRestricted", AccessLevel.OWNER_RESTRICTED.getLevel());

      assertEquals("NormalEnhanced", AccessLevel.NORMAL_ENHANCED.getLevel());
      assertEquals("Normal", AccessLevel.NORMAL.getLevel());
      assertEquals("NormalRestricted", AccessLevel.NORMAL_RESTRICTED.getLevel());

      assertEquals("GuestEnhanced", AccessLevel.GUEST_ENHANCED.getLevel());
      assertEquals("Guest", AccessLevel.GUEST.getLevel());
      assertEquals("GuestRestricted", AccessLevel.GUEST_RESTRICTED.getLevel());

      assertEquals("None", AccessLevel.NONE.getLevel());
      }
   }
