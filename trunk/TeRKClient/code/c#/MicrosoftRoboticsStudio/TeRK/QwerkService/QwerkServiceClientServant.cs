using System;
using Ice;
using peer;
using TeRK;

namespace TeRK.client
   {
   class QwerkServiceClientServant : TerkClientDisp_
      {
      private bool wasLogoutForced = false;

      public override string getProperty(string key, Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public override string[] getPropertyKeys(Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public override void setProperty(string key, string value, Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public override void forcedLogoutNotification(Current current)
         {
         wasLogoutForced = true;
         }

      public bool getWasLogoutForced()
         {
         return wasLogoutForced;
         }

      public override void peerConnected(string peerUserId, PeerAccessLevel accessLevel, ObjectPrx peerProxy, Current current)
         {
         Console.WriteLine("The robot [" + peerUserId + "|" + accessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
         }

      public override void peerConnectedNoProxy(string peerUserId, PeerAccessLevel accessLevel, Current current)
         {
         Console.WriteLine("The robot [" + peerUserId + "|" + accessLevel + "] just connected to me (and I didn't get a proxy).");
         }

      public override void peerDisconnected(string peerUserId, Current current)
         {
         Console.WriteLine("The robot [" + peerUserId + "] just disconnected from me.");
         }

      public override ProxyTypeIdToIdentityMap getSupportedServices(Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public override void newFrame(Image frame, Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }
      }
   }