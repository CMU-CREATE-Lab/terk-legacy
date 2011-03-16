package edu.cmu.ri.mrpl.TeRK.servants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import Ice.Current;
import Ice.Identity;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import Ice.Util;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>AbstractTerkUserServantHelper</code> provides some base functionality for all {@link TerkUserServantHelper}
 * implementations.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractTerkUserServantHelper extends AbstractServiceServant implements TerkUserServantHelper
   {
   private static final Logger LOG = Logger.getLogger(AbstractTerkUserServantHelper.class);

   /** Map of supported services */
   private final Map<String, Identity> commandControllerTypeToProxyIdentityMap = Collections.synchronizedMap(new HashMap<String, Identity>());

   public final Map<String, Identity> getSupportedServices(final Current current)
      {
      return new HashMap<String, Identity>(commandControllerTypeToProxyIdentityMap);
      }

   public final void registerServiceServant(final ObjectImpl serviceServant, final ObjectPrx serviceServantProxy)
      {
      if ((serviceServant != null) && (serviceServantProxy != null))
         {
         final String typeId = serviceServant.ice_id();
         final Identity identity = serviceServantProxy.ice_getIdentity();
         if (LOG.isDebugEnabled())
            {
            LOG.debug("AbstractTerkUserServantHelper.registerServiceServant() is registering type id [" + typeId + "] to identity [" + Util.identityToString(identity) + "]");
            }
         commandControllerTypeToProxyIdentityMap.put(typeId, identity);
         }
      }
   }
