package edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer;

import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoServiceIceImpl;
import edu.cmu.ri.mrpl.TeRK.AnalogInControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.led.FullColorLEDServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.led.SimpleLEDServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.motor.SpeedControllableMotorServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceCreator;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceFactory;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.servo.SimpleServoServicePrxHelper;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TerkServiceFactory implements IceServiceFactory
   {
   private static final Logger LOG = Logger.getLogger(TerkServiceFactory.class);

   private final Map<String, IceServiceCreator> typeIdToServiceCreatorsMap = new HashMap<String, IceServiceCreator>();

   TerkServiceFactory()
      {
      typeIdToServiceCreatorsMap.put(AudioService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return new RobotDiariesAudioServiceImpl(AudioControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(AnalogInputsService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return AnalogInputsServiceIceImpl.create(AnalogInControllerPrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(FullColorLEDService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return FullColorLEDServiceIceImpl.create(FullColorLEDServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(HummingbirdService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return HummingbirdServiceIceImpl.create(HummingbirdServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleLEDService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return SimpleLEDServiceIceImpl.create(SimpleLEDServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SimpleServoService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return SimpleServoServiceIceImpl.create(SimpleServoServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(SpeedControllableMotorService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return SpeedControllableMotorServiceIceImpl.create(SpeedControllableMotorServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      typeIdToServiceCreatorsMap.put(VelocityControllableMotorService.TYPE_ID,
                                     new IceServiceCreator()
                                     {
                                     public Service create(final ObjectPrx serviceProxy)
                                        {
                                        return VelocityControllableMotorServiceIceImpl.create(VelocityControllableMotorServicePrxHelper.uncheckedCast(serviceProxy));
                                        }
                                     });
      }

   public Service createService(final String serviceTypeId, final ObjectPrx serviceProxy)
      {
      if (typeIdToServiceCreatorsMap.containsKey(serviceTypeId))
         {
         LOG.debug("TerkServiceFactory.createService(" + serviceTypeId + ")");
         return typeIdToServiceCreatorsMap.get(serviceTypeId).create(serviceProxy);
         }
      return null;
      }
   }