package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import org.apache.log4j.Logger;

/**
 * Code taken from: http://java.sun.com/docs/books/tutorial/uiswing/dnd/examples/StringTransferHandler.java
 */
abstract class StringTransferHandler extends TransferHandler
   {
   private static final Logger LOG = Logger.getLogger(StringTransferHandler.class);

   protected abstract String exportString(JComponent c);

   protected abstract void importString(JComponent c, String str);

   protected abstract void cleanup(JComponent c, boolean remove);

   protected Transferable createTransferable(final JComponent c)
      {
      return new StringSelection(exportString(c));
      }

   public int getSourceActions(final JComponent c)
      {
      return COPY_OR_MOVE;
      }

   public boolean importData(final JComponent c, final Transferable t)
      {
      if (canImport(c, t.getTransferDataFlavors()))
         {
         try
            {
            final String str = (String)t.getTransferData(DataFlavor.stringFlavor);
            importString(c, str);
            return true;
            }
         catch (UnsupportedFlavorException e)
            {
            LOG.error("UnsupportedFlavorException caught", e);
            }
         catch (IOException e)
            {
            LOG.error("IOException caught", e);
            }
         }

      return false;
      }

   protected void exportDone(final JComponent c, final Transferable data, final int action)
      {
      cleanup(c, action == MOVE);
      }

   public boolean canImport(final JComponent c, final DataFlavor[] flavors)
      {
      for (int i = 0; i < flavors.length; i++)
         {
         if (DataFlavor.stringFlavor.equals(flavors[i]))
            {
            return true;
            }
         }
      return false;
      }
   }
