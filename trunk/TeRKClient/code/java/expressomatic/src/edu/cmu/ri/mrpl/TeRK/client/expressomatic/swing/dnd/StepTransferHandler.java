package edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.Sequence;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;

public class StepTransferHandler extends SequenceTransferHandler
   {
   public StepTransferHandler()
      {
      super();
      }

   public boolean importData(JComponent c, Transferable t)
      {
      if (!canImport(c, t.getTransferDataFlavors()))
         {
         return false;
         }

      try
         {
         JList list = (JList)c;
         int index = list.getSelectedIndex();
         DefaultListModel listModel = (DefaultListModel)list.getModel();

         // Dropping an XmlExpression, create new SequenceStep with default Transition
         // and insert it after selected XmlExpression
         if (hasExpressionFlavor(t.getTransferDataFlavors()))
            {
            Expression exp = (Expression)t.getTransferData(expressionFlavor);

            SequenceStep step = new SequenceStep(exp);
            listModel.insertElementAt(step, index + 1);
            list.setSelectedIndex(index + 1);
            }

         // Dropping a Condition, insert this condition into the Transition of the
         // selected SequenceStep, replace the old condition if necessary
         else if (hasConditionFlavor(t.getTransferDataFlavors()))
            {
            Condition cond = (Condition)t.getTransferData(conditionFlavor);

            // If this is the last step in the sequence or there are currently no steps in the sequence,
            // it cannot have a transition so do nothing
            if (index <= listModel.size() && index >= 0)
               {
               SequenceStep step = (SequenceStep)listModel.get(index);
               step.getTransition().setCondition(cond);
               }

            // Force a repaint because cell has changed
            for (ListSelectionListener l : list.getListSelectionListeners())
               {
               l.valueChanged(new ListSelectionEvent(list, index, index, false));
               }
            list.repaint();
            }

         // Dropping a SequenceStep, move to before the selected index
         else if (hasStepFlavor(t.getTransferDataFlavors()))
            {
            SequenceStep step = (SequenceStep)t.getTransferData(stepFlavor);

            int oldIndex = listModel.indexOf(step);
            listModel.removeElement(step);
            if (oldIndex < index)
               {
               index--;
               }

            listModel.insertElementAt(step, index);

            list.setSelectedIndex(index);
            }

         else if (hasRoboticonFlavor(t.getTransferDataFlavors()))
            {
            RoboticonFile theRoboticon = (RoboticonFile)t
                  .getTransferData(roboticonFlavor);
            final boolean sequence = theRoboticon.roboticonType == RoboticonType.SEQUENCE;
            if (sequence)
               {
               Sequence steps = SequenceFileHandler.getInstance()
                     .openFile(theRoboticon);
               // Dropping a Sequence, expand the exprs, move to after the
               // selected index
               int oldIndex = index;
               for (SequenceStep step : steps.getSteps())
                  {
                  listModel.insertElementAt(step, ++index);
                  }

               //only enable save for private seq, see ddd
               if (!theRoboticon.isPublic())
                  {
                  c.putClientProperty("RoboticonFile", theRoboticon);
                  }
               c.putClientProperty("loop", steps.getLoopBackToStart());
               list.setSelectedIndex(oldIndex + 1);
               }
            else
               {
               Expression exp = ExpressionFileHandler.getInstance()
                     .openFile(theRoboticon);
               // Dropping an XmlExpression, create new SequenceStep with
               // default Transition
               // and insert it after selected XmlExpression
               SequenceStep step = new SequenceStep(exp);
               listModel.insertElementAt(step, index + 1);
               list.setSelectedIndex(index + 1);
               }
            }

         // Not a recognizable DataFlavor
         else
            {
            return false;
            }
         }
      catch (UnsupportedFlavorException ufe)
         {
         System.out.println("importData: unsupported data flavor");
         return false;
         }
      catch (IOException ioe)
         {
         System.out.println("importData: I/O exception");
         return false;
         }
      return true;
      }

   protected void exportDone(JComponent c, Transferable data, int action)
      {
      }

   public boolean canImport(JComponent c, DataFlavor[] flavors)
      {
      boolean res = hasExpressionFlavor(flavors) ||
                    hasConditionFlavor(flavors) ||
                    hasStepFlavor(flavors) ||
                    hasRoboticonFlavor(flavors);

      return res;
      }

   protected Transferable createTransferable(JComponent c)
      {
      if (c != null && c instanceof JList)
         {
         JList source = (JList)c;
         Object value = source.getSelectedValue();

         if (value == null || !(value instanceof SequenceStep))
            {
            return null;
            }

         return new StepTransferable((SequenceStep)value);
         }
      return null;
      }

   public int getSourceActions(JComponent c)
      {
      return COPY_OR_MOVE;
      }

   public class StepTransferable implements Transferable
      {
      SequenceStep data;

      public StepTransferable(SequenceStep s)
         {
         data = s;
         }

      public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
         {
         if (!isDataFlavorSupported(flavor))
            {
            throw new UnsupportedFlavorException(flavor);
            }
         return data;
         }

      public DataFlavor[] getTransferDataFlavors()
         {
         return new DataFlavor[]{stepFlavor};
         }

      public boolean isDataFlavorSupported(DataFlavor flavor)
         {
         return stepFlavor.equals(flavor);
         }
      }
   }
