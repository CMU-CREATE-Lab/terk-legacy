package edu.cmu.ri.createlab.TeRK.client.artarm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.PropertyResourceBundle;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Styler
 * Date: Jul 21, 2008
 * Time: 1:13:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArtArm extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(ArtArm.class);
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ArtArm.class.getName());
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/expressomatic/Express-O-Matic.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/expressomatic/Express-O-Matic.relay.ice.properties";

   private boolean _calibrated = false;
   private boolean _imageLoaded = false;

   private DotImage _dotImage;
   private double _pageOffsetX;
   private double _pageOffsetY;

   private final JButton _drawButton = new JButton("Draw!");

   private DrawingArm _arm;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new ArtArm(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
               }
            });
      }

   protected ArtArm(final String applicationName,
                    final String relayCommunicatorIcePropertiesFile,
                    final String directConnectCommunicatorIcePropertiesFile)

      {
      super(applicationName, relayCommunicatorIcePropertiesFile, directConnectCommunicatorIcePropertiesFile);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void toggleGUIElementState(final boolean isEnabled)
               {

               }
            });

      buildGUI();
      }

   private void buildGUI()
      {
      JPanel topPanel = new JPanel();
      topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
      topPanel.add(connectionPanel());
      topPanel.add(this._drawButton);
      this._drawButton.setEnabled(false);
      this._drawButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent e)
         {
         _arm.drawDotImage(_dotImage, _pageOffsetX, _pageOffsetY, 0);
         }
      });
      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
      bottomPanel.add(calibrationPanel());
      bottomPanel.add(pictureChooserButton());

      getMainContentPane().setLayout(new BoxLayout(getMainContentPane(), BoxLayout.Y_AXIS));
      getMainContentPane().add(topPanel);
      getMainContentPane().add(bottomPanel);
      pack();
      setLocationRelativeTo(null);// center the window on the screen
      setVisible(true);
      }

   private JButton pictureChooserButton()
      {
      final JButton pictureChooserButton = new JButton("Open Picture");
      pictureChooserButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
         {
         JFileChooser fileChooser = new JFileChooser();
         if (fileChooser.showOpenDialog(pictureChooserButton) == JFileChooser.APPROVE_OPTION)
            {
            setPicture(fileChooser.getSelectedFile());
            }
         }
      });

      return pictureChooserButton;
      }

   private void setPicture(File file)
      {
      this._dotImage = ImageLoader.loadDotImageFromPath(file.getAbsolutePath(), 200, 5);
      if (this._dotImage != null)
         {
         this._imageLoaded = true;
         _drawButton.setEnabled(this._imageLoaded && this._calibrated);
         }
      }

   private JPanel connectionPanel()
      {
      // create a panel to hold the connect/disconnect button and the connection state panel
      final JPanel connectionPanel = new JPanel();
      connectionPanel.add(getConnectDisconnectButton());
      connectionPanel.add(getConnectionStatePanel());
      return connectionPanel;
      }

   private JPanel calibrationPanel()
      {
      final JPanel calibrationPanel = new JPanel();
      calibrationPanel.setLayout(new BoxLayout(calibrationPanel, BoxLayout.Y_AXIS));
      final JLabel linkOneLengthLabel = new JLabel("Link One Length (cm)");
      final JLabel linkTwoLengthLabel = new JLabel("Link Two Length (cm)");
      final JLabel servoOnePosOneLabel = new JLabel("Servo One @ 90 (ticks)");
      final JLabel servoOnePosTwoLabel = new JLabel("Servo One @ 45 (ticks)");
      final JLabel servoTwoPosOneLabel = new JLabel("Servo Two @ 90 (ticks)");
      final JLabel servoTwoPosTwoLabel = new JLabel("Servo Two @ 135 (ticks)");
      final JLabel pageOffsetXLabel = new JLabel("Page Offset X (cm)");
      final JLabel pageOffsetYLabel = new JLabel("Page Offset Y (cm)");
      final JTextField linkOneLengthTextField = new JTextField("l1  ");
      final JTextField linkTwoLengthTextField = new JTextField("l2  ");
      final JTextField servoOnePosOneTextField = new JTextField("127 ");
      final JTextField servoOnePosTwoTextField = new JTextField("127 ");
      final JTextField servoTwoPosOneTextField = new JTextField("127 ");
      final JTextField servoTwoPosTwoTextField = new JTextField("127 ");
      final JTextField pageOffsetXTextField = new JTextField("-l0 ");
      final JTextField pageOffsetYTextField = new JTextField("0   ");

      final JPanel linkOneLengthPanel = new JPanel();
      linkOneLengthPanel.add(linkOneLengthLabel);
      linkOneLengthPanel.add(linkOneLengthTextField);
      final JPanel linkTwoLengthPanel = new JPanel();
      linkTwoLengthPanel.add(linkTwoLengthLabel);
      linkTwoLengthPanel.add(linkTwoLengthTextField);
      final JPanel servoOnePosOnePanel = new JPanel();
      servoOnePosOnePanel.add(servoOnePosOneLabel);
      servoOnePosOnePanel.add(servoOnePosOneTextField);
      final JPanel servoOnePosTwoPanel = new JPanel();
      servoOnePosTwoPanel.add(servoOnePosTwoLabel);
      servoOnePosTwoPanel.add(servoOnePosTwoTextField);
      final JPanel servoTwoPosOnePanel = new JPanel();
      servoTwoPosOnePanel.add(servoTwoPosOneLabel);
      servoTwoPosOnePanel.add(servoTwoPosOneTextField);
      final JPanel servoTwoPosTwoPanel = new JPanel();
      servoTwoPosTwoPanel.add(servoTwoPosTwoLabel);
      servoTwoPosTwoPanel.add(servoTwoPosTwoTextField);
      final JPanel pageOffsetXPanel = new JPanel();
      pageOffsetXPanel.add(pageOffsetXLabel);
      pageOffsetXPanel.add(pageOffsetXTextField);
      final JPanel pageOffsetYPanel = new JPanel();
      pageOffsetYPanel.add(pageOffsetYLabel);
      pageOffsetYPanel.add(pageOffsetYTextField);

      final JButton calibrateButton = new JButton("Calibrate!");
      calibrateButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent e)
         {
         double linkOneLength = Double.parseDouble(linkOneLengthTextField.getText());
         double linkTwoLength = Double.parseDouble(linkTwoLengthTextField.getText());
         _arm = new DrawingArm(linkOneLength, linkTwoLength);
         int onePosOneTicks = Integer.parseInt(servoOnePosOneTextField.getText());
         int onePosTwoTicks = Integer.parseInt(servoOnePosTwoTextField.getText());
         _arm.calibrateOne(90, onePosOneTicks, 45, onePosTwoTicks);
         int twoPosOneTicks = Integer.parseInt(servoTwoPosOneTextField.getText());
         int twoPosTwoTicks = Integer.parseInt(servoTwoPosTwoTextField.getText());
         _arm.calibrateTwo(90, twoPosOneTicks, 135, twoPosTwoTicks);

         _pageOffsetX = Double.parseDouble(pageOffsetXTextField.getText());
         _pageOffsetY = Double.parseDouble(pageOffsetYTextField.getText());

         _calibrated = true;
         _drawButton.setEnabled(_imageLoaded && _calibrated);
         }
      });

      calibrationPanel.add(linkOneLengthPanel);
      calibrationPanel.add(linkTwoLengthPanel);
      calibrationPanel.add(servoOnePosOnePanel);
      calibrationPanel.add(servoOnePosTwoPanel);
      calibrationPanel.add(servoTwoPosOnePanel);
      calibrationPanel.add(servoTwoPosTwoPanel);
      calibrationPanel.add(pageOffsetXPanel);
      calibrationPanel.add(pageOffsetYPanel);
      calibrationPanel.add(calibrateButton);

      return calibrationPanel;
      }
   }
