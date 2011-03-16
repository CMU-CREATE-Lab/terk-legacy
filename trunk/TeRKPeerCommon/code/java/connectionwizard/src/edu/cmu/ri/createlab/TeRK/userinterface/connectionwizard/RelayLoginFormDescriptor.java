package edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelaySessionManager;
import edu.cmu.ri.mrpl.peer.ConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.ConnectionEventListener;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RelayLoginFormDescriptor extends WizardPanelDescriptor implements ActionListener
   {
   private static final Logger LOG = Logger.getLogger(RelayLoginFormDescriptor.class);

   public static final String IDENTIFIER = RelayLoginForm.class.getName();

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RelayLoginFormDescriptor.class.getName());
   private final RelayLoginForm panel = new RelayLoginForm(this, RESOURCES);
   private final ConnectionEventListener connectionEventListener = new MyConnectionEventListener();
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final boolean isDirectConnectSupported;
   private boolean isCurrentlyVisible = false;
   private final Runnable updateWidgetsAndSetFocusRunnable =
         new Runnable()
         {
         public void run()
            {
            updateWidgetsAccordingToLoginState();
            setFocusAppropriately();
            }
         };

   private final Runnable updateWidgetsRunnable =
         new Runnable()
         {
         public void run()
            {
            updateWidgetsAccordingToLoginState();
            }
         };

   public RelayLoginFormDescriptor(final RelayCommunicatorManager relayCommunicatorManager)
      {
      this(relayCommunicatorManager, true);
      }

   public RelayLoginFormDescriptor(final RelayCommunicatorManager relayCommunicatorManager, final boolean isDirectConnectSupported)
      {
      this.relayCommunicatorManager = relayCommunicatorManager;
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyRelayCommunicatorCreationEventAdapater());

      this.isDirectConnectSupported = isDirectConnectSupported;
      setPanelDescriptorIdentifier(IDENTIFIER);
      setPanelComponent(panel);
      }

   private RelaySessionManager getRelaySessionManager()
      {
      return (RelaySessionManager)relayCommunicatorManager.getTerkCommunicator();
      }

   public Object getBackPanelDescriptor()
      {
      return (isDirectConnectSupported) ? PeerConnectionMethodDescriptor.IDENTIFIER : null;
      }

   public Object getNextPanelDescriptor()
      {
      return PeerChooserDescriptor.IDENTIFIER;
      }

   private void setFocusAppropriately()
      {
      if (getRelaySessionManager() != null)
         {
         if (!getRelaySessionManager().isLoggedIn())
            {
            if (panel.isUserIdFieldNonEmpty())
               {
               if (panel.isPasswordFieldNonEmpty())
                  {
                  panel.getUserIdTextField().requestFocusInWindow();
                  panel.getUserIdTextField().selectAll();
                  }
               else
                  {
                  panel.getPasswordTextField().requestFocusInWindow();
                  }
               }
            else
               {
               panel.getUserIdTextField().requestFocusInWindow();
               }
            }
         }
      }

   public void aboutToDisplayPanel()
      {
      isCurrentlyVisible = true;

      if (relayCommunicatorManager.isCreated())
         {
         updateWidgetsAndSetFocusRunnable.run();
         }
      else
         {
         disableWidgetsAndSetWaitCursor();

         relayCommunicatorManager.createCommunicator();
         }
      }

   public void aboutToHidePanel()
      {
      isCurrentlyVisible = false;
      }

   public void actionPerformed(final ActionEvent event)
      {
      if (panel.areLoginFieldsNonEmpty())
         {
         disableWidgetsAndSetWaitCursor();

         if (getRelaySessionManager().isLoggedIn())
            {
            new DisconnectWorker().start();
            }
         else
            {
            new ConnectWorker().start();
            }
         }
      }

   private void disableWidgetsAndSetWaitCursor()
      {
      getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      panel.getUserIdTextField().setEnabled(false);
      panel.getPasswordTextField().setEnabled(false);
      panel.getSubmitButton().setEnabled(false);
      getWizard().setBackButtonEnabled(false);
      getWizard().setNextFinishButtonEnabled(false);
      getWizard().setCancelButtonEnabled(false);
      }

   private void updateWidgetsAccordingToLoginState()
      {
      final boolean isLoggedInToRelay = getRelaySessionManager() != null && getRelaySessionManager().isLoggedIn();
      panel.getUserIdTextField().setEnabled(!isLoggedInToRelay);
      panel.getPasswordTextField().setEnabled(!isLoggedInToRelay);
      panel.getSubmitButton().setText(isLoggedInToRelay ? RESOURCES.getString("button.logout") : RESOURCES.getString("button.login"));
      panel.enableSubmitButtonIfLoginFieldsAreNotEmpty();

      getWizard().setBackButtonEnabled(isDirectConnectSupported);
      getWizard().setNextFinishButtonEnabled(isLoggedInToRelay);
      getWizard().setCancelButtonEnabled(true);

      getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }

   private abstract class ConnectDisconnectWorker extends SwingWorker
      {
      public final Object construct()
         {
         doTimeConsumingAction();
         return null;
         }

      protected abstract void doTimeConsumingAction();

      public final void finished()
         {
         updateWidgetsAccordingToLoginState();
         }
      }

   private final class ConnectWorker extends ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         // fetch the user id and password from the GUI
         final String[] userIdAndPassword = new String[2];
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     userIdAndPassword[0] = panel.userIdTextField.getText();
                     userIdAndPassword[1] = (panel.passwordTextField.getPassword() == null) ? "" : new String(panel.passwordTextField.getPassword());
                     }
                  });
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while getting the user id and password from the form fields", e);
            }
         catch (InvocationTargetException e)
            {
            LOG.error("InvocationTargetException while getting the user id and password from the form fields", e);
            }

         // do the login
         final boolean loginWasSuccessful = getRelaySessionManager().login(userIdAndPassword[0], userIdAndPassword[1]);
         if (!loginWasSuccessful)
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     panel.getPasswordTextField().requestFocusInWindow();
                     panel.getPasswordTextField().selectAll();
                     }
                  });
            }
         }
      }

   private final class DisconnectWorker extends ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         getRelaySessionManager().logout();
         }
      }

   private final class MyRelayCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         SwingUtilities.invokeLater(updateWidgetsAndSetFocusRunnable);
         ((RelayCommunicator)terkCommunicator).addConnectionEventListener(connectionEventListener);
         }

      public void afterFailedConstruction()
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  getWizard().setBackButtonEnabled(true);
                  getWizard().setNextFinishButtonEnabled(false);
                  getWizard().setCancelButtonEnabled(true);

                  getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                  }
               });
         }
      }

   private static final class RelayLoginForm extends JPanel
      {
      private static final char BULLET_CHARACTER = '\u2022';

      private final JTextField userIdTextField = new JTextField(10);
      private final JPasswordField passwordTextField = new JPasswordField(10);
      private final JButton submitButton = new JButton();
      private final KeyAdapter loginFieldsKeyListener = new KeyAdapter()
      {
      public void keyReleased(final KeyEvent e)
         {
         enableSubmitButtonIfLoginFieldsAreNotEmpty();
         }
      };

      private RelayLoginForm(final ActionListener actionListener, final PropertyResourceBundle resources)
         {
         setLayout(new SpringLayout());

         userIdTextField.addKeyListener(loginFieldsKeyListener);
         passwordTextField.addKeyListener(loginFieldsKeyListener);
         passwordTextField.setEchoChar(BULLET_CHARACTER);

         submitButton.setEnabled(false);
         submitButton.setText(resources.getString("button.login"));
         submitButton.addActionListener(actionListener);
         userIdTextField.addActionListener(actionListener);//login on hit <ENTER>
         passwordTextField.addActionListener(actionListener);

         final JLabel userIdLabel = new JLabel(resources.getString("label.userId"));
         final JLabel passwordLabel = new JLabel(resources.getString("label.password"));

         final JPanel loginPanel = new JPanel(new SpringLayout());
         loginPanel.add(userIdLabel);
         loginPanel.add(userIdTextField);
         loginPanel.add(passwordLabel);
         loginPanel.add(passwordTextField);
         SpringLayoutUtilities.makeCompactGrid(loginPanel,
                                               2, 2, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad

         add(new JLabel(resources.getString("instructions")));
         add(Box.createGlue());
         add(loginPanel);
         add(Box.createGlue());
         add(submitButton);
         SpringLayoutUtilities.makeCompactGrid(this,
                                               5, 1, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad
         }

      private void enableSubmitButtonIfLoginFieldsAreNotEmpty()
         {
         submitButton.setEnabled(areLoginFieldsNonEmpty());
         }

      private boolean areLoginFieldsNonEmpty()
         {
         return isUserIdFieldNonEmpty() && isPasswordFieldNonEmpty();
         }

      private boolean isUserIdFieldNonEmpty()
         {
         final String text1 = userIdTextField.getText();
         final String trimmedText1 = (text1 != null) ? text1.trim() : null;
         return (trimmedText1 != null) && (trimmedText1.length() > 0);
         }

      private boolean isPasswordFieldNonEmpty()
         {
         final String text2 = (passwordTextField.getPassword() == null) ? "" : new String(passwordTextField.getPassword());
         final String trimmedText2 = text2.trim();
         return (trimmedText2 != null) && (trimmedText2.length() > 0);
         }

      private JTextField getUserIdTextField()
         {
         return userIdTextField;
         }

      private JPasswordField getPasswordTextField()
         {
         return passwordTextField;
         }

      private JButton getSubmitButton()
         {
         return submitButton;
         }
      }

   private class MyConnectionEventListener extends ConnectionEventAdapter
      {
      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         refreshIfVisible();
         }

      public void handleForcedLogoutNotificationEvent()
         {
         refreshIfVisible();
         }

      private void refreshIfVisible()
         {
         if (RelayLoginFormDescriptor.this.getWizard().getDialog().isVisible() && RelayLoginFormDescriptor.this.isCurrentlyVisible)
            {
            SwingUtilities.invokeLater(updateWidgetsRunnable);
            }
         }
      }
   }
