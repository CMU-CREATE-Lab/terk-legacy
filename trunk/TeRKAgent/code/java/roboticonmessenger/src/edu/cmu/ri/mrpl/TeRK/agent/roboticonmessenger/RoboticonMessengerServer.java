package edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.agent.peerinformation.PeerInfoServer;
import edu.cmu.ri.mrpl.TeRK.agent.peerinformation.PeerInfoServerServiceServant;
import edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging.RoboticonMessagingServer;
import edu.cmu.ri.mrpl.TeRK.agent.roboticonmessenger.messaging.RoboticonMessagingServerServiceServant;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.peerinformation.HTTPPeerImageFactory;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoServerServicePrx;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoServerServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoView;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerView;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingServerServicePrx;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingServerServicePrxHelper;
import edu.cmu.ri.mrpl.peer.ConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessengerServer extends JFrame
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerServer.class);

   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/agent/roboticonmessenger/RoboticonMessengerServer.relay.ice.properties";

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RoboticonMessengerServer.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private static final String OBJECT_ADAPTER_NAME = "Agent.Client";

   private static final String SAVED_FILES_DIRECTORY_PATH = System.getProperty("user.home") + File.separator + "TeRK" + File.separator + "RoboticonMessenger" + File.separator;
   private static final File SAVED_FILES_DIRECTORY = new File(SAVED_FILES_DIRECTORY_PATH);
   private static final String MESSAGING_HISTORY_FILENAME = "MessageHistory";
   private static final String HISTORY_BACKUP_FILENAME = "MessageHistory-Backup";
   private static final String HISTORY_FILE_TYPE = ".xml";

   private static final char BULLET_CHARACTER = '\u2022';
   private static final Dimension SPACER = new Dimension(5, 5);
   private static final Dimension BIG_SPACER = new Dimension(20, 20);
   private static final String FONT_NAME = "Verdana";
   private static final Font FONT = new Font(FONT_NAME, 0, 11);
   private static final Font FONT_LARGE = new Font(FONT_NAME, 0, 20);

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new RoboticonMessengerServer();
               }
            });
      }

   private RelayCommunicator relayCommunicator;

   private RoboticonMessagingServer roboticonMessagingServer;
   private PeerInfoServer peerInfoServer;

   private RoboticonMessengerClientController roboticonMessengerClientController;

   // peer info stuff
   private final HTTPPeerImageFactory peerImageFactory = new HTTPPeerImageFactory(RESOURCES.getString("terk-web-site.host"),
                                                                                  RESOURCES.getString("terk-web-site.avatar-image-loader-prefix"));
   private final PeerInfoModel peerInfoModel = new PeerInfoModel(peerImageFactory);
   private final PeerInfoView peerInfoView = new PeerInfoView(peerInfoModel);

   private final RoboticonMessengerModel roboticonMessengerModel = new RoboticonMessengerModel(true);

   private final RoboticonMessengerController roboticonMessengerController =
         new RoboticonMessengerController()
         {
         public void sendPublicMessage(final String parentMessageId, final ClientRoboticonMessage clientRoboticonMessage)
            {
            if (roboticonMessagingServer != null)
               {
               roboticonMessagingServer.sendPublicMessage(parentMessageId,
                                                          RESOURCES.getString("messaging.name"),
                                                          clientRoboticonMessage);
               }
            }

         public void sendPrivateMessage(final String parentMessageId, final String recipientUserId, final ClientRoboticonMessage clientRoboticonMessage)
            {
            if (roboticonMessagingServer != null)
               {
               roboticonMessagingServer.sendPrivateMessage(parentMessageId,
                                                           RESOURCES.getString("messaging.name"),
                                                           recipientUserId,
                                                           clientRoboticonMessage);
               }
            }
         };
   private final RoboticonMessengerView roboticonMessengerView = new RoboticonMessengerView(this,
                                                                                            roboticonMessengerController,
                                                                                            roboticonMessengerModel,
                                                                                            peerInfoModel,
                                                                                            new RoboticonManagerController()
                                                                                            {
                                                                                            public void addRoboticons(final Collection<RoboticonFile> roboticonFiles)
                                                                                               {
                                                                                               // do nothing, since we don't care about
                                                                                               // the RoboticonManager in the server app
                                                                                               }
                                                                                            });

   private final JPanel mainContentPane = new JPanel();

   private final JTextField userIdTextField = new JTextField(10);
   private final JPasswordField passwordTextField = new JPasswordField(10);
   private final JButton loginLogoutButton = new JButton();
   private final ActionListener loginLogoutAction = new RoboticonMessengerServer.LoginLogoutActionListener();
   private final RoboticonMessengerServer.MyConnectionEventListener connectionEventListener = new RoboticonMessengerServer.MyConnectionEventListener();

   private RoboticonMessengerServer()
      {
      super(APPLICATION_NAME);

      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setBackground(Color.WHITE);
      setResizable(false);
      this.addWindowListener(new RoboticonMessengerServer.MyWindowListener());

      setContentPane(mainContentPane);
      mainContentPane.setLayout(new SpringLayout());
      roboticonMessengerModel.setUserId("Roboticon Messenger");
      roboticonMessengerModel.addRoboticonMessengerListener(roboticonMessengerView);
      peerInfoModel.addPeerInfoListener(peerInfoView);
      peerInfoModel.addPeerInfoListener(roboticonMessengerView);

      roboticonMessengerView.setEnabled(false);
      peerInfoView.setEnabled(false);

      final KeyAdapter loginFieldsKeyListener =
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
               }
            };
      userIdTextField.addKeyListener(loginFieldsKeyListener);
      passwordTextField.addKeyListener(loginFieldsKeyListener);
      passwordTextField.setEchoChar(BULLET_CHARACTER);

      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
               }
            });

      loginLogoutButton.setText(RESOURCES.getString("button.login"));
      loginLogoutButton.setFont(FONT);
      loginLogoutButton.addActionListener(loginLogoutAction);
      loginLogoutButton.setOpaque(false);//required for Mac

      userIdTextField.addActionListener(loginLogoutAction);//pressing <ENTER> will cause login when textfields have focus
      passwordTextField.addActionListener(loginLogoutAction);

      final JLabel applicationNameLabel = new JLabel(APPLICATION_NAME);
      applicationNameLabel.setFont(FONT_LARGE);

      final JLabel userIdLabel = new JLabel(RESOURCES.getString("label.userId"));
      final JLabel passwordLabel = new JLabel(RESOURCES.getString("label.password"));
      userIdLabel.setFont(FONT);
      passwordLabel.setFont(FONT);

      final JPanel loginFormPanel = new JPanel(new SpringLayout());
      loginFormPanel.add(userIdLabel);
      loginFormPanel.add(userIdTextField);
      loginFormPanel.add(passwordLabel);
      loginFormPanel.add(passwordTextField);
      SpringLayoutUtilities.makeCompactGrid(loginFormPanel,
                                            2, 2, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final JPanel loginPanel = new JPanel();
      loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));

      loginPanel.add(loginFormPanel);
      loginPanel.add(Box.createRigidArea(SPACER));
      loginPanel.add(loginLogoutButton);

      final JPanel applicationNameLabelAndLoginPanel = new JPanel();
      applicationNameLabelAndLoginPanel.setLayout(new BoxLayout(applicationNameLabelAndLoginPanel, BoxLayout.X_AXIS));
      applicationNameLabelAndLoginPanel.add(applicationNameLabel);
      applicationNameLabelAndLoginPanel.add(Box.createRigidArea(BIG_SPACER));
      applicationNameLabelAndLoginPanel.add(loginPanel);

      final JPanel mainPanel = new JPanel(new SpringLayout());
      mainPanel.add(roboticonMessengerView.getMessageHistoryComponent());
      mainPanel.add(peerInfoView.getListComponent());
      mainPanel.add(roboticonMessengerView.getMessageSubmissionComponent());
      mainPanel.add(Box.createGlue());
      SpringLayoutUtilities.makeCompactGrid(mainPanel,
                                            2, 2, // rows, cols
                                            0, 0, // initX, initY
                                            10, 10);// xPad, yPad

      mainContentPane.add(applicationNameLabelAndLoginPanel);
      mainContentPane.add(mainPanel);
      SpringLayoutUtilities.makeCompactGrid(mainContentPane,
                                            2, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 10);// xPad, yPad

      pack();
      setLocationRelativeTo(null);// center the window on the screen
      setVisible(true);

      RelayCommunicator.createAsynchronously(APPLICATION_NAME,
                                             ICE_RELAY_PROPERTIES_FILE,
                                             OBJECT_ADAPTER_NAME,
                                             new MyRelayCommunicatorCreationEventAdapater());
      }

   private void doLogin()
      {
      // fetch the user id and password from the GUI
      final String[] userIdAndPassword = new String[2];
      try
         {
         SwingUtilities.invokeAndWait(new Runnable()
         {
         public void run()
            {
            userIdAndPassword[0] = userIdTextField.getText();
            userIdAndPassword[1] = (passwordTextField.getPassword() == null) ? "" : new String(passwordTextField.getPassword());
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
      final boolean loginWasSuccessful = relayCommunicator.login(userIdAndPassword[0], userIdAndPassword[1]);

      if (loginWasSuccessful)
         {
         // create and register the servants
         try
            {
            // create the main servant
            final RoboticonMessengerServerServant mainServant = new RoboticonMessengerServerServant(connectionEventListener);

            // create the main servant proxy
            final ObjectPrx mainServantProxy = relayCommunicator.createServantProxy(mainServant);
            final TerkUserPrx mainRoboticonMessengerServerServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

            // create secondary servants and their proxies
            final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();
            final String safeUser = userIdAndPassword[0].replace(':', '$');
            final String historyFilename = MESSAGING_HISTORY_FILENAME + "-" + safeUser + HISTORY_FILE_TYPE;
            final String backupFilename = HISTORY_BACKUP_FILENAME + "-" + safeUser + HISTORY_FILE_TYPE;

            //roboticonMessengerModel.clearHistory();

            roboticonMessagingServer = new RoboticonMessagingServer(roboticonMessengerModel, SAVED_FILES_DIRECTORY, historyFilename, backupFilename);
            roboticonMessagingServer.loadHistory();
            final ObjectImpl roboticonMessagingServerServiceServant = new RoboticonMessagingServerServiceServant(roboticonMessagingServer);
            final ObjectPrx untypedRoboticonMessagingServerServiceServantProxy = relayCommunicator.createServantProxy(roboticonMessagingServerServiceServant);
            final RoboticonMessagingServerServicePrx roboticonMessagingServerServiceServantProxy = RoboticonMessagingServerServicePrxHelper.uncheckedCast(untypedRoboticonMessagingServerServiceServantProxy);
            secondaryServantProxies.add(roboticonMessagingServerServiceServantProxy);

            peerInfoServer = new PeerInfoServer(peerInfoModel);
            final ObjectImpl peerInfoServerServiceServant = new PeerInfoServerServiceServant(peerInfoServer);
            final ObjectPrx untypedPeerInfoServerServiceServantProxy = relayCommunicator.createServantProxy(peerInfoServerServiceServant);
            final PeerInfoServerServicePrx peerInfoServerServiceServantProxy = PeerInfoServerServicePrxHelper.uncheckedCast(untypedPeerInfoServerServiceServantProxy);
            secondaryServantProxies.add(peerInfoServerServiceServantProxy);

            mainServant.registerServiceServant(roboticonMessagingServerServiceServant, roboticonMessagingServerServiceServantProxy);
            mainServant.registerServiceServant(peerInfoServerServiceServant, peerInfoServerServiceServantProxy);

            // register the main servant proxy and the command controller servant proxies with the relay
            relayCommunicator.registerCallbacks(mainRoboticonMessengerServerServantPrx, mainRoboticonMessengerServerServantPrx);
            relayCommunicator.registerProxies(secondaryServantProxies);
            }
         catch (RegistrationException e)
            {
            LOG.error("RegistrationException while trying to register the servants", e);
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to create and register the servants", e);
            }

         // initialize the PeerInfoModel
         try
            {
            final Set<PeerIdentifier> peerIdentifiers = relayCommunicator.getMyPeers();
            if ((peerIdentifiers != null) && (!peerIdentifiers.isEmpty()))
               {
               final List<PeerInfo> peers = new ArrayList<PeerInfo>();
               for (final PeerIdentifier peerIdentifier : peerIdentifiers)
                  {
                  peers.add(new PeerInfo(false, 0, peerIdentifier.userId, new HashMap<String, String>()));
                  }
               peerInfoModel.setPeerInfo(peers);
               }
            }
         catch (PeerException e)
            {
            LOG.error("PeerException while calling getMyPeers() on the relay communicator to initialize the PeerInfoModel", e);
            }

         // all done!
         LOG.info("Login successful!");
         }
      else
         {
         LOG.info("Login failed!");
         showLoginFailedMessageInGUIThread();
         }
      }

   private void showLoginFailedMessageInGUIThread()
      {
      try
         {
         SwingUtilities.invokeAndWait(new Runnable()
         {
         public void run()
            {
            JOptionPane.showMessageDialog(RoboticonMessengerServer.this,
                                          RESOURCES.getString("dialog.message.login-failed"),
                                          RESOURCES.getString("dialog.title.login-failed"),
                                          JOptionPane.INFORMATION_MESSAGE);
            passwordTextField.requestFocusInWindow();
            passwordTextField.selectAll();
            }
         });
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while displaying the login failed message", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while displaying the login failed message", e);
         }
      }

   private void doLogout()
      {
      if (relayCommunicator != null)
         {
         relayCommunicator.logout();
         LOG.info("Relay logout successful!");
         }
      if (roboticonMessagingServer != null)
         {
         roboticonMessagingServer.clearHistory();
         }

      roboticonMessagingServer = null;
      peerInfoServer = null;
      }

   private void enableLoginLogoutButtonIfLoginFieldsAreNotEmpty()
      {
      loginLogoutButton.setEnabled((relayCommunicator != null) && areLoginFieldsNonEmpty());
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

   private void toggleWidgetsAccordingToLoginStatus()
      {
      final boolean isRelayCommunicatorRunning = relayCommunicator != null;
      final boolean isLoggedIn = isRelayCommunicatorRunning && relayCommunicator.isLoggedIn();
      setCursor(Cursor.getDefaultCursor());
      userIdTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      passwordTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      loginLogoutButton.setText(isLoggedIn ? RESOURCES.getString("button.logout") : RESOURCES.getString("button.login"));
      enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();

      roboticonMessengerView.setEnabled(isLoggedIn);
      peerInfoView.setEnabled(isLoggedIn);
      }

   private final class MyRelayCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         RoboticonMessengerServer.this.relayCommunicator = (RelayCommunicator)terkCommunicator;

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  toggleWidgetsAccordingToLoginStatus();
                  }
               }
         );
         }

      public void afterFailedConstruction()
         {
         RoboticonMessengerServer.this.relayCommunicator = null;

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  JOptionPane.showMessageDialog(RoboticonMessengerServer.this,
                                                RESOURCES.getString("dialog.message.relay-communicator-creation-failed"),
                                                RESOURCES.getString("dialog.title.relay-communicator-creation-failed"),
                                                JOptionPane.INFORMATION_MESSAGE);
                  }
               });
         }
      }

   private class LoginLogoutActionListener implements ActionListener
      {
      public void actionPerformed(final ActionEvent e)
         {
         if (areLoginFieldsNonEmpty())
            {
            loginLogoutButton.setEnabled(false);
            userIdTextField.setEnabled(false);
            passwordTextField.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (relayCommunicator.isLoggedIn())
               {
               new RoboticonMessengerServer.DisconnectWorker().start();
               }
            else
               {
               new RoboticonMessengerServer.ConnectWorker().start();
               }
            }
         }
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
         toggleWidgetsAccordingToLoginStatus();
         }
      }

   private final class ConnectWorker extends RoboticonMessengerServer.ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         doLogin();
         }
      }

   private final class DisconnectWorker extends RoboticonMessengerServer.ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         doLogout();
         }
      }

   private class MyWindowListener extends WindowAdapter
      {
      public void windowClosing(final WindowEvent event)
         {
         // ask if the user really wants to exit
         final int selectedOption = JOptionPane.showConfirmDialog(RoboticonMessengerServer.this,
                                                                  RESOURCES.getString("dialog.message.exit-confirmation"),
                                                                  RESOURCES.getString("dialog.title.exit-confirmation"),
                                                                  JOptionPane.YES_NO_OPTION,
                                                                  JOptionPane.QUESTION_MESSAGE);

         if (selectedOption == JOptionPane.YES_OPTION)
            {
            final SwingWorker worker = new SwingWorker()
            {
            public Object construct()
               {
               doLogout();
               return null;
               }

            public void finished()
               {
               System.exit(0);
               }
            };
            worker.start();
            }
         }
      }

   private final class MyConnectionEventListener extends ConnectionEventAdapter
      {
      public void handleForcedLogoutNotificationEvent()
         {
         final SwingWorker worker =
               new SwingWorker()
               {
               public Object construct()
                  {
                  doLogout();
                  return null;
                  }

               public void finished()
                  {
                  toggleWidgetsAccordingToLoginStatus();
                  JOptionPane.showMessageDialog(RoboticonMessengerServer.this,
                                                RESOURCES.getString("dialog.message.logout-forced"),
                                                RESOURCES.getString("dialog.title.logout-forced"),
                                                JOptionPane.INFORMATION_MESSAGE);
                  }
               };
         worker.start();
         }

      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RoboticonMessagingServer$MyConnectionEventListener.handlePeerConnectedEvent(" + peerUserId + ", " + peerAccessLevel + ", " + Util.identityToString(peerObjectProxy.ice_getIdentity()) + ")");
            }
         final TerkUserPrx terkUserProxy = TerkUserPrxHelper.checkedCast(peerObjectProxy);
         if (terkUserProxy != null)
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug("   creating RoboticonMessengerClientController for client [" + peerUserId + "]...");
               }
            roboticonMessengerClientController = new RoboticonMessengerClientController(peerUserId,
                                                                                        terkUserProxy,
                                                                                        relayCommunicator);
            if (roboticonMessengerClientController.isRoboticonMessagingSupported())
               {
               roboticonMessagingServer.addClient(peerUserId, roboticonMessengerClientController.getRoboticonMessengerService());
               }
            if (roboticonMessengerClientController.isPeerInfoSupported())
               {
               peerInfoServer.markClientAsConnected(peerUserId, roboticonMessengerClientController.getPeerInfoService());
               }
            }
         else
            {
            LOG.info("Ignoring peer [" + peerUserId + "] since it is not a TerkUser.");
            }
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         if (LOG.isInfoEnabled())
            {
            LOG.info("Ignoring peer [" + peerUserId + "] since I didn't get a proxy.");
            }
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("RoboticonMessagingServer$MyConnectionEventListener.handlePeerDisconnectedEvent(" + peerUserId + ")");
            }
         roboticonMessagingServer.removeClient(peerUserId);
         peerInfoServer.markClientAsDisconnected(peerUserId);
         roboticonMessengerClientController = null;
         }
      }
   }
