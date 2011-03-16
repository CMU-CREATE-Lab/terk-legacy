package edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import javax.swing.JComponent;
import javax.swing.JList;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;

public class ExpressionTransferHandler extends DataTransferHandler
   {
   public ExpressionTransferHandler()
      {
      super();
      }

   public boolean importData(JComponent c, Transferable t)
      {
      return false;
      }

   public boolean canImport(JComponent c, DataFlavor[] flavors)
      {
      return false;
      }

   protected Transferable createTransferable(JComponent c)
      {
      if (c != null && c instanceof JList)
         {
         JList source = (JList)c;
         Object value = source.getSelectedValue();

         if (value == null || !(value instanceof FileEntry))
            {
            return null;
            }

         return new ExpressionTransferable((FileEntry<XmlExpression>)value);
         }
      return null;
      }

   public int getSourceActions(JComponent c)
      {
      return COPY;
      }

   public class ExpressionTransferable implements Transferable
      {
      FileEntry<XmlExpression> data;

      public ExpressionTransferable(FileEntry<XmlExpression> e)
         {
         data = e;
         }

      public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
         {
         if (!isDataFlavorSupported(flavor))
            {
            throw new UnsupportedFlavorException(flavor);
            }
         return data;
         }

      public DataFlavor[] getTransferDataFlavors()
         {
         return new DataFlavor[]{expressionFlavor};
         }

      public boolean isDataFlavorSupported(DataFlavor flavor)
         {
         return expressionFlavor.equals(flavor);
         }
      }
   }