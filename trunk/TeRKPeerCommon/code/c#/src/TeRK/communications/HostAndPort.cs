using System;
using System.Collections.Generic;
using System.Collections;
using System.Text;
using System.Text.RegularExpressions;
using System.Reflection;
using System.Threading;

namespace TeRK.communications
{
    public class HostAndPort
    {
        private static string LABEL_PATTERN = "[a-zA-Z0-9]{1}(?:[a-zA-Z0-9_-]{0,61}[a-zA-Z0-9]{1})?";
        private static string HOST_AND_PORT_PATTERN = "\\s*(" + LABEL_PATTERN + "(?:\\." + LABEL_PATTERN + ")*)\\s*(?:\\:\\s*\\d+)?\\s*";

        private string host;
        private string port;

        public static HostAndPort createHostAndPort(string hostAndPort)
        {
            if (hostAndPort == null)
            {
                throw new ArgumentNullException("Host and port string cannot be null!");
            }
            if (!isValid(hostAndPort))
            {
                return null;
            }

            // we're sure it matches now, so just split on the colon (if it's even present).
            string[] hostAndPortArray = hostAndPort.Split(":".ToCharArray());

            // the host is the first element in the array, and the port, if present, will be the second
            string host = hostAndPortArray[0].Trim();
            string port = (hostAndPortArray.Length > 1 ? hostAndPortArray[1].Trim() : null);

            return new HostAndPort(host, port);
        }

        /** Returns <code>true</code> if the given {@link String} is a valid host and port string; <code>false</code> otherwise. */
        public static bool isValid(string hostAndPort)
        {
            if (hostAndPort != null)
            {
                return Regex.IsMatch(hostAndPort, HOST_AND_PORT_PATTERN);
            }

            return false;
        }

        private HostAndPort(string host, string port)
        {
            this.port = port;
            this.host = host;
        }

        public string getHost()
        {
            return host;
        }

        public string getPort()
        {
            return (port == null) ? null : port;
        }

        public string getHostAndPort()
        {
            return host + ((port != null) ? ":" + port : "");
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

            HostAndPort that = (HostAndPort)o;

            if (host != null ? !host.Equals(that.host) : that.host != null)
            {
                return false;
            }
            if (port != null ? !port.Equals(that.port) : that.port != null)
            {
                return false;
            }

            return true;
        }

        public override int GetHashCode()
        {
            int result;
            result = (host != null ? host.GetHashCode() : 0);
            result = 31 * result + (port != null ? port.GetHashCode() : 0);
            return result;
        }

        public override string ToString()
        {
            return getHostAndPort();
        }
    }

}
