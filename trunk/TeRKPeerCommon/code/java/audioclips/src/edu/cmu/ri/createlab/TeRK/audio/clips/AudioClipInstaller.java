package edu.cmu.ri.createlab.TeRK.audio.clips;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.Scanner;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>AudioClipInstaller</code> helps install audio clips into a user's TeRK directory (which is located under the
 * user's home directory).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class AudioClipInstaller
   {
   private static final Logger LOG = Logger.getLogger(AudioClipInstaller.class);

   private static final AudioClipInstaller INSTANCE = new AudioClipInstaller();

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AudioClipInstaller.class.getName());
   private static final int BUFFER_SIZE = 4096;
   private static final String FILENAME_DELIMITER = ",";

   public static AudioClipInstaller getInstance()
      {
      return INSTANCE;
      }

   /**
    * Installs sound clips to the directory specified by {@link TerkConstants.FilePaths#AUDIO_DIR}.  Existing files will
    * not be overwritten.
    */
   public void install()
      {
      // create a scanner to read the comma-delimited list of clip filenames
      final Scanner scanner = new Scanner(RESOURCES.getString("clip.filenames"));
      scanner.useDelimiter(FILENAME_DELIMITER);

      // iterate over all the filenames and install each one
      while (scanner.hasNext())
         {
         installAudioFile("/" + scanner.next());
         }
      }

   /**
    * Copy an audio file from the JAR to the user's filesystem.
    */
   private void installAudioFile(final String audioFilename)
      {
      BufferedInputStream inputStream = null;
      BufferedOutputStream outputStream = null;
      try
         {
         // set up the input stream
         inputStream = new BufferedInputStream(this.getClass().getResourceAsStream(audioFilename));

         // make sure the audio directory exists
         final File audioDirectory = TerkConstants.FilePaths.AUDIO_DIR;
         audioDirectory.mkdirs();
         if (audioDirectory.mkdirs() || audioDirectory.exists())
            {
            final File file = new File(audioDirectory, audioFilename);

            if (file.exists())
               {
               LOG.info("AudioClipInstaller.installAudioFile(): file [" + file + "] already exists, so I won't overwrite it.");
               }
            else
               {
               // set up the output stream
               outputStream = new BufferedOutputStream(new FileOutputStream(file));

               final byte[] buffer = new byte[BUFFER_SIZE];
               int bytesRead;
               while ((bytesRead = inputStream.read(buffer)) >= 0)
                  {
                  outputStream.write(buffer, 0, bytesRead);
                  }

               LOG.info("AudioClipInstaller.installAudioFile(): successfully installed audio file [" + file + "]");
               }
            }
         else
            {
            LOG.error("AudioClipInstaller.installAudioFile(): Failed to create the audio directory [" + audioDirectory + "]");
            }
         }
      catch (final FileNotFoundException e)
         {
         LOG.error("AudioClipInstaller.installAudioFile(): Could not create the audio output file", e);
         }
      catch (final IOException e)
         {
         LOG.error("AudioClipInstaller.installAudioFile(): IOException while reading or writing the audio file", e);
         }
      finally
         {
         if (outputStream != null)
            {
            try
               {
               outputStream.close();
               }
            catch (final IOException e)
               {
               // nothing we can really do here, so just log the error
               LOG.error("AudioClipInstaller.installAudioFile(): IOException while closing the outputStream");
               }
            }
         if (inputStream != null)
            {
            try
               {
               inputStream.close();
               }
            catch (final IOException e)
               {
               // nothing we can really do here, so just log the error
               LOG.error("AudioClipInstaller.installAudioFile(): IOException while closing the inputstream");
               }
            }
         }
      }

   private AudioClipInstaller()
      {
      // private to prevent instantiation
      }
   }
