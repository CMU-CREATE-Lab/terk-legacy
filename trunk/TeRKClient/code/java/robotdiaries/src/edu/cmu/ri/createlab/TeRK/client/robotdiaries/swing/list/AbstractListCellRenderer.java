package edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.SwingConstants;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public abstract class AbstractListCellRenderer extends JLabel implements ListCellRenderer, SwingConstants
   {
   private static final Logger LOG = Logger.getLogger(AbstractListCellRenderer.class);

   private static final ImageIcon DEFAULT_ICON = new ImageIcon(new BufferedImage(ICON_IMAGE_SIZE, ICON_IMAGE_SIZE, BufferedImage.TYPE_3BYTE_BGR));

   private static final String DEFAULT_SEQUENCE_ICON = ".DefaultSequenceIcon";
   private static final Map<String, ImageIcon> EXPRESSION_IMAGES = Collections.synchronizedMap(new HashMap<String, ImageIcon>());
   private static final Map<String, ImageIcon> SEQUENCE_IMAGES = Collections.synchronizedMap(new HashMap<String, ImageIcon>());
   private static final Map<String, ImageIcon> CONDITION_IMAGES = Collections.synchronizedMap(new HashMap<String, ImageIcon>());

   public static void loadExpressionImages(final File iconsDir)
      {
      loadImages(iconsDir, EXPRESSION_IMAGES);
      }

   public static void loadSequenceImages(final File iconsDir)
      {
      loadImages(iconsDir, SEQUENCE_IMAGES);
      }

   public static void loadConditionImages(final File iconsDir)
      {
      loadImages(iconsDir, CONDITION_IMAGES);
      }

   private static void loadImages(final File iconsDir, final Map<String, ImageIcon> imageMap)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("AbstractListCellRenderer.loadImages(): loading images from directory [" + iconsDir + "]");
         }

      imageMap.clear();

      if (!iconsDir.exists())
         {
         if (LOG.isEnabledFor(Level.WARN))
            {
            LOG.warn("AbstractListCellRenderer.loadImages(): icon dir [" + iconsDir + "] doesn't exist");
            }
         return;
         }

      final File[] iconFiles = iconsDir.listFiles();
      for (final File f : iconFiles)
         {
         imageMap.put(getImageFilename(f), loadIcon(f));
         }
      }

   private static ImageIcon loadIcon(final File f)
      {
      try
         {
         final Image img = ImageIO.read(f);
         if (img != null)
            {
            return new ImageIcon(img.getScaledInstance(ICON_IMAGE_SIZE, ICON_IMAGE_SIZE, BufferedImage.SCALE_SMOOTH));
            }
         }
      catch (Exception e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("AbstractListCellRenderer.loadImage(): Exception while trying to load icon [" + f + "]", e);
            }
         }
      return null;
      }

   private static String getImageFilename(final File f)
      {
      final String name = f.getName();
      if (name != null)
         {
         return name.substring(0, name.lastIndexOf('.'));
         }
      return name;
      }

   protected ImageIcon getExpressionIcon(final String name)
      {
      if (EXPRESSION_IMAGES == null || name == null)
         {
         return DEFAULT_ICON;
         }

      final ImageIcon icon = EXPRESSION_IMAGES.get(name);
      if (icon == null)
         {
         return DEFAULT_ICON;
         }

      return icon;
      }

   protected ImageIcon getSequenceIcon(final String name)
      {
      if (SEQUENCE_IMAGES == null || name == null)
         {
         return DEFAULT_ICON;
         }

      ImageIcon icon = SEQUENCE_IMAGES.get(name);
      if (icon == null)
         {
         icon = SEQUENCE_IMAGES.get(DEFAULT_SEQUENCE_ICON);
         }
      if (icon == null)
         {
         return DEFAULT_ICON;
         }

      return icon;
      }

   protected ImageIcon getConditionIcon(final String name)
      {
      if (CONDITION_IMAGES == null || name == null)
         {
         return DEFAULT_ICON;
         }

      final ImageIcon icon = CONDITION_IMAGES.get(name);
      if (icon == null)
         {
         return DEFAULT_ICON;
         }

      return icon;
      }
   }