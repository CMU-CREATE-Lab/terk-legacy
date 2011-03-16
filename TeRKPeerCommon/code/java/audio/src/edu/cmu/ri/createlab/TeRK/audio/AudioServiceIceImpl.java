package edu.cmu.ri.createlab.TeRK.audio;

import Ice.LocalException;
import Ice.UserException;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.AMI_AudioController_execute;
import edu.cmu.ri.mrpl.TeRK.AudioCommand;
import edu.cmu.ri.mrpl.TeRK.AudioCommandException;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrx;
import edu.cmu.ri.mrpl.TeRK.AudioMode;
import org.apache.log4j.Logger;

/**
 */
public final class AudioServiceIceImpl extends BaseAudioServiceImpl
   {
   private static final Logger LOG = Logger.getLogger(AudioServiceIceImpl.class);

   private static final MyAMI_AudioController_execute NO_OP_CALLBACK = new MyAMI_AudioController_execute(null);

   public static AudioServiceIceImpl create(final AudioControllerPrx proxy)
      {
      return new AudioServiceIceImpl(proxy, new ServicePropertyManager(proxy));
      }

   private final AudioControllerPrx proxy;

   private AudioServiceIceImpl(final AudioControllerPrx proxy, final PropertyManager propertyManager)
      {
      super(propertyManager);
      this.proxy = proxy;
      }

   public void playTone(final int frequency, final int amplitude, final int duration)
      {
      execute(new AudioCommand(AudioMode.AudioTone,
                               frequency,
                               (byte)amplitude,
                               duration,
                               null));
      }

   public void playSound(final byte[] sound)
      {
      execute(new AudioCommand(AudioMode.AudioClip,
                               0,
                               (byte)0,
                               Math.abs(0),
                               sound));
      }

   public void playToneAsynchronously(final int frequency, final int amplitude, final int duration, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      final AudioCommand command = new AudioCommand(AudioMode.AudioTone,
                                                    frequency,
                                                    (byte)amplitude,
                                                    duration,
                                                    null);
      executeAsynchronously(command, callback);
      }

   public void playSoundAsynchronously(final byte[] sound, final AsynchronousCommandExceptionHandlerCallback callback)
      {
      final AudioCommand command = new AudioCommand(AudioMode.AudioClip,
                                                    0,
                                                    (byte)0,
                                                    Math.abs(0),
                                                    sound);
      executeAsynchronously(command, callback);
      }

   private void execute(final AudioCommand command)
      {
      try
         {
         proxy.execute(command);
         }
      catch (AudioCommandException e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting a command.", e);
         }
      }

   private void executeAsynchronously(final AudioCommand command, final AsynchronousCommandExceptionHandlerCallback asynchronousCommandExceptionHandlerCallback)
      {
      // don't create a new MyAMI_AudioController_execute object if the caller sends in a null AsynchronousCommandExceptionHandlerCallback
      final MyAMI_AudioController_execute callback;
      if (asynchronousCommandExceptionHandlerCallback == null)
         {
         callback = NO_OP_CALLBACK;
         }
      else
         {
         callback = new MyAMI_AudioController_execute(asynchronousCommandExceptionHandlerCallback);
         }
      proxy.execute_async(callback, command);
      }

   private static class MyAMI_AudioController_execute extends AMI_AudioController_execute
      {
      private final AsynchronousCommandExceptionHandlerCallback callback;

      private MyAMI_AudioController_execute(final AsynchronousCommandExceptionHandlerCallback callback)
         {
         this.callback = callback;
         }

      public void ice_response()
         {
         // do nothing
         }

      public void ice_exception(final LocalException ex)
         {
         if (callback != null)
            {
            callback.handleException(ex);
            }
         }

      public void ice_exception(final UserException ex)
         {
         if (callback != null)
            {
            callback.handleException(ex);
            }
         }
      }
   }
