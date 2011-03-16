package edu.cmu.ri.mrpl.TeRK.servants;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.peer.ConnectionEventHandlerPrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Servants
   {
   private final TerkUserPrx mainServantProxy;
   private final ConnectionEventHandlerPrx connectionEventHandlerProxy;
   private final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

   public Servants(final TerkUserPrx mainServantProxy,
                   final ConnectionEventHandlerPrx connectionEventHandlerProxy)
      {
      this(mainServantProxy, connectionEventHandlerProxy, null);
      }

   public Servants(final TerkUserPrx mainServantProxy,
                   final ConnectionEventHandlerPrx connectionEventHandlerProxy,
                   final Set<ObjectPrx> secondaryServantProxies)
      {
      this.mainServantProxy = mainServantProxy;
      this.connectionEventHandlerProxy = connectionEventHandlerProxy;
      if ((secondaryServantProxies != null) && (!secondaryServantProxies.isEmpty()))
         {
         this.secondaryServantProxies.addAll(secondaryServantProxies);
         }
      }

   public TerkUserPrx getMainServantProxy()
      {
      return mainServantProxy;
      }

   public ConnectionEventHandlerPrx getConnectionEventHandlerProxy()
      {
      return connectionEventHandlerProxy;
      }

   public Set<ObjectPrx> getSecondaryServantProxies()
      {
      return Collections.unmodifiableSet(secondaryServantProxies);
      }
   }
