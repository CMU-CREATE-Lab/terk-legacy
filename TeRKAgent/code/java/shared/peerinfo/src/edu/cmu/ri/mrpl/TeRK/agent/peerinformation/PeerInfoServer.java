package edu.cmu.ri.mrpl.TeRK.agent.peerinformation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerInfoServer
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoServer.class);

   private final Map<String, PeerInfoClientService> clients = Collections.synchronizedMap(new HashMap<String, PeerInfoClientService>());
   private final PeerInfoModel peerInfoModel;

   public PeerInfoServer(final PeerInfoModel peerInfoModel)
      {
      this.peerInfoModel = peerInfoModel;
      }

   List<PeerInfo> getPeerInfo()
      {
      return peerInfoModel.getPeerInfo();
      }

   List<PeerInfo> getConnectedPeerInfo()
      {
      return peerInfoModel.getConnectedPeerInfo();
      }

   List<PeerInfo> getDisconnectedPeerInfo()
      {
      return peerInfoModel.getDisconnectedPeerInfo();
      }

   public void setPeerAttribute(final String userId, final String key, final String value)
      {
      if (userId != null)
         {
         final PeerInfo peerInfo = peerInfoModel.setPeerAttribute(userId, key, value);
         if (peerInfo != null)
            {
            // now broadcast the attribute change to all connected users
            synchronized (clients)
               {
               if (!clients.isEmpty())
                  {
                  for (final String clientUserId : clients.keySet())
                     {
                     final PeerInfoClientService service = clients.get(clientUserId);

                     try
                        {
                        service.peerUpdated(peerInfo);
                        }
                     catch (Exception e)
                        {
                        LOG.error("Exception while sending attribute change to user [" + clientUserId + "]", e);
                        }
                     }
                  }
               }
            }
         }
      }

   public void markClientAsConnected(final String userId, final PeerInfoClientService peerInfoClientService)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("PeerInfoServer.markClientAsConnected(" + userId + ")");
         }

      if ((userId != null) && (peerInfoClientService != null))
         {
         final PeerInfo peerInfo = peerInfoModel.markPeerAsConnected(userId);

         if (peerInfo != null)
            {
            // now broadcast the new client to all connected users
            synchronized (clients)
               {
               clients.put(userId, peerInfoClientService);
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("There are now [" + clients.size() + "] peer info clients");
                  }

               for (final String clientUserId : clients.keySet())
                  {
                  final PeerInfoClientService service = clients.get(clientUserId);

                  try
                     {
                     service.peerConnected(peerInfo);
                     }
                  catch (Exception e)
                     {
                     LOG.error("Exception while sending peer connected notification to user [" + clientUserId + "]", e);
                     }
                  }
               }
            }
         }
      }

   public void markClientAsDisconnected(final String userId)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("PeerInfoServer.markClientAsDisconnected(" + userId + ")");
         }

      if (userId != null)
         {
         final PeerInfo peerInfo = peerInfoModel.markPeerAsDisconnected(userId);

         if (peerInfo != null)
            {
            // now broadcast the new client to all connected users
            synchronized (clients)
               {
               clients.remove(userId);
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("There are now [" + clients.size() + "] peer info clients");
                  }

               if (!clients.isEmpty())
                  {
                  for (final String clientUserId : clients.keySet())
                     {
                     final PeerInfoClientService service = clients.get(clientUserId);

                     try
                        {
                        service.peerDisconnected(peerInfo);
                        }
                     catch (Exception e)
                        {
                        LOG.error("Exception while sending peer connected notification to user [" + clientUserId + "]", e);
                        }
                     }
                  }
               }
            }
         }
      }
   }
