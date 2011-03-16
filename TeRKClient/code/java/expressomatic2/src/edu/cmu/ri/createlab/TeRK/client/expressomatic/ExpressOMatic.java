package edu.cmu.ri.createlab.TeRK.client.expressomatic;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.PropertyResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Ice.ObjectPrx;
import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.ConditionFileHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional.COMPARE_OPERATOR;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional.LOGICAL_OPERATOR;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.conditionals.AnalogInputsConditional;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.expressions.ExpressionFileHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence.Sequence;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence.SequenceFileHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence.SequencePlayer;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence.SequenceTransition;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.SwingConstants;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.dnd.ConditionTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.dnd.ExpressionTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.dnd.StepTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.list.AbstractListCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.list.ConditionCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.list.ExpressionCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.swing.list.StepCellRenderer;
import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.UserAlertingRelayEventFailureListener;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.ConnectDisconnectButton;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.DirectConnectDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerChooserDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.PeerConnectionMethodDescriptor;
import edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard.RelayLoginFormDescriptor;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.connectionstate.ConnectionStatePanel;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.UserConnectionEventAdapter;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.DragAndDropJList;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ExpressOMatic implements SwingConstants
   {
   private static final Logger LOG = Logger.getLogger(ExpressOMatic.class);
   // **** Default Objects ****

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ExpressOMatic.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/expressomatic/Express-O-Matic.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/expressomatic/Express-O-Matic.relay.ice.properties";
   private static final String ICE_OBJECT_ADAPTER_NAME = "Terk.User";
   private JPanel sequencePanel;
   private static final String XML_EXTENSION = ".xml";
   private static final FilenameFilter XML_FILENAME_FILTER =
         new FilenameFilter()
         {
         public boolean accept(final File dir, final String name)
            {
            return name != null && name.toLowerCase().endsWith(XML_EXTENSION);
            }
         };

   // **** ExpressOMatic Objects ****

   public static final int AVERAGE_LIGHT_CONDITION_1 = 0;
   public static final int BRIGHT_LIGHT_CONDITION_1 = 1;
   public static final int DARK_LIGHT_CONDITION_1 = 2;
   public static final int IR_NEARBY_CONDITION_1 = 3;
   public static final int IR_VERY_NEARBY_CONDITION_1 = 4;
   public static final int IR_NONE_CONDITION_1 = 5;
   public static final int AVERAGE_LIGHT_CONDITION_2 = 6;
   public static final int BRIGHT_LIGHT_CONDITION_2 = 7;
   public static final int DARK_LIGHT_CONDITION_2 = 8;
   public static final int IR_NEARBY_CONDITION_2 = 9;
   public static final int IR_VERY_NEARBY_CONDITION_2 = 10;
   public static final int IR_NONE_CONDITION_2 = 11;

   private final String expressionsPath;
   private final String expressionIconsPath;
   private final String conditionsPath;
   private final String conditionIconsPath;
   private final String sequencesPath;

   private final File conditionIconsDirectory;
   private final File sequencesDirectory;

   private File sequenceFile;

   private final SequenceFileHandler sequenceFileHandler;

   private final ListModel conditionListModel;
   private final ListModel expressionListModel;
   //private final ListModel sequenceListModel;

   private Sequence sequence;
   private final int[] conditionArray;

   private final JFrame jFrame = new JFrame(APPLICATION_NAME);
   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final Wizard connectToRobotWizard;
   private final ConnectionStatePanel connectionStatePanel = new ConnectionStatePanel();
   private final ConnectDisconnectButton connectDisconnectButton;
   private final MyPeerConnectionEventListener peerConnectionEventListener = new MyPeerConnectionEventListener();
   private final MyUserConnectionEventListener userConnectionEventListener = new MyUserConnectionEventListener();

   private final TerkServiceFactory terkServiceFactory = new TerkServiceFactory();
   private boolean isConnectedToPeer = false;
   private ServiceManager serviceManager = null;
   private TerkCommunicator terkCommunicator = null;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new ExpressOMatic();
               }
            });
      }

   public ExpressOMatic()
      {
      this(new int[]{BRIGHT_LIGHT_CONDITION_1, AVERAGE_LIGHT_CONDITION_1,
                     DARK_LIGHT_CONDITION_1, IR_VERY_NEARBY_CONDITION_1,
                     IR_NEARBY_CONDITION_1, IR_NONE_CONDITION_1,
                     BRIGHT_LIGHT_CONDITION_2, AVERAGE_LIGHT_CONDITION_2,
                     DARK_LIGHT_CONDITION_2, IR_VERY_NEARBY_CONDITION_2,
                     IR_NEARBY_CONDITION_2, IR_NONE_CONDITION_2});
      }

   protected ExpressOMatic(final int[] conditionArray)
      {
      // COMMUNICATIONS ------------------------------------------------------------------------------------------------

      // create the ServantFactory instances
      final ServantFactory directConnectServantFactory = new ExpressOMaticServantFactory();
      final ServantFactory relayServantFactory = new ExpressOMaticServantFactory();

      //change connected to qwerk to hummingbird
      connectionStatePanel.setPeerConnectionStateLabelText("Connected to Hummingbird:");

      // create the direct-connect manager
      directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(APPLICATION_NAME,
                                                                                  ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                                  directConnectServantFactory);

      // create the relay manager
      relayCommunicatorManager = new RelayCommunicatorManagerImpl(APPLICATION_NAME,
                                                                  ICE_RELAY_PROPERTIES_FILE,
                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                  relayServantFactory);

      // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
      // when various direct-connect-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(jFrame);
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

      // register a listener with the RelayCommunicatorManager which causes the user to be notified with an alert
      // when various relay-related failures occur.
      final TerkCommunicatorCreationEventListener userAlertingRelayEventFailureListener = new UserAlertingRelayEventFailureListener(jFrame);
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingRelayEventFailureListener);

      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(relayCommunicatorManager));
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener(directConnectCommunicatorManager));

      // CONNECTION WIZARD ---------------------------------------------------------------------------------------------

      // Create the connection-to-robot wizard
      connectToRobotWizard = new Wizard(jFrame);
      connectToRobotWizard.getDialog().setTitle(RESOURCES.getString("peer-connection-wizard.title"));

      // create the various pages in the wizard
      final WizardPanelDescriptor wizardDescriptorPeerConnectionMethod = new PeerConnectionMethodDescriptor();
      final DirectConnectDescriptor wizardDescriptorDirectConnect = new DirectConnectDescriptor(directConnectCommunicatorManager);
      final RelayLoginFormDescriptor wizardDescriptorRelayLoginForm = new RelayLoginFormDescriptor(relayCommunicatorManager);
      final PeerChooserDescriptor wizardDescriptorPeerChooser = new PeerChooserDescriptor(relayCommunicatorManager);

      // register the pages
      connectToRobotWizard.registerWizardPanel(PeerConnectionMethodDescriptor.IDENTIFIER, wizardDescriptorPeerConnectionMethod);
      connectToRobotWizard.registerWizardPanel(DirectConnectDescriptor.IDENTIFIER, wizardDescriptorDirectConnect);
      connectToRobotWizard.registerWizardPanel(RelayLoginFormDescriptor.IDENTIFIER, wizardDescriptorRelayLoginForm);
      connectToRobotWizard.registerWizardPanel(PeerChooserDescriptor.IDENTIFIER, wizardDescriptorPeerChooser);

      // ---------------------------------------------------------------------------------------------------------------

      connectDisconnectButton = new ConnectDisconnectButton();
      connectDisconnectButton.addActionListener(new ConnectDisconnectActionListener(jFrame));

      this.conditionArray = conditionArray;

      final String applicationHomePath = TerkConstants.FilePaths.TERK_PATH + APPLICATION_NAME + File.separator;
      expressionsPath = TerkConstants.FilePaths.TERK_PATH + "Expressions" + File.separator;
      expressionIconsPath = expressionsPath + "Icons" + File.separator;
      conditionsPath = applicationHomePath + "Conditions" + File.separator;
      conditionIconsPath = conditionsPath + "Icons" + File.separator;
      sequencesPath = applicationHomePath + "Sequences" + File.separator;

      final File homeDirectory = new File(TerkConstants.FilePaths.TERK_PATH);
      final File applicationHomeDirectory = new File(applicationHomePath);
      final File expressionsDirectory = new File(expressionsPath);
      final File expressionIconsDirectory = new File(expressionIconsPath);
      final File conditionsDirectory = new File(conditionsPath);
      conditionIconsDirectory = new File(conditionIconsPath);
      sequencesDirectory = new File(sequencesPath);

      if (!homeDirectory.exists())
         {
         homeDirectory.mkdirs();
         }

      if (!applicationHomeDirectory.exists())
         {
         applicationHomeDirectory.mkdirs();
         }

      if (!expressionsDirectory.exists())
         {
         expressionsDirectory.mkdirs();
         }

      if (!expressionIconsDirectory.exists())
         {
         expressionIconsDirectory.mkdirs();
         }

      if (!conditionsDirectory.exists())
         {
         conditionsDirectory.mkdirs();
         }

      if (!conditionIconsDirectory.exists())
         {
         conditionIconsDirectory.mkdirs();
         }

      if (!sequencesDirectory.exists())
         {
         sequencesDirectory.mkdirs();
         }

      conditionListModel =
            new DirectoryPollingListModel(conditionsDirectory, ConditionFileHandler.getInstance())
            {
            protected void performAfterRefresh()
               {
               AbstractListCellRenderer.loadConditionImages(conditionsPath);
               }
            };
      expressionListModel =
            new DirectoryPollingListModel(expressionsDirectory, ExpressionFileHandler.getInstance())
            {
            protected void performAfterRefresh()
               {
               AbstractListCellRenderer.loadExpressionImages(expressionsPath);
               }
            };

      sequenceFileHandler = SequenceFileHandler.getInstance();

      sequenceFile = null;

      sequence = new Sequence("Untitled");

      testConditions();

      buildGUI();
      }

   public JPanel getMainPanel()
      {
      return this.sequencePanel;
      }

   public void connectToPeer(final String peer) throws DuplicateConnectionException, PeerConnectionFailedException
      {
      directConnectCommunicatorManager.getDirectConnectCommunicator().connectToPeer(peer);
      }

   public void disconnectFromPeers()
      {
      if (terkCommunicator != null)
         {
         terkCommunicator.disconnectFromPeers();
         }
      }

   private void toggleGUIElementState(final boolean isEnabled)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         toggleGUIElementStateWorkhorse(isEnabled);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  toggleGUIElementStateWorkhorse(isEnabled);
                  }
               }
         );
         }
      }

   private void toggleGUIElementStateWorkhorse(final boolean isEnabled)
      {
      sequencePlayButton.setEnabled(isEnabled);

      if (!isEnabled && sequencePlayer.isPlaying())
         {
         for (final ActionListener a : sequencePlayButton.getActionListeners())
            {
            a.actionPerformed(new ActionEvent(sequencePlayButton, 0, ""));
            }
         }
      }

   @SuppressWarnings({"unchecked"})
   private abstract static class DirectoryPollingListModel extends AbstractListModel
      {
      private final File directory;
      private final AbstractFileHandler fileHandler;
      private final TreeList items = new TreeList();

      protected DirectoryPollingListModel(final File directory, final AbstractFileHandler fileHandler)
         {
         this.directory = directory;
         this.fileHandler = fileHandler;

         // start the directory-polling timer
         final Timer pollingTimer = new Timer("DirectoryPollingTimer_" + directory.getAbsolutePath(), true);
         pollingTimer.scheduleAtFixedRate(
               new TimerTask()
               {
               public void run()
                  {
                  checkDirectoryForFiles();
                  }
               },
               0,
               1000);
         }

      private void checkDirectoryForFiles()
         {
         final TreeList newItems = new TreeList();
         if (directory.isDirectory() && directory.exists())
            {
            final File[] xmlFiles = directory.listFiles(XML_FILENAME_FILTER);
            if ((xmlFiles != null) && (xmlFiles.length > 0))
               {
               LOG.trace("Found [" + xmlFiles.length + "] files in [" + directory.getAbsolutePath() + "]");
               for (final File file : xmlFiles)
                  {
                  try
                     {
                     final Object item = fileHandler.openFile(file);
                     if (item != null)
                        {
                        newItems.add(item);
                        }
                     }
                  catch (Exception e)
                     {
                     LOG.trace("Directory poller ignoring file [" + file.getAbsolutePath() + "] because openFile() threw an exception");
                     }
                  }
               }
            else
               {
               LOG.trace("Found 0 files in [" + directory.getAbsolutePath() + "]");
               }
            }
         else
            {
            LOG.trace("Directory [" + directory.getAbsolutePath() + "] isn't a directory or doesn't exist!");
            }

         synchronized (items)
            {
            items.clear();
            items.addAll(newItems);
            }

         performAfterRefresh();

         fireContentsChanged(this, 0, items.size());
         }

      public final int getSize()
         {
         return items.size();
         }

      public final Object getElementAt(final int index)
         {
         return items.get(index);
         }

      @SuppressWarnings({"NoopMethodInAbstractClass"})
      protected void performAfterRefresh()
         {
         // do nothing
         }
      }

   // HELPFUL METHODS USED TO EXTEND EXPRESS-O-MATIC =========================================

   /**
    * This method gets called when an Expression has been selected from the list
    * @param expression - the Expression that was selected
    */
   protected void expressionSelected(final XmlExpression expression)
      {
      }

   protected void sequenceSelected(final Sequence sequence)
      {
      }

   /**
    * This method gets called when a Condition has been selected from the list
    * @param condition - the Condition that was selected
    */
   protected void conditionSelected(final Condition condition)
      {
      }

   /**
    * This method gets called when a SequenceStep has been selected from the sequence
    * @param step - the SequenceStep that was selected
    */
   protected void stepSelected(final SequenceStep step)
      {
      }

   /**
    * This method gets called when a SequenceStep has begun to play in the sequence
    * @param step - the SequenceStep that was played
    */
   protected void stepPlayed(final SequenceStep step)
      {
      }

   /**
    * This method gets called when the value(s) of the current condition is checked
    * @param condition The condition being checked
    * @param values The values of the conditionals in the condition. The values
    * in the array are in the same order as their associated conditionals.
    */
   protected void conditionValuesChecked(final Condition condition, final Object[] values)
      {
      }

   /**
    * This method is called when the sequencePlayer starts and stops playing
    * @param isPlaying - if the sequencePlayer is playing
    */
   protected void togglePlaying(final boolean isPlaying)
      {
      }

   /**
    * This method is called during initialization of ExpressOMatic. It should return
    * the main JPanel to be displayed above the sequence player. If this method returns null,
    * then no panel will be displayed.
    * @return The panel to be displayed as the main panel in ExpressOMatic
    */
   protected JPanel mainPanel()
      {
      return null;
      }

   /**
    * Returns the SequencePlayer used in this ExpressOMatic
    * @return the SequencePlayer used in this ExpressOMatic
    */
   protected SequencePlayer getSequencePlayer()
      {
      return sequencePlayer;
      }

   /**
    * Returns the currently selected step in the sequence, if the sequence is playing,
    * this is the step that is currently being executed.
    * @return the currently selected step in the sequence
    */
   protected SequenceStep getSelectedStep()
      {
      return (SequenceStep)stepList.getSelectedValue();
      }

   // ALL GUI CODE BELOW ===================================================================

   private DragAndDropJList stepList = null;
   private JButton sequencePlayButton = null;
   private SequencePlayer sequencePlayer = null;
   private JPanel stepPropertiesPanel = null;

   // So we know whether the user selects an item in stepList
   // or if it's selected from the sequence being played
   private boolean userSelection = true;

   private void buildGUI()
      {
      setupGUI();

      jFrame.pack();
      jFrame.setLocationRelativeTo(null);// center the window on the screen
      jFrame.setVisible(true);
      }

   protected void setupGUI()
      {
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
                           directConnectCommunicatorManager.shutdownCommunicator();
                           relayCommunicatorManager.shutdownCommunicator();
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

      // set the mainContentPane to use the SpringLayout
      jFrame.getContentPane().setLayout(new SpringLayout());

      // Setup the size, title of the application
      jFrame.setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
      jFrame.setTitle(APPLICATION_NAME + " | Untitled");

      final JPanel expressionsPanel = expressionsPanel();
      final JPanel conditionsPanel = conditionsPanel();
      sequencePanel = sequencePanel();
      final JPanel mainPanel = mainPanel();

      // Contains expressionPanel and conditionsPanel
      final JSplitPane palletPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      palletPane.add(expressionsPanel);
      palletPane.add(conditionsPanel);

      final JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      if (mainPanel != null)
         {
         leftPane.add(mainPanel);
         leftPane.add(sequencePanel);
         }

      // Contains palletPane and leftPane
      final JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
      mainPane.add((mainPanel == null) ? sequencePanel : leftPane);
      mainPane.add(palletPane);
      jFrame.getContentPane().add(mainPane);

      SpringLayoutUtilities.makeCompactGrid(jFrame.getContentPane(),
                                            1, 1,
                                            0, 0,
                                            0, 0);

      jFrame.setJMenuBar(menuBar());

      //set the initial positions of the dividers
      jFrame.pack();
      palletPane.setDividerLocation(.5);
      if (mainPanel != null)
         {
         leftPane.setDividerLocation(leftPane.getMaximumDividerLocation());
         }
      mainPane.setDividerLocation(mainPane.getMinimumDividerLocation());
      }

   private JMenuBar menuBar()
      {
      final JMenuBar menuBar = new JMenuBar();

      final JMenu fileMenu = new JMenu("File");
      fileMenu.setMnemonic('F');
      menuBar.add(fileMenu);

      final JMenuItem newMenuItem = new JMenuItem("New Sequence");
      newMenuItem.setMnemonic(KeyEvent.VK_N);
      newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      fileMenu.add(newMenuItem);

      final JMenuItem openMenuItem = new JMenuItem("Open Sequence");
      openMenuItem.setMnemonic(KeyEvent.VK_O);
      openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      fileMenu.add(openMenuItem);

      final JMenuItem saveMenuItem = new JMenuItem("Save Sequence");
      saveMenuItem.setMnemonic(KeyEvent.VK_S);
      saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                                         Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
      fileMenu.add(saveMenuItem);

      final JMenuItem saveAsMenuItem = new JMenuItem("Save Sequence As");
      saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                                                           Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | ActionEvent.SHIFT_MASK));
      fileMenu.add(saveAsMenuItem);

      newMenuItem.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         sequenceFile = null;
         jFrame.setTitle(APPLICATION_NAME + " | Untitled");
         sequence = new Sequence("Untitled");
         stepList.setModel(sequence);
         stepList.repaint();
         }
      });

      openMenuItem.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final JFileChooser fileChooser = new JFileChooser(sequencesPath);
         final int returnValue = fileChooser.showOpenDialog(jFrame.getContentPane());

         if (returnValue == JFileChooser.APPROVE_OPTION)
            {
            final File filename = fileChooser.getSelectedFile();

            if (filename == null)
               {
               return;
               }

            Sequence newSequence;
            try
               {
               newSequence = sequenceFileHandler.openFile(filename);
               }
            catch (Exception e)
               {
               newSequence = null;
               LOG.error("Ignoring file [" + filename.getAbsolutePath() + "] because openFile() threw an exception", e);
               }

            if (newSequence != null)
               {
               sequenceFile = filename;
               jFrame.setTitle(APPLICATION_NAME + " | " + sequenceFile.getName());
               sequence = newSequence;

               stepList.setModel(sequence);
               }
            else
               {
               sequenceFile = null;
               jFrame.setTitle(APPLICATION_NAME + " | Untitled");
               }

            stepList.repaint();
            }
         }
      });

      final ActionListener saveActionListener = new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         if (!sequencesDirectory.exists())
            {
            sequencesDirectory.mkdirs();
            }

         final JFileChooser fileChooser = new JFileChooser(sequencesPath);
         final int returnValue = fileChooser.showSaveDialog(jFrame.getContentPane());

         if (returnValue == JFileChooser.APPROVE_OPTION)
            {
            String tmpFileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!tmpFileName.toLowerCase().endsWith(".xml"))
               {
               tmpFileName += ".xml";
               }
            final File saveFile = new File(tmpFileName);

            final String name = "" + saveFile.getName().substring(0, saveFile.getName().length() - 4);
            sequence.setName(name);

            if (sequenceFileHandler.saveFile(sequence, saveFile))
               {
               sequenceFile = saveFile;
               jFrame.setTitle(APPLICATION_NAME + " | " + sequence.getName());
               }
            }
         }
      };

      saveMenuItem.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         if (sequenceFile == null)
            {
            saveActionListener.actionPerformed(null);
            }
         else
            {
            sequenceFileHandler.saveFile(sequence, sequenceFile);
            }
         }
      });

      saveAsMenuItem.addActionListener(saveActionListener);

      return menuBar;
      }

   private JPanel expressionsPanel()
      {
      final JPanel expressionsPanel = new JPanel();
      expressionsPanel.setLayout(new SpringLayout());

      final JLabel expressionsLabel = new JLabel("Expressions");
      expressionsLabel.setFont(GUIConstants.FONT_LARGE);
      expressionsPanel.add(expressionsLabel);

      final DragAndDropJList expressionList = new DragAndDropJList(expressionListModel);
      expressionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      expressionList.setTransferHandler(new ExpressionTransferHandler());
      expressionList.setDragEnabled(true);
      expressionList.setCellRenderer(new ExpressionCellRenderer());

      expressionList.addListSelectionListener(new ListSelectionListener()
      {
      public void valueChanged(final ListSelectionEvent event)
         {
         expressionSelected((XmlExpression)expressionListModel.getElementAt(expressionList.getSelectedIndex()));
         }
      });

      final JScrollPane scrollPane = new JScrollPane(expressionList);
      expressionsPanel.add(scrollPane);

      SpringLayoutUtilities.makeCompactGrid(expressionsPanel,
                                            2, 1,
                                            0, 0,
                                            0, 0);

      return expressionsPanel;
      }

   private JPanel conditionsPanel()
      {
      final JPanel conditionsPanel = new JPanel();
      conditionsPanel.setLayout(new SpringLayout());

      final JLabel conditionsLabel = new JLabel("Conditions");
      conditionsLabel.setFont(GUIConstants.FONT_LARGE);
      conditionsPanel.add(conditionsLabel);

      final DragAndDropJList conditionList = new DragAndDropJList(conditionListModel);
      conditionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      conditionList.setTransferHandler(new ConditionTransferHandler());
      conditionList.setDragEnabled(true);
      conditionList.setCellRenderer(new ConditionCellRenderer());

      conditionList.addListSelectionListener(new ListSelectionListener()
      {
      public void valueChanged(final ListSelectionEvent event)
         {
         conditionSelected((Condition)conditionListModel.getElementAt(conditionList.getSelectedIndex()));
         }
      });

      final JScrollPane scrollPane = new JScrollPane(conditionList);
      conditionsPanel.add(scrollPane);

      SpringLayoutUtilities.makeCompactGrid(conditionsPanel,
                                            2, 1,
                                            0, 0,
                                            0, 0);
      return conditionsPanel;
      }

   private JPanel sequencePanel()
      {
      final JPanel sequencePanel = new JPanel();
      sequencePanel.setLayout(new SpringLayout());

      final JPanel topPanel = new JPanel(new SpringLayout());

      final JLabel sequenceLabel = new JLabel("Sequence");
      sequenceLabel.setFont(GUIConstants.FONT_LARGE);
      topPanel.add(sequenceLabel);

      topPanel.add(sequenceControlPanel());
      //		topPanel.add(hidePropertiesPanel());
      topPanel.add(connectionPanel());
      sequencePanel.add(topPanel);
      SpringLayoutUtilities.makeCompactGrid(topPanel, 1, 3, 0, 0, 0, 0);

      userSelection = true;
      stepList = new DragAndDropJList(sequence);
      stepList.addListSelectionListener(new ListSelectionListener()
      {
      public void valueChanged(final ListSelectionEvent event)
         {
         if (userSelection)
            {
            stepSelected((SequenceStep)stepList.getSelectedValue());
            }
         }
      });

      stepList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      stepList.setTransferHandler(new StepTransferHandler());
      stepList.setDragEnabled(true);
      stepList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
      stepList.setVisibleRowCount(1);
      stepList.setCellRenderer(new StepCellRenderer());

      final JScrollPane scrollPane = new JScrollPane(stepList);
      scrollPane.setMinimumSize(new Dimension(1, STEP_CELL_HEIGHT + 4));
      scrollPane.setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH - PALLET_WIDTH,
                                                STEP_CELL_HEIGHT + 4));
      sequencePanel.add(scrollPane);

      sequencePanel.add(stepPropertiesPanel());
      SpringLayoutUtilities.makeCompactGrid(sequencePanel,
                                            3, 1,
                                            0, 0,
                                            0, 0);
      return sequencePanel;
      }

   private JPanel connectionPanel()
      {
      // create a panel to hold the connect/disconnect button and the connection state panel
      final JPanel connectionPanel = new JPanel(new SpringLayout());
      connectionPanel.add(connectDisconnectButton);
      connectionPanel.add(connectionStatePanel);
      SpringLayoutUtilities.makeCompactGrid(connectionPanel,
                                            1, 2, // rows, cols
                                            0, 0, // initX, initY
                                            5, 5);// xPad, yPad
      return connectionPanel;
      }

   private JPanel sequenceControlPanel()
      {
      final JPanel controlPanel = new JPanel(new SpringLayout());

      sequencePlayButton = new JButton("Play");

      final ExpressOMatic expressOMatic = this;
      sequencePlayer = new SequencePlayer()
      {
      public void currentStepChanged(final SequenceStep step)
         {
         if (step != null)
            {
            userSelection = false;
            stepList.setSelectedIndex(sequence.indexOf(step));
            stepPlayed(step);
            userSelection = true;
            }
         }

      public void conditionValuesChecked(final Condition condition, final Object[] values)
         {
         expressOMatic.conditionValuesChecked(condition, values);
         }
      };

      sequencePlayButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent e)
         {

         // Sequence is not currently playing
         if (sequencePlayer.isPlaying())
            {
            sequencePlayer.stopPlaying();
            }
         else
            {
            sequencePlayButton.setText("Stop");

            stepList.setDragEnabled(false);
            stepPropertiesPanel.setEnabled(false);

            final Thread sequenceThread = new Thread()
            {
            public void run()
               {
               togglePlaying(true);
               sequencePlayer.playSequence(serviceManager, sequence);
               togglePlaying(false);

               sequencePlayButton.setText("Play");
               stepList.setDragEnabled(true);
               stepPropertiesPanel.setEnabled(true);
               }
            };
            sequenceThread.start();
            }
         }
      });

      sequencePlayButton.setEnabled(false);
      controlPanel.add(sequencePlayButton);

      SpringLayoutUtilities.makeCompactGrid(controlPanel,
                                            1, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// paddingX, paddingY

      return controlPanel;
      }

   //	private JPanel hidePropertiesPanel() {
   //		final JPanel panel = new JPanel(new SpringLayout());
   //
   //		final JButton showPropertiesButton = new JButton("Show Step Properties");
   //
   //		int width = (int)showPropertiesButton.getPreferredSize().getWidth();
   //		showPropertiesButton.setText("Hide Step Properties");
   //		width = Math.max(width, (int)showPropertiesButton.getPreferredSize().getWidth());
   //		showPropertiesButton.setMinimumSize(
   //				new Dimension(width,
   //						(int)showPropertiesButton.getPreferredSize().getHeight()));
   //
   //		showPropertiesButton.setIsSupported(false)
   //
   //		showPropertiesButton.addActionListener(new ActionListener() {
   //			public void actionPerformed(ActionEvent event) {
   //				if(stepPropertiesPanel.isVisible()) {
   //					stepPropertiesPanel.setVisible(false);
   //					stepPropertiesPanel.setMinimumSize(new Dimension(1, 0));
   //					stepPropertiesPanel.setMaximumSize(new Dimension(MAIN_WINDOW_WIDTH, 0));
   //					showPropertiesButton.setText("Show Step Properties");
   //				} else {
   //					stepPropertiesPanel.setVisible(true);
   //					leftPane.resetToPreferredSizes();
   //					stepPropertiesPanel.setMinimumSize(new Dimension(1, STEP_PROPERTIES_HEIGHT));
   //					stepPropertiesPanel.setMaximumSize(new Dimension(MAIN_WINDOW_WIDTH, STEP_PROPERTIES_HEIGHT));
   //					showPropertiesButton.setText("Hide Step Properties");
   //				}
   //			}
   //		});
   //
   //		panel.add(showPropertiesButton);
   //
   //		SpringLayoutUtilities.makeCompactGrid(panel,
   //				1, 1, // rows, cols
   //				0, 0, // initX, initY
   //				0, 0); // paddingX, paddingY
   //
   //		return panel;
   //	}

   private JPanel stepPropertiesPanel()
      {
      final JPanel innerPanel = new JPanel();

      final JPanel expressionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JLabel expressionLabel = new JLabel();

      final String transitionText = "Wait Until:";
      final JPanel transitionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JLabel transitionLabel = new JLabel();
      final JButton removeConditionButton = new JButton("Remove");
      final JLabel transitionSecondsLabel = new JLabel("seconds");
      final JTextField transitionTimeField = new JTextField();
      final JCheckBox loopCheckBox = new JCheckBox("Loop back to beginning");

      final NumberFormat transitionTimeFormat = NumberFormat.getNumberInstance();
      transitionTimeFormat.setMaximumFractionDigits(1);
      transitionTimeFormat.setMinimumFractionDigits(1);
      transitionTimeFormat.setMinimumIntegerDigits(1);

      final JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JButton deleteButton = new JButton("Delete Step");

      stepPropertiesPanel = new JPanel(new BorderLayout())
      {
      public void setEnabled(final boolean isEnabled)
         {
         innerPanel.setEnabled(isEnabled);
         expressionLabel.setEnabled(isEnabled);

         transitionLabel.setEnabled(isEnabled);
         transitionSecondsLabel.setEnabled(isEnabled);
         removeConditionButton.setEnabled(isEnabled);
         transitionTimeField.setEnabled(isEnabled);
         loopCheckBox.setEnabled(isEnabled);

         deleteButton.setEnabled(isEnabled);
         }
      };

      final ActionListener transitionTimeChanged = new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final SequenceStep step = (SequenceStep)stepList.getSelectedValue();
         try
            {
            final float value = Float.parseFloat(transitionTimeField.getText());
            step.getTransition().setSecondsToNextStep(value);
            transitionTimeField.setText(transitionTimeFormat.format(value));
            }
         catch (NumberFormatException e)
            {
            transitionTimeField.setText(
                  transitionTimeFormat.format(step.getTransition().getMillisToNextStep() / 1000f));
            }
         stepList.repaint();
         }
      };

      final DocumentListener transitionTimeDocumentChanged = new DocumentListener()
      {
      public void insertUpdate(final DocumentEvent event)
         {
         updateStepTime();
         }

      public void removeUpdate(final DocumentEvent event)
         {
         updateStepTime();
         }

      public void changedUpdate(final DocumentEvent event)
         {
         }

      private void updateStepTime()
         {
         final SequenceStep step = (SequenceStep)stepList.getSelectedValue();
         try
            {
            final float value = Float.parseFloat(transitionTimeField.getText());
            step.getTransition().setSecondsToNextStep(value);
            stepList.repaint();
            }
         catch (NumberFormatException e)
            {
            }
         }
      };

      final ListSelectionListener stepListListener = new ListSelectionListener()
      {
      public void valueChanged(final ListSelectionEvent event)
         {
         // No step is selected
         final int selectedIndex = stepList.getSelectedIndex();
         if (selectedIndex < 0)
            {
            innerPanel.setVisible(false);
            }

         else
            {
            final SequenceStep step = (SequenceStep)stepList.getSelectedValue();
            final SequenceTransition transition = step.getTransition();

            expressionLabel.setText("Expression: " + step.getExpression().getName());

            // This is the last step
            loopCheckBox.setSelected(sequence.getLoopBackToStart());

            if (selectedIndex < sequence.getSize() - 1 || sequence.getLoopBackToStart())
               {
               if (transition.getCondition() != null)
                  {
                  transitionLabel.setText(transitionText + " " + transition.getCondition().getName());
                  removeConditionButton.setVisible(true);
                  transitionTimeField.setVisible(false);
                  transitionSecondsLabel.setVisible(false);
                  }
               else
                  {
                  transitionLabel.setText(transitionText);
                  removeConditionButton.setVisible(false);
                  transitionTimeField.setText(transitionTimeFormat.format(transition.getMillisToNextStep() / 1000f));
                  transitionTimeField.setVisible(true);
                  transitionSecondsLabel.setVisible(true);
                  }
               }
            else
               {
               transitionLabel.setText(transitionText);
               removeConditionButton.setVisible(false);
               transitionTimeField.setVisible(false);
               transitionSecondsLabel.setVisible(false);
               }

            innerPanel.setVisible(true);
            }
         }
      };

      innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
      innerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Step Properties"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

      expressionPanel.add(expressionLabel);

      removeConditionButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final SequenceStep step = (SequenceStep)stepList.getSelectedValue();
         step.getTransition().setCondition(null);

         stepListListener.valueChanged(null);

         stepList.repaint();
         }
      });

      transitionTimeField.setColumns(3);
      transitionTimeField.addActionListener(transitionTimeChanged);
      transitionTimeField.getDocument().addDocumentListener(transitionTimeDocumentChanged);

      loopCheckBox.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         sequence.setLoopBackToStart(loopCheckBox.isSelected());

         stepListListener.valueChanged(null);

         stepList.repaint();
         }
      });

      transitionPanel.add(transitionLabel);
      transitionPanel.add(removeConditionButton);
      transitionPanel.add(transitionTimeField);
      transitionPanel.add(transitionSecondsLabel);
      transitionPanel.add(loopCheckBox);

      deleteButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         sequence.remove(stepList.getSelectedIndex());
         stepListListener.valueChanged(null);
         }
      });
      deletePanel.add(deleteButton);

      innerPanel.add(expressionPanel);
      innerPanel.add(transitionPanel);
      innerPanel.add(deletePanel);
      innerPanel.setVisible(false);

      stepList.addListSelectionListener(stepListListener);

      stepPropertiesPanel.add(innerPanel);
      stepPropertiesPanel.setMinimumSize(new Dimension(1, STEP_PROPERTIES_HEIGHT));
      stepPropertiesPanel.setMaximumSize(new Dimension(MAIN_WINDOW_WIDTH, STEP_PROPERTIES_HEIGHT));
      return stepPropertiesPanel;
      }

   private void testConditions()
      {
      if (conditionArray != null)
         {
         final ConditionFileHandler handler = ConditionFileHandler.getInstance();
         for (int i = 0; i < conditionArray.length; i++)
            {
            makeCondition(handler, conditionArray[i]);
            copyConditionIcons(conditionArray[i]);
            }
         }
      }

   private void makeCondition(final ConditionFileHandler handler, final int condition)
      {
      switch (condition)
         {
         case AVERAGE_LIGHT_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Average Light.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 50,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 150);
            break;
         case BRIGHT_LIGHT_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Bright Light.xml", COMPARE_OPERATOR.LESS_THAN, 50);
            break;
         case DARK_LIGHT_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Dark.xml", COMPARE_OPERATOR.GREATER_THAN, 150);
            break;
         case IR_NEARBY_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Something Nearby.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 50,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 150);
            break;
         case IR_VERY_NEARBY_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Something Very Nearby.xml", COMPARE_OPERATOR.GREATER_THAN, 150);
            break;
         case IR_NONE_CONDITION_1:
            makeConditionHelper(handler, 0, "Sensor 1 - Nothing in Range.xml", COMPARE_OPERATOR.LESS_THAN, 50);
            break;
         case AVERAGE_LIGHT_CONDITION_2:
            // Make a condition that says analogInput(4) > 1200 && analogInput(3) < 3700
            makeConditionHelper(handler, 1, "Sensor 2 - Average Light.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 50,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 150);
            break;
         case BRIGHT_LIGHT_CONDITION_2:
            makeConditionHelper(handler, 1, "Sensor 2 - Bright Light.xml", COMPARE_OPERATOR.LESS_THAN, 50);
            break;
         case DARK_LIGHT_CONDITION_2:
            makeConditionHelper(handler, 1, "Sensor 2 - Dark.xml", COMPARE_OPERATOR.GREATER_THAN, 150);
            break;
         case IR_NEARBY_CONDITION_2:
            makeConditionHelper(handler, 1, "Sensor 2 - Something Nearby.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 50,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 150);
            break;
         case IR_VERY_NEARBY_CONDITION_2:
            makeConditionHelper(handler, 1, "Sensor 2 - Something Very Nearby.xml", COMPARE_OPERATOR.GREATER_THAN, 150);
            break;
         case IR_NONE_CONDITION_2:
            makeConditionHelper(handler, 1, "Sensor 2 - Nothing in Range.xml", COMPARE_OPERATOR.LESS_THAN, 50);
            break;

         default:
            LOG.debug("Default branch reached in makeCondition() switch statement--this should never happen!");
            break;
         }
      }

   private void makeConditionHelper(final ConditionFileHandler handler,
                                    final int deviceId,
                                    final String fileName,
                                    final COMPARE_OPERATOR compareOperator,
                                    final int value)
      {
      makeConditionHelper(handler, deviceId, fileName, null, compareOperator, value, null, null, -1);
      }

   private void makeConditionHelper(final ConditionFileHandler handler,
                                    final int deviceId,
                                    final String fileName,
                                    final LOGICAL_OPERATOR logicOperator1,
                                    final COMPARE_OPERATOR compareOperator1,
                                    final int value1,
                                    final LOGICAL_OPERATOR logicOperator2,
                                    final COMPARE_OPERATOR compareOperator2,
                                    final int value2)
      {
      // Make a new condition
      final Condition newCondition = new Condition();

      // Create first analogConditional
      final AnalogInputsConditional far0Analog2Conditional = new AnalogInputsConditional();
      if (logicOperator1 != null)
         {
         far0Analog2Conditional.setLogicalOperator(logicOperator1);
         }
      far0Analog2Conditional.setDeviceId(deviceId);
      far0Analog2Conditional.setOperator(compareOperator1);
      far0Analog2Conditional.setValue(value1);
      // Insert the conditional into the condition
      newCondition.insert(far0Analog2Conditional);

      // Create second analogConditional
      if ((logicOperator2 != null) && (compareOperator2 != null))
         {
         final AnalogInputsConditional far1Analog2Conditional = new AnalogInputsConditional();
         far1Analog2Conditional.setLogicalOperator(logicOperator2);
         far1Analog2Conditional.setDeviceId(deviceId);
         far1Analog2Conditional.setOperator(compareOperator2);
         far1Analog2Conditional.setValue(value2);
         // Insert the conditional into the condition
         newCondition.insert(far1Analog2Conditional);
         }

      // Write the condition to a file
      final File newFile = new File(conditionsPath + File.separator + fileName);
      handler.saveFile(newCondition, newFile);
      }

   private void copyConditionIcons(final int condition)
      {
      switch (condition)
         {
         case AVERAGE_LIGHT_CONDITION_1:
            copyImgFile("Sensor 1 - Average Light.bmp", conditionIconsDirectory);
            break;
         case BRIGHT_LIGHT_CONDITION_1:
            copyImgFile("Sensor 1 - Bright Light.bmp", conditionIconsDirectory);
            break;
         case DARK_LIGHT_CONDITION_1:
            copyImgFile("Sensor 1 - Dark.bmp", conditionIconsDirectory);
            break;

         case AVERAGE_LIGHT_CONDITION_2:
            copyImgFile("Sensor 2 - Average Light.bmp", conditionIconsDirectory);
            break;
         case BRIGHT_LIGHT_CONDITION_2:
            copyImgFile("Sensor 2 - Bright Light.bmp", conditionIconsDirectory);
            break;
         case DARK_LIGHT_CONDITION_2:
            copyImgFile("Sensor 2 - Dark.bmp", conditionIconsDirectory);
            break;

         case IR_NEARBY_CONDITION_1:
            copyImgFile("Sensor 1 - Something Nearby.bmp", conditionIconsDirectory);
            break;
         case IR_VERY_NEARBY_CONDITION_1:
            copyImgFile("Sensor 1 - Something Very Nearby.bmp", conditionIconsDirectory);
            break;
         case IR_NONE_CONDITION_1:
            copyImgFile("Sensor 1 - Nothing in Range.bmp", conditionIconsDirectory);
            break;

         case IR_NEARBY_CONDITION_2:
            copyImgFile("Sensor 2 - Something Nearby.bmp", conditionIconsDirectory);
            break;
         case IR_VERY_NEARBY_CONDITION_2:
            copyImgFile("Sensor 2 - Something Very Nearby.bmp", conditionIconsDirectory);
            break;
         case IR_NONE_CONDITION_2:
            copyImgFile("Sensor 2 - Nothing in Range.bmp", conditionIconsDirectory);
            break;

         default:
            LOG.debug("Default branch reached in copyConditionIcons() switch statement--this should never happen!");
         }
      }

   /**
    * Copy an image file from the JAR to the user's filesystem
    */
   private void copyImgFile(final String imgFilename, final File targetDirectory)
      {
      BufferedInputStream inputStream = null;
      BufferedOutputStream outputStream = null;
      try
         {
         // set up the input stream
         inputStream = new BufferedInputStream(ExpressOMatic.class.getResourceAsStream(imgFilename));

         // set up the output stream
         final File outFile = new File(targetDirectory, imgFilename);
         outputStream = new BufferedOutputStream(new FileOutputStream(outFile));

         final byte[] buffer = new byte[4096];
         int bytesRead;
         while ((bytesRead = inputStream.read(buffer)) >= 0)
            {
            outputStream.write(buffer, 0, bytesRead);
            }
         }
      catch (final FileNotFoundException e)
         {
         LOG.error("Could not create the image output file", e);
         }
      catch (final IOException e)
         {
         LOG.error("IOException while reading or writing the image file", e);
         }
      finally
         {
         if (outputStream != null)
            {
            try
               {
               outputStream.close();
               }
            catch (final IOException e)
               {
               // nothing we can really do here, so just log the error
               LOG.error("IOException while closing the outputStream");
               }
            }
         if (inputStream != null)
            {
            try
               {
               inputStream.close();
               }
            catch (final IOException e)
               {
               // nothing we can really do here, so just log the error
               LOG.error("IOException while closing the inputstream");
               }
            }
         }
      }

   private void setIsConnectedToPeer(final boolean isConnectedToPeer)
      {
      this.isConnectedToPeer = isConnectedToPeer;
      connectDisconnectButton.setConnectionState(isConnectedToPeer);
      toggleGUIElementState(isConnectedToPeer);
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class ConnectDisconnectActionListener extends AbstractTimeConsumingAction
      {
      private ConnectDisconnectActionListener(final Component component)
         {
         super(component);
         }

      protected Object executeTimeConsumingAction()
         {
         LOG.debug("ConnectDisconnectActionListener$ConnectDisconnectActionListener.executeTimeConsumingAction()");
         if (isConnectedToPeer)
            {
            // disconnect from peers
            if (terkCommunicator != null)
               {
               terkCommunicator.disconnectFromPeers();
               }
            }
         else
            {
            // show the wizard
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     // determine which screen to display in the wizard
                     if (relayCommunicatorManager.isCreated())
                        {
                        if (relayCommunicatorManager.isLoggedIn())
                           {
                           connectToRobotWizard.setCurrentPanel(PeerChooserDescriptor.IDENTIFIER);
                           }
                        else
                           {
                           connectToRobotWizard.setCurrentPanel(RelayLoginFormDescriptor.IDENTIFIER);
                           }
                        }
                     else if (directConnectCommunicatorManager.isCreated())
                        {
                        connectToRobotWizard.setCurrentPanel(DirectConnectDescriptor.IDENTIFIER);
                        }
                     else
                        {
                        connectToRobotWizard.setCurrentPanel(PeerConnectionMethodDescriptor.IDENTIFIER);
                        }

                     connectToRobotWizard.showModalDialog();
                     }
                  });
            }
         return null;
         }
      }

   private final class MyUserConnectionEventListener extends UserConnectionEventAdapter
      {
      public void handleRelayLogoutEvent()
         {
         setIsConnectedToPeer(false);
         }

      public void handleForcedLogoutNotificationEvent()
         {
         setIsConnectedToPeer(false);
         }
      }

   private final class MyPeerConnectionEventListener extends PeerConnectionEventAdapter
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         LOG.debug("ExpressOMatic$MyPeerConnectionEventListener.handlePeerConnectedEvent()");
         serviceManager = new IceServiceManager(peerUserId,
                                                TerkUserPrxHelper.uncheckedCast(peerObjectProxy),
                                                terkCommunicator,
                                                terkServiceFactory);
         setIsConnectedToPeer(true);
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         LOG.debug("ExpressOMatic$MyPeerConnectionEventListener.handlePeerDisconnectedEvent()");
         serviceManager = null;
         setIsConnectedToPeer(false);
         }
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      private final TerkCommunicatorManager otherTerkCommunicatorManager;

      private MyTerkCommunicatorCreationEventListener(final TerkCommunicatorManager otherTerkCommunicatorManager)
         {
         this.otherTerkCommunicatorManager = otherTerkCommunicatorManager;
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // add the peer connection event listener
         terkCommunicator.addPeerConnectionEventListener(peerConnectionEventListener);

         if (terkCommunicator instanceof RelayCommunicator)
            {
            // If this is a RelayCommunicator, then register the user connection event listener (so we can properly disable
            // peer connections when logging out of the relay without having disconnected from peers first).
            ((RelayCommunicator)terkCommunicator).addUserConnectionEventListener(userConnectionEventListener);

            // register the connection state panel as a listener
            ((RelayCommunicator)terkCommunicator).addConnectionEventListener(connectionStatePanel);
            }
         else
            {
            // register the connection state panel as a listener
            terkCommunicator.addPeerConnectionEventListener(connectionStatePanel);
            }

         // creation of this communicator means we should shut down the other communicator since, for this app at least,
         // we only ever want to be able to connect via one mode at a time.
         otherTerkCommunicatorManager.shutdownCommunicator();

         // set the current TerkCommunicator
         ExpressOMatic.this.terkCommunicator = terkCommunicator;
         }
      }
   }