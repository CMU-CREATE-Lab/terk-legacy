package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import java.awt.Color;
import edu.cmu.ri.mrpl.TeRK.AnalogInState;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;
import edu.cmu.ri.mrpl.TeRK.hummingbird.HummingbirdState;

/**
 * <p>
 * <code>HummingbirdStateConverter</code> converts between the TeRK
 * {@link edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState HummingbirdState} and the Ice
 * {@link HummingbirdState HummingbirdState}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdStateConverter
   {
   static HummingbirdState convert(final edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState state)
      {
      if (state == null)
         {
         return null;
         }

      return new HummingbirdState(createAnalogInState(state),
                                  createFullColorLEDState(state),
                                  state.getLedIntensities(),
                                  state.getMotorVelocities(),
                                  state.getServoPositions(),
                                  state.getVibrationMotorSpeeds());
      }

   private static RGBColor[] createFullColorLEDState(final edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState state)
      {

      final Color[] colors = state.getFullColorLEDs();
      if (colors != null)
         {
         final RGBColor[] fullColorLedColors = new RGBColor[colors.length];

         for (int i = 0; i < fullColorLedColors.length; i++)
            {
            fullColorLedColors[i] = new RGBColor(colors[i].getRed(),
                                                 colors[i].getGreen(),
                                                 colors[i].getBlue());
            }

         return fullColorLedColors;
         }
      return null;
      }

   private static AnalogInState createAnalogInState(final edu.cmu.ri.createlab.TeRK.hummingbird.HummingbirdState state)
      {
      final short[] values = new short[state.getAnalogInputValues().length];
      for (int i = 0; i < values.length; i++)
         {
         values[i] = state.getAnalogInputValues()[i];
         }

      return new AnalogInState(values);
      }

   private HummingbirdStateConverter()
      {
      // private to prevent instantiation
      }
   }
