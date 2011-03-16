package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class PetalRenderer extends AbstractFlowerRenderer
   {

   public PetalRenderer(int petalNumber)
      {
      super();
      setAngleInRadians((petalNumber % 6) * Math.PI / 3);
      }

   // Draws petal such that the origin is the center of the flower
   // and angle is the angle at which the petal is turned out
   public void paintFlower(Graphics2D g)
      {
      int bottom = (int)(PETAL_MINOR_WIDTH * 0.9);

      g.setColor(isSelected() ? PETAL_SELECT_COLOR : PETAL_FILL_COLOR);

      g.fillPolygon(new int[]{-PETAL_MINOR_WIDTH / 2,
                              -PETAL_MAJOR_WIDTH / 2,
                              -PETAL_MINOR_WIDTH / 2,
                              PETAL_MINOR_WIDTH / 2,
                              PETAL_MAJOR_WIDTH / 2,
                              PETAL_MINOR_WIDTH / 2},
                    new int[]{bottom,
                              bottom + PETAL_HEIGHT / 2,
                              bottom + PETAL_HEIGHT,
                              bottom + PETAL_HEIGHT,
                              bottom + PETAL_HEIGHT / 2,
                              bottom},
                    6);

      g.setColor(PETAL_LINE_COLOR);
      g.drawPolygon(new int[]{-PETAL_MINOR_WIDTH / 2,
                              -PETAL_MAJOR_WIDTH / 2,
                              -PETAL_MINOR_WIDTH / 2,
                              PETAL_MINOR_WIDTH / 2,
                              PETAL_MAJOR_WIDTH / 2,
                              PETAL_MINOR_WIDTH / 2},
                    new int[]{bottom,
                              bottom + PETAL_HEIGHT / 2,
                              bottom + PETAL_HEIGHT,
                              bottom + PETAL_HEIGHT,
                              bottom + PETAL_HEIGHT / 2,
                              bottom},
                    6);

      AffineTransform t = g.getTransform();
      g.transform(AffineTransform.getTranslateInstance(0, PETAL_HEIGHT / 2 + bottom));
      g.transform(AffineTransform.getRotateInstance(-getAngleInRadians()));

      g.setColor(isSelected() ? PETAL_SELECT_FONT_COLOR : PETAL_FONT_COLOR);

      FontMetrics metrics = g.getFontMetrics();
      Rectangle2D b = metrics.getStringBounds(getText(), g);
      g.drawString(getText(), (int)(-b.getWidth() / 2), (int)(b.getHeight() / 2));

      g.setTransform(t);
      }
   }
