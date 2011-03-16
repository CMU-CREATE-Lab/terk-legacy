package edu.cmu.ri.createlab.TeRK.client.robotdiaries.universalremote;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.audio.AudioControlPanel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.HummingbirdClientApplication;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.RemoteTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.universalremote.UniversalRemote;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.swing.ImageUtils;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class UniversalRemoteWrapper extends JPanel implements HummingbirdClientApplication
   {
   private static final Logger LOG = Logger.getLogger(UniversalRemoteWrapper.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(UniversalRemoteWrapper.class.getName());

   public static final String APPLICATION_NAME = RESOURCES.getString("application.name");

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
               final UniversalRemoteWrapper application = new UniversalRemoteWrapper(jFrame);
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

   private final UniversalRemote universalRemote;

   private final Component leftGlue = Box.createGlue();
   private final Component rightGlue = Box.createGlue();
   private final AtomicBoolean isScanning = new AtomicBoolean(true);
   private final SetStageRunnable setStageForIsScanningRunnable;
   private final SetStageRunnable setStageForIsNotScanningRunnable;

   public UniversalRemoteWrapper(final JFrame jFrame)
      {
      // configure the panel
      final GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
      this.setBackground(Color.WHITE);

      // ===============================================================================================================
      final JPanel scanningPanel = new JPanel();
      scanningPanel.setBackground(Color.WHITE);
      final Component scanningPanelSpacer = GUIConstants.createRigidSpacer(20);
      final GroupLayout scanningPanelLayout = new GroupLayout(scanningPanel);
      scanningPanel.setLayout(scanningPanelLayout);
      final JLabel scanningGraphic = new JLabel(ImageUtils.createImageIcon(RESOURCES.getString("image.scanning")));
      final JLabel scanningLabel = GUIConstants.createLabel(RESOURCES.getString("label.scanning"), GUIConstants.FONT_MEDIUM);
      scanningPanelLayout.setHorizontalGroup(
            scanningPanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(scanningLabel)
                  .add(scanningPanelSpacer)
                  .add(scanningGraphic)
      );
      scanningPanelLayout.setVerticalGroup(
            scanningPanelLayout.createSequentialGroup()
                  .add(scanningLabel)
                  .add(scanningPanelSpacer)
                  .add(scanningGraphic)
      );

      // set system properties which will cause the AudioControlPanel to display different labels for the tone inputs
      System.setProperty(AudioControlPanel.TONE_FREQUENCY_PROPERTY_KEY, RESOURCES.getString("label.tone.frequency"));
      System.setProperty(AudioControlPanel.TONE_DURATION_PROPERTY_KEY, RESOURCES.getString("label.tone.duration"));
      System.setProperty(AudioControlPanel.TONE_AMPLITUDE_PROPERTY_KEY, RESOURCES.getString("label.tone.amplitude"));
      System.setProperty(AudioControlPanel.IS_TONE_DURATION_SPECIFIED_IN_SECONDS_PROPERTY_KEY, String.valueOf(Boolean.TRUE));

      universalRemote = new UniversalRemote(jFrame);

      setStageForIsScanningRunnable = new SetStageRunnable(scanningPanel, GroupLayout.CENTER);
      setStageForIsNotScanningRunnable = new SetStageRunnable(universalRemote.getStageComponent(), GroupLayout.LEADING);

      setStageForIsScanningRunnable.run();

      // ===============================================================================================================
      this.setTransferHandler(new RemoteTransferHandler());

      // add the root panel to the JFrame
      jFrame.add(this);
      }

   public String getName()
      {
      return APPLICATION_NAME;
      }

   public Component getComponent()
      {
      return this;
      }

   public void shutdown()
      {
      LOG.debug("UniversalRemoteWrapper.shutdown()");

      universalRemote.shutdown();
      }

   public void setIsScanning(final boolean isScanning)
      {
      LOG.debug("UniversalRemoteWrapper.setIsScanning(" + isScanning + ")");

      // don't do anything if the state hasn't changed
      if (this.isScanning.getAndSet(isScanning) != isScanning)
         {
         if (isScanning)
            {
            LOG.debug("   calling setStageForIsScanningRunnable");
            SwingUtilities.invokeLater(setStageForIsScanningRunnable);
            }
         else
            {
            LOG.debug("   calling setStageForIsNotScanningRunnable");
            SwingUtilities.invokeLater(setStageForIsNotScanningRunnable);
            }
         }
      }

   public void connectToPeer(final String hostname)
      {
      LOG.debug("UniversalRemoteWrapper.connectToPeer()");

      try
         {
         universalRemote.directConnectToPeer(hostname);
         }
      catch (DuplicateConnectionException e)
         {
         LOG.error("DuplicateConnectionException while trying to direct-connect to peer at [" + hostname + "]", e);
         }
      catch (PeerConnectionFailedException e)
         {
         LOG.error("PeerConnectionFailedException while trying to direct-connect to peer at [" + hostname + "]", e);
         }
      }

   public void loadExpression(final XmlExpression expression)
      {
      universalRemote.loadExpression(expression);
      }

   public void addPeerConnectionEventListener(PeerConnectionEventListener pcel)
      {
      universalRemote.addPeerConnectionEventListener(pcel);
      }

   public void setDirectConnectSupported(final boolean isSupported)
      {
      this.universalRemote.setDirectConnectSupported(isSupported);
      }

   private class SetStageRunnable implements Runnable
      {
      private final Component mainComponent;
      private final int parallelAlignment;

      private SetStageRunnable(final Component mainComponent, final int parallelGroupAlignment)
         {
         this.mainComponent = mainComponent;
         parallelAlignment = parallelGroupAlignment;
         }

      public void run()
         {
         UniversalRemoteWrapper.this.removeAll();

         final GroupLayout layout = new GroupLayout(UniversalRemoteWrapper.this);
         UniversalRemoteWrapper.this.setLayout(layout);
         layout.setHorizontalGroup(
               layout.createSequentialGroup()
                     .add(leftGlue)
                     .add(mainComponent)
                     .add(rightGlue)
         );
         layout.setVerticalGroup(
               layout.createParallelGroup(parallelAlignment)
                     .add(leftGlue)
                     .add(mainComponent)
                     .add(rightGlue)
         );
         }
      }
   }