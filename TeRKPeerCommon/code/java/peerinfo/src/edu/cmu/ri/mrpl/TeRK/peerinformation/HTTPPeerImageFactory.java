package edu.cmu.ri.mrpl.TeRK.peerinformation;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URL;
import edu.cmu.ri.mrpl.swing.ImageUtils;
import org.apache.log4j.Logger;

/**
 * <code>HTTPPeerImageFactory</code> loads peer images from a web site.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HTTPPeerImageFactory implements PeerImageFactory
   {
   private static final Logger LOG = Logger.getLogger(HTTPPeerImageFactory.class);

   private static final String HTTP_PROTOCOL = "http";
   private static final int DEFAULT_PORT = 80;

   private final String host;
   private final int port;
   private final String pathPrefix;

   public HTTPPeerImageFactory(final String host, final String pathPrefix)
      {
      this(host, DEFAULT_PORT, pathPrefix);
      }

   public HTTPPeerImageFactory(final String host, final int port, final String pathPrefix)
      {
      this.host = host;
      this.port = port;
      this.pathPrefix = pathPrefix;
      }

   public Image createImage(final String peerUserId)
      {
      if (peerUserId != null)
         {
         final String path = pathPrefix + peerUserId;
         try
            {
            final URL imageURL = new URL(HTTP_PROTOCOL, host, port, path);
            return ImageUtils.loadImageFromURL(imageURL);
            }
         catch (MalformedURLException e)
            {
            LOG.error("MalformedURLException while trying to construct URL using host [" + host + "], port [" + port + "], and path [" + path + "]", e);
            }
         }

      return null;
      }
   }
