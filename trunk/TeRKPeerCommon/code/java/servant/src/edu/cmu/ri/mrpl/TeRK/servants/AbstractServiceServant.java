package edu.cmu.ri.mrpl.TeRK.servants;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.createlab.TeRK.properties.ServantPropertyManager;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK._PropertyManagerOperations;
import edu.cmu.ri.mrpl.TeRK._PropertyManagerOperationsNC;

/**
 * <p>
 * <code>AbstractServiceServant</code> provides base functionality common to all service servants.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class AbstractServiceServant implements _PropertyManagerOperations, _PropertyManagerOperationsNC
   {
   private final ServantPropertyManager propertyManager = new ServantPropertyManager();

   public final String getProperty(final String key, final Current current)
      {
      return getProperty(key);
      }

   public final Map<String, String> getProperties(final Current current)
      {
      return getProperties();
      }

   public final List<String> getPropertyKeys(final Current current)
      {
      return getPropertyKeys();
      }

   public final void setProperty(final String key, final String value, final Current current) throws ReadOnlyPropertyException
      {
      setProperty(key, value);
      }

   public final String getProperty(final String key)
      {
      return propertyManager.getProperty(key);
      }

   public final Map<String, String> getProperties()
      {
      return propertyManager.getProperties();
      }

   public final List<String> getPropertyKeys()
      {
      return propertyManager.getPropertyKeys();
      }

   public final void setProperty(final String key, final String value) throws ReadOnlyPropertyException
      {
      propertyManager.setProperty(key, value);
      }

   protected final void setReadOnlyProperty(final String key, final String value)
      {
      propertyManager.setReadOnlyProperty(key, value);
      }

   protected final void setReadOnlyProperty(final String key, final int value)
      {
      propertyManager.setReadOnlyProperty(key, value);
      }
   }
