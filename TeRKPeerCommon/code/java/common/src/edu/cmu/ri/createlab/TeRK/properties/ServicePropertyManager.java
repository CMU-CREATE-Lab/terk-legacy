package edu.cmu.ri.createlab.TeRK.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import Ice.OperationNotExistException;
import edu.cmu.ri.mrpl.TeRK.PropertyManagerPrx;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ServicePropertyManager implements PropertyManager
   {
   private static final Logger LOG = Logger.getLogger(ServicePropertyManager.class);

   private final PropertyManagerPrx proxy;

   public ServicePropertyManager(final PropertyManagerPrx proxy)
      {
      this.proxy = proxy;
      }

   public String getProperty(final String key)
      {
      return proxy.getProperty(key);
      }

   public Integer getPropertyAsInteger(final String key)
      {
      final int i;
      try
         {
         i = Integer.parseInt(getProperty(key));
         }
      catch (NumberFormatException e)
         {
         return null;
         }
      return i;
      }

   public Map<String, String> getProperties()
      {
      final Map<String, String> properties = new HashMap<String, String>();
      try
         {
         properties.putAll(proxy.getProperties());
         }
      catch (OperationNotExistException e)
         {
         LOG.info("The getProperties() method doesn't exist for this proxy.  Using getPropertyKeys() instead.");
         final Set<String> keys = getPropertyKeys();
         for (final String key : keys)
            {
            properties.put(key, proxy.getProperty(key));
            }
         }
      return Collections.unmodifiableMap(properties);
      }

   public Set<String> getPropertyKeys()
      {
      return Collections.unmodifiableSet(new HashSet<String>(proxy.getPropertyKeys()));
      }

   public void setProperty(final String key, final String value) throws ReadOnlyPropertyException
      {
      try
         {
         proxy.setProperty(key, value);
         }
      catch (edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException e)
         {
         throw new ReadOnlyPropertyException(e);
         }
      }

   public void setProperty(final String key, final int value) throws ReadOnlyPropertyException
      {
      setProperty(key, String.valueOf(value));
      }
   }
