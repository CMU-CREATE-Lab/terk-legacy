package edu.cmu.ri.createlab.TeRK.client.artarm;

import org.apache.log4j.Logger;

//import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 17, 2008
 * Time: 2:25:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class DrawingArm
   {
   private static final Logger LOG = Logger.getLogger(DrawingArm.class);
   private final double _linkOneLength;
   private final double _linkTwoLength;
   private AngleToTicksTransformer _jointOneTransformer;
   private AngleToTicksTransformer _jointTwoTransformer;

   // private final QwerkController _controller;

   public DrawingArm(//QwerkController controller,
                     double linkOneLength,
                     double linkTwoLength)
      {
      //  this._controller = controller;
      this._linkOneLength = linkOneLength;
      this._linkTwoLength = linkTwoLength;
      }

   public void calibrateOne(double posOneDegrees, double posOneTicks, double posTwoDegrees, double posTwoTicks)
      {
      double posOneRads = Math.toRadians(posOneDegrees);
      double posTwoRads = Math.toRadians(posTwoDegrees);
      _jointOneTransformer = new AngleToTicksTransformer(posOneRads, posOneTicks, posTwoRads, posTwoTicks);
      }

   public void calibrateTwo(double posOneDegrees, double posOneTicks, double posTwoDegrees, double posTwoTicks)
      {
      double posOneRads = Math.toRadians(posOneDegrees);
      double posTwoRads = Math.toRadians(posTwoDegrees);
      _jointTwoTransformer = new AngleToTicksTransformer(posOneRads, posOneTicks, posTwoRads, posTwoTicks);
      }

   private ArmJointAngles solveInverseKinematics(Coordinates endPosition)
      {
      double x = endPosition.getX();
      double y = endPosition.getY();
      double Bsquared = Math.pow(x, 2) + Math.pow(y, 2);
      double B = Math.sqrt(Bsquared);
      double phi1 = Math.atan2(y, x);
      double length1squared = Math.pow(_linkOneLength, 2);
      double length2squared = Math.pow(_linkTwoLength, 2);
      double phi2 = Math.acos((length1squared - length2squared + Bsquared) / (2 * B * _linkOneLength));

      double theta1 = phi1 + phi2;
      double theta2 = Math.acos((length1squared + length2squared - Bsquared) / (2 * _linkOneLength * _linkTwoLength));

      return new ArmJointAngles(theta1, theta2);
      }

   private Coordinates solveForwardKinematics(ArmJointAngles jointAngles)
      {
      double theta1 = jointAngles.getLinkOneAngleRadians();
      double theta2 = jointAngles.getLinkTwoAngleRadians();
      double theta12 = theta1 + theta2;

      double x = this._linkOneLength * Math.cos(theta1) + this._linkTwoLength * Math.cos(theta12);
      double y = this._linkOneLength * Math.sin(theta1) + this._linkTwoLength * Math.sin(theta12);

      return new Coordinates(x, y);
      }

   public void drawLineImage(LineImage imageToDraw, double originOffsetX, double originOffsetY, double originOffsetTheta)
      {
      CoordinateFrameTransformer2D transformer = new CoordinateFrameTransformer2D(originOffsetX, originOffsetY, originOffsetTheta);
      for (Line line : imageToDraw.getLines())
         {
         drawLine(transformer.transformLine(line));
         }
      }

   public void drawDotImage(DotImage imageToDraw, double originOffsetX, double originOffsetY, double originOffsetTheta)
      {
      LOG.debug("Drawing dot image at: (" + originOffsetX + ", " + originOffsetY + ", " + originOffsetTheta + ")");
      LOG.debug("Drawing " + imageToDraw.getDotImage().size() + " dots.");
      CoordinateFrameTransformer2D transformer = new CoordinateFrameTransformer2D(originOffsetX, originOffsetY, originOffsetTheta);

      for (Coordinates dot : imageToDraw.getDotImage())
         {
         Coordinates newDot = transformer.transform(dot);
         LOG.debug("Drawing dot at page: (" + dot.getX() + ", " + dot.getY() + "), arm: (" + newDot.getX() + ", " + newDot.getY() + ")");
         drawDot(newDot);
         }

      LOG.debug("Drew " + imageToDraw.getDotImage().size() + " dots.");
      }

   private void drawLine(Line line)
      {
      if (line.length() > 2.0)
         {
         double xm = (line.get_x1() + line.get_x2()) / 2;
         double ym = (line.get_y1() + line.get_y2()) / 2;
         drawLine(new Line(line.get_x1(), line.get_y1(), xm, ym));
         drawLine(new Line(xm, ym, line.get_x2(), line.get_y2()));
         return;
         }

      ArmJointAngles start = solveInverseKinematics(line.getStart());
      ArmJointAngles end = solveInverseKinematics(line.getEnd());

      //draw the line
      liftPen();
      driveArm(start);
      dropPen();
      driveArmPathed(start, end);
      liftPen();
      }

   private void drawDot(Coordinates dot)
      {
      ArmJointAngles pos = solveInverseKinematics(dot);
      liftPen();
      driveArm(pos);
      dropPen();
      liftPen();
      }

   private void liftPen()
      {
      //   throw new UnsupportedOperationException();
      }

   private void driveArm(ArmJointAngles destination)
      {
      if (_jointOneTransformer == null || _jointTwoTransformer == null)
         {
         throw new IllegalStateException("Arm not calibrated!");
         }
      int servoOneTicks = this._jointOneTransformer.getTicks(destination.getLinkOneAngleRadians());
      int servoTwoTicks = this._jointTwoTransformer.getTicks(destination.getLinkTwoAngleRadians());
      }

   private void driveArmPathed(ArmJointAngles origin, ArmJointAngles destination)
      {
      throw new UnsupportedOperationException();
      }

   private void dropPen()
      {
      // throw new UnsupportedOperationException();
      }

   class CoordinateFrameTransformer2D
      {
      final double[][] transformationMatrix = new double[3][3];

      public CoordinateFrameTransformer2D(double xOffset, double yOffset, double thetaOffset)
         {
         transformationMatrix[2][0] = xOffset;
         transformationMatrix[2][1] = yOffset;
         transformationMatrix[2][2] = 1;
         transformationMatrix[0][0] = Math.cos(thetaOffset);
         transformationMatrix[1][0] = -Math.sin(thetaOffset);
         transformationMatrix[0][1] = Math.sin(thetaOffset);
         transformationMatrix[1][1] = Math.cos(thetaOffset);
         }

      public Coordinates transform(Coordinates pageCoordinates)
         {
         double x = pageCoordinates.getX();
         double y = pageCoordinates.getY();

         double xf = x * transformationMatrix[0][0] + y * transformationMatrix[1][0] + transformationMatrix[2][0];
         double yf = x * transformationMatrix[0][1] + y * transformationMatrix[1][1] + transformationMatrix[2][1];
         return new Coordinates(xf, yf);
         }

      public Line transformLine(Line line)
         {
         Coordinates start = transform(new Coordinates(line.get_x1(), line.get_y1()));
         Coordinates end = transform(new Coordinates(line.get_x2(), line.get_y2()));
         return new Line(start.getX(), start.getY(), end.getX(), end.getY());
         }
      }

   private class AngleToTicksTransformer
      {
      double m, b;

      public AngleToTicksTransformer(double x1, double y1, double x2, double y2)
         {
         m = (y1 - y2) / (x1 - x2);
         b = (-1 * x1 * m) + y1;
         }

      public int getTicks(double angle)
         {
         return (int)(b + m * angle);
         }
      }
   }




   
