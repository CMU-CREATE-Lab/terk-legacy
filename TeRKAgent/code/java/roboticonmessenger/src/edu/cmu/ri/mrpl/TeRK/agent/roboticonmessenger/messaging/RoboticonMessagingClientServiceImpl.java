package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging;

import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class RoboticonMessagingClientServiceImpl extends ServicePropertyManager implements RoboticonMessagingClientService
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessagingClientServiceImpl.class);

   private final RoboticonMessagingClientServicePrx proxy;

   public RoboticonMessagingClientServiceImpl(final RoboticonMessagingClientServicePrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void handleRoboticonMessage(final RoboticonMessage message)
      {
      try
         {
         proxy.handleRoboticonMessage(message);
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting handleRoboticonMessage()", e);
         }
      }
   }
