using System;
using System.Collections.Generic;
using System.Collections;
using System.Text;
using System.Text.RegularExpressions;
using System.Reflection;
using System.Threading;
using Ice;
using peer;
using TeRK.components.servants;
using TeRK.components.services;
using Exception=System.Exception;
using RouterPrx=Glacier2.RouterPrx;
using RouterPrxHelper=Glacier2.RouterPrxHelper;
using System.Diagnostics;


namespace TeRK.communications
{
public class TerkUserServant : TerkUserDisp_
   {
   // amount of time to wait before the forced logout notification is sent
   private static int FORCED_LOGOUT_NOTIFICATION_DELAY_MILLIS = 500;

   private TerkUserServantHelper helper;

   
   public TerkUserServant(Communicator communicator)
      {
      this.helper = new TerkUserServantHelper(communicator);
      }

   public TerkUserServant(TerkUserServantHelper helper)
      {
      this.helper = helper;
      }

   public override string getProperty(string key, Current current)
      {
      return helper.getProperty(key, current);
      }

   public override PropertyMap getProperties(Current current)
      {
      return helper.getProperties(current);
      }

   public override string[] getPropertyKeys(Current current)
      {
      return helper.getPropertyKeys(current);
      }

   public override void setProperty(string key, string value, Current current)
      {
      helper.setProperty(key, value, current);
      }

   public void registerServiceServant(ObjectImpl serviceServant, ObjectPrx serviceServantProxy)
      {
      helper.registerServiceServant(serviceServant, serviceServantProxy);
      }

   public override void peerConnected(string peerId, PeerAccessLevel peerAccessLevel, ObjectPrx peerProxy, Current current)
      {
         Trace.TraceError("TerkUserServant.peerConnected()" + current.ToString());
         Trace.TraceError("The peer [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(peerProxy.ice_getIdentity()) + "] just connected to me.");
      
       helper.peerConnected(peerId, peerAccessLevel, peerProxy, current);
   }

   public override void peerConnectedNoProxy(string peerId, PeerAccessLevel peerAccessLevel, Current current)
      {
         Trace.TraceError("TerkUserServant.peerConnectedNoProxy()" + current.ToString());
         Trace.TraceError("The peer [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
          helper.peerConnectedNoProxy(peerId, peerAccessLevel, current);
      }

   public override void peerDisconnected(string peerId, Current current)
      {
    Trace.TraceError("TerkUserServant.peerDisconnected()" + current.ToString());
         
      Trace.TraceError("The peer [" + peerId + "] just disconnected from me.");
         
      helper.peerDisconnected(peerId, current);
    }

    public override void forcedLogoutNotification(Current current)
      {
      Trace.TraceError("TerkUserServant.forcedLogoutNotification()");    
      }

   public override ProxyTypeIdToIdentityMap getSupportedServices(Current current)
      {
      return helper.getSupportedServices(current);
      }
   }
}
