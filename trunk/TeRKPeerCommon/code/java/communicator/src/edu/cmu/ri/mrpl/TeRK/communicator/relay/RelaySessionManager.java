package edu.cmu.ri.mrpl.TeRK.communicator.relay;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface RelaySessionManager
   {
   boolean isLoggedIn();

   /**
    * Attempts to log in to the relay using the given <code>userId</code> and <code>password</code>; returns
    * <code>true</code> if successful, <code>false</code> otherwise.
    */
   boolean login(String userId, String password);

   void logout();
   }
