package edu.cmu.ri.mrpl.TeRK.client.flower.render;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

public class PotRenderer extends AbstractFlowerRenderer
   {

   public PotRenderer()
      {
      super();
      }

   public void paintFlower(Graphics2D g)
      {
      GeneralPath path = new GeneralPath();
      path.moveTo(-POT_TOP_WIDTH / 2, 0);
      path.lineTo(-POT_BASE_WIDTH / 2, POT_HEIGHT);
      path.lineTo(POT_BASE_WIDTH / 2, POT_HEIGHT);
      path.lineTo(POT_TOP_WIDTH / 2, 0);
      path.curveTo(0, -POT_TOP_WIDTH / 5,
                   0, -POT_TOP_WIDTH / 5,
                   -POT_TOP_WIDTH / 2, 0);
      //		path.append(new Arc2D.Float(-POT_TOP_WIDTH/2, -POT_TOP_WIDTH/2,
      //									POT_TOP_WIDTH, POT_TOP_WIDTH/4,
      //									(float)(-3*Math.PI/4), (float)Math.PI/4,
      //									Arc2D.OPEN), true);

      AffineTransform oldTransform = g.getTransform();
      g.transform(AffineTransform.getTranslateInstance(0, POT_HEIGHT_LEVEL));

      g.setColor(isSelected() ? POT_SELECT_COLOR : POT_FILL_COLOR);
      g.fill(path);

      g.setColor(POT_LINE_COLOR);
      g.draw(path);

      g.setTransform(oldTransform);
      }
   }
