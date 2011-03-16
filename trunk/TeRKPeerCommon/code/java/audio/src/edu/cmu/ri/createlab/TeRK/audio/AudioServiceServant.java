package edu.cmu.ri.createlab.TeRK.audio;

import java.util.List;
import java.util.Map;
import Ice.Current;
import edu.cmu.ri.mrpl.TeRK.AudioCommand;
import edu.cmu.ri.mrpl.TeRK.AudioCommandException;
import edu.cmu.ri.mrpl.TeRK.ReadOnlyPropertyException;
import edu.cmu.ri.mrpl.TeRK._AudioControllerDisp;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public class AudioServiceServant extends _AudioControllerDisp
   {
   private final AudioServiceServantHelper helper;

   public AudioServiceServant(final AudioServiceServantHelper helper)
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

   public void execute(final AudioCommand command, final Current current) throws AudioCommandException
      {
      helper.execute(command);
      }
   }