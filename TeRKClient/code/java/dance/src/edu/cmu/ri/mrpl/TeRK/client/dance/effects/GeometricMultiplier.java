package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class GeometricMultiplier implements MotorEffect, MotorBufferEffect
   {
   public GeometricMultiplier()
      {
      this(2);
      }

   public GeometricMultiplier(final int value)
      {
      _factor = value;
      }

   public String toString()
      {
      return "Geometric Multiply x" + _factor;
      }

   public BackEMFMotorBuffer transform(final BackEMFMotorBuffer input)
      {
      final int[] current = input.getValues();
      final int[] result = new int[current.length * _factor];
      for (int i = 0; i < current.length; i++)
         {
         for (int j = 0; j < _factor; j++)
            {
            result[i * _factor + j] = current[i];
            }
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

   private int _factor;
   }
