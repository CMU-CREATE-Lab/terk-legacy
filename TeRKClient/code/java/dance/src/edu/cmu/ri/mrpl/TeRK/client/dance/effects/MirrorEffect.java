package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class MirrorEffect implements MotorBufferEffect
   {
   public MirrorEffect()
      {
      }

   public String toString()
      {
      return "Mirror";
      }

   public BackEMFMotorBuffer[] transform(final BackEMFMotorBuffer[] input)
      {
      BackEMFMotorBuffer tmp;
      for (int i = 0; i < input.length; i += 2)
         {
         if ((i + 1) < input.length)
            {
            tmp = input[i + 1];
            input[i + 1] = input[i];
            input[i] = tmp;
            }
         }
      return input;
      }
   }
