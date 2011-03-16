package edu.cmu.ri.createlab.TeRK.motor;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.motor.VelocityControllableMotorCommand;
import edu.cmu.ri.mrpl.TeRK.motor._VelocityControllableMotorServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class VelocityControllableMotorServiceServant extends _VelocityControllableMotorServiceDisp
   {
   private final VelocityControllableMotorServiceServantHelper helper;

   public VelocityControllableMotorServiceServant(final VelocityControllableMotorServiceServantHelper helper)
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

   public int[] execute(final VelocityControllableMotorCommand command, final Current current)
      {
      return helper.execute(command);
      }
   }