package edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.universalremote.UniversalRemoteWrapper;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

public class RemoteTransferHandler extends DataTransferHandler
   {
   private static final Logger LOG = Logger.getLogger(RemoteTransferHandler.class);

   public RemoteTransferHandler()
      {
      super();
      }

   public boolean importData(final JComponent c, final Transferable t)
      {
      if (!canImport(c, t.getTransferDataFlavors()))
         {
         return false;
         }

      try
         {
         final UniversalRemoteWrapper remotePanel = (UniversalRemoteWrapper)c;

         // Dropping an XmlExpression, create new SequenceStep with default Transition
         // and insert it after selected XmlExpression
         if (hasExpressionFlavor(t.getTransferDataFlavors()))
            {
            final FileEntry<XmlExpression> exp = (FileEntry<XmlExpression>)t.getTransferData(expressionFlavor);

            // load the expression in a worker thread
            final SwingWorker worker =
                  new SwingWorker()
                  {
                  public Object construct()
                     {
                     remotePanel.loadExpression(exp.getObject());
                     return null;
                     }
                  };
            worker.start();
            }

         // Not a recognizable DataFlavor
         else
            {
            return false;
            }
         }
      catch (UnsupportedFlavorException ufe)
         {
         LOG.error("importData: unsupported data flavor", ufe);
         return false;
         }
      catch (IOException ioe)
         {
         LOG.error("importData: I/O exception", ioe);
         return false;
         }
      return true;
      }

   public boolean canImport(final JComponent c, final DataFlavor[] flavors)
      {
      return hasExpressionFlavor(flavors);
      }

   protected Transferable createTransferable(final JComponent c)
      {
      return null;
      }

   public int getSourceActions(final JComponent c)
      {
      return COPY;
      }
   }