package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SwingVideoStreamViewport implements VideoStreamViewport
   {
   private static final Logger LOG = Logger.getLogger(SwingVideoStreamViewport.class);

   private final JLabel viewportLabel = new JLabel();
   private final ImageIcon viewportImageIcon = new ImageIcon();

   public SwingVideoStreamViewport()
      {
      viewportLabel.setDoubleBuffered(true);
      viewportLabel.setIconTextGap(0);
      }

   /**
    * Displays the image described by the given array of bytes which were read from an image file containing a supported
    * image format, such as GIF, JPEG, or PNG.
    */
   public void handleFrame(final byte[] frameData)
      {
      if (frameData != null && frameData.length > 0)
         {
         final SwingWorker worker =
               new SwingWorker()
               {
               public Object construct()
                  {
                  return Toolkit.getDefaultToolkit().createImage(frameData);
                  }

               public void finished()
                  {
                  viewportImageIcon.setImage((Image)get());
                  viewportLabel.setIcon(viewportImageIcon);
                  viewportLabel.repaint();
                  }
               };
         worker.start();
         }
      else
         {
         LOG.warn("SwingVideoStreamViewport.handleFrame(): ingoring null or empty frameData");
         }
      }

   public void setBorder(final Border border)
      {
      viewportLabel.setBorder(border);
      }

   public void setPreferredSize(final Dimension preferredSize)
      {
      viewportLabel.setPreferredSize(preferredSize);
      }

   public void setMinimumSize(final Dimension minimumSize)
      {
      viewportLabel.setMinimumSize(minimumSize);
      }

   public void setMaximumSize(final Dimension maximumSize)
      {
      viewportLabel.setMaximumSize(maximumSize);
      }

   public Component getComponent()
      {
      return viewportLabel;
      }
   }
