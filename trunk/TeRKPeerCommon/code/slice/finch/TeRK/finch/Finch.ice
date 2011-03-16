#ifndef TERK_FINCH_ICE
#define TERK_FINCH_ICE

#include <TeRK/accelerometer/Accelerometer.ice>
#include <TeRK/color/Color.ice>
#include <TeRK/motor/PositionControllableMotor.ice>
#include <TeRK/TeRKCommon.ice>

[["java:java5"]]
[["java:package:edu.cmu.ri.mrpl"]]
module TeRK
   {
   module finch
      {
      struct FinchState
         {
         color::RGBColor                              fullColorLedColor;
         accelerometer::AccelerometerState            accelerometerValues;
         motor::PositionControllableMotorStateArray   positionControllableMotorStates;    // array index 0 = left, 1 = right
         IntArray                                     velocityControllableMotorStates;    // array index 0 = left, 1 = right
         int                                          thermistorValue;
         IntArray                                     photoresistors;                     // array index 0 = left, 1 = right
         BooleanArray                                 isObstacleDetected;                 // array index 0 = left, 1 = right
         };

      interface FinchService extends AbstractCommandController
         {
         ["ami"] nonmutating FinchState getState();
         ["ami"] idempotent void emergencyStop();
         };
      };
   };

#endif
