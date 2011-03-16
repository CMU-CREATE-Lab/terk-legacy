package edu.cmu.ri.createlab.TeRK.serial;

import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialIOService extends Service
   {
   String TYPE_ID = "::TeRK::SerialIOService";

   /** Returns the {@link SerialPort} associated with the given <code>serialPortDeviceName</code>. */
   SerialPort getSerialPort(final String serialPortDeviceName);
   }
