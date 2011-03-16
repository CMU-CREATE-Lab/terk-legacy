package finch;

import java.io.File;
import java.io.IOException;
import edu.cmu.ri.createlab.TeRK.audio.AudioHelper;
import edu.cmu.ri.mrpl.TeRK.speech.Mouth;
import edu.cmu.ri.mrpl.util.io.FileUtils;

/**
 * Created by: Tom Lauwers (tlauwers@andrew.cmu.edu)
 * Date: Jan 15, 2009
 */
public class SoundPlayer
   {

   /**
    *  Instantiates the soundPlayer object
    */
   public SoundPlayer()
      {

      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds.  Middle C is about 262Hz.  Visit http://www.phy.mtu.edu/~suits/notefreqs.html for
    * frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param duration The time to play the tone in milliseconds
    */
   public void playTone(int frequency, int duration)
      {

      AudioHelper.playTone(frequency, 100, duration);
      }

   /**
    * Plays a tone over the computer speakers or headphones at a given frequency (in Hertz) for
    * a specified duration in milliseconds at a specified volume.  Middle C is about 262Hz.
    * Visit http://www.phy.mtu.edu/~suits/notefreqs.html for frequencies of musical notes.
    *
    * @param frequency The frequency of the tone in Hertz
    * @param volume The volume of the tone on a 1 to 10 scale
    * @param duration The time to play the tone in milliseconds
    */
   public void playTone(int frequency, int volume, int duration)
      {
      AudioHelper.playTone(frequency, volume, duration);
      }

   /**
    * Plays a wav or mp3 file at the specificied fileLocation path.  If you place the audio
    * file in the same path as your source, you can just specify the name of the file.
    *
    * @param     fileLocation Absolute path of the file or name of the file if located in some directory as source code
    */
   public void playClip(String fileLocation)
      {

      try
         {
         File file = new File(fileLocation);
         byte[] rawSound = FileUtils.getFileAsBytes(file);
         AudioHelper.playClip(rawSound);
         }
      catch (IOException e)
         {
         System.out.println("Error playing sound file, might not exist");
         }
      }

   /**
    * Takes the text of 'sayThis' and synthesizes it into a sound file.  Plays the sound file over
    * computer speakers.  sayThis can be arbitrarily long and can include variable arguments.
    *
    * Example:
    *   finch.saySomething("My light sensor has a value of "+ lightSensor + " and temperature is " + tempInCelcius);
    *
    * @param     sayThis The string of text that will be spoken by the computer
    */
   public void saySomething(String sayThis)
      {
      final Mouth mouth = Mouth.getInstance();

      if (mouth != null)
         {
         AudioHelper.playClip(mouth.getSpeech(sayThis));
         }
      else
         {
         System.out.println("Speech not working");
         }
      }
   }
