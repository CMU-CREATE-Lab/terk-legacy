package edu.cmu.ri.mrpl.TeRK.client.roboticonmessenger.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger._RoboticonMessagingClientServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessagingClientServiceServant extends _RoboticonMessagingClientServiceDisp
   {
   private final RoboticonMessengerModel roboticonMessengerModel;
   private final Map<String, String> properties = new HashMap<String, String>();

   public RoboticonMessagingClientServiceServant(final RoboticonMessengerModel roboticonMessengerModel)
      {
      this.roboticonMessengerModel = roboticonMessengerModel;
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

   public void handleRoboticonMessage(final RoboticonMessage message, final Current current)
      {
      roboticonMessengerModel.addMessage(message);
      }
   }
