package edu.cmu.ri.mrpl.TeRK.client.diffdrive.aviutils;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamEventListener;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamPlayer;

public class SwingVideoRecorder implements VideoRecorder, VideoStreamEventListener
   {
   public SwingVideoRecorder(VideoStreamPlayer player)
      {
      _player = player;
      }

   public boolean isStarted()
      {
      return _started;
      }

   public void startRecording()
      {
      _started = true;
      try
         {
         _temp = File.createTempFile("teleop", "vid");
         }
      catch (IOException failure)
         {
         //TODO:  better handling... (although really, when has it been not possible to create a temporary file?)
         _started = false;
         return;
         }
      _writer = getAnimatedGIFWriter();

      try
         {
         _writer.setOutput(ImageIO.createImageOutputStream(_temp));
         }
      catch (Exception failure) //IOException or NullPointerException (if there were no GIF writers)
         {
         //TODO: better handling...
         _started = false;
         return;
         }

      try
         {
         _writer.prepareWriteSequence(null); //use default sequence writing
         }
      catch (IOException failure)
         {
         //TODO: better handling
         _started = false;
         return;
         }

      _player.addVideoStreamEventListener(this);
      }

   public void stopRecording()
      {
      _player.removeVideoStreamEventListener(this);
      _started = false;
      try
         {
         _writer.endWriteSequence();
         }
      catch (IOException failure)
         {
         //TODO: log, then ignore
         }
      _writer.dispose();
      _writer = null;
      }

   public void handleFrame(final byte[] imagedata)
      {
      if ((imagedata == null) || (imagedata.length == 0))
         {
         return;
         }

      Image theimage = Toolkit.getDefaultToolkit().createImage(imagedata);
      try
         {
         _writer.writeToSequence(new IIOImage(toBufferedImage(theimage), null, null), null);
         }
      catch (IOException failure)
         {
         //TODO: log, then ignore
         }
      }

   public void saveToFile(File f) throws IOException
      {
      if (!_temp.renameTo(f))
         { //cannot simply rename...
         byte[] transferbuffer = new byte[1024];
         InputStream input = new BufferedInputStream(new FileInputStream(_temp));
         OutputStream output = new BufferedOutputStream(new FileOutputStream(f));
         int count = 0;
         while ((count = input.read(transferbuffer)) != -1)
            { //not yet empty...
            output.write(transferbuffer, 0, count);
            }
         input.close();
         output.close();
         _temp.delete();
         }
      _temp = null;
      }

   private ImageWriter getAnimatedGIFWriter()
      {
      Iterator<ImageWriter> list = ImageIO.getImageWritersByFormatName("GIF");
      ImageWriter current = null;
      while (list.hasNext())
         {
         current = list.next();
         if (current.canWriteSequence())
            {
            break;
            }
         }
      return current;
      }

   //************ BEGIN SOURCE NOTE: http://javaalmanac.com/egs/java.awt.image/Image2Buf.html ****************
   // This method returns a buffered image with the contents of an image
   private static BufferedImage toBufferedImage(Image image)
      {
      if (image instanceof BufferedImage)
         {
         return (BufferedImage)image;
         }

      // This code ensures that all the pixels in the image are loaded
      image = new ImageIcon(image).getImage();

      // Create a buffered image with a format that's compatible with the screen
      BufferedImage bimage = null;
      GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
      try
         {
         // Create the buffered image
         GraphicsDevice gs = ge.getDefaultScreenDevice();
         GraphicsConfiguration gc = gs.getDefaultConfiguration();
         bimage = gc.createCompatibleImage(
               image.getWidth(null), image.getHeight(null), Transparency.BITMASK);
         }
      catch (HeadlessException e)
         {
         // The system does not have a screen
         }

      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

      // Copy image to buffered image
      Graphics g = bimage.createGraphics();

      // Paint the image onto the buffered image
      g.drawImage(image, 0, 0, null);
      g.dispose();

      return bimage;
      }
   //********************END SOURCE NOTE**************************************

   private ImageWriter _writer = null;
   private File _temp = null;
   private VideoStreamPlayer _player = null;
   private boolean _started = false;
   }