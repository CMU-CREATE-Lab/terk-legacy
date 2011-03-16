package edu.cmu.ri.mrpl.TeRK.client.roboticonmessenger.messaging;

import java.util.List;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingServerServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class RoboticonMessagingServerServiceImpl extends ServicePropertyManager implements RoboticonMessagingServerService
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessagingServerServiceImpl.class);

   private final RoboticonMessagingServerServicePrx proxy;

   public RoboticonMessagingServerServiceImpl(final RoboticonMessagingServerServicePrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void sendPublicMessage(final String parentMessageId, final ClientRoboticonMessage clientRoboticonMessage)
      {
      try
         {
         proxy.sendPublicMessage(parentMessageId,
                                 clientRoboticonMessage.getMessage(),
                                 clientRoboticonMessage.getRoboticons());
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting sendPublicMessage()", e);
         }
      }

   public void sendPrivateMessage(final String parentMessageId, final String recipientUserId, final ClientRoboticonMessage clientRoboticonMessage)
      {
      try
         {
         proxy.sendPrivateMessage(parentMessageId,
                                  recipientUserId,
                                  clientRoboticonMessage.getMessage(),
                                  clientRoboticonMessage.getRoboticons());
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting sendPrivateMessage()", e);
         }
      }

   public List<RoboticonMessage> getMessageHistory()
      {
      try
         {
         return proxy.getMessageHistory();
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting getMessageHistory()", e);
         }
      return null;
      }
   }
