package edu.cmu.ri.mrpl.TeRK.client.components.services;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.digitalin.DigitalInService;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOService;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.mrpl.TeRK.QwerkPrx;
import edu.cmu.ri.mrpl.TeRK.QwerkState;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class QwerkController extends ServicePropertyManager
   {
   private final QwerkPrx qwerkProxy;
   private final AnalogInputsService analogInputsService;
   private final AudioService audioService;
   private final DigitalInService digitalInService;
   private final DigitalOutService digitalOutService;
   private final LEDService ledService;
   private final BackEMFMotorService motorService;
   private final SerialIOService serialIOService;
   private final ServoService servoService;
   private final VideoStreamService videoStreamService;
   private final Map<String, Service> serviceTypeIdToServiceMap = new HashMap<String, Service>();

   public QwerkController(final String qwerkIdentifier, final QwerkPrx qwerkProxy, final TerkCommunicator terkCommunicator)
      {
      super(qwerkProxy);
      this.qwerkProxy = qwerkProxy;
      final ServiceManager serviceManager = new IceServiceManager(qwerkIdentifier,
                                                                  qwerkProxy,
                                                                  terkCommunicator,
                                                                  new QwerkServiceFactory());

      // create the services
      analogInputsService = (AnalogInputsService)serviceManager.getServiceByTypeId(AnalogInputsService.TYPE_ID);
      audioService = (AudioService)serviceManager.getServiceByTypeId(AudioService.TYPE_ID);
      digitalInService = (DigitalInService)serviceManager.getServiceByTypeId(DigitalInService.TYPE_ID);
      digitalOutService = (DigitalOutService)serviceManager.getServiceByTypeId(DigitalOutService.TYPE_ID);
      ledService = (LEDService)serviceManager.getServiceByTypeId(LEDService.TYPE_ID);
      motorService = (BackEMFMotorService)serviceManager.getServiceByTypeId(BackEMFMotorService.TYPE_ID);
      serialIOService = (SerialIOService)serviceManager.getServiceByTypeId(SerialIOService.TYPE_ID);
      servoService = (ServoService)serviceManager.getServiceByTypeId(ServoService.TYPE_ID);
      videoStreamService = (VideoStreamService)serviceManager.getServiceByTypeId(VideoStreamService.TYPE_ID);

      // fill the type ID to service map
      serviceTypeIdToServiceMap.put(AnalogInputsService.TYPE_ID, analogInputsService);
      serviceTypeIdToServiceMap.put(AudioService.TYPE_ID, audioService);
      serviceTypeIdToServiceMap.put(DigitalInService.TYPE_ID, digitalInService);
      serviceTypeIdToServiceMap.put(DigitalOutService.TYPE_ID, digitalOutService);
      serviceTypeIdToServiceMap.put(LEDService.TYPE_ID, ledService);
      serviceTypeIdToServiceMap.put(BackEMFMotorService.TYPE_ID, motorService);
      serviceTypeIdToServiceMap.put(SerialIOService.TYPE_ID, serialIOService);
      serviceTypeIdToServiceMap.put(ServoService.TYPE_ID, servoService);
      serviceTypeIdToServiceMap.put(VideoStreamService.TYPE_ID, videoStreamService);
      }

   public AnalogInputsService getAnalogInputsService()
      {
      return analogInputsService;
      }

   public AudioService getAudioService()
      {
      return audioService;
      }

   public DigitalInService getDigitalInService()
      {
      return digitalInService;
      }

   public DigitalOutService getDigitalOutService()
      {
      return digitalOutService;
      }

   public LEDService getLEDService()
      {
      return ledService;
      }

   public BackEMFMotorService getMotorService()
      {
      return motorService;
      }

   public SerialIOService getSerialIOService()
      {
      return serialIOService;
      }

   public ServoService getServoService()
      {
      return servoService;
      }

   public VideoStreamService getVideoStreamService()
      {
      return videoStreamService;
      }

   public QwerkState getQwerkState()
      {
      return qwerkProxy.getState();
      }

   public Service getServiceByTypeId(final String typeId)
      {
      return serviceTypeIdToServiceMap.get(typeId);
      }
   }


