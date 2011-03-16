package edu.cmu.ri.createlab.TeRK.client.robotdiaries;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PropertyResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.audio.clips.AudioClipInstaller;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.datapanel.DataPanel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.Messenger;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.universalremote.UniversalRemoteWrapper;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer.VisualProgrammer;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.Hummingbird;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionEventListener;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectionState;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RobotDiaries extends JPanel
   {
   private static final Logger LOG = Logger.getLogger(RobotDiaries.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RobotDiaries.class.getName());

   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");
   private static final String HOST_FOR_HUMMINGBIRD_DIRECT_CONNECT = "localhost";
   private final UniversalRemoteWrapper universalRemoteWrapper;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame(APPLICATION_NAME);

               // add the root panel to the JFrame
               final RobotDiaries application = new RobotDiaries(jFrame);
               jFrame.add(application);

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
                                    application.shutdown();
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

   private final Collection<Application> applications = new ArrayList<Application>();
   private final Collection<HummingbirdClientApplication> hummingbirdClientApplications = new ArrayList<HummingbirdClientApplication>();
   private JLabel statusBar = GUIConstants.createLabel(RESOURCES.getString("status.message.welcome"));
   private final Hummingbird hummingbird;
   private AtomicBoolean isConnectedToHummingbird = new AtomicBoolean(false);
   private AtomicBoolean isShutdown = new AtomicBoolean(false);

   private RobotDiaries(final JFrame jFrame)
      {
      // Install the sound files to make sure the user has some sounds to play with
      AudioClipInstaller.getInstance().install();

      // Attempt to find an open port on which the Hummingbird server can listen
      final int portNumber = findOpenPort();
      LOG.debug("Hummingbird direct-connect will operate over port [" + portNumber + "]");

      hummingbird = new Hummingbird(jFrame, portNumber);
      hummingbird.addSerialDeviceConnectionEventListener(new HummingbirdSerialConnectionEventListener());
      hummingbird.addDirectConnectCommunicatorCreationEventListener(
            new TerkCommunicatorCreationEventAdapater()
            {
            public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
               {
               SwingUtils.warnIfEventDispatchThread("RobotDiaries.afterSuccessfulConstruction()");
               LOG.debug("RobotDiaries: notifying sub-applications that they can now connect to the hummingbird...");

               for (final HummingbirdClientApplication app : hummingbirdClientApplications)
                  {
                  app.connectToPeer(HOST_FOR_HUMMINGBIRD_DIRECT_CONNECT + ":" + portNumber);
                  }
               }
            }
      );
      final DataPanel dataPanel = new DataPanel();

      universalRemoteWrapper = new UniversalRemoteWrapper(jFrame);
      universalRemoteWrapper.addPeerConnectionEventListener(dataPanel.getRemoteListener());
      hummingbirdClientApplications.add(universalRemoteWrapper);
      hummingbirdClientApplications.add(new VisualProgrammer(jFrame));
      applications.addAll(hummingbirdClientApplications);
      final Messenger messenger = new Messenger(jFrame);
      messenger.addPeerConnectionEventListener(dataPanel.getMessengerListener());
      applications.add(messenger);

      messenger.setRelaySupported(true);
      universalRemoteWrapper.setDirectConnectSupported(true);

      // configure the panel
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      this.setBorder(BorderFactory.createEmptyBorder());
      this.setBackground(new Color(0xe8e8e8));

      // add the tabs
      final JTabbedPane tabbedPane = new JTabbedPane();
      tabbedPane.setPreferredSize(new Dimension(600, 600));
      tabbedPane.setFont(GUIConstants.FONT_NORMAL);
      int i = 0;
      for (final Application application : applications)
         {
         if (application != null)
            {
            tabbedPane.addTab(application.getName(), application.getComponent());
            tabbedPane.setBackgroundAt(i, Color.WHITE);
            i++;
            }
         }

      final JPanel applicationPanel = new JPanel();
      applicationPanel.setBackground(Color.WHITE);
      applicationPanel.setBorder(BorderFactory.createEmptyBorder());
      final GroupLayout layout = new GroupLayout(applicationPanel);
      applicationPanel.setLayout(layout);
      layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
                  .add(tabbedPane)
                  .add(statusBar));
      layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .add(tabbedPane)
                  .add(statusBar));

      dataPanel.setGlobalListModels(messenger.getGlobalExpressionsModel(), messenger.getGlobalSequencesModel());
      dataPanel.changeContext(tabbedPane.getSelectedComponent());

      dataPanel.setBorder(BorderFactory.createEmptyBorder());

      tabbedPane.addChangeListener(dataPanel);

      //add the application and data panels
      this.add(applicationPanel);
      this.add(GUIConstants.createRigidSpacer(3));
      this.add(dataPanel);

      // add the root panel to the JFrame
      jFrame.add(this);
      // try to connect to the hummingbird
      hummingbird.scanAndConnect();
      }

   private int findOpenPort()
      {
      LOG.debug("RobotDiaries.findOpenPort(): Finding an open port...");
      int port = DirectConnectCommunicator.DEFAULT_PORT;

      while (true)
         {
         // keep trying ports until we get a connection refused exception--that's our
         // signal that the socket is not in use
         try
            {
            LOG.debug("   Testing port [" + port + "]");
            final Socket socket = new Socket("localhost", port);
            if (socket.isConnected() && socket.isBound())
               {
               LOG.debug("      Port is already in use, trying another.");
               socket.close();
               }
            else
               {
               LOG.debug("      Port is not use!");
               socket.close();
               return port;
               }
            }
         catch (ConnectException e)
            {
            LOG.debug("ConnectException while testing port [" + port + "].  Assuming it's not in use.");
            return port;
            }
         catch (IOException e)
            {
            LOG.debug("IOException while testing port [" + port + "].  Trying another.");
            }

         port++;
         }
      }

   private void setStatusMessage(final String message)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         statusBar.setText(message);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  statusBar.setText(message);
                  }
               });
         }
      }

   private void shutdown()
      {
      SwingUtils.warnIfEventDispatchThread("RobotDiaries.shutdown()");

      // flag the app as shutdown so the SerialDeviceConnectionEventListener doesn't try to reconnect when it gets the
      // state as disconnected
      isShutdown.set(true);

      // shutdown sub-applications
      LOG.debug("RobotDiaries.shutdown(): calling shutdown on each sub-application...");
      for (final Application application : applications)
         {
         try
            {
            application.shutdown();
            }
         catch (Exception e)
            {
            LOG.error("Exception while shutting down application [" + application.getName() + "]", e);
            }
         }

      // turn off direct-connect support for a more graceful shutdown
      hummingbird.setDirectConnectSupported(false);

      // disconnect from the hummingbird
      LOG.debug("RobotDiaries.shutdown(): Disconnecting from the hummingbird...");
      hummingbird.disconnect();
      }

   private class HummingbirdSerialConnectionEventListener implements SerialDeviceConnectionEventListener
      {
      public void handleConnectionStateChange(final SerialDeviceConnectionState oldState,
                                              final SerialDeviceConnectionState newState,
                                              final String serialPortName)
         {
         SwingUtils.warnIfEventDispatchThread("RobotDiaries$HummingbirdSerialConnectionEventListener.handleConnectionStateChange()");

         LOG.debug("RobotDiaries.handleConnectionStateChange(" + oldState.name() + "," + newState.name() + "," + serialPortName + ")");
         if (SerialDeviceConnectionState.SCANNING.equals(newState))
            {
            if (serialPortName == null || serialPortName.length() == 0)
               {
               setStatusMessage(RESOURCES.getString("status.message.scanning-for-ports"));
               }
            else
               {
               setStatusMessage(RESOURCES.getString("status.message.scanning-for-hummingbird") + ": " + serialPortName);
               }
            universalRemoteWrapper.setIsScanning(true);
            }
         else if (SerialDeviceConnectionState.CONNECTED.equals(newState))
            {
            setStatusMessage(RESOURCES.getString("status.message.connected-to-hummingbird") + ": " + serialPortName);
            isConnectedToHummingbird.set(true);
            hummingbird.setDirectConnectSupported(true);
            universalRemoteWrapper.setIsScanning(false);
            }
         else if (SerialDeviceConnectionState.DISCONNECTED.equals(newState) && isConnectedToHummingbird.get())
            {
            LOG.debug("RobotDiaries$HummingbirdSerialConnectionEventListener.handleConnectionStateChange()");
            if (!isShutdown.get())
               {
               LOG.debug("RobotDiaries$HummingbirdSerialConnectionEventListener.handleConnectionStateChange(): setting disconnect status message");
               isConnectedToHummingbird.set(false);
               setStatusMessage(RESOURCES.getString("status.message.disconnected-from-hummingbird"));

               // Don't notify the sub-apps here, just let them detect the disconnection on their
               // own (their pingers will detect it).  Meanwhile, go back to scanning mode...
               hummingbird.scanAndConnect();
               }
            }
         else
            {
            setStatusMessage("");
            }
         }
      }
   }
