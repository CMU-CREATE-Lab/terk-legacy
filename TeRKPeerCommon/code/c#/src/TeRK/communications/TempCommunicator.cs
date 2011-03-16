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
    public class TempCommunicator
    {
        private readonly StringBuilder LOG = new StringBuilder();
        
        private static Communicator communicator;
        
        private void log(string message)
        {
            message = DateTime.Now +" : " + message+"\n";
            LOG.Append(message);            
        }

        private void writeLog(string path)
        {
            TextWriter logger = new StreamWriter(path, false);
            logger.Write(LOG.ToString());
            logger.Close();
        }

        public TempCommunicator(string configFileClasspath)
        {
            log("Constructor called");
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
            string proxy1 = "'::TeRK::TeRKUser':tcp -h 127.0.0.1 -p 10101";
            log("Calling stringToProxy on " + proxy1);
            communicator.stringToProxy(proxy1);
            log("Complete!");
            
            string proxy2 = "'::TeRK::TeRKUser':tcp -h 192.168.0.16 -p 10101";
            log("Calling stringToProxy on " + proxy2);
            communicator.stringToProxy(proxy2);
            log("Complete!");

            log("Constructor terminated");
            writeLog("C:\\tempCommLog.txt");
        }

    }
}

