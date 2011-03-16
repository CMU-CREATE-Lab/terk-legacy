package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import junit.framework.TestCase;

/**
 * <p>
 * <code>CommandTypeTest</code> tests the {@link CommandType} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandTypeTest extends TestCase
   {
   public CommandTypeTest(final String test)
      {
      super(test);
      }

   public void testGetTypeId()
      {
      assertEquals("::TeRK::AudioCommand", CommandType.AUDIO.getTypeId());
      assertEquals("::TeRK::DigitalOutCommand", CommandType.DIGITAL_OUT.getTypeId());
      assertEquals("::TeRK::LEDCommand", CommandType.LED.getTypeId());
      assertEquals("::TeRK::MotorCommand", CommandType.MOTOR.getTypeId());
      assertEquals("::TeRK::ServoCommand", CommandType.SERVO.getTypeId());
      }

   public void testFindByTypeId()
      {
      assertEquals(CommandType.AUDIO, CommandType.find("::TeRK::AudioCommand"));
      assertEquals(CommandType.DIGITAL_OUT, CommandType.find("::TeRK::DigitalOutCommand"));
      assertEquals(CommandType.LED, CommandType.find("::TeRK::LEDCommand"));
      assertEquals(CommandType.MOTOR, CommandType.find("::TeRK::MotorCommand"));
      assertEquals(CommandType.SERVO, CommandType.find("::TeRK::ServoCommand"));
      assertNull(CommandType.find("::TeRK::BogusCommand"));
      assertNull(CommandType.find("foo"));
      assertNull(CommandType.find(""));
      assertNull(CommandType.find(null));
      }
   }
