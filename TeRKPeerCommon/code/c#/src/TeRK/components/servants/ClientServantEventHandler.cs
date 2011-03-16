using Ice;
using peer;

namespace TeRK.components.servants
   {
   public interface ClientServantEventHandler
      {
      void handlePeerConnectedEvent(string peerUserId, PeerAccessLevel accessLevel, ObjectPrx peerProxy, Current current);

      void handlePeerConnectedNoProxyEvent(string peerUserId, PeerAccessLevel accessLevel, Current current);

      void handlePeerDisconnectedEvent(string peerUserId, Current current);

      ProxyTypeIdToIdentityMap handleGetSupportedServicesEvent(Current current);

      void handleNewFrameEvent(Image frame, Current current);
      }
   }