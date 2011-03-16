package edu.cmu.ri.createlab.TeRK.finch;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.finch.FinchState;
import edu.cmu.ri.mrpl.TeRK.finch._FinchServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class FinchServiceServant extends _FinchServiceDisp
   {
   private final FinchServiceServantHelper helper;

   public FinchServiceServant(final FinchServiceServantHelper helper)
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

   public FinchState getState(final Current current)
      {
      return helper.getState();
      }

   public void emergencyStop(final Current current)
      {
      helper.emergencyStop();
      }
   }