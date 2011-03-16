package edu.cmu.ri.createlab.TeRK.client.robotdiaries;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import org.apache.log4j.Logger;

public abstract class AbstractFileHandler<O>
   {
   protected static final Logger LOG = Logger.getLogger(AbstractFileHandler.class);

   /**
    * Method to encode and save xml representation of an object
    * @return whether the file was successfully saved
    */
   public boolean saveFile(O object, File filename)
      {
      try
         {
         final XMLEncoder encoder;

         final Hashtable savedRep = getRepresentation(object);

         filename.createNewFile();

         encoder = new XMLEncoder(new BufferedOutputStream(
               new FileOutputStream(filename)));
         encoder.writeObject(savedRep);
         encoder.close();
         }
      catch (Exception e)
         {
         LOG.error("Error " + this.getClass() + ": ", e);
         e.printStackTrace();
         return false;
         }

      return true;
      }

   /**
    * Method to deserialize xml representation of an object
    *
    * @return The object stored in this file
    */
   public O openFile(File filename)
      {
      Hashtable savedRep = decodeFile(filename);

      if (savedRep == null)
         {
         return null;
         }

      return setRepresentation(savedRep);
      }

   /**
    * A utility method to extract all fields from the xml file
    * created by XMLEncoder to the Hashtable.
    *
    * @param filename The xml file created by XMLEncoder
    * @return a Hashtable of all fields in the xml file
    */
   protected Hashtable decodeFile(final File filename)
      {
      try
         {
         final XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(
               new FileInputStream(filename)));

         final Hashtable savedRep = (Hashtable)decoder.readObject();

         return savedRep;
         }
      catch (FileNotFoundException e)
         {
         LOG.error("FileNotFoundException while trying to decode file [" + filename + "]", e);
         return null;
         }
      }

   /**
    * Returns one Hashtable which contains enough information to reconstruct the current object.
    * Used for serialization.
    */
   public abstract Hashtable getRepresentation(O object);

   /**
    * Reconstructs the object
    * Used for de-serialization.
    */
   public abstract O setRepresentation(Hashtable hash);
   }