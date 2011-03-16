package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.awt.Color;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandler;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandlerAdapter;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class OrbFaderText extends FinchCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(OrbFaderText.class);

   private Color[] colors = new Color[]{
         new Color(255, 0, 0),
         new Color(255, 255, 0),
         new Color(0, 255, 0),
         new Color(0, 255, 255),
         new Color(0, 0, 255),
         new Color(255, 0, 255)
   };
   private int targetColorIndex = 1;
   private Color currentColor = colors[0];

   public static void main(final String[] args)
      {
      new OrbFaderText();
      }

   public OrbFaderText()
      {
      final ConnectionStrategyEventHandler handler = new RepetitiveConnectionStrategyEventHandler(new MyConnectionStrategyEventHandler(),
                                                                                                  0,
                                                                                                  1,
                                                                                                  TimeUnit.MILLISECONDS);
      addConnectionStrategyEventHandler(handler);

      // run the command line app
      runCommandLineApplication();
      }

   private final class MyConnectionStrategyEventHandler extends ConnectionStrategyEventHandlerAdapter
      {
      public void handleConnectionEvent()
         {
         final FullColorLEDService fullColorLEDService = getFullColorLEDService();
         if (fullColorLEDService != null)
            {
            // see if we've reached the target color and need to update our starting and ending colors
            if (currentColor.equals(colors[targetColorIndex]))
               {
               targetColorIndex += 1;
               if (targetColorIndex >= colors.length)
                  {
                  targetColorIndex = 0;
                  }
               }

            final int rDelta = computeDelta(currentColor.getRed(), colors[targetColorIndex].getRed());
            final int gDelta = computeDelta(currentColor.getGreen(), colors[targetColorIndex].getGreen());
            final int bDelta = computeDelta(currentColor.getBlue(), colors[targetColorIndex].getBlue());

            currentColor = new Color(currentColor.getRed() + rDelta,
                                     currentColor.getGreen() + gDelta,
                                     currentColor.getBlue() + bDelta);
            LOG.debug("color = [" + currentColor + "]");
            fullColorLEDService.set(0, currentColor);
            }
         }

      private int computeDelta(final int currentValue, final int targetValue)
         {
         if (currentValue > targetValue)
            {
            return -5;
            }
         else if (currentValue < targetValue)
            {
            return 5;
            }
         return 0;
         }

      public void handleDisconnectionEvent()
         {
         LOG.debug("OrbFaderText$MyConnectionStrategyEventHandler.handleDisconnectionEvent()");
         }
      }
   }