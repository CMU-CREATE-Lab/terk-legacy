package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.DigitalInState;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK._DigitalInControllerDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DigitalInControllerServant extends _DigitalInControllerDisp implements MessageEventSource, QwerkEventSource
   {
   private static final int DEVICE_COUNT = 8;

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final ServantPropertyManager properties = new ServantPropertyManager();
   private int digitalInAlternate = 0;

   DigitalInControllerServant()
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

   private final QwerkEventSourceHelper qwerkEventSourceHelper = new QwerkEventSourceHelper();

   public void addQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.addQwerkEventListener(qwerkEventListener);
      }

   public void removeQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.removeQwerkEventListener(qwerkEventListener);
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

   public DigitalInState getState(final Current current)
      {
      final boolean[][] alternating = {{true, false, true, false, true, false, true, false},
                                       {false, true, false, true, false, true, false, true}};
      final boolean[] next = alternating[digitalInAlternate];
      digitalInAlternate++;
      digitalInAlternate %= 2;
      return new DigitalInState(next);
      }
   }
