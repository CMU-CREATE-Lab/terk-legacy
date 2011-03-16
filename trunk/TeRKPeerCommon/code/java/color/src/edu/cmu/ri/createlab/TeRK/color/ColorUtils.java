package edu.cmu.ri.createlab.TeRK.color;

import java.awt.Color;
import edu.cmu.ri.mrpl.TeRK.color.RGBColor;

/**
 * <p>
 * <code>ColorUtils</code> helps convert between {@link Color} and {@link RGBColor}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ColorUtils
   {
   public static RGBColor[] convert(final Color[] colors)
      {
      final RGBColor[] newColors = new RGBColor[colors.length];
      for (int i = 0; i < colors.length; i++)
         {
         newColors[i] = convert(colors[i]);
         }
      return newColors;
      }

   public static Color[] convert(final RGBColor[] colors)
      {
      final Color[] newColors = new Color[colors.length];
      for (int i = 0; i < colors.length; i++)
         {
         newColors[i] = convert(colors[i]);
         }
      return newColors;
      }

   public static RGBColor convert(final Color color)
      {
      return new RGBColor(color.getRed(),
                          color.getGreen(),
                          color.getBlue());
      }

   public static Color convert(final RGBColor color)
      {
      return new Color(color.red,
                       color.green,
                       color.blue);
      }

   private ColorUtils()
      {
      // private to prevent instantiation
      }
   }
