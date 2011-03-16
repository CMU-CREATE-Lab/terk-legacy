package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.Image;
import edu.cmu.ri.mrpl.TeRK.TerkClientPrx;
import edu.cmu.ri.mrpl.TeRK.VideoException;
import edu.cmu.ri.mrpl.TeRK._VideoStreamerServerDisp;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class VideoStreamControllerServant extends _VideoStreamerServerDisp implements MessageEventSource
   {
   private static final Logger LOG = Logger.getLogger(VideoStreamControllerServant.class);

   private static final String CONTEXT_MAP_KEY_PEER_USERID = "__peerUserId";

   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final Map<String, String> properties = new HashMap<String, String>();
   private final FakeQwerkServant fakeQwerkServant;

   VideoStreamControllerServant(final FakeQwerkServant fakeQwerkServant)
      {
      this.fakeQwerkServant = fakeQwerkServant;
      }

   public void addMessageEventListener(final MessageEventListener messageEventListener)
      {
      messageEventSourceHelper.addMessageEventListener(messageEventListener);
      }

   public void removeMessageEventListener(final MessageEventListener messageEventListener)
      {
      messageEventSourceHelper.removeMessageEventListener(messageEventListener);
      }

   private void fireMessageEvent(final String message)
      {
      messageEventSourceHelper.fireMessageEvent(message);
      }

   private final QwerkEventSourceHelper qwerkEventSourceHelper = new QwerkEventSourceHelper();

   public void addQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.addQwerkEventListener(qwerkEventListener);
      }

   public void removeQwerkEventListener(final QwerkEventListener qwerkEventListener)
      {
      qwerkEventSourceHelper.removeQwerkEventListener(qwerkEventListener);
      }

   private void fireQwerkEvent(final Object command)
      {
      qwerkEventSourceHelper.fireQwerkEvent(command);
      }

   public String getProperty(final String key, final Current current)
      {
      return properties.get(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return Collections.unmodifiableMap(properties);
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return new ArrayList<String>(properties.keySet());
      }

   public void setProperty(final String key, final String value, final Current current)
      {
      properties.put(key, value);
      }

   public void startVideoStream(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.startVideoStreamer()" + IceUtil.dumpCurrentToString(current));
         }
      final Object peerUserId = current.ctx.get(CONTEXT_MAP_KEY_PEER_USERID);
      final TerkClientPrx proxy = fakeQwerkServant.getPeerProxy(peerUserId);
      if (proxy != null)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Adding user [" + peerUserId + "] to Set of video subscribers.");
            }
         fireMessageEvent("Client " + peerUserId + " subscribed to video.");
         fakeQwerkServant.addVideoSubscriber(proxy);
         if (!fakeQwerkServant.isVideoStreamerRunning())
            {
            fakeQwerkServant.startVideoStreamer();
            }
         }
      else
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("Not adding [" + peerUserId + "] to Set of video subscribers since I don't have a proxy for that peer.");
            }
         }
      }

   public void stopVideoStream(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.stopVideoStream()" + IceUtil.dumpCurrentToString(current));
         }
      final Object peerUserId = current.ctx.get(CONTEXT_MAP_KEY_PEER_USERID);
      final TerkClientPrx proxy = fakeQwerkServant.getPeerProxy(peerUserId);
      if (LOG.isDebugEnabled())
         {
         LOG.debug("Removing user [" + peerUserId + "] from Set of video subscribers.");
         }
      fireMessageEvent("Client " + peerUserId + " unsubscribed from video.");
      fakeQwerkServant.removeVideoSubscriber(proxy);
      if (LOG.isDebugEnabled())
         {
         LOG.debug("User [" + peerUserId + "] successfully removed from Set of video subscribers.");
         }
      }

   public int startCamera(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.startCamera()" + IceUtil.dumpCurrentToString(current));
         }

      if (fakeQwerkServant.isCameraRunning())
         {
         LOG.info("Not starting the camera since it's already running");
         }
      else
         {
         LOG.info("Starting the camera");
         fakeQwerkServant.setIsCameraRunning(true);
         }
      return 0;
      }

   public int stopCamera(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.stopCamera()" + IceUtil.dumpCurrentToString(current));
         }

      fakeQwerkServant.setIsCameraRunning(false);

      return 0;
      }

   public Image getFrame(final int frameNumber, final Current current) throws VideoException
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.getFrame()" + IceUtil.dumpCurrentToString(current));
         }
      return fakeQwerkServant.getVideoFrame();
      }
   }
