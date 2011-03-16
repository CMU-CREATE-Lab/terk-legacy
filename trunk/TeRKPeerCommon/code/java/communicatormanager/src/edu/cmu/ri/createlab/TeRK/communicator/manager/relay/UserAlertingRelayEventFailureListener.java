package edu.cmu.ri.createlab.TeRK.communicator.manager.relay;

import java.awt.Component;
import java.util.PropertyResourceBundle;
import edu.cmu.ri.createlab.TeRK.userinterface.dialog.AbstractAlert;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.UserConnectionEventListener;

/**
 * The <code>UserAlertingRelayEventFailureListener</code> class  notifies the user of various failures associated with
 * using the relay.  This class adds itself to the {@link RelayCommunicator} as a {@link UserConnectionEventListener}
 * upon the communicator's construction so that this class will handle user connection events.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class UserAlertingRelayEventFailureListener extends AbstractAlert implements TerkCommunicatorCreationEventListener,
                                                                                    UserConnectionEventListener
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(UserAlertingRelayEventFailureListener.class.getName());

   public UserAlertingRelayEventFailureListener(final Component parentComponent)
      {
      super(parentComponent);
      }

   /** Does nothing. */
   public void beforeConstruction()
      {
      // do nothing
      }

   /**
    * Registers this instance with the {@link RelayCommunicator} so that user conection events will be handled
    * by this instance.
    *
    * @see UserConnectionEventListener
    */
   public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
      {
      if (terkCommunicator != null)
         {
         ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(this);
         }
      }

   /** Displays an alert to the user notifying him/her of the failed construction of the {@link RelayCommunicator}. */
   public void afterFailedConstruction()
      {
      showAlert(RESOURCES.getString("dialog.title.relay-communicator-creation-failed"),
                RESOURCES.getString("dialog.message.relay-communicator-creation-failed"));
      }

   /** Does nothing. */
   public void beforeWaitForShutdown()
      {
      // does nothing
      }

   /** Does nothing. */
   public void afterWaitForShutdown()
      {
      // does nothing
      }

   /** Does nothing. */
   public void handleRelayLoginEvent()
      {
      // does nothing
      }

   /** Displays an alert to the user notifying him/her of the failed login into the {@link RelayCommunicator}. */
   public void handleFailedRelayLoginEvent()
      {
      showAlert(RESOURCES.getString("dialog.title.login-failed"),
                RESOURCES.getString("dialog.message.login-failed"));
      }

   /** Does nothing. */
   public void handleRelayRegistrationEvent()
      {
      // does nothing
      }

   /** Does nothing. */
   public void handleRelayLogoutEvent()
      {
      // does nothing
      }

   /** Displays an alert to the user notifying him/her of the forced logout from the {@link RelayCommunicator}. */
   public void handleForcedLogoutNotificationEvent()
      {
      showAlert(RESOURCES.getString("dialog.title.logout-forced"),
                RESOURCES.getString("dialog.message.logout-forced"));
      }
   }
