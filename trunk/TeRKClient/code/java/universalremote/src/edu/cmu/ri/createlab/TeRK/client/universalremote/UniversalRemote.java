package edu.cmu.ri.createlab.TeRK.client.universalremote;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import Ice.ObjectPrx;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel.ControlPanelManager;
import edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel.ControlPanelManagerImpl;
import edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel.ControlPanelManagerView;
import edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel.ControlPanelManagerViewEventListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFile;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFileManagerModel;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFileManagerView;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.ConnectDisconnectButton;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.DirectConnectDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerConnectionMethodDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.RelayLoginFormDescriptor;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.UserConnectionEventAdapter;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public final class UniversalRemote
   {
   private static final Logger LOG = Logger.getLogger(UniversalRemote.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(UniversalRemote.class.getName());

   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/universalremote/UniversalRemote.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/universalremote/UniversalRemote.relay.ice.properties";
   private static final String ICE_OBJECT_ADAPTER_NAME = "Terk.User";

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame(APPLICATION_NAME);

               final UniversalRemote universalRemote = new UniversalRemote(jFrame);

               // add the root panel to the JFrame
               jFrame.add(universalRemote.getPanel());

               // set various properties for the JFrame
               jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrame.setBackground(Color.WHITE);
               jFrame.setResizable(true);
               jFrame.addWindowListener(
                     new WindowAdapter()
                     {
                     public void windowClosing(final WindowEvent event)
                        {
                        // ask if the user really wants to exit
                        final int selectedOption = JOptionPane.showConfirmDialog(jFrame,
                                                                                 RESOURCES.getString("dialog.message.exit-confirmation"),
                                                                                 RESOURCES.getString("dialog.title.exit-confirmation"),
                                                                                 JOptionPane.YES_NO_OPTION,
                                                                                 JOptionPane.QUESTION_MESSAGE);

                        if (selectedOption == JOptionPane.YES_OPTION)
                           {
                           final SwingWorker worker =
                                 new SwingWorker()
                                 {
                                 public Object construct()
                                    {
                                    universalRemote.shutdown();
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
                     });

               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   private final JFrame jFrame;
   private final JPanel stagePanel = new JPanel();
   private final JPanel mainPanel = new JPanel();
   private final Collection<PeerConnectionEventListener> peerConnectionEventListeners;
   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final Wizard connectToRobotWizard;
   private final ConnectDisconnectButton connectDisconnectButton;
   private final MyPeerConnectionEventListener peerConnectionEventListener = new MyPeerConnectionEventListener();
   private final MyUserConnectionEventListener userConnectionEventListener = new MyUserConnectionEventListener();
   private final UniversalRemoteServiceFactory universalRemoteServiceFactory = new UniversalRemoteServiceFactory();
   private final ControlPanelManager controlPanelManager = new ControlPanelManagerImpl();
   private final ControlPanelManagerView controlPanelManagerView = new ControlPanelManagerView(controlPanelManager);
   private final ExpressionFileManagerModel expressionFileManagerModel = new ExpressionFileManagerModel();
   private final ExpressionFileManagerView expressionFileManagerView = new ExpressionFileManagerView(expressionFileManagerModel);
   private final ExpressionFileManagerControlsController expressionFileManagerControlsController =
         new ExpressionFileManagerControlsController()
         {
         public void openExpression(final XmlExpression expression)
            {
            controlPanelManager.loadExpression(expression);
            }

         public void deleteExpression(final ExpressionFile expressionFile)
            {
            expressionFileManagerModel.deleteExpression(expressionFile);
            }
         };
   private final ExpressionFileManagerControlsView expressionFileManagerControlsView;
   private final StageControlsView stageControlsView =
         new StageControlsView(
               new StageControlsController()
               {
               public void clearControlPanels()
                  {
                  controlPanelManager.reset();
                  }

               public void refreshControlPanels()
                  {
                  controlPanelManager.refresh();
                  }

               public void saveExpression()
                  {
                  expressionFileManagerModel.saveExpression(controlPanelManager.buildExpression(), jFrame);
                  }
               }
         );
   private final Runnable jFramePackingRunnable =
         new Runnable()
         {
         public void run()
            {
            jFrame.pack();
            }
         };
   private boolean isConnectedToPeer = false;
   private ServiceManager serviceManager = null;
   private TerkCommunicator terkCommunicator = null;

   public UniversalRemote(final JFrame jFrame)
      {
      this.jFrame = jFrame;

      expressionFileManagerControlsView = new ExpressionFileManagerControlsView(jFrame,
                                                                                expressionFileManagerView,
                                                                                expressionFileManagerModel,
                                                                                expressionFileManagerControlsController);
      // COMMUNICATIONS ------------------------------------------------------------------------------------------------

      // create the ServantFactory instances
      final ServantFactory directConnectServantFactory = new UniversalRemoteServantFactory();
      final ServantFactory relayServantFactory = new UniversalRemoteServantFactory();

      // create the direct-connect manager
      directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(APPLICATION_NAME,
                                                                                  ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                                  directConnectServantFactory);

      // create the relay manager
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(APPLICATION_NAME,
                                                                  ICE_RELAY_PROPERTIES_FILE,
                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                  relayServantFactory);

      // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
      // when various direct-connect-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(jFrame);
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(jFrame);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);

      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(relayCommunicatorManager));
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(directConnectCommunicatorManager));

      peerConnectionEventListeners = new ArrayList<PeerConnectionEventListener>();
      peerConnectionEventListeners.add(peerConnectionEventListener);

      // CONNECTION WIZARD ---------------------------------------------------------------------------------------------

      // Create the connection-to-robot wizard
      connectToRobotWizard = new Wizard(jFrame);
      connectToRobotWizard.getDialog().setTitle(RESOURCES.getString("peer-connection-wizard.title"));

      // create the various pages in the wizard
      final WizardPanelDescriptor wizardDescriptorPeerConnectionMethod = new PeerConnectionMethodDescriptor();
      final DirectConnectDescriptor wizardDescriptorDirectConnect = new DirectConnectDescriptor(directConnectCommunicatorManager);
      final RelayLoginFormDescriptor wizardDescriptorRelayLoginForm = new RelayLoginFormDescriptor(relayCommunicatorManager);
      final PeerChooserDescriptor wizardDescriptorPeerChooser = new PeerChooserDescriptor(relayCommunicatorManager);

      // register the pages
      connectToRobotWizard.registerWizardPanel(PeerConnectionMethodDescriptor.IDENTIFIER, wizardDescriptorPeerConnectionMethod);
      connectToRobotWizard.registerWizardPanel(DirectConnectDescriptor.IDENTIFIER, wizardDescriptorDirectConnect);
      connectToRobotWizard.registerWizardPanel(RelayLoginFormDescriptor.IDENTIFIER, wizardDescriptorRelayLoginForm);
      connectToRobotWizard.registerWizardPanel(PeerChooserDescriptor.IDENTIFIER, wizardDescriptorPeerChooser);

      // CONTROL PANEL MANAGER -----------------------------------------------------------------------------------------

      // make sure we re-pack the jFrame whenever the control panel manager changes
      controlPanelManagerView.addControlPanelManagerViewEventListener(
            new ControlPanelManagerViewEventListener()
            {
            public void handleLayoutChange()
               {
               if (SwingUtilities.isEventDispatchThread())
                  {
                  jFramePackingRunnable.run();
                  }
               else
                  {
                  SwingUtilities.invokeLater(jFramePackingRunnable);
                  }
               }
            });

      // GUI WIDGETS ---------------------------------------------------------------------------------------------------

      connectDisconnectButton = new ConnectDisconnectButton();
      connectDisconnectButton.addActionListener(new ConnectDisconnectActionListener(jFrame));

      expressionFileManagerView.setEnabled(false);
      expressionFileManagerControlsView.setEnabled(false);

      stageControlsView.setEnabled(false);

      // LAYOUT --------------------------------------------------------------------------------------------------------

      final Component stageSpacer = GUIConstants.createRigidSpacer();
      final GroupLayout stagePanelLayout = new GroupLayout(stagePanel);
      stagePanel.setLayout(stagePanelLayout);
      stagePanel.setBackground(Color.WHITE);

      stagePanelLayout.setHorizontalGroup(
            stagePanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(stageControlsView.getComponent())
                  .add(stageSpacer)
                  .add(controlPanelManagerView.getComponent())
      );
      stagePanelLayout.setVerticalGroup(
            stagePanelLayout.createSequentialGroup()
                  .add(stageControlsView.getComponent())
                  .add(stageSpacer)
                  .add(controlPanelManagerView.getComponent())
      );

      final Component expressionFileManagerPanelSpacer = GUIConstants.createRigidSpacer();
      final JPanel expressionFileManagerPanel = new JPanel();
      final TitledBorder titledBorder = BorderFactory.createTitledBorder(RESOURCES.getString("expressions-file-manager-panel.title"));
      titledBorder.setTitleFont(GUIConstants.FONT_NORMAL);
      expressionFileManagerPanel.setBorder(BorderFactory.createTitledBorder(titledBorder));
      expressionFileManagerPanel.setBackground(Color.WHITE);

      final GroupLayout expressionFileManagerPanelLayout = new GroupLayout(expressionFileManagerPanel);
      expressionFileManagerPanel.setLayout(expressionFileManagerPanelLayout);
      expressionFileManagerPanelLayout.setHorizontalGroup(
            expressionFileManagerPanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(expressionFileManagerControlsView.getComponent())
                  .add(expressionFileManagerPanelSpacer)
                  .add(expressionFileManagerView.getComponent())
      );
      expressionFileManagerPanelLayout.setVerticalGroup(
            expressionFileManagerPanelLayout.createSequentialGroup()
                  .add(expressionFileManagerControlsView.getComponent())
                  .add(expressionFileManagerPanelSpacer)
                  .add(expressionFileManagerView.getComponent())
      );

      final JPanel controlPanel = new JPanel();
      controlPanel.setBackground(Color.WHITE);
      final GroupLayout controlPanelLayout = new GroupLayout(controlPanel);
      controlPanel.setLayout(controlPanelLayout);
      controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(connectDisconnectButton)
                  .add(expressionFileManagerPanel)
      );
      controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createSequentialGroup()
                  .add(connectDisconnectButton)
                  .add(expressionFileManagerPanel)
      );

      final GroupLayout mainPanelLayout = new GroupLayout(mainPanel);
      mainPanel.setLayout(mainPanelLayout);
      mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      mainPanel.setBackground(Color.WHITE);

      mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createSequentialGroup()
                  .add(stagePanel)
                  .add(controlPanel)
      );
      mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
                  .add(stagePanel)
                  .add(controlPanel)
      );
      }

   private JPanel getPanel()
      {
      return mainPanel;
      }

   public Component getStageComponent()
      {
      return stagePanel;
      }

   public void loadExpression(final XmlExpression expression)
      {
      controlPanelManager.loadExpression(expression);
      }

   /**
    * If the given <code>isSupported</code> flag is <code>true</code>, this method flags direct-connect as being
    * supported and creates the DirectConnectCommunicator; if <code>false</code>, it shuts down the communicator and
    * flags direct-connect as unsupported.
    */
   public void setDirectConnectSupported(final boolean isSupported)
      {
      directConnectCommunicatorManager.setIsSupported(isSupported);
      if (isSupported)
         {
         directConnectCommunicatorManager.createCommunicator();
         }
      }

   public void directConnectToPeer(final String hostname) throws DuplicateConnectionException, PeerConnectionFailedException
      {
      LOG.debug("UniversalRemote.directConnectToPeer()");
      directConnectCommunicatorManager.getDirectConnectCommunicator().connectToPeer(hostname);
      }

   public void shutdown()
      {
      directConnectCommunicatorManager.shutdownCommunicator();
      relayCommunicatorManager.shutdownCommunicator();
      }

   public void addPeerConnectionEventListener(final PeerConnectionEventListener listener)
      {
      if (directConnectCommunicatorManager.getDirectConnectCommunicator() != null)
         {
         directConnectCommunicatorManager.getDirectConnectCommunicator().addPeerConnectionEventListener(listener);
         }
      else
         {
         peerConnectionEventListeners.add(listener);
         }
      }

   private final class MyUserConnectionEventListener extends UserConnectionEventAdapter
      {
      public void handleRelayLogoutEvent()
         {
         setDisconnectedFromPeer();
         }

      public void handleForcedLogoutNotificationEvent()
         {
         setDisconnectedFromPeer();
         }

      private void setDisconnectedFromPeer()
         {
         isConnectedToPeer = false;
         connectDisconnectButton.setConnectionState(false);
         expressionFileManagerView.setEnabled(false);
         stageControlsView.setEnabled(false);
         expressionFileManagerControlsView.setEnabled(false);
         controlPanelManager.peerDisconnected();
         }
      }

   private final class MyPeerConnectionEventListener extends PeerConnectionEventAdapter
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         isConnectedToPeer = true;
         serviceManager = new IceServiceManager(peerUserId,
                                                TerkUserPrxHelper.uncheckedCast(peerObjectProxy),
                                                terkCommunicator,
                                                universalRemoteServiceFactory);

         connectDisconnectButton.setConnectionState(true);
         expressionFileManagerView.setEnabled(true);
         stageControlsView.setEnabled(true);
         expressionFileManagerControlsView.setEnabled(true);
         controlPanelManager.peerConnected(serviceManager);
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         serviceManager = null;

         isConnectedToPeer = false;
         connectDisconnectButton.setConnectionState(false);
         expressionFileManagerView.setEnabled(false);
         stageControlsView.setEnabled(false);
         expressionFileManagerControlsView.setEnabled(false);
         controlPanelManager.peerDisconnected();
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class ConnectDisconnectActionListener extends AbstractTimeConsumingAction
      {
      private ConnectDisconnectActionListener(final Component component)
         {
         super(component);
         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("UniversalRemote$ConnectDisconnectActionListener.executeTimeConsumingAction()");
         if (isConnectedToPeer)
            {
            // disconnect from peers
            if (terkCommunicator != null)
               {
               terkCommunicator.disconnectFromPeers();
               }
            }
         else
            {
            // show the wizard
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     // determine which screen to display in the wizard
                     if (relayCommunicatorManager.isCreated())
                        {
                        if (relayCommunicatorManager.isLoggedIn())
                           {
                           connectToRobotWizard.setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
                           }
                        else
                           {
                           connectToRobotWizard.setCurrentPanel(RelayLoginFormDescriptor.IDENTIFIER);
                           }
                        }
                     else if (directConnectCommunicatorManager.isCreated())
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
         return null;
         }
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      private final TerkCommunicatorManager otherTerkCommunicatorManager;

      private MyTerkCommunicatorCreationEventListener(final TerkCommunicatorManager otherTerkCommunicatorManager)
         {
         this.otherTerkCommunicatorManager = otherTerkCommunicatorManager;
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // add the peer connection event listener

         for (final PeerConnectionEventListener listener : peerConnectionEventListeners)
            {
            terkCommunicator.addPeerConnectionEventListener(listener);
            }

         // If this is a RelayCommunicator, then register the user connection event listener (so we can properly disable
         // peer connections when logging out of the relay without having disconnected from peers first)
         if (terkCommunicator instanceof RelayCommunicator)
            {
            ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(userConnectionEventListener);
            }

         // creation of this communicator means we should shut down the other communicator since, for this app at least,
         // we only ever want to be able to connect via one mode at a time.
         otherTerkCommunicatorManager.shutdownCommunicator();

         // set the current TerkCommunicator
         UniversalRemote.this.terkCommunicator = terkCommunicator;
         }
      }
   }
