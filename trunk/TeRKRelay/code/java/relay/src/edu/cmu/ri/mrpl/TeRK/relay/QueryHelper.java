package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.List;
import edu.cmu.ri.mrpl.TeRK.model.PeerAssociationRule;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.model.UserIdentifier;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 * <p>
 * <code>QueryHelper</code> is a helper class for creating Hibernate queries.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class QueryHelper
   {
   private static final String HQL_VAR_USER_ID = "userId";
   private static final String HQL_VAR_PASSWORD = "password";
   private static final String HQL_VAR_USER = "user";
   private static final String HQL_VAR_USER_1 = "user1";
   private static final String HQL_VAR_USER_2 = "user2";

   /**
    * Returns <code>true</code> if the given <code>userId</code> and <code>encruptedPassword</code> match exactly one
    * user in the database; returns <code>false</code> otherwise.  Note that matching is case-sensitive.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param userId the {@link TerkUser#getUserId() user id} of the {@link TerkUser} to validate
    * @param encryptedPassword the encrypted {@link TerkUser#getPassword() password} of the {@link TerkUser} to validate
    * @return <code>true</code> if the login is valid; <code>false</code> otherwise
    */
   public static boolean isValidLogin(final Session session, final String userId, final String encryptedPassword)
      {
      final TerkUser terkUser = (TerkUser)session.getNamedQuery("findUsersHavingUserIdAndPassword")
            .setString(HQL_VAR_USER_ID, userId)
            .setString(HQL_VAR_PASSWORD, encryptedPassword)
            .uniqueResult();
      return (terkUser != null) && (terkUser.getUserId().equals(userId));
      }

   /**
    * Returns the {@link TerkUser} with the specified <code>userId</code>.  Returns <code>null</code> if no such user
    * exists.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param userId the {@link TerkUser#getUserId() user id} of the {@link TerkUser} to retrieve
    * @return the {@link TerkUser} with the specified <code>userId</code> or <code>null</code> if no such user exists
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static TerkUser findTerkUserByUserId(final Session session, final String userId) throws HibernateException
      {
      final UserIdentifier userIdentifier = (UserIdentifier)session.getNamedQuery("findUserIdentifierByUserId").setString(HQL_VAR_USER_ID, userId).uniqueResult();
      return userIdentifier.getTerkUser();
      }

   /**
    * Returns the {@link TerkUser} with the specified <code>userId</code> who is currently registered with the relay.
    * Returns <code>null</code> if no such user exists.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param userId the {@link TerkUser#getUserId() user id} of the {@link TerkUser} to retrieve
    * @return the {@link TerkUser} with the specified <code>userId</code> or <code>null</code> if no such user exists
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static TerkUser findRegisteredTerkUserByUserId(final Session session, final String userId) throws HibernateException
      {
      return (TerkUser)session.getNamedQuery("findRegisteredUserByUserId").setString(HQL_VAR_USER_ID, userId).uniqueResult();
      }

   /**
    * Returns a {@link List} of {@link TerkUser}s for which there exists a {@link PeerAssociationRule} with the given
    * <code>user</code>.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param user the {@link TerkUser} for which this method gathers users.
    * @return a {@link List} of {@link TerkUser}s which have a {@link PeerAssociationRule} with the given <code>user</code>
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static List findAllPeers(final Session session, final TerkUser user)
      {
      return session.getNamedQuery("findAllPeers")
            .setParameter(HQL_VAR_USER, user)
            .list();
      }

   /**
    * Returns a {@link List} of {@link TerkUser}s which are currently available to the given <code>user</code>.  A user
    * is considered available if he/she is currently registered with the relay and a {@link PeerAssociationRule} exists
    * for the pair which does not require authentication by the given <code>user</code>.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param user the {@link TerkUser} for which this method gathers available users.
    * @return a {@link List} of {@link TerkUser}s which are currently available to the given <code>user</code>
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static List findAvailablePeers(final Session session, final TerkUser user)
      {
      return session.getNamedQuery("findAvailablePeers")
            .setParameter(HQL_VAR_USER, user)
            .list();
      }

   /**
    * Returns a {@link List} of {@link TerkUser}s which are currently not available to the given <code>user</code>.  A
    * user is considered unavailable if he/she is not currently registered with the relay but a
    * {@link PeerAssociationRule} exists for the pair which does not require authentication by the given
    * <code>user</code>.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param user the {@link TerkUser} for which this method gathers unavailable users.
    * @return a {@link List} of {@link TerkUser}s which are currently unavailable to the given <code>user</code>
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static List findUnavailablePeers(final Session session, final TerkUser user)
      {
      return session.getNamedQuery("findUnavailablePeers")
            .setParameter(HQL_VAR_USER, user)
            .list();
      }

   /**
    * Returns the {@link PeerAssociationRule} with the specified <code>sendingUser</code> and <code>receivingUser</code>.
    * Returns <code>null</code> if no such rule exists.
    *
    * @param session the {@link Session} used to create the {@link Query}
    * @param sendingUser the {@link TerkUser} requesting the association
    * @param receivingUser the {@link TerkUser} with whom the <code>sendingUser</code> would like to associate
    * @return the {@link PeerAssociationRule} with the specified <code>sendingUser</code> and <code>receivingUser</code>
    *         or <code>null</code> if no such rule exists
    * @throws HibernateException if a problem occurs while creating or executing the query
    */
   public static PeerAssociationRule findPeerAssociationRule(final Session session, final TerkUser sendingUser, final TerkUser receivingUser) throws HibernateException
      {
      return (PeerAssociationRule)session.getNamedQuery("findPeerAssociationRuleForUsers")
            .setParameter(HQL_VAR_USER_1, sendingUser)
            .setParameter(HQL_VAR_USER_2, receivingUser)
            .uniqueResult();
      }

   private QueryHelper()
      {
      // private to prevent instantiation
      }
   }
