package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceCreator;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdServiceFactory implements SerialDeviceServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdServiceFactory.class);

   private final Map<String, SerialDeviceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, SerialDeviceServiceCreator>();

   public HummingbirdServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return AnalogInputsServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return AudioServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return FullColorLEDServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(HummingbirdService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return HummingbirdServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleLEDService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return SimpleLEDServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleServoService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return SimpleServoServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SpeedControllableMotorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return SpeedControllableMotorServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VelocityControllableMotorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return VelocityControllableMotorServiceSerialImpl.create((HummingbirdProxy)proxy);
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final SerialDeviceProxy proxy)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("HummingbirdServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(proxy);
         }
      return null;
      }
   }
