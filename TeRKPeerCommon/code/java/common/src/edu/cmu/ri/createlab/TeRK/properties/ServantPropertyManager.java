package edu.cmu.ri.createlab.TeRK.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import edu.cmu.ri.mrpl.TeRK._PropertyManagerOperationsNC;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>ServantPropertyManager</code> helps servants manage properties.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ServantPropertyManager implements _PropertyManagerOperationsNC
   {
   private static final Logger LOG = Logger.getLogger(ServantPropertyManager.class);

   private final BasicPropertyManager propertyManager = new BasicPropertyManager();

   /** Returns the property named by the given key. */
   public String getProperty(final String key)
      {
      return propertyManager.getProperty(key);
      }

   /** Returns an unmodifiable {@link Map} of property keys and values. */
   public Map<String, String> getProperties()
      {
      return propertyManager.getProperties();
      }

   /** Returns an unmodifiable {@link List} of all property keys. */
   public List<String> getPropertyKeys()
      {
      return Collections.unmodifiableList(new ArrayList<String>(propertyManager.getPropertyKeys()));
      }

   /**
    * Sets a property using the given <code>key</code> to the given <code>value</code>.  Throws a
    * {@link ReadOnlyPropertyException} if the property is read-only.  If the property already exists, it is overwritten
    * with the new <code>value</code>.
    */
   public void setProperty(final String key, final String value) throws edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException
      {
      try
         {
         propertyManager.setProperty(key, value);
         }
      catch (ReadOnlyPropertyException e)
         {
         throw new edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException(e.getMessage());
         }
      }

   /**
    * Sets a read-only property using the given <code>key</code> to the given <code>value</code>. If the property
    * already exists, it is overwritten with the new <code>value</code>.
    */
   public void setReadOnlyProperty(final String key, final String value)
      {
      propertyManager.setReadOnlyProperty(key, value);
      }

   /**
    * Sets a read-only property using the given <code>key</code> to the given <code>value</code> after first converting
    * it to a String. If the property already exists, it is overwritten with the new <code>value</code>.
    */
   public void setReadOnlyProperty(final String key, final int value)
      {
      propertyManager.setReadOnlyProperty(key, value);
      }

   private boolean isReadOnly(final String key)
      {
      return propertyManager.isReadOnly(key);
      }
   }
