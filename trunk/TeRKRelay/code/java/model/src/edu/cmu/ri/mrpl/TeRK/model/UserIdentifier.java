package edu.cmu.ri.mrpl.TeRK.model;

import java.io.Serializable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class UserIdentifier implements Serializable
   {
   private Long id;
   private String userId;
   private String password;
   private String firstName;
   private String lastName;
   private TerkUser terkUser;

   UserIdentifier()
      {
      }

   public UserIdentifier(final String userId, final String password, final String firstName, final String lastName)
      {
      this.userId = userId;
      this.password = password;
      this.firstName = firstName;
      this.lastName = lastName;
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
    * Returns the user's login id.  To the persistence layer, this is the business key (it also happens to be a natural
    * key since our user ids never change).
    */
   public String getUserId()
      {
      return userId;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setUserId(final String userId)
      {
      this.userId = userId;
      }

   public String getPassword()
      {
      return password;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setPassword(final String password)
      {
      this.password = password;
      }

   public String getFirstName()
      {
      return firstName;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setFirstName(final String firstName)
      {
      this.firstName = firstName;
      }

   public String getLastName()
      {
      return lastName;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setLastName(final String lastName)
      {
      this.lastName = lastName;
      }

   public TerkUser getTerkUser()
      {
      return terkUser;
      }

   @SuppressWarnings({"UNUSED_SYMBOL"})
   private void setTerkUser(final TerkUser terkUser)
      {
      this.terkUser = terkUser;
      }

   /**
    * Indicates whether some other object is "equal to" this one. Note that this implementation only compares the user
    * ids, since it is the business key.
    *
    * @see #getUserId()
    */
   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }

      final UserIdentifier userIdentifier = (UserIdentifier)o;

      // note: using accessor methods here instead of direct field access since the object being compared to this one
      // might be a proxy object provided by the persistence layer.
      return !(getUserId() != null ? !getUserId().equals(userIdentifier.getUserId()) : userIdentifier.getUserId() != null);
      }

   /**
    * Returns a hash code value for the object.  Note that this implementation only considers the user id during the
    * hash code computation (to be consistent with {@link #equals(Object)}).
    *
    * @see #getUserId()
    * @see #equals(Object)
    */
   public int hashCode()
      {
      return (getUserId() != null ? getUserId().hashCode() : 0);
      }

   public String toString()
      {
      return "UserIdentifier{" +
             "id=" + getId() +
             ", userId='" + getUserId() + "'" +
             ", password='" + getPassword() + "'" +
             ", firstName='" + getFirstName() + "'" +
             ", lastName='" + getLastName() + "'" +
             "}";
      }
   }
