package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import Ice.Identity;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelper;
import Ice.Util;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.ConnectionEventSource;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RelayUser
   {
   private static final Logger LOG = Logger.getLogger(RelayUser.class);
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private final String userId;
   private final ObjectPrx mainCallbackProxy;
   private final Identity sessionIdentity;
   private final ConnectionEventSource connectionEventSource;
   private final Map<Identity, ObjectPrx> privateIdentityToSecondaryProxyMap = Collections.synchronizedMap(new HashMap<Identity, ObjectPrx>());
   private final Map<String, Identity> peerIdToSharedPublicIdentitiesOfMainProxyMap = Collections.synchronizedMap(new HashMap<String, Identity>());
   private final Map<String, Set<Identity>> peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap = Collections.synchronizedMap(new HashMap<String, Set<Identity>>());

   RelayUser(final String userId, final Identity sessionIdentity, final ObjectPrx mainCallbackProxy, final ConnectionEventSource connectionEventSource)
      {
      this.userId = userId;
      this.mainCallbackProxy = mainCallbackProxy;
      this.sessionIdentity = sessionIdentity;
      this.connectionEventSource = connectionEventSource;
      }

   String getUserId()
      {
      return userId;
      }

   ObjectPrx getMainCallbackProxy()
      {
      return mainCallbackProxy;
      }

   Identity getSessionIdentity()
      {
      return sessionIdentity;
      }

   ConnectionEventSource getConnectionEventSource()
      {
      return connectionEventSource;
      }

   void addProxies(final Collection<ObjectPrx> proxiesToRegister)
      {
      LOG.debug("RelayUser.addProxies()");
      if ((proxiesToRegister != null) && (!proxiesToRegister.isEmpty()))
         {
         for (final ObjectPrx proxyToRegister : proxiesToRegister)
            {
            // todo: do I need to do this new_context() stuff?
            final ObjectPrx proxy = ObjectPrxHelper.uncheckedCast(proxyToRegister.ice_context(IceUtil.TWOWAY_COMPRESSED_CALLBACK_CONTEXT_MAP));
            final Identity privateIdentity = proxy.ice_getIdentity();
            privateIdentityToSecondaryProxyMap.put(privateIdentity, proxy);
            if (LOG.isDebugEnabled())
               {
               LOG.debug("   Proxy with identity [" + Util.identityToString(privateIdentity) + "] is now registered to user [" + userId + "]");
               }
            }
         }
      }

   public ObjectPrx getProxyByPrivateIdentity(final Identity privateProxyIdentity)
      {
      return privateIdentityToSecondaryProxyMap.get(privateProxyIdentity);
      }

   public void addSharedPublicIdentityForMainCallbackProxy(final String peerUserId, final Identity publicIdentity)
      {
      peerIdToSharedPublicIdentitiesOfMainProxyMap.put(peerUserId, publicIdentity);
      }

   public void addSharedPublicIdentityForSecondaryCallbackProxy(final String peerUserId, final Identity publicIdentity)
      {
      synchronized (peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap)
         {
         // see if there's already a set for this peer
         Set<Identity> sharedPublicIdentities = peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.get(peerUserId);

         // if not, create one
         if (sharedPublicIdentities == null)
            {
            sharedPublicIdentities = Collections.synchronizedSet(new HashSet<Identity>());
            peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.put(peerUserId, sharedPublicIdentities);
            }

         // add the public identity to the set
         sharedPublicIdentities.add(publicIdentity);
         }
      }

   public Set<Identity> getPublicIdentitiesSharedWithPeer(final String peerUserId)
      {
      final Set<Identity> identities = new HashSet<Identity>();

      // get the public identity for the main proxy
      final Identity mainIdentity = peerIdToSharedPublicIdentitiesOfMainProxyMap.get(peerUserId);

      // get the public identities for the secondary proxies
      final Set<Identity> secondaryIdentities = peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.get(peerUserId);

      // add them to the collection to return (if not null and not empty)
      if (mainIdentity != null)
         {
         identities.add(mainIdentity);
         }
      if ((secondaryIdentities != null) && (!secondaryIdentities.isEmpty()))
         {
         identities.addAll(secondaryIdentities);
         }

      return Collections.unmodifiableSet(identities);
      }

   public void removePeerAssociation(final String peerUserId)
      {
      // remove the public identity for the main proxy
      peerIdToSharedPublicIdentitiesOfMainProxyMap.remove(peerUserId);

      // remove the public identities for the secondary proxies
      final Set<Identity> sharedPublicIdentities = peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.remove(peerUserId);

      // cleanup
      if (sharedPublicIdentities != null)
         {
         sharedPublicIdentities.clear();
         }
      }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final RelayUser relayUser = (RelayUser)o;

      return userId.equals(relayUser.userId);
      }

   public int hashCode()
      {
      return userId.hashCode();
      }

   public String toString()
      {
      return "RelayUser{" +
             "userId='" + userId + "'" +
             "}";
      }

   public String dumpToString()
      {
      final StringBuffer str = new StringBuffer();
      str.append("      ").append("User Id:                 ").append(userId).append(LINE_SEPARATOR);
      str.append("      ").append("Session Identity:        ").append(Util.identityToString(sessionIdentity)).append(LINE_SEPARATOR);
      str.append("      ").append("Main Callback Proxy:     ").append(Util.identityToString(mainCallbackProxy.ice_getIdentity())).append(LINE_SEPARATOR);
      str.append("      ").append("Connection Event Source: ").append(connectionEventSource).append(LINE_SEPARATOR);

      synchronized (privateIdentityToSecondaryProxyMap)
         {
         str.append("      ").append("privateIdentityToSecondaryProxyMap: ").append(LINE_SEPARATOR);
         for (final Identity identity : privateIdentityToSecondaryProxyMap.keySet())
            {
            str.append("         ").append(Util.identityToString(identity)).append(LINE_SEPARATOR);
            }
         }

      synchronized (peerIdToSharedPublicIdentitiesOfMainProxyMap)
         {
         str.append("      ").append("peerIdToSharedPublicIdentitiesOfMainProxyMap: ").append(LINE_SEPARATOR);
         for (final String peerId : peerIdToSharedPublicIdentitiesOfMainProxyMap.keySet())
            {
            final Identity identity = peerIdToSharedPublicIdentitiesOfMainProxyMap.get(peerId);
            str.append("         ").append(peerId).append(": ").append(Util.identityToString(identity)).append(LINE_SEPARATOR);
            }
         }

      synchronized (peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap)
         {
         str.append("      ").append("peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap: ").append(LINE_SEPARATOR);
         for (final String peerId : peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.keySet())
            {
            final Set<Identity> identities = peerIdToSharedPublicIdentitiesOfSecondaryProxiesMap.get(peerId);
            str.append("         ").append(peerId).append(":").append(LINE_SEPARATOR);
            for (final Identity identity : identities)
               {
               str.append("            ").append(Util.identityToString(identity)).append(LINE_SEPARATOR);
               }
            }
         }

      return str.toString();
      }
   }
