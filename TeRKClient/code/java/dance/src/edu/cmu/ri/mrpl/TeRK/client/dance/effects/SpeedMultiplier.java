package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class SpeedMultiplier implements MotorEffect, MotorBufferEffect
   {
   public SpeedMultiplier()
      {
      this(2.0);
      }

   public SpeedMultiplier(final double value)
      {
      _factor = value;
      }

   public SpeedMultiplier(final int value)
      {
      _factor = (double)value;
      }

   public String toString()
      {
      return "Speed Multiply x" + _factor;
      }

   public BackEMFMotorBuffer transform(final BackEMFMotorBuffer input)
      {
      final int[] result = new int[input.size()];
      int index = 0;
      for (final int k : input.getValues())
         {
         result[index++] = (int)((short)(((double)k) * _factor));
         }

      return new BackEMFMotorBuffer(result);
      }

   public BackEMFMotorBuffer[] transform(final BackEMFMotorBuffer[] input)
      {
      for (int i = 0; i < input.length; i++)
         {
         input[i] = transform(input[i]);
         }
      return input;
      }

   private double _factor;
   }
