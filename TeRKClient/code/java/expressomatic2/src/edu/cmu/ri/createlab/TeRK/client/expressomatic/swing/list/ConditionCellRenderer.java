package edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.list;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.Condition;

public class ConditionCellRenderer extends AbstractListCellRenderer
   {
   public Component getListCellRendererComponent(
         JList list,
         Object value, // value to display
         int index, // cell index
         boolean isSelected, // is the cell selected
         boolean cellHasFocus)// the list and the cell have the focus
   {
   if (value instanceof Condition)
      {
      Condition c = (Condition)value;

      setText(c.getName());
      setIcon(getConditionIcon(c.getName()));

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

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder(new EmptyBorder(1, 1, 1, 1));
      setOpaque(true);
      }
   return this;
   }
   }
