package edu.cmu.ri.mrpl.TeRK.agent.peerinformation;

import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PeerInfoClientServiceImpl extends ServicePropertyManager implements PeerInfoClientService
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoClientServiceImpl.class);

   private final PeerInfoClientServicePrx proxy;

   public PeerInfoClientServiceImpl(final PeerInfoClientServicePrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void peerConnected(final PeerInfo peer)
      {
      try
         {
         proxy.peerConnected(peer);
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting peerConnected()", e);
         }
      }

   public void peerUpdated(final PeerInfo peer)
      {
      try
         {
         proxy.peerUpdated(peer);
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting peerUpdated()", e);
         }
      }

   public void peerDisconnected(final PeerInfo peer)
      {
      try
         {
         proxy.peerDisconnected(peer);
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting peerDisconnected()", e);
         }
      }
   }
