package edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.JList;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.Sequence;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceStep;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceTransition;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;

public class StepCellRenderer extends AbstractListCellRenderer
   {

   final static ImageIcon renderingIcon =
         new ImageIcon(new BufferedImage(STEP_CELL_WIDTH,
                                         STEP_CELL_HEIGHT, BufferedImage.TYPE_3BYTE_BGR));

   final static Font defaultFont = new Font("Verdana", 0, 11);

   static NumberFormat transitionLabelFormat;

   public StepCellRenderer()
      {
      super();
      if (transitionLabelFormat == null)
         {
         transitionLabelFormat = NumberFormat.getInstance();
         transitionLabelFormat.setMaximumFractionDigits(1);
         transitionLabelFormat.setMinimumFractionDigits(1);
         }
      }

   public Component getListCellRendererComponent(
         JList list,
         Object value, // value to display
         int index, // cell index
         boolean isSelected, // is the cell selected
         boolean cellHasFocus)// the list and the cell have the focus
   {
   if (value instanceof SequenceStep)
      {
      SequenceStep s = (SequenceStep)value;
      SequenceTransition t = s.getTransition();

      Sequence sequence = (Sequence)list.getModel();
      int listSize = sequence.getSize();

      boolean loops = (sequence == null) ? false : sequence.getLoopBackToStart();

      boolean hasTransition = index < list.getModel().getSize() - 1 || loops;

      String nameLabel = "";
      if (s.getStep() instanceof XmlExpression)
         {
         nameLabel = ((XmlExpression)s.getStep()).getName();
         }
      else if (s.getStep() instanceof Sequence)
         {
         nameLabel = ((Sequence)s.getStep()).getName();
         }
      String transitionLabel;
      if (t.getCondition() != null)
         {
         transitionLabel = t.getCondition().getName();
         }
      else
         {
         transitionLabel = transitionLabelFormat.format(t.getMillisToNextStep() / 1000f) + "s";
         }

      if (isSelected)
         {
         setBackground(list.getSelectionBackground());
         setForeground(list.getSelectionForeground());
         }
      else
         {
         setBackground(list.getBackground());
         setForeground(list.getForeground());
         }

      //			this.setSize(STEP_CELL_WIDTH, STEP_CELL_HEIGHT);
      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setOpaque(true);

      Graphics g = renderingIcon.getImage().getGraphics();
      g.setColor(isSelected ? list.getSelectionBackground() : list.getBackground());
      g.fillRect(0, 0, STEP_CELL_WIDTH, STEP_CELL_HEIGHT);

      if (hasTransition)
         {
         g.setColor(Color.RED);
         if (g instanceof Graphics2D)
            {
            ((Graphics2D)g).setStroke(new BasicStroke(3));
            }

         if (loops && index == 0)
            {
            g.drawLine(20, STEP_CELL_HEIGHT - 10, STEP_CELL_WIDTH, STEP_CELL_HEIGHT - 10);
            g.drawLine(20, STEP_CELL_HEIGHT - 10, 20, STEP_CELL_HEIGHT - 15);
            g.fillPolygon(new int[]{20, 25, 15},
                          new int[]{STEP_CELL_HEIGHT - 20, STEP_CELL_HEIGHT - 15, STEP_CELL_HEIGHT - 15},
                          3);
            }
         else if (loops && index < listSize - 1)
            {
            g.drawLine(0, STEP_CELL_HEIGHT - 10, STEP_CELL_WIDTH, STEP_CELL_HEIGHT - 10);
            }

         if (index < listSize - 1)
            {
            g.drawLine(ICON_IMAGE_SIZE + 10, ICON_IMAGE_SIZE / 2 + 5,
                       STEP_CELL_WIDTH - 10, ICON_IMAGE_SIZE / 2 + 5);
            g.fillPolygon(new int[]{STEP_CELL_WIDTH - 10, STEP_CELL_WIDTH - 5, STEP_CELL_WIDTH - 10},
                          new int[]{ICON_IMAGE_SIZE / 2, ICON_IMAGE_SIZE / 2 + 5, ICON_IMAGE_SIZE / 2 + 10},
                          3);
            }
         else
            {
            g.drawLine(ICON_IMAGE_SIZE + 10, ICON_IMAGE_SIZE / 2 + 5,
                       STEP_CELL_WIDTH - 10, ICON_IMAGE_SIZE / 2 + 5);
            g.drawLine(STEP_CELL_WIDTH - 10, ICON_IMAGE_SIZE / 2 + 5,
                       STEP_CELL_WIDTH - 10, STEP_CELL_HEIGHT - 10);
            g.drawLine(0, STEP_CELL_HEIGHT - 10,
                       STEP_CELL_WIDTH - 10, STEP_CELL_HEIGHT - 10);
            }

         g.setColor(Color.BLACK);
         g.setFont(defaultFont);
         g.drawString(transitionLabel,
                      STEP_CELL_WIDTH - ICON_IMAGE_SIZE - 50,
                      STEP_CELL_HEIGHT - 35);

         if (s.getTransition().getCondition() != null)
            {
            if (g instanceof Graphics2D)
               {
               ((Graphics2D)g).setStroke(new BasicStroke(1));
               }
            g.setColor(Color.BLACK);
            g.drawRect(STEP_CELL_WIDTH - ICON_IMAGE_SIZE - 51, 4, ICON_IMAGE_SIZE + 1, ICON_IMAGE_SIZE + 1);
            g.drawImage(getConditionIcon(s.getTransition().getCondition().getName()).getImage(),
                        STEP_CELL_WIDTH - ICON_IMAGE_SIZE - 50, 5, this);
            }
         }
      g.setColor(Color.BLACK);
      g.setFont(defaultFont);

      if (g instanceof Graphics2D)
         {
         ((Graphics2D)g).setStroke(new BasicStroke(1));
         }
      g.setColor(Color.BLACK);
      g.drawRect(4, 4, ICON_IMAGE_SIZE + 1, ICON_IMAGE_SIZE + 1);
      if (s.getStep() instanceof XmlExpression)
         {
         g.drawImage(getExpressionIcon(((XmlExpression)s.getStep()).getName()).getImage(), 5, 5, this);
         }
      else if (s.getStep() instanceof Sequence)
         {
         g.drawImage(getSequenceIcon(((Sequence)s.getStep()).getName()).getImage(), 5, 5, this);
         }
      g.drawString(nameLabel, 0, STEP_CELL_HEIGHT - 23);

      setIcon(renderingIcon);
      }

   return this;
   }
   }
