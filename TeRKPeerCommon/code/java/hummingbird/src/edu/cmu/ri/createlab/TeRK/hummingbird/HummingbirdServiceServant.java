package edu.cmu.ri.createlab.TeRK.hummingbird;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.hummingbird._HummingbirdServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class HummingbirdServiceServant extends _HummingbirdServiceDisp
   {
   private final HummingbirdServiceServantHelper helper;

   public HummingbirdServiceServant(final HummingbirdServiceServantHelper helper)
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

   public HummingbirdState getState(final Current current)
      {
      return helper.getState();
      }

   public void emergencyStop(final Current current)
      {
      helper.emergencyStop();
      }
   }
