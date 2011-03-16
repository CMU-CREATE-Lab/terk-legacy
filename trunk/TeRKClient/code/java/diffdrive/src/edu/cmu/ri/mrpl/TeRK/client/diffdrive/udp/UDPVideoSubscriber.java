package edu.cmu.ri.mrpl.TeRK.client.diffdrive.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamEventListener;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamEventPublisher;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamSubscriber;
import org.apache.log4j.Logger;

final class UDPVideoSubscriber implements VideoStreamSubscriber
   {
   public UDPVideoSubscriber(UDPHelper helper) throws SocketException
      {
      this(helper, new VideoStreamEventListener()
      {
      public void handleFrame(final byte[] data)
         {
         }
      });
      }

   public UDPVideoSubscriber(UDPHelper helper, VideoStreamEventListener listener) throws SocketException
      {
      if ((helper == null) || (listener == null))
         {
         throw new IllegalArgumentException();
         }
      _helper = helper;
      _LOG.debug("Attempt: create socket");
      _socket = new DatagramSocket();
      _LOG.debug("Success: create socket");

      _LOG.debug("Attempt: assign timeout");
      _socket.setSoTimeout(7000);
      _LOG.debug("Success: assign timeout");

      _LOG.debug("Attempt: assign buffer size");
      _socket.setReceiveBufferSize(UDPVideoConstants.PACKETBUFFER_SIZE);
      _socket.setSendBufferSize(UDPVideoConstants.COMMANDPACKET_SIZE);
      _LOG.debug("Success: assign buffer size");

      _imageprocessing.setVideoStreamEventListener(listener);
      }

   public void setVideoStreamEventPublisher(final VideoStreamEventPublisher listener)
      {
      if (listener == null)
         {
         throw new IllegalArgumentException();
         }
      _imageprocessing.setVideoStreamEventListener(new VideoStreamEventListener()
      {
      public void handleFrame(final byte[] framedata)
         {
         listener.publishFrame(framedata);
         }
      });
      }

   public void setVideoStreamEventListener(VideoStreamEventListener listener)
      {
      if (listener == null)
         {
         throw new IllegalArgumentException();
         }
      _imageprocessing.setVideoStreamEventListener(listener);
      }

   public void startVideoStream()
      {
      if (_state != STATE.TERMINATED)
         {
         throw new IllegalStateException();
         }
      _LOG.info("Starting video stream");
      _state = STATE.RUNNING;
      _attemptconnect.start();//will manage sending of startMessage
      _detectdisconnect.start();
      _imagereceiving.start();
      _imageprocessing.start();
      }

   public void pauseVideoStream()
      {
      if (_state != STATE.RUNNING)
         {
         throw new IllegalStateException();
         }
      _LOG.info("Pausing video stream");
      _state = STATE.PAUSED;
      sendCommand(stopMessage());
      }

   public void resumeVideoStream()
      {
      if (_state != STATE.PAUSED)
         {
         throw new IllegalStateException();
         }
      _LOG.info("Resuming video stream");
      _state = STATE.RUNNING;
      sendCommand(startMessage());
      }

   public void stopVideoStream()
      {
      if (_state == STATE.TERMINATED)
         {//simply ignore
         _LOG.info("Video stream already stopped.");
         return;
         }

      _LOG.info("Stopping video stream");
      _state = STATE.TERMINATED;
      _attemptconnect.interrupt();//stop trying to reconnect
      _detectdisconnect.interrupt();//stop detecting disconnection
      _imagereceiving.interrupt();
      _imageprocessing.interrupt();
      sendCommand(stopMessage());
      synchronized (_socket)
         {
         _socket.disconnect();
         }

      _attemptconnect = new AttemptConnectionLoop();
      _detectdisconnect = new DetectDisconnectLoop();
      _imagereceiving = new ImageReceivingLoop();
      _imageprocessing = new ImageProcessingLoop(_imageprocessing.getVideoStreamEventListener());
      }

   private void sendCommand(final DatagramPacket packet)
      {
      Thread process = new Thread("UDP-Command-Dispatch")
      {
      //assists in debugging

      public void run()
         {
         int retry = 5;
         do
            {
            try
               {
               int waits = 5;
               synchronized (_socket)
                  {
                  _LOG.debug("Attempt: sending command");
                  while (!_socket.isConnected() && (waits != 0))
                     {
                     _socket.wait(500);
                     waits--;
                     }
                  if (!_socket.isConnected())
                     {
                     _LOG.debug("Failure: socket not connected");
                     return;
                     }
                  _socket.send(packet);
                  retry = 0;
                  _LOG.debug("Success: sending command");
                  }
               }
            catch (InterruptedException died)
               {
               return;
               }
            catch (Exception other)
               {
               _LOG.debug("Failure: attempt #" + (5 - retry));
               retry--;
               }
            }
         while (retry > 0);
         }
      };
      process.start();
      }

   private static class UDPCommand
      {
      public UDPCommand(byte[] user, byte[] key, int command)
         {
         if ((user.length != UDPVideoConstants.AUTHENTICATE_SIZE) || (key.length != UDPVideoConstants.AUTHENTICATE_SIZE))
            {
            throw new IllegalArgumentException("User ID and pass must be " + UDPVideoConstants.AUTHENTICATE_SIZE + "-bytes!");
            }
         System.arraycopy(user, 0, _usr, 0, UDPVideoConstants.AUTHENTICATE_SIZE);
         System.arraycopy(key, 0, _key, 0, UDPVideoConstants.AUTHENTICATE_SIZE);
         _cmd = command;
         }

      public byte[] getBytes()
         {
         byte[] result = new byte[UDPVideoConstants.COMMANDPACKET_SIZE];
         System.arraycopy(_usr, 0, result, 0, UDPVideoConstants.AUTHENTICATE_SIZE);
         System.arraycopy(_key, 0, result, UDPVideoConstants.AUTHENTICATE_SIZE, UDPVideoConstants.AUTHENTICATE_SIZE);

         int offset = 2 * UDPVideoConstants.AUTHENTICATE_SIZE;
         for (int i = 0; i < 4; i++)
            {
            int shift = (3 - i) * 8;
            result[offset + i] = (byte)((_cmd & (0xFF << shift)) >>> shift);
            }
         return result;
         }

      public static final int NOP_COMMAND = 0;
      public static final int START_COMMAND = 1;
      public static final int STOP_COMMAND = 2;

      private byte[] _usr = new byte[UDPVideoConstants.AUTHENTICATE_SIZE];
      private byte[] _key = new byte[UDPVideoConstants.AUTHENTICATE_SIZE];
      private int _cmd;
      }

   private static class UDPImage
      {
      public UDPImage(DatagramPacket packet)
         {
         _header = new PacketHeader(packet);
         int len = _header.length();
         _data = new byte[len];
         System.arraycopy(packet.getData(), UDPVideoConstants.PACKETHEADER_SIZE, _data, 0, len);
         }

      public int getHeight()
         {
         return _header.getHeight();
         }

      public int getWidth()
         {
         return _header.getWidth();
         }

      public int getFrameCount()
         {
         return _header.getFrameCount();
         }

      public int getImageFormat()
         {
         return _header.getImageFormat();
         }

      public byte[] getImage()
         {
         return _data;
         }

      private class PacketHeader
         {
         public PacketHeader(DatagramPacket packet)
            {
            byte[] flatdata = packet.getData();
            byte[] msbint = new byte[4];
            for (int i = 0; i < 5; i++)
               {
               System.arraycopy(flatdata, i * 4, msbint, 0, 4);
               _data[i] = readMSB(msbint);
               }
            }

         public int getHeight()
            {
            return _data[0];
            }

         public int getWidth()
            {
            return _data[1];
            }

         public int getFrameCount()
            {
            return _data[2];
            }

         public int getImageFormat()
            {
            return _data[3];
            }

         public int length()
            {
            return _data[4];
            }

         private int readMSB(byte[] msb)
            {
            int result = 0;
            for (int i = 0; i < 4; i++)
               {
               result |= ((int)msb[i]) << ((3 - i) * 8);
               }
            return result;
            }

         private int[] _data = new int[5];
         }

      private PacketHeader _header;
      private byte[] _data;
      }

   private class DetectDisconnectLoop extends Thread
      {
      public DetectDisconnectLoop()
         {
         super("Detect-Disconnect-Loop");//assists in debugging
         }

      public void run()
         {
         Logger log = Logger.getLogger(this.getClass());
         log.debug("Initiating disconnect-detection service");

         while (!isInterrupted())
            {
            int wait = 900;
            //detect we are disconnected
            synchronized (_socket)
               {
               if (!_socket.isConnected())
                  {
                  log.info("Detecting a disconnected socket");
                  _socket.notifyAll();
                  wait = 1000;
                  }
               }

            //sleep for a while
            try
               {
               Thread.sleep(wait);
               }
            catch (InterruptedException terminate)
               {
               return;
               }
            }
         }
      }

   private class AttemptConnectionLoop extends Thread
      {
      public AttemptConnectionLoop()
         {
         super("Attempt-Connection-Loop");//assists in debugging
         }

      public void run()
         {
         Logger log = Logger.getLogger(this.getClass());
         log.debug("Initiating automatic-reconnect service");

         while (!isInterrupted())
            {
            //ensure that we are not already connected
            try
               {
               synchronized (_socket)
                  {
                  while (_socket.isConnected())
                     {
                     _socket.wait();
                     }
                  }
               }
            catch (InterruptedException ie)
               {
               return;
               }

            //quick interruption check #1
            if (isInterrupted())
               {
               return;
               }

            //acquire the list of IP addresses if necessary
            if ((_addresses == null) || (_position >= _addresses.length))
               {
               try
                  {
                  Thread.sleep(500);
                  }
               catch (InterruptedException done)
                  {
                  return;
                  }
               _addresses = _helper.getIPAddresses();
               _position = 0;
               }

            //quick interruption check #2
            if (isInterrupted())
               {
               return;
               }

            //connect to the next ip address on the list
            try
               {
               synchronized (_socket)
                  {
                  InetAddress next = _addresses[_position];
                  log.info("Attempt: connect to " + next.toString());
                  _socket.connect(next, UDPVideoConstants.PORT_NUMBER);
                  if (_socket.isConnected())
                     {
                     log.info("Success: connected to host " + next.toString() + " on port " + UDPVideoConstants.PORT_NUMBER);
                     if (_state == STATE.RUNNING)
                        {
                        sendCommand(startMessage());
                        }
                     _socket.notifyAll();
                     _addresses = null;
                     }
                  }
               }
            catch (Exception error)
               {
               //connection failure!
               log.info("Failure: not connected");
               _position++;
               }

            //quick interruption check #3
            if (isInterrupted())
               {
               return;
               }

            //take a break from trying to connect
            try
               {
               Thread.sleep(30);
               }
            catch (InterruptedException ie)
               {
               return;
               }
            }
         }

      private InetAddress[] _addresses = null;
      private int _position = 0;
      }

   private class ImageProcessingLoop extends Thread
      {
      public ImageProcessingLoop()
         {
         super("Image-Processing-Loop");
         _listener = null;
         }

      public ImageProcessingLoop(VideoStreamEventListener listener)
         {
         _listener = listener;
         }

      public void run()
         {
         while (!isInterrupted())
            {
            UDPImage next = null;
            try
               {
               next = _images.take();
               }
            catch (InterruptedException interrupted)
               {
               break;
               }
            _LOG.debug("Processing image #" + next.getFrameCount());
            _listener.handleFrame(next.getImage());
            }
         _images.clear();//remove unprocessed images
         }

      public void setVideoStreamEventListener(VideoStreamEventListener listener)
         {
         _listener = listener;
         }

      public VideoStreamEventListener getVideoStreamEventListener()
         {
         return _listener;
         }

      private VideoStreamEventListener _listener = null;
      }

   private class ImageReceivingLoop extends Thread
      {
      public ImageReceivingLoop()
         {
         super("Image-Receiving-Loop");//assists in debugging
         }

      public void run()
         {
         while (!isInterrupted())
            {
            //verify the presence of a connection
            try
               {
               synchronized (_socket)
                  {
                  while (!_socket.isConnected())
                     {
                     _socket.wait();
                     }
                  }
               }
            catch (InterruptedException quitmsg)
               {
               return;
               }

            //check for interruption
            if (isInterrupted())
               {
               return;
               }

            //read into UDP buffer; simply ignore time-outs or other connection failures
            try
               {
               synchronized (_socket)
                  {
                  if (!_socket.isConnected())
                     {
                     continue;
                     }
                  _locallog.debug("Attempt: read UDP packet");
                  _socket.receive(_buffer);
                  _locallog.debug("Success: read UDP packet");
                  }
               UDPImage img = new UDPImage(_buffer);
               int ordernumber = img.getFrameCount();
               if (ordernumber > _last)
                  {
                  _last = ordernumber;
                  _images.put(img);
                  }
               }
            catch (InterruptedException terminate)
               {
               _locallog.debug("Failure: interrupted; quitting");
               return;
               }
            catch (SocketTimeoutException timeout)
               {
               _timeouts++;
               _locallog.debug("Failure: " + timeout.getMessage() + "; " + _timeouts + " consecutive timeouts, so far.");
               if (_timeouts >= 5)
                  {
                  _timeouts = 0;
                  _locallog.info("Attempting to disconnect/reconnect due to timeouts");
                  synchronized (_socket)
                     {
                     _socket.disconnect();
                     _socket.notifyAll();
                     }
                  }
               continue;
               }
            catch (Exception other)
               {
               _locallog.debug("Failure: " + other.getMessage());
               continue;
               }
            }
         }

      private Logger _locallog = Logger.getLogger(getClass());
      private int _timeouts = 0;
      private int _last = -1;
      private DatagramPacket _buffer = new DatagramPacket(new byte[UDPVideoConstants.PACKETBUFFER_SIZE], UDPVideoConstants.PACKETBUFFER_SIZE);
      }

   private DatagramPacket startMessage()
      {
      if (_startmessage == null)
         {
         UDPCommand start = new UDPCommand(_helper.getUser(), _helper.getKey(), UDPCommand.START_COMMAND);
         _startmessage = new DatagramPacket(start.getBytes(), start.getBytes().length);
         }
      return _startmessage;
      }

   private DatagramPacket stopMessage()
      {
      if (_stopmessage == null)
         {
         UDPCommand start = new UDPCommand(_helper.getUser(), _helper.getKey(), UDPCommand.STOP_COMMAND);
         _stopmessage = new DatagramPacket(start.getBytes(), start.getBytes().length);
         }
      return _stopmessage;
      }

   private static enum STATE
      {
         RUNNING, PAUSED, TERMINATED
      }

   ;
   private AttemptConnectionLoop _attemptconnect = new AttemptConnectionLoop();
   private DetectDisconnectLoop _detectdisconnect = new DetectDisconnectLoop();
   private ImageReceivingLoop _imagereceiving = new ImageReceivingLoop();
   private ImageProcessingLoop _imageprocessing = new ImageProcessingLoop();
   private DatagramPacket _startmessage = null;
   private DatagramPacket _stopmessage = null;
   private STATE _state = STATE.TERMINATED;
   private BlockingQueue<UDPImage> _images = new LinkedBlockingQueue<UDPImage>();
   private DatagramSocket _socket = null;
   private UDPHelper _helper = null;
   private static Logger _LOG = Logger.getLogger(UDPVideoSubscriber.class);
   }
