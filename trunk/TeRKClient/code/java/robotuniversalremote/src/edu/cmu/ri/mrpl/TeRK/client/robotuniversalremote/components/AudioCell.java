package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AudioControlPanel;

public class AudioCell extends DefaultCell
   {

   public static final String SPEAKER_NAME = "Speaker";
   public static final String IS_AUDIO_CLIP_SELECTED_KEY = "IsAudioClipSelected";
   public static final String AUDIO_FILE_NAME_KEY = "Audio";
   public static final String TEXT_TO_SPEAK_KEY = "TextToSpeak";
   public static final String SPEAKER_DEFAULT_SOUND = "hello.wav";

   private boolean isClipSelected = true;
   private String audioClipFileName = "";
   private String textToSpeak = "";

   public AudioCell()
      {
      super();
      }

   public AudioCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return SPEAKER_NAME;
      }

   public String getValueKey()
      {
      return AUDIO_FILE_NAME_KEY;
      }

   public static Color getColor()
      {
      return Color.pink;
      }

   public boolean isClipSelected()
      {
      return isClipSelected;
      }

   public void setIsClipSelected(final boolean isClipSelected)
      {
      this.isClipSelected = isClipSelected;
      }

   public String getAudio()
      {
      return audioClipFileName;
      }

   public void setAudio(final String audioFileName)
      {
      this.audioClipFileName = audioFileName;
      }

   public String getTextToSpeak()
      {
      return textToSpeak;
      }

   public void setTextToSpeak(final String textToSpeak)
      {
      this.textToSpeak = textToSpeak;
      }

   public Hashtable getValues()
      {
      final Hashtable values = new Hashtable();
      values.put(IS_AUDIO_CLIP_SELECTED_KEY, isClipSelected);
      values.put(AUDIO_FILE_NAME_KEY, audioClipFileName);
      values.put(TEXT_TO_SPEAK_KEY, textToSpeak);
      return values;
      }

   public void setValues(final Hashtable values)
      {
      final Object isClipValue = values.get(IS_AUDIO_CLIP_SELECTED_KEY);
      final Boolean isClip = (isClipValue == null) ? Boolean.TRUE : (Boolean)isClipValue;
      final String theAudioClipFileName = isClip ? (String)values.get(AUDIO_FILE_NAME_KEY) : "";
      final String theTextToSpeak = isClip ? "" : (String)values.get(TEXT_TO_SPEAK_KEY);
      setIsClipSelected(isClip);
      setAudio(theAudioClipFileName);
      setTextToSpeak(theTextToSpeak);
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      //create panel
      final AbstractControlPanel panel = new AudioControlPanel(c.getAudioService(), deviceId, this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
