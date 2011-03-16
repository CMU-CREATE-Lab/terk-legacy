package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.AudioCommand;
import edu.cmu.ri.mrpl.TeRK.AudioCommandException;
import edu.cmu.ri.mrpl.TeRK.AudioMode;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultAudioServiceServantHelper extends AbstractServiceServant implements AudioServiceServantHelper
   {
   private static final Logger LOG = Logger.getLogger(DefaultAudioServiceServantHelper.class);

   private final FinchProxy finchProxy;

   DefaultAudioServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.AUDIO_DEVICE_COUNT);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, FinchConstants.AUDIO_DEVICE_MIN_DURATION);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, FinchConstants.AUDIO_DEVICE_MAX_DURATION);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, FinchConstants.AUDIO_DEVICE_MIN_FREQUENCY);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, FinchConstants.AUDIO_DEVICE_MAX_FREQUENCY);
      }

   public void execute(final AudioCommand command) throws AudioCommandException
      {
      if (command != null)
         {
         if (AudioMode.AudioTone.equals(command.mode))
            {
            finchProxy.playTone(command.frequency,
                                command.amplitude,
                                command.duration);
            }
         else if (AudioMode.AudioClip.equals(command.mode))
            {
            finchProxy.playClip(command.sound);
            }
         else
            {
            LOG.error("DefaultAudioServiceServantHelper.execute() ignoring unknown AudioMode [" + command.mode + "]");
            }
         }
      }
   }