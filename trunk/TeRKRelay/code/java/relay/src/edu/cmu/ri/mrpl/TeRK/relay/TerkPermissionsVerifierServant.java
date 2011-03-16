package edu.cmu.ri.mrpl.TeRK.relay;

import Glacier2._PermissionsVerifierDisp;
import Ice.Current;
import Ice.StringHolder;
import edu.cmu.ri.mrpl.ice.util.IceUtil;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TerkPermissionsVerifierServant extends _PermissionsVerifierDisp
   {
   private static final Logger LOG = Logger.getLogger(TerkPermissionsVerifierServant.class);

   public boolean checkPermissions(final String userId, final String password, final StringHolder reason, final Current current)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("TerkPermissionsVerifierServant.checkPermissions()" + IceUtil.dumpCurrentToString(current));
         }
      return AuthenticationService.getInstance().isValidLogin(userId, password);
      }
   }
