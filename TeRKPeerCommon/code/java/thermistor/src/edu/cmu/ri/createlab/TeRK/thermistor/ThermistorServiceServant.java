package edu.cmu.ri.createlab.TeRK.thermistor;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.thermistor.ThermistorException;
import edu.cmu.ri.mrpl.TeRK.thermistor._ThermistorServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class ThermistorServiceServant extends _ThermistorServiceDisp
   {
   private final ThermistorServiceServantHelper helper;

   public ThermistorServiceServant(final ThermistorServiceServantHelper helper)
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

   public int getValue(final int id, final Current current) throws ThermistorException
      {
      return helper.getValue(id);
      }

   public int[] getValues(final Current current) throws ThermistorException
      {
      return helper.getValues();
      }
   }