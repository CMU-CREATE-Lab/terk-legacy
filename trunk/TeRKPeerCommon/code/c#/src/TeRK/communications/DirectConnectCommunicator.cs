using System;
using System.IO;
using System.Collections.Generic;
using System.Text;
using System.Diagnostics;
using System.Threading;
using Ice;
using peer;
using Exception = System.Exception;

namespace TeRK.communications
{
    public class DirectConnectCommunicator : TerkCommunicator
    {
        private readonly StringBuilder LOG = new StringBuilder();
        private static string PROTOCOL_PROPERTY_KEY = "TeRK.direct-connect.protocol";
        private static string PORT_PROPERTY_KEY = "TeRK.direct-connect.port";
        private static string ENDPOINT_PROPERTY_KEY = "Endpoints";
        private static string DEFAULT_PROTOCOL = "tcp";
        private static string DEFAULT_PORT = "10101";
        private static string CONTEXT_MAP_KEY_PEER_IDENTITY = "__peerProxyIdentity";
        private static string CONTEXT_MAP_KEY_PEER_USERID = "__peerUserId";
        private static string CONTEXT_MAP_KEY_IS_DIRECT_CONNECT = "__isDirectConnect";
        public static string MAIN_SERVANT_PROXY_IDENTITY_NAME = "::TeRK::TerkUser";

        private Dictionary<HostAndPort, TerkUserPrx> peerHostAndPortToProxyMap = new Dictionary<HostAndPort, TerkUserPrx>();
        private Dictionary<HostAndPort, Timer> peerHostAndPortToPingerMap = new Dictionary<HostAndPort, Timer>();

        private string uuid = Util.generateUUID();
        private Dictionary<string, ObjectAdapter> objectAdapterMap = new Dictionary<string, ObjectAdapter>();

        private string applicationName;
        private string objectAdapterName;
        private static Communicator communicator;
        private Timer pinger;
        private Servants servants;

        private static void pingSession(object state)
        {
            PeerPingerState pingerState = (PeerPingerState)state;
            try
            {
                pingerState.getProxy().ice_ping();
            }
            catch (Exception e)
            {
                pingerState.pingFailed();
                pingerState.log("Exception while pinging: " + e);
            }
        }

        private void log(string message)
        {
           /** 
            message = DateTime.Now + ": "+message;
            TextWriter logger = new StreamWriter("C:\\log.txt", true);
            logger.WriteLine(message);
            logger.Close();
            **/
        }

        private Timer startPinger(PeerPingerState state)
        {
            // create and activate session pinger
            log("Creating and activating session pinger");
            return new Timer(new TimerCallback(pingSession), // timer callback delegate
                             state, // user session object
                             0, // how long to wait before starting the timer
                             2000); // interval of time between pings (in milliseconds)
        }


        private QwerkClientServantFactory servantFactory;
        /**
         * Creates a DirectConnectCommunicator.  The caller is responsible for calling {@link #waitForShutdown()}.
         */
        public DirectConnectCommunicator(string applicationName, string configFileClasspath, string objectAdapterName, QwerkClientServantFactory servantFactory)
        {
            log("constructor called");
            this.applicationName = applicationName;
            this.servantFactory = servantFactory;
            this.objectAdapterName = objectAdapterName;
            log("About to do properties loading try block...");
            try
            {
                InitializationData initData = new InitializationData();
                initData.properties = Util.createProperties();
                initData.properties.load(configFileClasspath);                
                communicator = Util.initialize(initData);
                log("Properties Loaded!");
            }
            catch (Exception e)
            {
                log(e.ToString());
                throw e;
            }
            log("constructor terminated");
        }

        internal string getObjectAdapterName()
        {
            return this.objectAdapterName;
        }

        internal ObjectAdapter addObjectAdapter(string objectAdapterName)
        {
            ObjectAdapter objectAdapter;
            if (!objectAdapterMap.TryGetValue(objectAdapterName, out objectAdapter))
            {
                objectAdapter = communicator.createObjectAdapter(objectAdapterName);
                objectAdapter.activate();
                objectAdapterMap.Add(objectAdapterName, objectAdapter);
            }
            return objectAdapter;
        }

        internal Communicator getCommunicator()
        {
            return communicator;
        }

        /**
        * Creates a direct connection to the given peer identifier, which, in the case of direct connect, is simply the
        * peer's hostname or IP address.
        */
        public TerkUserPrx connectToPeer(string peerIdentifier)
        {
            log("connectToPeer called");
            peerIdentifier = peerIdentifier.Replace("localhost", "127.0.0.1");

            HostAndPort hostAndPort = createHostAndPort(peerIdentifier);

            if (peerHostAndPortToProxyMap.ContainsKey(hostAndPort))
            {
                throw new DuplicateConnectionException("Already connected to peer [" + hostAndPort + "]");
            }

            log("Not connected... creating new connection");
            TerkUserPrx peerProxy;
            ObjectPrx objectPrx;

            try
            {
                objectPrx = getPeerProxy(hostAndPort, new Identity(MAIN_SERVANT_PROXY_IDENTITY_NAME, ""));
                if (objectPrx != null)
                {
                    peerProxy = TerkUserPrxHelper.uncheckedCast(objectPrx);

                    peerHostAndPortToProxyMap.Add(hostAndPort, peerProxy);

                    //start pinger
                    pinger = startPinger(new PeerPingerState(hostAndPort, objectPrx, this));
                    peerHostAndPortToPingerMap.Add(hostAndPort, pinger);
                }

                else
                {
                    throw new PeerConnectionFailedException("getPeerProxy() returned null peer proxy for peer [" + hostAndPort + "]");
                }
            }
            catch (Exception e)
            {
                throw new PeerConnectionFailedException("Failed to create connection to peer [" + hostAndPort + "]: " + e);
            }
            HostInformation hostInformation = HostInformation.extractHostInformation(objectPrx.ice_getConnection().ToString());
            string endpointKey = objectAdapterName + "." + ENDPOINT_PROPERTY_KEY;            
            string endpointValue = communicator.getProperties().getProperty(PROTOCOL_PROPERTY_KEY) + " -h " + hostInformation.getLocalHost() + " -p " + hostInformation.getLocalPort();

            communicator.getProperties().setProperty(endpointKey, endpointValue);

            peerProxy.ice_getConnection().setAdapter(addObjectAdapter(getObjectAdapterName()));

            servants = this.servantFactory.createServants(this);

            // notify the peer of the connection
            notifyPeerOfConnection(peerProxy);

            return peerProxy;
        }

        private HostAndPort createHostAndPort(string peerHostAndPortStr)
        {
            if (peerHostAndPortStr != null)
            {
                HostAndPort hostAndPort = HostAndPort.createHostAndPort(peerHostAndPortStr.ToLower());
                if (hostAndPort == null)
                {
                    throw new ArgumentException("Cannot extract host and port from [" + peerHostAndPortStr + "] since the format is invalid");
                }
                return hostAndPort;
            }
            throw new ArgumentNullException("Peer host and port cannot be null!");
        }

        private void notifyPeerOfConnection(TerkUserPrx peerTerkUserPrx)
        {
            if (servants == null)
            {
                peerTerkUserPrx.peerConnectedNoProxy(getMyUserId(peerTerkUserPrx),
                                                     PeerAccessLevel.AccessLevelOwner);
            }
            else
            {
                peerTerkUserPrx.peerConnected(getMyUserId(peerTerkUserPrx),
                                              PeerAccessLevel.AccessLevelOwner,
                                              servants.getMainServantProxy());
            }
        }

        
        public void shutdown()
        {
            log("shutdown called");
            this.prepareForShutdown();
            communicator.destroy();
        }

        public List<PeerIdentifier> getConnectedPeers()
        {
            List<PeerIdentifier> peers = new List<PeerIdentifier>();

            if (!(peerHostAndPortToProxyMap.Count > 0))
            {
                foreach (HostAndPort peerHostAndPort in peerHostAndPortToProxyMap.Keys)
                {
                    peers.Add(new PeerIdentifier(peerHostAndPort.getHostAndPort(), "", ""));
                }
            }

            return peers;
        }

        public void disconnectFromPeer(string peerIdentifier)
        {
            disconnectFromPeer(createHostAndPort(peerIdentifier), true);
        }

        private void disconnectFromPeer(HostAndPort peerHostAndPort, bool willNotifyPeer)
        {
            if (peerHostAndPortToProxyMap.ContainsKey(peerHostAndPort))
            {
                if (willNotifyPeer)
                {
                    notifyPeerOfDisconnection(peerHostAndPort);
                }
                peerHostAndPortToProxyMap.Remove(peerHostAndPort);
                stopAndRemovePinger(peerHostAndPort);
            }
        }

        private void stopAndRemovePinger(HostAndPort peerHostAndPort)
        {
            Timer pinger;
            if (peerHostAndPortToPingerMap.TryGetValue(peerHostAndPort, out pinger))
            {
                pinger.Dispose();
                peerHostAndPortToPingerMap.Remove(peerHostAndPort);
            }
        }

        public void disconnectFromPeers()
        {
            if (peerHostAndPortToProxyMap.Count > 0)
            {
                foreach (HostAndPort peerHostAndPort in peerHostAndPortToProxyMap.Keys)
                {
                    notifyPeerOfDisconnection(peerHostAndPort);
                    stopAndRemovePinger(peerHostAndPort);
                }
                peerHostAndPortToProxyMap.Clear();
            }
        }

        protected void prepareForShutdown()
        {
            disconnectFromPeers();
        }

        private void notifyPeerOfDisconnection(HostAndPort peerHostAndPort)
        {
            TerkUserPrx terkUserPrx;
            peerHostAndPortToProxyMap.TryGetValue(peerHostAndPort, out terkUserPrx);

            if (terkUserPrx != null)
            {
                try
                {
                    terkUserPrx.peerDisconnected(getMyUserId(terkUserPrx));
                }
                catch (Exception e)
                {
                    log("Exception while trying to notify peer [" + peerHostAndPort + "] of the disconnection");
                }
            }
        }

        private string getMyUserId(ObjectPrx objectPrx)
        {
            HostInformation hostInformation = HostInformation.extractHostInformation(objectPrx.ice_getConnection().ToString());            
            string prefix = hostInformation != null ? hostInformation.getLocalHostAndPort() : "direct_connect_user";
            return prefix + "|" + uuid;
        }

        public bool isConnectedToPeer()
        {
            return peerHostAndPortToProxyMap.Count > 0;
        }

        public ObjectPrx getPeerProxy(string peerIdentifier, Identity proxyIdentity)
        {
            return getPeerProxy(createHostAndPort(peerIdentifier), proxyIdentity);
        }

        private ObjectPrx getPeerProxy(HostAndPort peerHostAndPort, Identity proxyIdentity)
        {
            log("getPeerProxy called");
            string protocol = communicator.getProperties().getProperty(PROTOCOL_PROPERTY_KEY);

            if (protocol == null || protocol.Length == 0)
            {
                protocol = DEFAULT_PROTOCOL;
            }
            string port;
            if (peerHostAndPort.getPort() != null)
            {
                port = peerHostAndPort.getPort();
            }
            else
            {
                port = communicator.getProperties().getProperty(PORT_PROPERTY_KEY);
            }
            if (port == null || port.Length == 0)
            {
                port = DEFAULT_PORT;
            }

            StringBuilder proxyString = new StringBuilder("'");
            proxyString.Append(Util.identityToString(proxyIdentity));
            proxyString.Append("':");
            proxyString.Append(protocol);
            proxyString.Append(" -h ");
            proxyString.Append(peerHostAndPort.getHost());
            proxyString.Append(" -p ");
            proxyString.Append(port);

            log("Calling stringToProxy on: "+proxyString);
            ObjectPrx objectPrx = communicator.stringToProxy(proxyString.ToString());
            log("stringToProxy returned");

            // ensure that our custom context entries are passed along with every call on the proxy
            Context context = new Context();
            context.Add(CONTEXT_MAP_KEY_PEER_IDENTITY, Util.identityToString(objectPrx.ice_getIdentity()));
            context.Add(CONTEXT_MAP_KEY_PEER_USERID, getMyUserId(objectPrx));
            context.Add(CONTEXT_MAP_KEY_IS_DIRECT_CONNECT, "true");

            return objectPrx.ice_context(context);
        }

        public Dictionary<Identity, ObjectPrx> getPeerProxies(string peerIdentifier, List<Identity> proxyIdentities)
        {
            return getPeerProxies(createHostAndPort(peerIdentifier), proxyIdentities);
        }

        private Dictionary<Identity, ObjectPrx> getPeerProxies(HostAndPort peerHostAndPort, List<Identity> proxyIdentities)
        {
            Dictionary<Identity, ObjectPrx> proxyMap = new Dictionary<Identity, ObjectPrx>();
            if ((proxyIdentities != null) && (proxyIdentities.Count > 0))
            {
                foreach (Identity identity in proxyIdentities)
                {
                    if (identity != null)
                    {
                        ObjectPrx proxy = getPeerProxy(peerHostAndPort, identity);

                        if (proxy != null)
                        {
                            proxyMap.Add(identity, proxy);
                        }
                        else
                        {
                            log("   Ignoring null proxy returned for identity [" + Util.identityToString(identity) + "]");
                        }
                    }
                    else
                    {
                        log("ignoring null identity");
                    }
                }
            }

            return proxyMap;
        }

        public class PeerPingerState
        {
            private HostAndPort peerHostAndPort;
            private ObjectPrx proxy;
            private DirectConnectCommunicator comm;

            public PeerPingerState(HostAndPort peerHostAndPort, ObjectPrx proxy, DirectConnectCommunicator comm)
            {
                this.peerHostAndPort = peerHostAndPort;
                this.proxy = proxy;
                this.comm = comm;
            }

            public ObjectPrx getProxy()
            {
                return this.proxy;
            }

            public void pingFailed()
            {
                comm.disconnectFromPeer(peerHostAndPort, false);
            }

            public void log(string message)
            {
                comm.log(message);
            }
        }
       
    }
}

