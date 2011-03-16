package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger._RoboticonMessagingServerServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessagingServerServiceServant extends _RoboticonMessagingServerServiceDisp
   {
   private static final String CONTEXT_MAP_KEY_PEER_USERID = "__peerUserId";

   private final Map<String, String> properties = new HashMap<String, String>();
   private final RoboticonMessagingServer roboticonMessagingServer;

   public RoboticonMessagingServerServiceServant(final RoboticonMessagingServer chatServer)
      {
      this.roboticonMessagingServer = chatServer;
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

   public void sendPublicMessage(final String parentMessageId, final Message message, final List<Roboticon> roboticons, final Current current)
      {
      roboticonMessagingServer.sendPublicMessage(parentMessageId, getUserId(current), message, roboticons);
      }

   public void sendPrivateMessage(final String parentMessageId, final String recipientUserId, final Message message, final List<Roboticon> roboticons, final Current current)
      {
      roboticonMessagingServer.sendPrivateMessage(parentMessageId, getUserId(current), recipientUserId, message, roboticons);
      }

   public ArrayList<RoboticonMessage> getMessageHistory(final Current current)
      {
      return new ArrayList<RoboticonMessage>(roboticonMessagingServer.getMessageHistory(getUserId(current)));
      }

   public long getLastLogoutTimestamp(final Current current)
      {
      return roboticonMessagingServer.getLastLogoutTimestamp(getUserId(current));
      }

   private String getUserId(final Current current)
      {
      return current.ctx != null ? current.ctx.get(CONTEXT_MAP_KEY_PEER_USERID) : null;
      }
   }
