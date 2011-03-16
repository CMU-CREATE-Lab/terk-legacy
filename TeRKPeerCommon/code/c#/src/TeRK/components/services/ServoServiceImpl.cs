using System;
using System.Collections.Generic;

namespace TeRK.components.services
   {
   internal class ServoServiceImpl : ServoService
      {
      private const int SERVO_MIN_POSITION = 10;
      private const int SERVO_DEFAULT_POSITION = 127;
      private const int SERVO_MAX_POSITION = 245;
      private const int SERVO_DEFAULT_VELOCITY = 1000;

      private readonly ServoControllerPrx proxy;
      private readonly int numServos;
      private readonly bool[] maskAllOn;
      private readonly IDictionary<int, bool[]> servoIdToMaskArrayMap;
      private readonly IDictionary<ServoMode, ServoMode[]> servoModeToModeArrayMap;
      private readonly int[] allZeros;
      private readonly int[] allDefaultPosition;
      private readonly int[] defaultVelocities;

      internal ServoServiceImpl(ServoControllerPrx proxy)
         {
         this.proxy = proxy;
         numServos = 16; // todo: this should be retrieved from the qwerk

         // create and initialize the all-on mask array
         maskAllOn = new bool[numServos];
         for (int i = 0; i < maskAllOn.Length; i++)
            {
            maskAllOn[i] = true;
            }

         // create the array used for zero velocity or acceleration
         allZeros = new int[numServos];
         allDefaultPosition = new int[numServos];
         defaultVelocities = new int[numServos];
         for (int i = 0; i < numServos; i++)
            {
            allDefaultPosition[i] = SERVO_DEFAULT_POSITION;
            defaultVelocities[i] = SERVO_DEFAULT_VELOCITY;
            }

         // build the mask arrays for each servo and store them in a map indexed on servo id
         servoIdToMaskArrayMap = new Dictionary<int, bool[]>(numServos);
         for (int i = 0; i < numServos; i++)
            {
            bool[] mask = new bool[numServos];
            mask[i] = true;
            servoIdToMaskArrayMap.Add(i, mask);
            }


         // build the servo mode arrays and store them in a map indexed on servo mode
         servoModeToModeArrayMap = new Dictionary<ServoMode, ServoMode[]>(numServos);
         ServoMode[] servoModeOffArray = new ServoMode[numServos];
         ServoMode[] servoModePositionControlArray = new ServoMode[numServos];
         ServoMode[] servoModeSpeedControlArray = new ServoMode[numServos];
         for (int i = 0; i < numServos; i++)
            {
            servoModeOffArray[i] = ServoMode.ServoOff;
            servoModePositionControlArray[i] = ServoMode.ServoMotorPositionControl;
            servoModeSpeedControlArray[i] = ServoMode.ServoMotorSpeedControl;
            }

         servoModeToModeArrayMap.Add(ServoMode.ServoOff, servoModeOffArray);
         servoModeToModeArrayMap.Add(ServoMode.ServoMotorPositionControl, servoModePositionControlArray);
         servoModeToModeArrayMap.Add(ServoMode.ServoMotorSpeedControl, servoModeSpeedControlArray);
         }

      public override void execute(bool[] servoMask, ServoMode[] servoModes, int[] servoPositions, int[] servoSpeeds)
         {
         execute(new ServoCommand(servoMask, servoModes, servoPositions, servoSpeeds));
         }

      public override void execute(ServoCommand cmd)
         {
         try
            {
            proxy.execute(cmd);
            }
         catch (Exception e)
            {
            // todo: allow this to propogate to the caller
            Console.Error.WriteLine("ERROR: Error in executing servo command: {0}", e.StackTrace);
            }
         }

      public override void setPosition(int position, int servoid)
         {
         //TODO: calibrate to appropriate units
         int[] positions = new int[numServos];
         positions[servoid] = position;

         ServoMode[] modeArray;
         servoModeToModeArrayMap.TryGetValue(ServoMode.ServoMotorPositionControl, out modeArray);
         execute(getMask(servoid), modeArray, positions, defaultVelocities);
         }

      public override void setPositionWithVelocity(int position, int velocity, int servoid)
         {
         //TODO: calibrate to appropriate units
         int[] positions = new int[numServos];
         int[] velocities = new int[numServos];
         positions[servoid] = position;
         velocities[servoid] = velocity;

         ServoMode[] modeArray;
         servoModeToModeArrayMap.TryGetValue(ServoMode.ServoMotorSpeedControl, out modeArray);
         execute(getMask(servoid), modeArray, positions, defaultVelocities);
         }

      public override void stopServo(int servoid)
         {
         int[] positions = new int[numServos];
         positions[servoid] = 0;

         ServoMode[] modeArray;
         servoModeToModeArrayMap.TryGetValue(ServoMode.ServoOff, out modeArray);
         execute(getMask(servoid), modeArray, positions, defaultVelocities);
         }

      public override void emergencyStopServo(int servoid)
         {
         setPosition(0, servoid);
         }

      public override void setServoVelocities(int[] velocities)
         {
         ServoMode[] modeArray;
         servoModeToModeArrayMap.TryGetValue(ServoMode.ServoMotorPositionControl, out modeArray);

         execute(maskAllOn,
                 modeArray,
                 determineTargetPositionsFromVelocities(velocities),
                 velocities);
         }

      public override void stopServos()
         {
         ServoMode[] modeArray;
         servoModeToModeArrayMap.TryGetValue(ServoMode.ServoMotorPositionControl, out modeArray);

         execute(maskAllOn, modeArray, allDefaultPosition, allZeros);
         }

      private bool[] getMask(int servoid)
         {
         bool[] mask;
         servoIdToMaskArrayMap.TryGetValue(servoid, out mask);
         return mask;
         }

      /** Looks at each velocity and determines the position to go towards based on the velocity's sign. */

      private int[] determineTargetPositionsFromVelocities(int[] velocities)
         {
         int[] positions = new int[velocities.Length];

         for (int i = 0; i < velocities.Length; i++)
            {
            if (velocities[i] < 0)
               {
               positions[i] = SERVO_MIN_POSITION;
               }
            else if (velocities[i] > 0)
               {
               positions[i] = SERVO_MAX_POSITION;
               }
            else
               {
               positions[i] = 0;
               }
            }

         return positions;
         }
      }
   }