package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.imageio.ImageIO;
import Ice.CloseConnectionException;
import Ice.Current;
import Ice.Identity;
import Ice.LocalException;
import Ice.ObjectNotExistException;
import Ice.ObjectPrx;
import Ice.Util;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.AbstractCommandController;
import edu.cmu.ri.mrpl.TeRK.AnalogInState;
import edu.cmu.ri.mrpl.TeRK.AudioController;
import edu.cmu.ri.mrpl.TeRK.BatteryState;
import edu.cmu.ri.mrpl.TeRK.ButtonState;
import edu.cmu.ri.mrpl.TeRK.CommandException;
import edu.cmu.ri.mrpl.TeRK.DigitalInState;
import edu.cmu.ri.mrpl.TeRK.DigitalOutController;
import edu.cmu.ri.mrpl.TeRK.Image;
import edu.cmu.ri.mrpl.TeRK.ImageFormat;
import edu.cmu.ri.mrpl.TeRK.LEDController;
import edu.cmu.ri.mrpl.TeRK.MotorController;
import edu.cmu.ri.mrpl.TeRK.MotorState;
import edu.cmu.ri.mrpl.TeRK.QwerkCommand;
import edu.cmu.ri.mrpl.TeRK.QwerkState;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.ServoController;
import edu.cmu.ri.mrpl.TeRK.ServoState;
import edu.cmu.ri.mrpl.TeRK.TerkClientPrx;
import edu.cmu.ri.mrpl.TeRK.TerkClientPrxHelper;
import edu.cmu.ri.mrpl.TeRK.VideoException;
import edu.cmu.ri.mrpl.TeRK._QwerkDisp;
import edu.cmu.ri.mrpl.ice.proxy.IceProxyPinger;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer._UserConnectionEventHandlerOperationsNC;
import edu.cmu.ri.mrpl.util.SimpleStrategy;
import edu.cmu.ri.mrpl.util.thread.ControllableThread;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FakeQwerkServant extends _QwerkDisp implements MessageEventSource, QwerkEventSource
   {
   private static final Logger LOG = Logger.getLogger(FakeQwerkServant.class);

   private static final String CONTEXT_MAP_KEY_IS_DIRECT_CONNECT = "__isDirectConnect";

   private final Map<String, TerkClientPrx> terkClientProxies = Collections.synchronizedMap(new HashMap<String, TerkClientPrx>());
   private final Set<TerkClientPrx> videoSubscribers = Collections.synchronizedSet(new HashSet<TerkClientPrx>());
   private boolean isCameraRunning = false;
   private final VideoStreamer videoStreamer;
   private final MessageEventSourceHelper messageEventSourceHelper = new MessageEventSourceHelper();
   private final _UserConnectionEventHandlerOperationsNC connectionEventHandler;
   private final boolean useFakeVideoFrames;
   private final byte[][] fakeVideoFramesJPEGs = new byte[2][];
   private final ServantPropertyManager properties = new ServantPropertyManager();
   private final Map<CommandType, AbstractCommandController> commandTypeToControllerMap = Collections.synchronizedMap(new HashMap<CommandType, AbstractCommandController>());
   private final Map<String, Identity> commandControllerTypeToProxyIdentityMap = Collections.synchronizedMap(new HashMap<String, Identity>());
   private int digitalInAlternate = 0;

   private final Map<String, IceProxyPinger> directConnectPeerIdentifierToPingerMap = new HashMap<String, IceProxyPinger>();

   FakeQwerkServant(final _UserConnectionEventHandlerOperationsNC connectionEventHandler)
      {
      this(connectionEventHandler, false);
      properties.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_TYPE, "fakeqwerk");
      properties.setReadOnlyProperty(TerkConstants.PropertyKeys.HARDWARE_VERSION, "2");
      }

   FakeQwerkServant(final _UserConnectionEventHandlerOperationsNC connectionEventHandler, final boolean useFakeVideoFrames)
      {
      this.connectionEventHandler = connectionEventHandler;
      this.videoStreamer = new VideoStreamer();
      this.useFakeVideoFrames = useFakeVideoFrames;
      if (useFakeVideoFrames)
         {
         try
            {
            final BufferedImage fakeFrame0 = ImageIO.read(FakeQwerkServant.class.getResourceAsStream("/edu/cmu/ri/mrpl/TeRK/robot/fakeqwerk/fakeVideoFrame1.jpg"));
            final BufferedImage fakeFrame1 = ImageIO.read(FakeQwerkServant.class.getResourceAsStream("/edu/cmu/ri/mrpl/TeRK/robot/fakeqwerk/fakeVideoFrame2.jpg"));

            fakeVideoFramesJPEGs[0] = createJPEG(fakeFrame0);
            fakeVideoFramesJPEGs[1] = createJPEG(fakeFrame1);
            }
         catch (IOException e)
            {
            LOG.error("IOException while reading the fake video frames", e);
            }
         }
      }

   void configureCommandControllerToHandleCommandType(final AbstractCommandController commandController, final Identity commandControllerProxyIdentity, final CommandAndControllerMapper commandAndControllerMapper)
      {
      final CommandControllerType commandControllerType = CommandControllerType.find(commandController.ice_id());
      final CommandType commandType = commandAndControllerMapper.getCommandType(commandControllerType);

      if (commandType != null)
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("FakeQwerkServant.configureCommandControllerToHandleCommandType() is processing identity [" + Util.identityToString(commandControllerProxyIdentity) + "]");
            LOG.trace("FakeQwerkServant.configureCommandControllerToHandleCommandType() is linking commands of type [" + commandType + "] to a command controller of type [" + commandControllerType + "]");
            }
         commandTypeToControllerMap.put(commandType, commandController);
         }

      commandControllerTypeToProxyIdentityMap.put(commandController.ice_id(), commandControllerProxyIdentity);
      }

   public HashMap<String, Identity> getSupportedServices(final Current current)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("FakeQwerkServant.getSupportedServices() --> Map of " + commandControllerTypeToProxyIdentityMap.size() + " element(s)");
         }
      return new HashMap<String, Identity>(commandControllerTypeToProxyIdentityMap);
      }

   public HashMap<String, Identity> getCommandControllerTypeToProxyIdentityMap(final Current current)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("FakeQwerkServant.getCommandControllerTypeToProxyIdentityMap() --> Map of " + commandControllerTypeToProxyIdentityMap.size() + " element(s)");
         }
      return new HashMap<String, Identity>(commandControllerTypeToProxyIdentityMap);
      }

   public String getProperty(final String key, final Current current)
      {
      return properties.getProperty(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return properties.getProperties();
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return properties.getPropertyKeys();
      }

   public void setProperty(final String key, final String value, final Current current) throws ReadOnlyPropertyException
      {
      properties.setProperty(key, value);
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

   public void forcedLogoutNotification(final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.forcedLogoutNotification()" + IceUtil.dumpCurrentToString(current));
         }
      connectionEventHandler.forcedLogoutNotification();
      }

   public void peerConnected(final String peerId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerProxy, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.peerConnected()" + IceUtil.dumpCurrentToString(current));
         }

      final ObjectPrx proxy;
      if (current.ctx.containsKey(CONTEXT_MAP_KEY_IS_DIRECT_CONNECT))
         {
         // create a new proxy upon which I can call callback methods (see the section on setting up bidirectional manually in the Ice manual)
         proxy = current.con.createProxy(peerProxy.ice_getIdentity());

         // create a pinger for this peer, and add it to the pinger map
         final PeerPinger peerPinger = new PeerPinger(peerId, 5, proxy, new PeerPingFailureStrategy(peerId));
         directConnectPeerIdentifierToPingerMap.put(peerId, peerPinger);
         peerPinger.start();
         }
      else
         {
         proxy = peerProxy;
         }

      terkClientProxies.put(peerId, TerkClientPrxHelper.uncheckedCast(proxy));

      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "|" + peerAccessLevel + "|" + Util.identityToString(proxy.ice_getIdentity()) + "] just connected to me.");
         }
      fireMessageEvent("Client " + peerId + " connected with access level " + peerAccessLevel + ".");
      fireQwerkEvent(QwerkEventSource.QwerkCommandHelper.CONNECTED);
      }

   public void peerConnectedNoProxy(final String peerId, final PeerAccessLevel peerAccessLevel, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.peerConnectedNoProxy()" + IceUtil.dumpCurrentToString(current));
         }
      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "|" + peerAccessLevel + "] just connected to me (and I didn't get a proxy).");
         }
      fireMessageEvent("Client " + peerId + " connected with access level " + peerAccessLevel + " (no proxy).");
      fireQwerkEvent(QwerkEventSource.QwerkCommandHelper.CONNECTED);
      }

   public void peerDisconnected(final String peerId, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("FakeQwerkServant.peerDisconnected()" + IceUtil.dumpCurrentToString(current));
         }
      handlePeerDisconnected(peerId);
      }

   private void handlePeerDisconnected(final String peerId)
      {
      // stop and remove the pinger
      final IceProxyPinger pinger = directConnectPeerIdentifierToPingerMap.remove(peerId);
      if (pinger != null)
         {
         pinger.stop();
         }

      // clean up
      final TerkClientPrx proxy = terkClientProxies.remove(peerId);
      videoSubscribers.remove(proxy);
      if (LOG.isInfoEnabled())
         {
         LOG.info("The client [" + peerId + "] just disconnected from me.");
         }

      // fire the event
      fireMessageEvent("Client " + peerId + " disconnected.");
      fireQwerkEvent(QwerkEventSource.QwerkCommandHelper.DISCONNECTED);
      }

   private AnalogInState getAnalogInState()
      {
      final Random r = new Random();
      final int len = 8;
      final short[] result = new short[len];
      for (int i = 0; i < len; i++)
         {
         result[i] = (short)r.nextInt();
         }
      return new AnalogInState(result);
      }

   private BatteryState getBatteryState()
      {
      return new BatteryState((new Random()).nextInt());
      }

   private DigitalInState getDigitalInState()
      {
      final boolean[][] alternating = {{true, false, true, false, true, false, true, false},
                                       {false, true, false, true, false, true, false, true}};
      final boolean[] next = alternating[digitalInAlternate];
      digitalInAlternate++;
      digitalInAlternate %= 2;
      return new DigitalInState(next);
      }

   private MotorState getMotorState()
      {
      Random r = new Random();
      int[] velocities = new int[4];
      int[] positions = new int[4];
      int[] currents = new int[4];
      int[] dutycycles = new int[4];
      boolean[] done = new boolean[4];
      for (int i = 0; i < 4; i++)
         {
         velocities[i] = r.nextInt();
         positions[i] = r.nextInt();
         currents[i] = r.nextInt();
         dutycycles[i] = r.nextInt();
         done[i] = r.nextBoolean();
         }
      return new MotorState(velocities, positions, currents, dutycycles, done);
      }

   private ServoState getServoState()
      {
      Random r = new Random();
      int[] positions = new int[16];
      for (int i = 0; i < 16; i++)
         {
         positions[i] = r.nextInt();
         }
      return new ServoState(positions);
      }

   private ButtonState getButtonState()
      {
      return new ButtonState(new boolean[]{false, false});
      }

   public QwerkState getState(final Current current)
      {
      final AnalogInState in = getAnalogInState();
      final BatteryState bat = getBatteryState();
      final ButtonState button = getButtonState();
      final DigitalInState dig = getDigitalInState();
      final MotorState mstate = getMotorState();
      final ServoState sstate = getServoState();
      return new QwerkState(in, bat, button, dig, mstate, sstate);
      }

   public QwerkState execute(final QwerkCommand qwerkCommand, final Current current)
      {
      final QwerkState qwerkState = getState(current);
      if (qwerkCommand != null)
         {
         // execute the commands
         try
            {
            final AudioController audioController = (AudioController)commandTypeToControllerMap.get(CommandType.AUDIO);
            if (audioController != null)
               {
               audioController.execute(qwerkCommand.audioCmd);
               }

            final DigitalOutController digitalOutController = (DigitalOutController)commandTypeToControllerMap.get(CommandType.DIGITAL_OUT);
            if (digitalOutController != null)
               {
               digitalOutController.execute(qwerkCommand.digitalOutCmd);
               }

            final LEDController ledController = (LEDController)commandTypeToControllerMap.get(CommandType.LED);
            if (ledController != null)
               {
               ledController.execute(qwerkCommand.ledCmd);
               }

            final MotorController motorController = (MotorController)commandTypeToControllerMap.get(CommandType.MOTOR);
            if (motorController != null)
               {
               qwerkState.motor = motorController.execute(qwerkCommand.motorCmd);
               }

            final ServoController servoController = (ServoController)commandTypeToControllerMap.get(CommandType.SERVO);
            if (servoController != null)
               {
               qwerkState.servo = servoController.execute(qwerkCommand.servoCmd);
               }
            }
         catch (CommandException e)
            {
            LOG.error("CommandException while executing the QwerkCommand", e);
            }
         }
      else
         {
         LOG.trace("FakeQwerkServant.exceute() will do nothing since the qwerkCommand is null.");
         }

      return qwerkState;
      }

   public QwerkState emergencyStop(final Current current)
      {
      throw new UnsupportedOperationException("Not yet implemented.");
      }

   public TerkClientPrx getPeerProxy(final Object peerUserId)
      {
      return terkClientProxies.get(peerUserId);
      }

   void addVideoSubscriber(final TerkClientPrx proxy)
      {
      videoSubscribers.add(proxy);
      }

   void removeVideoSubscriber(final TerkClientPrx proxy)
      {
      videoSubscribers.remove(proxy);
      if (videoSubscribers.isEmpty())
         {
         setIsCameraRunning(false);
         }
      }

   boolean isVideoStreamerRunning()
      {
      return videoStreamer.isRunning();
      }

   void startVideoStreamer()
      {
      videoStreamer.start();
      setIsCameraRunning(true);
      }

   void stopVideoStreamer()
      {
      setIsCameraRunning(false);
      videoStreamer.stop();
      }

   Image getVideoFrame() throws VideoException
      {
      if (!isCameraRunning())
         {
         throw new VideoException("Cannot get a video frame since the camera is not running.  You must call startCamera() first.");
         }
      return videoStreamer.getImage();
      }

   boolean isCameraRunning()
      {
      return isCameraRunning;
      }

   void setIsCameraRunning(final boolean isCameraRunning)
      {
      this.isCameraRunning = isCameraRunning;
      if (this.isCameraRunning && !isVideoStreamerRunning())
         {
         videoStreamer.start();
         }
      }

   private final class VideoStreamer extends ControllableThread
      {
      private static final int WIDTH = 320;
      private static final int HEIGHT = 240;
      private static final int FONTSIZE = 14;
      private static final String FONTNAME = "Arial";

      private final Image image = new Image();
      private int frameNumber = 0;
      private final Set<TerkClientPrx> videoSubscribersCopy = Collections.synchronizedSet(new HashSet<TerkClientPrx>());
      private final byte[] imageSynchronizationLock = new byte[0];

      private VideoStreamer()
         {
         super("Qwerk VideoStreamer", true);
         image.width = WIDTH;
         image.height = HEIGHT;
         image.frameNum = frameNumber;
         image.format = ImageFormat.ImageJPEG;
         image.data = null;
         }

      private Image getImage()
         {
         synchronized (imageSynchronizationLock)
            {
            return (Image)image.clone();
            }
         }

      protected int getSleepTime()
         {
         // 100 = 10 Hz
         //  50 = 20 Hz
         //  34 ~ 30 Hz
         return 50;
         }

      protected void performLoopedBehavior()
         {
         if (isCameraRunning || !videoSubscribers.isEmpty())
            {
            try
               {
               // get the latest image
               final byte[] jpeg = createJPEG();
               synchronized (imageSynchronizationLock)
                  {
                  image.frameNum = frameNumber;
                  image.data = jpeg;
                  }
               }
            catch (IOException e)
               {
               LOG.error("IOException while trying to create the JPEG", e);
               }
            }

         if (!videoSubscribers.isEmpty())
            {
            try
               {
               synchronized (videoSubscribers)
                  {
                  if (!videoSubscribers.isEmpty())
                     {
                     videoSubscribersCopy.clear();
                     videoSubscribersCopy.addAll(videoSubscribers);
                     }
                  }

               // publish the image to all the subscribers
               for (final TerkClientPrx subscriberProxy : videoSubscribersCopy)
                  {
                  subscriberProxy.newFrame(image);
                  }
               }
            catch (ObjectNotExistException e)
               {
               LOG.error("ObjectNotExistException while trying to send the JPEG.  Maybe the client disconnected and I haven't received the notification yet?", e);
               }
            catch (CloseConnectionException e)
               {
               LOG.error("CloseConnectionException while trying to send the JPEG.  Maybe the client disconnected and I haven't received the notification yet?", e);
               }
            catch (LocalException e)
               {
               LOG.error("LocalException while trying to send the JPEG.  Maybe the client disconnected and I haven't received the notification yet?", e);
               }
            }
         }

      // I got this JPEG creation code from http://www.developer.com/java/other/article.php/606541
      private byte[] createJPEG() throws IOException
         {
         if (useFakeVideoFrames)
            {
            return fakeVideoFramesJPEGs[(++frameNumber) % 2];
            }
         else
            {
            final BufferedImage img = createImage(Integer.toString(++frameNumber));
            return FakeQwerkServant.createJPEG(img);
            }
         }

      private BufferedImage createImage(final String str)
         {
         final BufferedImage img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
         final Graphics2D g2 = img.createGraphics();
         g2.setBackground(Color.orange);
         g2.clearRect(0, 0, WIDTH, HEIGHT);
         g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         g2.setColor(Color.black);
         final Font font = new Font(FONTNAME, Font.PLAIN, FONTSIZE);
         g2.setFont(font);
         final TextLayout tl = new TextLayout(str, font, g2.getFontRenderContext());
         final Rectangle2D r = tl.getBounds();
         // center the text
         tl.draw(g2, (float)((WIDTH - r.getWidth()) / 2), (float)(((HEIGHT - r.getHeight()) / 2) + r.getHeight()));
         g2.dispose();
         return img;
         }
      }

   private static byte[] createJPEG(final BufferedImage img) throws IOException
      {
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
      final JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(img);
      param.setQuality(1.0f, true);
      encoder.encode(img, param);
      final byte[] bytes = out.toByteArray();
      out.close();

      return bytes;
      }

   private final class PeerPinger extends IceProxyPinger
      {
      PeerPinger(final String peerIdentifier, final int sleepTimeInSeconds, final ObjectPrx objectProxy, final SimpleStrategy simpleStrategy)
         {
         super("IceProxyPinger for peer " + peerIdentifier,
               sleepTimeInSeconds,
               objectProxy,
               simpleStrategy);
         }
      }

   private final class PeerPingFailureStrategy implements SimpleStrategy
      {
      private final String peerIdentifier;

      private PeerPingFailureStrategy(final String peerIdentifier)
         {
         this.peerIdentifier = peerIdentifier;
         }

      public void execute()
         {
         LOG.debug("Ping failure detected for peer [" + peerIdentifier + "]");
         handlePeerDisconnected(peerIdentifier);
         }
      }
   }
