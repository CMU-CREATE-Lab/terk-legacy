package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;

public class RoboticonDirectory extends File
   {
   private static final long serialVersionUID = -3200721675657442697L;
   private static final String XML_EXTENSION = ".xml";
   private static final FilenameFilter XML_FILENAME_FILTER =
         new FilenameFilter()
         {
         public boolean accept(final File dir, final String name)
            {
            return name != null && name.toLowerCase().endsWith(XML_EXTENSION);
            }
         };

   RoboticonType roboticonType = null;

   /**
    * Returns instance of RoboticonDirectory.
    * Returns null if:
    *  - specifed File is null or does not point to a directory.
    *  - specified RoboticonType is null
    */
   public static RoboticonDirectory getInstance(File directory, RoboticonType type)
      {
      RoboticonDirectory instance = null;
      if (directory != null /* && directory.isDirectory()*/ && type != null)
         {
         instance = new RoboticonDirectory(directory, type);
         }
      return instance;
      }

   /** Constructor */
   private RoboticonDirectory(File directory, RoboticonType type)
      {
      super(directory.getAbsolutePath());
      roboticonType = type;
      }

   /**
    * Indicates if directory contains specified type of roboticon.
    * Returns false if specified type is null
    * @param type type of Roboticon
    * @return true if directory contains specified type of roboticon
    */
   public boolean contains(RoboticonType type)
      {
      return (type != null && roboticonType.equals(type));
      }

   public Collection<RoboticonFile> getRoboticonFiles()
      {
      Collection<RoboticonFile> fileList = new ArrayList<RoboticonFile>(0);
      if (this.exists() && this.isDirectory())
         {
         fileList =
               RoboticonFile.toRoboticonFileList(this.listFiles(XML_FILENAME_FILTER), this.roboticonType);
         }
      return fileList;
      }

   public enum RoboticonType
      {
         EXPRESSION,
         SEQUENCE;
      }
   } //RoboticonDirectory
