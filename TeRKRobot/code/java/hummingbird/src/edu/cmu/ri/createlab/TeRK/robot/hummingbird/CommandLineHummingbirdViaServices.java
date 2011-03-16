package edu.cmu.ri.createlab.TeRK.robot.hummingbird;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.services.HummingbirdServiceFactory;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.services.HummingbirdServiceManager;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class CommandLineHummingbirdViaServices extends BaseCommandLineHummingbird
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineHummingbirdViaServices(in).run();
      }

   private final SerialDeviceServiceFactory serviceFactory = new HummingbirdServiceFactory();
   private ServiceManager serviceManager;
   private HummingbirdProxy hummingbirdProxy;

   public CommandLineHummingbirdViaServices(final BufferedReader in)
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
                  serviceManager = null;
                  hummingbirdProxy = null;
                  }
               });
         serviceManager = new HummingbirdServiceManager(hummingbirdProxy, serviceFactory);
         }
      }

   protected HummingbirdState getState()
      {
      return ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).getHummingbirdState();
      }

   protected int getAnalogInputValue(final int analogInputId)
      {
      return ((AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID)).getAnalogInputValue(analogInputId);
      }

   protected void setMotorVelocity(final int motorId, final int velocity)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocity(motorId, velocity);
      }

   protected void setVibrationMotorSpeed(final int motorId, final int speed)
      {
      ((SpeedControllableMotorService)serviceManager.getServiceByTypeId(SpeedControllableMotorService.TYPE_ID)).setSpeed(motorId, speed);
      }

   protected void setServoPosition(final int servoId, final int position)
      {
      ((SimpleServoService)serviceManager.getServiceByTypeId(SimpleServoService.TYPE_ID)).setPosition(servoId, position);
      }

   protected void setLED(final int ledId, final int intensity)
      {
      ((SimpleLEDService)serviceManager.getServiceByTypeId(SimpleLEDService.TYPE_ID)).set(ledId, intensity);
      }

   protected void setFullColorLED(final int ledId, final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(ledId, new Color(r, g, b));
      }

   protected void playTone(final int frequency, final int amplitude, final int duration)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playTone(frequency, amplitude, duration);
      }

   protected void playClip(final byte[] data)
      {
      ((AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID)).playSound(data);
      }

   protected void emergencyStop()
      {
      ((HummingbirdService)serviceManager.getServiceByTypeId(HummingbirdService.TYPE_ID)).emergencyStop();
      }

   protected boolean isInitialized()
      {
      return serviceManager != null;
      }

   protected void disconnect()
      {
      if (hummingbirdProxy != null)
         {
         hummingbirdProxy.disconnect();
         hummingbirdProxy = null;
         }
      serviceManager = null;
      }
   }