package edu.cmu.ri.mrpl.TeRK.client.components.servants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.peerinformation._PeerInfoClientServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerInfoClientServiceServant extends _PeerInfoClientServiceDisp
   {
   private final PeerInfoModel peerInfoModel;
   private final Map<String, String> properties = new HashMap<String, String>();

   public PeerInfoClientServiceServant(final PeerInfoModel peerInfoModel)
      {
      this.peerInfoModel = peerInfoModel;
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

   public void peerConnected(final PeerInfo peerInfo, final Current current)
      {
      peerInfoModel.markPeerAsConnected(peerInfo);
      }

   public void peerUpdated(final PeerInfo peerInfo, final Current current)
      {
      peerInfoModel.updatePeer(peerInfo);
      }

   public void peerDisconnected(final PeerInfo peerInfo, final Current current)
      {
      peerInfoModel.markPeerAsDisconnected(peerInfo.userId);
      }
   }
