package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractListModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;
import org.apache.commons.collections.list.TreeList;

//import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.AbstractListCellRenderer;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"unchecked"})
public final class RoboticonManagerModel extends AbstractListModel
   {
   private static final String XML_EXTENSION = ".xml";
   private static final FilenameFilter XML_FILENAME_FILTER =
         new FilenameFilter()
         {
         public boolean accept(final File dir, final String name)
            {
            return name != null && name.toLowerCase().endsWith(XML_EXTENSION);
            }
         };

   public static final String TERK_PATH = System.getProperty("user.home") + File.separator + "TeRK" + File.separator;

   private static final List<RoboticonDirectory> ROBOTICON_DIRECTORIES;

   private final Set<RoboticonManagerListener> roboticonManagerListeners = new HashSet<RoboticonManagerListener>();
   private final TreeList roboticonFiles = new TreeList();
   public static String publicRoboticonPath = RoboticonManagerModel.TERK_PATH + "Public" + File.separator;

   static
      {
      // build the collection of search directories
      final List<RoboticonDirectory> roboticonDirectories = new ArrayList<RoboticonDirectory>(4);
      roboticonDirectories.add(RoboticonDirectory.getInstance(new File(TERK_PATH, "Expressions"), RoboticonType.EXPRESSION));
      roboticonDirectories.add(RoboticonDirectory.getInstance(new File(TERK_PATH, "Sequences"), RoboticonType.SEQUENCE));
      ROBOTICON_DIRECTORIES = Collections.unmodifiableList(roboticonDirectories);
      }

   public RoboticonManagerModel(boolean bFile)
      {
      if (bFile)
         {
         // start the directory-polling timer
         final Timer pollingTimer = new Timer("RoboticonPollingTimer", true);
         pollingTimer.scheduleAtFixedRate(
               new TimerTask()
               {
               public void run()
                  {
                  checkDirectoriesForRoboticonFiles();
                  }
               },
               0,
               1000);
         }
      }

   private void checkDirectoriesForRoboticonFiles()
      {
      final TreeList newRoboticonFiles = new TreeList();
      for (final RoboticonDirectory searchDirectory : ROBOTICON_DIRECTORIES)
         {
         //TODO fix: if directory created msgr is running, cannot be read
         if (searchDirectory != null && searchDirectory.exists())
            {
            final Collection<RoboticonFile> roboticonFiles = searchDirectory.getRoboticonFiles();
            if ((roboticonFiles != null) && !(roboticonFiles.isEmpty()))
               {
               newRoboticonFiles.addAll(roboticonFiles);
               }
            }
         }

      synchronized (roboticonFiles)
         {
         roboticonFiles.clear();
         roboticonFiles.addAll(newRoboticonFiles);
         }

      // todo: this is pretty inefficient
      fireContentsChanged(this, 0, roboticonFiles.size());

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

   public void addRoboticons(Collection<RoboticonFile> newRoboticonFiles)
      {
      synchronized (roboticonFiles)
         {
         for (RoboticonFile i : newRoboticonFiles)
            {
            if (!roboticonFiles.contains(i))
               {
               roboticonFiles.add(i);
               }
            }
         }

      // todo: this is pretty inefficient
      fireContentsChanged(this, 0, roboticonFiles.size());

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
      return roboticonFiles.size();
      }

   public Object getElementAt(final int index)
      {
      return roboticonFiles.get(index);
      }
   }
