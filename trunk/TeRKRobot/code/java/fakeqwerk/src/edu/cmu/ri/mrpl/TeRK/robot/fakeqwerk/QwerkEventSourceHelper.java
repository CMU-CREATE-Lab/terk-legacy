package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.HashSet;
import java.util.Set;

final class QwerkEventSourceHelper implements QwerkEventSource
   {
   private final Set<QwerkEventListener> qwerkEventListeners = new HashSet<QwerkEventListener>();

   public void addQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      if (qwerkEventListener != null)
         {
         qwerkEventListeners.add(qwerkEventListener);
         }
      }

   public void removeQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      if (qwerkEventListener != null)
         {
         qwerkEventListeners.remove(qwerkEventListener);
         }
      }

   void fireQwerkEvent(final Object command)
      {
      if (!qwerkEventListeners.isEmpty())
         {
         for (final QwerkEventListener qwerkEventListener : qwerkEventListeners)
            {
            qwerkEventListener.handleQwerkEvent(command);
            }
         }
      }
   }
