package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.Bounds;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.ServoCommand;
import edu.cmu.ri.mrpl.TeRK.ServoConfig;
import edu.cmu.ri.mrpl.TeRK.ServoState;
import edu.cmu.ri.mrpl.TeRK._ServoControllerDisp;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ServoControllerServant extends _ServoControllerDisp implements MessageEventSource
   {
   private static final Logger LOG = Logger.getLogger(ServoControllerServant.class);
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final int DEVICE_COUNT = 16;
   private static final int MIN_BOUND = 0;
   private static final int MAX_BOUND = 255;
   private static final int DEFAULT_INITIAL_POSITION = 127;

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final ServantPropertyManager properties = new ServantPropertyManager();
   private final int[] positions = new int[DEVICE_COUNT];
   private final ServoConfig[] servoConfigs = new ServoConfig[DEVICE_COUNT];

   ServoControllerServant()
      {
      for (int i = 0; i < DEVICE_COUNT; i++)
         {
         positions[i] = 0x80;
         servoConfigs[i] = new ServoConfig(new Bounds(MIN_BOUND, MAX_BOUND), DEFAULT_INITIAL_POSITION);
         }
      properties.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, DEVICE_COUNT);
      }

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
      return properties.getProperty(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return properties.getProperties();
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return properties.getPropertyKeys();
      }

   public void setProperty(final String key, final String value, final Current current) throws ReadOnlyPropertyException
      {
      properties.setProperty(key, value);
      }

   public ServoState execute(final ServoCommand command, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("ServoControllerServant.execute()" + IceUtil.dumpCurrentToString(current));
         }
      final StringBuffer message = new StringBuffer("ServoControllerServant.execute():").append(LINE_SEPARATOR);
      message.append("   ").append("Mask:      ").append(ArrayUtils.arrayToString(command.servoMask)).append(LINE_SEPARATOR);
      message.append("   ").append("Modes:     ").append(ArrayUtils.arrayToString(command.servoModes)).append(LINE_SEPARATOR);
      message.append("   ").append("Positions: ").append(ArrayUtils.arrayToString(command.servoPositions)).append(LINE_SEPARATOR);
      message.append("   ").append("Speeds:    ").append(ArrayUtils.arrayToString(command.servoSpeeds)).append(LINE_SEPARATOR);
      LOG.debug(message);
      fireMessageEvent(message.toString());
      fireQwerkEvent(command);
      for (int i = 0; i < positions.length; i++)
         {
         try
            {
            positions[i] = command.servoPositions[i];
            }
         catch (ArrayIndexOutOfBoundsException e)
            {
            LOG.warn("ArrayIndexOutOfBoundsException in execute()", e);
            break;
            }
         }
      return new ServoState(positions);
      }

   public void setBounds(final boolean[] servoMask, final Bounds[] servoBounds, final Current current)
      {
      if ((servoMask != null) && (servoMask.length > 0) &&
          (servoBounds != null) && (servoBounds.length > 0))
         {
         for (int i = 0; i < Math.min(servoMask.length, servoBounds.length); i++)
            {
            if (servoMask[i])
               {
               servoConfigs[i].rangeBounds.min = servoBounds[i].min;
               servoConfigs[i].rangeBounds.max = servoBounds[i].max;
               }
            }
         }
      }

   public Bounds[] getBounds(final Current current)
      {
      final Bounds[] bounds = new Bounds[servoConfigs.length];
      for (int i = 0; i < servoConfigs.length; i++)
         {
         bounds[i] = new Bounds(servoConfigs[i].rangeBounds.min,
                                servoConfigs[i].rangeBounds.max);
         }

      return bounds;
      }

   public void setConfigs(final boolean[] servoMask, final ServoConfig[] newServoConfigs, final Current current)
      {
      if ((servoMask != null) && (servoMask.length > 0) &&
          (newServoConfigs != null) && (newServoConfigs.length > 0))
         {
         for (int i = 0; i < Math.min(servoMask.length, newServoConfigs.length); i++)
            {
            if (servoMask[i])
               {
               servoConfigs[i].initialPosition = newServoConfigs[i].initialPosition;
               servoConfigs[i].rangeBounds.min = newServoConfigs[i].rangeBounds.min;
               servoConfigs[i].rangeBounds.max = newServoConfigs[i].rangeBounds.max;
               }
            }
         }
      }

   public ServoConfig[] getConfigs(final Current current)
      {
      final ServoConfig[] copy = new ServoConfig[servoConfigs.length];

      for (int i = 0; i < servoConfigs.length; i++)
         {
         copy[i] = new ServoConfig(new Bounds(servoConfigs[i].rangeBounds.min,
                                              servoConfigs[i].rangeBounds.max),
                                   servoConfigs[i].initialPosition);
         }

      return copy;
      }
   }
