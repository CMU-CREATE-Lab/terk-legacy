package edu.cmu.ri.mrpl.TeRK.client.components.services.peerinfo;

import java.util.List;
import edu.cmu.ri.createlab.TeRK.properties.ServicePropertyManager;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoServerServicePrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PeerInfoServerServiceImpl extends ServicePropertyManager implements PeerInfoServerService
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoServerServiceImpl.class);

   private final PeerInfoServerServicePrx proxy;

   public PeerInfoServerServiceImpl(final PeerInfoServerServicePrx proxy)
      {
      super(proxy);
      this.proxy = proxy;
      }

   public String getTypeId()
      {
      return TYPE_ID;
      }

   public void setAttribute(final String key, final String value)
      {
      try
         {
         proxy.setAttribute(key, value);
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting setAttribute()", e);
         }
      }

   public List<PeerInfo> getPeerInfo()
      {
      try
         {
         return proxy.getPeerInfo();
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting getPeerInfo()", e);
         }
      return null;
      }

   public List<PeerInfo> getConnectedPeerInfo()
      {
      try
         {
         return proxy.getConnectedPeerInfo();
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting getConnectedPeerInfo()", e);
         }
      return null;
      }

   public List<PeerInfo> getDisconnectedPeerInfo()
      {
      try
         {
         return proxy.getDisconnectedPeerInfo();
         }
      catch (Exception e)
         {
         // todo: allow this to propogate to the caller
         LOG.error("Exception while excecuting getDisconnectedPeerInfo()", e);
         }
      return null;
      }
   }
