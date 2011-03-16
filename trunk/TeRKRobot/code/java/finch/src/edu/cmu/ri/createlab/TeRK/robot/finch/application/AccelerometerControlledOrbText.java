package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandler;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandlerAdapter;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AccelerometerControlledOrbText extends FinchCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(AccelerometerControlledOrbText.class);

   public static void main(final String[] args)
      {
      new AccelerometerControlledOrbText();
      }

   public AccelerometerControlledOrbText()
      {
      final ConnectionStrategyEventHandler handler = new RepetitiveConnectionStrategyEventHandler(new MyConnectionStrategyEventHandler(),
                                                                                                  0,
                                                                                                  100,
                                                                                                  TimeUnit.MILLISECONDS);
      addConnectionStrategyEventHandler(handler);

      // run the command line app
      runCommandLineApplication();
      }

   private final class MyConnectionStrategyEventHandler extends ConnectionStrategyEventHandlerAdapter
      {
      public void handleConnectionEvent()
         {
         LOG.trace("AccelerometerControlledOrbText$MyConnectionStrategyEventHandler.handleConnectionEvent()");
         final AccelerometerService accelerometerService = getAccelerometerService();
         if (accelerometerService != null)
            {
            final AccelerometerState accelerometerState = accelerometerService.getAccelerometerState(0);
            if (accelerometerState != null)
               {
               final FullColorLEDService fullColorLEDService = getFullColorLEDService();
               if (fullColorLEDService != null)
                  {
                  fullColorLEDService.set(0, new Color(accelerometerState.getX(),
                                                       accelerometerState.getY(),
                                                       accelerometerState.getZ()));
                  }
               }
            }
         }

      public void handleDisconnectionEvent()
         {
         LOG.debug("AccelerometerControlledOrbText$MyConnectionStrategyEventHandler.handleDisconnectionEvent()");
         }
      }
   }