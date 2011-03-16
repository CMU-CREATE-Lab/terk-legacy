package edu.cmu.ri.createlab.TeRK.communicator.manager.relay;

import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManager;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicatorProvider;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RelayCommunicatorManager extends TerkCommunicatorManager,
                                                  RelayCommunicatorProvider
   {
   boolean isLoggedIn();

   boolean login(final String username, final String password);

   void logout();
   }
