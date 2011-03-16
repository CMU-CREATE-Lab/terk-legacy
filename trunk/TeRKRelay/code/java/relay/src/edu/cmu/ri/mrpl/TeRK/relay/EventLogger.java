package edu.cmu.ri.mrpl.TeRK.relay;

import Ice.Identity;
import edu.cmu.ri.mrpl.TeRK.model.EventLogRecord;
import edu.cmu.ri.mrpl.TeRK.model.EventType;
import edu.cmu.ri.mrpl.TeRK.model.TerkUser;
import org.hibernate.Session;

/**
 * <p>
 * <code>EventLogger</code> is a singleton class used for logging events.
 * </p>
 *
 * @see EventType
 * @see EventLogRecord
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class EventLogger
   {
   private static final EventLogger INSTANCE = new EventLogger();

   public static EventLogger getInstance()
      {
      return INSTANCE;
      }

   /**
    * Logs the successful login of a user.  The event is logged by creating a new {@link EventLogRecord} which describes
    * the event and then the record is saved by calling {@link Session#save(Object)}.
    *
    * @param session the {@link Session} used to save the record.
    * @param user the {@link TerkUser} who just logged in
    * @param sessionIdentity the session {@link Identity} of the user who just logged in
    */
   public static void logLoginEvent(final Session session, final TerkUser user, final Identity sessionIdentity)
      {
      final EventLogRecord record = EventLogRecord.createLoginRecord(user, sessionIdentity);
      session.save(record);
      }

   /**
    * Logs the successful logout of a user.  The event is logged by creating a new {@link EventLogRecord} which
    * describes the event and then the record is saved by calling {@link Session#save(Object)}.
    *
    * @param session the {@link Session} used to save the record.
    * @param user the {@link TerkUser} who just logged out
    * @param sessionIdentity the session {@link Identity} of the user who just logged out
    */
   public static void logLogoutEvent(final Session session, final TerkUser user, final Identity sessionIdentity)
      {
      logLogoutEvent(session, user, sessionIdentity, false);
      }

   /**
    * Logs the successful forced logout of a user.  The event is logged by creating a new {@link EventLogRecord} which
    * describes the event and then the record is saved by calling {@link Session#save(Object)}.
    *
    * @param session the {@link Session} used to save the record.
    * @param user the {@link TerkUser} who was just forced logged out
    * @param sessionIdentity the session {@link Identity} of the user who was just forced logged out
    */
   public static void logForcedLogoutEvent(final Session session, final TerkUser user, final Identity sessionIdentity)
      {
      logLogoutEvent(session, user, sessionIdentity, true);
      }

   private static void logLogoutEvent(final Session session, final TerkUser user, final Identity sessionIdentity, final boolean wasForced)
      {
      final EventLogRecord record;
      if (wasForced)
         {
         record = EventLogRecord.createForcedLogoutRecord(user, sessionIdentity);
         }
      else
         {
         record = EventLogRecord.createLogoutRecord(user, sessionIdentity);
         }
      session.save(record);
      }

   /**
    * Logs the successful creation of a connection between two peers.  The event is logged by creating a new
    * {@link EventLogRecord} which describes the event and then the record is saved by calling
    * {@link Session#save(Object)}.
    *
    * @param session the {@link Session} used to save the record.
    * @param initiatingUser the {@link TerkUser} initiating the connection
    * @param initiatingUserSessionIdentity the session {@link Identity} of the initiating user
    * @param targetUser the user to whom the <code>initiatingUser</code> just connected.
    * @param targetUserSessionIdentity the session {@link Identity} of the target user
    */
   public static void logConnectionEstablishedEvent(final Session session, final TerkUser initiatingUser, final Identity initiatingUserSessionIdentity, final TerkUser targetUser, final Identity targetUserSessionIdentity)
      {
      final EventLogRecord record = EventLogRecord.createConnectionEstablishedRecord(initiatingUser,
                                                                                     initiatingUserSessionIdentity,
                                                                                     targetUser,
                                                                                     targetUserSessionIdentity);
      session.save(record);
      }

   /**
    * Logs the successful creation of a disconnection of two peers.  The event is logged by creating a new
    * {@link EventLogRecord} which describes the event and then the record is saved by calling
    * {@link Session#save(Object)}.
    *
    * @param session the {@link Session} used to save the record.
    * @param initiatingUser the {@link TerkUser} initiating the disconnection
    * @param initiatingUserSessionIdentity the session {@link Identity} of the initiating user
    * @param targetUser the user from whom the <code>initiatingUser</code> just disconnected.
    * @param targetUserSessionIdentity the session {@link Identity} of the target user
    */
   public static void logConnectionDestroyedEvent(final Session session, final TerkUser initiatingUser, final Identity initiatingUserSessionIdentity, final TerkUser targetUser, final Identity targetUserSessionIdentity)
      {
      final EventLogRecord record = EventLogRecord.createConnectionDestroyedRecord(initiatingUser,
                                                                                   initiatingUserSessionIdentity,
                                                                                   targetUser,
                                                                                   targetUserSessionIdentity);
      session.save(record);
      }

   private EventLogger()
      {
      // private to prevent instantiation
      }
   }
