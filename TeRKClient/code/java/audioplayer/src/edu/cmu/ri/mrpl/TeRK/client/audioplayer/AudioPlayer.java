package edu.cmu.ri.mrpl.TeRK.client.audioplayer;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.audio.AudioControlPanel;
import edu.cmu.ri.createlab.TeRK.audio.DefaultAudioClipChooser;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.connectionstate.ConnectionStatePanel;
import edu.cmu.ri.mrpl.TeRK.speech.Mouth;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AudioPlayer extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(AudioPlayer.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AudioPlayer.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/audioplayer/AudioPlayer.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/audioplayer/AudioPlayer.relay.ice.properties";

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new AudioPlayer(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
               }
            });
      }

   private final AudioControlPanel audioControlPanel = new AudioControlPanel(new DefaultAudioClipChooser());

   private AudioPlayer(final String applicationName,
                       final String relayCommunicatorIcePropertiesFile,
                       final String directConnectCommunicatorIcePropertiesFile)
      {
      super(applicationName, relayCommunicatorIcePropertiesFile, directConnectCommunicatorIcePropertiesFile);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void toggleGUIElementState(final boolean isEnabled)
               {
               audioControlPanel.setEnabled(isEnabled);
               }
            });
      // CONFIGURE GUI ELEMENTS ========================================================================================

      audioControlPanel.setEnabled(false);
      audioControlPanel.addEventListener(
            new AudioControlPanel.EventListener()
            {
            public void playTone(final int frequency, final int amplitude, final int duration)
               {
               getQwerkController().getAudioService().playToneAsynchronously(frequency, amplitude, duration, null);
               }

            public void playSound(final File file, final AsynchronousCommandExceptionHandlerCallback myAsynchronousCommandExceptionHandlerCallback)
               {
               try
                  {
                  final byte[] sound = FileUtils.getFileAsBytes(file);
                  if (sound != null)
                     {
                     getQwerkController().getAudioService().playSoundAsynchronously(sound, myAsynchronousCommandExceptionHandlerCallback);
                     }
                  }
               catch (IOException e)
                  {
                  LOG.error("IOException while reading the file to be played", e);
                  }
               }

            public void playSpeech(final String speechText)
               {
               final byte[] speechData = Mouth.getInstance().getSpeech(speechText);
               if (speechData != null && speechData.length > 0)
                  {
                  getQwerkController().getAudioService().playSoundAsynchronously(speechData, null);
                  }
               else
                  {
                  LOG.error("AudioServiceControlPanel$ControlPanelDevice$AudioControlPanelEventListener.playSpeech(): speech byte array is null or empty");
                  }
               }
            }
      );

      // LAYOUT GUI ELEMENTS ===========================================================================================
      final ConnectionStatePanel connectionStatePanel = getConnectionStatePanel();
      connectionStatePanel.setBackground(Color.WHITE);

      // create a panel to hold the connect/disconnect button and the connection state labels
      final JPanel connectionPanel = new JPanel(new SpringLayout());
      connectionPanel.setBackground(Color.WHITE);
      connectionPanel.add(getConnectDisconnectButton());
      connectionPanel.add(connectionStatePanel);
      SpringLayoutUtilities.makeCompactGrid(connectionPanel,
                                            1, 2, // rows, cols
                                            0, 0, // initX, initY
                                            5, 5);// xPad, yPad

      // Layout the main content pane using SpringLayout
      getMainContentPane().setBackground(Color.WHITE);
      getMainContentPane().setLayout(new SpringLayout());
      getMainContentPane().add(connectionPanel);
      getMainContentPane().add(audioControlPanel);
      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            2, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 10);// xPad, yPad

      pack();

      setLocationRelativeTo(null);// center the window on the screen

      setVisible(true);
      }
   }
