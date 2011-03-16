package edu.cmu.ri.createlab.TeRK.client.artarm;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Aug 11, 2008
 * Time: 2:40:17 PM
 * To change this template use File | Settings | File Templates.
 */
class ArmJointAngles
   {
   private double _linkOneAngle;
   private double _linkTwoAngle;

   public double getLinkOneAngleRadians()
      {
      return _linkOneAngle;
      }

   public double getLinkTwoAngleRadians()
      {
      return _linkTwoAngle;
      }

   public double getLinkOneAngleDegrees()
      {
      return convertRadiansToDegrees(_linkOneAngle);
      }

   public double getLinkTwoAngleDegrees()
      {
      return convertRadiansToDegrees(_linkTwoAngle);
      }

   public static double convertRadiansToDegrees(double rads)
      {
      return rads * 180 / Math.PI;
      }

   public static double convertDegreesToRadians(double degrees)
      {
      return degrees * Math.PI / 180.0;
      }

   public ArmJointAngles(double linkOneAngle, double linkTwoAngle)
      {
      _linkOneAngle = linkOneAngle;
      _linkTwoAngle = linkTwoAngle;
      }
   }
