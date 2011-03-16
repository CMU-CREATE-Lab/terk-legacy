package edu.cmu.ri.mrpl.TeRK.client.components.framework;

import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServiceServantRegistrar;

/**
 * <p>
 * <code>GUIClientHelperEventHandlerAdapter</code> is an abstract adapter class for handling GUIClientHelper events.
 * The methods in this class are empty. This class exists as convenience for creating listener objects.
 * </p>
 * <p>
 * Extend this class to create a {@link GUIClientHelperEventHandler} and override the methods for the events of
 * interest. (If you implement the {@link GUIClientHelperEventHandler} interface, you have to define all of the methods
 * in it. This abstract class defines no-op methods for them all, so you only have to define methods for events you care
 * about.)
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class GUIClientHelperEventHandlerAdapter implements GUIClientHelperEventHandler
   {
   public void executeAfterRelayLogin()
      {
      }

   public void executeAfterFailedRelayLogin()
      {
      }

   public void executeAfterRelayRegistrationEvent()
      {
      }

   public void executeAfterRelayLogout()
      {
      }

   public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
      {
      }

   public void executeBeforeDisconnectingFromQwerk()
      {
      }

   public void executeAfterDisconnectingFromQwerk(final String qwerkUserId)
      {
      }

   public void executeUponFailureToConnectToQwerk(final String qwerkUserId)
      {
      }

   public void toggleGUIElementState(final boolean isConnectedToPeer)
      {
      }

   public Set<ObjectPrx> createAndRegisterSecondaryServantsAndReturnTheirProxies(final TerkCommunicator terkCommunicator, final ServiceServantRegistrar serviceServantRegistrar)
      {
      return null;
      }
   }
