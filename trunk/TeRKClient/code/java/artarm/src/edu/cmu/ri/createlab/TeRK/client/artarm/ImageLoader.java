package edu.cmu.ri.createlab.TeRK.client.artarm;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 17, 2008
 * Time: 3:21:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageLoader
   {
   private static double DEFAULT_PAGE_WIDTH = 8.5 * 2.54;
   private static double DEFAULT_PAGE_HEIGHT = 11 * 2.54;

   private static Image loadImageFromPath(String path)
      {
      Image returnImage;
      try
         {
         returnImage = ImageIO.read(new File(path));
         }
      catch (Exception x)
         {
         returnImage = null;
         }

      return returnImage;
      }

   public static LineImage loadLineImageFromPath(String path,
                                                 int thresh,
                                                 int guassian)
      {
      return loadLineImageFromPath(path, thresh, guassian, DEFAULT_PAGE_WIDTH, DEFAULT_PAGE_HEIGHT);
      }

   public static LineImage loadLineImageFromPath(String path,
                                                 int thresh,
                                                 int guassian,
                                                 double width,
                                                 double height)
      {
      return Image2LineImage(loadImageFromPath(path), thresh, guassian, width, height);
      }

   public static DotImage loadDotImageFromPath(String path,
                                               int thresh,
                                               int guassian)
      {
      return loadDotImageFromPath(path, thresh, guassian, DEFAULT_PAGE_WIDTH, DEFAULT_PAGE_HEIGHT);
      }

   public static DotImage loadDotImageFromPath(String path,
                                               int thresh,
                                               int guassian,
                                               double width,
                                               double height)
      {
      return Image2DotImage(loadImageFromPath(path), thresh, guassian, width, height);
      }

   private static DotImage Image2DotImage(Image imageIn,
                                          int thresh,
                                          int guassian,
                                          double width,
                                          double height)
      {
      int DPC = 3;//sqrt(dots per centimeter)
      int maxPixelWidth = (int)width * DPC;
      int maxPixelHeight = (int)height * DPC;
      int pixelWidth = imageIn.getWidth(null);
      int pixelHeight = imageIn.getHeight(null);
      Image scaledImage;

      if (((double)pixelWidth / pixelHeight) > ((double)maxPixelWidth / maxPixelHeight))
         {
         scaledImage = imageIn.getScaledInstance(maxPixelWidth, -1, Image.SCALE_DEFAULT);
         }
      else
         {
         scaledImage = imageIn.getScaledInstance(-1, maxPixelHeight, Image.SCALE_DEFAULT);
         }

      EdgeDetector canny = new EdgeDetector();
      Image imageOut;
      try
         {
         canny.setSourceImage(scaledImage);
         canny.setThreshold(thresh);
         canny.setWidGaussianKernel(guassian);
         canny.process();
         imageOut = canny.getEdgeImage();
         }
      catch (Exception x)
         {
         return null;
         }

      BufferedImage bufferedImage = new BufferedImage(imageOut.getWidth(null), imageOut.getHeight(null), BufferedImage.TYPE_INT_RGB);
      Graphics2D g2 = bufferedImage.createGraphics();
      g2.drawImage(imageOut, 0, 0, null);
      g2.dispose();
      try
         {
         System.out.println("Writing image");

         ImageIO.write(bufferedImage, "jpg", new File("C:\\imageOut.jpg"));
         }
      catch (IOException ioe)
         {
         System.out.println("write: " + ioe.getMessage());
         }

      return new DotImage(imageOut, width, height);
      }

   private static LineImage Image2LineImage(Image imageIn,
                                            int thresh,
                                            int guassian,
                                            double width,
                                            double height)
      {
      EdgeDetector canny = new EdgeDetector();
      Image imageOut;
      try
         {
         canny.setSourceImage(imageIn);
         canny.setThreshold(thresh);
         canny.setWidGaussianKernel(guassian);
         canny.process();
         imageOut = canny.getEdgeImage();
         }
      catch (Exception x)
         {
         return null;
         }

      //todo implement sampling

      LineImage result = new LineImage(width, height);
      return result;
      }
   }
                       