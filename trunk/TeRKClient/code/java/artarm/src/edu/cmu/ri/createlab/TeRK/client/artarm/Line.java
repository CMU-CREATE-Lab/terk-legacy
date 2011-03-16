package edu.cmu.ri.createlab.TeRK.client.artarm;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 18, 2008
 * Time: 12:19:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class Line
   {

   private Coordinates _startPoint;
   private Coordinates _endPoint;

   public Line(Coordinates startPoint, Coordinates endPoint)
      {
      _startPoint = new Coordinates(startPoint);
      _endPoint = new Coordinates(endPoint);
      }

   public Line(double x1, double y1, double x2, double y2)
      {
      _startPoint = new Coordinates(x1, y1);
      _endPoint = new Coordinates(x2, y2);
      }

   public double length()
      {
      return Math.sqrt(Math.pow((get_x1() - get_x2()), 2) + Math.pow((get_y1() - get_y2()), 2));
      }

   public double get_x1()
      {
      return this._startPoint.getX();
      }

   public double get_x2()
      {
      return this._endPoint.getX();
      }

   public double get_y1()
      {
      return this._startPoint.getY();
      }

   public double get_y2()
      {
      return this._endPoint.getY();
      }

   public Coordinates getStart()
      {
      return this._startPoint;
      }

   public Coordinates getEnd()
      {
      return this._endPoint;
      }
   }