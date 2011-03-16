package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.finch.FinchService;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceFactory;
import edu.cmu.ri.createlab.TeRK.robot.finch.services.FinchServiceManager;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class CommandLineFinchViaServices extends BaseCommandLineFinch
   {
   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineFinchViaServices(in).run();
      }

   private final SerialDeviceServiceFactory serviceFactory = new FinchServiceFactory();
   private ServiceManager serviceManager;
   private FinchProxy finchProxy;

   public CommandLineFinchViaServices(final BufferedReader in)
      {
      super(in);
      }

   protected boolean connect(final String serialPortName)
      {
      finchProxy = FinchProxy.create(serialPortName);

      if (finchProxy == null)
         {
         println("Connection failed.");
         return false;
         }
      else
         {
         println("Connection successful.");
         finchProxy.addSerialDevicePingFailureEventListener(
               new SerialDevicePingFailureEventListener()
               {
               public void handlePingFailureEvent()
                  {
                  println("Finch ping failure detected.  You will need to reconnect.");
                  serviceManager = null;
                  finchProxy = null;
                  }
               });
         serviceManager = new FinchServiceManager(finchProxy, serviceFactory);
         return true;
         }
      }

   protected FinchState getState()
      {
      return ((FinchService)serviceManager.getServiceByTypeId(FinchService.TYPE_ID)).getFinchState();
      }

   protected void setFullColorLED(final int r, final int g, final int b)
      {
      ((FullColorLEDService)serviceManager.getServiceByTypeId(FullColorLEDService.TYPE_ID)).set(0, new Color(r, g, b));
      }

   protected AccelerometerState getAccelerometer()
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).getAccelerometerState(0);
      }

   protected AccelerometerGs getAccelerometerGs()
      {
      return ((AccelerometerService)serviceManager.getServiceByTypeId(AccelerometerService.TYPE_ID)).getAccelerometerGs(0);
      }

   protected boolean[] getObstacleDetectors()
      {
      return ((SimpleObstacleDetectorService)serviceManager.getServiceByTypeId(SimpleObstacleDetectorService.TYPE_ID)).areObstaclesDetected();
      }

   protected int[] getPhotoresistors()
      {
      return ((PhotoresistorService)serviceManager.getServiceByTypeId(PhotoresistorService.TYPE_ID)).getPhotoresistorValues();
      }

   protected int getThermistor()
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).getThermistorValue(0);
      }

   protected double getThermistorCelsiusTemperature()
      {
      return ((ThermistorService)serviceManager.getServiceByTypeId(ThermistorService.TYPE_ID)).getCelsiusTemperature(0);
      }

   protected int[] getCurrentMotorPositions()
      {
      final PositionControllableMotorState[] states = ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).getStates();

      if ((states != null) && (states.length > 0))
         {
         final int[] positions = new int[states.length];

         for (int i = 0; i < states.length; i++)
            {
            final PositionControllableMotorState state = states[i];
            if (state != null)
               {
               positions[i] = state.getCurrentPosition();
               }
            }

         return positions;
         }

      return null;
      }

   protected double[] getCurrentMotorPositionsInCentimeters()
      {
      return ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).convertToCentimeters(getCurrentMotorPositions());
      }

   protected int[] getCurrentMotorVelocities()
      {
      return ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).getVelocities();
      }

   protected double[] getCurrentMotorVelocitiesInCentimetersPerSecond()
      {
      return ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).convertToCentimetersPerSecond(getCurrentMotorVelocities());
      }

   protected PositionControllableMotorState[] getCurrentMotorStates()
      {
      return ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).getStates();
      }

   protected boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed)
      {
      ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).setPositions(new int[]{leftPositionDelta, rightPositionDelta},
                                                                                                                                   new int[]{leftSpeed, rightSpeed});
      return true;
      }

   protected boolean setMotorPositions(final boolean[] mask, final int[] positionDeltas, final int[] speeds)
      {
      ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).setPositions(mask, positionDeltas, speeds);

      return true;
      }

   protected boolean setMotorPositions(final double leftDistanceDelta, final double rightDistanceDelta, final double leftSpeed, final double rightSpeed)
      {
      ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).setPositions(new double[]{leftDistanceDelta, rightDistanceDelta},
                                                                                                                                   new double[]{leftSpeed, rightSpeed});
      return true;
      }

   protected boolean setMotorPositions(final boolean[] mask, final double[] distanceDeltas, final double[] speeds)
      {
      ((PositionControllableMotorService)serviceManager.getServiceByTypeId(PositionControllableMotorService.TYPE_ID)).setPositions(mask, distanceDeltas, speeds);

      return true;
      }

   protected boolean setMotorVelocities(final int leftVelocity, final int rightVelocity)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocities(new int[]{leftVelocity, rightVelocity});

      return true;
      }

   protected boolean setMotorVelocities(final double leftVelocity, final double rightVelocity)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocities(new double[]{leftVelocity, rightVelocity});

      return true;
      }

   protected boolean setMotorVelocity(final boolean[] mask, final int[] velocities)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocities(mask, velocities);

      return true;
      }

   protected boolean setMotorVelocity(final boolean[] mask, final double[] velocities)
      {
      ((VelocityControllableMotorService)serviceManager.getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)).setVelocities(mask, velocities);

      return true;
      }

   protected void playBuzzerTone(final int frequency, final int duration)
      {
      ((BuzzerService)serviceManager.getServiceByTypeId(BuzzerService.TYPE_ID)).playTone(0, frequency, duration);
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
      ((FinchService)serviceManager.getServiceByTypeId(FinchService.TYPE_ID)).emergencyStop();
      }

   protected boolean isInitialized()
      {
      return serviceManager != null;
      }

   protected void disconnect()
      {
      if (finchProxy != null)
         {
         finchProxy.disconnect();
         finchProxy = null;
         }
      serviceManager = null;
      }
   }