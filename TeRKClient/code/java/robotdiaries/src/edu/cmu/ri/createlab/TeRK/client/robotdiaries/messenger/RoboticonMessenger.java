package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import com.nexes.wizard.Wizard;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.manager.RoboticonManagerModel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.messaging.RoboticonMessagingClientServiceServant;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.ConnectDisconnectButton;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.servants.PeerInfoClientServiceServant;
import edu.cmu.ri.mrpl.TeRK.color.ColorChooserDialog;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.peerinformation.HTTPPeerImageFactory;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerImageFactory;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrx;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoController;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoView;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrx;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.TeRK.servants.TerkUserServant;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.peer.UserConnectionEventAdapter;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.ColorUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class RoboticonMessenger extends JPanel
   {

   private static final Logger LOG = Logger.getLogger(RoboticonMessenger.class);

   public static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RoboticonMessenger.class.getName());

   /** The application name (appears in the title bar) */
   public static final String APPLICATION_NAME = RESOURCES
         .getString("application.name");

   /** Properties file used to setup Ice for this application */
   public static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/robotdiaries/messenger/RoboticonMessenger.relay.ice.properties";//  @jve:decl-index=0:
   private static final String ICE_OBJECT_ADAPTER_NAME = "Terk.User";

   private final RelayCommunicatorManager relayCommunicatorManager;
   private final Wizard connectToRobotWizard;

   private ConnectDisconnectButton connectDisconnectButton;
   private JLabel connectedUserIdLabel = new JLabel("Not Logged In");
   private JLabel userIconLabel = new JLabel();

   private JButton colorButton;
   private JPanel messageHistoryPanel;
   private final MyUserConnectionEventListener userConnectionEventListener = new MyUserConnectionEventListener();
   private final Collection<PeerConnectionEventListener> peerConnectionEventListeners;
   private boolean isConnectedToPeer = false;
   private TerkCommunicator terkCommunicator = null;
   private Component parent;
   //END Gui builders

   private RoboticonMessengerServerController roboticonMessengerServerController;

   // peer info stuff
   private final PeerImageFactory peerImageFactory = new HTTPPeerImageFactory(RESOURCES.getString("terk-web-site.host"),
                                                                              RESOURCES.getString("terk-web-site.avatar-image-loader-prefix"));

   private final PeerInfoModel peerInfoModel = new PeerInfoModel(peerImageFactory);

   private final PeerInfoView peerInfoView = new PeerInfoView(peerInfoModel, false);
   private final PeerInfoController peerInfoController =
         new PeerInfoController()
         {
         public void setAttribute(final String key, final String value)
            {
            if (roboticonMessengerServerController != null)
               {
               roboticonMessengerServerController.getPeerInfoService().setAttribute(key, value);
               }
            }
         };

   // messaging stuff
   private final RoboticonMessengerModel roboticonMessengerModel = new RoboticonMessengerModel(false);

   private final RoboticonMessengerController roboticonMessengerController =
         new RoboticonMessengerController()
         {
         public void sendPublicMessage(final String parentMessageId,
                                       final ClientRoboticonMessage clientRoboticonMessage)
            {
            if (roboticonMessengerServerController != null)
               {
               roboticonMessengerServerController
                     .getRoboticonMessengerService().sendPublicMessage(
                     parentMessageId, clientRoboticonMessage);
               }
            }

         public void sendPrivateMessage(final String parentMessageId,
                                        final String recipientUserId,
                                        final ClientRoboticonMessage clientRoboticonMessage)
            {
            if (roboticonMessengerServerController != null)
               {
               roboticonMessengerServerController
                     .getRoboticonMessengerService().sendPrivateMessage(
                     parentMessageId, recipientUserId,
                     clientRoboticonMessage);
               }
            }
         };
   private final RoboticonManagerModel expressionsRoboticonManagerModel = new RoboticonManagerModel(RoboticonDirectory.RoboticonType.EXPRESSION);

   private final RoboticonManagerModel sequenceRoboticonManagerModel = new RoboticonManagerModel(RoboticonDirectory.RoboticonType.SEQUENCE);

   private final RoboticonMessengerView roboticonMessengerView = new RoboticonMessengerView(this,
                                                                                            roboticonMessengerController,
                                                                                            roboticonMessengerModel,
                                                                                            peerInfoModel,
                                                                                            new RoboticonManagerController()
                                                                                            {
                                                                                            public void addRoboticons(final Collection<RoboticonFile> roboticonFiles)
                                                                                               {
                                                                                               if (roboticonFiles.size() > 0)
                                                                                                  {
                                                                                                  sequenceRoboticonManagerModel.addRoboticons(roboticonFiles);
                                                                                                  expressionsRoboticonManagerModel.addRoboticons(roboticonFiles);
                                                                                                  }
                                                                                               }
                                                                                            });

   public RoboticonMessenger(final Component parent)
      {
      this.parent = parent;
      final ServantFactory relayServantFactory = new MessengerServantFactory();

      // create the relay manager
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(APPLICATION_NAME,
                                                                  ICE_RELAY_PROPERTIES_FILE,
                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                  relayServantFactory);

      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(this);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener());

      peerConnectionEventListeners = new ArrayList<PeerConnectionEventListener>();
      peerConnectionEventListeners.add(new MyPeerConnectionEventListener());

      // CONNECTION WIZARD ---------------------------------------------------------------------------------------------

      // Create the connection-to-robot wizard
      connectToRobotWizard = new Wizard();
      connectToRobotWizard.getDialog().setTitle(RESOURCES.getString("peer-connection-wizard.title"));

      // create the various pages in the wizard
      //final RelayLoginFormDescriptor wizardDescriptorRelayLoginForm = new RelayLoginFormDescriptor(relayCommunicatorManager);
      final PeerChooserDescriptor wizardDescriptorPeerChooser = new PeerChooserDescriptor(relayCommunicatorManager, false);

      // register the pages
      //connectToRobotWizard.registerWizardPanel(RelayLoginFormDescriptor.IDENTIFIER, wizardDescriptorRelayLoginForm);
      connectToRobotWizard.registerWizardPanel(PeerChooserDescriptor.IDENTIFIER, wizardDescriptorPeerChooser);

      // ---------------------------------------------------------------------------------------------------------------

      initialize();
      }

   private void initialize()
      {
      roboticonMessengerModel.addRoboticonMessengerListener(roboticonMessengerView);
      peerInfoModel.addPeerInfoListener(peerInfoView);
      peerInfoModel.addPeerInfoListener(roboticonMessengerView);

      connectedUserIdLabel.setFont(GUIConstants.FONT_MEDIUM_BOLD);
      //connectedUserIdLabel.setBounds(new Rectangle(93, 2, 197, 18));
      connectedUserIdLabel.setForeground(new Color(0xd5d5d5));

      this.connectDisconnectButton = new ConnectDisconnectButton(RESOURCES.getString("button.label.login"),
                                                                 RESOURCES.getString("button.label.logout"));
      this.connectDisconnectButton.addActionListener(new ConnectDisconnectActionListener(this));

      this.connectDisconnectButton.setEnabled(false);
      colorButton = GUIConstants.createButton(RESOURCES.getString("button.label.my-color"));
      colorButton.addActionListener(
            new ActionListener()
            {

            public void actionPerformed(final ActionEvent e)
               {
               final ColorChooserDialog colorChooserDialog = new ColorChooserDialog();
               colorChooserDialog.setLocationRelativeTo(colorButton);
               colorChooserDialog.setOriginalColor(peerInfoModel.getUserColor(roboticonMessengerModel.getUserId()));
               if (colorChooserDialog.showDialog())
                  {
                  //set new color
                  Color newColor = colorChooserDialog.getNewColor();
                  if (newColor.equals(Color.WHITE))
                     {
                     newColor = new Color(224, 224, 224);//todo: decide if this is the desired behavior
                     }
                  peerInfoController.setAttribute("hexColor", ColorUtils.getHexColor(newColor));
                  //userIconButton.setBackground(newColor);
                  connectedUserIdLabel.setForeground(newColor);
                  }
               }
            });

      messageHistoryPanel = new JPanel(new BorderLayout());
      messageHistoryPanel.setPreferredSize(new Dimension(430, 350));

      final JPanel writeMessagePanel = new JPanel(new BorderLayout());
      writeMessagePanel.setPreferredSize(new Dimension(430, 250));
      writeMessagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
      writeMessagePanel.add(roboticonMessengerView.getRoboticonAndMessageSubmissionComponent(), BorderLayout.CENTER);

      final JPanel userListPanel = new JPanel(new BorderLayout());
      userListPanel.setPreferredSize(new Dimension(150, 550));
      userListPanel.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
      //userListPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      userListPanel.add(peerInfoView.getListComponent(), BorderLayout.CENTER);

      final JPanel messengerComponentPanel = new JPanel();
      messengerComponentPanel.setLayout(new BoxLayout(messengerComponentPanel, BoxLayout.Y_AXIS));
      messengerComponentPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      messengerComponentPanel.add(messageHistoryPanel);
      messengerComponentPanel.add(writeMessagePanel);

      final JPanel messengerPanel = new JPanel();
      messengerPanel.setLayout(new BoxLayout(messengerPanel, BoxLayout.X_AXIS));
      messengerPanel.add(userListPanel);
      messengerPanel.add(messengerComponentPanel);

      final JPanel connectionPanel = new JPanel();
      connectionPanel.setBackground(Color.WHITE);
      connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.X_AXIS));

      connectionPanel.add(this.userIconLabel);
      connectionPanel.add(this.connectedUserIdLabel);
      connectionPanel.add(Box.createGlue());
      connectionPanel.add(this.connectDisconnectButton);
      connectionPanel.add(colorButton);

      this.setVisible(true);
      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.add(connectionPanel);
      this.add(messengerPanel);

      setIsConnectedToPeer(false);
      }

   public void setRelaySupported(final boolean isSupported)
      {
      relayCommunicatorManager.setIsSupported(isSupported);
      if (isSupported)
         {
         relayCommunicatorManager.createCommunicator();
         }
      }

   public void addPeerConnectionEventListener(final PeerConnectionEventListener pcel)
      {
      if (relayCommunicatorManager.getRelayCommunicator() != null)
         {
         relayCommunicatorManager.getRelayCommunicator().addPeerConnectionEventListener(pcel);
         }
      else
         {
         peerConnectionEventListeners.add(pcel);
         }
      }

   private void setIsConnectedToPeer(final boolean isConnectedToPeer)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         setIsConnectedToPeerWorkhorse(isConnectedToPeer);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  setIsConnectedToPeerWorkhorse(isConnectedToPeer);
                  }
               });
         }
      }

   private void setIsConnectedToPeerWorkhorse(final boolean isConnectedToPeer)
      {
      this.isConnectedToPeer = isConnectedToPeer;
      this.connectDisconnectButton.setConnectionState(isConnectedToPeer);
      this.colorButton.setEnabled(isConnectedToPeer);
      this.roboticonMessengerView.setEnabled(isConnectedToPeer);
      this.peerInfoView.setEnabled(isConnectedToPeer);

      if (terkCommunicator != null)
         {
         String userId = ((RelayCommunicator)terkCommunicator).getUserId();

         if (userId == null || userId.length() <= 0)
            {
            userId = RESOURCES.getString("unknown-user-id");
            }

         this.connectedUserIdLabel.setText((isConnectedToPeer ? userId : "Not Logged In"));
         }

      if (!isConnectedToPeer)
         {
         connectedUserIdLabel.setForeground(new Color(0xd5d5d5));
         userIconLabel.setIcon(null);
         }
      }

   public void attachRoboticon(final FileEntry o)
      {
      roboticonMessengerView.attachRoboticon(o);
      }

   public ListModel getGlobalExpressionsModel()
      {
      return this.expressionsRoboticonManagerModel;
      }

   public ListModel getGlobalSequencesModel()
      {
      return this.sequenceRoboticonManagerModel;
      }

   public void setHistoryVisible(final boolean visible)
      {
      if (visible)
         {
         messageHistoryPanel.add(roboticonMessengerView.getMessageHistoryComponent(), BorderLayout.CENTER);
         }
      else
         {
         messageHistoryPanel.removeAll();
         }
      }

   public void shutdown()
      {
      if (terkCommunicator != null)
         {
         terkCommunicator.disconnectFromPeers();
         }
      terkCommunicator = null;
      }

   private void connectToServer()
      {
      LOG.debug("Attempting to connect to server");
      boolean showPeerChooserDialog = true;
      try
         {
         int elapsed = 0;
         Set<PeerIdentifier> peers;

         do
            {
            peers = ((RelayCommunicator)terkCommunicator).getMyAvailablePeers();
            try
               {
               elapsed += 50;
               Thread.sleep(50);
               }
            catch (InterruptedException x)
               {
               LOG.debug("RoboticonMessenger.connectToServer(): InterruptedException while sleeping: ", x);
               }
            }
         while (peers.size() == 0 && elapsed <= 2000);

         if (peers.size() == 1)
            {
            final PeerIdentifier peer = (PeerIdentifier)peers.toArray()[0];
            if (terkCommunicator != null)
               {
               terkCommunicator.connectToPeer(peer.userId);
               showPeerChooserDialog = false;
               }
            }
         else if (peers.size() == 0)
            {
            relayCommunicatorManager.logout();
            SwingUtilities.invokeLater(new Runnable()
            {
            public void run()
               {
               JOptionPane.showMessageDialog(parent, "Messenger server is currently offline, please try again later.", "Login Failed", JOptionPane.WARNING_MESSAGE);
               }
            });

            showPeerChooserDialog = false;
            LOG.debug("No avaiable peers");
            }
         }
      catch (PeerException x)
         {
         LOG.debug("Peer exception in auto-connect", x);
         }

      SwingUtilities.invokeLater(new Runnable()
      {
      public void run()
         {
         connectDisconnectButton.setEnabled(true);
         }
      });

      if (relayCommunicatorManager.isLoggedIn() && showPeerChooserDialog)
         {
         connectToRobotWizard.setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
         connectToRobotWizard.showModalDialog();
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class ConnectDisconnectActionListener extends AbstractTimeConsumingAction
      {
      private ConnectDisconnectActionListener(final Component component)
         {
         super(component);
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void executeGUIActionBefore()
         {
         connectDisconnectButton.setEnabled(false);
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {

         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("ConnectDisconnectActionListener$ConnectDisconnectActionListener.executeTimeConsumingAction()");
         if (isConnectedToPeer)
            {
            LOG.debug("Logging out of relay");
            relayCommunicatorManager.logout();
            SwingUtilities.invokeLater(new Runnable()
            {
            public void run()
               {
               connectDisconnectButton.setEnabled(true);
               }
            });
            }
         else
            {
            LOG.debug("Logging into the relay");
            final class LoginData
               {
               private String user;
               private String password;

               LoginData()
                  {
                  user = "";
                  password = "";
                  }
               }

            final LoginData loginData = new LoginData();

            try
               {
               SwingUtilities.invokeAndWait(
                     new Runnable()
                     {
                     public void run()
                        {
                        final JPanel connectionPanel;

                        final JLabel userLabel = GUIConstants.createLabel("User:  ");
                        userLabel.setHorizontalAlignment(JLabel.RIGHT);
                        final JTextField userField = new JTextField("");
                        final JLabel passwordLabel = GUIConstants.createLabel("Password:  ");
                        passwordLabel.setHorizontalAlignment(JLabel.RIGHT);
                        final JTextField passwordField = new JPasswordField("");
                        connectionPanel = new JPanel();
                        connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.X_AXIS));
                        final JPanel namePanel = new JPanel();
                        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
                        namePanel.add(userLabel);
                        namePanel.add(GUIConstants.createRigidSpacer(4));
                        namePanel.add(passwordLabel);
                        final JPanel fieldPanel = new JPanel();
                        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
                        fieldPanel.add(userField);
                        fieldPanel.add(GUIConstants.createRigidSpacer(4));
                        fieldPanel.add(passwordField);

                        connectionPanel.add(namePanel);
                        connectionPanel.add(fieldPanel);

                        if (JOptionPane.showOptionDialog(parent, connectionPanel,
                                                         "Login to Messenger",
                                                         JOptionPane.OK_CANCEL_OPTION,
                                                         JOptionPane.INFORMATION_MESSAGE,
                                                         null, new String[]{"Login", "Cancel"},
                                                         "Login") == 0)
                           {
                           loginData.user = userField.getText();
                           loginData.password = passwordField.getText();
                           }
                        }
                     });
               }
            catch (InterruptedException x)
               {
               LOG.debug("Exception in login pane thread", x);
               }
            catch (InvocationTargetException x)
               {
               LOG.debug("Exception in login pane thread", x);
               }

            if (loginData.user.trim().length() == 0 ||
                relayCommunicatorManager.login(loginData.user, loginData.password) == false)
               {
               SwingUtilities.invokeLater(new Runnable()
               {
               public void run()
                  {
                  connectDisconnectButton.setEnabled(true);
                  }
               });
               }
            }
         return null;
         }
      }

   private final class MyUserConnectionEventListener extends UserConnectionEventAdapter
      {
      public void handleRelayLogoutEvent()
         {
         roboticonMessengerServerController = null;
         roboticonMessengerModel.clearHistory();
         peerInfoModel.setPeerInfo(null);
         setIsConnectedToPeer(false);
         }

      public void handleForcedLogoutNotificationEvent()
         {
         roboticonMessengerServerController = null;
         roboticonMessengerModel.clearHistory();
         peerInfoModel.setPeerInfo(null);
         setIsConnectedToPeer(false);
         }

      public void handleRelayLoginEvent()
         {

         String userId = ((RelayCommunicator)terkCommunicator).getUserId();

         if (userId == null || userId.length() <= 0)
            {
            userId = RESOURCES.getString("unknown-user-id");
            }

         roboticonMessengerModel.setUserId(userId);

         connectToServer();
         }
      }

   private final class MyPeerConnectionEventListener extends PeerConnectionEventAdapter
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         final TerkUserPrx terkUserProxy = TerkUserPrxHelper.checkedCast(peerObjectProxy);
         if (terkUserProxy != null)
            {
            roboticonMessengerServerController = new RoboticonMessengerServerController(peerUserId, terkUserProxy, terkCommunicator);

            if (roboticonMessengerServerController.isRoboticonMessagingSupported())
               {
               final List<RoboticonMessage> messageHistory = roboticonMessengerServerController.getRoboticonMessengerService().getMessageHistory();
               final long lastLogoutTimestamp = roboticonMessengerServerController.getRoboticonMessengerService().getLastLogoutTimestamp();
               roboticonMessengerModel.setMessageHistory(messageHistory, lastLogoutTimestamp);
               }
            if (roboticonMessengerServerController.isPeerInfoSupported())
               {
               final List<PeerInfo> allPeerInfo = roboticonMessengerServerController.getPeerInfoService().getPeerInfo();
               peerInfoModel.setPeerInfo(allPeerInfo);
               connectedUserIdLabel.setForeground(peerInfoModel.getUserColor(roboticonMessengerModel.getUserId()));
               userIconLabel.setIcon(peerInfoModel.getUserIcon(roboticonMessengerModel.getUserId(), 32, 32));
               }

            setIsConnectedToPeer(true);
            }
         else
            {
            LOG.info("Ignoring peer [" + peerUserId + "] since it is not a TerkUser.");
            }
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         LOG.debug("RoboticonMessnger$MyPeerConnectionEventListener.handlePeerDisconnectedEvent()");
         roboticonMessengerServerController = null;
         roboticonMessengerModel.clearHistory();
         peerInfoModel.setPeerInfo(null);
         setIsConnectedToPeer(false);
         }
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {

      private MyTerkCommunicatorCreationEventListener()
         {
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // add the peer connection event listener
         for (final PeerConnectionEventListener pcel : peerConnectionEventListeners)
            {
            terkCommunicator.addPeerConnectionEventListener(pcel);
            }

         if (terkCommunicator instanceof RelayCommunicator)
            {
            // If this is a RelayCommunicator, then register the user connection event listener (so we can properly disable
            // peer connections when logging out of the relay without having disconnected from peers first).
            ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(userConnectionEventListener);
            connectDisconnectButton.setEnabled(true);
            }
         // set the current TerkCommunicator
         RoboticonMessenger.this.terkCommunicator = terkCommunicator;
         }
      }

   final class MessengerServantFactory implements ServantFactory
      {
      public Servants createServants(final TerkCommunicator communicator)
         {

         final TerkUserServant mainServant = new TerkUserServant(communicator);

         // create the main servant proxy (only...no secondary servant proxies are needed since this client doesn't currently provide any services)
         final ObjectPrx mainServantProxy = communicator.createServantProxy(mainServant);
         final TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

         // create secondary servants and their proxies
         final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

         final ObjectImpl roboticonMessagingClientServiceServant = new RoboticonMessagingClientServiceServant(roboticonMessengerModel);
         final ObjectPrx untypedRoboticonMessagingClientServiceServantProxy = terkCommunicator.createServantProxy(roboticonMessagingClientServiceServant);
         final RoboticonMessagingClientServicePrx roboticonMessagingClientServicePrx = RoboticonMessagingClientServicePrxHelper.uncheckedCast(untypedRoboticonMessagingClientServiceServantProxy);
         secondaryServantProxies.add(roboticonMessagingClientServicePrx);
         mainServant.registerServiceServant(roboticonMessagingClientServiceServant, roboticonMessagingClientServicePrx);

         final ObjectImpl peerInfoClientServiceServant = new PeerInfoClientServiceServant(peerInfoModel);
         final ObjectPrx untypedPeerInfoClientServiceServantProxy = terkCommunicator.createServantProxy(peerInfoClientServiceServant);
         final PeerInfoClientServicePrx peerInfoClientServiceServantProxy = PeerInfoClientServicePrxHelper.uncheckedCast(untypedPeerInfoClientServiceServantProxy);
         secondaryServantProxies.add(peerInfoClientServiceServantProxy);
         mainServant.registerServiceServant(peerInfoClientServiceServant, peerInfoClientServiceServantProxy);

         return new Servants(mainServantPrx, mainServantPrx, secondaryServantProxies);
         }
      }
   }
