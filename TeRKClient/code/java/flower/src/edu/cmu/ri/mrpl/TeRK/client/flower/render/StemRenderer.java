package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class StemRenderer extends AbstractFlowerRenderer
   {

   public StemRenderer()
      {
      super();
      }

   public void paintFlower(Graphics2D g)
      {
      // Draw top portion of stem
      int correctedWidthTop = (int)(STEM_PART_WIDTH *
                                    STEM_JOINT_POSITION.distance(0, 0) /
                                    STEM_JOINT_POSITION.getY());

      g.setColor(isSelected() ? STEM_SELECT_COLOR : STEM_FILL_COLOR);
      g.fillPolygon(
            new int[]{-correctedWidthTop / 2, correctedWidthTop / 2,
                      (int)(correctedWidthTop / 2 + STEM_JOINT_POSITION.getX()),
                      (int)(-correctedWidthTop / 2 + STEM_JOINT_POSITION.getX())},
            new int[]{0, 0,
                      (int)STEM_JOINT_POSITION.getY(), (int)STEM_JOINT_POSITION.getY()},
            4);

      g.setColor(STEM_LINE_COLOR);
      g.drawPolygon(
            new int[]{-correctedWidthTop / 2, correctedWidthTop / 2,
                      (int)(correctedWidthTop / 2 + STEM_JOINT_POSITION.getX()),
                      (int)(-correctedWidthTop / 2 + STEM_JOINT_POSITION.getX())},
            new int[]{0, 0,
                      (int)STEM_JOINT_POSITION.getY(), (int)STEM_JOINT_POSITION.getY()},
            4);

      // Draw bottom portion of stem
      int correctedWidthBottom = (int)(STEM_PART_WIDTH *
                                       STEM_JOINT_POSITION.distance(0, STEM_TOTAL_HEIGHT) /
                                       (STEM_TOTAL_HEIGHT - STEM_JOINT_POSITION.getY()));

      g.setColor(isSelected() ? STEM_SELECT_COLOR : STEM_FILL_COLOR);
      g.fillPolygon(
            new int[]{(int)(-correctedWidthBottom / 2 + STEM_JOINT_POSITION.getX()),
                      (int)(correctedWidthBottom / 2 + STEM_JOINT_POSITION.getX()),
                      correctedWidthBottom / 2, -correctedWidthBottom / 2},
            new int[]{(int)STEM_JOINT_POSITION.getY(), (int)STEM_JOINT_POSITION.getY(),
                      STEM_TOTAL_HEIGHT, STEM_TOTAL_HEIGHT},
            4);

      g.setColor(STEM_LINE_COLOR);
      g.drawPolygon(
            new int[]{(int)(-correctedWidthBottom / 2 + STEM_JOINT_POSITION.getX()),
                      (int)(correctedWidthBottom / 2 + STEM_JOINT_POSITION.getX()),
                      correctedWidthBottom / 2, -correctedWidthBottom / 2},
            new int[]{(int)STEM_JOINT_POSITION.getY(), (int)STEM_JOINT_POSITION.getY(),
                      STEM_TOTAL_HEIGHT, STEM_TOTAL_HEIGHT},
            4);

      // Draw center of stem
      int correctedWidth = Math.max(correctedWidthTop, correctedWidthBottom);
      g.setColor(isSelected() ? STEM_SELECT_COLOR : STEM_FILL_COLOR);
      g.fillOval((int)(-correctedWidth / 2 + STEM_JOINT_POSITION.getX()),
                 (int)(-correctedWidth / 2 + STEM_JOINT_POSITION.getY()),
                 correctedWidth, correctedWidth);

      g.setColor(STEM_LINE_COLOR);
      g.drawOval((int)(-correctedWidth / 2 + STEM_JOINT_POSITION.getX()),
                 (int)(-correctedWidth / 2 + STEM_JOINT_POSITION.getY()),
                 correctedWidth, correctedWidth);

      AffineTransform t = g.getTransform();
      g.transform(AffineTransform.getTranslateInstance(STEM_JOINT_POSITION.getX(), STEM_JOINT_POSITION.getY()));
      g.transform(AffineTransform.getRotateInstance(-getAngleInRadians()));

      g.setColor(isSelected() ? STEM_SELECT_FONT_COLOR : STEM_FONT_COLOR);

      FontMetrics metrics = g.getFontMetrics();
      Rectangle2D b = metrics.getStringBounds(getText(), g);
      g.drawString(getText(), (int)(-b.getWidth() / 2), (int)(b.getHeight() / 2));

      g.setTransform(t);
      }
   }
