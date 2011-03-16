package edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;

public class ExpressionCellRenderer extends AbstractListCellRenderer
   {
   public Component getListCellRendererComponent(
         JList list,
         Object value,            // value to display
         int index,               // cell index
         boolean isSelected,      // is the cell selected
         boolean cellHasFocus)    // the list and the cell have the focus
   {
   if (value instanceof Expression)
      {
      Expression e = (Expression)value;

      setText(e.getName());
      setIcon(getExpressionIcon(e.getName()));

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

      this.setSize(PALLET_WIDTH, EXPRESSION_CELL_HEIGHT);

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder(new EmptyBorder(3, 3, 3, 3));
      setOpaque(true);
      }
   return this;
   }
   }
