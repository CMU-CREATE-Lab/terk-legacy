package edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.audio.AudioExpressionConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceIceImpl;
import edu.cmu.ri.createlab.TeRK.audio.TerkAudioClipChooser;
import edu.cmu.ri.createlab.TeRK.expression.XmlDevice;
import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.properties.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrx;
import edu.cmu.ri.mrpl.TeRK.services.OperationExecutor;
import edu.cmu.ri.mrpl.TeRK.speech.Mouth;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 */
public final class RobotDiariesAudioServiceImpl implements AudioService, OperationExecutor
   {
   private static final Logger LOG = Logger.getLogger(RobotDiariesAudioServiceImpl.class);

   private abstract class OperationExecutionStrategy
      {
      private void executeOperation(final XmlOperation operation)
         {
         final Set<XmlDevice> xmlDevices = operation.getDevices();
         if ((xmlDevices != null) && (!xmlDevices.isEmpty()))
            {
            // todo: We currently don't support any concept of devices for audio, so just execute all of them (there'll
            // probably only be one anyway)
            for (final XmlDevice xmlDevice : xmlDevices)
               {
               if (xmlDevice != null)
                  {
                  executeOperationOnDevice(xmlDevice);
                  }
               }
            }
         }

      protected abstract void executeOperationOnDevice(final XmlDevice xmlDevice);
      }

   private final AudioServiceIceImpl audioService;
   private final Map<String, OperationExecutionStrategy> operationExecutionStrategyMap = new HashMap<String, OperationExecutionStrategy>();

   public RobotDiariesAudioServiceImpl(final AudioControllerPrx proxy)
      {
      this.audioService = AudioServiceIceImpl.create(proxy);

      operationExecutionStrategyMap.put(AudioExpressionConstants.OPERATION_NAME_TONE,
                                        new ToneOperationExecutionStrategy());
      operationExecutionStrategyMap.put(AudioExpressionConstants.OPERATION_NAME_CLIP,
                                        new ClipOperationExecutionStrategy());
      operationExecutionStrategyMap.put(AudioExpressionConstants.OPERATION_NAME_SPEECH,
                                        new SpeechOperationExecutionStrategy());
      }

   public String getProperty(final String key)
      {
      return audioService.getProperty(key);
      }

   public Integer getPropertyAsInteger(final String key)
      {
      return audioService.getPropertyAsInteger(key);
      }

   public Map<String, String> getProperties()
      {
      return audioService.getProperties();
      }

   public Set<String> getPropertyKeys()
      {
      return audioService.getPropertyKeys();
      }

   public void setProperty(final String key, final String value) throws ReadOnlyPropertyException
      {
      audioService.setProperty(key, value);
      }

   public void setProperty(final String key, final int value) throws ReadOnlyPropertyException
      {
      audioService.setProperty(key, value);
      }

   public String getTypeId()
      {
      return audioService.getTypeId();
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      audioService.playTone(frequency, amplitude, duration);
      }

   public void playSound(final byte[] sound)
      {
      audioService.playSound(sound);
      }

   public void playToneAsynchronously(final int frequency, final int amplitude, final int duration, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      audioService.playToneAsynchronously(frequency, amplitude, duration, callback);
      }

   public void playSoundAsynchronously(final byte[] sound, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      audioService.playSoundAsynchronously(sound, callback);
      }

   public Object executeOperation(final XmlOperation operation)
      {
      if (operation != null)
         {
         final OperationExecutionStrategy operationExecutionStrategy = operationExecutionStrategyMap.get(operation.getName());
         if (operationExecutionStrategy != null)
            {
            operationExecutionStrategy.executeOperation(operation);
            }
         }
      return null;
      }

   private final class ToneOperationExecutionStrategy extends OperationExecutionStrategy
      {
      protected void executeOperationOnDevice(final XmlDevice xmlDevice)
         {
         final Map<String, XmlParameter> parametersMap = xmlDevice.getParametersAsMap();
         if (parametersMap != null)
            {
            final XmlParameter frequencyParameter = parametersMap.get(AudioExpressionConstants.PARAMETER_NAME_FREQUENCY);
            final XmlParameter amplitudeParameter = parametersMap.get(AudioExpressionConstants.PARAMETER_NAME_AMPLITUDE);
            final XmlParameter durationParameter = parametersMap.get(AudioExpressionConstants.PARAMETER_NAME_DURATION);
            if (frequencyParameter != null &&
                amplitudeParameter != null &&
                durationParameter != null)
               {
               final Integer freq = frequencyParameter.getValueAsInteger();
               final Integer amp = amplitudeParameter.getValueAsInteger();
               final Integer dur = durationParameter.getValueAsInteger();
               if (freq != null &&
                   amp != null &&
                   dur != null)
                  {
                  audioService.playToneAsynchronously(freq, amp, dur, null);
                  }
               }
            }
         }
      }

   private final class ClipOperationExecutionStrategy extends OperationExecutionStrategy
      {
      protected void executeOperationOnDevice(final XmlDevice xmlDevice)
         {
         final XmlParameter fileParameter = xmlDevice.getParameter(AudioExpressionConstants.PARAMETER_NAME_FILE);
         if (fileParameter != null)
            {
            final String clipFilePath = fileParameter.getValue();
            if (clipFilePath != null)
               {
               final File file = TerkAudioClipChooser.convertFilenameToFile(clipFilePath);
               try
                  {
                  final byte[] data = FileUtils.getFileAsBytes(file);
                  audioService.playSoundAsynchronously(data, null);
                  }
               catch (IOException e)
                  {
                  LOG.error("IOException while trying to read the audio file [" + file + "]", e);
                  }
               }
            }
         }
      }

   private final class SpeechOperationExecutionStrategy extends OperationExecutionStrategy
      {
      protected void executeOperationOnDevice(final XmlDevice xmlDevice)
         {
         final XmlParameter speechParameter = xmlDevice.getParameter(AudioExpressionConstants.PARAMETER_NAME_TEXT);
         if (speechParameter != null)
            {
            final String speechText = speechParameter.getValue();
            if (speechText != null)
               {
               final byte[] speechData = Mouth.getInstance().getSpeech(speechText);
               if (speechData != null && speechData.length > 0)
                  {
                  audioService.playSoundAsynchronously(speechData, null);
                  }
               else
                  {
                  LOG.error("AudioServiceControlPanel$ControlPanelDevice$AudioControlPanelEventListener.playSpeech(): speech byte array is null or empty");
                  }
               }
            }
         }
      }
   }