package edu.cmu.ri.createlab.TeRK.hummingbird;

import java.awt.Color;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface HummingbirdState
   {
   Color[] getFullColorLEDs();

   int[] getLedIntensities();

   int[] getServoPositions();

   int[] getMotorVelocities();

   int[] getVibrationMotorSpeeds();

   short[] getAnalogInputValues();
   }