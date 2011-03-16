package edu.cmu.ri.mrpl.TeRK.servants;

import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ServantFactory
   {
   Servants createServants(final TerkCommunicator terkCommunicator);
   }