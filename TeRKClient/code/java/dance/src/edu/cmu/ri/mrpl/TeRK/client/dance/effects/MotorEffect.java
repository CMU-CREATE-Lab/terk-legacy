package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;

public interface MotorEffect
   {
   BackEMFMotorBuffer transform(final BackEMFMotorBuffer input);
   }
