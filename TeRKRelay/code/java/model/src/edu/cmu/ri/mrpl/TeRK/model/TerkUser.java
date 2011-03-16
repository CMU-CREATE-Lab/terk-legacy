package edu.cmu.ri.mrpl.TeRK.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class TerkUser implements Serializable
   {
   private Long id;
   private UserIdentifier userIdentifier = new UserIdentifier();
   private UserType userType;
   private Calendar creationTimestamp;
   private boolean registered;
   private Set<TerkUser> terkUserPeers = new HashSet<TerkUser>();
   private Set<PeerAssociationRule> senderPeerAssociationRules = new HashSet<PeerAssociationRule>();
   private Set<PeerAssociationRule> recipientPeerAssociationRules = new HashSet<PeerAssociationRule>();

   TerkUser()
      {
      }

   public TerkUser(final String userId, final String password, final String firstName, final String lastName, final UserType userType, final Calendar creationTimestamp)
      {
      this(userId, password, firstName, lastName, userType, creationTimestamp, Boolean.FALSE.booleanValue());
      }

   private TerkUser(final String userId, final String password, final String firstName, final String lastName, final UserType userType, final Calendar creationTimestamp, final boolean isRegistered)
      {
      this.userIdentifier = new UserIdentifier(userId, password, firstName, lastName);
      this.userType = userType;
      this.creationTimestamp = creationTimestamp;
      this.registered = isRegistered;
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

   /**
    * Returns the user's identifier.  To the persistence layer, this is the business key (it also happens to be a natural
    * key since our user identifiers never change).
    */
   public UserIdentifier getUserIdentifier()
      {
      return userIdentifier;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setUserIdentifier(final UserIdentifier userIdentifier)
      {
      this.userIdentifier = userIdentifier;
      }

   /**
    * Convenience method which return the user's login id stored in the {@link UserIdentifier} returned by
    * {@link #getUserIdentifier()}.
    *
    * @see UserIdentifier#getUserId()
    */
   public String getUserId()
      {
      return userIdentifier.getUserId();
      }

   /**
    * Convenience method which return the user's password stored in the {@link UserIdentifier} returned by
    * {@link #getUserIdentifier()}.
    *
    * @see UserIdentifier#getPassword()
    */
   public String getPassword()
      {
      return userIdentifier.getPassword();
      }

   /**
    * Convenience method which return the user's first name stored in the {@link UserIdentifier} returned by
    * {@link #getUserIdentifier()}.
    *
    * @see UserIdentifier#getFirstName()
    */
   public String getFirstName()
      {
      return userIdentifier.getFirstName();
      }

   /**
    * Convenience method which return the user's last name stored in the {@link UserIdentifier} returned by
    * {@link #getUserIdentifier()}.
    *
    * @see UserIdentifier#getLastName()
    */
   public String getLastName()
      {
      return userIdentifier.getLastName();
      }

   public UserType getUserType()
      {
      return userType;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setUserType(final UserType userType)
      {
      this.userType = userType;
      }

   public Calendar getCreationTimestamp()
      {
      return creationTimestamp;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setCreationTimestamp(final Calendar creationTimestamp)
      {
      this.creationTimestamp = creationTimestamp;
      }

   public boolean isRegistered()
      {
      return registered;
      }

   public void setRegistered(final boolean registered)
      {
      this.registered = registered;
      }

   @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
   public Set<TerkUser> getTerkUserPeers()
      {
      return terkUserPeers;
      }

   @SuppressWarnings({"UNUSED_SYMBOL", "AssignmentToCollectionOrArrayFieldFromParameter"})
   private void setTerkUserPeers(final Set<TerkUser> terkUserPeers)
      {
      this.terkUserPeers = terkUserPeers;
      }

   /**
    * Associates the two given {@link TerkUser} peers.  Does nothing if either peer is <code>null</code> or if the peers
    * are {@link #equals equal}.
    */
   public static void associatePeers(final TerkUser user1, final TerkUser user2)
      {
      if ((user1 != null) && (user2 != null) && (!user1.equals(user2)))
         {
         user1.getTerkUserPeers().add(user2);
         user2.getTerkUserPeers().add(user1);
         }
      }

   /**
    * Disassociates the two given {@link TerkUser} peers.  Does nothing if either peer is <code>null</code> or if the
    * peers are {@link #equals equal}.
    */
   public static void disassociatePeers(final TerkUser user1, final TerkUser user2)
      {
      if ((user1 != null) && (user2 != null) && (!user1.equals(user2)))
         {
         user1.getTerkUserPeers().remove(user2);
         user2.getTerkUserPeers().remove(user1);
         }
      }

   public boolean isAssociatedWith(final TerkUser otherUser)
      {
      return getTerkUserPeers().contains(otherUser);
      }

   @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
   public Set<PeerAssociationRule> getSenderPeerAssociationRules()
      {
      return senderPeerAssociationRules;
      }

   @SuppressWarnings({"UNUSED_SYMBOL", "AssignmentToCollectionOrArrayFieldFromParameter"})
   private void setSenderPeerAssociationRules(final Set<PeerAssociationRule> senderPeerAssociationRules)
      {
      this.senderPeerAssociationRules = senderPeerAssociationRules;
      }

   @SuppressWarnings({"ReturnOfCollectionOrArrayField"})
   public Set<PeerAssociationRule> getRecipientPeerAssociationRules()
      {
      return recipientPeerAssociationRules;
      }

   @SuppressWarnings({"UNUSED_SYMBOL", "AssignmentToCollectionOrArrayFieldFromParameter"})
   private void setRecipientPeerAssociationRules(final Set<PeerAssociationRule> recipientPeerAssociationRules)
      {
      this.recipientPeerAssociationRules = recipientPeerAssociationRules;
      }

   /**
    * Indicates whether some other object is "equal to" this one. Note that this implementation only compares the user
    * identifiers, since it is the business key.
    *
    * @see #getUserIdentifier()
    */
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }

      final TerkUser terkUser = (TerkUser)o;

      // note: using accessor methods here instead of direct field access since the object being compared to this one
      // might be a proxy object provided by the persistence layer.
      return !(getUserIdentifier() != null ? !getUserIdentifier().equals(terkUser.getUserIdentifier()) : terkUser.getUserIdentifier() != null);
      }

   /**
    * Returns a hash code value for the object.  Note that this implementation only considers the user identifier during
    * the hash code computation (to be consistent with {@link #equals(Object)}).
    *
    * @see #getUserIdentifier()
    * @see #equals(Object)
    */
   public int hashCode()
      {
      return (getUserIdentifier() != null ? getUserIdentifier().hashCode() : 0);
      }

   public String toString()
      {
      final Date prettyCreationTimestamp = getCreationTimestamp() == null ? null : getCreationTimestamp().getTime();
      return "TerkUser{" +
             "id=" + getId() +
             ", userIdentifier='" + getUserIdentifier() + "'" +
             ", userType=" + getUserType() +
             ", creationTimestamp=" + prettyCreationTimestamp +
             ", registered=" + isRegistered() +
             "}";
      }
   }
