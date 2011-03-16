package edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect;

import java.awt.Component;
import java.util.PropertyResourceBundle;
import edu.cmu.ri.createlab.TeRK.userinterface.dialog.AbstractAlert;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;

/**
 * The <code>UserAlertingDirectConnectEventFailureListener</code> class  notifies the user of a failure to create the
 * {@link DirectConnectCommunicator}.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class UserAlertingDirectConnectEventFailureListener extends AbstractAlert implements TerkCommunicatorCreationEventListener
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(UserAlertingDirectConnectEventFailureListener.class.getName());

   public UserAlertingDirectConnectEventFailureListener(final Component parentComponent)
      {
      super(parentComponent);
      }

   /** Does nothing. */
   public void beforeConstruction()
      {
      // do nothing
      }

   /** Does nothing. */
   public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
      {
      // does nothing
      }

   /** Displays an alert to the user notifying him/her of the failed construction of the {@link DirectConnectCommunicator}. */
   public void afterFailedConstruction()
      {
      showAlert(RESOURCES.getString("dialog.title.direct-connect-communicator-creation-failed"),
                RESOURCES.getString("dialog.message.direct-connect-communicator-creation-failed"));
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
   }