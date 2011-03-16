package edu.cmu.ri.createlab.TeRK.client.artarm;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 25, 2008
 * Time: 11:50:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class DotImage
   {
   private List<Coordinates> _dots;
   private int _pixelWidth;
   private int _pixelHeight;
   private double _width;
   private double _height;

   public DotImage(Image imageIn, double width, double height)
      {
      _dots = new ArrayList<Coordinates>();
      this._width = width;
      this._height = height;
      this._pixelWidth = imageIn.getWidth(null);
      this._pixelHeight = imageIn.getHeight(null);

      BufferedImage bufferedImage = new BufferedImage(this._pixelWidth, this._pixelHeight, BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.drawImage(imageIn, 0, 0, null);
      graphics.dispose();

      boolean[][] bwImage = new boolean[this._pixelWidth][this._pixelHeight];

      for (int x = 0; x < this._pixelWidth; x++)
         {
         for (int y = 0; y < this._pixelHeight; y++)
            {
            int srgb = bufferedImage.getRGB(x, y);
            int red = (srgb >> 16) & 0xFF;
            int green = (srgb >> 8) & 0xFF;
            int blue = (srgb) & 0xFF;
            if ((red + green + blue) > 100)
               {
               bwImage[x][y] = false;
               }
            else
               {
               bwImage[x][y] = true;
               }
            }
         }

      double widthRatio = this._width / this._pixelWidth;// cm/pixel
      double heightRatio = this._height / this._pixelHeight;

      for (int x = 0; x < this._pixelWidth; x++)
         {
         for (int y = 0; y < this._pixelHeight; y++)
            {
            if (bwImage[x][y])
               {//black
               _dots.add(new Coordinates(widthRatio * x, heightRatio * y));
               }
            }
         }
      }

   public List<Coordinates> getDotImage()
      {
      return _dots;
      }

   public double getWidth()
      {
      return this._width;
      }

   public double getHeight()
      {
      return this._height;
      }
   }
