package edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list;

import java.awt.Component;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;

public class ExpressionCellRenderer extends AbstractListCellRenderer
   {
   public Component getListCellRendererComponent(
         JList list,
         Object value, // value to display
         int index, // cell index
         boolean isSelected, // is the cell selected
         boolean cellHasFocus)// the list and the cell have the focus
   {
   if (value instanceof FileEntry)
      {
      final FileEntry<XmlExpression> entry = (FileEntry<XmlExpression>)value;

      setText(entry.getName());
      // setIcon(getExpressionIcon(entry.getObject().getName()));
      setToolTipText(entry.getToolTip());

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

      //this.setSize(PALLET_WIDTH, EXPRESSION_CELL_HEIGHT);

      setEnabled(list.isEnabled());
      setFont(list.getFont());
      setBorder(new EmptyBorder(1, 1, 1, 1));
      setOpaque(true);
      }
   return this;
   }
   }
