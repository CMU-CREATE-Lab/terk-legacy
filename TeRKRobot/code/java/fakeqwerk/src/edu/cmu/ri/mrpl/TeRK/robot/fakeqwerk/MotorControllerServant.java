package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.List;
import java.util.Map;
import java.util.Random;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.MotorBuffer;
import edu.cmu.ri.mrpl.TeRK.MotorCommand;
import edu.cmu.ri.mrpl.TeRK.MotorState;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK._MotorControllerDisp;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class MotorControllerServant extends _MotorControllerDisp implements MessageEventSource, QwerkEventSource
   {
   private static final Logger LOG = Logger.getLogger(MotorControllerServant.class);
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final int DEVICE_COUNT = 4;

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final ServantPropertyManager properties = new ServantPropertyManager();

   MotorControllerServant()
      {
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

   public MotorState execute(final MotorCommand command, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("MotorControllerServant.execute()" + IceUtil.dumpCurrentToString(current));
         }
      final StringBuffer message = new StringBuffer("MotorControllerServant.execute():").append(LINE_SEPARATOR);
      message.append("   ").append("Mask:          ").append(ArrayUtils.arrayToString(command.motorMask)).append(LINE_SEPARATOR);
      message.append("   ").append("Modes:         ").append(ArrayUtils.arrayToString(command.motorModes)).append(LINE_SEPARATOR);
      message.append("   ").append("Positions:     ").append(ArrayUtils.arrayToString(command.motorPositions)).append(LINE_SEPARATOR);
      message.append("   ").append("Velocities:    ").append(ArrayUtils.arrayToString(command.motorVelocities)).append(LINE_SEPARATOR);
      message.append("   ").append("Accelerations: ").append(ArrayUtils.arrayToString(command.motorAccelerations)).append(LINE_SEPARATOR);
      LOG.debug(message);
      fireMessageEvent(message.toString());
      fireQwerkEvent(command);

      // todo: return the real state
      final Random r = new Random();
      final int[] velocities = new int[DEVICE_COUNT];
      final int[] positions = new int[DEVICE_COUNT];
      final int[] currents = new int[DEVICE_COUNT];
      final int[] dutycycles = new int[DEVICE_COUNT];
      final boolean[] done = new boolean[DEVICE_COUNT];
      for (int i = 0; i < DEVICE_COUNT; i++)
         {
         velocities[i] = r.nextInt();
         positions[i] = r.nextInt();
         currents[i] = r.nextInt();
         dutycycles[i] = r.nextInt();
         done[i] = r.nextBoolean();
         }
      return new MotorState(velocities, positions, currents, dutycycles, done);
      }

   public int getFrequency(final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public void startMotorBufferRecord(final boolean[] motorMask, final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public void stopMotorBufferRecord(final boolean[] motorMask, final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public MotorBuffer[] getMotorBuffers(final boolean[] motorMask, final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public void setMotorBuffer(final boolean[] motorMask, final MotorBuffer[] motorBuffers, final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public void playMotorBuffer(final boolean[] motorMask, final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }
   }
