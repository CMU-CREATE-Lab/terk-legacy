package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;

public class RoboticonFile extends File
   {
   private static final long serialVersionUID = 7159914470543297749L;

   private static final String SEQUENCE_IDENTIFIER = "seq_version";

   public RoboticonType roboticonType = null;

   public String senderId = "";

   public long timestamp;

   public String unsavedXML = null;

   /**
    * Converts an array of file objects to an array of RoboticonFiles. Returned
    * array will not include: - nulls - File objects that represent directories
    */
   public static Collection<RoboticonFile> toRoboticonFileList(
         File[] fileList, RoboticonType type)
      {
      Collection<RoboticonFile> rFileList = new ArrayList<RoboticonFile>(0);
      if (fileList != null && type != null)
         {
         rFileList = new ArrayList<RoboticonFile>(fileList.length);
         for (int i = 0; i < fileList.length; i++)
            {
            File file = fileList[i];
            if (file != null && file.isFile())
               {
               rFileList.add(new RoboticonFile(fileList[i], type, file.lastModified()));
               }
            }
         }
      return rFileList;
      }

   public static Collection<RoboticonFile> toRoboticonFileList(
         List<Roboticon> inputList, String sender, long timestamp)
      {
      Collection<RoboticonFile> rFileList = new ArrayList<RoboticonFile>(0);
      if (inputList != null)
         {
         rFileList = new ArrayList<RoboticonFile>(inputList.size());
         for (Roboticon i : inputList)
            {
            File file = new File(RoboticonManagerModel.publicRoboticonPath + i.filename);
            RoboticonType type = i.xml.indexOf(SEQUENCE_IDENTIFIER) >= 0 ? RoboticonType.SEQUENCE
                                                                         : RoboticonType.EXPRESSION;
            RoboticonFile toAdd = new RoboticonFile(file, type, timestamp);
            toAdd.senderId = i.creator.equals("Me") ? sender : i.creator;
            toAdd.setLastModified(timestamp);
            toAdd.unsavedXML = i.xml;
            rFileList.add(toAdd);
            }
         }
      return rFileList;
      }

   public boolean isPublic()
      {
      return this.unsavedXML != null;
      }

   private RoboticonFile(File file, RoboticonType type, long ts)
      {
      super(file.getAbsolutePath());
      roboticonType = type;
      timestamp = ts;
      }

   public boolean equals(java.lang.Object rhs)
      {
      if (this == rhs)
         {
         return true;
         }
      RoboticonFile _r = null;
      try
         {
         _r = (RoboticonFile)rhs;
         }
      catch (ClassCastException ex)
         {
         }

      if (_r != null)
         {
         if (this.roboticonType != _r.roboticonType)
            {
            return false;
            }
         if (this.getName() != _r.getName()
             && !this.getName().equals(_r.getName()))
            {
            return false;
            }
         if (this.senderId != _r.senderId && senderId != null
             && !senderId.equals(_r.senderId))
            {
            return false;
            }

         return true;
         }

      return false;
      }

   public static void saveToFile(File file, String unsavedXML)
      {
      try
         {
         if (file.exists())
            {
            file.delete();
            }
         FileOutputStream fout = new FileOutputStream(file);

         // Print a line of text
         new PrintStream(fout).println(unsavedXML);

         // Close our output stream
         fout.close();
         }
      catch (IOException e)
         {
         // TODO Auto-generated catch block
         e.printStackTrace();
         }
      }

   public boolean saveAsPrivate()
      {
      if (!this.isPublic())
         {
         return false;//can't import private
         }
      //save seq or expr
      final boolean sequence = this.roboticonType == RoboticonType.SEQUENCE;
      String path = RoboticonManagerModel.TERK_PATH;
      if (sequence)
         {
         //save seq
         path += "Sequences" + File.separator;
         }
      else
         {
         //save expr
         path += "Expressions" + File.separator;
         }

      File newPrivate = new File(path + this.getName());
      if (newPrivate.exists())
         {
         return false;
         }
      RoboticonFile.saveToFile(newPrivate, this.unsavedXML);
      return true;
      }
   }
