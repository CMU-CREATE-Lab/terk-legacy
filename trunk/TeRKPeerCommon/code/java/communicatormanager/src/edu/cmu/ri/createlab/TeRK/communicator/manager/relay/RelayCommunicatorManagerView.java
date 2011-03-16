package edu.cmu.ri.createlab.TeRK.communicator.manager.relay;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.PropertyResourceBundle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManagerListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.UserConnectionEventListener;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public final class RelayCommunicatorManagerView
   {
   private static final Logger LOG = Logger.getLogger(RelayCommunicatorManagerView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RelayCommunicatorManagerView.class.getName());

   private static JLabel createLabel(final String text)
      {
      final JLabel label = new JLabel(text);
      label.setFont(GUIConstants.FONT_SMALL);
      label.setEnabled(false);
      return label;
      }

   private final RelayCommunicatorManager manager;

   // the panel in which caller will place this view's widgets (necessary for setting the cursor and such)
   private final Component parentComponent;

   private final JPanel loginPanel = new JPanel();

   // widgets
   private final JTextField userIdTextField = new JTextField(10);
   private final JPasswordField passwordTextField = new JPasswordField(10);
   private final JLabel userIdLabel = createLabel(RESOURCES.getString("label.userId"));
   private final JLabel passwordLabel = createLabel(RESOURCES.getString("label.password"));
   private final JButton loginLogoutButton = new JButton();
   private final JCheckBox relayConnectionsCheckbox = new JCheckBox(RESOURCES.getString("label.relay-connections"));

   // listeners
   private final MyUserConnectionEventListener userConnectionEventListener = new MyUserConnectionEventListener();
   private final KeyAdapter loginFieldsKeyListener =
         new KeyAdapter()
         {
         public void keyReleased(final KeyEvent e)
            {
            enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
            }
         };

   // actions
   private final ActionListener loginLogoutAction = new LoginLogoutActionListener();

   // runnables
   private final Runnable relayConnectionsCheckboxEnabler = new SetWidgetEnabledRunnable(relayConnectionsCheckbox, true);
   private final Runnable relayConnectionsCheckboxDisabler = new SetWidgetEnabledRunnable(relayConnectionsCheckbox, false);
   private final Runnable relayConnectionsCheckboxUnselector = new SetCheckBoxSelectedRunnable(relayConnectionsCheckbox, false);
   private final Runnable toggleRelayLoginFormWidgetsAccordingToLoginStatusRunnable =
         new Runnable()
         {
         public void run()
            {
            toggleRelayLoginFormWidgetsAccordingToLoginStatus();
            }
         };
   private final Runnable handleFailedLoginEventRunnable =
         new Runnable()
         {
         public void run()
            {
            passwordTextField.requestFocusInWindow();
            passwordTextField.selectAll();
            }
         };

   public RelayCommunicatorManagerView(final RelayCommunicatorManager manager, final Component parentComponent)
      {
      this.manager = manager;
      this.parentComponent = parentComponent;

      this.manager.addTerkCommunicatorCreationEventListener(new MyRelayCommunicatorCreationEventAdapater());
      this.manager.addTerkCommunicatorManagerListener(new MyTerkCommunicatorManagerListener());

      userIdTextField.setEnabled(false);
      userIdTextField.setFont(GUIConstants.FONT_SMALL);
      userIdTextField.addActionListener(loginLogoutAction);
      userIdTextField.addKeyListener(loginFieldsKeyListener);

      passwordTextField.setEnabled(false);
      passwordTextField.setFont(GUIConstants.FONT_SMALL);
      passwordTextField.addActionListener(loginLogoutAction);
      passwordTextField.addKeyListener(loginFieldsKeyListener);
      passwordTextField.setEchoChar(GUIConstants.BULLET_CHARACTER);

      enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();

      loginLogoutButton.setFont(GUIConstants.FONT_SMALL);
      loginLogoutButton.setText(RESOURCES.getString("button.login"));
      loginLogoutButton.addActionListener(loginLogoutAction);
      loginLogoutButton.setOpaque(false);//required for Mac

      relayConnectionsCheckbox.setFont(GUIConstants.FONT_SMALL);
      relayConnectionsCheckbox.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected void executeGUIActionBefore()
               {
               LOG.debug("AbstractTimeConsumingAction.executeGUIActionBefore()");
               relayConnectionsCheckboxDisabler.run();
               parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               }

            protected Object executeTimeConsumingAction()
               {
               LOG.debug("relay box checked: " + relayConnectionsCheckbox.isSelected());
               if (relayConnectionsCheckbox.isSelected())
                  {
                  manager.createCommunicator();
                  }
               else
                  {
                  manager.shutdownCommunicator();
                  }

               return null;
               }

            protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
               {
               LOG.debug("AbstractTimeConsumingAction.executeGUIActionAfter() [toggle relay connect checkbox]");
               // If we're turning OFF relay connect, then just set the cursor to the default and enable the
               // checkbox.  However, if we're trying to turn ON relay connect, then that's done asynchronously, plus
               // we need to know whether it succeeded, so we need to let the MyRelayCommunicatorCreationEventAdapater
               // make the GUI calls.
               if (!relayConnectionsCheckbox.isSelected())
                  {
                  parentComponent.setCursor(Cursor.getDefaultCursor());
                  relayConnectionsCheckboxEnabler.run();
                  toggleRelayLoginFormWidgets(false, false);
                  }
               }
            });

      final JPanel loginFormPanel = new JPanel(new SpringLayout());
      loginFormPanel.add(userIdLabel);
      loginFormPanel.add(userIdTextField);
      loginFormPanel.add(passwordLabel);
      loginFormPanel.add(passwordTextField);
      SpringLayoutUtilities.makeCompactGrid(loginFormPanel,
                                            2, 2, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));

      loginPanel.add(loginFormPanel);
      loginPanel.add(GUIConstants.createRigidSpacer());
      loginPanel.add(loginLogoutButton);
      }

   public JCheckBox getCheckbox()
      {
      return relayConnectionsCheckbox;
      }

   public JPanel getLoginFormPanel()
      {
      return loginPanel;
      }

   private void toggleRelayLoginFormWidgetsAccordingToLoginStatus()
      {
      LOG.debug("RelayCommunicatorManagerView.toggleRelayLoginFormWidgetsAccordingToLoginStatus()");

      final RelayCommunicator relayCommunicator = (RelayCommunicator)manager.getTerkCommunicator();
      final boolean isRelayCommunicatorRunning = relayCommunicator != null;
      final boolean isLoggedIn = isRelayCommunicatorRunning && manager.isLoggedIn();
      toggleRelayLoginFormWidgets(isRelayCommunicatorRunning, isLoggedIn);
      }

   private void toggleRelayLoginFormWidgets(final boolean isRelayCommunicatorRunning, final boolean isLoggedIn)
      {
      parentComponent.setCursor(Cursor.getDefaultCursor());
      userIdTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      passwordTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      userIdLabel.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      passwordLabel.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      loginLogoutButton.setText(isLoggedIn ? RESOURCES.getString("button.logout") : RESOURCES.getString("button.login"));
      enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
      }

   private void enableLoginLogoutButtonIfLoginFieldsAreNotEmpty()
      {
      loginLogoutButton.setEnabled(manager.isCreated() && areLoginFieldsNonEmpty());
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

   private final class SetWidgetEnabledRunnable implements Runnable
      {
      private final Component component;
      private final boolean isEnabled;

      private SetWidgetEnabledRunnable(final Component component, final boolean isEnabled)
         {
         this.component = component;
         this.isEnabled = isEnabled;
         }

      public void run()
         {
         component.setEnabled(isEnabled);
         }
      }

   private class SetCheckBoxSelectedRunnable implements Runnable
      {
      private final JCheckBox checkbox;
      private final boolean isSelected;

      private SetCheckBoxSelectedRunnable(final JCheckBox checkbox, final boolean isSelected)
         {
         this.checkbox = checkbox;
         this.isSelected = isSelected;
         }

      public void run()
         {
         checkbox.setSelected(isSelected);
         }
      }

   private class LoginLogoutActionListener extends AbstractTimeConsumingAction
      {
      private String username;
      private String password;

      protected void executeGUIActionBefore()
         {
         loginLogoutButton.setEnabled(false);
         userIdTextField.setEnabled(false);
         passwordTextField.setEnabled(false);
         parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

         username = userIdTextField.getText();
         password = (passwordTextField.getPassword() == null) ? "" : new String(passwordTextField.getPassword());
         }

      protected Object executeTimeConsumingAction()
         {
         if (manager.isLoggedIn())
            {
            manager.logout();
            }
         else
            {
            manager.login(username, password);
            }

         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         }
      }

   private final class MyRelayCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         LOG.debug("RelayCommunicatorManagerView$MyRelayCommunicatorCreationEventAdapater.afterSuccessfulConstruction()");

         ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(userConnectionEventListener);

         enable();
         }

      public void afterFailedConstruction()
         {
         LOG.debug("RelayCommunicatorManagerView$MyRelayCommunicatorCreationEventAdapater.afterFailedConstruction()");

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  relayConnectionsCheckbox.setSelected(false);
                  enable();
                  }
               }
         );
         }

      public void afterWaitForShutdown()
         {
         SwingUtilities.invokeLater(relayConnectionsCheckboxUnselector);
         }

      private void enable()
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            enableWorkhorse();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     enableWorkhorse();
                     }
                  });
            }
         }

      private void enableWorkhorse()
         {
         relayConnectionsCheckboxEnabler.run();
         parentComponent.setCursor(Cursor.getDefaultCursor());
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         }
      }

   private class MyUserConnectionEventListener implements UserConnectionEventListener
      {
      public void handleRelayLoginEvent()
         {
         LOG.debug("RelayCommunicatorManagerView$MyUserConnectionEventListener.handleRelayLoginEvent()");
         runInGUIThread(toggleRelayLoginFormWidgetsAccordingToLoginStatusRunnable);
         }

      public void handleFailedRelayLoginEvent()
         {
         LOG.debug("RelayCommunicatorManagerView$MyUserConnectionEventListener.handleFailedRelayLoginEvent()");
         runInGUIThread(handleFailedLoginEventRunnable);
         }

      public void handleRelayRegistrationEvent()
         {
         LOG.debug("RelayCommunicatorManagerView$MyUserConnectionEventListener.handleRelayRegistrationEvent()");
         // nothing to do here (I think?)
         }

      public void handleRelayLogoutEvent()
         {
         LOG.debug("RelayCommunicatorManagerView$MyUserConnectionEventListener.handleRelayLogoutEvent()");
         runInGUIThread(toggleRelayLoginFormWidgetsAccordingToLoginStatusRunnable);
         }

      public void handleForcedLogoutNotificationEvent()
         {
         LOG.debug("RelayCommunicatorManagerView$MyUserConnectionEventListener.handleForcedLogoutNotificationEvent()");
         runInGUIThread(toggleRelayLoginFormWidgetsAccordingToLoginStatusRunnable);
         }
      }

   // ensures that the given runnable is run in the GUI thread
   private void runInGUIThread(final Runnable runnable)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         runnable.run();
         }
      else
         {
         SwingUtilities.invokeLater(runnable);
         }
      }

   private class MyTerkCommunicatorManagerListener implements TerkCommunicatorManagerListener
      {
      public void handleEnabledStateChange(final boolean isEnabled)
         {
         final SwingWorker worker =
               new SwingWorker()
               {
               public Object construct()
                  {
                  relayConnectionsCheckbox.setEnabled(isEnabled);

                  if (isEnabled)
                     {
                     toggleRelayLoginFormWidgetsAccordingToLoginStatus();
                     }
                  else
                     {
                     userIdTextField.setEnabled(false);
                     passwordTextField.setEnabled(false);
                     loginLogoutButton.setEnabled(false);
                     }
                  return null;
                  }
               };
         worker.start();
         }
      }
   }