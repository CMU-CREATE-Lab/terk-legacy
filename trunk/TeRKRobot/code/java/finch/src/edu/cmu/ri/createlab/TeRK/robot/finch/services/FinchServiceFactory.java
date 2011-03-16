package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.finch.FinchService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceCreator;
import edu.cmu.ri.createlab.TeRK.serial.services.SerialDeviceServiceFactory;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchServiceFactory implements SerialDeviceServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(FinchServiceFactory.class);

   private final Map<String, SerialDeviceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, SerialDeviceServiceCreator>();

   public FinchServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AccelerometerService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return AccelerometerServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return AudioServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(BuzzerService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return BuzzerServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FinchService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return FinchServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return FullColorLEDServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(PhotoresistorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return PhotoresistorServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(PositionControllableMotorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return PositionControllableMotorServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleObstacleDetectorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return SimpleObstacleDetectorServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(ThermistorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return ThermistorServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VelocityControllableMotorService.TYPE_ID,
                                     new SerialDeviceServiceCreator()
                                     {
                                     public Service create(final SerialDeviceProxy proxy)
                                        {
                                        return VelocityControllableMotorServiceSerialImpl.create((FinchProxy)proxy);
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final SerialDeviceProxy proxy)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FinchServiceFactory.createService(" + serviceTypeId + ")");
            }
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(proxy);
         }
      return null;
      }
   }