package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import edu.cmu.ri.mrpl.TeRK.model.AccessLevel;
import edu.cmu.ri.mrpl.TeRK.model.PeerAssociationRule;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.model.UserType;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import junit.framework.TestCase;
import org.hibernate.Session;

/**
 * <p>
 * <code>TerkUserTest</code> tests the {@link TerkUser} class.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class TerkUserTest extends TestCase
   {
   private static final String userId1 = "user1";
   private static final String password1 = "password1";
   private static final String firstName1 = "User1";
   private static final String lastName1 = "One";
   private static final UserType userType1 = UserType.HUMAN;
   private static final Calendar creationTimestamp1 = new GregorianCalendar(2006, 0, 15, 11, 12, 13);

   private static final String userId2 = "user2";
   private static final String password2 = "password2";
   private static final String firstName2 = "User2";
   private static final String lastName2 = "Two";
   private static final UserType userType2 = UserType.HUMAN;
   private static final Calendar creationTimestamp2 = new GregorianCalendar(2005, 1, 16, 12, 13, 14);

   private TerkUser terkUser1a = new TerkUser(userId1, password1, firstName1, lastName1, userType1, creationTimestamp1);
   private TerkUser terkUser1b = new TerkUser(userId1, password1, firstName1, lastName1, userType1, creationTimestamp1);
   private TerkUser terkUser2a = new TerkUser(userId2, password2, firstName2, lastName2, userType2, creationTimestamp2);
   private TerkUser terkUser2b = new TerkUser(userId2, password2, firstName2, lastName2, userType2, creationTimestamp2);

   public TerkUserTest(final String test)
      {
      super(test);
      }

   public void testConstructorAndGetters()
      {
      assertEquals(userId1, terkUser1a.getUserId());
      assertEquals(password1, terkUser1a.getPassword());
      assertEquals(firstName1, terkUser1a.getFirstName());
      assertEquals(lastName1, terkUser1a.getLastName());
      assertEquals(userType1, terkUser1a.getUserType());
      assertEquals(creationTimestamp1, terkUser1a.getCreationTimestamp());
      }

   public void testEquals()
      {
      assertEquals(terkUser1a, terkUser1b);
      assertEquals(terkUser2a, terkUser2b);
      assertTrue(!terkUser1a.equals(terkUser2a));
      assertTrue(!terkUser1a.equals(terkUser2b));
      }

   public void testHashCode()
      {
      assertEquals(terkUser1a.hashCode(), terkUser1b.hashCode());
      assertEquals(terkUser2a.hashCode(), terkUser2b.hashCode());
      assertTrue(terkUser1a.hashCode() != terkUser2a.hashCode());
      assertTrue(terkUser1a.hashCode() != terkUser2b.hashCode());
      }

   public void testRetrieveUser()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      System.out.println("*** user1 = [" + user1 + "]");
      System.out.println("*** user2 = [" + user2 + "]");

      session.getTransaction().commit();
      session.close();
      }

   public void testMarkAsRegistered()
      {
      markRegistrationStatus(true);
      }

   public void testMarkAsUnregistered()
      {
      markRegistrationStatus(false);
      }

   public void testListRegisteredUsers()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final List users = session.createQuery("select userId from TerkUser where registered = true").list();
      for (final ListIterator listIterator = users.listIterator(); listIterator.hasNext();)
         {
         final String userId = (String)listIterator.next();
         System.out.println(userId);
         }

      session.getTransaction().commit();
      session.close();
      }

   private void markRegistrationStatus(final boolean isRegistered)
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      user1.setRegistered(isRegistered);
      user2.setRegistered(isRegistered);

      session.getTransaction().commit();
      session.close();
      }

   public void testMultiple()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));

      session.getTransaction().commit();
      session.close();

      System.out.println("-------------------------------------------------------------------------------------------");
      doPeerAssociationCreation(user1);
      System.out.println("-------------------------------------------------------------------------------------------");
      doPeerAssociationRemoval(user1);
      System.out.println("-------------------------------------------------------------------------------------------");
      doPeerAssociationCreation(user1);
      System.out.println("-------------------------------------------------------------------------------------------");
      doPeerAssociationRemoval(user1);
      System.out.println("-------------------------------------------------------------------------------------------");
      }

   private void doPeerAssociationCreation(final TerkUser user1)
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      session.saveOrUpdate(user1);

      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      TerkUser.associatePeers(user1, user2);

      session.getTransaction().commit();
      session.close();
      }

   private void doPeerAssociationRemoval(final TerkUser user1)
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      session.saveOrUpdate(user1);

      final Set<TerkUser> peers = new HashSet<TerkUser>(user1.getTerkUserPeers());

      for (final TerkUser peer : peers)
         {
         TerkUser.disassociatePeers(user1, peer);
         session.saveOrUpdate(peer);
         }

      session.getTransaction().commit();
      session.close();
      }

   public void testPeerAssociationCreation()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      TerkUser.associatePeers(user1, user2);

      session.getTransaction().commit();
      session.close();
      }

   public void testPeerAssociationRemoval()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      TerkUser.disassociatePeers(user1, user2);

      session.getTransaction().commit();
      session.close();
      }

   public void testUserRemoval()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));

      // disassociate the user from all of its peers
      final Set<TerkUser> peers = new HashSet<TerkUser>(user1.getTerkUserPeers());
      for (final TerkUser peer : peers)
         {
         TerkUser.disassociatePeers(user1, peer);
         session.saveOrUpdate(peer);
         }

      // remove the user
      session.delete(user1);

      session.getTransaction().commit();
      session.close();
      }

   public void testPeerAssociationRuleCreation()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));
      final TerkUser user3 = (TerkUser)session.load(TerkUser.class, new Long(13));
      final TerkUser user4 = (TerkUser)session.load(TerkUser.class, new Long(14));

      final PeerAssociationRule rule1 = PeerAssociationRule.create(user1, user2, AccessLevel.OWNER);
      final PeerAssociationRule rule2 = PeerAssociationRule.create(user2, user1, AccessLevel.GUEST);
      final PeerAssociationRule rule3 = PeerAssociationRule.create(user1, user3, AccessLevel.OWNER_RESTRICTED);
      final PeerAssociationRule rule4 = PeerAssociationRule.create(user1, user4, AccessLevel.GUEST_RESTRICTED);
      session.save(rule1);
      session.save(rule2);
      session.save(rule3);
      session.save(rule4);

      session.getTransaction().commit();
      session.close();
      }

   public void testPeerAssociationRuleRemoval()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));
      removeSenderRulesForUser(user1, session);
      removeSenderRulesForUser(user2, session);

      session.getTransaction().commit();
      session.close();
      }

   private void removeSenderRulesForUser(final TerkUser user, final Session session)
      {
      final Set<PeerAssociationRule> rules = new HashSet<PeerAssociationRule>(user.getSenderPeerAssociationRules());
      for (final PeerAssociationRule rule : rules)
         {
         System.out.println("----------------------------------------------------------------------------------------");
         final TerkUser recipient = rule.getReceivingUser();
         System.out.println("BEFORE user1.getSenderPeerAssociationRules().size()    = [" + user.getSenderPeerAssociationRules().size() + "]");
         System.out.println("BEFORE user2.getRecipientPeerAssociationRules().size() = [" + recipient.getRecipientPeerAssociationRules().size() + "]");
         PeerAssociationRule.preparePeerAssociationRuleForDeletion(rule);
         session.delete(rule);
         System.out.println("AFTER user1.getSenderPeerAssociationRules().size()     = [" + user.getSenderPeerAssociationRules().size() + "]");
         System.out.println("AFTER user2.getRecipientPeerAssociationRules().size()  = [" + recipient.getRecipientPeerAssociationRules().size() + "]");
         }
      }

   public void testListPeerAssociationRules()
      {
      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      final TerkUser user1 = (TerkUser)session.load(TerkUser.class, new Long(2));
      final TerkUser user2 = (TerkUser)session.load(TerkUser.class, new Long(12));

      listRulesForUser(user1);
      listRulesForUser(user2);

      session.getTransaction().commit();
      session.close();
      }

   private void listRulesForUser(final TerkUser user)
      {
      System.out.println("Sender rules for user " + user.getUserId() + " (" + user.getSenderPeerAssociationRules().size() + "):");
      for (final PeerAssociationRule rule : user.getSenderPeerAssociationRules())
         {
         System.out.println("   " + rule);
         }

      System.out.println("Recipient rules for user " + user.getUserId() + " (" + user.getRecipientPeerAssociationRules().size() + "):");
      for (final PeerAssociationRule rule : user.getRecipientPeerAssociationRules())
         {
         System.out.println("   " + rule);
         }
      }
   }