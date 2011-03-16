#ifndef TERK_QWERK_ICE
#define TERK_QWERK_ICE

#include <TeRK/AnalogIn.ice>
#include <TeRK/Audio.ice>
#include <TeRK/DigitalIn.ice>
#include <TeRK/DigitalOut.ice>
#include <TeRK/LED.ice>
#include <TeRK/Motor.ice>
#include <TeRK/Servo.ice>
#include <TeRK/TeRKCommon.ice>

[["java:java5"]]
[["java:package:edu.cmu.ri.mrpl"]]
module TeRK
   {
   struct BatteryState
      {
      int batteryVoltage;
      };

   struct ButtonState
      {
      BooleanArray buttonStates;       // buttons are not latched, so this just returns instantaneous value
      };

   struct QwerkState
      {
      AnalogInState    analogIn;
      BatteryState     battery;
      ButtonState      button;
      DigitalInState   digitalIn;
      MotorState       motor;
      ServoState       servo;
      };

   struct QwerkCommand
      {
      AudioCommand        audioCmd;
      DigitalOutCommand   digitalOutCmd;
      LEDCommand          ledCmd;
      MotorCommand        motorCmd;
      ServoCommand        servoCmd;
      };

   interface Qwerk extends TerkUser
      {
      ["ami"] nonmutating QwerkState getState();
      ["ami"] QwerkState execute(QwerkCommand command) throws CommandException;
      ["ami"] idempotent QwerkState emergencyStop();

      // deprecated, use getSupportedServices() instead
      ["ami"] nonmutating ProxyTypeIdToIdentityMap getCommandControllerTypeToProxyIdentityMap();
      };
   };

#endif
