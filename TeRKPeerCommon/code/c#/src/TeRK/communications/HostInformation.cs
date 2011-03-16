using System;
using System.Collections.Generic;
using System.Collections;
using System.Text;
using System.Text.RegularExpressions;
using System.Reflection;
using System.Threading;

namespace TeRK.communications
{
    public class HostInformation
    {
        private static string NEWLINE_PATTERN = "\n";
        private static string LOCAL_HOST_PATTERN = "local address = (.+):(\\d+)";
        private static string REMOTE_HOST_PATTERN = "remote address = (.+):(\\d+)";

        private string localHost;
        private string localPort;
        private string remoteHost;
        private string remotePort;

        public static HostInformation extractHostInformation(string localAndRemoteHostsAndPorts)
        {
            return (localAndRemoteHostsAndPorts != null) ? new HostInformation(localAndRemoteHostsAndPorts) : null;
        }

        private HostInformation(string localAndRemoteHostsAndPorts)
        {
            string[] info = localAndRemoteHostsAndPorts.Split(NEWLINE_PATTERN.ToCharArray());

            if (info.Length >= 1)
            {
                string localAddressString = info[0];
                string[] localHostAndPort = extractHostAndPort(localAddressString, LOCAL_HOST_PATTERN);
                localHost = localHostAndPort[0];
                localPort = localHostAndPort[1];
            }
            else
            {
                localHost = null;
                localPort = null;
            }

            if (info.Length >= 2)
            {
                string remoteAddressString = info[1];
                string[] remoteHostAndPort = extractHostAndPort(remoteAddressString, REMOTE_HOST_PATTERN);
                remoteHost = remoteHostAndPort[0];
                remotePort = remoteHostAndPort[1];
            }
            else
            {
                remoteHost = null;
                remotePort = null;
            }
        }

        public string getLocalHost()
        {
            return localHost;
        }

        public string getLocalPort()
        {
            return localPort;
        }


        public string getLocalHostAndPort()
        {
            return getColonDelimitedHostAndPort(localHost, localPort);
        }

        public string getRemoteHost()
        {
            return remoteHost;
        }

        public string getRemotePort()
        {
            return remotePort;
        }

        public string getRemoteHostAndPort()
        {
            return getColonDelimitedHostAndPort(remoteHost, remotePort);
        }

        private string getColonDelimitedHostAndPort(string host, string port)
        {
            if (host == null && port == null)
            {
                return null;
            }
            return host + ":" + port;
        }

        private string[] extractHostAndPort(string addressString, string hostPattern)
        {
            MatchCollection matches = Regex.Matches(addressString, hostPattern);

            String host = null;
            String port = null;

            if (matches.Count >= 1)
            {
                if (matches[0].Groups.Count >= 2)
                    host = matches[0].Groups[1].Value;
                if (matches[0].Groups.Count >=3)
                    port = matches[0].Groups[2].Value;
                
            }

            return new string[] { host, port };
        }

        public override bool Equals(object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || GetType() != o.GetType())
            {
                return false;
            }

            HostInformation that = (HostInformation)o;

            if (localHost != null ? !localHost.Equals(that.localHost) : that.localHost != null)
            {
                return false;
            }
            if (localPort != null ? !localPort.Equals(that.localPort) : that.localPort != null)
            {
                return false;
            }
            if (remoteHost != null ? !remoteHost.Equals(that.remoteHost) : that.remoteHost != null)
            {
                return false;
            }
            return !(remotePort != null ? !remotePort.Equals(that.remotePort) : that.remotePort != null);
        }

        public override int GetHashCode()
        {
            int result;
            result = (localHost != null ? localHost.GetHashCode() : 0);
            result = 29 * result + (localPort != null ? localPort.GetHashCode() : 0);
            result = 29 * result + (remoteHost != null ? remoteHost.GetHashCode() : 0);
            result = 29 * result + (remotePort != null ? remotePort.GetHashCode() : 0);
            return result;
        }

        public override string ToString()
        {
            return "HostInformation{" +
                   "localHost='" + localHost + "'" +
                   ", localPort='" + localPort + "'" +
                   ", remoteHost='" + remoteHost + "'" +
                   ", remotePort='" + remotePort + "'" +
                   "}";
        }
    }

}
