package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class ReverseEffect implements MotorEffect, MotorBufferEffect
   {
   public ReverseEffect()
      {
      }

   public String toString()
      {
      return "Reverse";
      }

   public BackEMFMotorBuffer transform(final BackEMFMotorBuffer input)
      {
      final int[] states = new int[input.size()];
      final int[] values = input.getValues();
      if (input.size() > 0)
         {
         states[0] = values[0];
         for (int i = 1; i < states.length; i++)
            {
            states[i] = values[i] + states[i - 1];
            }
         }

      final int[] result = new int[states.length];
      if (input.size() > 0)
         {
         result[0] = states[0];
         for (int i = 1; i < states.length; i++)
            {
            result[i] = states[states.length - i - 1] - states[states.length - i];
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
   }
