package edu.cmu.ri.mrpl.TeRK.model;

/**
 * Defines access rules for messages sent from one user (the "sending user") to another (the "receiving user") in a peer
 * relationship.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class PeerAssociationRule
   {
   private Long id;
   private TerkUser sendingUser;
   private TerkUser receivingUser;
   private AccessLevel accessLevel;

   PeerAssociationRule()
      {
      }

   /**
    * Creates a new {@link PeerAssociationRule} and then adds it to the appropriate collections contained by its sender
    * and recipient users.
    */
   public static PeerAssociationRule create(final TerkUser sendingUser, final TerkUser receivingUser, final AccessLevel accessLevel)
      {
      final PeerAssociationRule peerAssociationRule = new PeerAssociationRule(sendingUser, receivingUser, accessLevel);
      sendingUser.getSenderPeerAssociationRules().add(peerAssociationRule);
      receivingUser.getRecipientPeerAssociationRules().add(peerAssociationRule);

      return peerAssociationRule;
      }

   /**
    * Prepares the given {@link PeerAssociationRule} for deletion by removing it from the appropriate collections
    * contained by its sender and recipient users and setting the {@link PeerAssociationRule}'s users to
    * <code>null</code>.
    */
   public static void preparePeerAssociationRuleForDeletion(final PeerAssociationRule peerAssociationRule)
      {
      peerAssociationRule.getSendingUser().getSenderPeerAssociationRules().remove(peerAssociationRule);
      peerAssociationRule.getReceivingUser().getRecipientPeerAssociationRules().remove(peerAssociationRule);
      peerAssociationRule.setSendingUser(null);
      peerAssociationRule.setReceivingUser(null);
      }

   private PeerAssociationRule(final TerkUser sendingUser, final TerkUser receivingUser, final AccessLevel accessLevel)
      {
      this.sendingUser = sendingUser;
      this.receivingUser = receivingUser;
      this.accessLevel = accessLevel;
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

   public TerkUser getSendingUser()
      {
      return sendingUser;
      }

   public void setSendingUser(final TerkUser sendingUser)
      {
      this.sendingUser = sendingUser;
      }

   public TerkUser getReceivingUser()
      {
      return receivingUser;
      }

   public void setReceivingUser(final TerkUser receivingUser)
      {
      this.receivingUser = receivingUser;
      }

   public AccessLevel getAccessLevel()
      {
      return accessLevel;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setAccessLevel(final AccessLevel accessLevel)
      {
      this.accessLevel = accessLevel;
      }

   /**
    * Indicates whether some other object is "equal to" this one. Note that this implementation only compares the two
    * {@link TerkUser}s, since the combination of the two is the business key.
    *
    * @see #getSendingUser()
    * @see #getReceivingUser()
    */
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }

      final PeerAssociationRule that = (PeerAssociationRule)o;

      if (getSendingUser() != null ? !getSendingUser().equals(that.getSendingUser()) : that.getSendingUser() != null)
         {
         return false;
         }
      return !(getReceivingUser() != null ? !getReceivingUser().equals(that.getReceivingUser()) : that.getReceivingUser() != null);
      }

   /**
    * Returns a hash code value for the object.  Note that this implementation only considers the two {@link TerkUser}s
    *  during the hash code computation (to be consistent with {@link #equals(Object)}).
    *
    * @see #getSendingUser()
    * @see #getReceivingUser()
    * @see #equals(Object)
    */
   public int hashCode()
      {
      int result;
      result = (getSendingUser() != null ? getSendingUser().hashCode() : 0);
      result = 29 * result + (getReceivingUser() != null ? getReceivingUser().hashCode() : 0);
      return result;
      }

   public String toString()
      {
      return "PeerAssociationRule{" +
             "id=" + getId() +
             ", sendingUser=" + (getSendingUser() == null ? null : getSendingUser().getUserId()) +
             ", receivingUser=" + (getReceivingUser() == null ? null : getReceivingUser().getUserId()) +
             ", accessLevel=" + (getAccessLevel() == null ? null : getAccessLevel().getLevel()) +
             "}";
      }
   }
