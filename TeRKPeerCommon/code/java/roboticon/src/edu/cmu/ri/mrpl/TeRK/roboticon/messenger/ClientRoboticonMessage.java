package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ClientRoboticonMessage
   {
   private final Message message;
   private final List<Roboticon> roboticons = new ArrayList<Roboticon>();

   public ClientRoboticonMessage(final Message message, final Collection<Roboticon> roboticons)
      {
      this.message = (Message)message.clone();
      if ((roboticons != null) && (!roboticons.isEmpty()))
         {
         this.roboticons.addAll(roboticons);
         }
      }

   public String getText()
      {
      return message.text;
      }

   public String getSubject()
      {
      return message.subject;
      }

   /** Returns a copy of the message used to create this object. */
   public Message getMessage()
      {
      return (Message)message.clone();
      }

   /**
    * Returns an unmodifiable {@link List} containins copies of the {@link Roboticon}s contained in this object; returns
    * an empty {@link List} if this object contains no {@link Roboticon}s.  Guaranteed to not return <code>null</code>.
    */
   public List<Roboticon> getRoboticons()
      {
      final List<Roboticon> roboticonsCopy = new ArrayList<Roboticon>(roboticons.size());
      for (final Roboticon roboticon : roboticons)
         {
         if (roboticon != null)
            {
            roboticonsCopy.add((Roboticon)roboticon.clone());
            }
         }

      return Collections.unmodifiableList(roboticonsCopy);
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

      final ClientRoboticonMessage that = (ClientRoboticonMessage)o;

      if (message != null ? !message.equals(that.message) : that.message != null)
         {
         return false;
         }
      if (roboticons != null ? !roboticons.equals(that.roboticons) : that.roboticons != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (message != null ? message.hashCode() : 0);
      result = 31 * result + (roboticons != null ? roboticons.hashCode() : 0);
      return result;
      }
   }
