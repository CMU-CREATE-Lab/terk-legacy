package RobotClient;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.ImageFormat;
import edu.cmu.ri.mrpl.swing.SavePictureActionListener;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RobotClientGUI extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(RobotClientGUI.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RobotClientGUI.class.getName());
   private static final PropertyResourceBundle GUI_CLIENT_HELPER_RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(GUIClientHelper.class.getName());

   /** Line separator, used for appending messages to the message area */
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   /** Date formatter, used for time-stamping messages in the message area */
   private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss,SSS: ");

   private final JTextField textField = new JTextField(5);

   // text area for messages
   private final JTextArea messageTextArea = new JTextArea(10, 60);
   private final JButton savePictureButton = GUIConstants.createButton(RESOURCES.getString("button.label.save.picture"), true);
   private final JButton pauseResumeVideoButton = GUIConstants.createButton(RESOURCES.getString("button.label.start.video"));
   private final JButton startStopProgramButton = GUIConstants.createButton(RESOURCES.getString("button.label.start.program"));

   private boolean isVideoStreamPaused = true;
   private boolean isRunningUserCode = false;
   private boolean isExecutionCancelled = false;
   private final Runnable clearMessageTextAreaRunnable =
         new Runnable()
         {
         public void run()
            {
            clearMessageAreaWorkhorse();
            }
         };
   private final SetStartStopButtonLabelRunnable setStartButtonLabelRunnable = new SetStartStopButtonLabelRunnable(RESOURCES.getString("button.label.start.program"));
   private final SetStartStopButtonLabelRunnable setStopButtonLabelRunnable = new SetStartStopButtonLabelRunnable(RESOURCES.getString("button.label.stop.program"));
   private final RobotClientEventHandler robotClientEventHandler;
   private final byte[] dataSynchronizationLock = new byte[0];

   RobotClientGUI(final String applicationName,
                  final String relayCommunicatorIcePropertiesFile,
                  final String directConnectCommunicatorIcePropertiesFile,
                  final RobotClientEventHandler robotClientEventHandler,
                  final String peerHostname)
      {
      this(applicationName,
           relayCommunicatorIcePropertiesFile,
           directConnectCommunicatorIcePropertiesFile,
           robotClientEventHandler);

      // block and wait for the connection to be established (or until it fails)
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      getConnectDisconnectButton().setText(GUI_CLIENT_HELPER_RESOURCES.getString("button.label.connecting"));
      getConnectDisconnectButton().setEnabled(false);
      doHeadlessConnectToPeer(peerHostname);
      writeToTextBox(MessageFormat.format(RESOURCES.getString("message.autoconnecting"), peerHostname));
      synchronized (dataSynchronizationLock)
         {
         while (true)
            {
            try
               {
               dataSynchronizationLock.wait();
               LOG.info("After wait()!");
               break;
               }
            catch (InterruptedException e)
               {
               LOG.error("InterruptedException", e);
               break;
               }
            }
         }
      LOG.debug("Done waiting for a connection!");
      getConnectDisconnectButton().setEnabled(true);
      setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

      // now simulate clicking on the Play button (but only if we're connected)
      if (getQwerkController() != null)
         {
         setStartStopButtonLabelDependingOnRunningState();
         isRunningUserCode = true;
         try
            {
            robotClientEventHandler.executeUponPlay();
            }
         catch (Exception e)
            {
            LOG.warn("Exception caught while executing executeUponStart()", e);
            }
         }
      else
         {
         LOG.info("Ack!");
         }
      }

   RobotClientGUI(final String applicationName,
                  final String relayCommunicatorIcePropertiesFile,
                  final String directConnectCommunicatorIcePropertiesFile,
                  final RobotClientEventHandler robotClientEventHandler)
      {
      super(applicationName, relayCommunicatorIcePropertiesFile, directConnectCommunicatorIcePropertiesFile);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterRelayLogin()
               {
               writeToTextBox("Logged in to relay.");
               }

            public void executeAfterRelayLogout()
               {
               writeToTextBox("Logged out from relay.");
               }

            public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
               {
               writeToTextBox("Connected to qwerk " + qwerkUserId);
               synchronized (dataSynchronizationLock)
                  {
                  dataSynchronizationLock.notifyAll();
                  }
               }

            public void executeBeforeDisconnectingFromQwerk()
               {
               writeToTextBox("Disconnecting from qwerk...");
               super.executeBeforeDisconnectingFromQwerk();
               }

            public void executeAfterDisconnectingFromQwerk(final String qwerkUserId)
               {
               writeToTextBox("Disconnected from qwerk " + qwerkUserId);
               }

            public void executeUponFailureToConnectToQwerk(final String qwerkUserId)
               {
               writeToTextBox("Failed to connect to qwerk " + qwerkUserId);
               getConnectDisconnectButton().setText(GUI_CLIENT_HELPER_RESOURCES.getString("button.label.connect"));
               synchronized (dataSynchronizationLock)
                  {
                  dataSynchronizationLock.notifyAll();
                  }
               }

            public void toggleGUIElementState(final boolean isConnectedToQwerk)
               {
               messageTextArea.setEnabled(isConnectedToQwerk);
               textField.setEnabled(isConnectedToQwerk);
               pauseResumeVideoButton.setEnabled(isConnectedToQwerk);
               startStopProgramButton.setEnabled(isConnectedToQwerk);
               isVideoStreamPaused = true;
               pauseResumeVideoButton.setText(RESOURCES.getString("button.label.start.video"));
               }
            });
      this.robotClientEventHandler = robotClientEventHandler;

      // CONFIGURE GUI ELEMENTS ========================================================================================

      textField.setEnabled(false);

      // set up the message text area
      messageTextArea.setFont(new Font("Monospaced", 0, 10));
      messageTextArea.setLineWrap(true);
      messageTextArea.setWrapStyleWord(true);
      messageTextArea.setEditable(false);
      messageTextArea.setEnabled(false);
      final JScrollPane messageTextAreaScrollPane = new JScrollPane(messageTextArea,
                                                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      startStopProgramButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected void executeGUIActionBefore()
               {
               // setStartStopButtonLabelDependingOnRunningState();
               }

            protected Object executeTimeConsumingAction()
               {
               // if we're running, then we want to stop, and vice versa
               if (isRunningUserCode)
                  {
                  setStartStopButtonLabelDependingOnRunningState();
                  stopExecution();
                  }
               else
                  {
                  setStartStopButtonLabelDependingOnRunningState();
                  isRunningUserCode = true;
                  try
                     {
                     robotClientEventHandler.executeUponPlay();
                     }
                  catch (Exception e)
                     {
                     LOG.warn("Exception caught while executing executeUponStart()", e);
                     }
                  }
               return null;
               }

            protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
               {
               // set the button label back to Start and reset the running flag
               if (isRunningUserCode)
                  {
                  //  setStartStopButtonLabelDependingOnRunningState();
                  //  isRunningUserCode = false;
                  }
               }
            });

      pauseResumeVideoButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected void executeGUIActionBefore()
               {
               RobotClientGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               if (isVideoStreamPaused)
                  {
                  pauseResumeVideoButton.setText(RESOURCES.getString("button.label.pause.video"));
                  }
               else
                  {
                  pauseResumeVideoButton.setText(RESOURCES.getString("button.label.resume.video"));
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
               RobotClientGUI.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
               }
            });

      savePictureButton.addActionListener(new SavePictureActionListener(this, getVideoStreamViewport().getComponent(), ImageFormat.JPEG));

      // LAYOUT GUI ELEMENTS ===========================================================================================

      // create a JPanel to hold connection stuff
      final JPanel connectionPanel = new JPanel(new SpringLayout());
      connectionPanel.add(getConnectDisconnectButton());
      connectionPanel.add(getConnectionStatePanel());
      SpringLayoutUtilities.makeCompactGrid(connectionPanel,
                                            1, 2, // rows, cols
                                            0, 0, // initX, initY
                                            10, 10);// xPad, yPad

      // create a JPanel to hold video buttons
      final JPanel videoButtonsPanel = new JPanel(new SpringLayout());
      videoButtonsPanel.add(Box.createHorizontalGlue());
      videoButtonsPanel.add(savePictureButton);
      videoButtonsPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      videoButtonsPanel.add(pauseResumeVideoButton);
      videoButtonsPanel.add(Box.createHorizontalGlue());
      SpringLayoutUtilities.makeCompactGrid(videoButtonsPanel,
                                            1, 5, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold video viewport and buttons
      final JPanel videoPanel = new JPanel(new SpringLayout());
      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));
      videoPanel.add(getVideoStreamViewportComponent());
      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));

      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));
      videoPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));

      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));
      videoPanel.add(videoButtonsPanel);
      videoPanel.add(Box.createRigidArea(new Dimension(100, 5)));
      SpringLayoutUtilities.makeCompactGrid(videoPanel,
                                            3, 3, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold the user's button and text area
      final JPanel userControls = new JPanel(new SpringLayout());
      userControls.add(startStopProgramButton);
      userControls.add(Box.createRigidArea(new Dimension(5, 5)));
      userControls.add(textField);
      SpringLayoutUtilities.makeCompactGrid(userControls,
                                            1, 3, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // create a JPanel to hold the user's program stuff
      final JPanel userPanel = new JPanel(new SpringLayout());
      userPanel.add(userControls);
      userPanel.add(Box.createRigidArea(new Dimension(5, 5)));
      userPanel.add(messageTextAreaScrollPane);
      SpringLayoutUtilities.makeCompactGrid(userPanel,
                                            3, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // Layout the main content pane using SpringLayout
      getMainContentPane().setLayout(new SpringLayout());
      getMainContentPane().add(connectionPanel);
      getMainContentPane().add(Box.createRigidArea(new Dimension(5, 10)));
      getMainContentPane().add(videoPanel);
      getMainContentPane().add(Box.createRigidArea(new Dimension(5, 20)));
      getMainContentPane().add(userPanel);
      getMainContentPane().add(Box.createRigidArea(new Dimension(5, 10)));
      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            6, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 0);// xPad, yPad

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

   /**
    * Returns the {@link QwerkController} used to control the qwerk (may be <code>null</code>, such as when not
    * connected to a Qwerk).
    */
   QwerkController qwerkController()
      {
      return super.getQwerkController();
      }

   private void setStartStopButtonLabelDependingOnRunningState()
      {
      SwingUtilities.invokeLater(isRunningUserCode ? setStartButtonLabelRunnable : setStopButtonLabelRunnable);
      }

   private void stopExecution()
      {
      if (isRunningUserCode)
         {
         try
            {
            robotClientEventHandler.executeUponStop();
            }
         catch (Exception e)
            {
            LOG.warn("Exception caught while executing executeUponStop()", e);
            }
         setStartStopButtonLabelDependingOnRunningState();
         }
      isRunningUserCode = false;
      isExecutionCancelled = true;
      }

   @SuppressWarnings({"BusyWait"})
   final boolean sleepAndReturnTrueIfCancelled(final int millisecondsToSleep)
      {
      isExecutionCancelled = false;
      try
         {
         final int sleepIncrement = (millisecondsToSleep < 50) ? millisecondsToSleep : 50;
         int millisecondsSlept = 0;
         while (!isExecutionCancelled && millisecondsSlept < millisecondsToSleep)
            {
            Thread.sleep(sleepIncrement);
            millisecondsSlept += sleepIncrement;
            }
         }
      catch (InterruptedException e1)
         {
         LOG.error("InterruptedException while sleeping", e1);
         }

      return isExecutionCancelled;
      }

   //todo override the inner class
   public void handleRelayLogoutEvent()
      {
      stopExecution();
      //super.handleRelayLogoutEvent();
      }

   //todo override the inner class
   public void handleForcedLogoutNotificationEvent()
      {
      stopExecution();
      //super.handleForcedLogoutNotificationEvent();
      }

   //todo override the inner class
   public void handlePeerDisconnectedEvent(final String peerUserId)
      {
      stopExecution();
      //super.handlePeerDisconnectedEvent(peerUserId);
      }

   /** Appends the given <code>message</code> to the message text area */
   void writeToTextBox(final String message)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         appendMessageWorkhorse(message);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  appendMessageWorkhorse(message);
                  }
               });
         }
      }

   private void appendMessageWorkhorse(final String message)
      {
      SwingUtils.warnIfNotEventDispatchThread("RobotClient.appendMessageWorkhorse()");
      messageTextArea.append(dateFormatter.format(new Date()) + message + LINE_SEPARATOR);
      messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
      }

   /** Clears the message text area */
   void clearTextBox()
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         clearMessageAreaWorkhorse();
         }
      else
         {
         SwingUtilities.invokeLater(clearMessageTextAreaRunnable);
         }
      }

   private void clearMessageAreaWorkhorse()
      {
      SwingUtils.warnIfNotEventDispatchThread("RobotClient.clearMessageAreaWorkhorse()");
      messageTextArea.setText("");
      }

   /** Retrieves the value from the specified text field as an <code>int</code>. */
   @SuppressWarnings({"UnusedCatchParameter"})
   int getTextFieldValueAsInt()
      {
      final int i;
      final String str = getTextFieldValueAsString();
      try
         {
         i = Integer.parseInt(str);
         }
      catch (NumberFormatException e)
         {
         writeToTextBox("NumberFormatException while trying to convert [" + str + "] into an int.  Returning 0 instead.");
         return 0;
         }
      return i;
      }

   /** Retrieves the value from the specified text field as a {@link String}. */
   @SuppressWarnings({"UnusedCatchParameter"})
   String getTextFieldValueAsString()
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         final String textFieldValue;
         try
            {
            final String text1 = textField.getText();
            textFieldValue = (text1 != null) ? text1.trim() : null;
            }
         catch (Exception e)
            {
            writeToTextBox("Exception while getting the value from text field.  Returning null instead.");
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
                     textFieldValue[0] = textField.getText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the value from text field.", e);
            writeToTextBox("Exception while getting the value from text field.  Returning null instead.");
            return null;
            }

         return textFieldValue[0];
         }
      }

   private class SetStartStopButtonLabelRunnable implements Runnable
      {
      private final String buttonLabel;

      private SetStartStopButtonLabelRunnable(final String buttonLabel)
         {
         this.buttonLabel = buttonLabel;
         }

      public void run()
         {
         startStopProgramButton.setText(buttonLabel);
         }
      }
   }
