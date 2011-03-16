package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.util.HashMap;
import java.util.Map;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.finch.FinchService;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ServiceControlPanelFactory
   {
   private static final ServiceControlPanelFactory INSTANCE = new ServiceControlPanelFactory();

   static ServiceControlPanelFactory getInstance()
      {
      return INSTANCE;
      }

   private Map<String, ServiceControlPanelCreator> serviceControlPanelCreatorMap = new HashMap<String, ServiceControlPanelCreator>();

   private ServiceControlPanelFactory()
      {
      serviceControlPanelCreatorMap.put(AccelerometerService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new AccelerometerServiceControlPanel(controlPanelManager, (AccelerometerService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(AnalogInputsService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new AnalogInputsServiceControlPanel(controlPanelManager, (AnalogInputsService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(AudioService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new AudioServiceControlPanel(controlPanelManager, (AudioService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(BuzzerService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new BuzzerServiceControlPanel(controlPanelManager, (BuzzerService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(FinchService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new FinchServiceControlPanel(controlPanelManager, (FinchService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(FullColorLEDService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new FullColorLEDServiceControlPanel(controlPanelManager, (FullColorLEDService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(HummingbirdService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new HummingbirdServiceControlPanel(controlPanelManager, (HummingbirdService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(PhotoresistorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new PhotoresistorServiceControlPanel(controlPanelManager, (PhotoresistorService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(PositionControllableMotorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new PositionControllableMotorServiceControlPanel(controlPanelManager, (PositionControllableMotorService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(SimpleLEDService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new SimpleLEDServiceControlPanel(controlPanelManager, (SimpleLEDService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(SimpleServoService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new SimpleServoServiceControlPanel(controlPanelManager, (SimpleServoService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(SimpleObstacleDetectorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new SimpleObstacleDetectorServiceControlPanel(controlPanelManager, (SimpleObstacleDetectorService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(SpeedControllableMotorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new SpeedControllableMotorServiceControlPanel(controlPanelManager, (SpeedControllableMotorService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(ThermistorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new ThermistorServiceControlPanel(controlPanelManager, (ThermistorService)service);
                                           }
                                        });
      serviceControlPanelCreatorMap.put(VelocityControllableMotorService.TYPE_ID,
                                        new ServiceControlPanelCreator()
                                        {
                                        public ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
                                           {
                                           return new VelocityControllableMotorServiceControlPanel(controlPanelManager, (VelocityControllableMotorService)service);
                                           }
                                        });
      }

   Map<String, ServiceControlPanel> createServiceControlPanelsMap(final ControlPanelManager controlPanelManager, final ServiceManager serviceManager)
      {
      final Map<String, ServiceControlPanel> controlPanels = new HashMap<String, ServiceControlPanel>();
      if (serviceManager != null)
         {
         for (final String serviceTypeId : serviceManager.getTypeIdsOfSupportedServices())
            {
            final Service service = serviceManager.getServiceByTypeId(serviceTypeId);
            final ServiceControlPanel serviceControlPanel = createServiceControlPanel(controlPanelManager, serviceTypeId, service);
            if (serviceControlPanel != null)
               {
               controlPanels.put(serviceTypeId, serviceControlPanel);
               }
            }
         }
      return controlPanels;
      }

   private ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final String serviceTypeId, final Service service)
      {
      final ServiceControlPanelCreator creator = serviceControlPanelCreatorMap.get(serviceTypeId);
      if (creator != null)
         {
         return creator.createServiceControlPanel(controlPanelManager, service);
         }
      return null;
      }

   private interface ServiceControlPanelCreator
      {
      ServiceControlPanel createServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service);
      }
   }
