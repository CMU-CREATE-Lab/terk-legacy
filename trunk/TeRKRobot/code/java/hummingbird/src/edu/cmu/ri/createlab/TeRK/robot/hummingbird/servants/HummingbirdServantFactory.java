package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import java.util.HashSet;
import java.util.Set;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsServiceServant;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServant;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdServiceServant;
import edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServant;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDServiceServant;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorServiceServant;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServant;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdProxyProvider;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoServiceServant;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoServiceServantHelper;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.TeRK.servants.TerkUserServant;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>HummingbirdServantCreator</code> creates the various servants required for the Hummingbird.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdServantFactory implements ServantFactory
   {
   private static final Logger LOG = Logger.getLogger(HummingbirdServantFactory.class);
   private final HummingbirdProxyProvider hummingbirdProxyProvider;

   public HummingbirdServantFactory(final HummingbirdProxyProvider hummingbirdProxyProvider)
      {
      this.hummingbirdProxyProvider = hummingbirdProxyProvider;
      }

   public Servants createServants(final TerkCommunicator terkCommunicator)
      {
      LOG.debug("HummingbirdServantFactory.createServants()");

      final TerkUserServant mainServant = new TerkUserServant(terkCommunicator);

      // create the main servant proxy
      final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
      final TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

      // create secondary servants and their proxies
      final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

      // create the AnalogInputs service servant
      final DefaultAnalogInputsServiceServantHelper analogInputsServiceServantHelper = new DefaultAnalogInputsServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final AnalogInputsServiceServant analogInputsServiceServant = new AnalogInputsServiceServant(analogInputsServiceServantHelper);
      createAndRegisterServantProxy(analogInputsServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the FullColorLED service servant
      final FullColorLEDServiceServantHelper fullColorLEDServiceServantHelper = new DefaultFullColorLEDServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final FullColorLEDServiceServant fullColorLEDServiceServant = new FullColorLEDServiceServant(fullColorLEDServiceServantHelper);
      createAndRegisterServantProxy(fullColorLEDServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Hummingbird service servant
      final HummingbirdServiceServantHelper hummingbirdServiceServantHelper = new DefaultHummingbirdServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final HummingbirdServiceServant hummingbirdServiceServant = new HummingbirdServiceServant(hummingbirdServiceServantHelper);
      createAndRegisterServantProxy(hummingbirdServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the SimpleLED service servant
      final SimpleLEDServiceServantHelper simpleLEDServiceServantHelper = new DefaultSimpleLEDServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final SimpleLEDServiceServant simpleLEDServiceServant = new SimpleLEDServiceServant(simpleLEDServiceServantHelper);
      createAndRegisterServantProxy(simpleLEDServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the SimpleServo service servant
      final SimpleServoServiceServantHelper simpleServoServiceServantHelper = new DefaultSimpleServoServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final SimpleServoServiceServant simpleServoServiceServant = new SimpleServoServiceServant(simpleServoServiceServantHelper);
      createAndRegisterServantProxy(simpleServoServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the SpeedControllableMotor service servant
      final SpeedControllableMotorServiceServantHelper speedControllableMotorServiceServantHelper = new DefaultSpeedControllableMotorServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final SpeedControllableMotorServiceServant speedControllableMotorServiceServant = new SpeedControllableMotorServiceServant(speedControllableMotorServiceServantHelper);
      createAndRegisterServantProxy(speedControllableMotorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the VelocityControllableMotor service servant
      final VelocityControllableMotorServiceServantHelper velocityControllableMotorServiceServantHelper = new DefaultVelocityControllableMotorServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final VelocityControllableMotorServiceServant velocityControllableMotorServiceServant = new VelocityControllableMotorServiceServant(velocityControllableMotorServiceServantHelper);
      createAndRegisterServantProxy(velocityControllableMotorServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

      // create the Audio service servant
      final AudioServiceServantHelper audioServiceServantHelper = new DefaultAudioServiceServantHelper(hummingbirdProxyProvider.getHummingbirdProxy());
      final AudioServiceServant audioServiceServant = new AudioServiceServant(audioServiceServantHelper);
      createAndRegisterServantProxy(audioServiceServant, terkCommunicator, mainServant, secondaryServantProxies);

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
