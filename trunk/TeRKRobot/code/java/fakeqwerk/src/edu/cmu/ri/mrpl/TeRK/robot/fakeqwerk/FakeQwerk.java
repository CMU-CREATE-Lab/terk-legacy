package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import Ice.ObjectPrx;
import Ice.SyscallException;
import edu.cmu.ri.mrpl.TeRK.AnalogInControllerPrx;
import edu.cmu.ri.mrpl.TeRK.AnalogInControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrx;
import edu.cmu.ri.mrpl.TeRK.AudioControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.DigitalInControllerPrx;
import edu.cmu.ri.mrpl.TeRK.DigitalInControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.DigitalOutControllerPrx;
import edu.cmu.ri.mrpl.TeRK.DigitalOutControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.LEDControllerPrx;
import edu.cmu.ri.mrpl.TeRK.LEDControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.MotorControllerPrx;
import edu.cmu.ri.mrpl.TeRK.MotorControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.QwerkPrx;
import edu.cmu.ri.mrpl.TeRK.QwerkPrxHelper;
import edu.cmu.ri.mrpl.TeRK.ServoControllerPrx;
import edu.cmu.ri.mrpl.TeRK.ServoControllerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.VideoStreamerServerPrx;
import edu.cmu.ri.mrpl.TeRK.VideoStreamerServerPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.peer._UserConnectionEventHandlerOperationsNC;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

public class FakeQwerk extends JFrame implements MessageEventListener, QwerkEventListener, _UserConnectionEventHandlerOperationsNC
   {
   private static final Logger LOG = Logger.getLogger(FakeQwerk.class);
   private static final String APPLICATION_NAME = "Fake Qwerk";
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/robot/fakeqwerk/FakeQwerk.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/robot/fakeqwerk/FakeQwerk.relay.ice.properties";
   private static final String DIRECT_CONNECT_OBJECT_ADAPTER_NAME = "Robot.Server";
   private static final String RELAY_OBJECT_ADAPTER_NAME = "Robot.Client";
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");
   private static final Dimension SPACER = new Dimension(5, 5);
   private static final char BULLET_CHARACTER = '\u2022';
   private static final CommandAndControllerMapper COMMAND_AND_CONTROLLER_MAPPER = DefaultCommandAndControllerMapper.getInstance();

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(FakeQwerk.class.getName());
   protected final JTextArea messageTextArea = new JTextArea(16, 50);
   private final JTextField userIdTextField = new JTextField(10);
   private final JPasswordField passwordTextField = new JPasswordField(10);
   private final JButton loginLogoutButton = new JButton();
   private final KeyAdapter loginFieldsKeyListener = new KeyAdapter()
   {
   public void keyReleased(final KeyEvent e)
      {
      enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
      }
   };
   private final ActionListener loginLogoutAction = new LoginLogoutActionListener();

   private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss,SSS: ");
   private final JCheckBox directConnectionsCheckbox = new JCheckBox(RESOURCES.getString("label.direct-connections"));
   private final JCheckBox relayConnectionsCheckbox = new JCheckBox(RESOURCES.getString("label.relay-connections"));
   private final Runnable directConnectionsCheckboxEnabler = new SetWidgetEnabledRunnable(directConnectionsCheckbox, true);
   private final Runnable directConnectionsCheckboxDisabler = new SetWidgetEnabledRunnable(directConnectionsCheckbox, false);
   private final Runnable relayConnectionsCheckboxEnabler = new SetWidgetEnabledRunnable(relayConnectionsCheckbox, true);
   private final Runnable relayConnectionsCheckboxDisabler = new SetWidgetEnabledRunnable(relayConnectionsCheckbox, false);

   private RelayCommunicator relayCommunicator;
   private DirectConnectCommunicator directConnectCommunicator;
   private FakeQwerkServant qwerkServantForRelay;
   private FakeQwerkServant qwerkServantForDirectConnect;
   private final AbstractAction shutdownDirectConnectCommunicatorAction = new ShutdownDirectConnectCommunicatorAction(this);
   private final AbstractAction shutdownRelayCommunicatorAction = new ShutdownRelayCommunicatorAction(this);

   protected JPanel rootPanel;
   protected JScrollPane messageTextAreaScrollPane;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new FakeQwerk();
               }
            });
      }

   protected FakeQwerk()
      {
      super(RESOURCES.getString("application.name"));

      directConnectionsCheckbox.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               if (directConnectionsCheckbox.isSelected())
                  {
                  final Collection<TerkCommunicatorCreationEventListener> listeners = new HashSet<TerkCommunicatorCreationEventListener>();
                  listeners.add(new MyDirectConnectCommunicatorCreationEventAdapater());

                  DirectConnectCommunicator.createAsynchronously(APPLICATION_NAME,
                                                                 ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                 DIRECT_CONNECT_OBJECT_ADAPTER_NAME,
                                                                 listeners,
                                                                 new ServantFactory()
                                                                 {
                                                                 public edu.cmu.ri.mrpl.TeRK.servants.Servants createServants(final TerkCommunicator terkCommunicator)
                                                                    {
                                                                    LOG.debug("FakeQwerk.createServants() is simply returning null.");
                                                                    return null;
                                                                    }
                                                                 });
                  }
               else
                  {
                  shutdownDirectConnectCommunicatorAction.actionPerformed(e);
                  }
               }
            });

      relayConnectionsCheckbox.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               if (relayConnectionsCheckbox.isSelected())
                  {
                  RelayCommunicator.createAsynchronously(APPLICATION_NAME,
                                                         ICE_RELAY_PROPERTIES_FILE,
                                                         RELAY_OBJECT_ADAPTER_NAME,
                                                         new MyRelayCommunicatorCreationEventAdapater());
                  }
               else
                  {
                  shutdownRelayCommunicatorAction.actionPerformed(e);
                  }
               }
            });

      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      setBackground(Color.WHITE);
      setResizable(false);
      this.addWindowListener(
            new WindowAdapter()
            {
            public void windowClosing(final WindowEvent event)
               {
               // ask if the user really wants to exit
               final int selectedOption = JOptionPane.showConfirmDialog(FakeQwerk.this,
                                                                        RESOURCES.getString("dialog.message.exit-confirmation"),
                                                                        RESOURCES.getString("dialog.title.exit-confirmation"),
                                                                        JOptionPane.YES_NO_OPTION,
                                                                        JOptionPane.QUESTION_MESSAGE);

               if (selectedOption == JOptionPane.YES_OPTION)
                  {
                  final SwingWorker worker = new SwingWorker()
                  {
                  public Object construct()
                     {
                     doLogout();
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

      messageTextArea.setLineWrap(true);
      messageTextArea.setWrapStyleWord(true);
      messageTextArea.setEditable(false);
      messageTextAreaScrollPane = new JScrollPane(messageTextArea,
                                                  JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                  JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

      rootPanel = new JPanel();
      rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
      rootPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      userIdTextField.addKeyListener(loginFieldsKeyListener);
      userIdTextField.setEnabled(false);
      passwordTextField.addKeyListener(loginFieldsKeyListener);
      passwordTextField.setEchoChar(BULLET_CHARACTER);
      passwordTextField.setEnabled(false);

      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
               }
            });

      loginLogoutButton.setText(RESOURCES.getString("button.login"));
      loginLogoutButton.addActionListener(loginLogoutAction);
      loginLogoutButton.setOpaque(false);//required for Mac

      userIdTextField.addActionListener(loginLogoutAction);//pressing <ENTER> will cause login when textfields have focus
      passwordTextField.addActionListener(loginLogoutAction);

      final JLabel userIdLabel = new JLabel(RESOURCES.getString("label.userId"));
      final JLabel passwordLabel = new JLabel(RESOURCES.getString("label.password"));

      final JPanel loginFormPanel = new JPanel(new SpringLayout());
      loginFormPanel.add(userIdLabel);
      loginFormPanel.add(userIdTextField);
      loginFormPanel.add(passwordLabel);
      loginFormPanel.add(passwordTextField);
      SpringLayoutUtilities.makeCompactGrid(loginFormPanel,
                                            2, 2, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final JPanel loginPanel = new JPanel();
      loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));

      loginPanel.add(loginFormPanel);
      loginPanel.add(Box.createRigidArea(SPACER));
      loginPanel.add(loginLogoutButton);

      final JPanel communicatorChoicePanel = new JPanel();
      communicatorChoicePanel.setLayout(new BoxLayout(communicatorChoicePanel, BoxLayout.Y_AXIS));
      communicatorChoicePanel.add(directConnectionsCheckbox);
      communicatorChoicePanel.add(relayConnectionsCheckbox);

      final JPanel communicatorPanel = new JPanel();
      communicatorPanel.setLayout(new BoxLayout(communicatorPanel, BoxLayout.X_AXIS));
      communicatorPanel.add(communicatorChoicePanel);
      communicatorPanel.add(Box.createGlue());

      rootPanel.add(communicatorPanel);
      rootPanel.add(loginPanel);
      rootPanel.add(messageTextAreaScrollPane);

      add(rootPanel);

      pack();

      // limit the text area's size (must do this AFTER the call to pack())
      final Dimension messageTextAreaScrollPaneDimensions = new Dimension(messageTextArea.getWidth(), messageTextArea.getHeight());
      messageTextAreaScrollPane.setPreferredSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMinimumSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMaximumSize(new Dimension(10000, messageTextArea.getHeight()));

      // re-pack everything
      pack();

      setLocationRelativeTo(null);// center the window on the screen

      setVisible(true);
      }

   private void doLogin()
      {
      // fetch the user id and password from the GUI
      final String[] userIdAndPassword = new String[2];
      try
         {
         SwingUtilities.invokeAndWait(new Runnable()
         {
         public void run()
            {
            userIdAndPassword[0] = userIdTextField.getText();
            userIdAndPassword[1] = (passwordTextField.getPassword() == null) ? "" : new String(passwordTextField.getPassword());
            }
         });
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while getting the user id and password from the form fields", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while getting the user id and password from the form fields", e);
         }

      // do the login
      final boolean loginWasSucccessful = relayCommunicator.login(userIdAndPassword[0], userIdAndPassword[1]);

      if (loginWasSucccessful)
         {
         // create and register the servants
         try
            {
            // create the servants
            final Servants servants = createServants(relayCommunicator);

            qwerkServantForRelay = servants.getMainServant();

            // register the main servant proxy and the command controller servant proxies with the relay
            relayCommunicator.registerCallbacks(servants.getMainServantProxy(), servants.getMainServantProxy());
            relayCommunicator.registerProxies(servants.getSecondaryServantProxies());
            }
         catch (RegistrationException e)
            {
            LOG.error("RegistrationException while trying to register the servants", e);
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to create and register the servants", e);
            }

         // all done!
         LOG.info("Login successful!");
         }
      else
         {
         LOG.info("Login failed!");
         showLoginFailedMessageInGUIThread();
         }
      }

   private Servants createServants(final TerkCommunicator terkCommunicator)
      {
      // create the main servant
      final FakeQwerkServant mainServant = new FakeQwerkServant(this);
      final AnalogInControllerServant analogInControllerServant = new AnalogInControllerServant();
      final AudioControllerServant audioControllerServant = new AudioControllerServant();
      final DigitalInControllerServant digitalInControllerServant = new DigitalInControllerServant();
      final DigitalOutControllerServant digitalOutControllerServant = new DigitalOutControllerServant();
      final LEDControllerServant ledControllerServant = new LEDControllerServant();
      final MotorControllerServant motorControllerServant = new MotorControllerServant();
      final ServoControllerServant servoControllerServant = new ServoControllerServant();
      final VideoStreamControllerServant videoStreamControllerServant = new VideoStreamControllerServant(mainServant);

      mainServant.addMessageEventListener(this);
      mainServant.addQwerkEventListener(this);

      // create the main servant proxy
      final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
      final QwerkPrx mainQwerkServantPrx = QwerkPrxHelper.uncheckedCast(mainServantProxy);

      // create secondary servants and their proxies
      final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>();

      analogInControllerServant.addMessageEventListener(this);
      analogInControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedAnalogInControllerServantProxy = terkCommunicator.createServantProxy(analogInControllerServant);
      final AnalogInControllerPrx analogInControllerServantProxy = AnalogInControllerPrxHelper.uncheckedCast(untypedAnalogInControllerServantProxy);
      secondaryServantProxies.add(analogInControllerServantProxy);

      audioControllerServant.addMessageEventListener(this);
      audioControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedAudioControllerServantProxy = terkCommunicator.createServantProxy(audioControllerServant);
      final AudioControllerPrx audioControllerServantProxy = AudioControllerPrxHelper.uncheckedCast(untypedAudioControllerServantProxy);
      secondaryServantProxies.add(audioControllerServantProxy);

      digitalInControllerServant.addMessageEventListener(this);
      digitalInControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedDigitalInControllerServantProxy = terkCommunicator.createServantProxy(digitalInControllerServant);
      final DigitalInControllerPrx digitalInControllerServantProxy = DigitalInControllerPrxHelper.uncheckedCast(untypedDigitalInControllerServantProxy);
      secondaryServantProxies.add(digitalInControllerServantProxy);

      digitalOutControllerServant.addMessageEventListener(this);
      digitalOutControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedDigitalOutControllerServantProxy = terkCommunicator.createServantProxy(digitalOutControllerServant);
      final DigitalOutControllerPrx digitalOutControllerServantProxy = DigitalOutControllerPrxHelper.uncheckedCast(untypedDigitalOutControllerServantProxy);
      secondaryServantProxies.add(digitalOutControllerServantProxy);

      ledControllerServant.addMessageEventListener(this);
      ledControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedLEDControllerServantProxy = terkCommunicator.createServantProxy(ledControllerServant);
      final LEDControllerPrx ledControllerServantProxy = LEDControllerPrxHelper.uncheckedCast(untypedLEDControllerServantProxy);
      secondaryServantProxies.add(ledControllerServantProxy);

      motorControllerServant.addMessageEventListener(this);
      motorControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedMotorControllerServantProxy = terkCommunicator.createServantProxy(motorControllerServant);
      final MotorControllerPrx motorControllerServantProxy = MotorControllerPrxHelper.uncheckedCast(untypedMotorControllerServantProxy);
      secondaryServantProxies.add(motorControllerServantProxy);

      servoControllerServant.addMessageEventListener(this);
      servoControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedServoControllerServantProxy = terkCommunicator.createServantProxy(servoControllerServant);
      final ServoControllerPrx servoControllerServantProxy = ServoControllerPrxHelper.uncheckedCast(untypedServoControllerServantProxy);
      secondaryServantProxies.add(servoControllerServantProxy);

      videoStreamControllerServant.addMessageEventListener(this);
      videoStreamControllerServant.addQwerkEventListener(this);
      final ObjectPrx untypedVideoStreamControllerServantProxy = terkCommunicator.createServantProxy(videoStreamControllerServant);
      final VideoStreamerServerPrx videoStreamControllerServantProxy = VideoStreamerServerPrxHelper.uncheckedCast(untypedVideoStreamControllerServantProxy);
      secondaryServantProxies.add(videoStreamControllerServantProxy);

      // register secondary servants with the main servant
      mainServant.configureCommandControllerToHandleCommandType(analogInControllerServant, analogInControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(audioControllerServant, audioControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(digitalInControllerServant, digitalInControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(digitalOutControllerServant, digitalOutControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(ledControllerServant, ledControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(motorControllerServant, motorControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(servoControllerServant, servoControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);
      mainServant.configureCommandControllerToHandleCommandType(videoStreamControllerServant, videoStreamControllerServantProxy.ice_getIdentity(), COMMAND_AND_CONTROLLER_MAPPER);

      return new Servants(mainServant, mainQwerkServantPrx, secondaryServantProxies);
      }

   private void showLoginFailedMessageInGUIThread()
      {
      try
         {
         SwingUtilities.invokeAndWait(new Runnable()
         {
         public void run()
            {
            JOptionPane.showMessageDialog(FakeQwerk.this,
                                          RESOURCES.getString("dialog.message.login-failed"),
                                          RESOURCES.getString("dialog.title.login-failed"),
                                          JOptionPane.INFORMATION_MESSAGE);
            passwordTextField.requestFocusInWindow();
            passwordTextField.selectAll();
            }
         });
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while displaying the login failed message", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while displaying the login failed message", e);
         }
      }

   public void forcedLogoutNotification()
      {
      final SwingWorker worker = new SwingWorker()
      {
      public Object construct()
         {
         doLogout();
         return null;
         }

      public void finished()
         {
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         JOptionPane.showMessageDialog(FakeQwerk.this,
                                       RESOURCES.getString("dialog.message.logout-forced"),
                                       RESOURCES.getString("dialog.title.logout-forced"),
                                       JOptionPane.INFORMATION_MESSAGE);
         }
      };
      worker.start();
      }

   void doLogout()
      {
      if (qwerkServantForRelay != null)
         {
         qwerkServantForRelay.stopVideoStreamer();
         }

      if (relayCommunicator != null)
         {
         relayCommunicator.logout();
         LOG.info("Logout successful!");
         }
      else
         {
         LOG.debug("The RelayCommunicator is null, so I won't try to logout.");
         }
      }

   private void enableLoginLogoutButtonIfLoginFieldsAreNotEmpty()
      {
      loginLogoutButton.setEnabled((relayCommunicator != null) && areLoginFieldsNonEmpty());
      }

   private boolean areLoginFieldsNonEmpty()
      {
      return isUserIdFieldNonEmpty() && isPasswordFieldNonEmpty();
      }

   private boolean isUserIdFieldNonEmpty()
      {
      final String text1 = userIdTextField.getText();
      final String trimmedText1 = (text1 != null) ? text1.trim() : null;
      return (trimmedText1 != null) && (trimmedText1.length() > 0);
      }

   private boolean isPasswordFieldNonEmpty()
      {
      final String text2 = (passwordTextField.getPassword() == null) ? "" : new String(passwordTextField.getPassword());
      final String trimmedText2 = text2.trim();
      return (trimmedText2 != null) && (trimmedText2.length() > 0);
      }

   private void toggleRelayLoginFormWidgetsAccordingToLoginStatus()
      {
      final boolean isRelayCommunicatorRunning = relayCommunicator != null;
      final boolean isLoggedIn = isRelayCommunicatorRunning && relayCommunicator.isLoggedIn();
      setCursor(Cursor.getDefaultCursor());
      userIdTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      passwordTextField.setEnabled(isRelayCommunicatorRunning && !isLoggedIn);
      loginLogoutButton.setText(isLoggedIn ? RESOURCES.getString("button.logout") : RESOURCES.getString("button.login"));
      enableLoginLogoutButtonIfLoginFieldsAreNotEmpty();
      }

   public void handleQwerkEvent(final Object command)
      {

      }

   public void handleMessageEvent(final String message)
      {
      SwingUtilities.invokeLater(new Runnable()
      {
      public void run()
         {
         messageTextArea.append(dateFormatter.format(new Date()) + message + LINE_SEPARATOR);
         messageTextArea.setCaretPosition(messageTextArea.getDocument().getLength());
         }
      });
      }

   private class LoginLogoutActionListener implements ActionListener
      {
      public void actionPerformed(final ActionEvent e)
         {
         if (areLoginFieldsNonEmpty())
            {
            loginLogoutButton.setEnabled(false);
            userIdTextField.setEnabled(false);
            passwordTextField.setEnabled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (relayCommunicator.isLoggedIn())
               {
               new DisconnectWorker().start();
               }
            else
               {
               new ConnectWorker().start();
               }
            }
         }
      }

   private abstract class ConnectDisconnectWorker extends SwingWorker
      {
      public final Object construct()
         {
         doTimeConsumingAction();
         return null;
         }

      protected abstract void doTimeConsumingAction();

      public final void finished()
         {
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         doFinishingGUIAction();
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void doFinishingGUIAction()
         {
         // do nothing by default
         }
      }

   private final class ConnectWorker extends ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         doLogin();
         }
      }

   private final class DisconnectWorker extends ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         doLogout();
         }
      }

   private final class MyDirectConnectCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void beforeConstruction()
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  directConnectionsCheckboxDisabler.run();
                  FakeQwerk.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  }
               });
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         FakeQwerk.this.directConnectCommunicator = (DirectConnectCommunicator)terkCommunicator;

         // create the servants
         try
            {
            final Servants servants = createServants(directConnectCommunicator);

            qwerkServantForDirectConnect = servants.getMainServant();

            enable();
            }
         catch (SyscallException e)
            {
            LOG.error("Ice.SyscallException while trying to create the servants for direct-connect", e);

            // inform the user of the problem
            afterFailedConstruction(RESOURCES.getString("dialog.message.direct-connect-communicator-creation-failed-due-to-syscallexception"),
                                    RESOURCES.getString("dialog.title.direct-connect-communicator-creation-failed"));
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to create the servants for direct-connect", e);

            // inform the user of the problem
            afterFailedConstruction();
            }
         }

      public void afterFailedConstruction()
         {
         afterFailedConstruction(RESOURCES.getString("dialog.message.direct-connect-communicator-creation-failed"),
                                 RESOURCES.getString("dialog.title.direct-connect-communicator-creation-failed"));
         }

      private void afterFailedConstruction(final String message, final String title)
         {
         // Make sure the direct-connect communicator is shutdown.  It might not be if the user tried to create it
         // previously, but it failed for some reason during the processing of afterSuccessfulConstruction() (this can
         // happen if there's already another instance running on the same machine and port).
         if (FakeQwerk.this.directConnectCommunicator != null)
            {
            shutdownDirectConnectCommunicatorAction.actionPerformed(null);
            }

         JOptionPane.showMessageDialog(FakeQwerk.this, message, title, JOptionPane.INFORMATION_MESSAGE);
         directConnectionsCheckbox.setSelected(false);
         enable();
         }

      private void enable()
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            enableWorkhorse();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     enableWorkhorse();
                     }
                  });
            }
         }

      private void enableWorkhorse()
         {
         directConnectionsCheckboxEnabler.run();
         FakeQwerk.this.setCursor(Cursor.getDefaultCursor());
         }
      }

   private final class MyRelayCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void beforeConstruction()
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  relayConnectionsCheckboxDisabler.run();
                  FakeQwerk.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  }
               }
         );
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         FakeQwerk.this.relayCommunicator = (RelayCommunicator)terkCommunicator;
         enable();
         }

      public void afterFailedConstruction()
         {
         FakeQwerk.this.relayCommunicator = null;

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  JOptionPane.showMessageDialog(FakeQwerk.this,
                                                RESOURCES.getString("dialog.message.relay-communicator-creation-failed"),
                                                RESOURCES.getString("dialog.title.relay-communicator-creation-failed"),
                                                JOptionPane.INFORMATION_MESSAGE);
                  relayConnectionsCheckbox.setSelected(false);
                  enable();
                  }
               }
         );
         }

      private void enable()
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            enableWorkhorse();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     enableWorkhorse();
                     }
                  });
            }
         }

      private void enableWorkhorse()
         {
         FakeQwerk.this.setCursor(Cursor.getDefaultCursor());
         relayConnectionsCheckboxEnabler.run();
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         }
      }

   private final class SetWidgetEnabledRunnable implements Runnable
      {
      private final Component component;
      private final boolean isEnabled;

      private SetWidgetEnabledRunnable(final Component component, final boolean enabled)
         {
         this.component = component;
         isEnabled = enabled;
         }

      public void run()
         {
         component.setEnabled(isEnabled);
         }
      }

   private final class ShutdownDirectConnectCommunicatorAction extends AbstractTimeConsumingAction
      {
      private ShutdownDirectConnectCommunicatorAction(final Component component)
         {
         super(component);
         }

      protected void executeGUIActionBefore()
         {
         directConnectionsCheckboxDisabler.run();
         }

      protected Object executeTimeConsumingAction()
         {
         if (directConnectCommunicator != null)
            {
            LOG.debug("Shutting down the DirectConnectCommunicator...");
            qwerkServantForDirectConnect.stopVideoStreamer();
            directConnectCommunicator.shutdown();
            directConnectCommunicator = null;
            qwerkServantForDirectConnect = null;
            LOG.debug("Done shutting down the DirectConnectCommunicator!");
            }
         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         directConnectionsCheckboxEnabler.run();
         }
      }

   private final class ShutdownRelayCommunicatorAction extends AbstractTimeConsumingAction
      {
      private ShutdownRelayCommunicatorAction(final Component component)
         {
         super(component);
         }

      protected void executeGUIActionBefore()
         {
         relayConnectionsCheckboxDisabler.run();
         }

      protected Object executeTimeConsumingAction()
         {
         if (relayCommunicator != null)
            {
            doLogout();
            LOG.debug("Shutting down the RelayCommunicator...");
            relayCommunicator.shutdown();
            relayCommunicator = null;
            qwerkServantForRelay = null;
            LOG.debug("Done shutting down the RelayCommunicator!");
            }
         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         toggleRelayLoginFormWidgetsAccordingToLoginStatus();
         relayConnectionsCheckboxEnabler.run();
         }
      }
   }
