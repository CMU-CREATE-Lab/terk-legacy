package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import Ice.Current;
import Ice.Identity;
import Ice.ObjectAdapter;
import Ice.ObjectNotExistException;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.model.PeerAssociationRule;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import edu.cmu.ri.mrpl.ice.ContextMapEntrySetter;
import edu.cmu.ri.mrpl.ice.IdentityToObjectProxyMapper;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.ConnectionEventSource;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.InvalidIdentityException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

/**
 * <p>
 * <code>ConnectionManager</code> manages connections between relay peers.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ConnectionManager implements RelayConnectionManager, ContextMapEntrySetter, IdentityToObjectProxyMapper
   {
   private static final Logger LOG = Logger.getLogger(ConnectionManager.class);
   private static final int PUBLIC_IDENTITY_MAP_INITIAL_CAPACITY = 10000;
   private static final int RELAY_USER_MAP_INITIAL_CAPACITY = 500;
   private static final String UNDERSCORE = "_";
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final String DASHED_LINE = "-----------------------------------------------------------------------------------------------------";
   private static final String PRINT_STATE_HEADER = "/" + DASHED_LINE + "\\";
   private static final String PRINT_STATE_FOOTER = "\\" + DASHED_LINE + "/";

   private final ObjectAdapter adapter;
   private final String contextMapKeyPeerIdentity;
   private final String contextMapKeyPeerUserid;

   /** Maps public identities to object proxies */
   private final Map<Identity, ObjectPrx> publicIdentityToCallbackMap = Collections.synchronizedMap(new HashMap<Identity, ObjectPrx>(PUBLIC_IDENTITY_MAP_INITIAL_CAPACITY));

   /** Maps a userId to that user's RelayUser object */
   private final Map<String, RelayUser> userIdToRelayUserMap = Collections.synchronizedMap(new HashMap<String, RelayUser>(RELAY_USER_MAP_INITIAL_CAPACITY));

   private static interface GetPeersStrategy
      {
      List getPeers(final Session session, final TerkUser user);
      }

   private final GetPeersStrategy getMyPeersStrategy =
         new GetPeersStrategy()
         {
         public List getPeers(final Session session, final TerkUser user)
            {
            return QueryHelper.findAllPeers(session, user);
            }
         };

   private final GetPeersStrategy getAvailablePeersStrategy =
         new GetPeersStrategy()
         {
         public List getPeers(final Session session, final TerkUser user)
            {
            return QueryHelper.findAvailablePeers(session, user);
            }
         };

   private final GetPeersStrategy getMyUnavailablePeersStrategy =
         new GetPeersStrategy()
         {
         public List getPeers(final Session session, final TerkUser user)
            {
            return QueryHelper.findUnavailablePeers(session, user);
            }
         };

   ConnectionManager(final ObjectAdapter adapter, final String contextMapKeyPeerIdentity, final String contextMapKeyPeerUserid)
      {
      this.adapter = adapter;
      this.contextMapKeyPeerIdentity = contextMapKeyPeerIdentity;
      this.contextMapKeyPeerUserid = contextMapKeyPeerUserid;
      }

   @SuppressWarnings({"unchecked"})
   public void setCustomContextMapEntries(final Current current)
      {
      String peerIdentityStr = "unknown";
      String peerUserId = "unknown";

      if (current.ctx == null)
         {
         current.ctx = new HashMap();
         }
      if (current.id != null)
         {
         peerIdentityStr = Util.identityToString(current.id);
         if ((current.id.category != null) && (current.id.category.startsWith(UNDERSCORE)))
            {
            peerUserId = current.id.category.substring(1);
            }
         }

      current.ctx.put(contextMapKeyPeerIdentity, peerIdentityStr);
      current.ctx.put(contextMapKeyPeerUserid, peerUserId);
      }

   public ObjectPrx getObjectProxyForIdentity(final Identity publicIdentity)
      {
      return publicIdentityToCallbackMap.get(publicIdentity);
      }

   public void registerUser(final String userId, final Identity sessionIdentity, final ObjectPrx mainCallbackProxy, final ConnectionEventSource connectionEventSource) throws RegistrationException
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         final TerkUser user = QueryHelper.findTerkUserByUserId(session, userId);

         if (user != null)
            {
            // mark the user as registered
            user.setRegistered(true);

            // add a login event to the event log
            EventLogger.logLoginEvent(session, user, sessionIdentity);
            }

         // commit the change to the database
         session.getTransaction().commit();

         if (user != null)
            {
            // add the user to the relay user map
            final RelayUser relayUser = new RelayUser(userId, sessionIdentity, mainCallbackProxy, connectionEventSource);
            userIdToRelayUserMap.put(userId, relayUser);
            }
         else
            {
            throw new RegistrationException("Relay registration failed since user [" + userId + "] does not exist.");
            }
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new RegistrationException("HibernateException while trying to register user [" + userId + "].  Original exception: " + e.getMessage());
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }

      printState();
      }

   public void registerProxy(final String userId, final ObjectPrx proxyToRegister) throws RegistrationException
      {
      final Collection<ObjectPrx> proxies = new ArrayList<ObjectPrx>();
      proxies.add(proxyToRegister);
      registerProxies(userId, proxies);
      }

   public void registerProxies(final String userId, final Collection<ObjectPrx> proxiesToRegister) throws RegistrationException
      {
      LOG.debug("ConnectionManager.registerProxies()");

      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // make sure the user exists and is logged in
         final boolean isLoggedIn = QueryHelper.findRegisteredTerkUserByUserId(session, userId) != null;

         // commit the change to the database
         session.getTransaction().commit();

         final RelayUser relayUser = userIdToRelayUserMap.get(userId);
         if (isLoggedIn && (relayUser != null))
            {
            relayUser.addProxies(proxiesToRegister);
            }
         else
            {
            throw new RegistrationException("Proxy registration failed since the user " + userId + " is not logged in.");
            }
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new RegistrationException("HibernateException while trying to register user [" + userId + "].  Original exception: " + e.getMessage());
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }
      if (LOG.isDebugEnabled())
         {
         printState();
         }
      }

   public ObjectPrx getPeerProxy(final String userId, final String peerUserId, final Identity privateProxyIdentity) throws InvalidIdentityException, PeerAccessException
      {
      final Collection<Identity> privateProxyIdentities = new ArrayList<Identity>();
      privateProxyIdentities.add(privateProxyIdentity);
      return getPeerProxies(userId, peerUserId, privateProxyIdentities).get(privateProxyIdentity);
      }

   public Map<Identity, ObjectPrx> getPeerProxies(final String userId, final String peerUserId, final Collection<Identity> privateProxyIdentities) throws InvalidIdentityException, PeerAccessException
      {
      LOG.debug("ConnectionManager.getPeerProxies()");

      if ((privateProxyIdentities == null) || (privateProxyIdentities.isEmpty()))
         {
         LOG.debug("ConnectionManager.getPeerProxies() throwing InvalidIdentityException since the collection of identities is empty or null.");
         throw new InvalidIdentityException("The collection of identities must not be empty or null");
         }

      Session session = null;

      try
         {
         final Map<Identity, ObjectPrx> identityToProxyMap = new HashMap<Identity, ObjectPrx>();

         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // find the user
         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);

         // find the peer
         final TerkUser peer = QueryHelper.findRegisteredTerkUserByUserId(session, peerUserId);

         // make sure the user is actually connected to this peer
         if ((user != null) && (peer != null))
            {
            if (user.getTerkUserPeers().contains(peer))
               {
               final RelayUser relayPeer = userIdToRelayUserMap.get(peerUserId);

               for (final Identity privateProxyIdentity : privateProxyIdentities)
                  {
                  // first make sure that the specified peer has actually registered a proxy with the given private identity
                  final ObjectPrx registeredProxy = relayPeer.getProxyByPrivateIdentity(privateProxyIdentity);

                  // skip it if it doesn't exist
                  if (registeredProxy != null)
                     {
                     // now create a new public identity with which we'll create the new proxy
                     final Identity publicIdentity = IceUtil.createIdentity(userId);

                     // create a new proxy to pass back to the user
                     final ObjectPrx newProxy = adapter.createProxy(publicIdentity);

                     // link the new identity to the registered proxy
                     publicIdentityToCallbackMap.put(publicIdentity, registeredProxy);

                     // add the new identity to this user's set of identities
                     relayPeer.addSharedPublicIdentityForSecondaryCallbackProxy(userId, publicIdentity);

                     if (LOG.isDebugEnabled())
                        {
                        LOG.debug("   Returning new proxy [" + (newProxy == null ? null : newProxy.ice_toString()) + "] with public identity [" + Util.identityToString(publicIdentity) + "] to user [" + userId + "]");
                        }

                     identityToProxyMap.put(privateProxyIdentity, newProxy);
                     }
                  else
                     {
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error("The identity [" + Util.identityToString(privateProxyIdentity) + "] does not identify any proxy registered to user [" + peerUserId + "].  Skipping it.");
                        }
                     }
                  }
               }
            else
               {
               final String message = "Proxy retrieval failed since users [" + userId + "] and [" + peerUserId + "] aren't currently connected.";
               LOG.error(message);
               throw new PeerAccessException(message);
               }
            }
         else
            {
            final String message = "Proxy retrieval failed since user [" + userId + "] or peer [" + peerUserId + "] does not exist.";
            LOG.error(message);
            throw new PeerAccessException(message);
            }

         // commit changes to the database
         session.getTransaction().commit();

         return identityToProxyMap;
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new PeerAccessException("HibernateException (rethrown as a PeerAccessException) while trying to determine whether the peers are connected" + e.getMessage());
         }
      catch (PeerAccessException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw e;
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }
      }

   public Set<PeerIdentifier> getMyPeers(final String userId) throws PeerException
      {
      return getPeers(userId, getMyPeersStrategy);
      }

   public Set<PeerIdentifier> getMyAvailablePeers(final String userId) throws PeerException
      {
      return getPeers(userId, getAvailablePeersStrategy);
      }

   public Set<PeerIdentifier> getMyUnavailablePeers(final String userId) throws PeerException
      {
      return getPeers(userId, getMyUnavailablePeersStrategy);
      }

   private Set<PeerIdentifier> getPeers(final String userId, final GetPeersStrategy strategy) throws PeerException
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // find the user
         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);

         final List peers;
         if (user == null)
            {
            peers = null;
            }
         else
            {
            // find the peers
            peers = strategy.getPeers(session, user);
            }

         session.getTransaction().commit();

         // build and return a set of PeerIdentifiers for the peers
         final Set<PeerIdentifier> peerIdentifiers = new HashSet<PeerIdentifier>();
         if ((peers != null) && (!peers.isEmpty()))
            {
            for (final ListIterator listIterator = peers.listIterator(); listIterator.hasNext();)
               {
               final TerkUser peer = (TerkUser)listIterator.next();
               peerIdentifiers.add(new PeerIdentifier(peer.getUserId(), peer.getFirstName(), peer.getLastName()));
               }
            }

         return peerIdentifiers;
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new PeerException("HibernateException while building the set of peers.  Original exception: " + e.getMessage());
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }
      }

   public ObjectPrx connectToPeer(final String userId1, final String userId2) throws PeerAccessException,
                                                                                     PeerUnavailableException,
                                                                                     PeerConnectionFailedException,
                                                                                     DuplicateConnectionException
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // find the user
         final TerkUser user1 = QueryHelper.findRegisteredTerkUserByUserId(session, userId1);

         if (user1 == null)
            {
            throw new PeerConnectionFailedException("Failed to connect to peer since user [" + userId1 + "] does not exist.");
            }

         // get the peer
         final TerkUser user2 = QueryHelper.findRegisteredTerkUserByUserId(session, userId2);

         if (user2 != null)
            {
            // make sure the two users aren't already connected to each other
            if (user1.isAssociatedWith(user2))
               {
               throw new DuplicateConnectionException("User [" + user1.getUserId() + "] is already connected to user [" + user2.getUserId() + "]");
               }

            // get the association rule for user1
            final PeerAssociationRule peerAssociationRule12 = QueryHelper.findPeerAssociationRule(session, user1, user2);

            // make sure user1 has access to user2
            if (peerAssociationRule12 == null)
               {
               throw new PeerAccessException("User [" + user1.getUserId() + "] does not have access to user [" + user2.getUserId() + "]");
               }

            // get the association rule for user2
            final PeerAssociationRule peerAssociationRule21 = QueryHelper.findPeerAssociationRule(session, user2, user1);

            // create public proxy identities and the associated proxy for each user
            final Identity user1PublicProxyIceIdentity = IceUtil.createIdentity(user1.getUserId());
            final Identity user2PublicProxyIceIdentity = IceUtil.createIdentity(user2.getUserId());
            final ObjectPrx user1PublicObjectPrx;
            if (peerAssociationRule21 != null)
               {
               user1PublicObjectPrx = adapter.createProxy(user2PublicProxyIceIdentity);
               }
            else
               {
               user1PublicObjectPrx = null;
               }
            final ObjectPrx user2PublicObjectPrx = adapter.createProxy(user1PublicProxyIceIdentity);

            // associate the two users
            TerkUser.associatePeers(user1, user2);

            // log the connection in the event log
            final RelayUser relayUser1 = userIdToRelayUserMap.get(userId1);
            final RelayUser relayUser2 = userIdToRelayUserMap.get(userId2);
            EventLogger.logConnectionEstablishedEvent(session, user1, relayUser1.getSessionIdentity(), user2, relayUser2.getSessionIdentity());

            // commit association to the database
            session.getTransaction().commit();

            // update in-memory maps
            relayUser1.addSharedPublicIdentityForMainCallbackProxy(userId2, user2PublicProxyIceIdentity);
            relayUser2.addSharedPublicIdentityForMainCallbackProxy(userId1, user1PublicProxyIceIdentity);
            publicIdentityToCallbackMap.put(user1PublicProxyIceIdentity, relayUser2.getMainCallbackProxy());
            publicIdentityToCallbackMap.put(user2PublicProxyIceIdentity, relayUser1.getMainCallbackProxy());

            // finally, notify the users' connection event handlers of the connection
            if (user1PublicObjectPrx == null)
               {
               relayUser2.getConnectionEventSource().firePeerConnectedNoProxyEvent(userId1, getPeerAccessLevel(peerAssociationRule12));
               }
            else
               {
               relayUser2.getConnectionEventSource().firePeerConnectedEvent(userId1, getPeerAccessLevel(peerAssociationRule12), user1PublicObjectPrx);
               relayUser1.getConnectionEventSource().firePeerConnectedEvent(userId2, getPeerAccessLevel(peerAssociationRule21), user2PublicObjectPrx);
               }

            if (LOG.isInfoEnabled())
               {
               LOG.info("Connected users:");
               LOG.info("   Initiating User: [" + userId1 + "|" + Util.identityToString(user2PublicProxyIceIdentity) + "]");
               LOG.info("   Target User:     [" + userId2 + "|" + Util.identityToString(user1PublicProxyIceIdentity) + "]");
               }

            if (LOG.isDebugEnabled())
               {
               printState();
               }

            return user2PublicObjectPrx;
            }

         throw new PeerUnavailableException("The requested peer connection could not be established since user " +
                                            "[" + userId2 + "] either does not exist or is not currently registered " +
                                            "with the relay.");
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new PeerConnectionFailedException("The requested peer connection could not be established because a " +
                                                 "database error occurred.  Original exception: " + e.getMessage());
         }
      catch (DuplicateConnectionException e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Failed to connect user [" + userId1 + "] to user [" + userId2 + "] because the users are already connected.");
            }
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw e;
         }
      catch (PeerAccessException e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Failed to connect user [" + userId1 + "] to user [" + userId2 + "] due to peer association rules.");
            }
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw e;
         }
      catch (PeerUnavailableException e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("Failed to connect user [" + userId1 + "] to user [" + userId2 + "] since the user either does " +
                      "not exist or is not currently registered with the relay.");
            }
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw e;
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to connect the peers.  Aborting.", e);
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new PeerConnectionFailedException("The requested peer connection could not be established. Original exception: " + e.getMessage());
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session", e);
               }
            }
         }
      }

   private PeerAccessLevel getPeerAccessLevel(final PeerAssociationRule rule)
      {
      if (rule != null)
         {
         switch (rule.getAccessLevel())
            {
            case OWNER:
               return PeerAccessLevel.AccessLevelOwner;
            case OWNER_RESTRICTED:
               return PeerAccessLevel.AccessLevelOwnerRestricted;
            case NORMAL_ENHANCED:
               return PeerAccessLevel.AccessLevelNormalEnhanced;
            case NORMAL:
               return PeerAccessLevel.AccessLevelNormal;
            case NORMAL_RESTRICTED:
               return PeerAccessLevel.AccessLevelNormalRestricted;
            case GUEST_ENHANCED:
               return PeerAccessLevel.AccessLevelGuestEnhanced;
            case GUEST:
               return PeerAccessLevel.AccessLevelGuest;
            case GUEST_RESTRICTED:
               return PeerAccessLevel.AccessLevelGuestRestricted;
            default:
               return PeerAccessLevel.AccessLevelNone;
            }
         }
      return PeerAccessLevel.AccessLevelNone;
      }

   public Set<PeerIdentifier> getConnectedPeers(final String userId) throws PeerException
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // find the user
         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);

         // get the current peers
         final Set<TerkUser> currentPeers;
         if (user == null)
            {
            currentPeers = null;
            }
         else
            {
            currentPeers = user.getTerkUserPeers();
            }

         session.getTransaction().commit();

         // build and return a set of PeerIdentifiers for the current peers
         if (LOG.isDebugEnabled())
            {
            LOG.debug("ConnectionManager.getConnectedPeers() found " + (currentPeers == null ? 0 : currentPeers.size()) + " peers:");
            }

         final Set<PeerIdentifier> peerIdentifiers = new HashSet<PeerIdentifier>();
         if ((currentPeers != null) && (!currentPeers.isEmpty()))
            {
            for (final TerkUser peer : currentPeers)
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("   " + peer.getUserId());
                  }
               peerIdentifiers.add(new PeerIdentifier(peer.getUserId(), peer.getFirstName(), peer.getLastName()));
               }
            }

         return peerIdentifiers;
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         throw new PeerException("HibernateException while building the set of current peers.  Original exception: " + e.getMessage());
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }
      }

   public void disconnectFromPeer(final String userId, final String peerUserId)
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         // find the user
         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);

         // find the peer
         final TerkUser peer = QueryHelper.findRegisteredTerkUserByUserId(session, peerUserId);

         // make sure the user is actually connected to this peer
         if ((user != null) && (peer != null))
            {
            if (user.getTerkUserPeers().contains(peer))
               {
               // disassociate the peers
               TerkUser.disassociatePeers(user, peer);

               // log the disconnection in the event log
               final RelayUser userRelayUser = userIdToRelayUserMap.get(userId);
               final RelayUser peerRelayUser = userIdToRelayUserMap.get(peerUserId);
               EventLogger.logConnectionDestroyedEvent(session, user, userRelayUser.getSessionIdentity(), peer, peerRelayUser.getSessionIdentity());

               // update the in-memory maps
               disconnectPeersInMemoryMaps(user.getUserId(), peer.getUserId());

               // notify the users of the disconnection
               notifyUserOfDisconnection(peer.getUserId(), user.getUserId());
               notifyUserOfDisconnection(user.getUserId(), peer.getUserId());
               }
            else
               {
               if (LOG.isInfoEnabled())
                  {
                  LOG.info("Ignoring disconnection request since user [" + userId + "] is not currently connected to peer [" + peerUserId + "]");
                  }
               }
            }
         else
            {
            if (LOG.isInfoEnabled())
               {
               LOG.info("Ignoring disconnection request since user [" + userId + "] or peer [" + peerUserId + "] do not exist.");
               }
            }

         // commit changes to the database
         session.getTransaction().commit();
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         LOG.error("HibernateException while trying to disconnect the peers.", e);
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }

      if (LOG.isDebugEnabled())
         {
         printState();
         }
      }

   public void disconnectFromPeers(final String userId)
      {
      disconnectFromPeers(userId, true);
      }

   private void disconnectFromPeers(final String userId, final boolean notifySelfOfDisconnection)
      {
      Session session = null;

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);
         final RelayUser userRelayUser = userIdToRelayUserMap.get(userId);
         if (user != null)
            {
            // iterate over user's peers and disconnect from each one
            for (final TerkUser peer : new HashSet<TerkUser>(user.getTerkUserPeers()))
               {
               // disassociate the peers (and update the peer in the database)
               TerkUser.disassociatePeers(user, peer);
               session.saveOrUpdate(peer);

               // log the disconnection in the event log
               final RelayUser peerRelayUser = userIdToRelayUserMap.get(peer.getUserId());
               EventLogger.logConnectionDestroyedEvent(session, user, userRelayUser.getSessionIdentity(), peer, peerRelayUser.getSessionIdentity());

               // update the in-memory maps
               disconnectPeersInMemoryMaps(user.getUserId(), peer.getUserId());

               // notify the users of the disconnection
               notifyUserOfDisconnection(peer.getUserId(), user.getUserId());
               if (notifySelfOfDisconnection)
                  {
                  notifyUserOfDisconnection(user.getUserId(), peer.getUserId());
                  }
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("disconnectFromPeers() couldn't find a registered user with id [" + userId + "]...maybe the user isn't registered?");
               }
            }

         // commit changes to the database
         session.getTransaction().commit();
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         LOG.error("HibernateException while trying to disconnect the peers.", e);
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }

      if (LOG.isDebugEnabled())
         {
         printState();
         }
      }

   private void disconnectPeersInMemoryMaps(final String userId1, final String userId2)
      {
      if (LOG.isInfoEnabled())
         {
         LOG.info("ConnectionManager.disconnectPeersInMemoryMaps(" + userId1 + "," + userId2 + ")");
         }

      // update the in-memory maps
      final RelayUser user1 = userIdToRelayUserMap.get(userId1);
      if (user1 != null)
         {
         final Set<Identity> publicIdentitiesSharedToUser2 = user1.getPublicIdentitiesSharedWithPeer(userId2);
         publicIdentityToCallbackMap.keySet().removeAll(publicIdentitiesSharedToUser2);
         user1.removePeerAssociation(userId2);
         }

      final RelayUser user2 = userIdToRelayUserMap.get(userId2);
      if (user2 != null)
         {
         final Set<Identity> publicIdentitiesSharedToUser1 = user2.getPublicIdentitiesSharedWithPeer(userId1);
         publicIdentityToCallbackMap.keySet().removeAll(publicIdentitiesSharedToUser1);
         user2.removePeerAssociation(userId1);
         }
      }

   private void notifyUserOfDisconnection(final String userId1, final String userId2)
      {
      try
         {
         final RelayUser user1 = userIdToRelayUserMap.get(userId1);
         if (user1 != null)
            {
            final ConnectionEventSource connectionEventSource = user1.getConnectionEventSource();
            if (connectionEventSource != null)
               {
               connectionEventSource.firePeerDisconnectedEvent(userId2);
               }
            }
         }
      catch (ObjectNotExistException e)
         {
         // log but otherwise ignore
         if (LOG.isInfoEnabled())
            {
            LOG.info("ObjectNotExistException ignored while sending disconnect message to connection event handler for user [" + userId1 + "]");
            }
         }
      catch (Exception e)
         {
         // log but otherwise ignore
         if (LOG.isInfoEnabled())
            {
            LOG.info("Exception ignored while sending disconnect message to connection event handler for user [" + userId1 + "]", e);
            }
         }
      }

   public void unregister(final String userId, final Identity sessionIdentity)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("ConnectionManager.unregister(" + userId + ", " + Util.identityToString(sessionIdentity) + ")");
         }
      final RelayUser user = userIdToRelayUserMap.get(userId);
      if (user != null)
         {
         final Identity registeredSessionIdentity = user.getSessionIdentity();
         if (registeredSessionIdentity != null)
            {
            if (registeredSessionIdentity.equals(sessionIdentity))
               {
               // log the user out
               unregisterUser(userId, sessionIdentity, false);
               }
            else
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("A user with id [" + userId + "] is currently registered, but the session identity differs, so unregister() won't be called.");
                  }
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("No user with id [" + userId + "] is currently registered.");
               }
            }
         }
      else
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("No user with id [" + userId + "] is currently registered.");
            }
         }
      }

   public void forceUnregister(final String userId)
      {
      try
         {
         final RelayUser user = userIdToRelayUserMap.get(userId);
         if (user != null)
            {
            final ConnectionEventSource connectionEventSource = user.getConnectionEventSource();
            unregisterUser(userId, user.getSessionIdentity(), true);

            // now that the user is unregistered, notify him of the forced logout
            if (connectionEventSource != null)
               {
               connectionEventSource.fireForcedLogoutNotificationEvent();
               }
            else
               {
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("forceUnregister() won't do anything for user [" + userId + "] since a ConnectionEventSource for the user could not be found.");
                  }
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("forceUnregister() won't do anything for user [" + userId + "] since the user could not be found.");
               }
            }
         }
      catch (ObjectNotExistException e)
         {
         // log but otherwise ignore
         if (LOG.isInfoEnabled())
            {
            LOG.info("ObjectNotExistException ignored while sending forced logout message to connection event handler for user [" + userId + "]");
            }
         }
      catch (Exception e)
         {
         // log but otherwise ignore
         if (LOG.isInfoEnabled())
            {
            LOG.info("Exception ignored while sending forced logout message to connection event handler for user [" + userId + "]", e);
            }
         }
      }

   private void unregisterUser(final String userId, final Identity sessionIdentity, final boolean wasForcedLogout)
      {
      Session session = null;

      disconnectFromPeers(userId, false);

      try
         {
         session = HibernateUtil.getSessionFactory().openSession();
         session.beginTransaction();

         final TerkUser user = QueryHelper.findRegisteredTerkUserByUserId(session, userId);
         if (user != null)
            {
            // mark the user as unregistered
            user.setRegistered(false);

            // add a logout event to the event log
            if (wasForcedLogout)
               {
               EventLogger.logForcedLogoutEvent(session, user, sessionIdentity);
               }
            else
               {
               EventLogger.logLogoutEvent(session, user, sessionIdentity);
               }
            }
         else
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("unregister() couldn't find a registered user with id [" + userId + "]...maybe the user isn't registered?");
               }
            }

         // commit the change to the database
         session.getTransaction().commit();

         // update in-memory maps
         userIdToRelayUserMap.remove(userId);

         if (LOG.isInfoEnabled())
            {
            LOG.info("Unregistered user [" + userId + "]");
            }
         printState();
         }
      catch (HibernateException e)
         {
         if (session != null)
            {
            session.getTransaction().rollback();
            }
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("HibernateException while trying to unregister user [" + userId + "].", e);
            }
         }
      finally
         {
         if (session != null && session.isOpen())
            {
            try
               {
               session.close();
               }
            catch (HibernateException e)
               {
               LOG.error("HibernateException while trying to close the session.", e);
               }
            }
         }
      }

   private void printState()
      {
      if (LOG.isInfoEnabled())
         {
         final StringBuffer str = new StringBuffer();

         if (LOG.isTraceEnabled() || LOG.isDebugEnabled())
            {
            str.append(LINE_SEPARATOR);
            }

         if (LOG.isTraceEnabled())
            {
            str.append(PRINT_STATE_HEADER).append(LINE_SEPARATOR);
            }

         if (LOG.isDebugEnabled())
            {
            final Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();

            final List registeredUsers = session.createQuery("from TerkUser where registered = true").list();

            str.append("REGISTERED USERS [").append(registeredUsers.size()).append("]:").append(LINE_SEPARATOR);
            for (final ListIterator listIterator = registeredUsers.listIterator(); listIterator.hasNext();)
               {
               final TerkUser user = (TerkUser)listIterator.next();
               str.append("   ").append(user.getUserId()).append(" [").append(user.getTerkUserPeers().size()).append("]").append(LINE_SEPARATOR);
               }

            session.getTransaction().commit();
            session.close();
            }

         if (LOG.isTraceEnabled())
            {
            str.append(DASHED_LINE).append(LINE_SEPARATOR);
            synchronized (publicIdentityToCallbackMap)
               {
               str.append("PUBLIC IDENTITY TO CALLBACK MAP [").append(publicIdentityToCallbackMap.size()).append("]:").append(LINE_SEPARATOR);
               for (final Identity key : publicIdentityToCallbackMap.keySet())
                  {
                  final ObjectPrx val = publicIdentityToCallbackMap.get(key);
                  str.append("   [").append(Util.identityToString(key)).append("] --> [").append(Util.identityToString(val.ice_getIdentity())).append("]").append(LINE_SEPARATOR);
                  }
               }
            str.append(DASHED_LINE).append(LINE_SEPARATOR);
            synchronized (userIdToRelayUserMap)
               {
               str.append("RELAY USERS [").append(userIdToRelayUserMap.size()).append("]:").append(LINE_SEPARATOR);
               for (final String key : userIdToRelayUserMap.keySet())
                  {
                  final RelayUser val = userIdToRelayUserMap.get(key);
                  str.append("   ").append(key).append(":").append(LINE_SEPARATOR).append(val.dumpToString());
                  }
               }
            }
         else if (LOG.isDebugEnabled())
            {
            synchronized (userIdToRelayUserMap)
               {
               str.append("RELAY USERS [").append(userIdToRelayUserMap.size()).append("]:").append(LINE_SEPARATOR);
               for (final String key : userIdToRelayUserMap.keySet())
                  {
                  str.append("   ").append(key).append(LINE_SEPARATOR);
                  }
               }
            }
         else
            {
            str.append("RELAY USERS [").append(userIdToRelayUserMap.size()).append("]").append(LINE_SEPARATOR);
            }

         if (LOG.isTraceEnabled())
            {
            str.append(PRINT_STATE_FOOTER).append(LINE_SEPARATOR);
            }
         LOG.info(str);
         }
      }
   }
