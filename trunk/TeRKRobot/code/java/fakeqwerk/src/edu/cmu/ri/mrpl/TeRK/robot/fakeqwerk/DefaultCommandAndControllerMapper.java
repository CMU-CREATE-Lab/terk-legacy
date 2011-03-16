package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DefaultCommandAndControllerMapper implements CommandAndControllerMapper
   {
   private static final DefaultCommandAndControllerMapper INSTANCE = new DefaultCommandAndControllerMapper();

   private static final Map<CommandType, CommandControllerType> COMMAND_TO_CONTROLLER_MAP;
   private static final Map<CommandControllerType, CommandType> CONTROLLER_TO_COMMAND_MAP;

   static
      {
      final Map<CommandType, CommandControllerType> commandToControllerMap = new HashMap<CommandType, CommandControllerType>();
      commandToControllerMap.put(CommandType.AUDIO, CommandControllerType.AUDIO);
      commandToControllerMap.put(CommandType.DIGITAL_OUT, CommandControllerType.DIGITAL_OUT);
      commandToControllerMap.put(CommandType.LED, CommandControllerType.LED);
      commandToControllerMap.put(CommandType.MOTOR, CommandControllerType.MOTOR);
      commandToControllerMap.put(CommandType.SERVO, CommandControllerType.SERVO);
      COMMAND_TO_CONTROLLER_MAP = Collections.unmodifiableMap(commandToControllerMap);

      final Map<CommandControllerType, CommandType> controllerToCommandMap = new HashMap<CommandControllerType, CommandType>();
      controllerToCommandMap.put(CommandControllerType.AUDIO, CommandType.AUDIO);
      controllerToCommandMap.put(CommandControllerType.DIGITAL_OUT, CommandType.DIGITAL_OUT);
      controllerToCommandMap.put(CommandControllerType.LED, CommandType.LED);
      controllerToCommandMap.put(CommandControllerType.MOTOR, CommandType.MOTOR);
      controllerToCommandMap.put(CommandControllerType.SERVO, CommandType.SERVO);
      controllerToCommandMap.put(CommandControllerType.VIDEO, null);
      CONTROLLER_TO_COMMAND_MAP = Collections.unmodifiableMap(controllerToCommandMap);
      }

   public static DefaultCommandAndControllerMapper getInstance()
      {
      return INSTANCE;
      }

   private DefaultCommandAndControllerMapper()
      {
      // private to prevent instantiation
      }

   public CommandType getCommandType(final CommandControllerType commandControllerType)
      {
      return CONTROLLER_TO_COMMAND_MAP.get(commandControllerType);
      }

   public CommandControllerType getCommandControllerType(final CommandType commandType)
      {
      return COMMAND_TO_CONTROLLER_MAP.get(commandType);
      }
   }
