package edu.cmu.ri.mrpl.TeRK.peerinformation;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerInfoModel extends AbstractListModel
   {
   private static final Logger LOG = Logger.getLogger(PeerInfoModel.class);

   private final SortedMap<String, PeerInfo> peers = Collections.synchronizedSortedMap(new TreeMap<String, PeerInfo>());
   private final Map<String, ImageIcon> peerIcons = Collections.synchronizedMap(new HashMap<String, ImageIcon>());
   private final Set<PeerInfoListener> peerInfoListeners = Collections.synchronizedSet(new HashSet<PeerInfoListener>());
   private final PeerImageFactory peerImageFactory;
   private static final int DEFAULT_IMAGE_ICON_SIZE = 32;
   private static final Color DEFAULT_USER_COLOR = Color.GRAY;

   public PeerInfoModel(final PeerImageFactory peerImageFactory)
      {
      this.peerImageFactory = peerImageFactory;
      }

   public void addPeerInfoListener(final PeerInfoListener peerInfoListener)
      {
      if (peerInfoListener != null)
         {
         peerInfoListeners.add(peerInfoListener);
         }
      }

   public void removePeerInfoListener(final PeerInfoListener peerInfoListener)
      {
      if (peerInfoListener != null)
         {
         peerInfoListeners.remove(peerInfoListener);
         }
      }

   public List<PeerInfo> getPeerInfo()
      {
      final List<PeerInfo> allPeerInfo = new ArrayList<PeerInfo>();
      synchronized (peers)
         {
         if (!peers.isEmpty())
            {
            allPeerInfo.addAll(peers.values());
            }
         }
      return Collections.unmodifiableList(allPeerInfo);
      }

   public List<PeerInfo> getConnectedPeerInfo()
      {
      return getPeerInfoByConnectionStatus(true);
      }

   public List<PeerInfo> getDisconnectedPeerInfo()
      {
      return getPeerInfoByConnectionStatus(false);
      }

   // todo: why is this a separate method?  All it does is delegate to getPeerInfo().
   public List<PeerInfo> getAllPeerInfo()
      {
      return getPeerInfo();
      }

   private List<PeerInfo> getPeerInfoByConnectionStatus(final boolean isConnected)
      {
      final List<PeerInfo> peerInfo = new ArrayList<PeerInfo>();
      synchronized (peers)
         {
         if (!peers.isEmpty())
            {
            for (final PeerInfo peer : peers.values())
               {
               if ((peer != null) && (peer.isConnected == isConnected))
                  {
                  peerInfo.add(peer);
                  }
               }
            }
         }
      return Collections.unmodifiableList(peerInfo);
      }

   @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
   public void setPeerInfo(final List<PeerInfo> allPeerInfo)
      {
      synchronized (peers)
         {
         peers.clear();
         if ((allPeerInfo != null) && (!allPeerInfo.isEmpty()))
            {
            for (final PeerInfo peerInfo : allPeerInfo)
               {
               if (peerInfo != null)
                  {
                  addPeerAndLoadIcon(peerInfo.userId, peerInfo);
                  }
               }
            }
         }

      fireContentsChanged(this, 0, peers.size());

      synchronized (peerInfoListeners)
         {
         // notify listeners
         if (!peerInfoListeners.isEmpty())
            {
            for (final PeerInfoListener listener : peerInfoListeners)
               {
               listener.peersChanged();
               }
            }
         }
      }

   public PeerInfo markPeerAsConnected(final PeerInfo peerInfo)
      {
      if (peerInfo != null)
         {
         addPeerAndLoadIcon(peerInfo.userId, peerInfo);

         // todo: improve efficiency by calling fireIntervalAdded()
         fireContentsChanged(this, 0, peers.size());

         synchronized (peerInfoListeners)
            {
            // notify listeners
            if (!peerInfoListeners.isEmpty())
               {
               for (final PeerInfoListener listener : peerInfoListeners)
                  {
                  // send a copy of the peerInfo just in case listeners modify it
                  listener.peerConnected((PeerInfo)peerInfo.clone());
                  }
               }
            }

         return peerInfo;
         }
      return null;
      }

   private void addPeerAndLoadIcon(final String userId, final PeerInfo peerInfo)
      {
      peers.put(userId, peerInfo);
      final Image image = peerImageFactory.createImage(userId);
      if (image != null)
         {
         peerIcons.put(userId, new ImageIcon(image.getScaledInstance(DEFAULT_IMAGE_ICON_SIZE,
                                                                     DEFAULT_IMAGE_ICON_SIZE,
                                                                     Image.SCALE_DEFAULT)));
         }
      }

   public ImageIcon getPeerIcon(final String userId)
      {
      return peerIcons.get(userId);
      }

   public PeerInfo markPeerAsConnected(final String userId)
      {
      if (userId != null)
         {
         final long currentTime = System.currentTimeMillis();
         PeerInfo peerInfo = peers.get(userId);
         if (peerInfo == null)
            {
            peerInfo = new PeerInfo(true,
                                    currentTime,
                                    userId,
                                    new HashMap<String, String>());
            }
         else
            {
            peerInfo.isConnected = true;
            peerInfo.connectionTimestamp = currentTime;
            }

         return markPeerAsConnected(peerInfo);
         }
      return null;
      }

   public void updatePeer(final PeerInfo newPeerInfo)
      {
      if (newPeerInfo != null)
         {
         final String userId = newPeerInfo.userId;
         final PeerInfo oldPeerInfo = peers.get(userId);
         if (oldPeerInfo != null)
            {
            peers.put(userId, newPeerInfo);

            // todo: improve efficiency by setting the interval properly
            fireContentsChanged(this, 0, peers.size());

            synchronized (peerInfoListeners)
               {
               // notify listeners
               if (!peerInfoListeners.isEmpty())
                  {
                  for (final PeerInfoListener listener : peerInfoListeners)
                     {
                     // send a copy of the peerInfo just in case listeners modify it
                     listener.peerUpdated((PeerInfo)newPeerInfo.clone());
                     }
                  }
               }
            }
         }
      }

   public PeerInfo markPeerAsDisconnected(final String userId)
      {
      final PeerInfo peerInfo = peers.get(userId);

      if (peerInfo != null)
         {
         peerInfo.isConnected = false;

         // todo: improve efficiency by calling fireIntervalRemoved()
         fireContentsChanged(this, 0, peers.size());

         synchronized (peerInfoListeners)
            {
            // notify listeners
            if (!peerInfoListeners.isEmpty())
               {
               for (final PeerInfoListener listener : peerInfoListeners)
                  {
                  // send a copy of the peerInfo just in case listeners modify it
                  listener.peerDisconnected((PeerInfo)peerInfo.clone());
                  }
               }
            }
         }

      return peerInfo;
      }

   public PeerInfo setPeerAttribute(final String userId, final String key, final String value)
      {
      if (userId != null && key != null)
         {
         final PeerInfo peerInfo = peers.get(userId);
         if (peerInfo != null)
            {
            peerInfo.attributes.put(key, value);

            // todo: improve efficiency by setting the interval properly
            fireContentsChanged(this, 0, peers.size());

            synchronized (peerInfoListeners)
               {
               // notify listeners
               if (!peerInfoListeners.isEmpty())
                  {
                  for (final PeerInfoListener listener : peerInfoListeners)
                     {
                     // send a copy of the peerInfo just in case listeners modify it
                     listener.peerUpdated((PeerInfo)peerInfo.clone());
                     }
                  }
               }

            return peerInfo;
            }
         }
      return null;
      }

   public int getSize()
      {
      return peers.size();
      }

   public Object getElementAt(final int index)
      {
      final List<String> userIdList = new ArrayList<String>(peers.keySet());
      final String userId = userIdList.get(index);
      return peers.get(userId);
      }

   public ImageIcon getUserIcon(final String userId, int scale)
      {
      ImageIcon icon = getPeerIcon(userId);
      Image image;
      if (icon == null)
         {
         image = peerImageFactory.createImage(userId);
         }
      else
         {
         image = icon.getImage();
         }
      if (image != null)
         {
         icon = new ImageIcon(image.getScaledInstance(DEFAULT_IMAGE_ICON_SIZE * scale,
                                                      DEFAULT_IMAGE_ICON_SIZE * scale,
                                                      Image.SCALE_DEFAULT));
         }
      return icon;
      }

   public ImageIcon getUserIcon(final String userId, int x, int y)
      {
      ImageIcon icon = getPeerIcon(userId);
      Image image;
      if (icon == null)
         {
         image = peerImageFactory.createImage(userId);
         }
      else
         {
         image = icon.getImage();
         }
      if (image != null)
         {
         icon = new ImageIcon(image.getScaledInstance(x,
                                                      y,
                                                      Image.SCALE_DEFAULT));
         }
      return icon;
      }

   public Color getUserColor(final String userId)
      {
      Color color = DEFAULT_USER_COLOR;
      String hexcolor = "";
      PeerInfo peerInfo = null;

      if (userId != null)
         {
         peerInfo = peers.get(userId);
         }
      if (peerInfo != null)
         {
         if (peerInfo.attributes.get("hexColor") != null)
            {
            hexcolor = "0X" + peerInfo.attributes.get("hexColor");
            }
         else
            {
            hexcolor = "0X808080";
            }
         }
      try
         {
         color = Color.decode(hexcolor);
         }
      catch (NumberFormatException e)
         {
         LOG.trace("NumberFormatException in PeerInfoModel.getUserColor() " + hexcolor);
         }
      return color;
      }
   }