package edu.cmu.ri.createlab.TeRK.obstacle;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK.obstacle._SimpleObstacleDetectorServiceDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class SimpleObstacleDetectorServiceServant extends _SimpleObstacleDetectorServiceDisp
   {
   private final SimpleObstacleDetectorServiceServantHelper helper;

   public SimpleObstacleDetectorServiceServant(final SimpleObstacleDetectorServiceServantHelper helper)
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

   public boolean isObstacleDetected(final int id, final Current current)
      {
      return helper.isObstacleDetected(id);
      }

   public boolean[] areObstaclesDetected(final Current current)
      {
      return helper.areObstaclesDetected();
      }
   }