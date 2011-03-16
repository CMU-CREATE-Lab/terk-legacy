using System;
using System.Collections.Generic;

namespace TeRK.components.services
   {
   internal class MotorServiceImpl : MotorService
      {
      private const int DEFAULT_ACCELERATION = 16000; // todo: verify this value (and get it from somewhere else)

      private readonly MotorControllerPrx proxy;
      private readonly int numMotors;
      private readonly bool[] maskAllOn;
      private readonly IDictionary<int, bool[]> motorIdToMaskArrayMap;
      private readonly IDictionary<MotorMode, MotorMode[]> motorModeToModeArrayMap;
      private readonly int[] nullIntArray;
      private readonly int[] defaultAccelerationArray;

      internal MotorServiceImpl(MotorControllerPrx proxy)
         {
         this.proxy = proxy;
         numMotors = 4; // todo: this should be retrieved from the qwerk

         // create and initialize the all-on mask array
         maskAllOn = new bool[numMotors];
         for (int i = 0; i < maskAllOn.Length; i++)
            {
            maskAllOn[i] = true;
            }

         // create the array used for zero velocity/position and default acceleration
         nullIntArray = new int[numMotors];
         defaultAccelerationArray = new int[numMotors];
         for (int i = 0; i < numMotors; i++)
            {
            defaultAccelerationArray[i] = DEFAULT_ACCELERATION;
            }

         // build the mask arrays for each motor and store them in a map indexed on motor id
         motorIdToMaskArrayMap = new Dictionary<int, bool[]>(numMotors);
         for (int i = 0; i < numMotors; i++)
            {
            bool[] mask = new bool[numMotors];
            mask[i] = true;
            motorIdToMaskArrayMap.Add(i, mask);
            }

         // build the motor mode arrays and store them in a map indexed on motor mode
         motorModeToModeArrayMap = new Dictionary<MotorMode, MotorMode[]>(numMotors);
         MotorMode[] motorModeOffArray = new MotorMode[numMotors];
         MotorMode[] motorModePositionControlArray = new MotorMode[numMotors];
         MotorMode[] motorModeSpeedControlArray = new MotorMode[numMotors];
         for (int i = 0; i < numMotors; i++)
            {
            motorModeOffArray[i] = MotorMode.MotorOff;
            motorModePositionControlArray[i] = MotorMode.MotorPositionControl;
            motorModeSpeedControlArray[i] = MotorMode.MotorSpeedControl;
            }
         motorModeToModeArrayMap.Add(MotorMode.MotorOff, motorModeOffArray);
         motorModeToModeArrayMap.Add(MotorMode.MotorPositionControl, motorModePositionControlArray);
         motorModeToModeArrayMap.Add(MotorMode.MotorSpeedControl, motorModeSpeedControlArray);
         }

      public override MotorState execute(bool[] mask, MotorMode[] modes, int[] positions, int[] velocities, int[] accelerations)
         {
         return execute(new MotorCommand(mask, modes, positions, velocities, accelerations));
         }

      public override MotorState execute(MotorCommand command)
         {
         try
            {
            return proxy.execute(command);
            }
         catch (Exception e)
            {
            // todo: allow this to propogate to the caller
            Console.Error.WriteLine("ERROR: Error while executing motor command: {0}", e.StackTrace);
            }
         return null;
         }

      public override void setMotorVelocity(int velocity, int motorid)
         {
         //TODO: calibrate in correct units
         int[] velocities = new int[numMotors];
         velocities[motorid] = velocity;
         MotorMode[] modeArray;
         motorModeToModeArrayMap.TryGetValue(MotorMode.MotorSpeedControl, out modeArray);

         execute(getMask(motorid),
                 modeArray,
                 nullIntArray,
                 velocities,
                 defaultAccelerationArray);
         }

      public override void setMotorVelocityUntil(int velocity, int position, int motorid)
         {
         //TODO: calibrate in correct units
         int[] velocities = new int[numMotors];
         int[] positions = new int[numMotors];
         velocities[motorid] = velocity;
         positions[motorid] = position;

         MotorMode[] modeArray;
         motorModeToModeArrayMap.TryGetValue(MotorMode.MotorPositionControl, out modeArray);

         execute(getMask(motorid),
                 modeArray,
                 positions,
                 velocities,
                 defaultAccelerationArray);
         }

      public override void setMotorVelocities(int[] velocities)
         {
         MotorMode[] modeArray;
         motorModeToModeArrayMap.TryGetValue(MotorMode.MotorSpeedControl, out modeArray);

         execute(maskAllOn,
                 modeArray,
                 nullIntArray,
                 velocities,
                 defaultAccelerationArray);
         }


      public override void stopMotors(params int[] motorIds)
         {
         bool[] mask;

         if (motorIds == null || motorIds.Length == 0)
            {
            mask = maskAllOn;
            }
         else
            {
            mask = new bool[numMotors];
            foreach (int i in motorIds)
               {
               mask[i] = true;
               }
            }

         MotorMode[] modeArray;
         motorModeToModeArrayMap.TryGetValue(MotorMode.MotorOff, out modeArray);

         execute(mask,
                 modeArray,
                 nullIntArray,
                 nullIntArray,
                 defaultAccelerationArray);
         }

      public override void emergencyStopMotor(int motorid)
         {
         setMotorVelocity(0, motorid);
         }

      private bool[] getMask(int motorid)
         {
         bool[] mask;
         motorIdToMaskArrayMap.TryGetValue(motorid, out mask);
         return mask;
         }
      }
   }