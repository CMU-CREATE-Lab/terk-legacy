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
    public class TerkUserServantHelper : PropertyManagerOperations_
   {

   private Communicator communicator;
   private Dictionary<string, string> propertyMap = new Dictionary<string,string>();

   public TerkUserServantHelper(Communicator communicator)
      {
      this.communicator = communicator;
      }

   public PropertyMap getProperties(Current current__)
   {
      return getProperties();
   }
   public string getProperty(string key, Current current__)
   {
       return getProperty(key);
   }
   public string[] getPropertyKeys(Current current__)
   {
          return getPropertyKeys();
   }
   public void setProperty(string key, string value, Current current__)
   {
       setProperty(key, value);
   }

   public string getProperty(string key)
      {
       string value = "";
       propertyMap.TryGetValue(key, out value);
       return value;
      }

   public PropertyMap getProperties()
      {
       PropertyMap map = new PropertyMap();
       Dictionary<string, string>.Enumerator enumer = propertyMap.GetEnumerator();
       while(enumer.MoveNext()){
           map.Add(enumer.Current.Key, enumer.Current.Value);
       }
       return map;       
      }

   public string[] getPropertyKeys()
      {
      string[] value = new string[propertyMap.Keys.Count];
      propertyMap.Keys.CopyTo(value, 0);
      return value;
   }

   public void setProperty(string key, string value)
      {
        propertyMap[key]=value;
      }

   public void forcedLogoutNotification(Current current)
      {
      Trace.TraceError("DefaultTerkUserServantHelper.forcedLogoutNotification()");
      }

   public void peerConnected(string peerId, PeerAccessLevel peerAccessLevel, ObjectPrx objectPrx, Current current)
      {
     Trace.TraceError("DefaultTerkUserServantHelper.peerConnected()");
      //terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedEvent(peerId, peerAccessLevel, objectPrx);
      }

   public void peerConnectedNoProxy(string peerId, PeerAccessLevel peerAccessLevel, Current current)
      {
      Trace.TraceError("DefaultTerkUserServantHelper.peerConnectedNoProxy()");
      //terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedNoProxyEvent(peerId, peerAccessLevel);
      }

   public void peerDisconnected(string peerId, Current current)
      {
      Trace.TraceError("DefaultTerkUserServantHelper.peerDisconnected()");
      //terkCommunicator.getPeerConnectionEventDistributorHelper().publishPeerDisconnectedEvent(peerId);
      }

   /** Map of supported services */
   private ProxyTypeIdToIdentityMap commandControllerTypeToProxyIdentityMap = new ProxyTypeIdToIdentityMap();

   public ProxyTypeIdToIdentityMap getSupportedServices(Current current)
      {
       ProxyTypeIdToIdentityMap newMap = new ProxyTypeIdToIdentityMap();
       newMap.AddRange(commandControllerTypeToProxyIdentityMap);
       return newMap;
      }

   public void registerServiceServant(ObjectImpl serviceServant, ObjectPrx serviceServantProxy)
      {
      if ((serviceServant != null) && (serviceServantProxy != null))
         {
         string typeId = serviceServant.ice_id();
         Identity identity = serviceServantProxy.ice_getIdentity();
         Trace.TraceError("AbstractTerkUserServantHelper.registerServiceServant() is registering type id [" + typeId + "] to identity [" + Util.identityToString(identity) + "]");

         commandControllerTypeToProxyIdentityMap.Add(typeId, identity);
         }
      }
   }

}
