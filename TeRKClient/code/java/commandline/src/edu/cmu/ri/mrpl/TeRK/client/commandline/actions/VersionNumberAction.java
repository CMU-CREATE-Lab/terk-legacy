package edu.cmu.ri.mrpl.TeRK.client.commandline.actions;

import edu.cmu.ri.mrpl.util.VersionNumberReader;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class VersionNumberAction implements Action
   {
   public void execute()
      {
      System.out.println("TeRK Client build #" + VersionNumberReader.getVersionNumber());
      }
   }
