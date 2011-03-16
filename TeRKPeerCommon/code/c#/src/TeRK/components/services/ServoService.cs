using TeRK.services;

namespace TeRK.components.services
   {
   public abstract class ServoService : Service
      {
      public const string TYPE_ID = "::TeRK::ServoController";

      public string getTypeId()
         {
         return TYPE_ID;
         }

      public abstract void execute(bool[] servoMask, ServoMode[] servoModes, int[] servoPositions, int[] servoSpeeds);

      public abstract void execute(ServoCommand cmd);

      public abstract void setPosition(int position, int servoid);

      /** Sets the servo given by the id to a certain position (degrees) at a given velocity (m/s). */
      public abstract void setPositionWithVelocity(int position, int velocity, int servoid);

      /** Halts the servo and places it in neutral. */
      public abstract void stopServo(int servoid);

      /** Halts the servo and prevents further motion. */
      public abstract void emergencyStopServo(int servoid);

      public abstract void setServoVelocities(int[] velocities);

      public abstract void stopServos();
      }
   }