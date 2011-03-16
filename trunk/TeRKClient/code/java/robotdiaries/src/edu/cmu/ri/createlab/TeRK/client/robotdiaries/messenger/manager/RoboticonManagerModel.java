package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.manager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.swing.AbstractListModel;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.expressions.ExpressionFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceFileHandler;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerListener;
import org.apache.commons.collections.list.TreeList;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"unchecked"})
public final class RoboticonManagerModel extends AbstractListModel
   {
   private final Set<RoboticonManagerListener> roboticonManagerListeners = new HashSet<RoboticonManagerListener>();

   private final TreeList fileEntries = new TreeList();

   private final RoboticonType type;

   public static File publicExpressionsPath = TerkConstants.FilePaths.EXPRESSIONS_PUBLIC_DIR;
   public static File publicSequencesPath = TerkConstants.FilePaths.SEQUENCES_PUBLIC_DIR;

   public RoboticonManagerModel(RoboticonType type)
      {
      this.type = type;
      }

   public void addRoboticons(Collection<RoboticonFile> newRoboticonFiles)
      {
      synchronized (fileEntries)
         {
         for (RoboticonFile i : newRoboticonFiles)
            {
            if (i.roboticonType == type)
               {
               File publicPath = (type == RoboticonType.EXPRESSION) ? publicExpressionsPath : publicSequencesPath;
               File directory = new File(publicPath, i.senderId);
               if (!directory.exists())
                  {
                  directory.mkdirs();
                  }
               File file = new File(directory, i.getName());
               try
                  {
                  FileEntry entry;

                  BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                  writer.write(i.unsavedXML);
                  writer.flush();
                  writer.close();

                  if (i.roboticonType == RoboticonType.EXPRESSION)
                     {
                     entry = new FileEntry(file, i.senderId, i.timestamp, ExpressionFileHandler.getInstance());
                     }
                  else
                     {
                     entry = new FileEntry(file, i.senderId, i.timestamp, SequenceFileHandler.getInstance());
                     }

                  if (!contains(entry))
                     {
                     fileEntries.add(entry);
                     }
                  }
               catch (IOException x)
                  {
                  }
               }
            }
         }

      // todo: this is pretty inefficient
      fireContentsChanged(this, 0, fileEntries.size());

      synchronized (roboticonManagerListeners)
         {
         // notify listeners
         if (!roboticonManagerListeners.isEmpty())
            {
            for (final RoboticonManagerListener listener : roboticonManagerListeners)
               {
               listener.contentsChanged();
               }
            }
         }
      }

   private boolean contains(FileEntry entry)
      {
      for (Object e : fileEntries)
         {
         if (entry.isEqual((FileEntry)e))
            {
            return true;
            }
         }

      return false;
      }

   public void addRoboticonManagerListener(final RoboticonManagerListener listener)
      {
      if (listener != null)
         {
         roboticonManagerListeners.add(listener);
         }
      }

   public void removeRoboticonManagerListener(final RoboticonManagerListener listener)
      {
      if (listener != null)
         {
         roboticonManagerListeners.remove(listener);
         }
      }

   public int getSize()
      {
      return fileEntries.size();
      }

   public Object getElementAt(final int index)
      {
      return fileEntries.get(index);
      }
   }