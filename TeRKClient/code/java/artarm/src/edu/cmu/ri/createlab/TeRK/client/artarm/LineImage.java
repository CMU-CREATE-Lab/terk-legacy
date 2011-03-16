package edu.cmu.ri.createlab.TeRK.client.artarm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 17, 2008
 * Time: 3:15:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class LineImage
   {

   private double _width;
   private double _height;
   public List<Line> imageLines;

   public LineImage(double width, double height)
      {
      _width = width;
      _height = height;
      imageLines = new ArrayList<Line>();
      }

   public LineImage(LineImage original)
      {
      this._width = original._width;
      this._height = original._height;
      imageLines = new ArrayList<Line>(original.getLines());
      }

   public void clearImage()
      {
      imageLines.clear();
      }

   public void addLine(Line line) throws IllegalArgumentException
      {
      checkBounds(line);
      imageLines.add(line);
      }

   public void addLine(double x1, double y1, double x2, double y2)
      {
      Line line = new Line(x1, y1, x2, y2);
      this.addLine(line);
      }

   public void checkBounds(Line line) throws IllegalArgumentException
      {

      if ((line.get_x2() > _width) ||
          (line.get_x1() > _width) ||
          (line.get_y1() > _height) ||
          (line.get_y2() > _height) ||
          (line.get_x1() < 0) ||
          (line.get_x2() < 0) ||
          (line.get_y1() < 0) ||
          (line.get_y2() < 0))
         {
         throw new IllegalArgumentException("Line out of image bounds");
         }
      }

   public List<Line> getLines()
      {
      return Collections.unmodifiableList(imageLines);
      }
   }
