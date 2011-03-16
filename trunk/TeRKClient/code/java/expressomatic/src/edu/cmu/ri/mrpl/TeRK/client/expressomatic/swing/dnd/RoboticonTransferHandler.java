package edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JList;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;

public class RoboticonTransferHandler extends SequenceTransferHandler
   {

   private boolean bCanImport = true;
   /**
    *
    */
   private static final long serialVersionUID = 1L;

   public RoboticonTransferHandler()
      {
      super();
      }

   public RoboticonTransferHandler(boolean bCanImport)
      {
      super();
      this.bCanImport = bCanImport;
      }

   public boolean importData(JComponent c, Transferable t)
      {
      if (!canImport(c, t.getTransferDataFlavors()))
         {
         return false;
         }

      try
         {
         if (hasRoboticonFlavor(t.getTransferDataFlavors()))
            {
            RoboticonFile theRoboticon = (RoboticonFile)t
                  .getTransferData(roboticonImportFlavor);

            return theRoboticon.saveAsPrivate();
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
      }

   public boolean canImport(JComponent c, DataFlavor[] flavors)
      {
      boolean res = this.bCanImport && hasRoboticonImportFlavor(flavors);

      return res;
      }

   protected Transferable createTransferable(JComponent c)
      {
      if (c != null && c instanceof JList)
         {
         JList source = (JList)c;
         Object value = source.getSelectedValue();

         if (value == null
             || !(value instanceof RoboticonFile))
            {
            return null;
            }

         return new RoboticonTransferable((RoboticonFile)value);
         }
      return null;
      }

   public class RoboticonTransferable implements Transferable
      {
      RoboticonFile data;

      public RoboticonTransferable(RoboticonFile e)
         {
         data = e;
         }

      public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
         {
         if (!isDataFlavorSupported(flavor))
            {
            throw new UnsupportedFlavorException(flavor);
            }
         if (flavor.equals(roboticonFlavor))
            {
            if (data.isPublic())
               {
               //save hidden file
               RoboticonFile.saveToFile(data, data.unsavedXML);
               data.deleteOnExit();
               }
            return data;
            }
         else if (flavor.equals(roboticonImportFlavor))
            {
            return data;
            }
         //string flavor
         if (data.isPublic())
            {
            return null;//can't attach public roboticon
            }
         return data.getAbsolutePath();
         }

      public DataFlavor[] getTransferDataFlavors()
         {
         return new DataFlavor[]{roboticonFlavor, roboticonImportFlavor, DataFlavor.stringFlavor};
         }

      public boolean isDataFlavorSupported(DataFlavor flavor)
         {
         DataFlavor[] flavors = getTransferDataFlavors();

         for (int i = 0; i < flavors.length; i++)
            {
            if (flavor.equals(flavors[i]))
               {
               return true;
               }
            }
         return false;
         }
      }

   public int getSourceActions(JComponent c)
      {
      return COPY_OR_MOVE;
      }
   }
