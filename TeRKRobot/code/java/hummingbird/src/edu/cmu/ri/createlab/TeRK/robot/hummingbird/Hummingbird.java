package edu.cmu.ri.createlab.TeRK.robot.hummingbird;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerView;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerView;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.servants.HummingbirdServantFactory;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.serial.device.connectivity.DefaultSerialDeviceConnectivityManagerView;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionEventListener;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionState;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerImpl;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerView;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.ice.util.PropertiesUtil;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Hummingbird
   {
   private static final Logger LOG = Logger.getLogger(Hummingbird.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(Hummingbird.class.getName());
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/hummingbird/Hummingbird.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/robot/hummingbird/Hummingbird.relay.ice.properties";
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
               // create the JFrame that wraps the panel
               final JFrame jFrame = new JFrame(APPLICATION_NAME);

               final Hummingbird hummingbird = new Hummingbird(jFrame);

               // add the panel to the JFrame
               jFrame.add(hummingbird.getPanel());

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
                                    hummingbird.disconnect();
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

   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final JPanel mainPanel = new JPanel();
   private final SerialDeviceConnectivityManager serialDeviceConnectivityManager;

   public Hummingbird(final JFrame jFrame)
      {
      this(jFrame, null);
      }

   public Hummingbird(final JFrame jFrame, final Integer directConnectPortNumber)
      {
      // if a port number was specified for direct connect, make sure we use that
      // one instead by setting an Ice properties override
      if (directConnectPortNumber != null)
         {
         PropertiesUtil.setOverrideProperty(DIRECT_CONNECT_OBJECT_ADAPTER_NAME + ".Endpoints", "tcp -p " + directConnectPortNumber);
         }

      // create the manager and view instances for the hummingbird connectivity manager
      serialDeviceConnectivityManager = new SerialDeviceConnectivityManagerImpl(new HummingbirdProxyCreator());
      final SerialDeviceConnectivityManagerView serialDeviceConnectivityManagerView = new DefaultSerialDeviceConnectivityManagerView(serialDeviceConnectivityManager, jFrame);

      // create the ServantFactory instances
      final HummingbirdProxyProvider hummingbirdProxyProvider = new HummingbirdProxyProvider(serialDeviceConnectivityManager);
      final HummingbirdServantFactory directConnectServantFactory = new HummingbirdServantFactory(hummingbirdProxyProvider);
      final HummingbirdServantFactory relayServantFactory = new HummingbirdServantFactory(hummingbirdProxyProvider);

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
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("Hummingbird.handleConnectionStateChange(" + oldState.name() + "," + newState.name() + "," + serialPortName + ")");
                  }
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
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
      mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      // add the views to the main panel
      mainPanel.add(serialDeviceConnectivityManagerView.getComponent());
      mainPanel.add(GUIConstants.createRigidSpacer(10));
      mainPanel.add(communicatorPanel);
      mainPanel.add(relayCommunicatorManagerView.getLoginFormPanel());
      }

   private JPanel getPanel()
      {
      return mainPanel;
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

   public void addDirectConnectCommunicatorCreationEventListener(final TerkCommunicatorCreationEventListener listener)
      {
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(listener);
      }

   public void scanAndConnect()
      {
      // simply calling disconnect here will cause the TerkCommunicatorManager to be
      // set to disabled which will make sure that the direct-connect and/or relay communicators
      // are shutdown.
      serialDeviceConnectivityManager.scanAndConnect();
      }

   public void disconnect()
      {
      // simply calling disconnect here will cause the TerkCommunicatorManager to be
      // set to disabled which will make sure that the direct-connect and/or relay communicators
      // are shutdown.
      serialDeviceConnectivityManager.disconnect();
      }

   public void addSerialDeviceConnectionEventListener(final SerialDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         serialDeviceConnectivityManager.addConnectionEventListener(listener);
         }
      }
   }
