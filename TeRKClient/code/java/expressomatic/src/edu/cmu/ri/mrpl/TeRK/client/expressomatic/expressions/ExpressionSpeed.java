package edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions;

import java.io.Serializable;

public class ExpressionSpeed implements Serializable
   {
   public final static int MINIMUM_VELOCITY = 1;
   public final static int MAXIMUM_VELOCITY = 1000;

   public final static int FAST_VELOCITY = 1000;
   public final static int MEDIUM_VELOCITY = 500;
   public final static int SLOW_VELOCITY = 100;

   // The velocity to move the servos at
   private int servoVelocity;

   public ExpressionSpeed(int servoVelocity)
      {
      setServoVelocity(servoVelocity);
      }

   public void setServoVelocity(int velocity)
      {
      servoVelocity = (velocity < MINIMUM_VELOCITY) ? MINIMUM_VELOCITY :
                      (velocity > MAXIMUM_VELOCITY) ? MAXIMUM_VELOCITY :
                      velocity;
      }

   public int getServoVelocity()
      {
      return servoVelocity;
      }
   }
