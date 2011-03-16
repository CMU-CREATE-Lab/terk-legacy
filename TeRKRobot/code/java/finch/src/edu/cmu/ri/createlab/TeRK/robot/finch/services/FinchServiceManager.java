package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.finch.FinchService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceManager;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchServiceManager extends SerialDeviceServiceManager
   {
   public FinchServiceManager(final SerialDeviceProxy serialDeviceProxy,
                              final SerialDeviceServiceFactory serviceFactory)
      {
      super(serialDeviceProxy, serviceFactory);

      // get the collection of supported services from the peer's proxy
      final Set<String> supportedServices = new HashSet<String>();
      supportedServices.add(AccelerometerService.TYPE_ID);
      supportedServices.add(AudioService.TYPE_ID);
      supportedServices.add(BuzzerService.TYPE_ID);
      supportedServices.add(FinchService.TYPE_ID);
      supportedServices.add(FullColorLEDService.TYPE_ID);
      supportedServices.add(PhotoresistorService.TYPE_ID);
      supportedServices.add(PositionControllableMotorService.TYPE_ID);
      supportedServices.add(SimpleObstacleDetectorService.TYPE_ID);
      supportedServices.add(ThermistorService.TYPE_ID);
      supportedServices.add(VelocityControllableMotorService.TYPE_ID);

      // register the supported services with the superclass
      registerSupportedServices(supportedServices);
      }
   }