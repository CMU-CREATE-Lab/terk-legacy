/**
 *
 *  @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 */
package TTS;

import com.sun.speech.freetts.FreeTTS;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

public class TTS
   {

   // Use the kevin16 voice (very common computerized voice)
   String voiceName = "kevin16";

   /** Default constructor for Text To Speech synthesizer */
   public TTS()
      {
      // Do nothing on instantiation
      }

   /** Synthesizes a string of text into a .wav audio file in the file location specified by <code>fileName</code>
    *
    * @param text The string of text to be synthesized
    * @param fileName The path pointing to where the audio file should be placed, for example 'C:/sounds/monkey.wav'
    */

   public void saveTextAsWav(String text, String fileName)
      {

      /* The VoiceManager manages all the voices for FreeTTS.
      */
      VoiceManager voiceManager = VoiceManager.getInstance();
      Voice helloVoice = voiceManager.getVoice(voiceName);

      // Insantiate a new voice
      FreeTTS freetts = new FreeTTS(helloVoice);
      // Sets the audio file to dump the audio information into
      freetts.setAudioFile(fileName);
      /* Allocates the resources for the voice.
      */
      freetts.startup();

      /* Synthesize speech.
      */
      freetts.textToSpeech("terk " + text);

      /* Clean up and leave.
      */
      freetts.shutdown();
      }

   /** Synthesizes the text passed to the method and the resulting audio over the <b>computer's</b> speakers
    *
    * @param text String with the text to be synthesized and spoken by the computer
    */
   public void speakText(String text)
      {

      /* The VoiceManager manages all the voices for FreeTTS.
      */
      VoiceManager voiceManager = VoiceManager.getInstance();
      Voice helloVoice = voiceManager.getVoice(voiceName);

      // Insantiate a new voice
      FreeTTS freetts = new FreeTTS(helloVoice);
      /* Allocates the resources for the voice.
      */
      freetts.startup();

      /* Synthesize speech.
      */
      freetts.textToSpeech(text);

      /* Clean up and leave.
      */
      freetts.shutdown();
      }

   /* The following methods allow for concetenation of multiple strings to a single wav
     * file.  The methods are openWav, addTextToWav, and closeWav.  openWav opens a wav file
     * for writing, addTextToWav appends synthesized text to the end of the open wav file,
     * and closeWav closes the file.  Once closeWav is called, the wav file is ready for playing
     * on either the robot's or computer's speakers.

    /* The VoiceManager manages all the voices for FreeTTS.
     */
   VoiceManager voiceManagerPublic = VoiceManager.getInstance();

   // The voice object
   Voice voice = voiceManagerPublic.getVoice(voiceName);

   // The public freeTTS object
   FreeTTS freettsPublic = new FreeTTS(voice);

   /** Open the wav file pointed to by 'fileName' for writing
    *
    * @param fileName The path pointing to where the audio file should be placed, for example 'C:/sounds/monkey.wav'
    */
   public void openWav(String fileName)
      {
      freettsPublic.setAudioFile(fileName);
      /* Allocates the resources for the voice.
      */
      freettsPublic.startup();
      }

   /** Synthesizes <code>text</code> and appends the resulting sound data to the end of the wav file opened by {@link #openWav(String)}.
    * Note that you must used the {@link #openWav(String)} method before calling this method
    * @param text The string of text to be synthesized and appended to the wav file
    */
   public void addTextToWav(String text)
      {
      freettsPublic.textToSpeech(text);
      }

   /** Closes the wav file opened by {@link #openWav(String)}, allowing it to be played by audioplaying methods. */
   public void closeWav()
      {
      freettsPublic.shutdown();
      }
   }