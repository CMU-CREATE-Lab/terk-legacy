using System;
using System.Collections.Generic;
using System.Collections;
using System.Diagnostics;
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
    public class QwerkClientServantFactory
    {
        public QwerkClientServantFactory()
        {
        }

        public Servants createServants(DirectConnectCommunicator communicator)
        {
            Trace.TraceError("QwerkClientServantFactory.createServants()");

            TerkUserServant mainServant = new TerkUserServant(communicator.getCommunicator());

            ObjectAdapter adapter = communicator.addObjectAdapter(communicator.getObjectAdapterName());

            ObjectPrx mainServantProxy = adapter.add(mainServant, new Identity(DirectConnectCommunicator.MAIN_SERVANT_PROXY_IDENTITY_NAME, ""));

            TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

            return new Servants(mainServantPrx, mainServantPrx);
        }
    }
}
