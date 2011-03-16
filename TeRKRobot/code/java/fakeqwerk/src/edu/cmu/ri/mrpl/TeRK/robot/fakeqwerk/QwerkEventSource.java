package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

public interface QwerkEventSource
   {

   public enum QwerkCommandHelper
      {
         DISCONNECTED,
         CONNECTED
      }

   ;

   void addQwerkEventListener(final QwerkEventListener qwerkEventListener);

   void removeQwerkEventListener(final QwerkEventListener qwerkEventListener);
   }
