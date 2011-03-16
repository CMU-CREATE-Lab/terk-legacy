package edu.cmu.ri.createlab.TeRK.robot.hummingbird;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class CommandLineHummingbirdViaProxy extends BaseCommandLineHummingbird
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineHummingbirdViaProxy(in).run();
      }

   private HummingbirdProxy hummingbirdProxy;

   public CommandLineHummingbirdViaProxy(final BufferedReader in)
      {
      super(in);
      }

   protected void connect(final String serialPortName) throws IOException, SerialPortException
      {
      hummingbirdProxy = HummingbirdProxy.create(serialPortName);

      if (hummingbirdProxy == null)
         {
         println("Connection failed.");
         }
      else
         {
         println("Connection successful.");
         hummingbirdProxy.addSerialDevicePingFailureEventListener(
               new SerialDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("Hummingbird ping failure detected.  You will need to reconnect.");
                  hummingbirdProxy = null;
                  }
               });
         }
      }

   protected HummingbirdState getState()
      {
      return hummingbirdProxy.getState();
      }

   protected int getAnalogInputValue(final int analogInputId)
      {
      return hummingbirdProxy.getAnalogInputValue(analogInputId);
      }

   protected void setMotorVelocity(final int motorId, final int velocity)
      {
      hummingbirdProxy.setMotorVelocity(motorId, velocity);
      }

   protected void setVibrationMotorSpeed(final int motorId, final int speed)
      {
      hummingbirdProxy.setVibrationMotorSpeed(motorId, speed);
      }

   protected void setServoPosition(final int servoId, final int position)
      {
      hummingbirdProxy.setServoPosition(servoId, position);
      }

   protected void setLED(final int ledId, final int intensity)
      {
      hummingbirdProxy.setLED(ledId, intensity);
      }

   protected void setFullColorLED(final int ledId, final int r, final int g, final int b)
      {
      hummingbirdProxy.setFullColorLED(ledId, r, g, b);
      }

   protected void playTone(final int freq, final int amp, final int dur)
      {
      hummingbirdProxy.playTone(freq, amp, dur);
      }

   protected void playClip(final byte[] data)
      {
      hummingbirdProxy.playClip(data);
      }

   protected void emergencyStop()
      {
      hummingbirdProxy.emergencyStop();
      }

   protected boolean isInitialized()
      {
      return hummingbirdProxy != null;
      }

   protected void disconnect()
      {
      if (hummingbirdProxy != null)
         {
         hummingbirdProxy.disconnect();
         hummingbirdProxy = null;
         }
      }
   }