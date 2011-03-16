package edu.cmu.ri.mrpl.TeRK.relay;

import java.util.Calendar;
import java.util.GregorianCalendar;
import edu.cmu.ri.mrpl.TeRK.model.AccessLevel;
import edu.cmu.ri.mrpl.TeRK.model.PeerAssociationRule;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import edu.cmu.ri.mrpl.TeRK.model.UserType;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import edu.cmu.ri.mrpl.util.security.EncryptionService;
import org.hibernate.Session;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DatabasePopulator
   {
   public static void main(final String[] args)
      {
      final DatabasePopulator databasePopulator = new DatabasePopulator();
      databasePopulator.initialize();
      }

   // todo: read initialization data from a file instead of this lame hard-coding.
   private void initialize()
      {
      final EncryptionService crypt = EncryptionService.getInstance();

      final Calendar creationTime = new GregorianCalendar();
      final Calendar deletionTime = new GregorianCalendar();
      deletionTime.add(Calendar.SECOND, 5);

      final TerkUser user1 = new TerkUser("client1", crypt.encrypt("c1"), "Client", "One", UserType.HUMAN, creationTime);
      final TerkUser user2 = new TerkUser("client2", crypt.encrypt("c2"), "Client", "Two", UserType.HUMAN, creationTime);
      final TerkUser user3 = new TerkUser("client3", crypt.encrypt("c3"), "Client", "Three", UserType.HUMAN, creationTime);
      final TerkUser user4 = new TerkUser("client4", crypt.encrypt("c4"), "Client", "Four", UserType.HUMAN, creationTime);
      final TerkUser user5 = new TerkUser("client5", crypt.encrypt("c5"), "Client", "Five", UserType.HUMAN, creationTime);
      final TerkUser user6 = new TerkUser("client6", crypt.encrypt("c6"), "Client", "Six", UserType.HUMAN, creationTime);
      final TerkUser user7 = new TerkUser("client7", crypt.encrypt("c7"), "Client", "Seven", UserType.HUMAN, creationTime);
      final TerkUser user8 = new TerkUser("client8", crypt.encrypt("c8"), "Client", "Eight", UserType.HUMAN, creationTime);
      final TerkUser user9 = new TerkUser("client9", crypt.encrypt("c9"), "Client", "Nine", UserType.HUMAN, creationTime);

      final TerkUser robot1 = new TerkUser("robot1", crypt.encrypt("r1"), "Robot", "One", UserType.ROBOT, creationTime);
      final TerkUser robot2 = new TerkUser("robot2", crypt.encrypt("r2"), "Robot", "Two", UserType.ROBOT, creationTime);
      final TerkUser robot3 = new TerkUser("robot3", crypt.encrypt("r3"), "Robot", "Three", UserType.ROBOT, creationTime);
      final TerkUser robot4 = new TerkUser("robot4", crypt.encrypt("r4"), "Robot", "Four", UserType.ROBOT, creationTime);
      final TerkUser robot5 = new TerkUser("robot5", crypt.encrypt("r5"), "Robot", "Five", UserType.ROBOT, creationTime);
      final TerkUser robot6 = new TerkUser("robot6", crypt.encrypt("r6"), "Robot", "Six", UserType.ROBOT, creationTime);
      final TerkUser robot7 = new TerkUser("robot7", crypt.encrypt("r7"), "Robot", "Seven", UserType.ROBOT, creationTime);
      final TerkUser robot8 = new TerkUser("robot8", crypt.encrypt("r8"), "", "", UserType.ROBOT, creationTime);
      final TerkUser robot9 = new TerkUser("robot9", crypt.encrypt("r9"), null, null, UserType.ROBOT, creationTime);

      final Session session = HibernateUtil.getSessionFactory().openSession();
      session.beginTransaction();

      session.save(user1);
      session.save(user2);
      session.save(user3);
      session.save(user4);
      session.save(user5);
      session.save(user6);
      session.save(user7);
      session.save(user8);
      session.save(user9);
      session.save(robot1);
      session.save(robot2);
      session.save(robot3);
      session.save(robot4);
      session.save(robot5);
      session.save(robot6);
      session.save(robot7);
      session.save(robot8);
      session.save(robot9);

      // each user owns the robot with the same number
      createRule(session, user1, robot1, AccessLevel.OWNER);
      createRule(session, user2, robot2, AccessLevel.OWNER);
      createRule(session, user3, robot3, AccessLevel.OWNER);
      createRule(session, user4, robot4, AccessLevel.OWNER);
      createRule(session, user5, robot5, AccessLevel.OWNER);
      createRule(session, user6, robot6, AccessLevel.OWNER);
      createRule(session, user7, robot7, AccessLevel.OWNER);
      createRule(session, user8, robot8, AccessLevel.OWNER);
      createRule(session, user9, robot9, AccessLevel.OWNER);

      // user1 has ownership access to all robots
      createRule(session, user1, robot2, AccessLevel.OWNER);
      createRule(session, user1, robot3, AccessLevel.OWNER);
      createRule(session, user1, robot4, AccessLevel.OWNER);
      createRule(session, user1, robot5, AccessLevel.OWNER);
      createRule(session, user1, robot6, AccessLevel.OWNER);
      createRule(session, user1, robot7, AccessLevel.OWNER);
      createRule(session, user1, robot8, AccessLevel.OWNER);
      createRule(session, user1, robot9, AccessLevel.OWNER);

      // create some more rules
      createRule(session, user2, robot1, AccessLevel.OWNER_RESTRICTED, AccessLevel.NORMAL);
      createRule(session, user3, robot1, AccessLevel.NORMAL_ENHANCED, AccessLevel.NORMAL);
      createRule(session, user4, robot1, AccessLevel.NORMAL_RESTRICTED, AccessLevel.NORMAL);
      createRule(session, user5, robot1, AccessLevel.GUEST_ENHANCED, AccessLevel.NORMAL);
      createRule(session, user6, robot1, AccessLevel.GUEST, AccessLevel.NORMAL);
      createRule(session, user7, robot1, AccessLevel.GUEST_RESTRICTED, AccessLevel.NORMAL);

      // and even more rules
      createRule(session, user3, robot2, AccessLevel.OWNER_RESTRICTED, AccessLevel.NORMAL);
      createRule(session, user4, robot2, AccessLevel.NORMAL_ENHANCED, AccessLevel.NORMAL);
      createRule(session, user5, robot2, AccessLevel.NORMAL_RESTRICTED, AccessLevel.NORMAL);
      createRule(session, user6, robot2, AccessLevel.GUEST_ENHANCED, AccessLevel.NORMAL);
      createRule(session, user7, robot2, AccessLevel.GUEST, AccessLevel.NORMAL);

      // and now some rules where the user, the robot, or both are blocked
      createRule(session, user8, robot1, AccessLevel.NONE, AccessLevel.NORMAL);
      createRule(session, user8, robot2, AccessLevel.NONE, AccessLevel.NORMAL);
      createRule(session, user8, robot3, AccessLevel.NONE, AccessLevel.NORMAL);
      createRule(session, user8, robot4, AccessLevel.NORMAL, AccessLevel.NONE);
      createRule(session, user8, robot5, AccessLevel.NORMAL, AccessLevel.NONE);
      createRule(session, user8, robot6, AccessLevel.NORMAL, AccessLevel.NONE);
      createRule(session, user8, robot7, AccessLevel.NONE, AccessLevel.NONE);

      // finally, some one-way associations
      session.save(PeerAssociationRule.create(user9, robot1, AccessLevel.NORMAL));
      session.save(PeerAssociationRule.create(user9, robot2, AccessLevel.NORMAL));
      session.save(PeerAssociationRule.create(user9, robot3, AccessLevel.NONE));
      session.save(PeerAssociationRule.create(user9, robot4, AccessLevel.NONE));
      session.save(PeerAssociationRule.create(user9, robot5, AccessLevel.NORMAL));
      session.save(PeerAssociationRule.create(user9, robot6, AccessLevel.NORMAL));
      session.save(PeerAssociationRule.create(user9, robot7, AccessLevel.NONE));
      session.save(PeerAssociationRule.create(user9, robot8, AccessLevel.NONE));

      session.getTransaction().commit();
      session.close();
      }

   private void createRule(final Session session, final TerkUser user1, final TerkUser user2, final AccessLevel accessLevel)
      {
      createRule(session, user1, user2, accessLevel, accessLevel);
      }

   private void createRule(final Session session, final TerkUser user1, final TerkUser user2,
                           final AccessLevel accessLevel1,
                           final AccessLevel accessLevel2)
      {
      session.save(PeerAssociationRule.create(user1, user2, accessLevel1));
      session.save(PeerAssociationRule.create(user2, user1, accessLevel2));
      }
   }
