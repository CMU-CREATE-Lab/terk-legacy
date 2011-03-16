using TeRK.services;

namespace TeRK.components.services
   {
   public abstract class MotorService : Service
      {
      public const string TYPE_ID = "::TeRK::MotorController";

      public string getTypeId()
         {
         return TYPE_ID;
         }

      public abstract MotorState execute(bool[] mask, MotorMode[] modes, int[] positions, int[] velocities, int[] accelerations);

      public abstract MotorState execute(MotorCommand command);

      /**
       *	Sets the velocity of the given motor
       *
       * The motor given by motorid will run on a duty-cycle at the given signed velocity (m/s) until the next call to
       * setMotorVelocity or stopMotors() or another non-const function.
       */
      public abstract void setMotorVelocity(int velocity, int motorid);

      /**
       *	Sets the velocity of the given motor until the position is reached
       *
       * The motor given by motorid will move with the given signed velocity (m/s) until the number of degrees has been
       * turned or until the next command has been executed.
       *
       *	NOTE: setMotorVelocityUntil(v,-p,id) and setMotorVelocityUntil(-v,p,id) are equivalent
       */
      public abstract void setMotorVelocityUntil(int velocity, int position, int motorid);

      public abstract void setMotorVelocities(int[] velocities);

      /**
       *	Halts the given motors (specified by motor ID) and puts them in neutral.  Halts all the motors if none are
       * specified.
       */
      public abstract void stopMotors(params int[] motorIds);

      /** Halts the motor and prevents further movement. */
      public abstract void emergencyStopMotor(int motorid);
      }
   }