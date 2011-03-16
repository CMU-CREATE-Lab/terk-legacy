package edu.cmu.ri.mrpl.TeRK.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import Ice.Identity;
import Ice.Util;

/**
 * Represents an audit log record.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class EventLogRecord
   {
   private Long id;
   private TerkUser user;
   private EventType eventType;
   private Calendar eventTimestamp;
   private String field0;
   private String field1;
   private String field2;
   private String field3;
   private String field4;
   private String field5;
   private String field6;
   private String field7;
   private String field8;
   private String field9;

   public static EventLogRecord createLoginRecord(final TerkUser user, final Identity sessionIdentity)
      {
      return new EventLogRecord(user,
                                EventType.LOGIN,
                                new GregorianCalendar(),
                                user.getUserId(),
                                getIdentityAsString(sessionIdentity),
                                null, null, null, null, null, null, null, null);
      }

   public static EventLogRecord createLogoutRecord(final TerkUser user, final Identity sessionIdentity)
      {
      return new EventLogRecord(user,
                                EventType.LOGOUT,
                                new GregorianCalendar(),
                                user.getUserId(),
                                getIdentityAsString(sessionIdentity),
                                null, null, null, null, null, null, null, null);
      }

   public static EventLogRecord createForcedLogoutRecord(final TerkUser user, final Identity sessionIdentity)
      {
      return new EventLogRecord(user,
                                EventType.FORCED_LOGOUT,
                                new GregorianCalendar(),
                                user.getUserId(),
                                getIdentityAsString(sessionIdentity),
                                null, null, null, null, null, null, null, null);
      }

   public static EventLogRecord createConnectionEstablishedRecord(final TerkUser initiatingUser, final Identity initiatingUserSessionIdentity, final TerkUser targetUser, final Identity targetUserSessionIdentity)
      {
      return new EventLogRecord(initiatingUser,
                                EventType.CONNECTION_ESTABLISHED,
                                new GregorianCalendar(),
                                initiatingUser.getUserId(),
                                getIdentityAsString(initiatingUserSessionIdentity),
                                targetUser.getUserIdentifier().getId(),
                                targetUser.getUserId(),
                                getIdentityAsString(targetUserSessionIdentity),
                                null, null, null, null, null);
      }

   public static EventLogRecord createConnectionDestroyedRecord(final TerkUser initiatingUser, final Identity initiatingUserSessionIdentity, final TerkUser targetUser, final Identity targetUserSessionIdentity)
      {
      return new EventLogRecord(initiatingUser,
                                EventType.CONNECTION_DESTROYED,
                                new GregorianCalendar(),
                                initiatingUser.getUserId(),
                                getIdentityAsString(initiatingUserSessionIdentity),
                                targetUser.getUserIdentifier().getId(),
                                targetUser.getUserId(),
                                getIdentityAsString(targetUserSessionIdentity),
                                null, null, null, null, null);
      }

   private static String getIdentityAsString(final Identity sessionIdentity)
      {
      return (sessionIdentity == null) ? null : Util.identityToString(sessionIdentity);
      }

   EventLogRecord()
      {
      }

   private EventLogRecord(final TerkUser user, final EventType eventType, final Calendar eventTimestamp, final Object field0, final Object field1, final Object field2, final Object field3, final Object field4, final Object field5, final Object field6, final Object field7, final Object field8, final Object field9)
      {
      this.user = user;
      this.eventType = eventType;
      this.eventTimestamp = eventTimestamp;
      this.field0 = (field0 == null) ? null : field0.toString();
      this.field1 = (field1 == null) ? null : field1.toString();
      this.field2 = (field2 == null) ? null : field2.toString();
      this.field3 = (field3 == null) ? null : field3.toString();
      this.field4 = (field4 == null) ? null : field4.toString();
      this.field5 = (field5 == null) ? null : field5.toString();
      this.field6 = (field6 == null) ? null : field6.toString();
      this.field7 = (field7 == null) ? null : field7.toString();
      this.field8 = (field8 == null) ? null : field8.toString();
      this.field9 = (field9 == null) ? null : field9.toString();
      }

   /** Returns the surrogate key used by the persistence layer. */
   public Long getId()
      {
      return id;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setId(final Long id)
      {
      this.id = id;
      }

   public TerkUser getUser()
      {
      return user;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setUser(final TerkUser user)
      {
      this.user = user;
      }

   public EventType getEventType()
      {
      return eventType;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setEventType(final EventType eventType)
      {
      this.eventType = eventType;
      }

   public Calendar getEventTimestamp()
      {
      return eventTimestamp;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setEventTimestamp(final Calendar eventTimestamp)
      {
      this.eventTimestamp = eventTimestamp;
      }

   public String getField0()
      {
      return field0;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField0(final String field0)
      {
      this.field0 = field0;
      }

   public String getField1()
      {
      return field1;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField1(final String field1)
      {
      this.field1 = field1;
      }

   public String getField2()
      {
      return field2;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField2(final String field2)
      {
      this.field2 = field2;
      }

   public String getField3()
      {
      return field3;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField3(final String field3)
      {
      this.field3 = field3;
      }

   public String getField4()
      {
      return field4;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField4(final String field4)
      {
      this.field4 = field4;
      }

   public String getField5()
      {
      return field5;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField5(final String field5)
      {
      this.field5 = field5;
      }

   public String getField6()
      {
      return field6;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField6(final String field6)
      {
      this.field6 = field6;
      }

   public String getField7()
      {
      return field7;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField7(final String field7)
      {
      this.field7 = field7;
      }

   public String getField8()
      {
      return field8;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField8(final String field8)
      {
      this.field8 = field8;
      }

   public String getField9()
      {
      return field9;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setField9(final String field9)
      {
      this.field9 = field9;
      }

   /**
    * Indicates whether some other object is "equal to" this one. Note that this implementation only compares the
    * {@link #getId() ID}s, which is the business key.
    *
    * @see #getId()
    */
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }

      final EventLogRecord that = (EventLogRecord)o;

      return !(getId() != null ? !getId().equals(that.getId()) : that.getId() != null);
      }

   /**
    * Returns a hash code value for the object.  Note that this implementation only considers the {@link #getId() ID}s
    * during the hash code computation (to be consistent with {@link #equals(Object)}).
    *
    * @see #getId()
    * @see #equals(Object)
    */
   public int hashCode()
      {
      return (getId() != null ? getId().hashCode() : 0);
      }

   public String toString()
      {
      return "EventLogRecord{" +
             "eventTimestamp=" + eventTimestamp +
             ", id=" + id +
             ", user=" + user +
             ", eventType=" + eventType +
             "}";
      }
   }
