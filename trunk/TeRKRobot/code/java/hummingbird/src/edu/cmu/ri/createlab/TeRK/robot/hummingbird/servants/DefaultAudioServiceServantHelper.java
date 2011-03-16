package edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.audio.AudioServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.proxy.HummingbirdProxy;
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

   private final HummingbirdProxy hummingbirdProxy;

   DefaultAudioServiceServantHelper(final HummingbirdProxy hummingbirdProxy)
      {
      this.hummingbirdProxy = hummingbirdProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, HummingbirdConstants.AUDIO_DEVICE_COUNT);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_AMPLITUDE, HummingbirdConstants.AUDIO_DEVICE_MIN_AMPLITUDE);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_AMPLITUDE, HummingbirdConstants.AUDIO_DEVICE_MAX_AMPLITUDE);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_DURATION, HummingbirdConstants.AUDIO_DEVICE_MIN_DURATION);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_DURATION, HummingbirdConstants.AUDIO_DEVICE_MAX_DURATION);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MIN_FREQUENCY, HummingbirdConstants.AUDIO_DEVICE_MIN_FREQUENCY);
      this.setReadOnlyProperty(AudioService.PROPERTY_NAME_MAX_FREQUENCY, HummingbirdConstants.AUDIO_DEVICE_MAX_FREQUENCY);
      }

   public void execute(final AudioCommand command) throws AudioCommandException
      {
      if (command != null)
         {
         if (AudioMode.AudioTone.equals(command.mode))
            {
            hummingbirdProxy.playTone(command.frequency,
                                      command.amplitude,
                                      command.duration);
            }
         else if (AudioMode.AudioClip.equals(command.mode))
            {
            hummingbirdProxy.playClip(command.sound);
            }
         else
            {
            LOG.error("DefaultAudioServiceServantHelper.execute() ignoring unknown AudioMode [" + command.mode + "]");
            }
         }
      }
   }