package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import java.util.HashSet;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceManager;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdServiceManager extends SerialDeviceServiceManager
   {
   public HummingbirdServiceManager(final HummingbirdProxy hummingbirdProxy,
                                    final SerialDeviceServiceFactory serviceFactory)
      {
      super(hummingbirdProxy, serviceFactory);

      // get the collection of supported services from the peer's proxy
      final Set<String> supportedServices = new HashSet<String>();
      supportedServices.add(AnalogInputsService.TYPE_ID);
      supportedServices.add(AudioService.TYPE_ID);
      supportedServices.add(FullColorLEDService.TYPE_ID);
      supportedServices.add(HummingbirdService.TYPE_ID);
      supportedServices.add(SimpleLEDService.TYPE_ID);
      supportedServices.add(SimpleServoService.TYPE_ID);
      supportedServices.add(SpeedControllableMotorService.TYPE_ID);
      supportedServices.add(VelocityControllableMotorService.TYPE_ID);

      // register the supported services with the superclass
      registerSupportedServices(supportedServices);
      }
   }
