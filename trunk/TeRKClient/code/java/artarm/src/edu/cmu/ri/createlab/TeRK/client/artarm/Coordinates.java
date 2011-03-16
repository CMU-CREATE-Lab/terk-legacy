package edu.cmu.ri.createlab.TeRK.client.artarm;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 18, 2008
 * Time: 3:33:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Coordinates
   {
   private double _x;
   private double _y;

   public double getX()
      {
      return _x;
      }

   public double getY()
      {
      return _y;
      }

   public Coordinates(double x, double y)
      {
      _x = x;
      _y = y;
      }

   public Coordinates(Coordinates original)
      {
      _x = original.getX();
      _y = original.getY();
      }
   }
