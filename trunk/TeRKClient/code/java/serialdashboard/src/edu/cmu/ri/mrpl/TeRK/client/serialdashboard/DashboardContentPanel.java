package edu.cmu.ri.mrpl.TeRK.client.serialdashboard;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import edu.cmu.ri.createlab.TeRK.serial.ASCIICharacter;
import edu.cmu.ri.createlab.TeRK.serial.BaudRate;
import edu.cmu.ri.createlab.TeRK.serial.CharacterSize;
import edu.cmu.ri.createlab.TeRK.serial.FlowControl;
import edu.cmu.ri.createlab.TeRK.serial.Parity;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOConfiguration;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOException;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOService;
import edu.cmu.ri.createlab.TeRK.serial.StopBits;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkControllerProvider;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class DashboardContentPanel extends JPanel
   {
   private static final Logger LOG = Logger.getLogger(DashboardContentPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DashboardContentPanel.class.getName());

   /** Dimensions used for spacing out GUI elements */
   private static final Dimension SPACER_DIMENSIONS = new Dimension(5, 5);
   private static final Dimension SPACER_DIMENSIONS_BIG = new Dimension(10, 10);

   /** Line separator, used for appending messages to the message area */
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   /** Number of columns to display in the text areas */
   private static final int TEXT_AREA_COLUMNS = 50;

   /** Name of the serial port device controlled by this GUI */
   private final String serialPortDeviceName;
   private final QwerkControllerProvider qwerkControllerProvider;

   /** Date formatter, used for time-stamping messages in the message area */
   private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss,SSS: ");

   private final JButton openPortButton = GUIConstants.createButton(RESOURCES.getString("button.label.open-port"));
   private final JButton closePortButton = GUIConstants.createButton(RESOURCES.getString("button.label.close-port"));
   private final JButton isPortOpenButton = GUIConstants.createButton(RESOURCES.getString("button.label.is-port-open"));
   private final JButton isDataAvailableButton = GUIConstants.createButton(RESOURCES.getString("button.label.is-data-available"));
   private final JButton writeStringToPortButton = GUIConstants.createButton(RESOURCES.getString("button.label.write"));
   private final JButton chooseFileButton = GUIConstants.createButton(RESOURCES.getString("button.label.browser"));
   private final JButton writeFileToPortButton = GUIConstants.createButton(RESOURCES.getString("button.label.write"));
   private final JButton readFromPortButton = GUIConstants.createButton(RESOURCES.getString("button.label.read"));
   private final JButton readFromPortUntilDelimiterButton = GUIConstants.createButton(RESOURCES.getString("button.label.read-until-delimiter"));
   private final JButton blockingReadFromPortButton = GUIConstants.createButton(RESOURCES.getString("button.label.blocking-read"));
   private final JButton blockingReadFromPortUntilDelimiterButton = GUIConstants.createButton(RESOURCES.getString("button.label.blocking-read-until-delimiter"));

   private final JButton clearInputAreaButton = GUIConstants.createButton(RESOURCES.getString("button.label.clear"));
   private final JButton clearMessageAreaButton = GUIConstants.createButton(RESOURCES.getString("button.label.clear"));

   private final JComboBox baudRateComboBox = new JComboBox(BaudRate.values());
   private final JComboBox flowControlComboBox = new JComboBox(FlowControl.values());
   private final JComboBox characterSizeComboBox = new JComboBox(CharacterSize.values());
   private final JComboBox parityComboBox = new JComboBox(Parity.values());
   private final JComboBox stopBitsComboBox = new JComboBox(StopBits.values());
   private final JComboBox delimiterCharacterComboBox = new JComboBox(ASCIICharacter.values());

   // text fields for output
   private final JTextField outputStringTextField = new JTextField(20);
   private final JTextField outputFilePathTextField = new JTextField(20);

   private final JFormattedTextField numBytesToReadTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());
   private final JFormattedTextField timeoutTextField = new JFormattedTextField(NumberFormat.getIntegerInstance());

   private final PropertyChangeListener numBytesToReadTextFieldPropertyChangeListener = new IntegerTextFieldPropertyChangeListener(numBytesToReadTextField);
   private final PropertyChangeListener timeoutTextFieldPropertyChangeListener = new IntegerTextFieldPropertyChangeListener(timeoutTextField);

   // text area for input
   private final JTextArea inputTextArea = createTextArea(8, TEXT_AREA_COLUMNS);

   // text area for messages
   private final JTextArea messageTextArea = createTextArea(8, TEXT_AREA_COLUMNS);

   private final TextComponentValidator isNonEmptyValidator =
         new TextComponentValidator()
         {
         public boolean isValid(final JTextComponent textComponent)
            {
            return textComponent != null && isTextComponentNonEmpty(textComponent);
            }
         };

   private final TextComponentValidator isFileValidator =
         new TextComponentValidator()
         {
         public boolean isValid(final JTextComponent textComponent)
            {
            if (isTextComponentNonEmpty(textComponent))
               {
               final File file = getTextComponentValueAsFile(textComponent);
               return file != null && file.exists() && file.isFile();
               }
            return false;
            }
         };

   private final KeyListener outputStringKeyListener = new EnableButtonIfTextFieldIsValidKeyAdapter(writeStringToPortButton, outputStringTextField, isNonEmptyValidator);
   private final KeyListener outputFilePathKeyListener = new EnableButtonIfTextFieldIsValidKeyAdapter(writeFileToPortButton, outputFilePathTextField, isFileValidator);

   private final JFileChooser fileChooser = new JFileChooser();

   DashboardContentPanel(final String serialPortDeviceName, final QwerkControllerProvider qwerkControllerProvider)
      {
      super(new SpringLayout());
      this.serialPortDeviceName = serialPortDeviceName;
      this.qwerkControllerProvider = qwerkControllerProvider;

      // CONFIGURE GUI ELEMENTS ========================================================================================

      // add action listeners to the buttons
      openPortButton.addActionListener(new OpenSerialPortAction());
      closePortButton.addActionListener(new CloseSerialPortAction());
      isPortOpenButton.addActionListener(new IsSerialPortOpenAction());
      isDataAvailableButton.addActionListener(new IsDataAvailableAction());
      writeStringToPortButton.addActionListener(new WriteStringToSerialPortAction(outputStringTextField));
      chooseFileButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               final int returnValue = fileChooser.showOpenDialog(DashboardContentPanel.this);
               if (returnValue == JFileChooser.APPROVE_OPTION)
                  {
                  final File file = fileChooser.getSelectedFile();
                  outputFilePathTextField.setText(file.getAbsolutePath());
                  outputFilePathKeyListener.keyReleased(null);

                  LOG.info("File chosen: " + file.getAbsolutePath());
                  }
               else
                  {
                  LOG.info("Open command cancelled by user.");
                  }
               }
            });
      writeFileToPortButton.addActionListener(new WriteFileToSerialPortAction(outputFilePathTextField));
      readFromPortButton.addActionListener(new ReadFromSerialPortAction(numBytesToReadTextField));
      readFromPortUntilDelimiterButton.addActionListener(new ReadFromSerialPortUntilDelimiterAction(numBytesToReadTextField, delimiterCharacterComboBox));
      blockingReadFromPortButton.addActionListener(new BlockingReadFromSerialPortAction(numBytesToReadTextField, timeoutTextField));
      blockingReadFromPortUntilDelimiterButton.addActionListener(new BlockingReadFromSerialPortUntilDelimiterAction(numBytesToReadTextField, delimiterCharacterComboBox, timeoutTextField));

      clearInputAreaButton.addActionListener(new JTextComponentClearingActionListener(inputTextArea));
      clearMessageAreaButton.addActionListener(new JTextComponentClearingActionListener(messageTextArea));

      baudRateComboBox.setSelectedItem(BaudRate.BAUD_57600);
      flowControlComboBox.setSelectedItem(FlowControl.NONE);
      characterSizeComboBox.setSelectedItem(CharacterSize.EIGHT);
      parityComboBox.setSelectedItem(Parity.NONE);
      stopBitsComboBox.setSelectedItem(StopBits.ONE);
      delimiterCharacterComboBox.setSelectedItem(ASCIICharacter.ASCII_013);
      baudRateComboBox.setEnabled(false);
      flowControlComboBox.setEnabled(false);
      characterSizeComboBox.setEnabled(false);
      parityComboBox.setEnabled(false);
      stopBitsComboBox.setEnabled(false);
      delimiterCharacterComboBox.setEnabled(false);

      numBytesToReadTextField.setEnabled(false);
      numBytesToReadTextField.setValue(1024);
      numBytesToReadTextField.setColumns(4);
      numBytesToReadTextField.addPropertyChangeListener(numBytesToReadTextFieldPropertyChangeListener);

      timeoutTextField.setEnabled(false);
      timeoutTextField.setValue(2000);
      timeoutTextField.setColumns(4);
      timeoutTextField.addPropertyChangeListener(timeoutTextFieldPropertyChangeListener);

      outputStringTextField.setEnabled(false);
      outputStringTextField.addKeyListener(outputStringKeyListener);

      outputFilePathTextField.setEnabled(false);
      outputFilePathTextField.addKeyListener(outputFilePathKeyListener);

      // set up the text area scroll panes
      final JScrollPane inputTextAreaScrollPane = new JScrollPane(inputTextArea,
                                                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      final JScrollPane messageTextAreaScrollPane = new JScrollPane(messageTextArea,
                                                                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fileChooser.setAcceptAllFileFilterUsed(true);

      // LAYOUT GUI ELEMENTS ===========================================================================================

      final JPanel configurationPanel = new JPanel(new SpringLayout());
      configurationPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.qwerk-serial-port-configuration")));
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.port-name")));
      configurationPanel.add(GUIConstants.createLabel(serialPortDeviceName));
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.baud-rate")));
      configurationPanel.add(baudRateComboBox);
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.flow-control")));
      configurationPanel.add(flowControlComboBox);
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.character-size")));
      configurationPanel.add(characterSizeComboBox);
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.parity")));
      configurationPanel.add(parityComboBox);
      configurationPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.stop-bits")));
      configurationPanel.add(stopBitsComboBox);
      SpringLayoutUtilities.makeCompactGrid(configurationPanel,
                                            2, 6, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final Box openClosePortButtonsPanel = new Box(BoxLayout.X_AXIS);
      openClosePortButtonsPanel.add(Box.createGlue());
      openClosePortButtonsPanel.add(openPortButton);
      openClosePortButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      openClosePortButtonsPanel.add(isPortOpenButton);
      openClosePortButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      openClosePortButtonsPanel.add(isDataAvailableButton);
      openClosePortButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      openClosePortButtonsPanel.add(closePortButton);
      openClosePortButtonsPanel.add(Box.createGlue());

      final Box messageAreaBottomButtonsPanel = new Box(BoxLayout.X_AXIS);
      messageAreaBottomButtonsPanel.add(Box.createGlue());
      messageAreaBottomButtonsPanel.add(clearMessageAreaButton);

      final JPanel messageAreaPanel = new JPanel(new SpringLayout());
      messageAreaPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.messages")));
      messageAreaPanel.add(messageTextAreaScrollPane);
      messageAreaPanel.add(messageAreaBottomButtonsPanel);
      SpringLayoutUtilities.makeCompactGrid(messageAreaPanel,
                                            2, 1, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final Box fileChooserBox = new Box(BoxLayout.X_AXIS);
      fileChooserBox.add(outputFilePathTextField);
      fileChooserBox.add(Box.createRigidArea(SPACER_DIMENSIONS));
      fileChooserBox.add(chooseFileButton);

      final JPanel outputAreaPanel = new JPanel(new SpringLayout());
      outputAreaPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.output")));
      outputAreaPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.text")));
      outputAreaPanel.add(outputStringTextField);
      outputAreaPanel.add(writeStringToPortButton);

      outputAreaPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.file")));
      outputAreaPanel.add(fileChooserBox);
      outputAreaPanel.add(writeFileToPortButton);
      SpringLayoutUtilities.makeCompactGrid(outputAreaPanel,
                                            2, 3, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final Box inputAreaFieldsPanel = new Box(BoxLayout.X_AXIS);
      inputAreaFieldsPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.max-number-of-bytes-to-read")));
      inputAreaFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaFieldsPanel.add(numBytesToReadTextField);
      inputAreaFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaFieldsPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.delimiter")));
      inputAreaFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaFieldsPanel.add(delimiterCharacterComboBox);
      inputAreaFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaFieldsPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.blocking-timeout")));
      inputAreaFieldsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaFieldsPanel.add(timeoutTextField);
      inputAreaFieldsPanel.add(Box.createGlue());

      final Box inputAreaTopButtonsPanel = new Box(BoxLayout.X_AXIS);
      inputAreaTopButtonsPanel.add(Box.createGlue());
      inputAreaTopButtonsPanel.add(readFromPortButton);
      inputAreaTopButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaTopButtonsPanel.add(readFromPortUntilDelimiterButton);
      inputAreaTopButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaTopButtonsPanel.add(blockingReadFromPortButton);
      inputAreaTopButtonsPanel.add(Box.createRigidArea(SPACER_DIMENSIONS));
      inputAreaTopButtonsPanel.add(blockingReadFromPortUntilDelimiterButton);
      inputAreaTopButtonsPanel.add(Box.createGlue());

      final Box inputAreaBottomButtonsPanel = new Box(BoxLayout.X_AXIS);
      inputAreaBottomButtonsPanel.add(Box.createGlue());
      inputAreaBottomButtonsPanel.add(clearInputAreaButton);

      final JPanel inputAreaPanel = new JPanel(new SpringLayout());
      inputAreaPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.input")));
      inputAreaPanel.add(inputAreaFieldsPanel);
      inputAreaPanel.add(inputAreaTopButtonsPanel);
      inputAreaPanel.add(inputTextAreaScrollPane);
      inputAreaPanel.add(inputAreaBottomButtonsPanel);
      SpringLayoutUtilities.makeCompactGrid(inputAreaPanel,
                                            4, 1, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      // Layout the main content pane using SpringLayout
      setLayout(new SpringLayout());
      add(Box.createRigidArea(new Dimension(500, 1)));
      add(configurationPanel);
      add(Box.createRigidArea(SPACER_DIMENSIONS_BIG));
      add(openClosePortButtonsPanel);
      add(Box.createRigidArea(SPACER_DIMENSIONS_BIG));
      add(inputAreaPanel);
      add(Box.createRigidArea(SPACER_DIMENSIONS_BIG));
      add(outputAreaPanel);
      add(Box.createRigidArea(SPACER_DIMENSIONS_BIG));
      add(messageAreaPanel);
      SpringLayoutUtilities.makeCompactGrid(this,
                                            10, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // ADDITIONAL GUI ELEMENT CONFIGURATION ==========================================================================

      setVisible(true);
      }

   private JTextArea createTextArea(final int rows, final int cols)
      {
      final JTextArea textArea = new JTextArea(rows, cols);
      textArea.setFont(new Font("Monospaced", 0, 10));
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setEditable(false);
      textArea.setEnabled(false);
      return textArea;
      }

   void toggleGUIElementState(final boolean isEnabled)
      {
      openPortButton.setEnabled(isEnabled);
      closePortButton.setEnabled(isEnabled);
      isPortOpenButton.setEnabled(isEnabled);
      isDataAvailableButton.setEnabled(isEnabled);
      writeStringToPortButton.setEnabled(isEnabled && isNonEmptyValidator.isValid(outputStringTextField));
      chooseFileButton.setEnabled(isEnabled);
      writeFileToPortButton.setEnabled(isEnabled && isFileValidator.isValid(outputFilePathTextField));
      readFromPortButton.setEnabled(isEnabled);
      readFromPortUntilDelimiterButton.setEnabled(isEnabled);
      blockingReadFromPortButton.setEnabled(isEnabled);
      blockingReadFromPortUntilDelimiterButton.setEnabled(isEnabled);

      clearInputAreaButton.setEnabled(isEnabled);
      clearMessageAreaButton.setEnabled(isEnabled);

      numBytesToReadTextField.setEnabled(isEnabled);
      timeoutTextField.setEnabled(isEnabled);

      baudRateComboBox.setEnabled(isEnabled);
      flowControlComboBox.setEnabled(isEnabled);
      characterSizeComboBox.setEnabled(isEnabled);
      parityComboBox.setEnabled(isEnabled);
      stopBitsComboBox.setEnabled(isEnabled);
      delimiterCharacterComboBox.setEnabled(isEnabled);

      outputStringTextField.setEnabled(isEnabled);
      outputFilePathTextField.setEnabled(isEnabled);
      inputTextArea.setEnabled(isEnabled);
      messageTextArea.setEnabled(isEnabled);
      }

   /** Appends the given <code>message</code> to the message text area */
   void appendMessage(final String message)
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

   /** Appends the given <code>message</code> to the input text area */
   private void appendMessageToInputArea(final String message)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               inputTextArea.append(message + LINE_SEPARATOR);
               inputTextArea.setCaretPosition(inputTextArea.getDocument().getLength());
               }
            });
      }

   /** Retrieves the value from the specified text component as a {@link File}; returns <code>null</code> if the file does not exist. */
   private File getTextComponentValueAsFile(final JTextComponent textComponent)
      {
      final String filePath = getTextComponentValueAsString(textComponent);
      if (filePath != null)
         {
         final File file = new File(filePath);
         if (file.exists())
            {
            return file;
            }
         }
      return null;
      }

   /** Retrieves the value from the specified text field as a {@link String}. */
   @SuppressWarnings({"UnusedCatchParameter"})
   private String getTextComponentValueAsString(final JTextComponent textComponent)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         final String textFieldValue;
         try
            {
            final String text1 = textComponent.getText();
            textFieldValue = (text1 != null) ? text1.trim() : null;
            }
         catch (Exception e)
            {
            appendMessage("Exception while getting the value from the text field.  Returning null instead.");
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
                     textFieldValue[0] = textComponent.getText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the value from text field.", e);
            appendMessage("Exception while getting the value from text field.  Returning null instead.");
            return null;
            }

         return textFieldValue[0];
         }
      }

   private final class JTextComponentClearingActionListener implements ActionListener
      {
      private final JTextComponent component;

      private JTextComponentClearingActionListener(final JTextComponent component)
         {
         this.component = component;
         }

      public void actionPerformed(final ActionEvent actionEvent)
         {
         component.setText("");
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class OpenSerialPortAction extends AbstractTimeConsumingAction
      {
      private SerialIOConfiguration config;

      private OpenSerialPortAction()
         {
         super(DashboardContentPanel.this);
         }

      protected void executeGUIActionBefore()
         {
         config = new SerialIOConfiguration(serialPortDeviceName,
                                            (BaudRate)baudRateComboBox.getSelectedItem(),
                                            (CharacterSize)characterSizeComboBox.getSelectedItem(),
                                            (Parity)parityComboBox.getSelectedItem(),
                                            (StopBits)stopBitsComboBox.getSelectedItem(),
                                            (FlowControl)flowControlComboBox.getSelectedItem());
         }

      protected Object executeTimeConsumingAction()
         {
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               serialIOService.getSerialPort(serialPortDeviceName).open(config);
               appendMessage("Serial port opened.");
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while opening the serial port";
               LOG.error(msg, e);
               appendMessage(msg);
               }
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class WriteStringToSerialPortAction extends AbstractTimeConsumingAction
      {
      private final JTextComponent textComponent;

      private WriteStringToSerialPortAction(final JTextComponent textComponent)
         {
         super(DashboardContentPanel.this);
         this.textComponent = textComponent;
         }

      protected Object executeTimeConsumingAction()
         {
         final String data = getTextComponentValueAsString(textComponent);
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               serialIOService.getSerialPort(serialPortDeviceName).write(data);
               appendMessage("Data written successfully.");
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while writing to the serial port";
               LOG.error(msg, e);
               appendMessage(msg);
               }
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class WriteFileToSerialPortAction extends AbstractTimeConsumingAction
      {
      private final JTextComponent textComponent;

      private WriteFileToSerialPortAction(final JTextComponent textComponent)
         {
         super(DashboardContentPanel.this);
         this.textComponent = textComponent;
         }

      protected Object executeTimeConsumingAction()
         {
         if (qwerkControllerProvider.getQwerkController() != null)
            {
            final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
            if (serialIOService != null)
               {
               final File file = getTextComponentValueAsFile(textComponent);
               if (file != null)
                  {
                  if (file.exists())
                     {
                     try
                        {
                        final byte[] bytes = FileUtils.getFileAsBytes(file);
                        serialIOService.getSerialPort(serialPortDeviceName).write(bytes);
                        appendMessage("Data written successfully.");
                        }
                     catch (IOException e)
                        {
                        final String msg = "IOException while reading in the file";
                        LOG.error(msg, e);
                        appendMessage(msg);
                        }
                     catch (SerialIOException e)
                        {
                        final String msg = "SerialIOException while writing to the serial port";
                        LOG.error(msg, e);
                        appendMessage(msg);
                        }
                     }
                  else
                     {
                     final String msg = "File [" + file.getAbsolutePath() + "] does not exist!";
                     LOG.error(msg);
                     appendMessage(msg);
                     }
                  }
               else
                  {
                  final String msg = "File is null!";
                  LOG.error(msg);
                  appendMessage(msg);
                  }
               }
            else
               {
               appendMessage("SerialIOService is null!");
               }
            }
         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private abstract class AbstractReadFromSerialPortAction extends AbstractTimeConsumingAction
      {
      protected AbstractReadFromSerialPortAction(final Component component)
         {
         super(component);
         }

      protected final Object executeTimeConsumingAction()
         {
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               final byte[] bytes = performRead(serialIOService);
               if (bytes == null)
                  {
                  appendMessage("SerialIOService.read() returned null byte array!");
                  }
               else
                  {
                  appendMessage("Read " + bytes.length + " byte" + (bytes.length == 1 ? "" : "s"));
                  if (bytes.length > 0)
                     {
                     final StringBuffer s = new StringBuffer(bytes.length);
                     for (final byte b : bytes)
                        {
                        s.append((char)b);
                        }
                     appendMessageToInputArea(s.toString());
                     }
                  }
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while reading from the serial port";
               LOG.error(msg, e);
               appendMessage(msg);
               }
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }

      protected abstract byte[] performRead(final SerialIOService serialIOService) throws SerialIOException;
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class ReadFromSerialPortAction extends AbstractReadFromSerialPortAction
      {
      private final JFormattedTextField maxNumberOfBytesTextComponent;
      private int numBytesToRead;

      private ReadFromSerialPortAction(final JFormattedTextField maxNumberOfBytesTextComponent)
         {
         super(DashboardContentPanel.this);
         this.maxNumberOfBytesTextComponent = maxNumberOfBytesTextComponent;
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void executeGUIActionBefore()
         {
         numBytesToRead = ((Number)maxNumberOfBytesTextComponent.getValue()).intValue();
         }

      protected byte[] performRead(final SerialIOService serialIOService) throws SerialIOException
         {
         return serialIOService.getSerialPort(serialPortDeviceName).read(numBytesToRead);
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class ReadFromSerialPortUntilDelimiterAction extends AbstractReadFromSerialPortAction
      {
      private final JFormattedTextField maxNumberOfBytesTextComponent;
      private final JComboBox delimiterCharacterJComboBox;
      private int numBytesToRead;
      private ASCIICharacter delimiterCharacter;

      private ReadFromSerialPortUntilDelimiterAction(final JFormattedTextField maxNumberOfBytesTextComponent, final JComboBox delimiterCharacterJComboBox)
         {
         super(DashboardContentPanel.this);
         this.maxNumberOfBytesTextComponent = maxNumberOfBytesTextComponent;
         this.delimiterCharacterJComboBox = delimiterCharacterJComboBox;
         }

      protected void executeGUIActionBefore()
         {
         numBytesToRead = ((Number)maxNumberOfBytesTextComponent.getValue()).intValue();
         delimiterCharacter = (ASCIICharacter)delimiterCharacterJComboBox.getSelectedItem();
         }

      protected byte[] performRead(final SerialIOService serialIOService) throws SerialIOException
         {
         return serialIOService.getSerialPort(serialPortDeviceName).read(numBytesToRead, delimiterCharacter);
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class BlockingReadFromSerialPortAction extends AbstractReadFromSerialPortAction
      {
      private final JFormattedTextField maxNumberOfBytesTextComponent;
      private final JFormattedTextField timeoutTextComponent;
      private int numBytesToRead;
      private int timeoutMilliseconds;

      private BlockingReadFromSerialPortAction(final JFormattedTextField maxNumberOfBytesTextComponent, final JFormattedTextField timeoutTextComponent)
         {
         super(DashboardContentPanel.this);
         this.maxNumberOfBytesTextComponent = maxNumberOfBytesTextComponent;
         this.timeoutTextComponent = timeoutTextComponent;
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void executeGUIActionBefore()
         {
         numBytesToRead = ((Number)maxNumberOfBytesTextComponent.getValue()).intValue();
         timeoutMilliseconds = ((Number)timeoutTextComponent.getValue()).intValue();
         }

      protected byte[] performRead(final SerialIOService serialIOService) throws SerialIOException
         {
         return serialIOService.getSerialPort(serialPortDeviceName).read(numBytesToRead, timeoutMilliseconds);
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class BlockingReadFromSerialPortUntilDelimiterAction extends AbstractReadFromSerialPortAction
      {
      private final JFormattedTextField maxNumberOfBytesTextComponent;
      private final JComboBox delimiterCharacterJComboBox;
      private final JFormattedTextField timeoutTextComponent;
      private int numBytesToRead;
      private ASCIICharacter delimiterCharacter;
      private int timeoutMilliseconds;

      private BlockingReadFromSerialPortUntilDelimiterAction(final JFormattedTextField maxNumberOfBytesTextComponent, final JComboBox delimiterCharacterJComboBox, final JFormattedTextField timeoutTextComponent)
         {
         super(DashboardContentPanel.this);
         this.maxNumberOfBytesTextComponent = maxNumberOfBytesTextComponent;
         this.delimiterCharacterJComboBox = delimiterCharacterJComboBox;
         this.timeoutTextComponent = timeoutTextComponent;
         }

      protected void executeGUIActionBefore()
         {
         numBytesToRead = ((Number)maxNumberOfBytesTextComponent.getValue()).intValue();
         delimiterCharacter = (ASCIICharacter)delimiterCharacterJComboBox.getSelectedItem();
         timeoutMilliseconds = ((Number)timeoutTextComponent.getValue()).intValue();
         }

      protected byte[] performRead(final SerialIOService serialIOService) throws SerialIOException
         {
         return serialIOService.getSerialPort(serialPortDeviceName).read(numBytesToRead, delimiterCharacter, timeoutMilliseconds);
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class CloseSerialPortAction extends AbstractTimeConsumingAction
      {
      private CloseSerialPortAction()
         {
         super(DashboardContentPanel.this);
         }

      protected Object executeTimeConsumingAction()
         {
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            serialIOService.getSerialPort(serialPortDeviceName).close();
            appendMessage("Serial port closed.");
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class IsSerialPortOpenAction extends AbstractTimeConsumingAction
      {
      private IsSerialPortOpenAction()
         {
         super(DashboardContentPanel.this);
         }

      protected Object executeTimeConsumingAction()
         {
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            appendMessage("Serial port is open = [" + serialIOService.getSerialPort(serialPortDeviceName).isOpen() + "]");
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class IsDataAvailableAction extends AbstractTimeConsumingAction
      {
      private IsDataAvailableAction()
         {
         super(DashboardContentPanel.this);
         }

      protected Object executeTimeConsumingAction()
         {
         final SerialIOService serialIOService = qwerkControllerProvider.getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               appendMessage("Serial port has data available = [" + serialIOService.getSerialPort(serialPortDeviceName).isDataAvailable() + "]");
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while trying to determine whether data is available on the serial port.";
               LOG.error(msg, e);
               appendMessage(msg);
               }
            }
         else
            {
            appendMessage("SerialIOService is null!");
            }

         return null;
         }
      }

   private static boolean isTextComponentNonEmpty(final JTextComponent textField)
      {
      final String text1 = textField.getText();
      final String trimmedText1 = (text1 != null) ? text1.trim() : null;
      return (trimmedText1 != null) && (trimmedText1.length() > 0);
      }

   private static interface TextComponentValidator
      {
      boolean isValid(final JTextComponent textComponent);
      }

   private static final class EnableButtonIfTextFieldIsValidKeyAdapter extends KeyAdapter
      {
      private final JButton button;
      private final JTextComponent textComponent;
      private final TextComponentValidator validator;

      private EnableButtonIfTextFieldIsValidKeyAdapter(final JButton button,
                                                       final JTextComponent textComponent,
                                                       final TextComponentValidator validator)
         {
         this.button = button;
         this.textComponent = textComponent;
         this.validator = validator;
         }

      public void keyReleased(final KeyEvent keyEvent)
         {
         button.setEnabled(validator.isValid(textComponent));
         }
      }

   private static final class IntegerTextFieldPropertyChangeListener implements PropertyChangeListener
      {
      private final JFormattedTextField textField;

      private IntegerTextFieldPropertyChangeListener(final JFormattedTextField textField)
         {
         this.textField = textField;
         }

      public void propertyChange(final PropertyChangeEvent propertyChangeEvent)
         {
         textField.setValue(Math.abs(((Number)textField.getValue()).intValue()));
         }
      }
   }
