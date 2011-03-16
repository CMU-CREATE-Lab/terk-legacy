/**
 * @author Victor M. Marmol G. (vmarmol@andrew.cmu.edu)
 * @author Tilak Sharma (tilaks@andrew.cmu.edu)
 *
 * http://www.viberobotics.com/finch/
 *
 * Version 1.0
 * February 14-16, 2008
 *
 */

import finch.Finch;

public class Calibration
   {
   /*
        * Arrays to hold the calibration values for the horizontal, vertical, and wall setup
        * 0 - X
        * 1 - Y
        * 2 = Z
        */
   public double horizontalCalibration[];
   public double verticalCalibration[];
   public double wallCalibration[];

   //The finch we will be applying our calibration to
   private Finch toCalibrate;

   //Number of readings to take for calibration
   private final int numReadings = 10;

   /**
    * COnstructor that applies this calibration to the specified Finch
    *
    * @param toCal = the Finch we will calibrate
    */
   public Calibration(Finch toCal)
      {
      //Initialize calibration arrays
      horizontalCalibration = new double[3];
      verticalCalibration = new double[3];
      wallCalibration = new double[3];

      //Initialize the arrays to 0
      horizontalCalibration[0] = 0;
      horizontalCalibration[1] = 0;
      horizontalCalibration[2] = 0;
      verticalCalibration[0] = 0;
      verticalCalibration[1] = 0;
      verticalCalibration[2] = 0;
      wallCalibration[0] = 0;
      wallCalibration[1] = 0;
      wallCalibration[2] = 0;

      //Store the finch we are going to calibrate
      toCalibrate = toCal;
      }

   /**
    * Constructor that receives a preset calibration
    *
    * @param toCal = the Finch we will calibrate
    * @param horizontal = horizontal calibration values
    * @param vertical = vertical calibration values
    * @param wall = wall calibration values
    */
   public Calibration(Finch toCal, double horizontal[], double vertical[], double wall[])
      {
      //Initialize calibration arrays
      horizontalCalibration = new double[3];
      verticalCalibration = new double[3];
      wallCalibration = new double[3];

      //Initialize the arrays to their parametized value
      horizontalCalibration[0] = horizontal[0];
      horizontalCalibration[1] = horizontal[1];
      horizontalCalibration[2] = horizontal[2];
      verticalCalibration[0] = vertical[0];
      verticalCalibration[1] = vertical[1];
      verticalCalibration[2] = vertical[2];
      wallCalibration[0] = wall[0];
      wallCalibration[1] = wall[1];
      wallCalibration[2] = wall[2];

      //Store the finch we are going to calibrate
      toCalibrate = toCal;
      }

   /**
    * Takes horizontal calibration readings
    */
   public void calibrateHorizontal()
      {
      double readX = 0;
      double readY = 0;
      double readZ = 0;

      //Take numReadings readings while Finch is belly-down
      for (int i = 0; i < numReadings; i++)
         {
         //Take readings for X, Y, and Z
         readX += toCalibrate.getXAcceleration();
         readY += toCalibrate.getYAcceleration();
         readZ += toCalibrate.getZAcceleration();

         //Wait 2ms before taking the next reading
         //toCalibrate.sleep(2);
         }

      //Divide totals by numReadings to find the average (we just took numReadings readings)
      horizontalCalibration[0] = readX / numReadings;
      horizontalCalibration[1] = readY / numReadings;
      horizontalCalibration[2] = readZ / numReadings;
      }

   /**
    * Takes vertical calibration readings
    */
   public void calibrateVertical()
      {
      double readX = 0;
      double readY = 0;
      double readZ = 0;

      //Take numReadings readings while Finch is belly-down
      for (int i = 0; i < numReadings; i++)
         {
         //Take readings for X, Y, and Z
         readX += toCalibrate.getXAcceleration();
         readY += toCalibrate.getYAcceleration();
         readZ += toCalibrate.getZAcceleration();

         //Wait 2ms before taking the next reading
         //toCalibrate.sleep(2);
         }

      //Divide totals by numReadings to find the average (we just took numReadings readings)
      verticalCalibration[0] = readX / numReadings;
      verticalCalibration[1] = readY / numReadings;
      verticalCalibration[2] = readZ / numReadings;
      }

   /**
    * Takes wall calibration readings
    */
   public void calibrateWall()
      {
      double readX = 0;
      double readY = 0;
      double readZ = 0;

      //Take numReadings readings while Finch is belly-down
      for (int i = 0; i < numReadings; i++)
         {
         //Take readings for X, Y, and Z
         readX += toCalibrate.getXAcceleration();
         readY += toCalibrate.getYAcceleration();
         readZ += toCalibrate.getZAcceleration();

         //Wait 2ms before taking the next reading
         //toCalibrate.sleep(2);
         }

      //Divide totals by numReadings to find the average (we just took numReadings readings)
      wallCalibration[0] = readX / numReadings;
      wallCalibration[1] = readY / numReadings;
      wallCalibration[2] = readZ / numReadings;
      }

   /**
    * Returns the X acceleration using the horizontal calibration
    *
    * @return x acceleration using the horizontal calibration
    */
   public double getXAcceleration()
      {
      //Take the raw value and subtract our calibration value (we only use the horizontal calibration value for this)
      double rawValue = toCalibrate.getXAcceleration();

      return (rawValue - horizontalCalibration[0]);
      }

   /**
    * Returns the Y acceleration using the horizontal calibration
    *
    * @return y acceleration using the horizontal calibration
    */
   public double getYAcceleration()
      {
      //Take the raw value and subtract our calibration value (we only use the horizontal calibration value for this)
      double rawValue = toCalibrate.getYAcceleration();

      return (rawValue - horizontalCalibration[1]);
      }

   /**
    * Returns the Z acceleration using the horizontal calibration
    *
    * @return z acceleration using the horizontal calibration
    */
   public double getZAcceleration()
      {
      //Take the raw value and subtract our calibration value (we only use the horizontal calibration value for this)
      double rawValue = toCalibrate.getZAcceleration();

      return (rawValue - horizontalCalibration[2]);
      }

   /**
    * Get angle on the X using horizontal and vertical calibration
    *
    * @return angle from which the robot is tipped up 0 = horizontal
    */
   public double getXAngle()
      {
      //Take the raw value and ADD COMMENT HERE
      double rawValue = toCalibrate.getXAcceleration();
      double sin = (rawValue - horizontalCalibration[0]) / (verticalCalibration[0] - horizontalCalibration[0]);
      if (sin > 1)
         {
         sin = 1;
         }
      if (sin < -1)
         {
         sin = -1;
         }
      return (Math.toDegrees(Math.asin(sin)));
      }

   /**
    * Get angle on the Y using horizontal and wall calibration
    *
    * @return angle from which the robot is tipped to the side 0 = horizontal
    */
   public double getYAngle()
      {
      //Take the raw value and ADD COMMENT HERE
      double rawValue = toCalibrate.getYAcceleration();
      double sin = (rawValue - horizontalCalibration[1]) / (wallCalibration[1] - horizontalCalibration[1]);
      if (sin > 1)
         {
         sin = 1;
         }
      if (sin < -1)
         {
         sin = -1;
         }
      return (Math.toDegrees(Math.asin(sin)));
      }

   /**
    * Returns a string representing the calibrations
    *
    * @return Returns a string with calibration information
    */
   public String toString()
      {
      String toReturn = new String();
      toReturn = "Horizontal calibration: (" + horizontalCalibration[0] + "," + horizontalCalibration[1] + "," + horizontalCalibration[2] + ").\n";
      toReturn = toReturn.concat("Vertical calibration: (" + verticalCalibration[0] + "," + verticalCalibration[1] + "," + verticalCalibration[2] + ").\n");
      toReturn = toReturn.concat("Wall calibration: (" + wallCalibration[0] + "," + wallCalibration[1] + "," + wallCalibration[2] + ").");

      return (toReturn);
      }
   }
