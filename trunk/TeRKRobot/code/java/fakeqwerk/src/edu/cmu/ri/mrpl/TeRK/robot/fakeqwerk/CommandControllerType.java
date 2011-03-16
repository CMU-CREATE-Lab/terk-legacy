package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;

/**
 * <p>
 * <code>CommandControllerType</code> defines the various command controller types.  These types are the Ice type IDs
 * (i.e. the value returned by {@link ObjectPrx#ice_id()}).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
enum CommandControllerType implements Serializable
   {
      AUDIO("::TeRK::AudioController"),
      DIGITAL_OUT("::TeRK::DigitalOutController"),
      LED("::TeRK::LEDController"),
      MOTOR("::TeRK::MotorController"),
      SERVO("::TeRK::ServoController"),
      VIDEO("::TeRK::VideoStreamerServer");

   private static final Map<String, CommandControllerType> TYPE_MAP;

   static
      {
      final HashMap<String, CommandControllerType> typeMap = new HashMap<String, CommandControllerType>();
      typeMap.put(AUDIO.getTypeId(), AUDIO);
      typeMap.put(DIGITAL_OUT.getTypeId(), DIGITAL_OUT);
      typeMap.put(LED.getTypeId(), LED);
      typeMap.put(MOTOR.getTypeId(), MOTOR);
      typeMap.put(SERVO.getTypeId(), SERVO);
      typeMap.put(VIDEO.getTypeId(), VIDEO);
      TYPE_MAP = Collections.unmodifiableMap(typeMap);
      }

   private final String typeId;

   /**
    * Returns the <code>CommandControllerType</code> corresponding to the given <code>typeId</code> or <code>null</code>
    * if no such <code>CommandControllerType</code> exists.
    */
   public static CommandControllerType find(final String typeId)
      {
      return TYPE_MAP.get(typeId);
      }

   private CommandControllerType(final String typeId)
      {
      this.typeId = typeId;
      }

   public String getTypeId()
      {
      return typeId;
      }

   public String toString()
      {
      return "CommandControllerType{" +
             "typeId='" + typeId + "'" +
             "}";
      }
   }
