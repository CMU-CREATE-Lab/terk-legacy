using System;
using System.Collections.Generic;
using System.Text;
using Ice;

namespace TeRK.communications
{
    public interface TerkCommunicator
    {
        ObjectPrx getPeerProxy(string peerUserId, Identity identity);
    }
}
