package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.util.PropertyResourceBundle;
import javax.swing.SwingUtilities;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.DirectConnectDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerConnectionMethodDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.RelayLoginFormDescriptor;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class NetworkedFinchGUIConnectionStrategy extends NetworkedFinchConnectionStrategy
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(NetworkedFinchGUIConnectionStrategy.class.getName());

   private final Wizard connectToRobotWizard;

   public NetworkedFinchGUIConnectionStrategy()
      {
      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(null);
      getRelayCommunicatorManager().addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);

      // Create the connection-to-robot wizard
      connectToRobotWizard = new Wizard();
      connectToRobotWizard.getDialog().setTitle(RESOURCES.getString("peer-connection-wizard.title"));

      // create the various pages in the wizard
      final WizardPanelDescriptor wizardDescriptorPeerConnectionMethod = new PeerConnectionMethodDescriptor();
      final DirectConnectDescriptor wizardDescriptorDirectConnect = new DirectConnectDescriptor(getDirectConnectCommunicatorManager());
      final RelayLoginFormDescriptor wizardDescriptorRelayLoginForm = new RelayLoginFormDescriptor(getRelayCommunicatorManager());
      final PeerChooserDescriptor wizardDescriptorPeerChooser = new PeerChooserDescriptor(getRelayCommunicatorManager());

      // register the pages
      connectToRobotWizard.registerWizardPanel(PeerConnectionMethodDescriptor.IDENTIFIER, wizardDescriptorPeerConnectionMethod);
      connectToRobotWizard.registerWizardPanel(DirectConnectDescriptor.IDENTIFIER, wizardDescriptorDirectConnect);
      connectToRobotWizard.registerWizardPanel(RelayLoginFormDescriptor.IDENTIFIER, wizardDescriptorRelayLoginForm);
      connectToRobotWizard.registerWizardPanel(PeerChooserDescriptor.IDENTIFIER, wizardDescriptorPeerChooser);
      }

   public void connect()
      {
      // show the wizard
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               // determine which screen to display in the wizard
               if (getRelayCommunicatorManager().isCreated())
                  {
                  if (getRelayCommunicatorManager().isLoggedIn())
                     {
                     connectToRobotWizard.setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
                     }
                  else
                     {
                     connectToRobotWizard.setCurrentPanel(RelayLoginFormDescriptor.IDENTIFIER);
                     }
                  }
               else if (getDirectConnectCommunicatorManager().isCreated())
                  {
                  connectToRobotWizard.setCurrentPanel(DirectConnectDescriptor.IDENTIFIER);
                  }
               else
                  {
                  connectToRobotWizard.setCurrentPanel(PeerConnectionMethodDescriptor.IDENTIFIER);
                  }

               connectToRobotWizard.showModalDialog();
               }
            });
      }
   }