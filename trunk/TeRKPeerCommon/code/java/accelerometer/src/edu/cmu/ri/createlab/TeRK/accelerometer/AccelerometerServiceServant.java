package edu.cmu.ri.createlab.TeRK.accelerometer;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.accelerometer._AccelerometerServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class AccelerometerServiceServant extends _AccelerometerServiceDisp
   {
   private final AccelerometerServiceServantHelper helper;

   public AccelerometerServiceServant(final AccelerometerServiceServantHelper helper)
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

   public AccelerometerState getState(final int id, final Current current)
      {
      return helper.getState(id);
      }

   public AccelerometerState[] getStates(final Current current)
      {
      return helper.getStates();
      }
   }