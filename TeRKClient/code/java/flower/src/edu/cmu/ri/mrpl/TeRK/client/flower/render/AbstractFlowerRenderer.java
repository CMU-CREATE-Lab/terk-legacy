package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.JComponent;

public abstract class AbstractFlowerRenderer extends JComponent implements FlowerRenderingConstants
   {

   private boolean selected = false;
   private double angle = 0; // in radians
   private Point translation = new Point(0, 0);

   private String text = "";

   protected AbstractFlowerRenderer()
      {
      super();
      setBounds(0, 0, 1000, 1000);
      setTranslation(FLOWER_CENTER_POSITION);
      }

   public void setSelected(boolean b)
      {
      selected = b;
      }

   public boolean isSelected()
      {
      return selected;
      }

   public void setText(String s)
      {
      text = (s == null) ? text : s;
      }

   public String getText()
      {
      return text;
      }

   public void setAngleInRadians(double radians)
      {
      angle = radians;
      }

   public void setAngleInDegrees(double degrees)
      {
      angle = Math.toRadians(degrees);
      }

   public double getAngleInRadians()
      {
      return angle;
      }

   public void setTranslation(Point p)
      {
      translation = (p == null) ? translation : p;
      }

   public Point getTranslation()
      {
      return translation;
      }

   protected void paintComponent(Graphics g)
      {
      Graphics2D g2d = (Graphics2D)g.create();
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      final AffineTransform oldTransform = g2d.getTransform();

      if (getParent() != null)
         {
         // Scale flower to as large as it can be to fit in its parent panel
         double widthScale = getParent().getWidth() / FLOWER_SIZE.getWidth();
         double heightScale = getParent().getHeight() / FLOWER_SIZE.getHeight();
         double scale = Math.min(widthScale, heightScale);

         g2d.transform(AffineTransform.getScaleInstance(scale, scale));

         // Translate flower to be centered in parent panel
         g2d.transform(AffineTransform.getTranslateInstance(
               (getParent().getWidth() - scale * FLOWER_SIZE.getWidth()) / 2,
               0));
         }

      g2d.transform(AffineTransform.getTranslateInstance(translation.getX(), translation.getY()));
      g2d.transform(AffineTransform.getRotateInstance(angle));

      g2d.setStroke(new BasicStroke(LINE_WIDTH));
      g2d.setFont(FLOWER_FONT);
      paintFlower(g2d);

      g2d.setTransform(oldTransform);

      g2d.dispose();
      }

   abstract public void paintFlower(Graphics2D g);
   }
