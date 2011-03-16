package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.application.TerkApplication;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.finch.FinchService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;

/**
 * <p>
 * <code>BaseFinchApplication</code> provides core functionality for Finch applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseFinchApplication extends TerkApplication
   {
   protected BaseFinchApplication()
      {
      super();
      }

   protected BaseFinchApplication(final String defaultConnectionStrategyClassName)
      {
      super(defaultConnectionStrategyClassName);
      }

   protected final AccelerometerService getAccelerometerService()
      {
      if (getServiceManager() != null)
         {
         return ((AccelerometerService)(getServiceManager().getServiceByTypeId(AccelerometerService.TYPE_ID)));
         }
      return null;
      }

   protected final AudioService getAudioService()
      {
      if (getServiceManager() != null)
         {
         return ((AudioService)(getServiceManager().getServiceByTypeId(AudioService.TYPE_ID)));
         }
      return null;
      }

   protected final BuzzerService getBuzzerService()
      {
      if (getServiceManager() != null)
         {
         return ((BuzzerService)(getServiceManager().getServiceByTypeId(BuzzerService.TYPE_ID)));
         }
      return null;
      }

   protected final FinchService getFinchService()
      {
      if (getServiceManager() != null)
         {
         return ((FinchService)(getServiceManager().getServiceByTypeId(FinchService.TYPE_ID)));
         }
      return null;
      }

   protected final FullColorLEDService getFullColorLEDService()
      {
      if (getServiceManager() != null)
         {
         return ((FullColorLEDService)(getServiceManager().getServiceByTypeId(FullColorLEDService.TYPE_ID)));
         }
      return null;
      }

   protected final PhotoresistorService getPhotoresistorService()
      {
      if (getServiceManager() != null)
         {
         return ((PhotoresistorService)(getServiceManager().getServiceByTypeId(PhotoresistorService.TYPE_ID)));
         }
      return null;
      }

   protected final PositionControllableMotorService getPositionControllableMotorService()
      {
      if (getServiceManager() != null)
         {
         return ((PositionControllableMotorService)(getServiceManager().getServiceByTypeId(PositionControllableMotorService.TYPE_ID)));
         }
      return null;
      }

   protected final SimpleObstacleDetectorService getSimpleObstacleDetectorService()
      {
      if (getServiceManager() != null)
         {
         return ((SimpleObstacleDetectorService)(getServiceManager().getServiceByTypeId(SimpleObstacleDetectorService.TYPE_ID)));
         }
      return null;
      }

   protected final ThermistorService getThermistorService()
      {
      if (getServiceManager() != null)
         {
         return ((ThermistorService)(getServiceManager().getServiceByTypeId(ThermistorService.TYPE_ID)));
         }
      return null;
      }

   protected final VelocityControllableMotorService getVelocityControllableMotorService()
      {
      if (getServiceManager() != null)
         {
         return ((VelocityControllableMotorService)(getServiceManager().getServiceByTypeId(VelocityControllableMotorService.TYPE_ID)));
         }
      return null;
      }
   }
