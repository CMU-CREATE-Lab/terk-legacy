package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import org.jdom.Document;
import org.jdom.JDOMException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessengerModel
   {
   private RoboticonMessengerHistory history;

   private final Set<RoboticonMessengerListener> roboticonMessengerListeners = new HashSet<RoboticonMessengerListener>();
   private String userId;
   private final boolean server;

   public RoboticonMessengerModel(boolean server)
      {
      this.history = XmlRoboticonMessengerHistory.create();
      this.userId = null;
      this.server = server;
      }

   public void addMessage(final RoboticonMessage message)
      {
      if (userId == null)
         {
         throw new IllegalStateException();
         }

      if (message != null)
         {
         synchronized (history)
            {// add it to the message collection
            history.addRoboticonMessage(message);
            }
         synchronized (roboticonMessengerListeners)
            {
            if (!roboticonMessengerListeners.isEmpty())
               {
               for (final RoboticonMessengerListener listener : roboticonMessengerListeners)
                  {
                  // send a copy of the message just in case listeners modify it
                  listener.messageAdded((RoboticonMessage)message.clone());
                  }
               }
            }
         }
      }

   public Document getDocument() throws IOException, JDOMException
      {
      Document d;
      synchronized (history)
         {
         d = history.getDocument();
         }
      return d;
      }

   @SuppressWarnings({"unchecked"})
   public List<RoboticonMessage> getMessageHistory()
      {
      List<RoboticonMessage> messages = new ArrayList<RoboticonMessage>();
      synchronized (history)
         {
         messages.addAll(history.getRoboticonMessages());
         }
      return Collections.unmodifiableList(messages);
      }

   @SuppressWarnings({"unchecked"})
   public void setMessageHistory(final RoboticonMessengerHistory messageHistory)
      {
      if (userId == null)
         {
         throw new IllegalStateException();
         }
      synchronized (history)
         {
         history.clear();
         for (RoboticonMessage r : messageHistory.getRoboticonMessages())
            {
            history.addRoboticonMessage(r);
            }
         }
      synchronized (roboticonMessengerListeners)
         {
         if (!roboticonMessengerListeners.isEmpty())
            {
            for (final RoboticonMessengerListener listener : roboticonMessengerListeners)
               {
               listener.contentsChanged();
               }
            }
         }
      }

   @SuppressWarnings({"unchecked"})
   public void setMessageHistory(final List<RoboticonMessage> messageHistory)
      {
      if (userId == null)
         {
         throw new IllegalStateException();
         }
      synchronized (history)
         {
         history.clear();
         for (RoboticonMessage r : messageHistory)
            {
            history.addRoboticonMessage(r);
            }
         }
      synchronized (roboticonMessengerListeners)
         {
         if (!roboticonMessengerListeners.isEmpty())
            {
            for (final RoboticonMessengerListener listener : roboticonMessengerListeners)
               {
               listener.contentsChanged();
               }
            }
         }
      }

   @SuppressWarnings({"unchecked"})
   public void setMessageHistory(final List<RoboticonMessage> messageHistory, final long lastLogoutTimestamp)
      {
      if (userId == null)
         {
         throw new IllegalStateException();
         }
      synchronized (history)
         {
         history.clear();
         for (RoboticonMessage r : messageHistory)
            {
            history.addRoboticonMessage(r);
            }
         }
      synchronized (roboticonMessengerListeners)
         {
         if (!roboticonMessengerListeners.isEmpty())
            {
            for (final RoboticonMessengerListener listener : roboticonMessengerListeners)
               {
               listener.contentsChanged(lastLogoutTimestamp);
               }
            }
         }
      }

   public void clearHistory()
      {
      if (userId == null)
         {
         throw new IllegalStateException();
         }
      synchronized (history)
         {
         history.clear();
         }
      synchronized (roboticonMessengerListeners)
         {
         if (!roboticonMessengerListeners.isEmpty())
            {
            for (final RoboticonMessengerListener listener : roboticonMessengerListeners)
               {
               listener.contentsChanged();
               }
            }
         }
      }

   public void addRoboticonMessengerListener(final RoboticonMessengerListener listener)
      {
      if (listener != null)
         {
         roboticonMessengerListeners.add(listener);
         }
      }

   public void removeRoboticonMessengerListener(final RoboticonMessengerListener listener)
      {
      if (listener != null)
         {
         roboticonMessengerListeners.remove(listener);
         }
      }

   public void setUserId(String userId)
      {
      this.userId = userId;
      }

   public String getUserId()
      {
      return this.userId;
      }

   public boolean isServer()
      {
      return this.server;
      }
   }
