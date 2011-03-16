package edu.cmu.ri.mrpl.TeRK.client.components.services;

import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.digitalin.DigitalInService;
import edu.cmu.ri.createlab.TeRK.digitalin.DigitalInServiceImpl;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutServiceImpl;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.createlab.TeRK.led.LEDServiceImpl;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorServiceImpl;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOService;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOServiceImpl;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.createlab.TeRK.servo.ServoServiceImpl;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamServiceImpl;
import edu.cmu.ri.mrpl.TeRK.AnalogInControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.DigitalInControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.DigitalOutControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.LEDControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.MotorControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.SerialIOServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.ServoControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.VideoStreamerServerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceCreator;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class QwerkServiceFactory implements IceServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(QwerkServiceFactory.class);

   private final Map<String, IceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, IceServiceCreator>();

   QwerkServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return AnalogInputsServiceIceImpl.create(AnalogInControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return AudioServiceIceImpl.create(AudioControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(DigitalInService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new DigitalInServiceImpl(DigitalInControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(DigitalOutService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new DigitalOutServiceImpl(DigitalOutControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(LEDService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new LEDServiceImpl(LEDControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(BackEMFMotorService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new BackEMFMotorServiceImpl(MotorControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SerialIOService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new SerialIOServiceImpl(SerialIOServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(ServoService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new ServoServiceImpl(ServoControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VideoStreamService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new VideoStreamServiceImpl(VideoStreamerServerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      }

   public final Service createService(final String serviceTypeId, final ObjectPrx serviceProxy)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         LOG.debug("QwerkServiceFactory.createService(" + serviceTypeId + ")");
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(serviceProxy);
         }
      return null;
      }
   }
