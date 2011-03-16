import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.QwerkState;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamEventListener;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.ImageFormat;
import edu.cmu.ri.mrpl.swing.SavePictureActionListener;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.util.ArrayUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PrototypingPlayground extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(PrototypingPlayground.class);

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = "Prototyping Playground";

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/PrototypingPlayground.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/PrototypingPlayground.relay.ice.properties";

   /** Dimensions used for spacing out GUI elements */
   private static final Dimension SPACER_DIMENSIONS = new Dimension(5, 5);

   /** Line separator, used for appending messages to the message area */
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   /** Number of programmable buttons and text fields */
   private static final int NUM_BUTTONS_AND_TEXT_FIELDS = 8;

   /** Number of columns to display in the text fields */
   private static final int TEXT_FIELD_COLUMNS = 10;

   /** Number of frames between each fps computation */
   private static final int FPS_FRAME_COUNT = 10;
   private static final int FPS_FRAME_COUNT_TIMES_1000 = 1000 * FPS_FRAME_COUNT;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new PrototypingPlayground();
               }
            });
      }

   /** Programmable buttons */
   private final JButton[] buttons = new JButton[NUM_BUTTONS_AND_TEXT_FIELDS];
   private final JTextField[] textFields = new JTextField[NUM_BUTTONS_AND_TEXT_FIELDS];

   /** buttons */
   private final JButton savePictureButton = GUIConstants.createButton("Save Picture", true);
   private final JButton pauseResumeVideoButton = GUIConstants.createButton("Start Video");
   private final JButton clearMessageAreaButton = GUIConstants.createButton("Clear", true);

   private final JLabel framesPerSecondLabel = GUIConstants.createLabel("0 fps");

   private boolean isVideoStreamPaused = true;

   private long fpsTimestamp = 0;
   private int fpsCount = 0;
   private float fps = 0;
   private final FPSDisplayRunnable fpsDisplayRunnable = new FPSDisplayRunnable();

   /** Date formatter, used for time-stamping messages in the message area */
   private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss,SSS: ");

   // text area for messages
   private final JTextArea messageTextArea = new JTextArea(16, 50);

   private PrototypingPlayground()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterRelayLogin()
               {
               appendMessage("Logged in to relay.");
               }

            public void executeAfterRelayLogout()
               {
               appendMessage("Logged out from relay.");
               }

            public void executeBeforeDisconnectingFromQwerk()
               {
               appendMessage("Disconnecting from qwerk...");
               super.executeBeforeDisconnectingFromQwerk();
               }

            public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
               {
               appendMessage("Connected to qwerk " + qwerkUserId);
               isVideoStreamPaused = true;
               }

            public void executeAfterDisconnectingFromQwerk(final String qwerkUserId)
               {
               appendMessage("Disconnected from qwerk " + qwerkUserId);
               fps = 0;
               updateFramesPerSecondLabel();
               }

            public void toggleGUIElementState(final boolean isConnectedToQwerk)
               {
               for (int i = 0; i < NUM_BUTTONS_AND_TEXT_FIELDS; i++)
                  {
                  buttons[i].setEnabled(isConnectedToQwerk);
                  textFields[i].setEnabled(isConnectedToQwerk);
                  }
               messageTextArea.setEnabled(isConnectedToQwerk);
               pauseResumeVideoButton.setEnabled(isConnectedToQwerk);
               pauseResumeVideoButton.setText("Start Video");
               }
            });

      // CONFIGURE GUI ELEMENTS ========================================================================================

      // create and configure the buttons and text fields
      for (int i = 0; i < NUM_BUTTONS_AND_TEXT_FIELDS; i++)
         {
         final JButton button = new JButton();
         button.setFont(GUIConstants.FONT_SMALL);
         button.setEnabled(false);
         button.setOpaque(false);// required for Macintosh

         buttons[i] = button;

         textFields[i] = new JTextField(TEXT_FIELD_COLUMNS);
         final Dimension fieldSize = new Dimension(textFields[i].getWidth(), textFields[i].getHeight());
         textFields[i].setMaximumSize(fieldSize);
         textFields[i].setEnabled(false);
         }

      // set up the message text area
      messageTextArea.setFont(new Font("Monospaced", 0, 10));
      messageTextArea.setLineWrap(true);
      messageTextArea.setWrapStyleWord(true);
      messageTextArea.setEditable(false);
      messageTextArea.setEnabled(false);
      final JScrollPane messageTextAreaScrollPane = new JScrollPane(messageTextArea,
                                                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      // add action listeners to the buttons
      buttons[0].setText("Get State");
      buttons[0].addActionListener(new GetQwerkStateAction());

      buttons[1].setText("Move Motor X");
      buttons[1].addActionListener(new MoveMotorAction(1));

      buttons[2].setText("Move Servo 0");
      buttons[2].addActionListener(new MoveServoAction(2, 0));

      buttons[3].setText("Set Dig Out X On");
      buttons[3].addActionListener(new SingleDigitalOutAction(3, true));

      buttons[4].setText("Set Dig Out X Off");
      buttons[4].addActionListener(new SingleDigitalOutAction(4, false));

      buttons[5].setText("Speed Test");
      buttons[5].addActionListener(new SpeedTestAction());

      buttons[6].setText("Your Code");

      buttons[7].setText("Your Code");

      savePictureButton.addActionListener(new SavePictureActionListener(this, getVideoStreamViewport().getComponent(), ImageFormat.JPEG));
      pauseResumeVideoButton.addActionListener(new PauseResumeVideoAction());
      clearMessageAreaButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent actionEvent)
               {
               messageTextArea.setText("");
               }
            });

      // compute and display frames per second
      getVideoStreamPlayer().addVideoStreamEventListener(
            new VideoStreamEventListener()
            {
            public void handleFrame(final byte[] frameData)
               {
               fpsCount++;

               if (fpsCount == FPS_FRAME_COUNT)
                  {
                  final long elapsedMilliseconds = System.currentTimeMillis() - fpsTimestamp;

                  fps = FPS_FRAME_COUNT_TIMES_1000 / (float)elapsedMilliseconds;

                  updateFramesPerSecondLabel();

                  fpsTimestamp = System.currentTimeMillis();
                  fpsCount = 0;
                  }
               }
            });

      updateFramesPerSecondLabel();

      // LAYOUT GUI ELEMENTS ===========================================================================================

      // create a JPanel to hold the buttons and text fields
      final JPanel buttonsAndTextFieldsPanel = new JPanel(new SpringLayout());
      for (int i = 0; i < NUM_BUTTONS_AND_TEXT_FIELDS; i++)
         {
         buttonsAndTextFieldsPanel.add(buttons[i]);
         buttonsAndTextFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
         buttonsAndTextFieldsPanel.add(textFields[i]);
         }
      SpringLayoutUtilities.makeCompactGrid(buttonsAndTextFieldsPanel,
                                            NUM_BUTTONS_AND_TEXT_FIELDS, 3, // rows, cols
                                            0, 0, // initX, initY
                                            0, 5);// xPad, yPad

      // create a JPanel to hold the buttons, text fields, and color panel
      final JPanel videoButtonsPanel = new JPanel(new SpringLayout());
      videoButtonsPanel.add(savePictureButton);
      videoButtonsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
      videoButtonsPanel.add(pauseResumeVideoButton);
      videoButtonsPanel.add(Box.createGlue());
      videoButtonsPanel.add(framesPerSecondLabel);
      SpringLayoutUtilities.makeCompactGrid(videoButtonsPanel,
                                            1, 5, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold the buttons, text fields, and color panel
      final JPanel videoPanel = new JPanel(new SpringLayout());
      videoPanel.add(getVideoStreamViewportComponent());
      videoPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      videoPanel.add(videoButtonsPanel);
      SpringLayoutUtilities.makeCompactGrid(videoPanel,
                                            3, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold video port and the buttons
      final JPanel controlsPanel = new JPanel(new SpringLayout());
      controlsPanel.add(getConnectDisconnectButton());
      controlsPanel.add(getConnectionStatePanel());
      controlsPanel.add(videoPanel);
      controlsPanel.add(buttonsAndTextFieldsPanel);
      SpringLayoutUtilities.makeCompactGrid(controlsPanel,
                                            2, 2, // rows, cols
                                            0, 0, // initX, initY
                                            10, 10);// xPad, yPad

      // create a JPanel to hold video port and the buttons
      final JPanel messageAreaButtonsPanel = new JPanel(new SpringLayout());
      messageAreaButtonsPanel.add(Box.createGlue());
      messageAreaButtonsPanel.add(clearMessageAreaButton);
      SpringLayoutUtilities.makeCompactGrid(messageAreaButtonsPanel,
                                            1, 2, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold video port and the buttons
      final JPanel messageArea = new JPanel(new SpringLayout());
      messageArea.add(messageTextAreaScrollPane);
      messageArea.add(messageAreaButtonsPanel);
      SpringLayoutUtilities.makeCompactGrid(messageArea,
                                            2, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 5);// xPad, yPad

      // Layout the main content pane using SpringLayout
      getMainContentPane().setLayout(new SpringLayout());
      getMainContentPane().add(controlsPanel);
      getMainContentPane().add(messageArea);
      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            2, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 10);// xPad, yPad

      // ADDITIONAL GUI ELEMENT CONFIGURATION ==========================================================================

      // pack the window so the GUI elements are properly sized
      pack();

      // limit the text area's size (must do this AFTER the call to pack())
      final Dimension messageTextAreaScrollPaneDimensions = new Dimension(messageTextArea.getWidth(), messageTextArea.getHeight());
      messageTextAreaScrollPane.setPreferredSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMinimumSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMaximumSize(new Dimension(10000, messageTextArea.getHeight()));

      pack();

      setLocationRelativeTo(null);// center the window on the screen

      setVisible(true);
      }

   private void updateFramesPerSecondLabel()
      {
      SwingUtilities.invokeLater(fpsDisplayRunnable);
      }

   /** Appends the given <code>message</code> to the message text area */
   private void appendMessage(final String message)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               messageTextArea.append(dateFormatter.format(new Date()) + message + LINE_SEPARATOR);
               messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
               }
            });
      }

   /** Retrieves the value from the specified text field as an <code>int</code>. */
   @SuppressWarnings({"UnusedCatchParameter"})
   private int getTextFieldValueAsInt(final int textFieldIndex)
      {
      final int i;
      final String str = getTextFieldValueAsString(textFieldIndex);
      try
         {
         i = Integer.parseInt(str);
         }
      catch (NumberFormatException e)
         {
         appendMessage("NumberFormatException while trying to convert [" + str + "] into an int.  Returning 0 instead.");
         return 0;
         }
      return i;
      }

   /** Retrieves the value from the specified text field as a {@link String}. */
   @SuppressWarnings({"UnusedCatchParameter"})
   private String getTextFieldValueAsString(final int textFieldIndex)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         final String textFieldValue;
         try
            {
            final String text1 = textFields[textFieldIndex].getText();
            textFieldValue = (text1 != null) ? text1.trim() : null;
            }
         catch (Exception e)
            {
            appendMessage("Exception while getting the value from text field " + textFieldIndex + ".  Returning null instead.");
            return null;
            }
         return textFieldValue;
         }
      else
         {
         final String[] textFieldValue = new String[1];
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     textFieldValue[0] = textFields[textFieldIndex].getText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the value from text field " + textFieldIndex, e);
            appendMessage("Exception while getting the value from text field " + textFieldIndex + ".  Returning null instead.");
            return null;
            }

         return textFieldValue[0];
         }
      }

   private class PauseResumeVideoAction extends AbstractTimeConsumingAction
      {
      private PauseResumeVideoAction()
         {
         super(PrototypingPlayground.this);
         }

      protected void executeGUIActionBefore()
         {
         PrototypingPlayground.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
         if (isVideoStreamPaused)
            {
            pauseResumeVideoButton.setText("Pause Video");
            }
         else
            {
            pauseResumeVideoButton.setText("Resume Video");
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (isVideoStreamPaused)
            {
            getVideoStreamPlayer().resumeVideoStream();
            }
         else
            {
            getVideoStreamPlayer().pauseVideoStream();
            }
         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         isVideoStreamPaused = !isVideoStreamPaused;
         PrototypingPlayground.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         fpsTimestamp = System.currentTimeMillis();
         fpsCount = 0;
         fps = 0;
         updateFramesPerSecondLabel();
         }
      }

   private final class GetQwerkStateAction extends AbstractTimeConsumingAction
      {
      private GetQwerkStateAction()
         {
         super(PrototypingPlayground.this);
         }

      protected Object executeTimeConsumingAction()
         {
         return getQwerkController().getQwerkState();
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         // read the values from QwerkState and format it nicely for display in the message area
         final QwerkState state = (QwerkState)resultOfTimeConsumingAction;
         if (state != null)
            {
            final StringBuffer s = new StringBuffer("Qwerk State:" + LINE_SEPARATOR);
            if (state.analogIn != null)
               {
               s.append("   Analog Inputs:    ").append(ArrayUtils.arrayToString(state.analogIn.analogInValues)).append(LINE_SEPARATOR);
               }
            if (state.button != null)
               {
               s.append("   Button State:     ").append(state.button.buttonStates[0]).append(LINE_SEPARATOR);
               }
            if (state.digitalIn != null)
               {
               s.append("   Digital Inputs:   ").append(ArrayUtils.arrayToString(state.digitalIn.digitalInStates)).append(LINE_SEPARATOR);
               }
            if (state.motor != null)
               {
               s.append("   Motor Currents:   ").append(ArrayUtils.arrayToString(state.motor.motorCurrents)).append(LINE_SEPARATOR);
               s.append("   Motor Positions:  ").append(ArrayUtils.arrayToString(state.motor.motorPositions)).append(LINE_SEPARATOR);
               s.append("   Motor Velocities: ").append(ArrayUtils.arrayToString(state.motor.motorVelocities)).append(LINE_SEPARATOR);
               s.append("   Motor Done:       ").append(ArrayUtils.arrayToString(state.motor.motorDone)).append(LINE_SEPARATOR);
               }
            if (state.servo != null)
               {
               s.append("   Servo Positions:  ").append(ArrayUtils.arrayToString(state.servo.servoPositions)).append(LINE_SEPARATOR);
               }
            if (state.battery != null)
               {
               s.append("   Battery Voltage:  ").append(state.battery.batteryVoltage).append(LINE_SEPARATOR);
               }

            // display the state in the message area
            appendMessage(s.toString());
            }
         else
            {
            appendMessage("QwerkState is null!");
            }
         }
      }

   private final class MoveMotorAction extends AbstractTimeConsumingAction
      {
      private final int buttonIndex;

      private MoveMotorAction(final int buttonIndex)
         {
         super(PrototypingPlayground.this);
         this.buttonIndex = buttonIndex;
         }

      protected Object executeTimeConsumingAction()
         {
         // get the motor index
         final int motorIndex = getTextFieldValueAsInt(buttonIndex);

         // set the motor velocity
         getQwerkController().getMotorService().setMotorVelocity(20000, motorIndex);

         // sleep for 5 seconds
         try
            {
            Thread.sleep(5000);
            }
         catch (InterruptedException e1)
            {
            LOG.error("InterruptedException while sleeping", e1);
            }

         // stop the motor
         getQwerkController().getMotorService().stopMotors(motorIndex);

         return null;
         }
      }

   private final class MoveServoAction extends AbstractTimeConsumingAction
      {
      private final int buttonIndex;
      private final int servoIndex;

      private MoveServoAction(final int buttonIndex, final int servoIndex)
         {
         super(PrototypingPlayground.this);
         this.buttonIndex = buttonIndex;
         this.servoIndex = servoIndex;
         }

      protected Object executeTimeConsumingAction()
         {
         // get the desired servo position
         final int servoPosition = getTextFieldValueAsInt(buttonIndex);

         // set the servo position
         getQwerkController().getServoService().setPosition(servoIndex, servoPosition);

         return null;
         }
      }

   private final class SingleDigitalOutAction extends AbstractTimeConsumingAction
      {
      private final boolean digitalOutState;
      private final int buttonIndex;

      private SingleDigitalOutAction(final int buttonIndex, final boolean digitalOutState)
         {
         super(PrototypingPlayground.this);
         this.buttonIndex = buttonIndex;
         this.digitalOutState = digitalOutState;
         }

      protected Object executeTimeConsumingAction()
         {
         // get the desired digital out port
         final int digitalOutPort = getTextFieldValueAsInt(buttonIndex);

         appendMessage("Sending [" + digitalOutState + "] to digital out port [" + digitalOutPort + "]...");

         // set the digital out
         getQwerkController().getDigitalOutService().setOutputs(digitalOutState, digitalOutPort);

         appendMessage("Done sending digital out command!");

         return null;
         }
      }

   private class SpeedTestAction extends AbstractTimeConsumingAction
      {
      private static final int NUM_CALLS = 50;

      private SpeedTestAction()
         {
         super(PrototypingPlayground.this);
         }

      protected Object executeTimeConsumingAction()
         {
         final long[] millisecondsPerCall = new long[NUM_CALLS];

         final QwerkController qwerkController = getQwerkController();

         for (int i = 0; i < NUM_CALLS; i++)
            {
            final long startTime = System.currentTimeMillis();
            qwerkController.getQwerkState();
            final long endTime = System.currentTimeMillis();
            millisecondsPerCall[i] = endTime - startTime;
            }

         return millisecondsPerCall;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         final long[] millisecondsPerCall = (long[])resultOfTimeConsumingAction;
         final StringBuffer str = new StringBuffer("Milliseconds per call: " + LINE_SEPARATOR);
         int sum = 0;
         for (int i = 0; i < NUM_CALLS; i++)
            {
            str.append("   ").append(millisecondsPerCall[i]).append(LINE_SEPARATOR);
            sum += millisecondsPerCall[i];
            }
         str.append("   Total:   ").append(sum).append(" ms");
         str.append("   Average: ").append(sum / (double)NUM_CALLS).append(" ms");
         appendMessage(str.toString());
         }
      }

   private final class FPSDisplayRunnable implements Runnable
      {
      private final NumberFormat decimalFormatter = new DecimalFormat("#,##0.0");

      public void run()
         {
         PrototypingPlayground.this.framesPerSecondLabel.setText(decimalFormatter.format(fps) + " fps");
         }
      }
   }
