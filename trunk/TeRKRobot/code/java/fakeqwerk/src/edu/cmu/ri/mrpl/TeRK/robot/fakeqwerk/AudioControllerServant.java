package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.audio.AudioHelper;
import edu.cmu.ri.mrpl.TeRK.AudioCommand;
import edu.cmu.ri.mrpl.TeRK.AudioMode;
import edu.cmu.ri.mrpl.TeRK._AudioControllerDisp;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class AudioControllerServant extends _AudioControllerDisp implements MessageEventSource, QwerkEventSource
   {
   private static final Logger LOG = Logger.getLogger(AudioControllerServant.class);

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final Map<String, String> properties = new HashMap<String, String>();

   public void addMessageEventListener(final MessageEventListener messageEventListener)
      {
      messageEventSourceHelper.addMessageEventListener(messageEventListener);
      }

   public void removeMessageEventListener(final MessageEventListener messageEventListener)
      {
      messageEventSourceHelper.removeMessageEventListener(messageEventListener);
      }

   private void fireMessageEvent(final String message)
      {
      messageEventSourceHelper.fireMessageEvent(message);
      }

   private final QwerkEventSourceHelper qwerkEventSourceHelper = new QwerkEventSourceHelper();

   public void addQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.addQwerkEventListener(qwerkEventListener);
      }

   public void removeQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.removeQwerkEventListener(qwerkEventListener);
      }

   private void fireQwerkEvent(final Object command)
      {
      qwerkEventSourceHelper.fireQwerkEvent(command);
      }

   public String getProperty(final String key, final Current current)
      {
      return properties.get(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return Collections.unmodifiableMap(properties);
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return new ArrayList<String>(properties.keySet());
      }

   public void setProperty(final String key, final String value, final Current current)
      {
      properties.put(key, value);
      }

   public void execute(final AudioCommand command, final Current current)
      {
      if (command != null)
         {
         final String message;
         if (AudioMode.AudioTone.equals(command.mode))
            {
            //frequency playing code in the spirit of http://www.cs.princeton.edu/introcs/stdlib/ book's examples on audio generation
            message = "AudioControllerServant.execute() playing tone with frequency [" + command.frequency + "], " +
                      "amplitude [" + command.amplitude + "], and duration [" + command.duration + "].";
            LOG.debug(message);

            AudioHelper.playTone(command.frequency,
                                 command.amplitude,
                                 command.duration);
            }
         else if (AudioMode.AudioClip.equals(command.mode))
            {
            message = "AudioControllerServant.execute() playing " + (command.sound == null ? 0 : command.sound.length) + "-byte sound clip";
            LOG.debug(message);

            AudioHelper.playClip(command.sound);
            }
         else
            {
            message = "AudioControllerServant.execute() ignoring unknown AudioMode [" + command.mode + "]";
            LOG.error(message);
            }
         fireMessageEvent(message);
         fireQwerkEvent(command);
         }
      }
   }
