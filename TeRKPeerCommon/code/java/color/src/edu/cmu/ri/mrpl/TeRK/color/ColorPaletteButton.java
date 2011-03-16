package edu.cmu.ri.mrpl.TeRK.color;

import java.awt.Color;
import java.awt.Cursor;
import javax.swing.JToggleButton;
import edu.cmu.ri.mrpl.swing.ColorUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ColorPaletteButton extends JToggleButton
   {
   private final ColorSwatch colorSwatch;

   public ColorPaletteButton(final Color color)
      {
      this(color, false);
      }

   public ColorPaletteButton(final Color color, final boolean isSelected)
      {
      colorSwatch = new ColorSwatch(color);
      setIcon(colorSwatch);
      setSelected(isSelected);
      setOpaque(false);
      setIconTextGap(0);
      setBorder(null);
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      setToolTipText(ColorUtils.getHexColor(color));
      }

   public Color getColor()
      {
      return colorSwatch.getColor();
      }
   }
