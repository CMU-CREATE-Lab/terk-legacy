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
using Exception = System.Exception;
using RouterPrx = Glacier2.RouterPrx;
using RouterPrxHelper = Glacier2.RouterPrxHelper;

namespace TeRK.communications
{
    public class Servants
    {
        private TerkUserPrx mainServantProxy;
        private ConnectionEventHandlerPrx connectionEventHandlerProxy;
        private List<ObjectPrx> secondaryServantProxies = new List<ObjectPrx>();

        public Servants(TerkUserPrx mainServantProxy,
                        ConnectionEventHandlerPrx connectionEventHandlerProxy) :
            this(mainServantProxy, connectionEventHandlerProxy, null)
        {
        }

        public Servants(TerkUserPrx mainServantProxy,
                        ConnectionEventHandlerPrx connectionEventHandlerProxy,
                        List<ObjectPrx> secondaryServantProxies)
        {
            this.mainServantProxy = mainServantProxy;
            this.connectionEventHandlerProxy = connectionEventHandlerProxy;
            if ((secondaryServantProxies != null) && (secondaryServantProxies.Count > 0))
            {
                this.secondaryServantProxies.AddRange(secondaryServantProxies);
            }
        }

        public TerkUserPrx getMainServantProxy()
        {
            return mainServantProxy;
        }

        public ConnectionEventHandlerPrx getConnectionEventHandlerProxy()
        {
            return connectionEventHandlerProxy;
        }
    }

}
