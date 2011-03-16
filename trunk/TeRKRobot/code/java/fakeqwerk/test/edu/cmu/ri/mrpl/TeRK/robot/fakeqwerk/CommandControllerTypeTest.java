package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import junit.framework.TestCase;

/**
 * <p>
 * <code>CommandControllerTypeTest</code> tests the {@link CommandControllerType} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandControllerTypeTest extends TestCase
   {
   public CommandControllerTypeTest(final String test)
      {
      super(test);
      }

   public void testGetTypeId()
      {
      assertEquals("::TeRK::AudioController", CommandControllerType.AUDIO.getTypeId());
      assertEquals("::TeRK::DigitalOutController", CommandControllerType.DIGITAL_OUT.getTypeId());
      assertEquals("::TeRK::LEDController", CommandControllerType.LED.getTypeId());
      assertEquals("::TeRK::MotorController", CommandControllerType.MOTOR.getTypeId());
      assertEquals("::TeRK::ServoController", CommandControllerType.SERVO.getTypeId());
      assertEquals("::TeRK::VideoStreamerServer", CommandControllerType.VIDEO.getTypeId());
      }

   public void testFindByTypeId()
      {
      assertEquals(CommandControllerType.AUDIO, CommandControllerType.find("::TeRK::AudioController"));
      assertEquals(CommandControllerType.DIGITAL_OUT, CommandControllerType.find("::TeRK::DigitalOutController"));
      assertEquals(CommandControllerType.LED, CommandControllerType.find("::TeRK::LEDController"));
      assertEquals(CommandControllerType.MOTOR, CommandControllerType.find("::TeRK::MotorController"));
      assertEquals(CommandControllerType.SERVO, CommandControllerType.find("::TeRK::ServoController"));
      assertEquals(CommandControllerType.VIDEO, CommandControllerType.find("::TeRK::VideoStreamerServer"));
      assertNull(CommandControllerType.find("::TeRK::BogusController"));
      assertNull(CommandControllerType.find("foo"));
      assertNull(CommandControllerType.find(""));
      assertNull(CommandControllerType.find(null));
      }
   }