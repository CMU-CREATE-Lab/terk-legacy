package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerView;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerView;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.robot.finch.servants.FinchServantFactory;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.serial.device.connectivity.DefaultSerialDeviceConnectivityManagerView;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionEventListener;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionState;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerImpl;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerView;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Finch
   {
   private static final Logger LOG = Logger.getLogger(Finch.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(Finch.class.getName());
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/finch/Finch.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/finch/Finch.relay.ice.properties";
   private static final String DIRECT_CONNECT_OBJECT_ADAPTER_NAME = "Robot.Server";
   private static final String RELAY_OBJECT_ADAPTER_NAME = "Robot.Client";

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new Finch();
               }
            });
      }

   private final FinchProxyProvider finchProxyProvider;
   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final AtomicInteger numRelayConnectedPeers = new AtomicInteger(0);
   private final AtomicInteger numDirectConnectedPeers = new AtomicInteger(0);

   private Finch()
      {
      // create the JFrame that wraps the panel
      final JFrame jFrame = new JFrame(APPLICATION_NAME);

      // create the manager and view instances for the finch connectivity manager
      final SerialDeviceConnectivityManager serialDeviceConnectivityManager = new SerialDeviceConnectivityManagerImpl(new FinchProxyCreator());
      final SerialDeviceConnectivityManagerView serialDeviceConnectivityManagerView = new DefaultSerialDeviceConnectivityManagerView(serialDeviceConnectivityManager, jFrame);

      // create the ServantFactory instances
      finchProxyProvider = new FinchProxyProvider(serialDeviceConnectivityManager);
      final FinchServantFactory directConnectServantFactory = new FinchServantFactory(finchProxyProvider);
      final FinchServantFactory relayServantFactory = new FinchServantFactory(finchProxyProvider);

      // create the direct-connect manager and view
      directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(APPLICATION_NAME,
                                                                                  ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                                  DIRECT_CONNECT_OBJECT_ADAPTER_NAME,
                                                                                  directConnectServantFactory);
      final DirectConnectCommunicatorManagerView directConnectCommunicatorManagerView = new DirectConnectCommunicatorManagerView(directConnectCommunicatorManager, jFrame);

      // create the relay manager and view
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(APPLICATION_NAME,
                                                                  ICE_RELAY_PROPERTIES_FILE,
                                                                  RELAY_OBJECT_ADAPTER_NAME,
                                                                  relayServantFactory);
      final RelayCommunicatorManagerView relayCommunicatorManagerView = new RelayCommunicatorManagerView(relayCommunicatorManager, jFrame);

      // register a listener with the SerialDeviceConnectivityManager that
      // enables/disables the communicator managers upon connect/disconnect
      serialDeviceConnectivityManager.addConnectionEventListener(
            new SerialDeviceConnectionEventListener()
            {
            public void handleConnectionStateChange(final SerialDeviceConnectionState oldState,
                                                    final SerialDeviceConnectionState newState,
                                                    final String serialPortName)
               {
               directConnectCommunicatorManager.setIsSupported(SerialDeviceConnectionState.CONNECTED.equals(newState));
               relayCommunicatorManager.setIsSupported(SerialDeviceConnectionState.CONNECTED.equals(newState));
               }
            });

      // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
      // when various direct-connect-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(jFrame);
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final UserAlertingRelayEventFailureListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(jFrame);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);

      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new PeerCountingTerkCommunicatorCreationEventAdapater(numDirectConnectedPeers));
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new PeerCountingTerkCommunicatorCreationEventAdapater(numRelayConnectedPeers));
      // Layout the GUI ------------------------------------------------------------------------------------------------

      final JPanel communicatorChoicePanel = new JPanel();
      communicatorChoicePanel.setLayout(new BoxLayout(communicatorChoicePanel, BoxLayout.Y_AXIS));
      communicatorChoicePanel.add(directConnectCommunicatorManagerView.getCheckbox());
      communicatorChoicePanel.add(relayCommunicatorManagerView.getCheckbox());

      final JPanel communicatorPanel = new JPanel();
      communicatorPanel.setLayout(new BoxLayout(communicatorPanel, BoxLayout.X_AXIS));
      communicatorPanel.add(communicatorChoicePanel);
      communicatorPanel.add(Box.createGlue());

      // start the communicator managers in a disabled state
      directConnectCommunicatorManager.setIsSupported(false);
      relayCommunicatorManager.setIsSupported(false);

      // create the main panel for the JFrame
      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      // add the views to the main panel
      panel.add(serialDeviceConnectivityManagerView.getComponent());
      panel.add(GUIConstants.createRigidSpacer(10));
      panel.add(communicatorPanel);
      panel.add(relayCommunicatorManagerView.getLoginFormPanel());

      // add the panel to the JFrame
      jFrame.add(panel);

      // set various properties for the JFrame
      jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      jFrame.setBackground(Color.WHITE);
      jFrame.setResizable(false);
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
                           // simply calling disconnect here will cause the TerkCommunicatorManager to be
                           // set to disabled which will make sure that the direct-connect and/or relay communicators
                           // are shutdown.
                           serialDeviceConnectivityManager.disconnect();
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

   private final class PeerCountingTerkCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      private final AtomicInteger counter;

      private PeerCountingTerkCommunicatorCreationEventAdapater(final AtomicInteger counter)
         {
         this.counter = counter;
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // reset the counter since there can't be any connected peers for this communicator if we just created it
         counter.set(0);
         actOnConnectedPeerCount();

         if (terkCommunicator != null)
            {
            terkCommunicator.addPeerConnectionEventListener(
                  new PeerConnectionEventAdapter()
                  {
                  public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
                     {
                     counter.incrementAndGet();
                     actOnConnectedPeerCount();
                     }

                  public void handlePeerDisconnectedEvent(final String peerUserId)
                     {
                     if (counter.decrementAndGet() < 0)
                        {
                        counter.set(0);
                        }
                     actOnConnectedPeerCount();
                     }
                  }
            );
            }
         }

      public void afterWaitForShutdown()
         {
         // reset the counter since there can't be any connected peers for this communicator if we just destroyed it
         counter.set(0);
         actOnConnectedPeerCount();
         }
      }

   private void actOnConnectedPeerCount()
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug("CONNECTED PEERS: Direct-Connect [" + numDirectConnectedPeers + "]    Relay [" + numRelayConnectedPeers + "]");
         }
      final int total = numDirectConnectedPeers.get() + numRelayConnectedPeers.get();
      if (total <= 0)
         {
         final FinchProxy finchProxy = finchProxyProvider.getFinchProxy();
         if (finchProxy != null)
            {
            finchProxy.emergencyStop();
            }
         }
      }
   }