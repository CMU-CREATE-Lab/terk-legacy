package edu.cmu.ri.mrpl.TeRK.servants;

import Ice.Current;
import edu.cmu.ri.mrpl.TeRK._TerkUserOperations;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface TerkUserServantHelper extends _TerkUserOperations, ServiceServantRegistrar

   {
   /**
    * Called when the user has been forced to logout.  Implementations may assume that this method is called
    * asynchronously.
    */
   void forcedLogoutNotification(final Current current);
   }