package edu.cmu.ri.mrpl.TeRK.client.components.framework;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import com.nexes.wizard.Wizard;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.connectionstate.ConnectionStatePanel;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamPlayer;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamViewport;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;

public abstract class BaseGUIClient extends JFrame
   {
   private final GUIClientHelper guiClientHelper;
   private final JPanel mainContentPane = new JPanel();

   /** Creates the BaseGUIClient with a non-resizable window. */
   protected BaseGUIClient(final String applicationName,
                           final String relayCommunicatorIcePropertiesFile,
                           final String directConnectCommunicatorIcePropertiesFile)
      {
      this(applicationName,
           relayCommunicatorIcePropertiesFile,
           directConnectCommunicatorIcePropertiesFile,
           false);
      }

   /**
    * Creates a non-resizable BaseGUIClient with no direct-connect support.
    */
   protected BaseGUIClient(final String applicationName,
                           final String relayCommunicatorIcePropertiesFile)
      {
      this(applicationName,
           relayCommunicatorIcePropertiesFile,
           null,
           false);
      }

   protected BaseGUIClient(final String applicationName,
                           final String relayCommunicatorIcePropertiesFile,
                           final String directConnectCommunicatorIcePropertiesFile,
                           final boolean isResizable)
      {
      super(applicationName);

      guiClientHelper = new GUIClientHelper(applicationName,
                                            relayCommunicatorIcePropertiesFile,
                                            directConnectCommunicatorIcePropertiesFile,
                                            this,
                                            null);

      // set up the frame
      setContentPane(mainContentPane);
      setResizable(isResizable);
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
      addWindowListener(
            new WindowAdapter()
            {
            public void windowClosing(final WindowEvent event)
               {
               performQuitAction();
               }
            });
      }

   public final GUIClientHelper getGUIClientHelper()
      {
      return guiClientHelper;
      }

   protected final void setGUIClientHelperEventHandler(final GUIClientHelperEventHandler eventHandler)
      {
      guiClientHelper.setGUIClientHelperEventHandler(eventHandler);
      }

   protected final void setCustomRelayPeerConnectionEventListener(final PeerConnectionEventListener customRelayPeerConnectionEventListener)
      {
      guiClientHelper.setCustomRelayPeerConnectionEventListener(customRelayPeerConnectionEventListener);
      }

   protected final void setCustomDirectConnectPeerConnectionEventListener(final PeerConnectionEventListener customDirectConnectPeerConnectionEventListener)
      {
      guiClientHelper.setCustomDirectConnectPeerConnectionEventListener(customDirectConnectPeerConnectionEventListener);
      }

   public final Container getContentPane()
      {
      return mainContentPane;
      }

   protected final JPanel getMainContentPane()
      {
      return mainContentPane;
      }

   protected final JButton getConnectDisconnectButton()
      {
      return guiClientHelper.getConnectDisconnectButton();
      }

   protected final ActionListener getConnectDisconnectButtonActionListener()
      {
      return guiClientHelper.getConnectDisconnectButtonActionListener();
      }

   protected final DirectConnectCommunicator getDirectConnectCommunicator()
      {
      return guiClientHelper.getDirectConnectCommunicator();
      }

   protected final RelayCommunicator getRelayCommunicator()
      {
      return guiClientHelper.getRelayCommunicator();
      }

   protected final Wizard getConnectToRobotWizard()
      {
      return guiClientHelper.getConnectToRobotWizard();
      }

   protected final void performQuitAction()
      {
      guiClientHelper.performQuitAction();
      }

   /** Returns the {@link VideoStreamPlayer} used to control the video. */
   protected final VideoStreamPlayer getVideoStreamPlayer()
      {
      return guiClientHelper.getVideoStreamPlayer();
      }

   /** Returns the {@link VideoStreamViewport} used to display the video. */
   protected final VideoStreamViewport getVideoStreamViewport()
      {
      return guiClientHelper.getVideoStreamViewport();
      }

   /** Returns the {@link Component} of the {@link VideoStreamViewport} used to display the video. */
   protected final Component getVideoStreamViewportComponent()
      {
      return guiClientHelper.getVideoStreamViewportComponent();
      }

   /**
    * Returns the {@link QwerkController} used to control the qwerk (may be <code>null</code>, such as when not
    * connected to a Qwerk).
    */
   protected final QwerkController getQwerkController()
      {
      return guiClientHelper.getQwerkController();
      }

   protected final ConnectionStatePanel getConnectionStatePanel()
      {
      return guiClientHelper.getConnectionStatePanel();
      }

   /**
    * Attempts to connect to the peer specified by the given <code>peerIdentifier</code> using the Direct Connect
    * connection model.  The connection is "headless" because it establishes the connection without having to display
    * the connection wizard.
    *
    * @param peerIdentifier the hostname or IP address of the peer
    */
   public final void doHeadlessConnectToPeer(final String peerIdentifier)
      {
      guiClientHelper.doHeadlessConnectToPeer(peerIdentifier);
      }
   }
