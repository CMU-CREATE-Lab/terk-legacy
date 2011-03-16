package edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.ListCellRenderer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.SwingConstants;

public abstract class AbstractListCellRenderer extends JLabel implements ListCellRenderer, SwingConstants
   {
   private static final String ICON_DIR_STRING = "Icons";

   private static final ImageIcon DEFAULT_ICON =
         new ImageIcon(new BufferedImage(ICON_IMAGE_SIZE,
                                         ICON_IMAGE_SIZE, BufferedImage.TYPE_3BYTE_BGR));

   private static Hashtable<String, ImageIcon> expressionImages = null;
   private static Hashtable<String, ImageIcon> conditionImages = null;

   public static void loadExpressionImages(String expressionPath)
      {
      expressionImages = new Hashtable<String, ImageIcon>();

      File iconsDir = new File(expressionPath + ICON_DIR_STRING);

      if (!iconsDir.exists())
         {
         return;
         }

      File[] iconFiles = iconsDir.listFiles();
      Image img;
      String name;
      for (File f : iconFiles)
         {
         try
            {
            img = ImageIO.read(f);
            img = img.getScaledInstance(ICON_IMAGE_SIZE, ICON_IMAGE_SIZE, BufferedImage.SCALE_SMOOTH);

            name = f.getName();
            name = name.substring(0, name.lastIndexOf('.'));

            expressionImages.put(name, new ImageIcon(img));
            }
         catch (Exception e)
            {
            }
         }
      }

   public static void loadConditionImages(String conditionPath)
      {
      conditionImages = new Hashtable<String, ImageIcon>();

      File iconsDir = new File(conditionPath + ICON_DIR_STRING);

      if (!iconsDir.exists())
         {
         return;
         }

      File[] iconFiles = iconsDir.listFiles();
      Image img;
      String name;
      for (File f : iconFiles)
         {
         try
            {
            img = ImageIO.read(f);
            img = img.getScaledInstance(ICON_IMAGE_SIZE, ICON_IMAGE_SIZE, BufferedImage.SCALE_SMOOTH);

            name = f.getName();
            name = name.substring(0, name.lastIndexOf('.'));

            conditionImages.put(name, new ImageIcon(img));
            }
         catch (Exception e)
            {
            }
         }
      }

   protected ImageIcon getExpressionIcon(String name)
      {
      if (expressionImages == null || name == null)
         {
         return DEFAULT_ICON;
         }

      ImageIcon icon = expressionImages.get(name);
      if (icon == null)
         {
         return DEFAULT_ICON;
         }

      return icon;
      }

   protected ImageIcon getConditionIcon(String name)
      {
      if (conditionImages == null || name == null)
         {
         return DEFAULT_ICON;
         }

      ImageIcon icon = conditionImages.get(name);
      if (icon == null)
         {
         return DEFAULT_ICON;
         }

      return icon;
      }
   }
