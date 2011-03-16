package edu.cmu.ri.mrpl.TeRK.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ColorSwatch implements Icon
   {
   private static final int DEFAULT_SIZE = 8;

   private final Color color;
   private final int width;
   private final int height;

   public ColorSwatch(final Color color)
      {
      this(color, DEFAULT_SIZE);
      }

   public ColorSwatch(final Color color, final int widthAndHeight)
      {
      this(color, widthAndHeight, widthAndHeight);
      }

   public ColorSwatch(final Color color, final int width, final int height)
      {
      this.color = color;
      this.width = width;
      this.height = height;
      }

   public void paintIcon(final Component c, final Graphics g, final int x, final int y)
      {
      if (g != null)
         {
         g.setColor(color);
         g.fillRect(0, 0, width, height);
         }
      }

   public int getIconWidth()
      {
      return width;
      }

   public int getIconHeight()
      {
      return height;
      }

   public Color getColor()
      {
      return color;
      }
   }
