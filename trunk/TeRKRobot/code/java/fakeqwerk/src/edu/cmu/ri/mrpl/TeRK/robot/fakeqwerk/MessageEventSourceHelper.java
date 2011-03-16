package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class MessageEventSourceHelper implements MessageEventSource
   {
   private final Set<MessageEventListener> messageEventListeners = new HashSet<MessageEventListener>();

   public void addMessageEventListener(final MessageEventListener messageEventListener)
      {
      if (messageEventListener != null)
         {
         messageEventListeners.add(messageEventListener);
         }
      }

   public void removeMessageEventListener(final MessageEventListener messageEventListener)
      {
      if (messageEventListener != null)
         {
         messageEventListeners.remove(messageEventListener);
         }
      }

   void fireMessageEvent(final String message)
      {
      if (!messageEventListeners.isEmpty())
         {
         for (final MessageEventListener messageEventListener : messageEventListeners)
            {
            messageEventListener.handleMessageEvent(message);
            }
         }
      }
   }
