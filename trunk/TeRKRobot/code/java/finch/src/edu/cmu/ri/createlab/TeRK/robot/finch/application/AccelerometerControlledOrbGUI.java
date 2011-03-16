package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandler;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandlerAdapter;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AccelerometerControlledOrbGUI extends FinchGUIApplication
   {
   private static final Logger LOG = Logger.getLogger(AccelerometerControlledOrbGUI.class);

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame("Accelerometer Controlled Orb");

               // add the root panel to the JFrame
               final AccelerometerControlledOrbGUI application = new AccelerometerControlledOrbGUI();
               jFrame.add(application.getMainComponent());

               // set various properties for the JFrame
               jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
               jFrame.setBackground(Color.WHITE);
               jFrame.setResizable(true);
               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   public AccelerometerControlledOrbGUI()
      {
      final ConnectionStrategyEventHandler handler = new RepetitiveConnectionStrategyEventHandler(new MyConnectionStrategyEventHandler(),
                                                                                                  0,
                                                                                                  100,
                                                                                                  TimeUnit.MILLISECONDS);
      addConnectionStrategyEventHandler(handler);
      }

   private final class MyConnectionStrategyEventHandler extends ConnectionStrategyEventHandlerAdapter
      {
      public void handleConnectionEvent()
         {
         LOG.trace("AccelerometerControlledOrbGUI$MyConnectionStrategyEventHandler.handleConnectionEvent()");
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
         LOG.debug("AccelerometerControlledOrbGUI$MyConnectionStrategyEventHandler.handleDisconnectionEvent()");
         }
      }
   }
