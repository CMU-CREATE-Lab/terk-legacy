package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.audio.AudioControlPanel;
import edu.cmu.ri.createlab.TeRK.audio.AudioExpressionConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.TerkAudioClipChooser;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.TeRK.speech.Mouth;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(AudioServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AudioServiceControlPanel.class.getName());

   private final AudioService service;

   AudioServiceControlPanel(final ControlPanelManager controlPanelManager, final AudioService service)
      {
      super(controlPanelManager, service, AudioExpressionConstants.OPERATIONS_TO_PARAMETERS_MAP);
      this.service = service;
      }

   public String getDisplayName()
      {
      return RESOURCES.getString("control-panel.title");
      }

   public String getShortDisplayName()
      {
      return RESOURCES.getString("control-panel.short-title");
      }

   public void refresh()
      {
      LOG.debug("AudioServiceControlPanel.refresh()");

      // nothing to do here
      }

   protected ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex)
      {
      return new ControlPanelDevice(deviceIndex);
      }

   private final class ControlPanelDevice extends AbstractServiceControlPanelDevice
      {
      private final AudioControlPanel audioControlPanel = new AudioControlPanel(new TerkAudioClipChooser());
      private AudioControlPanelEventListener audioControlPanelEventListener = new AudioControlPanelEventListener();

      private ControlPanelDevice(final int deviceIndex)
         {
         super(deviceIndex);

         audioControlPanel.addEventListener(audioControlPanelEventListener);
         }

      public Component getComponent()
         {
         return audioControlPanel;
         }

      private void updateToneGUI(final int frequency, final int amplitude, final int duration)
         {
         // Update the GUI, but don't execute the operation
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  audioControlPanel.setFrequency(frequency);
                  audioControlPanel.setAmplitude(amplitude);
                  audioControlPanel.setDuration(duration);
                  audioControlPanel.setCurrentMode(AudioControlPanel.Mode.TONE);
                  }
               });
         }

      private void updateClipGUI(final String clipFilePath)
         {
         // Update the GUI, but don't execute the operation
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  audioControlPanel.setClipPath(clipFilePath);
                  audioControlPanel.setCurrentMode(AudioControlPanel.Mode.CLIP);
                  }
               });
         }

      private void updateSpeechGUI(final String speechText)
         {
         // Update the GUI, but don't execute the operation
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  audioControlPanel.setSpeechText(speechText);
                  audioControlPanel.setCurrentMode(AudioControlPanel.Mode.SPEECH);
                  }
               });
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         if (AudioExpressionConstants.OPERATION_NAME_TONE.equals(operationName))
            {
            final String freqStr = parameterMap.get(AudioExpressionConstants.PARAMETER_NAME_FREQUENCY);
            final String ampStr = parameterMap.get(AudioExpressionConstants.PARAMETER_NAME_AMPLITUDE);
            final String durStr = parameterMap.get(AudioExpressionConstants.PARAMETER_NAME_DURATION);
            try
               {
               final int frequency = Integer.parseInt(freqStr);
               final int amplitude = Integer.parseInt(ampStr);
               final int duration = Integer.parseInt(durStr);

               // update the GUI
               updateToneGUI(frequency, amplitude, duration);

               // execute the operation on the service
               audioControlPanelEventListener.playTone(frequency, amplitude, duration);
               return true;
               }
            catch (NumberFormatException e)
               {
               LOG.error("NumberFormatException while trying to convert frequency, amplitude, or duration to an integer.", e);
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to load or execute the operation.", e);
               }
            }
         else if (AudioExpressionConstants.OPERATION_NAME_CLIP.equals(operationName))
            {
            final String clipFilePath = parameterMap.get(AudioExpressionConstants.PARAMETER_NAME_FILE);

            try
               {
               // update the GUI
               updateClipGUI(clipFilePath);

               // execute the operation on the service
               audioControlPanelEventListener.playSound(TerkAudioClipChooser.convertFilenameToFile(clipFilePath), null);
               return true;
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to load or execute the operation.", e);
               }
            }
         else if (AudioExpressionConstants.OPERATION_NAME_SPEECH.equals(operationName))
            {
            final String spechText = parameterMap.get(AudioExpressionConstants.PARAMETER_NAME_TEXT);

            try
               {
               // update the GUI
               updateSpeechGUI(spechText);

               // execute the operation on the service
               audioControlPanelEventListener.playSpeech(spechText);
               return true;
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to load or execute the operation.", e);
               }
            }
         return false;
         }

      public String getCurrentOperationName()
         {
         if (audioControlPanel.isCurrentModePlayable())
            {
            return AudioExpressionConstants.MODE_NAME_MAP.get(audioControlPanel.getCurrentMode());
            }

         return null;
         }

      public Set<XmlParameter> buildParameters()
         {
         LOG.debug("AudioServiceControlPanel$ControlPanelDevice.buildParameters()");

         final AudioControlPanel.Mode currentMode = audioControlPanel.getCurrentMode();

         if (AudioControlPanel.Mode.TONE.equals(currentMode))
            {
            final Integer f = audioControlPanel.getFrequency();
            final Integer a = audioControlPanel.getAmplitude();
            final Integer d = audioControlPanel.getDuration();

            if (f != null && a != null && d != null)
               {
               final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
               parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_FREQUENCY, f));
               parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_AMPLITUDE, a));
               parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_DURATION, d));
               return parameters;
               }
            }
         else if (AudioControlPanel.Mode.CLIP.equals(currentMode))
            {
            final String clipPath = audioControlPanel.getClipPath();

            if (clipPath != null)
               {
               final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
               parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_FILE, clipPath));
               return parameters;
               }
            }
         else if (AudioControlPanel.Mode.SPEECH.equals(currentMode))
            {
            final String speechText = audioControlPanel.getSpeechText();

            if (speechText != null)
               {
               final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
               parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_TEXT, speechText));
               return parameters;
               }
            }

         return null;
         }

      private final class AudioControlPanelEventListener implements AudioControlPanel.EventListener
         {
         public void playTone(final int frequency, final int amplitude, final int duration)
            {
            service.playToneAsynchronously(frequency, amplitude, duration, null);
            }

         public void playSound(final File file, final AsynchronousCommandExceptionHandlerCallback myAsynchronousCommandExceptionHandlerCallback)
            {
            if (file != null)
               {
               try
                  {
                  final byte[] data = FileUtils.getFileAsBytes(file);
                  if (data != null)
                     {
                     service.playSoundAsynchronously(data, myAsynchronousCommandExceptionHandlerCallback);
                     }
                  }
               catch (IOException e)
                  {
                  LOG.error("IOException while trying to read the file for playSound()", e);
                  }
               }
            }

         public void playSpeech(final String speechText)
            {
            final byte[] speechData = Mouth.getInstance().getSpeech(speechText);
            if (speechData != null && speechData.length > 0)
               {
               service.playSoundAsynchronously(speechData, null);
               }
            else
               {
               LOG.error("AudioServiceControlPanel$ControlPanelDevice$AudioControlPanelEventListener.playSpeech(): speech byte array is null or empty");
               }
            }
         }
      }
   }