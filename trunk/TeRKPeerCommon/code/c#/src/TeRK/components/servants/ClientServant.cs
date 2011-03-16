using System;
using Ice;
using peer;

namespace TeRK.components.servants
   {
   public class ClientServant : TerkClientDisp_
      {
      private bool wasLogoutForced = false;
      private ClientServantEventHandler eventHandler;

      public ClientServant(ClientServantEventHandler eventHandler)
         {
         this.eventHandler = eventHandler;
         }

      public override string getProperty(string key, Current current)
         {
         throw new NotImplementedException("This operation is not yet supported.");
         }

      public override PropertyMap getProperties(Current current)
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
         eventHandler.handlePeerConnectedEvent(peerUserId, accessLevel, peerProxy, current);
         }

      public override void peerConnectedNoProxy(string peerUserId, PeerAccessLevel accessLevel, Current current)
         {
         eventHandler.handlePeerConnectedNoProxyEvent(peerUserId, accessLevel, current);
         }

      public override void peerDisconnected(string peerUserId, Current current)
         {
         eventHandler.handlePeerDisconnectedEvent(peerUserId, current);
         }

      public override ProxyTypeIdToIdentityMap getSupportedServices(Current current)
         {
         return eventHandler.handleGetSupportedServicesEvent(current);
         }

      public override void newFrame(Image frame, Current current)
         {
         eventHandler.handleNewFrameEvent(frame, current);
         }
      }
   }