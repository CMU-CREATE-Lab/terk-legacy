package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class CenterRenderer extends AbstractFlowerRenderer
   {

   public CenterRenderer()
      {
      super();
      }

   public void paintFlower(Graphics2D g)
      {
      g.setColor(isSelected() ? CENTER_SELECT_COLOR : CENTER_FILL_COLOR);
      g.fillOval(-FLOWER_CENTER_RADIUS, -FLOWER_CENTER_RADIUS,
                 2 * FLOWER_CENTER_RADIUS, 2 * FLOWER_CENTER_RADIUS);

      g.setColor(CENTER_LINE_COLOR);
      g.drawOval(-FLOWER_CENTER_RADIUS, -FLOWER_CENTER_RADIUS,
                 2 * FLOWER_CENTER_RADIUS, 2 * FLOWER_CENTER_RADIUS);

      g.setColor(isSelected() ? CENTER_SELECT_FONT_COLOR : CENTER_FONT_COLOR);

      FontMetrics metrics = g.getFontMetrics();
      Rectangle2D b = metrics.getStringBounds(getText(), g);
      g.drawString(getText(), (int)(-b.getWidth() / 2), (int)(-b.getHeight() / 2));
      }
   }
