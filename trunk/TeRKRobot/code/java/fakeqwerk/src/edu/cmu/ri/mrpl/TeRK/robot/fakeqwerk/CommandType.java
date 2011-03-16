package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import Ice.ObjectPrx;

/**
 * <p>
 * <code>CommandType</code> defines the various controller command types.  These types are the Ice type IDs
 * (i.e. the value returned by {@link ObjectPrx#ice_id()}).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
enum CommandType implements Serializable
   {
      AUDIO("::TeRK::AudioCommand"),
      DIGITAL_OUT("::TeRK::DigitalOutCommand"),
      LED("::TeRK::LEDCommand"),
      MOTOR("::TeRK::MotorCommand"),
      SERVO("::TeRK::ServoCommand");

   private static final Map<String, CommandType> TYPE_MAP;

   static
      {
      final HashMap<String, CommandType> typeMap = new HashMap<String, CommandType>();
      typeMap.put(AUDIO.getTypeId(), AUDIO);
      typeMap.put(DIGITAL_OUT.getTypeId(), DIGITAL_OUT);
      typeMap.put(LED.getTypeId(), LED);
      typeMap.put(MOTOR.getTypeId(), MOTOR);
      typeMap.put(SERVO.getTypeId(), SERVO);
      TYPE_MAP = Collections.unmodifiableMap(typeMap);
      }

   private final String typeId;

   /**
    * Returns the <code>CommandType</code> corresponding to the given <code>typeId</code> or <code>null</code> if no
    * such <code>CommandType</code> exists.
    */
   public static CommandType find(final String typeId)
      {
      return TYPE_MAP.get(typeId);
      }

   private CommandType(final String typeId)
      {
      this.typeId = typeId;
      }

   public String getTypeId()
      {
      return typeId;
      }

   public String toString()
      {
      return "CommandType{" +
             "typeId='" + typeId + "'" +
             "}";
      }
   }
