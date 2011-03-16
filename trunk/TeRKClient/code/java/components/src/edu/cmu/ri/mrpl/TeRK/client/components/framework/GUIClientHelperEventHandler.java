package edu.cmu.ri.mrpl.TeRK.client.components.framework;

import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServiceServantRegistrar;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface GUIClientHelperEventHandler
   {
   /** Perform special behavior upon relay login. */
   void executeAfterRelayLogin();

   /** Perform special behavior upon a failed relay login. */
   void executeAfterFailedRelayLogin();

   /** Perform special behavior upon registration with the relay. */
   void executeAfterRelayRegistrationEvent();

   /** Perform special behavior upon relay logout (including forced logout). */
   void executeAfterRelayLogout();

   /** Perform special behavior upon establishing a connection to a qwerk. */
   void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId);

   /** Perform special behavior before disconnecting from a qwerk. */
   void executeBeforeDisconnectingFromQwerk();

   /** Perform special behavior upon disconnecting from a qwerk. */
   void executeAfterDisconnectingFromQwerk(final String qwerkUserId);

   /** Perform special behavior upon failure to connect to a qwerk. */
   void executeUponFailureToConnectToQwerk(final String qwerkUserId);

   /**
    * Used for toggling GUI element state (such as setting an element as enabled/disabled) based on whether we're
    * connected to a peer.
    */
   void toggleGUIElementState(final boolean isConnectedToPeer);

   /**
    * Creates the secondary servants and their proxies for the services that this client supports.  Also registers the
    * services with the given {@link ServiceServantRegistrar}.  Returns the {@link Set} of servant proxies.
    */
   Set<ObjectPrx> createAndRegisterSecondaryServantsAndReturnTheirProxies(final TerkCommunicator terkCommunicator, final ServiceServantRegistrar serviceServantRegistrar);
   }
