package edu.cmu.ri.createlab.TeRK.robot.hummingbird.services;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.BaseAudioServiceImpl;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioServiceSerialImpl extends BaseAudioServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AudioServiceSerialImpl.class);

   static AudioServiceSerialImpl create(final HummingbirdProxy hummingbirdProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.AUDIO_DEVICE_COUNT);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, HummingbirdConstants.AUDIO_DEVICE_MIN_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, HummingbirdConstants.AUDIO_DEVICE_MAX_AMPLITUDE);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, HummingbirdConstants.AUDIO_DEVICE_MIN_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, HummingbirdConstants.AUDIO_DEVICE_MAX_DURATION);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, HummingbirdConstants.AUDIO_DEVICE_MIN_FREQUENCY);
      basicPropertyManager.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, HummingbirdConstants.AUDIO_DEVICE_MAX_FREQUENCY);

      return new AudioServiceSerialImpl(hummingbirdProxy,
                                        basicPropertyManager);
      }

   private final HummingbirdProxy hummingbirdProxy;
   private final Executor executor = Executors.newCachedThreadPool();

   private AudioServiceSerialImpl(final HummingbirdProxy hummingbirdProxy,
                                  final PropertyManager propertyManager)
      {
      super(propertyManager);
      this.hummingbirdProxy = hummingbirdProxy;
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      hummingbirdProxy.playTone(frequency, amplitude, duration);
      }

   public void playSound(final byte[] sound)
      {
      hummingbirdProxy.playClip(sound);
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
                  hummingbirdProxy.playTone(frequency, amplitude, duration);
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
                  hummingbirdProxy.playClip(sound);
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