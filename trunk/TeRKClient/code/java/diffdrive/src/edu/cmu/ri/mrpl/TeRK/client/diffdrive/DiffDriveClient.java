package edu.cmu.ri.mrpl.TeRK.client.diffdrive;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import Ice.LocalException;
import Ice.UnknownLocalException;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.DirectConnectDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.RelayLoginFormDescriptor;
import edu.cmu.ri.mrpl.TeRK.ServoState;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.swing.ImageFormat;
import edu.cmu.ri.mrpl.swing.ImageUtils;
import edu.cmu.ri.mrpl.swing.SavePictureActionListener;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import edu.cmu.ri.mrpl.util.VersionNumberReader;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DiffDriveClient extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(DiffDriveClient.class);

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/diffdrive/DiffDriveClient.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/diffdrive/DiffDriveClient.relay.ice.properties";

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DiffDriveClient.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private static final String ACTION_TILT_UP = "tiltUp";
   private static final String ACTION_TILT_DOWN = "tiltDown";
   private static final String ACTION_PAN_LEFT = "panLeft";
   private static final String ACTION_PAN_RIGHT = "panRight";
   private static final String ACTION_DRIVE_FORWARD = "driveForward";
   private static final String ACTION_DRIVE_BACK = "driveBack";
   private static final String ACTION_SPIN_LEFT = "spinLeft";
   private static final String ACTION_SPIN_RIGHT = "spinRight";
   private static final String ACTION_STOP_SERVOS = "stopServos";
   private static final String ACTION_STOP_MOTORS = "stopMotors";

   private static final boolean[] SERVO_MASK = {true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false};

   private static final int SERVO_CENTER_PAN = Math.abs(Integer.valueOf(RESOURCES.getString("servo.center.pan")));
   private static final int SERVO_CENTER_TILT = Math.abs(Integer.valueOf(RESOURCES.getString("servo.center.tilt")));
   private static final int[] SERVO_CENTER_POSITIONS = {SERVO_CENTER_PAN, SERVO_CENTER_TILT, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   private static final int MOTOR_VELOCITY_SLOW = Math.abs(Integer.valueOf(RESOURCES.getString("motor.velocity.slow")));
   private static final int MOTOR_VELOCITY_FAST = Math.abs(Integer.valueOf(RESOURCES.getString("motor.velocity.fast")));
   private static final int MOTOR_VELOCITY_LIGHTNING = Math.abs(Integer.valueOf(RESOURCES.getString("motor.velocity.lightning")));
   private static final int SERVO_VELOCITY_SLOW = Math.abs(Integer.valueOf(RESOURCES.getString("servo.velocity.slow")));
   private static final int SERVO_VELOCITY_FAST = Math.abs(Integer.valueOf(RESOURCES.getString("servo.velocity.fast")));
   private static final int SERVO_VELOCITY_LIGHTNING = Math.abs(Integer.valueOf(RESOURCES.getString("servo.velocity.lightning")));

   private static final int[] SERVO_TILT_UP_SLOW = {0, SERVO_VELOCITY_SLOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_TILT_UP_FAST = {0, SERVO_VELOCITY_FAST, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_TILT_UP_LIGHTNING = {0, SERVO_VELOCITY_LIGHTNING, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_TILT_DOWN_SLOW = {0, -SERVO_VELOCITY_SLOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_TILT_DOWN_FAST = {0, -SERVO_VELOCITY_FAST, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_TILT_DOWN_LIGHTNING = {0, -SERVO_VELOCITY_LIGHTNING, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   private static final int[] SERVO_PAN_RIGHT_SLOW = {-SERVO_VELOCITY_SLOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_PAN_RIGHT_FAST = {-SERVO_VELOCITY_FAST, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_PAN_RIGHT_LIGHTNING = {-SERVO_VELOCITY_LIGHTNING, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_PAN_LEFT_SLOW = {SERVO_VELOCITY_SLOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_PAN_LEFT_FAST = {SERVO_VELOCITY_FAST, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_PAN_LEFT_LIGHTNING = {SERVO_VELOCITY_LIGHTNING, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   private static final int[] SERVO_CENTER_SLOW = {SERVO_VELOCITY_SLOW, SERVO_VELOCITY_SLOW, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_CENTER_FAST = {SERVO_VELOCITY_FAST, SERVO_VELOCITY_FAST, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
   private static final int[] SERVO_CENTER_LIGHTNING = {SERVO_VELOCITY_LIGHTNING, SERVO_VELOCITY_LIGHTNING, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

   private static final int[] MOTOR_FORWARD_SLOW = {-MOTOR_VELOCITY_SLOW, MOTOR_VELOCITY_SLOW, 0, 0};
   private static final int[] MOTOR_FORWARD_FAST = {-MOTOR_VELOCITY_FAST, MOTOR_VELOCITY_FAST, 0, 0};
   private static final int[] MOTOR_FORWARD_LIGHTNING = {-MOTOR_VELOCITY_LIGHTNING, MOTOR_VELOCITY_LIGHTNING, 0, 0};
   private static final int[] MOTOR_BACKWARD_SLOW = {MOTOR_VELOCITY_SLOW, -MOTOR_VELOCITY_SLOW, 0, 0};
   private static final int[] MOTOR_BACKWARD_FAST = {MOTOR_VELOCITY_FAST, -MOTOR_VELOCITY_FAST, 0, 0};
   private static final int[] MOTOR_BACKWARD_LIGHTNING = {MOTOR_VELOCITY_LIGHTNING, -MOTOR_VELOCITY_LIGHTNING, 0, 0};

   private static final int[] MOTOR_SPIN_RIGHT_SLOW = {-MOTOR_VELOCITY_SLOW, -MOTOR_VELOCITY_SLOW, 0, 0};
   private static final int[] MOTOR_SPIN_RIGHT_FAST = {-MOTOR_VELOCITY_FAST, -MOTOR_VELOCITY_FAST, 0, 0};
   private static final int[] MOTOR_SPIN_RIGHT_LIGHTNING = {-MOTOR_VELOCITY_LIGHTNING, -MOTOR_VELOCITY_LIGHTNING, 0, 0};
   private static final int[] MOTOR_SPIN_LEFT_SLOW = {MOTOR_VELOCITY_SLOW, MOTOR_VELOCITY_SLOW, 0, 0};
   private static final int[] MOTOR_SPIN_LEFT_FAST = {MOTOR_VELOCITY_FAST, MOTOR_VELOCITY_FAST, 0, 0};
   private static final int[] MOTOR_SPIN_LEFT_LIGHTNING = {MOTOR_VELOCITY_LIGHTNING, MOTOR_VELOCITY_LIGHTNING, 0, 0};

   private final JMenuBar menubar = new JMenuBar();

   // File menu items
   private final JMenuItem exitMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.exit.name"),
                                                                    RESOURCES.getString("menuitem.exit.mnemonic").charAt(0));

   // Edit menu items
   private final JMenuItem preferencesMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.preferences.name"),
                                                                           RESOURCES.getString("menuitem.preferences.mnemonic").charAt(0));

   // Robot menu items
   private final JMenuItem connectDirectlyMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.connect-directly.name"),
                                                                               RESOURCES.getString("menuitem.connect-directly.mnemonic").charAt(0),
                                                                               RESOURCES.getString("menuitem.connect-directly.accelerator").charAt(0));
   private final JMenuItem connectViaRelayMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.connect-via-relay.name"),
                                                                               RESOURCES.getString("menuitem.connect-via-relay.mnemonic").charAt(0),
                                                                               RESOURCES.getString("menuitem.connect-via-relay.accelerator").charAt(0));
   private final JMenuItem disconnectFromRobotMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.disconnect.name"),
                                                                                   RESOURCES.getString("menuitem.disconnect.mnemonic").charAt(0));

   // Help menu items
   private final JMenuItem aboutMenuItem = SwingUtils.createMenuItem(RESOURCES.getString("menuitem.about.name"),
                                                                     RESOURCES.getString("menuitem.about.mnemonic").charAt(0));

   private final ActionListener unimplementedActionListener =
         new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            JOptionPane.showMessageDialog(DiffDriveClient.this,
                                          RESOURCES.getString("dialog.unimplemented.message"),
                                          RESOURCES.getString("dialog.unimplemented.title"),
                                          JOptionPane.INFORMATION_MESSAGE);
            }
         };

   private int[] servoTiltUp;
   private int[] servoTiltDown;
   private int[] servoPanRight;
   private int[] servoPanLeft;
   private int[] servoCenter;
   private int[] motorForward;
   private int[] motorBackward;
   private int[] motorSpinRight;
   private int[] motorSpinLeft;

   private final JLabel controlLabel = new JLabel();
   private final JLabel panTiltControlLabel = new JLabel();

   private final JButton connectDisconnectButton = new JButton();
   private final JButton savePictureButton = new JButton();
   private final JButton pauseResumeVideoStreamButton = new JButton();

   private boolean isVideoStreamPaused;

   // radio buttons to control motor and servo speed
   private final JRadioButton fastButton = new JRadioButton(RESOURCES.getString("button.label.fast"));
   private final JRadioButton slowButton = new JRadioButton(RESOURCES.getString("button.label.slow"));
   private final JRadioButton lightningButton = new JRadioButton(RESOURCES.getString("button.label.lightning"));

   // Areas defining the four arrows of the motor controls and the pan tilt controls.
   private Area forwardArrow;
   private Area backArrow;
   private Area clockwiseArrow;
   private Area counterClockwiseArrow;
   private Area tiltUpArrow;
   private Area tiltDownArrow;
   private Area panRightArrow;
   private Area panLeftArrow;
   private Area centerCircle;

   // Image icons for the arrows.
   private final ImageIcon activeControls = ImageUtils.createImageIcon(RESOURCES.getString("images.activecontrols"));
   private final ImageIcon forwardPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.forwardclick"));
   private final ImageIcon backPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.backClick"));
   private final ImageIcon clockwisePressed = ImageUtils.createImageIcon(RESOURCES.getString("images.leftClick"));
   private final ImageIcon counterClockwisePressed = ImageUtils.createImageIcon(RESOURCES.getString("images.rightClick"));
   private final ImageIcon forwardHover = ImageUtils.createImageIcon(RESOURCES.getString("images.forwardHover"));
   private final ImageIcon backHover = ImageUtils.createImageIcon(RESOURCES.getString("images.backHover"));
   private final ImageIcon clockwiseHover = ImageUtils.createImageIcon(RESOURCES.getString("images.leftHover"));
   private final ImageIcon counterClockwiseHover = ImageUtils.createImageIcon(RESOURCES.getString("images.rightHover"));
   private final ImageIcon panTiltEnabled = ImageUtils.createImageIcon(RESOURCES.getString("images.panTiltEnabled"));
   private final ImageIcon tiltUpPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.tiltUpPressed"));
   private final ImageIcon tiltUpHover = ImageUtils.createImageIcon(RESOURCES.getString("images.tiltUpHover"));
   private final ImageIcon tiltDownPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.tiltDownPressed"));
   private final ImageIcon tiltDownHover = ImageUtils.createImageIcon(RESOURCES.getString("images.tiltDownHover"));
   private final ImageIcon panRightPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.panRightPressed"));
   private final ImageIcon panRightHover = ImageUtils.createImageIcon(RESOURCES.getString("images.panRightHover"));
   private final ImageIcon panLeftPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.panLeftPressed"));
   private final ImageIcon panLeftHover = ImageUtils.createImageIcon(RESOURCES.getString("images.panLeftHover"));
   private final ImageIcon centerPressed = ImageUtils.createImageIcon(RESOURCES.getString("images.centerPressed"));
   private final ImageIcon centerHover = ImageUtils.createImageIcon(RESOURCES.getString("images.centerHover"));

   // Regular border for the viewer area.
   private final LineBorder regularBorder = new LineBorder(new Color(0, 0, 0), 3);

   // Actions for robot operation.
   private Action tiltUp;
   private Action tiltDown;
   private Action panLeft;
   private Action panRight;
   private Action center;
   private Action driveForward;
   private Action driveBack;
   private Action spinLeft;//counterclockwise
   private Action spinRight;//clockwise
   private Action stopServos;
   private Action stopMotors;
   private Runnable currentAction = null;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new DiffDriveClient();
               }
            });
      }

   private DiffDriveClient()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
               {
               DiffDriveClient.this.setTitle(APPLICATION_NAME + " - Connected to " + qwerkUserId);
               getVideoStreamPlayer().startVideoStream();
               }

            public void executeBeforeDisconnectingFromQwerk()
               {
               setMenuBarEnabled(false);
               }

            public void executeAfterDisconnectingFromQwerk(final String qwerkUserId)
               {
               setMenuBarEnabled(true);
               DiffDriveClient.this.setTitle(APPLICATION_NAME);
               }

            public void toggleGUIElementState(final boolean isConnectedToQwerk)
               {
               connectDisconnectButton.setIcon(ImageUtils.createImageIcon(isConnectedToQwerk ? RESOURCES.getString("images.disconnectButton") : RESOURCES.getString("images.connectButton")));
               connectDisconnectButton.setPressedIcon(ImageUtils.createImageIcon(isConnectedToQwerk ? RESOURCES.getString("images.disconnectButton.mousedown") : RESOURCES.getString("images.connectButton.mousedown")));
               connectDisconnectButton.setRolloverIcon(ImageUtils.createImageIcon(isConnectedToQwerk ? RESOURCES.getString("images.disconnectButton.mouseover") : RESOURCES.getString("images.connectButton.mouseover")));
               setPauseResumeButtonImages(true);
               pauseResumeVideoStreamButton.setEnabled(isConnectedToQwerk);
               isVideoStreamPaused = !isConnectedToQwerk;
               controlLabel.setEnabled(isConnectedToQwerk);
               panTiltControlLabel.setEnabled(isConnectedToQwerk);
               setActionsEnabled(isConnectedToQwerk);
               savePictureButton.setEnabled(isConnectedToQwerk);
               fastButton.setEnabled(isConnectedToQwerk);
               slowButton.setEnabled(isConnectedToQwerk);
               lightningButton.setEnabled(isConnectedToQwerk);
               }
            });

      // add action listeners to menu items
      exitMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               performQuitAction();
               }
            });

      preferencesMenuItem.addActionListener(unimplementedActionListener);

      connectDirectlyMenuItem.setEnabled(true);
      connectDirectlyMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               performConnectDirectlyToRobotAction();
               }
            });

      connectViaRelayMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               performConnectToRobotViaRelayAction();
               }
            });

      disconnectFromRobotMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               DiffDriveClient.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               final SwingWorker worker =
                     new SwingWorker()
                     {
                     public Object construct()
                        {
                        performDisconnectFromRobotAction();
                        return null;
                        }

                     public void finished()
                        {
                        DiffDriveClient.this.setCursor(Cursor.getDefaultCursor());
                        }
                     };
               worker.start();
               }
            });

      aboutMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               performAboutAction();
               }
            });

      // assemble the File menu
      final JMenu fileMenu = new JMenu(RESOURCES.getString("menu.file.name"));
      fileMenu.setMnemonic(RESOURCES.getString("menu.file.mnemonic").charAt(0));
      fileMenu.add(exitMenuItem);

      // assemble the Edit menu
      final JMenu editMenu = new JMenu(RESOURCES.getString("menu.edit.name"));
      editMenu.setMnemonic(RESOURCES.getString("menu.edit.mnemonic").charAt(0));
      editMenu.add(preferencesMenuItem);

      // assemble the Robot menu
      final JMenu robotMenu = new JMenu(RESOURCES.getString("menu.robot.name"));
      robotMenu.setMnemonic(RESOURCES.getString("menu.robot.mnemonic").charAt(0));
      robotMenu.add(connectDirectlyMenuItem);
      robotMenu.add(connectViaRelayMenuItem);
      robotMenu.addSeparator();
      robotMenu.add(disconnectFromRobotMenuItem);

      // assemble the Help menu
      final JMenu helpMenu = new JMenu(RESOURCES.getString("menu.help.name"));
      helpMenu.setMnemonic(RESOURCES.getString("menu.help.mnemonic").charAt(0));
      helpMenu.add(aboutMenuItem);

      // assemble the menu bar
      setJMenuBar(menubar);
      menubar.add(fileMenu);
      menubar.add(editMenu);
      menubar.add(robotMenu);
      menubar.add(helpMenu);

      getMainContentPane().setBackground(Color.WHITE);
      getMainContentPane().setLayout(new BorderLayout(0, 0));
      getMainContentPane().setDoubleBuffered(true);

      getMainContentPane().setBorder(BorderFactory.createEmptyBorder());

      // initialize GUI components
      initComponents();
      getVideoStreamViewport().setBorder(regularBorder);
      defineAreas();

      // initialize actions and key bindings
      initActions();
      setActionsEnabled(false);
      initKeyBindings();

      pack();

      setLocationRelativeTo(null);// center the window on the screen

      setVisible(true);
      }

   private void setMenuBarEnabled(final boolean isEnabled)
      {
      menubar.setEnabled(isEnabled);
      for (int i = 0; i < menubar.getMenuCount(); i++)
         {
         final JMenu menu = menubar.getMenu(i);
         menu.setEnabled(isEnabled);
         }
      }

   private void performConnectToRobotViaRelayAction()
      {
      LOG.debug("DiffDriveClient.performConnectToRobotViaRelayAction()");
      SwingUtils.warnIfNotEventDispatchThread("performConnectToRobotViaRelayAction()");
      if (getRelayCommunicator() != null && getRelayCommunicator().isLoggedIn())
         {
         getConnectToRobotWizard().setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
         }
      else
         {
         getConnectToRobotWizard().setCurrentPanel(RelayLoginFormDescriptor.IDENTIFIER);
         }
      getConnectToRobotWizard().showModalDialog();
      }

   private void performConnectDirectlyToRobotAction()
      {
      LOG.debug("DiffDriveClient.performConnectDirectlyToRobotAction()");
      SwingUtils.warnIfNotEventDispatchThread("performConnectDirectlyToRobotAction()");
      getConnectToRobotWizard().setCurrentPanel(DirectConnectDescriptor.IDENTIFIER);
      getConnectToRobotWizard().showModalDialog();
      }

   private void performDisconnectFromRobotAction()
      {
      LOG.debug("DiffDriveClient.performDisconnectFromRobotAction()");
      SwingUtils.warnIfEventDispatchThread("performDisconnectFromRobotAction()");
      getConnectDisconnectButtonActionListener().actionPerformed(null);
      }

   private void performAboutAction()
      {
      LOG.debug("DiffDriveClient.performAboutAction()");
      SwingUtils.warnIfNotEventDispatchThread("performAboutAction()");
      final String message = MessageFormat.format(RESOURCES.getString("dialog.about.message"),
                                                  VersionNumberReader.getVersionNumber());
      final String title = RESOURCES.getString("dialog.about.title");

      JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
      }

   /** Enables or disables all of the Actions, but assumes it's executing in the Swing event dispatch thread. */
   private void setActionsEnabled(final boolean isEnabled)
      {
      tiltUp.setEnabled(isEnabled);
      tiltDown.setEnabled(isEnabled);
      panLeft.setEnabled(isEnabled);
      panRight.setEnabled(isEnabled);
      center.setEnabled(isEnabled);
      driveForward.setEnabled(isEnabled);
      driveBack.setEnabled(isEnabled);
      spinLeft.setEnabled(isEnabled);
      spinRight.setEnabled(isEnabled);
      stopServos.setEnabled(isEnabled);
      stopMotors.setEnabled(isEnabled);
      }

   private void setPauseResumeButtonImages(final boolean isPaused)
      {
      if (isPaused)
         {
         pauseResumeVideoStreamButton.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.pauseButton")));
         pauseResumeVideoStreamButton.setDisabledIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.pauseButton.disabled")));
         pauseResumeVideoStreamButton.setPressedIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.pauseButton.mousedown")));
         pauseResumeVideoStreamButton.setRolloverIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.pauseButton.mouseover")));
         }
      else
         {
         pauseResumeVideoStreamButton.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.resumeButton")));
         pauseResumeVideoStreamButton.setDisabledIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.resumeButton.disabled")));
         pauseResumeVideoStreamButton.setPressedIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.resumeButton.mousedown")));
         pauseResumeVideoStreamButton.setRolloverIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.resumeButton.mouseover")));
         }
      }

   private void initComponents()
      {
      GridBagConstraints gridBagConstraints;

      final JLabel titleLabel = new JLabel();
      final JLabel jLabel1 = new JLabel();
      final JLabel jLabel2 = new JLabel();
      final JLabel jLabel3 = new JLabel();
      final JLabel jLabel4 = new JLabel();
      final JLabel jLabel5 = new JLabel();
      final JLabel jLabel6 = new JLabel();
      final JLabel jLabel8 = new JLabel();
      final JLabel jLabel9 = new JLabel();
      final JLabel dividerLabel = new JLabel();
      final JPanel queuePanel = new JPanel();
      final JPanel openingPanel = new JPanel();
      final JPanel controlsPanel = new JPanel();

      setLayout(new GridBagLayout());
      setBackground(new Color(255, 255, 255));

      titleLabel.setFont(GUIConstants.FONT_LARGE);
      titleLabel.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.teleoptitle")));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 4;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      add(titleLabel, gridBagConstraints);

      jLabel1.setBackground(new Color(255, 255, 255));
      jLabel1.setFont(GUIConstants.FONT_SMALL);
      jLabel1.setText(RESOURCES.getString("instructions1"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.insets = new Insets(10, 14, 0, 0);
      add(jLabel1, gridBagConstraints);

      jLabel2.setBackground(new Color(255, 255, 255));
      jLabel2.setFont(GUIConstants.FONT_SMALL);
      jLabel2.setText(RESOURCES.getString("instructions2"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.insets = new Insets(0, 14, 0, 0);
      add(jLabel2, gridBagConstraints);

      jLabel3.setBackground(new Color(255, 255, 255));
      jLabel3.setFont(GUIConstants.FONT_SMALL);
      jLabel3.setText(RESOURCES.getString("instructions3"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.insets = new Insets(0, 14, 0, 0);
      add(jLabel3, gridBagConstraints);

      jLabel4.setFont(GUIConstants.FONT_SMALL);
      jLabel4.setText(RESOURCES.getString("instructions4"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.insets = new Insets(0, 14, 0, 0);
      add(jLabel4, gridBagConstraints);

      jLabel5.setFont(GUIConstants.FONT_SMALL);
      jLabel5.setText(RESOURCES.getString("instructions5"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 5;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.WEST;
      gridBagConstraints.insets = new Insets(0, 14, 0, 0);
      add(jLabel5, gridBagConstraints);

      jLabel6.setFont(GUIConstants.FONT_SMALL);
      jLabel6.setText(RESOURCES.getString("label.viewer"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 6;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.anchor = GridBagConstraints.SOUTHWEST;
      gridBagConstraints.insets = new Insets(0, 14, 0, 0);
      add(jLabel6, gridBagConstraints);

      dividerLabel.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.divider")));
      dividerLabel.setIconTextGap(0);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 6;
      gridBagConstraints.anchor = GridBagConstraints.NORTH;
      add(dividerLabel, gridBagConstraints);

      getVideoStreamViewport().setBorder(new LineBorder(new Color(242, 146, 0), 3));
      getVideoStreamViewport().setPreferredSize(new Dimension(320, 240));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      add(getVideoStreamViewportComponent(), gridBagConstraints);

      savePictureButton.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.savebutton")));
      savePictureButton.setDisabledIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.savebutton.disabled")));
      savePictureButton.setPressedIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.savebutton.mousedown")));
      savePictureButton.setRolloverIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.savebutton.mouseover")));
      savePictureButton.setOpaque(false);
      savePictureButton.setBorder(null);
      savePictureButton.setBorderPainted(false);
      savePictureButton.setIconTextGap(0);
      savePictureButton.setEnabled(false);
      final SavePictureActionListener SPAL = new SavePictureActionListener(this, getVideoStreamViewport().getComponent(), ImageFormat.JPEG);
      savePictureButton.addActionListener(
            new ActionListener()
            {

            public void actionPerformed(final ActionEvent evt)
               {
               final SwingWorker worker =
                     new SwingWorker()
                     {
                     public Object construct()
                        {
                        if (isVideoStreamPaused)
                           {
                           SPAL.actionPerformed(evt);
                           }
                        else
                           {
                           getVideoStreamPlayer().pauseVideoStream();
                           SPAL.actionPerformed(evt);
                           getVideoStreamPlayer().resumeVideoStream();
                           }
                        return null;
                        }

                     public void finished()
                        {
                        }
                     };
               worker.start();
               }
            });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.weightx = 0.5;
      gridBagConstraints.insets = new Insets(5, 5, 15, 5);
      add(savePictureButton, gridBagConstraints);

      queuePanel.setLayout(new CardLayout());

      queuePanel.setBackground(new Color(255, 255, 255));
      openingPanel.setLayout(new GridBagLayout());

      openingPanel.setBackground(new Color(255, 255, 255));

      connectDisconnectButton.setIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.connectButton")));
      connectDisconnectButton.setPressedIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.connectButton.mousedown")));
      connectDisconnectButton.setRolloverIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.connectButton.mouseover")));
      connectDisconnectButton.setOpaque(false);
      connectDisconnectButton.setBorder(null);
      connectDisconnectButton.setBorderPainted(false);
      connectDisconnectButton.setIconTextGap(0);
      connectDisconnectButton.setEnabled(true);
      connectDisconnectButton.addActionListener(getConnectDisconnectButtonActionListener());

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      openingPanel.add(connectDisconnectButton, gridBagConstraints);

      queuePanel.add(openingPanel, "openingPanel");

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridheight = 6;
      gridBagConstraints.anchor = GridBagConstraints.NORTH;
      gridBagConstraints.insets = new Insets(10, 0, 0, 18);
      add(queuePanel, gridBagConstraints);

      controlsPanel.setLayout(new GridBagLayout());

      controlsPanel.setBackground(new Color(255, 255, 255));
      jLabel8.setBackground(new Color(255, 255, 255));
      jLabel8.setFont(GUIConstants.FONT_MEDIUM);
      jLabel8.setText(RESOURCES.getString("label.robot-controls"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      controlsPanel.add(jLabel8, gridBagConstraints);

      controlLabel.setIcon(activeControls);
      controlLabel.setDisabledIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.disabledcontrols")));
      controlLabel.setEnabled(false);
      controlLabel.setIconTextGap(0);
      controlLabel.addMouseMotionListener(
            new MouseMotionAdapter()
            {
            public void mouseMoved(final MouseEvent evt)
               {
               controlLabelMouseMoved(evt);
               }
            });
      controlLabel.addMouseListener(
            new MouseAdapter()
            {
            public void mousePressed(final MouseEvent evt)
               {
               controlLabelMousePressed(evt);
               }

            public void mouseReleased(final MouseEvent evt)
               {
               controlLabelMouseReleased(evt);
               }
            });

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(5, 5, 5, 5);
      controlsPanel.add(controlLabel, gridBagConstraints);

      jLabel9.setBackground(new Color(255, 255, 255));
      jLabel9.setFont(GUIConstants.FONT_MEDIUM);
      jLabel9.setText(RESOURCES.getString("label.panTilt-controls"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      controlsPanel.add(jLabel9, gridBagConstraints);

      panTiltControlLabel.setIcon(panTiltEnabled);
      panTiltControlLabel.setDisabledIcon(ImageUtils.createImageIcon(RESOURCES.getString("images.panTiltDisabled")));
      panTiltControlLabel.setEnabled(false);
      panTiltControlLabel.setIconTextGap(0);
      panTiltControlLabel.addMouseMotionListener(
            new MouseMotionAdapter()
            {
            public void mouseMoved(final MouseEvent evt)
               {
               panTiltControlLabelMouseMoved(evt);
               }
            });
      panTiltControlLabel.addMouseListener(
            new MouseAdapter()
            {
            public void mousePressed(final MouseEvent evt)
               {
               panTiltControlLabelMousePressed(evt);
               }

            public void mouseReleased(final MouseEvent evt)
               {
               panTiltControlLabelMouseReleased(evt);
               }
            });

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = gridBagConstraints.NORTH;
      gridBagConstraints.insets = new Insets(25, 5, 5, 5);
      controlsPanel.add(panTiltControlLabel, gridBagConstraints);

      final ButtonGroup speedControlButtonGroup = new ButtonGroup();
      speedControlButtonGroup.add(slowButton);
      speedControlButtonGroup.add(fastButton);
      speedControlButtonGroup.add(lightningButton);
      final JPanel speedControlPanel = new JPanel();
      speedControlPanel.setLayout(new BoxLayout(speedControlPanel, BoxLayout.X_AXIS));
      speedControlPanel.add(slowButton);
      speedControlPanel.add(fastButton);
      speedControlPanel.add(lightningButton);

      slowButton.setEnabled(false);
      fastButton.setEnabled(false);
      lightningButton.setEnabled(false);
      speedControlPanel.setBackground(Color.WHITE);
      slowButton.setBackground(Color.WHITE);
      fastButton.setBackground(Color.WHITE);
      lightningButton.setBackground(Color.WHITE);

      final JRadioButton[] speedselection = {slowButton, fastButton, lightningButton};
      final ActionListener[] buttonselection = new ActionListener[3];
      buttonselection[0] =
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               servoTiltUp = SERVO_TILT_UP_SLOW;
               servoTiltDown = SERVO_TILT_DOWN_SLOW;
               servoPanRight = SERVO_PAN_RIGHT_SLOW;
               servoPanLeft = SERVO_PAN_LEFT_SLOW;
               servoCenter = SERVO_CENTER_SLOW;
               motorForward = MOTOR_FORWARD_SLOW;
               motorBackward = MOTOR_BACKWARD_SLOW;
               motorSpinRight = MOTOR_SPIN_RIGHT_SLOW;
               motorSpinLeft = MOTOR_SPIN_LEFT_SLOW;
               }
            };
      buttonselection[1] =
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               servoTiltUp = SERVO_TILT_UP_FAST;
               servoTiltDown = SERVO_TILT_DOWN_FAST;
               servoPanRight = SERVO_PAN_RIGHT_FAST;
               servoPanLeft = SERVO_PAN_LEFT_FAST;
               servoCenter = SERVO_CENTER_FAST;
               motorForward = MOTOR_FORWARD_FAST;
               motorBackward = MOTOR_BACKWARD_FAST;
               motorSpinRight = MOTOR_SPIN_RIGHT_FAST;
               motorSpinLeft = MOTOR_SPIN_LEFT_FAST;
               }
            };

      buttonselection[2] =
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               servoTiltUp = SERVO_TILT_UP_LIGHTNING;
               servoTiltDown = SERVO_TILT_DOWN_LIGHTNING;
               servoPanRight = SERVO_PAN_RIGHT_LIGHTNING;
               servoPanLeft = SERVO_PAN_LEFT_LIGHTNING;
               servoCenter = SERVO_CENTER_LIGHTNING;
               motorForward = MOTOR_FORWARD_LIGHTNING;
               motorBackward = MOTOR_BACKWARD_LIGHTNING;
               motorSpinRight = MOTOR_SPIN_RIGHT_LIGHTNING;
               motorSpinLeft = MOTOR_SPIN_LEFT_LIGHTNING;
               }
            };

      //install handlers
      for (int i = 0; i < speedselection.length; i++)
         {
         speedselection[i].addActionListener(buttonselection[i]);
         }

      //TODO: get which button to select by default as a preference
      final int chosen = 0;
      speedselection[chosen].setSelected(true);
      buttonselection[chosen].actionPerformed(null);//initialize speeds

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.gridwidth = 2;
      gridBagConstraints.insets = new Insets(5, 50, 5, 5);
      controlsPanel.add(speedControlPanel, gridBagConstraints);

      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 7;
      gridBagConstraints.insets = new Insets(0, 0, 0, 18);
      add(controlsPanel, gridBagConstraints);

      setPauseResumeButtonImages(true);
      pauseResumeVideoStreamButton.setOpaque(false);//required for Mac
      pauseResumeVideoStreamButton.setBorder(null);
      pauseResumeVideoStreamButton.setBorderPainted(false);
      pauseResumeVideoStreamButton.setIconTextGap(0);
      pauseResumeVideoStreamButton.setEnabled(false);
      pauseResumeVideoStreamButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent evt)
               {
               setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               setPauseResumeButtonImages(isVideoStreamPaused);

               final SwingWorker worker =
                     new SwingWorker()
                     {
                     public Object construct()
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

                     public void finished()
                        {
                        isVideoStreamPaused = !isVideoStreamPaused;
                        setCursor(Cursor.getDefaultCursor());
                        }
                     };
               worker.start();
               }
            });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 8;
      gridBagConstraints.weightx = 0.5;
      gridBagConstraints.insets = new Insets(5, 5, 15, 5);
      add(pauseResumeVideoStreamButton, gridBagConstraints);
      }

   /** Defines the areas for the four control arrows and the pan tilt controls. */
   private void defineAreas()
      {
      //forwardArrow
      final int[] xpoints1 = {94, 135, 114, 114, 75, 54, 94};
      final int[] ypoints1 = {16, 54, 54, 96, 96, 54, 16};
      Polygon p = new Polygon(xpoints1, ypoints1, 7);

      forwardArrow = new Area(p);

      //backArrow
      final int[] xpoints2 = {75, 114, 114, 135, 94, 54, 75};
      final int[] ypoints2 = {109, 109, 137, 137, 161, 137, 137, 109};
      p = new Polygon(xpoints2, ypoints2, 7);

      backArrow = new Area(p);

      //counterClockwiseArrow
      Arc2D.Double arc1 = new Arc2D.Double(5.0, 0.0, 180.0, 180.0, 297.0, 122.0, Arc2D.PIE);
      Arc2D.Double arc2 = new Arc2D.Double(4.0, 8.0, 170.0, 164.0, 297.0, 122.0, Arc2D.PIE);
      final int[] xpoints3 = {117, 147, 132, 117};
      final int[] ypoints3 = {5, 5, 30, 5};
      p = new Polygon(xpoints3, ypoints3, 3);
      final int[] xpoints4 = {61, 54, 55, 61};
      final int[] ypoints4 = {162, 169, 161, 162};
      Polygon p2 = new Polygon(xpoints4, ypoints4, 3);

      Area a1 = new Area(arc1);
      Area a2 = new Area(arc2);
      Area a3 = new Area(p);
      Area a4 = new Area(p2);

      counterClockwiseArrow = new Area();
      counterClockwiseArrow.add(a1);
      counterClockwiseArrow.subtract(a2);
      counterClockwiseArrow.add(a3);
      counterClockwiseArrow.add(a4);

      //clockwiseArrow
      arc1 = new Arc2D.Double(5.0, 0.0, 180.0, 180.0, 120.0, 122.0, Arc2D.PIE);
      arc2 = new Arc2D.Double(14.0, 8.0, 180.0, 164.0, 120.0, 122.0, Arc2D.PIE);
      final int[] xpoints5 = {41, 71, 56, 41};
      final int[] ypoints5 = {5, 5, 30, 5};
      p = new Polygon(xpoints5, ypoints5, 3);
      final int[] xpoints6 = {127, 133, 134, 127};
      final int[] ypoints6 = {162, 169, 160, 162};
      p2 = new Polygon(xpoints6, ypoints6, 3);

      a1 = new Area(arc1);
      a2 = new Area(arc2);
      a3 = new Area(p);
      a4 = new Area(p2);

      clockwiseArrow = new Area();
      clockwiseArrow.add(a1);
      clockwiseArrow.subtract(a2);
      clockwiseArrow.add(a3);
      counterClockwiseArrow.add(a4);

      //tiltUpArrow
      final int[] xpoints7 = {26, 34, 29, 29, 23, 23, 18};
      final int[] ypoints7 = {0, 8, 8, 19, 19, 8, 8};
      p = new Polygon(xpoints7, ypoints7, 7);

      tiltUpArrow = new Area(p);

      //tiltDownArrow
      final int[] xpoints8 = {26, 17, 23, 23, 29, 29, 43};
      final int[] ypoints8 = {52, 44, 44, 33, 33, 44, 44};
      p = new Polygon(xpoints8, ypoints8, 7);

      tiltDownArrow = new Area(p);

      //panRightArrow
      final int[] xpoints9 = {52, 44, 44, 33, 33, 44, 44};
      final int[] ypoints9 = {26, 34, 29, 29, 23, 23, 18};
      p = new Polygon(xpoints9, ypoints9, 7);

      panRightArrow = new Area(p);

      //panLeftArrow
      final int[] xpoints10 = {0, 8, 9, 19, 19, 8, 8};
      final int[] ypoints10 = {26, 18, 23, 23, 29, 29, 34};
      p = new Polygon(xpoints10, ypoints10, 7);

      panLeftArrow = new Area(p);

      //centerCircle
      Ellipse2D e = new Ellipse2D.Double(22.0, 22.0, 8.0, 8.0);
      centerCircle = new Area(e);
      }

   /** Initializes the Actions used by this GUI (drive forward, pan, tilt, stop, etc.). */
   private void initActions()
      {
      tiltUp =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               panTiltControlLabel.setIcon(tiltUpPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("tilt up");
               getQwerkController().getServoService().setVelocities(servoTiltUp);
               }
            };

      tiltDown =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               panTiltControlLabel.setIcon(tiltDownPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("tilt down");
               getQwerkController().getServoService().setVelocities(servoTiltDown);
               }
            };

      panLeft =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               panTiltControlLabel.setIcon(panLeftPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("pan left");
               getQwerkController().getServoService().setVelocities(servoPanLeft);
               }
            };

      panRight =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               panTiltControlLabel.setIcon(panRightPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("pan right");
               getQwerkController().getServoService().setVelocities(servoPanRight);
               }
            };

      center =
            new NoRepeatTimeConsumingAction()
            {
            ServoState servoState;

            void executeGUIActionBefore()
               {
               panTiltControlLabel.setIcon(centerPressed);
               //disable the pan tilt controls
               panTiltControlLabel.setEnabled(false);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("center");
               servoState = getQwerkController().getQwerkState().servo;
               while (servoState.servoPositions[0] != SERVO_CENTER_PAN ||
                      servoState.servoPositions[1] != SERVO_CENTER_TILT)
                  {
                  getQwerkController().getServoService().setPositionsWithSpeeds(SERVO_MASK,
                                                                                SERVO_CENTER_POSITIONS,
                                                                                servoCenter);
                  servoState = getQwerkController().getQwerkState().servo;
                  }
               }

            void executeGUIActionAfter()
               {
               panTiltControlLabel.setIcon(panTiltEnabled);
               //enable the pan tilt controls
               panTiltControlLabel.setEnabled(true);
               }
            };

      driveForward =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(forwardPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("drive forward");
               getQwerkController().getMotorService().setMotorVelocities(motorForward);
               }
            };

      driveBack =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(backPressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("drive back");
               getQwerkController().getMotorService().setMotorVelocities(motorBackward);
               }
            };

      spinLeft =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(counterClockwisePressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("spin left");
               getQwerkController().getMotorService().setMotorVelocities(motorSpinLeft);
               }
            };

      spinRight =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(clockwisePressed);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("spin right");
               getQwerkController().getMotorService().setMotorVelocities(motorSpinRight);
               }
            };

      stopServos =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(activeControls);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("stop servos");
               getQwerkController().getServoService().stopServos();
               }
            };

      stopMotors =
            new NoRepeatTimeConsumingAction()
            {
            void executeGUIActionBefore()
               {
               controlLabel.setIcon(activeControls);
               }

            public void executeTimeConsumingAction()
               {
               LOG.trace("stop motors");
               getQwerkController().getMotorService().stopMotors();
               }
            };
      }

   /**
    * Binds keyboard inputs to specific Actions so that keyboard shortcuts can be used to operate the robot.
    *
    * The arrow keys are used to control driving and spinning in place. Shift plus the arrow keys are used to control
    * pan and tilt.
    */
   private void initKeyBindings()
      {
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift UP"), ACTION_TILT_UP);
      getMainContentPane().getActionMap().put(ACTION_TILT_UP, tiltUp);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift DOWN"), ACTION_TILT_DOWN);
      getMainContentPane().getActionMap().put(ACTION_TILT_DOWN, tiltDown);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift LEFT"), ACTION_PAN_LEFT);
      getMainContentPane().getActionMap().put(ACTION_PAN_LEFT, panLeft);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift RIGHT"), ACTION_PAN_RIGHT);
      getMainContentPane().getActionMap().put(ACTION_PAN_RIGHT, panRight);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), ACTION_DRIVE_FORWARD);
      getMainContentPane().getActionMap().put(ACTION_DRIVE_FORWARD, driveForward);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), ACTION_DRIVE_BACK);
      getMainContentPane().getActionMap().put(ACTION_DRIVE_BACK, driveBack);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("LEFT"), ACTION_SPIN_LEFT);
      getMainContentPane().getActionMap().put(ACTION_SPIN_LEFT, spinLeft);

      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("RIGHT"), ACTION_SPIN_RIGHT);
      getMainContentPane().getActionMap().put(ACTION_SPIN_RIGHT, spinRight);

      //stop when keys are released
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift released UP"), ACTION_STOP_SERVOS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift released DOWN"), ACTION_STOP_SERVOS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift released LEFT"), ACTION_STOP_SERVOS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("shift released RIGHT"), ACTION_STOP_SERVOS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released UP"), ACTION_STOP_MOTORS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released DOWN"), ACTION_STOP_MOTORS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), ACTION_STOP_MOTORS);
      getMainContentPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), ACTION_STOP_MOTORS);
      getMainContentPane().getActionMap().put(ACTION_STOP_SERVOS, stopServos);
      getMainContentPane().getActionMap().put(ACTION_STOP_MOTORS, stopMotors);
      }

   /** Changes the control image based on the location of the mouse event. */
   private void controlLabelMouseMoved(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("controlLabelMouseMoved()");

      //check that controls are active
      if (!controlLabel.isEnabled())
         {
         return;
         }

      //get mouse event location
      final Point p;
      if (evt != null)
         {
         p = evt.getPoint();
         }
      else
         {
         //fake point at 0,0 in case the method is called with a null mouse event
         p = new Point(0, 0);
         }

      //determine which arrow is under the pointer and change the image accordingly
      if (forwardArrow.contains(p))
         {
         controlLabel.setIcon(forwardHover);
         }
      else if (backArrow.contains(p))
         {
         controlLabel.setIcon(backHover);
         }
      else if (counterClockwiseArrow.contains(p))
         {
         controlLabel.setIcon(counterClockwiseHover);
         }
      else if (clockwiseArrow.contains(p))
         {
         controlLabel.setIcon(clockwiseHover);
         }
      else
         {
         controlLabel.setIcon(activeControls);
         }
      }

   /** Changes the pan tilt control image based on the location of the mouse event. */
   private void panTiltControlLabelMouseMoved(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("panTiltControlLabelMouseMoved()");

      //check that controls are active
      if (!panTiltControlLabel.isEnabled())
         {
         return;
         }

      //get mouse event location
      final Point p;
      if (evt != null)
         {
         p = evt.getPoint();
         }
      else
         {
         //fake point at 0,0 in case the method is called with a null mouse event
         p = new Point(0, 0);
         }

      //determine which arrow is under the pointer and change the image accordingly
      if (tiltUpArrow.contains(p))
         {
         panTiltControlLabel.setIcon(tiltUpHover);
         }
      else if (tiltDownArrow.contains(p))
         {
         panTiltControlLabel.setIcon(tiltDownHover);
         }
      else if (panRightArrow.contains(p))
         {
         panTiltControlLabel.setIcon(panRightHover);
         }
      else if (panLeftArrow.contains(p))
         {
         panTiltControlLabel.setIcon(panLeftHover);
         }
      else if (centerCircle.contains(p))
         {
         panTiltControlLabel.setIcon(centerHover);
         }
      else
         {
         panTiltControlLabel.setIcon(panTiltEnabled);
         }
      }

   /** Changes the control image and moves the robot based on the location of the mouse event. */
   private void controlLabelMousePressed(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("controlLabelMousePressed()");

      //check that controls are active
      if (!controlLabel.isEnabled())
         {
         return;
         }

      //get mouse event location
      final Point p = evt.getPoint();

      //determine which arrow was pressed, set the control image, and do the approriate Action
      if (forwardArrow.contains(p))
         {
         controlLabel.setIcon(forwardPressed);
         driveForward.actionPerformed(null);
         }
      else if (backArrow.contains(p))
         {
         controlLabel.setIcon(backPressed);
         driveBack.actionPerformed(null);
         }
      else if (counterClockwiseArrow.contains(p))
         {
         controlLabel.setIcon(counterClockwisePressed);
         spinLeft.actionPerformed(null);
         }
      else if (clockwiseArrow.contains(p))
         {
         controlLabel.setIcon(clockwisePressed);
         spinRight.actionPerformed(null);
         }
      }

   /** Changes the pan tilt control image and moves the robot based on the location of the mouse event. */
   private void panTiltControlLabelMousePressed(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("controlLabelMousePressed()");

      //check that controls are active
      if (!panTiltControlLabel.isEnabled())
         {
         return;
         }

      //get mouse event location
      final Point p = evt.getPoint();

      //determine which arrow was pressed, set the control image, and do the approriate Action
      if (tiltUpArrow.contains(p))
         {
         panTiltControlLabel.setIcon(tiltUpPressed);
         tiltUp.actionPerformed(null);
         }
      else if (tiltDownArrow.contains(p))
         {
         panTiltControlLabel.setIcon(tiltDownPressed);
         tiltDown.actionPerformed(null);
         }
      else if (panRightArrow.contains(p))
         {
         panTiltControlLabel.setIcon(panRightPressed);
         panRight.actionPerformed(null);
         }
      else if (panLeftArrow.contains(p))
         {
         panTiltControlLabel.setIcon(panLeftPressed);
         panLeft.actionPerformed(null);
         }
      else if (centerCircle.contains(p))
         {
         panTiltControlLabel.setIcon(centerPressed);
         center.actionPerformed(null);
         }
      }

   /** Stops moving the robot. */
   private void controlLabelMouseReleased(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("controlLabelMousePressed()");

      //check that controls are active
      if (!controlLabel.isEnabled())
         {
         return;
         }

      //stop moving
      stopMotors.actionPerformed(null);

      //set the appropriate control arrows image
      controlLabelMouseMoved(evt);
      }

   /** Stops moving the robot and sets the appropriate pan tilt control image. */
   private void panTiltControlLabelMouseReleased(final MouseEvent evt)
      {
      SwingUtils.warnIfNotEventDispatchThread("panTiltControlLabelMouseReleased()");

      //check that controls are active
      if (!panTiltControlLabel.isEnabled())
         {
         return;
         }

      //stop moving
      stopServos.actionPerformed(null);

      //set the appropriate control arrows image
      panTiltControlLabelMouseMoved(evt);
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private abstract class NoRepeatTimeConsumingAction extends AbstractAction
      {
      private final Runnable action = new Runnable()
      {
      public void run()
         {
         if (!this.equals(currentAction))
            {
            currentAction = this;
            executeGUIActionBefore();
            try
               {
               // execute the time-consuming action in a SwingWorker, so it doesn't bog down the GUI thread
               final SwingWorker swingWorker =
                     new SwingWorker()
                     {
                     public Object construct()
                        {
                        try
                           {
                           executeTimeConsumingAction();
                           }
                        catch (UnknownLocalException e)
                           {
                           // todo: do something better
                           LOG.error("UnknownLocalException while performing executeTimeConsumingAction()", e);
                           }
                        return null;
                        }

                     public void finished()
                        {
                        executeGUIActionAfter();
                        }
                     };
               swingWorker.start();
               }
            catch (LocalException e)
               {
               LOG.error("LocalException caught in DiffDriveClient$NoRepeatTimeConsumingAction.actionPerformed()", e);
               throw e;
               }
            }
         }
      };

      public final void actionPerformed(final ActionEvent event)
         {
         SwingUtilities.invokeLater(action);
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      void executeGUIActionBefore()
         {
         // do nothing by default
         }

      abstract void executeTimeConsumingAction();

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      void executeGUIActionAfter()
         {
         // do nothing by default
         }
      }
   }
