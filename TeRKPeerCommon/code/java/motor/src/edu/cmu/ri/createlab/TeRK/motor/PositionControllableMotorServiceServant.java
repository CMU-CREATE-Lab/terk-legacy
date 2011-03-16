package edu.cmu.ri.createlab.TeRK.motor;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.mrpl.TeRK.motor._PositionControllableMotorServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class PositionControllableMotorServiceServant extends _PositionControllableMotorServiceDisp
   {
   private final PositionControllableMotorServiceServantHelper helper;

   public PositionControllableMotorServiceServant(final PositionControllableMotorServiceServantHelper helper)
      {
      this.helper = helper;
      }

   public String getProperty(final String key, final Current current)
      {
      return helper.getProperty(key);
      }

   public Map<String, String> getProperties(final Current current)
      {
      return helper.getProperties();
      }

   public List<String> getPropertyKeys(final Current current)
      {
      return helper.getPropertyKeys();
      }

   public void setProperty(final String key, final String value, final Current current) throws ReadOnlyPropertyException
      {
      helper.setProperty(key, value);
      }

   public PositionControllableMotorState[] getState(final Current current)
      {
      return helper.getState();
      }

   public void execute(final PositionControllableMotorCommand command, final Current current)
      {
      helper.execute(command);
      }
   }