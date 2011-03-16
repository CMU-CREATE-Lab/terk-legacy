package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.BaseAudioServiceImpl;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioServiceSerialImpl extends BaseAudioServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AudioServiceSerialImpl.class);

   static AudioServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.AUDIO_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, FinchConstants.AUDIO_DEVICE_MIN_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, FinchConstants.AUDIO_DEVICE_MAX_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, FinchConstants.AUDIO_DEVICE_MIN_FREQUENCY);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, FinchConstants.AUDIO_DEVICE_MAX_FREQUENCY);

      return new AudioServiceSerialImpl(finchProxy,
                                        basicPropertyManager);
      }

   private final FinchProxy finchProxy;
   private final Executor executor = Executors.newCachedThreadPool();

   private AudioServiceSerialImpl(final FinchProxy finchProxy,
                                  final PropertyManager propertyManager)
      {
      super(propertyManager);
      this.finchProxy = finchProxy;
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      finchProxy.playTone(frequency, amplitude, duration);
      }

   public void playSound(final byte[] sound)
      {
      finchProxy.playClip(sound);
      }

   public void playToneAsynchronously(final int frequency, final int amplitude, final int duration, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  finchProxy.playTone(frequency, amplitude, duration);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the tone asynchronously", e);
         callback.handleException(e);
         }
      }

   public void playSoundAsynchronously(final byte[] sound, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      try
         {
         executor.execute(
               new Runnable()
               {
               public void run()
                  {
                  finchProxy.playClip(sound);
                  }
               });
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to play the clip asynchronously", e);
         callback.handleException(e);
         }
      }
   }