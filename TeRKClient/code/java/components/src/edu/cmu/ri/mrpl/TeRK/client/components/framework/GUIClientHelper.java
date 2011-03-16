package edu.cmu.ri.mrpl.TeRK.client.components.framework;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import Ice.ConnectionLostException;
import Ice.ObjectPrx;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.DirectConnectDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerConnectionMethodDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.RelayLoginFormDescriptor;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.mrpl.TeRK.QwerkPrx;
import edu.cmu.ri.mrpl.TeRK.QwerkPrxHelper;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.connectionstate.ConnectionStatePanel;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.DefaultVideoStreamPlayer;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.IceVideoStreamSubscriber;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.SwingVideoStreamViewport;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamPlayer;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamViewport;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.peer.ConnectionEventDistributorHelperAdapter;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.peer.UserConnectionEventListener;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>GUIClientHelper</code> provides functionality common to GUI clients
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class GUIClientHelper
   {
   private static final Logger LOG = Logger.getLogger(GUIClientHelper.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(GUIClientHelper.class.getName());

   private static final String OBJECT_ADAPTER_NAME = "Base.GUI.Client";

   private static interface TerkCommunicatorGetter
      {
      TerkCommunicator getTerkCommunicator();
      }

   private final Component parentJFrame;
   private GUIClientHelperEventHandler eventHandler;
   private final TerkCommunicatorGetter directConnectCommunicatorGetter =
         new TerkCommunicatorGetter()
         {
         public TerkCommunicator getTerkCommunicator()
            {
            return directConnectCommunicator;
            }
         };
   private final TerkCommunicatorGetter relayCommunicatorGetter =
         new TerkCommunicatorGetter()
         {
         public TerkCommunicator getTerkCommunicator()
            {
            return relayCommunicator;
            }
         };

   private PeerConnectionEventListener customRelayPeerConnectionEventListener = new DefaultCustomPeerConnectionEventListener(relayCommunicatorGetter);
   private PeerConnectionEventListener customDirectConnectPeerConnectionEventListener = new DefaultCustomPeerConnectionEventListener(directConnectCommunicatorGetter);

   private final boolean isDirectConnectSupported;

   private final DirectConnectDescriptor wizardDescriptorDirectConnect;

   private final JButton connectDisconnectButton = new JButton();

   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;

   private RelayCommunicator relayCommunicator;
   private DirectConnectCommunicator directConnectCommunicator;
   private final Wizard connectToRobotWizard;
   private boolean isConnectedToPeer;

   private TerkUserPrx terkUserServantPrx;

   private QwerkController qwerkController;

   private final IceVideoStreamSubscriber iceVideoStreamSubscriber = new IceVideoStreamSubscriber();
   private final VideoStreamViewport videoStreamViewport = new SwingVideoStreamViewport();
   private final VideoStreamPlayer videoStreamPlayer = new DefaultVideoStreamPlayer(iceVideoStreamSubscriber, videoStreamViewport);

   private final UserConnectionEventListener relayUserConnectionEventListener = new RelayUserConnectionEventListener();
   private final PeerConnectionEventListener relayPeerConnectionEventListener = new RelayPeerConnectionEventListener();
   private final PeerConnectionEventListener directConnectPeerConnectionEventListener = new DirectConnectPeerConnectionEventListener();
   private final ConnectDisconnectActionListener connectDisconnectActionListener = new ConnectDisconnectActionListener();

   /** Panel which displays the current relay login state and connected peer state */
   private final ConnectionStatePanel connectionStatePanel = new ConnectionStatePanel();

   // runnables for SwingUtilities.invokeLater()
   private final Runnable setWidgetsAndActionsEnabledRunnable =
         new Runnable()
         {
         public void run()
            {
            setWidgetsAndActionsEnabledWorkhorse(true);
            }
         };

   private final Runnable setWidgetsAndActionsDisabledRunnable =
         new Runnable()
         {
         public void run()
            {
            setWidgetsAndActionsEnabledWorkhorse(false);
            }
         };

   /** Creates GUIClientHelper with no direct-connect support. */
   public GUIClientHelper(final String applicationName,
                          final String relayCommunicatorIcePropertiesFile,
                          final JFrame parentJFrame)
      {
      this(applicationName,
           relayCommunicatorIcePropertiesFile,
           null,
           parentJFrame,
           null);
      }

   public GUIClientHelper(final String applicationName,
                          final String relayCommunicatorIcePropertiesFile,
                          final String directConnectCommunicatorIcePropertiesFile,
                          final JFrame parentJFrame,
                          final GUIClientHelperEventHandler eventHandler)
      {
      this.parentJFrame = parentJFrame;
      setGUIClientHelperEventHandler(eventHandler);
      this.isDirectConnectSupported = directConnectCommunicatorIcePropertiesFile != null;

      // Make sure we have platform-specific window decorations
      JFrame.setDefaultLookAndFeelDecorated(false);

      // Create the connection-to-robot wizard
      connectToRobotWizard = new Wizard(parentJFrame);

      // create the relay manager
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(applicationName,
                                                                  relayCommunicatorIcePropertiesFile,
                                                                  OBJECT_ADAPTER_NAME);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyRelayCommunicatorCreationEventListener());

      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final UserAlertingRelayEventFailureListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(parentJFrame);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);

      // Now check whether direct-connect should be supported
      if (isDirectConnectSupported)
         {
         // create the direct-connect manager
         directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(applicationName,
                                                                                     directConnectCommunicatorIcePropertiesFile,
                                                                                     OBJECT_ADAPTER_NAME,
                                                                                     new MyDirectConnectServantFactory());
         directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyDirectConnectCommunicatorCreationEventListener());

         // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
         // when various direct-connect-related failures occur.
         final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(parentJFrame);
         directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

         // create the panels in the wizard that pertain to direct connect
         final WizardPanelDescriptor wizardDescriptorRobotConnectionMethod = new PeerConnectionMethodDescriptor();
         wizardDescriptorDirectConnect = new DirectConnectDescriptor(directConnectCommunicatorManager);
         connectToRobotWizard.registerWizardPanel(PeerConnectionMethodDescriptor.IDENTIFIER, wizardDescriptorRobotConnectionMethod);
         connectToRobotWizard.registerWizardPanel(DirectConnectDescriptor.IDENTIFIER, wizardDescriptorDirectConnect);
         }
      else
         {
         directConnectCommunicatorManager = null;
         wizardDescriptorDirectConnect = null;
         }
      // continue with setup for relay connections
      final RelayLoginFormDescriptor wizardDescriptorRelayLoginForm = new RelayLoginFormDescriptor(relayCommunicatorManager,
                                                                                                   isDirectConnectSupported);
      final PeerChooserDescriptor wizardDescriptorPeerChooser = new PeerChooserDescriptor(relayCommunicatorManager);
      connectToRobotWizard.getDialog().setTitle(RESOURCES.getString("peer-connection-wizard.title"));
      connectToRobotWizard.registerWizardPanel(RelayLoginFormDescriptor.IDENTIFIER, wizardDescriptorRelayLoginForm);
      connectToRobotWizard.registerWizardPanel(PeerChooserDescriptor.IDENTIFIER, wizardDescriptorPeerChooser);

      // set up the video viewport
      videoStreamViewport.setBorder(new LineBorder(new Color(0, 0, 0), 3));
      videoStreamViewport.setPreferredSize(GUIConstants.VIDEO_FRAME_RESOLUTION);
      videoStreamViewport.setMinimumSize(GUIConstants.VIDEO_FRAME_RESOLUTION);
      videoStreamViewport.setMaximumSize(GUIConstants.VIDEO_FRAME_RESOLUTION);

      connectDisconnectButton.setFont(GUIConstants.BUTTON_FONT);
      connectDisconnectButton.setText(RESOURCES.getString("button.label.connect"));
      connectDisconnectButton.setEnabled(true);
      connectDisconnectButton.setOpaque(false);//required for Mac
      connectDisconnectButton.addActionListener(connectDisconnectActionListener);
      }

   /**
    * Sets the {@link GUIClientHelperEventHandler} to be used.  If the given handler is <code>null</code>, then a
    * default handler is used instead.  The default handler does nothing for all events except
    * {@link GUIClientHelperEventHandler#executeBeforeDisconnectingFromQwerk()}, for which it stops all motors before
    * disconnecting.
    */
   public void setGUIClientHelperEventHandler(final GUIClientHelperEventHandler eventHandler)
      {
      if (eventHandler == null)
         {
         this.eventHandler =
               new GUIClientHelperEventHandlerAdapter()
               {
               public void executeBeforeDisconnectingFromQwerk()
                  {
                  if (getQwerkController() != null)
                     {
                     getQwerkController().getMotorService().stopMotors();
                     }
                  }
               };
         }
      else
         {
         this.eventHandler = eventHandler;
         }
      }

   public final void setCustomRelayPeerConnectionEventListener(final PeerConnectionEventListener customRelayPeerConnectionEventListener)
      {
      this.customRelayPeerConnectionEventListener = customRelayPeerConnectionEventListener;
      }

   public final void setCustomDirectConnectPeerConnectionEventListener(final PeerConnectionEventListener customDirectConnectPeerConnectionEventListener)
      {
      this.customDirectConnectPeerConnectionEventListener = customDirectConnectPeerConnectionEventListener;
      }

   public final JButton getConnectDisconnectButton()
      {
      return connectDisconnectButton;
      }

   public final ActionListener getConnectDisconnectButtonActionListener()
      {
      return connectDisconnectActionListener;
      }

   public final DirectConnectCommunicator getDirectConnectCommunicator()
      {
      return directConnectCommunicator;
      }

   public final RelayCommunicator getRelayCommunicator()
      {
      return relayCommunicator;
      }

   public final Wizard getConnectToRobotWizard()
      {
      return connectToRobotWizard;
      }

   public final void performQuitAction()
      {
      SwingUtils.warnIfNotEventDispatchThread("performQuitAction()");

      final int selectedOption = JOptionPane.showConfirmDialog(parentJFrame,
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
                  if (relayCommunicator != null)
                     {
                     relayCommunicatorManager.shutdownCommunicator();
                     }
                  if (directConnectCommunicator != null)
                     {
                     directConnectCommunicatorManager.shutdownCommunicator();
                     }
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

   /** Enables or disables all of the widgets and Actions. */
   private void setWidgetsAndActionsEnabled(final boolean isConnectedToRobot)
      {
      SwingUtilities.invokeLater(isConnectedToRobot ? setWidgetsAndActionsEnabledRunnable : setWidgetsAndActionsDisabledRunnable);
      }

   /** Enables or disables all of the widgets and Actions, but assumes it's executing in the Swing event dispatch thread. */
   private void setWidgetsAndActionsEnabledWorkhorse(final boolean isConnectedToRobot)
      {
      this.isConnectedToPeer = isConnectedToRobot;
      connectDisconnectButton.setText(isConnectedToRobot ? RESOURCES.getString("button.label.disconnect") : RESOURCES.getString("button.label.connect"));
      eventHandler.toggleGUIElementState(isConnectedToRobot);
      }

   /** Returns the {@link VideoStreamPlayer} used to control the video. */
   public final VideoStreamPlayer getVideoStreamPlayer()
      {
      return videoStreamPlayer;
      }

   /** Returns the {@link VideoStreamViewport} used to display the video. */
   public final VideoStreamViewport getVideoStreamViewport()
      {
      return videoStreamViewport;
      }

   /** Returns the {@link Component} of the {@link VideoStreamViewport} used to display the video. */
   public final Component getVideoStreamViewportComponent()
      {
      return videoStreamViewport.getComponent();
      }

   /**
    * Returns the {@link QwerkController} used to control the qwerk (may be <code>null</code>, such as when not
    * connected to a Qwerk).
    */
   public final QwerkController getQwerkController()
      {
      return qwerkController;
      }

   private void setQwerkController(final QwerkController qwerkController)
      {
      final VideoStreamService videoStreamService = (qwerkController == null) ? null : qwerkController.getVideoStreamService();
      iceVideoStreamSubscriber.setVideoStreamService(videoStreamService);
      this.qwerkController = qwerkController;
      }

   public final ConnectionStatePanel getConnectionStatePanel()
      {
      return connectionStatePanel;
      }

   /**
    * Attempts to connect to the peer specified by the given <code>peerIdentifier</code> using the Direct Connect
    * connection model.  The connection is "headless" because it establishes the connection without having to display
    * the connection wizard.
    *
    * @param peerIdentifier the hostname or IP address of the peer
    */
   public void doHeadlessConnectToPeer(final String peerIdentifier)
      {
      wizardDescriptorDirectConnect.doHeadlessConnectToPeer(peerIdentifier);
      }

   private final class MyDirectConnectCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void beforeConstruction()
         {
         if (relayCommunicatorManager != null)
            {
            relayCommunicatorManager.shutdownCommunicator();
            }
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         directConnectCommunicator = (DirectConnectCommunicator)terkCommunicator;

         // register this class's PeerConnectionEventListener for direct connections
         directConnectCommunicator.addPeerConnectionEventListener(directConnectPeerConnectionEventListener);

         // register the ConnectionStatePanel as a PeerConnectionEventListener
         directConnectCommunicator.addPeerConnectionEventListener(connectionStatePanel);

         super.afterSuccessfulConstruction(terkCommunicator);
         }
      }

   private final class MyRelayCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void beforeConstruction()
         {
         if (directConnectCommunicatorManager != null)
            {
            directConnectCommunicatorManager.shutdownCommunicator();
            }
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         relayCommunicator = (RelayCommunicator)terkCommunicator;

         // register this class's UserConnectionEventListener and PeerConnectionEventListener for relay connections
         relayCommunicator.addUserConnectionEventListener(relayUserConnectionEventListener);
         relayCommunicator.addPeerConnectionEventListener(relayPeerConnectionEventListener);

         // register the ConnectionStatePanel as a ConnectionEventListener
         relayCommunicator.addConnectionEventListener(connectionStatePanel);

         super.afterSuccessfulConstruction(terkCommunicator);
         }
      }

   private class RelayUserConnectionEventListener implements UserConnectionEventListener
      {
      public void handleRelayLoginEvent()
         {
         SwingUtils.warnIfEventDispatchThread("handleRelayLoginEvent()");

         // create the mainServant
         final GUIClientServant mainServant = new GUIClientServant(relayCommunicator.getConnectionEventDistributorHelper(), videoStreamPlayer);

         // create the mainServant proxy
         final ObjectPrx mainServantProxy = relayCommunicator.createServantProxy(mainServant);
         terkUserServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

         // create secondary servants and their proxies
         final Set<ObjectPrx> secondaryServantProxies = eventHandler.createAndRegisterSecondaryServantsAndReturnTheirProxies(relayCommunicator, mainServant);

         // register callbacks and secondary servants
         try
            {
            relayCommunicator.registerCallbacks(terkUserServantPrx, terkUserServantPrx);
            if ((secondaryServantProxies != null) && (!secondaryServantProxies.isEmpty()))
               {
               relayCommunicator.registerProxies(secondaryServantProxies);
               }
            eventHandler.executeAfterRelayLogin();
            }
         catch (RegistrationException e)
            {
            LOG.error("RegistrationException while trying to register the callbacks", e);
            }
         }

      public void handleFailedRelayLoginEvent()
         {
         eventHandler.executeAfterFailedRelayLogin();
         }

      public void handleRelayRegistrationEvent()
         {
         eventHandler.executeAfterRelayRegistrationEvent();
         }

      public void handleRelayLogoutEvent()
         {
         setQwerkController(null);

         setWidgetsAndActionsEnabled(false);
         eventHandler.executeAfterRelayLogout();
         }

      public void handleForcedLogoutNotificationEvent()
         {
         handleRelayLogoutEvent();
         }
      }

   private final class DefaultCustomPeerConnectionEventListener extends PeerConnectionEventAdapter
      {
      private final TerkCommunicatorGetter terkCommunicatorGetter;

      private DefaultCustomPeerConnectionEventListener(final TerkCommunicatorGetter terkCommunicatorGetter)
         {
         this.terkCommunicatorGetter = terkCommunicatorGetter;
         }

      public final void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         final QwerkPrx qwerkProxy = QwerkPrxHelper.checkedCast(peerObjectProxy);
         setQwerkController(new QwerkController(peerUserId, qwerkProxy, terkCommunicatorGetter.getTerkCommunicator()));
         }

      public final void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         throw new UnsupportedOperationException("handlePeerConnectedNoProxyEvent() is currently unimplemented.");
         }

      public final void handlePeerDisconnectedEvent(final String peerUserId)
         {
         setQwerkController(null);
         }
      }

   private abstract class BasePeerConnectionEventListener implements PeerConnectionEventListener
      {
      public final void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         if (LOG.isDebugEnabled())
            {
            LOG.debug("handlePeerConnectedEvent(" + peerUserId + ", " + peerAccessLevel + ", " + peerObjectProxy.ice_toString() + ")");
            }

         boolean isConnectionSuccessful = false;
         if (peerObjectProxy != null)
            {
            final TerkUserPrx terkUserProxy = TerkUserPrxHelper.checkedCast(peerObjectProxy);
            executeAfterEstablishingConnectionToPeer(terkUserProxy);

            if (getCustomPeerConnectionEventListener() != null)
               {
               getCustomPeerConnectionEventListener().handlePeerConnectedEvent(peerUserId, peerAccessLevel, peerObjectProxy);
               }

            isConnectionSuccessful = true;
            LOG.info("   Connection successful to peer " + peerUserId + "!");
            eventHandler.executeAfterEstablishingConnectionToQwerk(peerUserId);
            }
         else
            {
            LOG.error("handleConnectToPeerEvent() sent a null peer.  Bummer.");
            }

         setWidgetsAndActionsEnabled(isConnectionSuccessful);
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void executeAfterEstablishingConnectionToPeer(final TerkUserPrx terkUserProxy)
         {
         }

      public final void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         throw new UnsupportedOperationException("handlePeerConnectedNoProxyEvent() is currently unimplemented.");
         }

      public final void handlePeerDisconnectedEvent(final String peerUserId)
         {
         SwingUtils.warnIfEventDispatchThread("handlePeerDisconnectedEvent()");

         if (getCustomPeerConnectionEventListener() != null)
            {
            getCustomPeerConnectionEventListener().handlePeerDisconnectedEvent(peerUserId);
            }

         setWidgetsAndActionsEnabled(false);
         eventHandler.executeAfterDisconnectingFromQwerk(peerUserId);
         }

      public final void handlePeerConnectionFailedEvent(final String peerUserId)
         {
         eventHandler.executeUponFailureToConnectToQwerk(peerUserId);
         }

      protected abstract TerkCommunicator getTerkCommunicator();

      protected abstract PeerConnectionEventListener getCustomPeerConnectionEventListener();
      }

   private class RelayPeerConnectionEventListener extends BasePeerConnectionEventListener
      {
      protected TerkCommunicator getTerkCommunicator()
         {
         return relayCommunicator;
         }

      protected PeerConnectionEventListener getCustomPeerConnectionEventListener()
         {
         return customRelayPeerConnectionEventListener;
         }
      }

   private class DirectConnectPeerConnectionEventListener extends BasePeerConnectionEventListener
      {
      protected TerkCommunicator getTerkCommunicator()
         {
         return directConnectCommunicator;
         }

      protected PeerConnectionEventListener getCustomPeerConnectionEventListener()
         {
         return customDirectConnectPeerConnectionEventListener;
         }

      protected void executeAfterEstablishingConnectionToPeer(final TerkUserPrx peerTerkUserPrx)
         {
         terkUserServantPrx = directConnectCommunicator.getServants().getMainServantProxy();
         }
      }

   private final class ConnectDisconnectActionListener implements ActionListener
      {
      public void actionPerformed(final ActionEvent evt)
         {
         if (isConnectedToPeer)
            {
            parentJFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            setWidgetsAndActionsEnabledWorkhorse(false);
            final SwingWorker worker =
                  new SwingWorker()
                  {
                  public Object construct()
                     {
                     eventHandler.executeBeforeDisconnectingFromQwerk();
                     if (relayCommunicator != null && relayCommunicator.isLoggedIn())
                        {
                        try
                           {
                           relayCommunicator.disconnectFromPeers();
                           }
                        catch (ConnectionLostException e)
                           {
                           if (relayCommunicator != null)
                              {
                              LOG.error("ConnectionLostException while trying to disconnect from peer.  This is unrecoverable, so I'm logging out from the relay.", e);
                              relayCommunicator.logout();
                              }
                           }
                        }
                     if (directConnectCommunicator != null)
                        {
                        try
                           {
                           directConnectCommunicator.disconnectFromPeers();
                           }
                        catch (Exception e)
                           {
                           LOG.error("Exception while disconnecting", e);
                           }
                        }
                     return null;
                     }

                  public void finished()
                     {
                     parentJFrame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                     }
                  };
            worker.start();
            }
         else
            {
            // determine which screen to display in the wizard
            if (!isDirectConnectSupported || relayCommunicator != null)
               {
               if (relayCommunicator != null && relayCommunicator.isLoggedIn())
                  {
                  connectToRobotWizard.setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
                  }
               else
                  {
                  connectToRobotWizard.setCurrentPanel(RelayLoginFormDescriptor.IDENTIFIER);
                  }
               }
            else if (directConnectCommunicator != null)
               {
               connectToRobotWizard.setCurrentPanel(DirectConnectDescriptor.IDENTIFIER);
               }
            else
               {
               connectToRobotWizard.setCurrentPanel(PeerConnectionMethodDescriptor.IDENTIFIER);
               }

            // show the wizard
            connectToRobotWizard.showModalDialog();
            }
         }
      }

   private class MyDirectConnectServantFactory implements ServantFactory
      {
      public Servants createServants(final TerkCommunicator terkCommunicator)
         {
         // create the mainServant
         final GUIClientServant mainServant = new GUIClientServant(
               new ConnectionEventDistributorHelperAdapter()
               {
               public void publishPeerConnectedEvent(final String peerUserId, final PeerAccessLevel accessLevel, final ObjectPrx peerProxy)
                  {
                  directConnectCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedEvent(peerUserId, accessLevel, peerProxy);
                  }

               public void publishPeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel accessLevel)
                  {
                  directConnectCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectedNoProxyEvent(peerUserId, accessLevel);
                  }

               public void publishPeerDisconnectedEvent(final String peerUserId)
                  {
                  directConnectCommunicator.getPeerConnectionEventDistributorHelper().publishPeerDisconnectedEvent(peerUserId);
                  }

               public void publishPeerConnectionFailedEvent(final String peerUserId)
                  {
                  directConnectCommunicator.getPeerConnectionEventDistributorHelper().publishPeerConnectionFailedEvent(peerUserId);
                  }
               },
               videoStreamPlayer);

         // create the mainServant proxy
         final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
         final TerkUserPrx terkUserServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

         // create secondary servants and their proxies
         final Set<ObjectPrx> secondaryServantProxies = eventHandler.createAndRegisterSecondaryServantsAndReturnTheirProxies(terkCommunicator, mainServant);

         return new Servants(terkUserServantPrx, terkUserServantPrx, secondaryServantProxies);
         }
      }
   }
