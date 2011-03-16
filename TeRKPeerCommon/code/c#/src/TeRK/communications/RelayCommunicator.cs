using System;
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
   public class RelayCommunicator : TerkCommunicator
      {
      /// <summary>
      /// TeRK relay session pinger
      /// </summary>
       private Communicator communicator;
       public RelayCommunicator(string applicationName, string configFileClasspath)
       {
           try
           {
               InitializationData initData = new InitializationData();
               initData.properties = Util.createProperties();
               Trace.TraceInformation("About to load properties...");
               initData.properties.load(configFileClasspath);
               Trace.TraceInformation("Done loading properties!  About to call Util.initialize()...");
               communicator = Util.initialize(initData);
               Trace.TraceInformation("Done calling Util.initialize()!");
           }
           catch (Exception e)
           {
               throw e;
           }

       }

      private static void pingSession(object state)
         {
         try
            {
            UserSessionPrx userSessionPrx = (UserSessionPrx) state;
            userSessionPrx.ice_ping();
            }
         catch (Exception e)
            {
            Trace.TraceError("Exception while pinging: " + e.ToString());
            throw e;
            }
         }

      private Timer sessionPinger;

      public UserSessionPrx loginAndRegister(string userId, string password)
         {
         // log in
         Trace.TraceInformation("Logging in as user [" + userId + "]");
         RouterPrx glacierRouter = RouterPrxHelper.checkedCast(communicator.getDefaultRouter());
         UserSessionPrx userSessionPrx = UserSessionPrxHelper.uncheckedCast(glacierRouter.createSession(userId, password));
         Trace.TraceInformation("Login successful!  Session identity = [" + Util.identityToString(userSessionPrx.ice_getIdentity()) + "]");

         // start session pinger
         sessionPinger = startSessionPinger(userSessionPrx);

         // create the servant and its proxy
         Trace.TraceInformation("Creating client servant and proxy");
         ClientServant servant = new ClientServant(new DefaultClientServantEventHandler());
         ObjectAdapter adapter = communicator.createObjectAdapter("Qwerk.Service.Client");
         adapter.activate();
         string category = glacierRouter.getServerProxy().ice_getIdentity().category;
         ObjectPrx servantProxy = adapter.add(servant, new Identity("clientCallbackReceiver", category));
         TerkClientPrx terkClientServantPrx = TerkClientPrxHelper.uncheckedCast(servantProxy);

         // register ===============================================================================================
         Trace.TraceInformation("Login successful!  Now registering with the relay...");
         try
            {
            userSessionPrx.registerCallbacks(terkClientServantPrx, terkClientServantPrx);
            }
         catch (RegistrationException e)
            {
            Trace.TraceError("RegistrationException while trying to register callbacks with the relay.");
            throw e;
            }
         Trace.TraceInformation("Registration successful!");

         return userSessionPrx;
         }
      UserSessionPrx userSessionPrx;

       public ObjectPrx getPeerProxy(string peerUserId, Identity identity){
           if (userSessionPrx != null)
               return userSessionPrx.getPeerProxy(peerUserId, identity);
           else
               return null;
       }

      public QwerkController connectToQwerk(UserSessionPrx userSessionPrx, string qwerkUserId)
         {
         // connect to the Qwerk
         Trace.TraceInformation("Connecting to Qwerk [" + qwerkUserId + "]...");
         try
            {
            this.userSessionPrx = userSessionPrx;
            ObjectPrx objectPrx = userSessionPrx.connectToPeer(qwerkUserId);
            if (objectPrx != null)
               {
               QwerkPrx qwerkPrx = QwerkPrxHelper.checkedCast(objectPrx);
               if (qwerkPrx != null)
                  {
                  Trace.TraceInformation("Connection successful! (" + Util.identityToString(qwerkPrx.ice_getIdentity()) + ")");
                  return new QwerkController(qwerkUserId, qwerkPrx, this);
                  }
               else
                  {
                  Trace.TraceError("   Connection failed!");
                  }
               }
            else
               {
                   Trace.TraceError("   connectToPeer() returned a null peer.  Bummer.");
               }
            }
         catch (PeerException e)
            {
                Trace.TraceError("   Connection failed due to a PeerException: {0}", e.reason);
                throw e;
            }
         return null;
         }

      public void shutdown()
      {
          this.sessionPinger.Dispose();
      }
      private Timer startSessionPinger(UserSessionPrx userSessionPrx)
         {
         // create and activate session pinger
         Trace.TraceInformation("Creating and activating session pinger");
         return new Timer(new TimerCallback(pingSession), // timer callback delegate
                          userSessionPrx, // user session object
                          0, // how long to wait before starting the timer
                          5000); // interval of time between pings (in milliseconds)
         }
      }
   }