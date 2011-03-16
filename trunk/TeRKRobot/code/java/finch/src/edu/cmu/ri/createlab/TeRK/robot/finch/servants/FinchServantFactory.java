package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import java.util.HashSet;
import java.util.Set;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerServiceServant;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServant;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerServiceServant;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.finch.FinchServiceServant;
import edu.cmu.ri.createlab.TeRK.finch.FinchServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServant;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorServiceServant;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServant;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorServiceServant;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorServiceServant;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchProxyProvider;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorServiceServant;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorServiceServantHelper;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.TeRK.servants.TerkUserServant;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FinchServantFactory</code> creates the various servants required for the finch.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FinchServantFactory implements ServantFactory
   {
   private static final Logger LOG = Logger.getLogger(FinchServantFactory.class);
   private final FinchProxyProvider finchProxyProvider;

   public FinchServantFactory(final FinchProxyProvider finchProxyProvider)
      {
      this.finchProxyProvider = finchProxyProvider;
      }

   public Servants createServants(final TerkCommunicator terkCommunicator)
      {
      LOG.debug("FinchServantFactory.createServants()");

      final TerkUserServant mainServant = new TerkUserServant(terkCommunicator);

      // create the main servant proxy
      final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
      final TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

      // create secondary servants and their proxies
      final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

      // create the Accelerometer service servant
      final AccelerometerServiceServantHelper accelerometerServiceServantHelper = new DefaultAccelerometerServiceServantHelper(finchProxyProvider.getFinchProxy());
      final AccelerometerServiceServant accelerometerServiceServant = new AccelerometerServiceServant(accelerometerServiceServantHelper);
      createAndRegisterServantProxy(accelerometerServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Audio service servant
      final AudioServiceServantHelper audioServiceServantHelper = new DefaultAudioServiceServantHelper(finchProxyProvider.getFinchProxy());
      final AudioServiceServant audioServiceServant = new AudioServiceServant(audioServiceServantHelper);
      createAndRegisterServantProxy(audioServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Buzzer service servant
      final BuzzerServiceServantHelper buzzerServiceServantHelper = new DefaultBuzzerServiceServantHelper(finchProxyProvider.getFinchProxy());
      final BuzzerServiceServant buzzerServiceServant = new BuzzerServiceServant(buzzerServiceServantHelper);
      createAndRegisterServantProxy(buzzerServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Finch service servant
      final FinchServiceServantHelper finchServiceServantHelper = new DefaultFinchServiceServantHelper(finchProxyProvider.getFinchProxy());
      final FinchServiceServant finchServiceServant = new FinchServiceServant(finchServiceServantHelper);
      createAndRegisterServantProxy(finchServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the FullColorLED service servant
      final FullColorLEDServiceServantHelper fullColorLEDServiceServantHelper = new DefaultFullColorLEDServiceServantHelper(finchProxyProvider.getFinchProxy());
      final FullColorLEDServiceServant fullColorLEDServiceServant = new FullColorLEDServiceServant(fullColorLEDServiceServantHelper);
      createAndRegisterServantProxy(fullColorLEDServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Photoresistor service servant
      final PhotoresistorServiceServantHelper photoresistorServiceServantHelper = new DefaultPhotoresistorServiceServantHelper(finchProxyProvider.getFinchProxy());
      final PhotoresistorServiceServant photoresistorServiceServant = new PhotoresistorServiceServant(photoresistorServiceServantHelper);
      createAndRegisterServantProxy(photoresistorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the PositionControllableMotor service servant
      final PositionControllableMotorServiceServantHelper positionControllableMotorServiceServantHelper = new DefaultPositionControllableMotorServiceServantHelper(finchProxyProvider.getFinchProxy());
      final PositionControllableMotorServiceServant positionControllableMotorServiceServant = new PositionControllableMotorServiceServant(positionControllableMotorServiceServantHelper);
      createAndRegisterServantProxy(positionControllableMotorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the SimpleObstacleDetector service servant
      final SimpleObstacleDetectorServiceServantHelper simpleObstacleDetectorServiceServantHelper = new DefaultSimpleObstacleDetectorServiceServantHelper(finchProxyProvider.getFinchProxy());
      final SimpleObstacleDetectorServiceServant simpleObstacleDetectorServiceServant = new SimpleObstacleDetectorServiceServant(simpleObstacleDetectorServiceServantHelper);
      createAndRegisterServantProxy(simpleObstacleDetectorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Thermistor service servant
      final ThermistorServiceServantHelper thermistorServiceServantHelper = new DefaultThermistorServiceServantHelper(finchProxyProvider.getFinchProxy());
      final ThermistorServiceServant thermistorServiceServant = new ThermistorServiceServant(thermistorServiceServantHelper);
      createAndRegisterServantProxy(thermistorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the VelocityControllableMotor service servant
      final VelocityControllableMotorServiceServantHelper velocityControllableMotorServiceServantHelper = new DefaultVelocityControllableMotorServiceServantHelper(finchProxyProvider.getFinchProxy());
      final VelocityControllableMotorServiceServant velocityControllableMotorServiceServant = new VelocityControllableMotorServiceServant(velocityControllableMotorServiceServantHelper);
      createAndRegisterServantProxy(velocityControllableMotorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      return new Servants(mainServantPrx, mainServantPrx, secondaryServantProxies);
      }

   private void createAndRegisterServantProxy(final ObjectImpl secondaryServant,
                                              final TerkCommunicator terkCommunicator,
                                              final TerkUserServant mainServant,
                                              final Set<ObjectPrx> secondaryServantProxies)
      {
      final ObjectPrx untypedServantProxy = terkCommunicator.createServantProxy(secondaryServant);
      secondaryServantProxies.add(untypedServantProxy);
      mainServant.registerServiceServant(secondaryServant, untypedServantProxy);
      }
   }