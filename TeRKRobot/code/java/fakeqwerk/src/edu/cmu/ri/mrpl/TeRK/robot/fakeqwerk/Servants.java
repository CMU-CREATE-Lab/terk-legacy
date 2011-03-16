package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.QwerkPrx;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class Servants
   {
   private final FakeQwerkServant mainServant;
   private final QwerkPrx mainServantProxy;
   private final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

   Servants(final FakeQwerkServant mainServant, final QwerkPrx mainServantProxy, final Set<ObjectPrx> secondaryServants)
      {
      this.mainServant = mainServant;
      this.mainServantProxy = mainServantProxy;
      if ((secondaryServants != null) && (!secondaryServants.isEmpty()))
         {
         this.secondaryServantProxies.addAll(secondaryServants);
         }
      }

   FakeQwerkServant getMainServant()
      {
      return mainServant;
      }

   QwerkPrx getMainServantProxy()
      {
      return mainServantProxy;
      }

   Set<ObjectPrx> getSecondaryServantProxies()
      {
      return Collections.unmodifiableSet(secondaryServantProxies);
      }
   }
