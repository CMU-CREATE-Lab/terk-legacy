package edu.cmu.ri.createlab.TeRK.buzzer;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.buzzer.BuzzerCommand;
import edu.cmu.ri.mrpl.TeRK.buzzer._BuzzerServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class BuzzerServiceServant extends _BuzzerServiceDisp
   {
   private final BuzzerServiceServantHelper helper;

   public BuzzerServiceServant(final BuzzerServiceServantHelper helper)
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

   public void execute(final int id, final BuzzerCommand command, final Current current)
      {
      helper.execute(id, command);
      }
   }