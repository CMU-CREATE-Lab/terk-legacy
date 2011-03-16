package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public interface MotorBufferEffect
   {
   BackEMFMotorBuffer[] transform(BackEMFMotorBuffer[] input);
   }
