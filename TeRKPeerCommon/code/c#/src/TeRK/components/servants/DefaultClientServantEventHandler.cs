using System;
using Ice;
using peer;

namespace TeRK.components.servants
   {
   public class DefaultClientServantEventHandler : ClientServantEventHandler
      {
      public void handlePeerConnectedEvent(string peerUserId, PeerAccessLevel accessLevel, ObjectPrx peerProxy, Current current)
         {
         Console.WriteLine("The peer [" + peerUserId + "|" + accessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
         }

      public void handlePeerConnectedNoProxyEvent(string peerUserId, PeerAccessLevel accessLevel, Current current)
         {
         Console.WriteLine("The peer [" + peerUserId + "|" + accessLevel + "] just connected to me (and I didn't get a proxy).");
         }

      public void handlePeerDisconnectedEvent(string peerUserId, Current current)
         {
         Console.WriteLine("The peer [" + peerUserId + "] just disconnected from me.");
         }

      public ProxyTypeIdToIdentityMap handleGetSupportedServicesEvent(Current current)
         {
         Console.WriteLine("The peer just called getSupportedServices() on me, but I don't support that method yet.");
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public void handleNewFrameEvent(Image frame, Current current)
         {
         Console.WriteLine("The peer just called newFrame() on me, but I don't support that method yet.");
         throw new NotImplementedException("This operation is not yet supported.");
         }
      }
   }