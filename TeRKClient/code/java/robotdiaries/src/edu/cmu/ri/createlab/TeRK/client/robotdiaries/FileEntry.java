package edu.cmu.ri.createlab.TeRK.client.robotdiaries;

import java.io.File;
import java.io.FileNotFoundException;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

public class FileEntry<O> implements Comparable<FileEntry>
   {
   private static final Logger LOG = Logger.getLogger(FileEntry.class);

   private final File file;
   private final String creator;
   private final long timestamp;
   private final AbstractFileHandler<O> handler;
   private final String name;

   public FileEntry(final File file, final String creator, final long timestamp, final AbstractFileHandler<O> handler)
      {
      //this.object = object;
      this.file = file;

      if (timestamp == 0)
         {
         this.timestamp = file.lastModified();
         }
      else
         {
         this.timestamp = timestamp;
         }

      this.creator = creator;
      this.handler = handler;

      final String tempName = file.getName();
      this.name = tempName.substring(0, tempName.length() - 4);
      }

   public O getObject()
      {
      return this.handler.openFile(file);
      }

   public String getXml()
      {
      String xml = "";
      try
         {
         xml = FileUtils.getFileAsString(this.file);
         }
      catch (FileNotFoundException x)
         {
         LOG.error("FileNotFoundException caught when trying to get file [" + this.file + "] as a String.");
         }
      return xml;
      }

   public long getTimestamp()
      {
      return this.timestamp;
      }

   public String getFilename()
      {
      return this.file.getName();
      }

   public String getName()
      {
      return this.name;
      }

   public String getAbsolutePath()
      {
      return this.file.getAbsolutePath();
      }

   public String getCreator()
      {
      return this.creator;
      }

   public String getToolTip()
      {
      return "Created by: " + this.getCreator();//+" on "+new Date(timestamp);
      }

   public boolean isEqual(final FileEntry that)
      {
      if (this.getCreator().equals(that.getCreator()) && this.getXml().equals(that.getXml()))
         {
         return true;
         }
      else
         {
         return false;
         }
      }

   public int compareTo(final FileEntry that)
      {
      // yes, I really meant to use == and not .equals() here, since I want to check equivalence first
      if (this == that)
         {
         return 0;
         }

      if (!this.file.equals(that.file))
         {
         return this.file.compareTo(that.file);
         }
      if (!this.creator.equals(that.creator))
         {
         return this.creator.compareTo(that.creator);
         }

      final long diff = this.timestamp - that.timestamp;
      if (diff < 0)
         {
         return -1;
         }
      else if (diff > 0)
         {
         return 1;
         }
      return 0;
      }
   }