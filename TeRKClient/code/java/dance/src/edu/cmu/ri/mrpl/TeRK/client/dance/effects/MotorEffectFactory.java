package edu.cmu.ri.mrpl.TeRK.client.dance.effects;

public class MotorEffectFactory
   {
   public static MotorEffect[] getEffects()
      {
      return new MotorEffect[]{
            new NoEffect(),
            new ReverseEffect(),
            new GeometricMultiplier(2),
            new GeometricMultiplier(5),
            new GeometricMultiplier(10),
            new GeometricMultiplier(20),
            new GeometricMultiplier(100),
            new SpeedMultiplier(.5),
            new SpeedMultiplier(1.5),
            new SpeedMultiplier(2.0),
            new SpeedMultiplier(5.0),
            new SpeedMultiplier(10.0),
            new SpeedMultiplier(20.0),
            new SpeedMultiplier(50.0),
            new SpeedMultiplier(100.0),
            new SpeedMultiplier(500.0)
      };
      }

   public static MotorBufferEffect[] getBufferEffects()
      {
      return new MotorBufferEffect[]{
            new NoEffect(),
            new MirrorEffect(),
            new ReverseEffect(),
            new GeometricMultiplier(2),
            new GeometricMultiplier(5),
            new GeometricMultiplier(10),
            new GeometricMultiplier(20),
            new GeometricMultiplier(100),
            new SpeedMultiplier(.5),
            new SpeedMultiplier(1.5),
            new SpeedMultiplier(2.0),
            new SpeedMultiplier(5.0),
            new SpeedMultiplier(10.0),
            new SpeedMultiplier(20.0),
            new SpeedMultiplier(50.0),
            new SpeedMultiplier(100.0),
            new SpeedMultiplier(500.0)
      };
      }

   private MotorEffectFactory()
      {
      // private to prevent instantiation
      }
   }
