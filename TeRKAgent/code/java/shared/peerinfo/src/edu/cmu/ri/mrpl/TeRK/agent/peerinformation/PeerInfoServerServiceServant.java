package edu.cmu.ri.mrpl.TeRK.agent.peerinformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation._PeerInfoServerServiceDisp;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerInfoServerServiceServant extends _PeerInfoServerServiceDisp
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoServerServiceServant.class);
   private static final String CONTEXT_MAP_KEY_PEER_USERID = "__peerUserId";

   private final Map<String, String> properties = new HashMap<String, String>();
   private final PeerInfoServer peerInfoServer;

   public PeerInfoServerServiceServant(final PeerInfoServer peerInfoServer)
      {
      this.peerInfoServer = peerInfoServer;
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

   public List<PeerInfo> getPeerInfo(final Current __current)
      {
      return new ArrayList<PeerInfo>(peerInfoServer.getPeerInfo());
      }

   public ArrayList<PeerInfo> getConnectedPeerInfo(final Current current)
      {
      return new ArrayList<PeerInfo>(peerInfoServer.getConnectedPeerInfo());
      }

   public List<PeerInfo> getDisconnectedPeerInfo(final Current __current)
      {
      return new ArrayList<PeerInfo>(peerInfoServer.getDisconnectedPeerInfo());
      }

   public void setAttribute(final String key, final String value, final Current current)
      {
      final String userId = current.ctx != null ? (String)current.ctx.get(CONTEXT_MAP_KEY_PEER_USERID) : null;
      peerInfoServer.setPeerAttribute(userId, key, value);
      }
   }
