package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public class NoEffect implements MotorEffect, MotorBufferEffect
   {
   public NoEffect()
      {
      }

   public String toString()
      {
      return "No Effect";
      }

   public BackEMFMotorBuffer transform(final BackEMFMotorBuffer input)
      {
      return input;
      }

   public BackEMFMotorBuffer[] transform(final BackEMFMotorBuffer[] input)
      {
      return input;
      }
   }
