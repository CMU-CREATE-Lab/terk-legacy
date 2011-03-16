package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

/**
 * <p>
 * <code>CommandAndControllerMapper</code> maps {@link CommandType}s to {@link CommandControllerType}s (and vice versa).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface CommandAndControllerMapper
   {
   CommandType getCommandType(CommandControllerType commandControllerType);

   CommandControllerType getCommandControllerType(CommandType commandType);
   }