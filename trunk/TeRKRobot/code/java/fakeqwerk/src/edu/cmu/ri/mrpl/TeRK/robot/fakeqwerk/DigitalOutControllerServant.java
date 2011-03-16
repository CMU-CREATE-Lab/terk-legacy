package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.DigitalOutCommand;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK._DigitalOutControllerDisp;
import edu.cmu.ri.mrpl.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DigitalOutControllerServant extends _DigitalOutControllerDisp implements MessageEventSource, QwerkEventSource
   {
   private static final Logger LOG = Logger.getLogger(DigitalOutControllerServant.class);
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final int DEVICE_COUNT = 8;

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final ServantPropertyManager properties = new ServantPropertyManager();
   private final QwerkEventSourceHelper qwerkEventSourceHelper = new QwerkEventSourceHelper();

   DigitalOutControllerServant()
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

   public void execute(final DigitalOutCommand command, final Current current)
      {
      final StringBuffer message = new StringBuffer("DigitalOutControllerServant.execute():").append(LINE_SEPARATOR);
      message.append("   ").append("Mask:   ").append(ArrayUtils.arrayToString(command.digitalOutMask)).append(LINE_SEPARATOR);
      message.append("   ").append("Values: ").append(ArrayUtils.arrayToString(command.digitalOutValues)).append(LINE_SEPARATOR);
      LOG.debug(message);
      fireMessageEvent(message.toString());
      fireQwerkEvent(command);
      }
   }
