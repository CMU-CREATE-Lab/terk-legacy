package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class IRRenderer extends AbstractFlowerRenderer
   {

   public IRRenderer(int petalNumber)
      {
      super();
      setAngleInRadians((petalNumber % 6) * Math.PI / 3);
      }

   public void paintFlower(Graphics2D g)
      {
      g.setColor(isSelected() ? IR_SELECT_COLOR : IR_FILL_COLOR);
      g.fillRoundRect(-IR_WIDTH / 2, IR_PLACEMENT_HEIGHT, IR_WIDTH, IR_HEIGHT,
                      IR_HEIGHT / 2, IR_HEIGHT / 2);

      g.setColor(IR_LINE_COLOR);
      g.drawRoundRect(-IR_WIDTH / 2, IR_PLACEMENT_HEIGHT, IR_WIDTH, IR_HEIGHT,
                      IR_HEIGHT / 2, IR_HEIGHT / 2);

      AffineTransform t = g.getTransform();
      g.transform(AffineTransform.getTranslateInstance(0, IR_PLACEMENT_HEIGHT + IR_HEIGHT / 2));
      g.transform(AffineTransform.getRotateInstance(-getAngleInRadians()));

      g.setColor(isSelected() ? IR_SELECT_FONT_COLOR : IR_FONT_COLOR);

      FontMetrics metrics = g.getFontMetrics();
      Rectangle2D b = metrics.getStringBounds(getText(), g);
      g.drawString(getText(), (int)(-b.getWidth() / 2), (int)(b.getHeight() / 2));

      g.setTransform(t);
      }
   }
