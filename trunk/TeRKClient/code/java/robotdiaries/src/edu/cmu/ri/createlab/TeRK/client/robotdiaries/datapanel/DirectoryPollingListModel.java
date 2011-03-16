package edu.cmu.ri.createlab.TeRK.client.robotdiaries.datapanel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import javax.swing.AbstractListModel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.AbstractFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;

abstract class DirectoryPollingListModel<O> extends AbstractListModel
   {
   private static final Logger LOG = Logger.getLogger(DirectoryPollingListModel.class);

   private static final String XML_EXTENSION = ".xml";
   private static final FilenameFilter XML_FILENAME_FILTER =
         new FilenameFilter()
         {
         public boolean accept(final File dir, final String name)
            {
            return name != null && name.toLowerCase().endsWith(XML_EXTENSION);
            }
         };

   private Timer pollingTimer;
   private final File directory;
   private final AbstractFileHandler<O> fileHandler;

   private final byte[] dataSynchronizationLock = new byte[0];
   private final TreeList items = new TreeList();
   private final SortedMap<File, FileEntry<O>> fileEntryMap = new TreeMap<File, FileEntry<O>>();
   private File[] filesArray;
   private final HashMap<File, Long> fileModificationTimeMap = new HashMap<File, Long>();

   interface EventHandler
      {
      void handleNewFileEvent(final Set<File> files);

      void handleModifiedFileEvent(final Set<File> files);

      void handleDeletedFileEvent(final Set<File> files);
      }

   private final EventHandler eventHandler =
         new EventHandler()
         {
         public void handleNewFileEvent(final Set<File> files)
            {
            if ((files != null) && (!files.isEmpty()))
               {
               for (final File file : files)
                  {
                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("DirectoryPollingListModel.handleNewFileEvent(" + file.getName() + ")");
                     }
                  final FileEntry<O> entry = createFileEntry(file);
                  if (entry != null)
                     {
                     final int insertionPostion;
                     synchronized (dataSynchronizationLock)
                        {
                        // Do a binary search to figure out where to insert this entry since TreeList's add() and addAll()
                        // methods merely (and unintuitively, IMO!) append to the end of the list.
                        final int searchResult = Collections.binarySearch(items, entry);
                        if (searchResult >= 0)
                           {
                           LOG.error("File " + file.getName() + " already exists in the list (this should never happen).");
                           return;
                           }

                        // compute the insertion position from the search result (see javadocs for binarySearch())
                        insertionPostion = -(searchResult + 1);
                        items.add(insertionPostion, entry);
                        fileEntryMap.put(file, entry);
                        }
                     if (insertionPostion >= 0)
                        {
                        performAfterRefresh();
                        fireIntervalAdded(this, insertionPostion, insertionPostion);
                        printSizes();
                        if (LOG.isInfoEnabled())
                           {
                           LOG.info("File added: [" + file.getName() + "]");
                           }
                        }
                     else
                        {
                        LOG.error("DirectoryPollingListModel.handleModifiedFileEvent(): Index for file [" + file + "] not found!");
                        }
                     }
                  else
                     {
                     LOG.error("File [" + file.getName() + "] is invalid.  Ignoring.");
                     }
                  }
               }
            }

         public void handleModifiedFileEvent(final Set<File> files)
            {
            if ((files != null) && (!files.isEmpty()))
               {
               for (final File file : files)
                  {
                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("DirectoryPollingListModel.handleModifiedFileEvent(" + file.getName() + ")");
                     }

                  int index = -1;
                  synchronized (dataSynchronizationLock)
                     {
                     // look up the file entry
                     final FileEntry<O> fileEntry = fileEntryMap.get(file);
                     if (fileEntry != null)
                        {
                        // get the index
                        index = items.indexOf(fileEntry);
                        }
                     }
                  if (index >= 0)
                     {
                     performAfterRefresh();
                     fireContentsChanged(this, index, index);
                     printSizes();
                     if (LOG.isInfoEnabled())
                        {
                        LOG.info("File modified: [" + file.getName() + "]");
                        }
                     }
                  else
                     {
                     LOG.error("DirectoryPollingListModel.handleModifiedFileEvent(): Index for file [" + file + "] not found!");
                     }
                  }
               }
            }

         public void handleDeletedFileEvent(final Set<File> files)
            {
            if ((files != null) && (!files.isEmpty()))
               {
               for (final File file : files)
                  {
                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("DirectoryPollingListModel.handleDeletedFileEvent(" + file.getName() + ")");
                     }

                  int index = -1;
                  synchronized (dataSynchronizationLock)
                     {
                     final FileEntry fileEntry = fileEntryMap.remove(file);
                     if (fileEntry != null)
                        {
                        index = items.indexOf(fileEntry);
                        final boolean removed = items.remove(fileEntry);
                        if (LOG.isTraceEnabled())
                           {
                           LOG.trace("Removed? [" + fileEntry.getFilename() + "] and [" + removed + "]");
                           }
                        }
                     }
                  if (index >= 0)
                     {
                     performAfterRefresh();
                     fireIntervalRemoved(this, index, index);
                     printSizes();
                     if (LOG.isInfoEnabled())
                        {
                        LOG.info("File deleted: [" + file.getName() + "]");
                        }
                     }
                  else
                     {
                     LOG.error("DirectoryPollingListModel.handleDeletedFileEvent(): Index for file [" + file + "] not found!");
                     }
                  }
               }
            }
         };

   private void printSizes()
      {
      if (LOG.isTraceEnabled())
         {
         synchronized (dataSynchronizationLock)
            {
            LOG.trace("SIZES: " +
                      items.size() + " " +
                      fileEntryMap.size() + " " +
                      fileModificationTimeMap.size() + " " +
                      filesArray.length);
            }
         }
      }

   protected DirectoryPollingListModel(final File directory, final AbstractFileHandler<O> fileHandler)
      {
      this.directory = directory;
      if (this.directory == null || !this.directory.isDirectory() || !this.directory.exists())
         {
         throw new IllegalArgumentException("The given directory [" + directory + "] either does not exist or is not a directory");
         }
      this.fileHandler = fileHandler;

      // initialize the collections (no need to synchonize here)
      filesArray = getFileList();
      int index = 0;
      for (int i = 0; i < filesArray.length; i++)
         {
         final File file = filesArray[i];
         final FileEntry<O> entry = createFileEntry(file);
         if (entry != null)
            {
            items.add(index++, entry);
            fileEntryMap.put(file, entry);
            fileModificationTimeMap.put(file, file.lastModified());
            }
         }

      // call refresh so that the icons get loaded
      performAfterRefresh();

      // start the directory-polling timer
      startTask();
      }

   private FileEntry<O> createFileEntry(final File file)
      {
      // create the object
      final FileEntry<O> entry = new FileEntry<O>(file, "Me", 0, this.fileHandler);

      // make sure it's valid
      final Object tempObject = entry.getObject();
      if (tempObject == null)
         {
         return null;
         }

      return entry;
      }

   private void startTask()
      {
      final TimerTask task =
            new TimerTask()
            {
            public void run()
               {
               final Set<File> newFiles = new HashSet<File>();
               final Set<File> modifiedFiles = new HashSet<File>();
               final Set<File> deletedFiles = new HashSet<File>();

               synchronized (dataSynchronizationLock)
                  {
                  final HashSet<File> checkedFiles = new HashSet<File>();
                  filesArray = getFileList();

                  // scan the files and check for modification/addition
                  for (int i = 0; i < filesArray.length; i++)
                     {
                     final File file = filesArray[i];
                     final Long current = fileModificationTimeMap.get(file);
                     checkedFiles.add(file);
                     if (current == null)
                        {
                        // new file
                        fileModificationTimeMap.put(file, file.lastModified());
                        newFiles.add(file);
                        }
                     else if (current.longValue() != file.lastModified())
                        {
                        // modified file
                        fileModificationTimeMap.put(file, file.lastModified());
                        modifiedFiles.add(file);
                        }
                     }

                  // now check for deleted files
                  final Set<File> files = new HashSet<File>(fileModificationTimeMap.keySet());
                  files.removeAll(checkedFiles);
                  for (final File file : files)
                     {
                     fileModificationTimeMap.remove(file);
                     deletedFiles.add(file);
                     }
                  }

               // notify the handler of new/modified/removed files
               eventHandler.handleNewFileEvent(newFiles);
               eventHandler.handleModifiedFileEvent(modifiedFiles);
               eventHandler.handleDeletedFileEvent(deletedFiles);
               }
            };

      this.pollingTimer = new Timer("DirectoryPollingTimer_" + directory.getAbsolutePath(), true);
      this.pollingTimer.scheduleAtFixedRate(task, 0, 1000);
      }

   private File[] getFileList()
      {
      return directory.listFiles(XML_FILENAME_FILTER);
      }

   private void stopTask()
      {
      this.pollingTimer.cancel();
      }

   public final int getSize()
      {
      synchronized (dataSynchronizationLock)
         {
         return items.size();
         }
      }

   public final Object getElementAt(final int index)
      {
      synchronized (dataSynchronizationLock)
         {
         if (index >= 0 && index < items.size())
            {
            return items.get(index);
            }
         }
      return null;
      }

   public final void forceDirectoryPoll()
      {
      stopTask();
      startTask();
      }

   protected abstract void performAfterRefresh();
   }
