package edu.cmu.ri.mrpl.TeRK.client.expressomatic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.ConditionFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional.COMPARE_OPERATOR;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional.LOGICAL_OPERATOR;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AnalogInputsConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.TimeOfDayConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.Sequence;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequencePlayer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceTransition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.SwingConstants;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd.ConditionTransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd.ExpressionTransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd.StepTransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.AbstractListCellRenderer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.ConditionCellRenderer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.ExpressionCellRenderer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.StepCellRenderer;
import edu.cmu.ri.mrpl.swing.DragAndDropJList;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.commons.collections.list.TreeList;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ExpressOMatic extends BaseGUIClient implements SwingConstants
   {
   private static final Logger LOG = Logger.getLogger(ExpressOMatic.class);
   // **** Default Objects ****

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = "Express-O-Matic";

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/expressomatic/Express-O-Matic.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/expressomatic/Express-O-Matic.relay.ice.properties";

   public static final String TERK_PATH = System.getProperty("user.home") + File.separator + "TeRK" + File.separator;

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

   public static final int IR_NEAR_CONDITION_1 = 0;
   public static final int IR_FAR_CONDITION_1 = 1;
   public static final int IR_NONE_CONDITION_1 = 2;
   public static final int IR_NEAR_CONDITION_2 = 3;
   public static final int IR_FAR_CONDITION_2 = 4;
   public static final int IR_NONE_CONDITION_2 = 5;
   public static final int LONG_IR_NEAR_CONDITION = 6;
   public static final int LONG_IR_FAR_CONDITION = 7;
   public static final int LONG_IR_NONE_CONDITION = 8;
   public static final int DARK_CONDITION_1 = 9;
   public static final int AVERAGELIGHT_CONDITION_1 = 10;
   public static final int BRIGHT_CONDITION_1 = 11;
   public static final int DARK_CONDITION_2 = 12;
   public static final int AVERAGELIGHT_CONDITION_2 = 13;
   public static final int BRIGHT_CONDITION_2 = 14;
   public static final int COLD_CONDITION = 15;
   public static final int LUKEWARM_CONDITION = 16;
   public static final int HOT_CONDITION = 17;
   public static final int AFTER6PM_CONDITION = 18;
   public static final int FLOWERTOUCHED_CONDITION = 19;

   private final String applicationName;

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
   private Sequence sequence;
   private final int[] conditionArray;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new ExpressOMatic(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                 new int[]{IR_NEAR_CONDITION_1, IR_FAR_CONDITION_1, IR_NONE_CONDITION_1,
                                           IR_NEAR_CONDITION_2, IR_FAR_CONDITION_2, IR_NONE_CONDITION_2,
                                           LONG_IR_NEAR_CONDITION, LONG_IR_FAR_CONDITION, LONG_IR_NONE_CONDITION,
                                           DARK_CONDITION_1, AVERAGELIGHT_CONDITION_1, BRIGHT_CONDITION_1,
                                           DARK_CONDITION_2, AVERAGELIGHT_CONDITION_2, BRIGHT_CONDITION_2,
                                           COLD_CONDITION, LUKEWARM_CONDITION, HOT_CONDITION});
               }
            });
      }

   protected ExpressOMatic(final String applicationName,
                           final String relayCommunicatorIcePropertiesFile,
                           final String directConnectCommunicatorIcePropertiesFile,
                           final int[] conditionArray)
      {
      super(applicationName, relayCommunicatorIcePropertiesFile, directConnectCommunicatorIcePropertiesFile);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void toggleGUIElementState(final boolean isEnabled)
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
            });

      this.applicationName = applicationName;
      this.conditionArray = conditionArray;

      final String applicationHomePath = TERK_PATH + applicationName + File.separator;
      expressionsPath = TERK_PATH + "Expressions" + File.separator;
      expressionIconsPath = expressionsPath + "Icons" + File.separator;
      conditionsPath = applicationHomePath + "Conditions" + File.separator;
      conditionIconsPath = conditionsPath + "Icons" + File.separator;
      sequencesPath = applicationHomePath + "Sequences" + File.separator;

      final File homeDirectory = new File(TERK_PATH);
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

      sequence = new Sequence();

      testConditions();

      buildGUI();
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
                     LOG.error("Directory poller ignoring file [" + file.getAbsolutePath() + "] because openFile() threw an exception", e);
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

         // todo: this is pretty inefficient
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
   protected void expressionSelected(final Expression expression)
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

      pack();
      setLocationRelativeTo(null);// center the window on the screen
      setVisible(true);
      }

   protected void setupGUI()
      {
      // set the mainContentPane to use the SpringLayout
      getMainContentPane().setLayout(new SpringLayout());

      // Setup the size, title of the application
      setPreferredSize(new Dimension(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
      setResizable(true);

      setTitle(applicationName + " | Untitled");

      final JPanel expressionsPanel = expressionsPanel();
      final JPanel conditionsPanel = conditionsPanel();
      final JPanel sequencePanel = sequencePanel();
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
      getMainContentPane().add(mainPane);

      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            1, 1,
                                            0, 0,
                                            0, 0);

      setJMenuBar(menuBar());

      //set the initial positions of the dividers
      pack();
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
         setTitle(applicationName + " | Untitled");
         sequence = new Sequence();
         stepList.setModel(sequence);
         stepList.repaint();
         }
      });

      openMenuItem.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final JFileChooser fileChooser = new JFileChooser(sequencesPath);
         final int returnValue = fileChooser.showOpenDialog(getMainContentPane());

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
               setTitle(applicationName + " | " + sequenceFile.getName());
               sequence = newSequence;

               stepList.setModel(sequence);
               }
            else
               {
               sequenceFile = null;
               setTitle(applicationName + " | Untitled");
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
         final int returnValue = fileChooser.showSaveDialog(getMainContentPane());

         if (returnValue == JFileChooser.APPROVE_OPTION)
            {
            String tmpFileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!tmpFileName.toLowerCase().endsWith(".xml"))
               {
               tmpFileName += ".xml";
               }
            final File saveFile = new File(tmpFileName);

            if (sequenceFileHandler.saveFile(sequence, saveFile))
               {
               sequenceFile = saveFile;
               setTitle(applicationName + " | " + sequenceFile.getName());
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
         expressionSelected((Expression)expressionListModel.getElementAt(expressionList.getSelectedIndex()));
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
      connectionPanel.add(getConnectDisconnectButton());
      connectionPanel.add(getConnectionStatePanel());
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
               sequencePlayer.playSequence(getQwerkController(), sequence);
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

      final JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JLabel speedLabel = new JLabel("Expression Speed:");
      final JComboBox speedComboBox = new JComboBox(new String[]{"Fast", "Medium", "Slow", "Custom"});
      final Hashtable<Integer, JLabel> velocityLabelDictionary = new Hashtable<Integer, JLabel>();
      final JSlider velocitySlider = new JSlider(ExpressionSpeed.MINIMUM_VELOCITY, ExpressionSpeed.MAXIMUM_VELOCITY);
      final JPanel velocityPanel = new JPanel();
      final JLabel velocityLabel = new JLabel();
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

         speedLabel.setEnabled(isEnabled);
         speedComboBox.setEnabled(isEnabled);
         velocityLabel.setEnabled(isEnabled);
         velocitySlider.setEnabled(isEnabled && "Custom".equals(speedComboBox.getSelectedItem()));

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
            final ExpressionSpeed speed = step.getExpresisonSpeed();

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

            switch (speed.getServoVelocity())
               {
               case ExpressionSpeed.FAST_VELOCITY:
                  speedComboBox.setSelectedItem("Fast");
                  velocitySlider.setEnabled(false);
                  velocityLabel.setEnabled(false);
                  break;
               case ExpressionSpeed.MEDIUM_VELOCITY:
                  speedComboBox.setSelectedItem("Medium");
                  velocitySlider.setEnabled(false);
                  velocityLabel.setEnabled(false);
                  break;
               case ExpressionSpeed.SLOW_VELOCITY:
                  speedComboBox.setSelectedItem("Slow");
                  velocitySlider.setEnabled(false);
                  velocityLabel.setEnabled(false);
                  break;
               default:
                  speedComboBox.setSelectedItem("Custom");
                  velocitySlider.setEnabled(!sequencePlayer.isPlaying());
                  velocityLabel.setEnabled(!sequencePlayer.isPlaying());
               }
            velocitySlider.setValue(speed.getServoVelocity());
            velocityLabel.setText("Velocity: " + speed.getServoVelocity());
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

      speedComboBox.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final ExpressionSpeed speed = ((SequenceStep)stepList.getSelectedValue()).getExpresisonSpeed();

         final String selection = (String)speedComboBox.getSelectedItem();
         if ("Fast".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.FAST_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: " + speed.getServoVelocity());
            velocityLabel.setEnabled(false);
            }
         else if ("Medium".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.MEDIUM_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: " + speed.getServoVelocity());
            velocityLabel.setEnabled(false);
            }
         else if ("Slow".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.SLOW_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: " + speed.getServoVelocity());
            velocityLabel.setEnabled(false);
            }
         else
            {//selectoin.equals("Custom")
            velocitySlider.setEnabled(true);
            velocityLabel.setEnabled(true);
            }
         }
      });

      velocityLabel.setHorizontalAlignment(JLabel.LEFT);
      velocityLabel.setFont(GUIConstants.FONT_SMALL);

      velocitySlider.setOrientation(JSlider.HORIZONTAL);
      velocitySlider.setMajorTickSpacing(
            (ExpressionSpeed.MAXIMUM_VELOCITY - ExpressionSpeed.MINIMUM_VELOCITY) / 2);
      velocitySlider.setPaintLabels(true);
      velocitySlider.setSize(100, 20);

      final JLabel minVelocityLabel = new JLabel(ExpressionSpeed.MINIMUM_VELOCITY + "");
      minVelocityLabel.setFont(GUIConstants.FONT_SMALL);
      velocityLabelDictionary.put(ExpressionSpeed.MINIMUM_VELOCITY, minVelocityLabel);
      final JLabel maxVelocityLabel = new JLabel(ExpressionSpeed.MAXIMUM_VELOCITY + "");
      maxVelocityLabel.setFont(GUIConstants.FONT_SMALL);
      velocityLabelDictionary.put(ExpressionSpeed.MAXIMUM_VELOCITY, maxVelocityLabel);
      velocitySlider.setLabelTable(velocityLabelDictionary);

      velocitySlider.addChangeListener(new ChangeListener()
      {
      public void stateChanged(final ChangeEvent event)
         {
         final ExpressionSpeed speed = ((SequenceStep)stepList.getSelectedValue()).getExpresisonSpeed();
         speed.setServoVelocity(velocitySlider.getValue());
         velocityLabel.setText("Velocity: " + velocitySlider.getValue());
         }
      });

      velocityPanel.setLayout(new BoxLayout(velocityPanel, BoxLayout.Y_AXIS));
      velocityPanel.setLayout(null);
      velocityPanel.setMinimumSize(velocitySlider.getPreferredSize());
      velocityPanel.setPreferredSize(velocitySlider.getPreferredSize());
      velocitySlider.setBounds(0, 0,
                               (int)velocitySlider.getPreferredSize().getWidth(),
                               (int)velocitySlider.getPreferredSize().getHeight());
      velocityPanel.add(velocitySlider);
      velocityLabel.setText("Velocity: " + ExpressionSpeed.MAXIMUM_VELOCITY + " ");// This is to get the maximum size of the label
      velocityLabel.setBounds(
            (int)(velocityPanel.getPreferredSize().getWidth() - velocityLabel.getPreferredSize().getWidth()) / 2,
            (int)(velocityPanel.getPreferredSize().getHeight() - velocityLabel.getPreferredSize().getHeight()),
            (int)velocityLabel.getPreferredSize().getWidth(),
            (int)velocityLabel.getPreferredSize().getHeight());
      velocityPanel.add(velocityLabel);

      speedPanel.add(speedLabel);
      speedPanel.add(speedComboBox);
      speedPanel.add(velocityPanel);

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
      innerPanel.add(speedPanel);
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

   private void makeCondition(ConditionFileHandler handler, int condition)
      {
      switch (condition)
         {
         case AFTER6PM_CONDITION:
            // Make a conditional that says timeOfDay > 6pm
            final Condition sixPMCondition = new Condition();

            // Make a calendar object that is set for 6pm on the day of the Epoc
            final Calendar sixPM = Calendar.getInstance();
            sixPM.setTimeInMillis(0);
            sixPM.set(Calendar.HOUR_OF_DAY, 18);

            // Create a TimeOfDayConditional
            final TimeOfDayConditional todC = new TimeOfDayConditional();

            // Set the calendar value to 6pm
            todC.setValue(sixPM);

            // Set the compare operator to >=
            todC.setOperator(COMPARE_OPERATOR.GREATER_THAN_EQUALS);

            // Add this conditional to the condition
            sixPMCondition.insert(todC);

            // Save it to file
            final File sixPMFile = new File(conditionsPath + File.separator + "After 6PM.xml");
            handler.saveFile(sixPMCondition, sixPMFile);
            break;

         case FLOWERTOUCHED_CONDITION:
            // Make a condition that says analogInput(0) > 1700 || analogInput(1) > 1700 || analogInput(2) > 1700
            final Condition closeContactCondition = new Condition();

            // Create an analogConditional for "|| analogInput(0) > 1700"
            final AnalogInputsConditional closeAnalog0Conditional = new AnalogInputsConditional();
            closeAnalog0Conditional.setLogicalOperator(LOGICAL_OPERATOR.OR);
            closeAnalog0Conditional.setDeviceId(0);
            closeAnalog0Conditional.setOperator(COMPARE_OPERATOR.GREATER_THAN);
            closeAnalog0Conditional.setValue(1700);

            // Create an analogConditional for "|| analogInput(1) > 1700"
            final AnalogInputsConditional closeAnalog1Conditional = new AnalogInputsConditional();
            closeAnalog1Conditional.setLogicalOperator(LOGICAL_OPERATOR.OR);
            closeAnalog1Conditional.setDeviceId(1);
            closeAnalog1Conditional.setOperator(COMPARE_OPERATOR.GREATER_THAN);
            closeAnalog1Conditional.setValue(1700);

            // Create an analogConditional for "|| analogInput(2) > 1700"
            final AnalogInputsConditional closeAnalog2Conditional = new AnalogInputsConditional();
            closeAnalog2Conditional.setLogicalOperator(LOGICAL_OPERATOR.OR);
            closeAnalog2Conditional.setDeviceId(2);
            closeAnalog2Conditional.setOperator(COMPARE_OPERATOR.GREATER_THAN);
            closeAnalog2Conditional.setValue(1700);

            // Insert all these conditionals into conditional statement
            closeContactCondition.insert(closeAnalog0Conditional);
            closeContactCondition.insert(closeAnalog1Conditional);
            closeContactCondition.insert(closeAnalog2Conditional);

            // Write these to file
            final File closeContactFile = new File(conditionsPath + File.separator + "Flower Touched.xml");
            handler.saveFile(closeContactCondition, closeContactFile);
            break;

         // The following three conditions are to make conditions for bright, average, and
         // dark photoresistor values.
         case DARK_CONDITION_1:
            // Make a conditional that says analogInput(3) > 3700 for dark
            makeConditionHelper(handler, 3, "port 3 - Dark.xml", COMPARE_OPERATOR.GREATER_THAN, 3700);
            break;

         case DARK_CONDITION_2:
            // Make a conditional that says analogInput(4) > 3700 for dark
            makeConditionHelper(handler, 4, "port 4 - Dark.xml", COMPARE_OPERATOR.GREATER_THAN, 3700);
            break;

         case AVERAGELIGHT_CONDITION_1:
            // Make a condition that says analogInput(3) > 1200 && analogInput(0) < 3700
            makeConditionHelper(handler, 3, "port 3 - Average Light.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 1200,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 3700);
            break;

         case AVERAGELIGHT_CONDITION_2:
            // Make a condition that says analogInput(4) > 1200 && analogInput(3) < 3700
            makeConditionHelper(handler, 4, "port 4 - Average Light.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 1200,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 3700);
            break;

         case BRIGHT_CONDITION_1:
            // Make a condition that says analogInput(3) < 1200 for dark
            makeConditionHelper(handler, 3, "port 3 - Bright Light.xml", COMPARE_OPERATOR.LESS_THAN, 1200);
            break;

         case BRIGHT_CONDITION_2:
            // Make a condition that says analogInput(4) < 1200 for dark
            makeConditionHelper(handler, 4, "port 4 - Bright Light.xml", COMPARE_OPERATOR.LESS_THAN, 1200);
            break;

         // The following three conditions are to make conditions for hot, lukewarm, and
         // cold thermistor values - the code is very similar to the previous photoresistor code,
         // but the values have changed.
         case COLD_CONDITION:
            // Make a condition that says analogInput(5) > 2400 for cold
            makeConditionHelper(handler, 5, "port 5 - Cold.xml", COMPARE_OPERATOR.GREATER_THAN, 2400);
            break;

         case LUKEWARM_CONDITION:
            // Make a condition that says "analogInput(5) > 2000 && analogInput(1) < 2400"
            makeConditionHelper(handler, 5, "port 5 - Lukewarm.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 2000,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 2400);
            break;

         case HOT_CONDITION:
            // Make a condition that says analogInput(5) < 2000 for hot
            makeConditionHelper(handler, 5, "port 5 - Hot.xml", COMPARE_OPERATOR.LESS_THAN, 2000);
            break;

         // The following nine conditions are to make conditions for near, far, and
         // none IR rangefinder values - the code is very similar to the previous photoresistor code,
         // but the values have changed.
         case IR_NONE_CONDITION_1:
            // Make a condition that says analogInput(0) < 500
            makeConditionHelper(handler, 0, "port 0 - Nothing detected by IR.xml", COMPARE_OPERATOR.LESS_THAN, 500);
            break;

         case IR_NEAR_CONDITION_1:
            // Make a condition that says analogInput(0) > 2400 for near
            makeConditionHelper(handler, 0, "port 0 - Near.xml", COMPARE_OPERATOR.GREATER_THAN, 2400);
            break;

         case IR_FAR_CONDITION_1:
            // Make a condition that says "analogInput(0) > 500 && analogInput(2) < 2400"
            makeConditionHelper(handler, 0, "port 0 - Far.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 500,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 2400);
            break;

         case IR_NONE_CONDITION_2:
            // Make a condition that says analogInput(1) < 500
            makeConditionHelper(handler, 1, "port 1 - Nothing detected by IR.xml", COMPARE_OPERATOR.LESS_THAN, 500);
            break;

         case IR_NEAR_CONDITION_2:
            // Make a condition that says analogInput(1) > 2400 for near
            makeConditionHelper(handler, 1, "port 1 - Near.xml", COMPARE_OPERATOR.GREATER_THAN, 2400);
            break;

         case IR_FAR_CONDITION_2:
            // Make a condition that says "analogInput(1) > 500 && analogInput(2) < 2400"
            makeConditionHelper(handler, 1, "port 1 - Far.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 500,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 2400);
            break;

         case LONG_IR_NONE_CONDITION:
            // Make a condition that says analogInput(2) < 500
            makeConditionHelper(handler, 2, "port 2 - Nothing detected by IR.xml", COMPARE_OPERATOR.LESS_THAN, 500);
            break;

         case LONG_IR_NEAR_CONDITION:
            // Make a condition that says analogInput(2) > 2400 for near
            makeConditionHelper(handler, 2, "port 2 - Near.xml", COMPARE_OPERATOR.GREATER_THAN, 2400);
            break;

         case LONG_IR_FAR_CONDITION:
            // Make a condition that says "analogInput(2) > 500 && analogInput(2) < 2400"
            makeConditionHelper(handler, 2, "port 2 - Far.xml",
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.GREATER_THAN, 500,
                                LOGICAL_OPERATOR.AND, COMPARE_OPERATOR.LESS_THAN, 2400);
            break;
         }
      }

   private void makeConditionHelper(ConditionFileHandler handler,
                                    int deviceId,
                                    String fileName,
                                    COMPARE_OPERATOR compareOperator,
                                    int value)
      {
      makeConditionHelper(handler, deviceId, fileName, null, compareOperator, value, null, null, -1);
      }

   private void makeConditionHelper(ConditionFileHandler handler,
                                    int deviceId,
                                    String fileName,
                                    LOGICAL_OPERATOR logicOperator1,
                                    COMPARE_OPERATOR compareOperator1,
                                    int value1,
                                    LOGICAL_OPERATOR logicOperator2,
                                    COMPARE_OPERATOR compareOperator2,
                                    int value2)
      {
      // Make a new condition
      Condition newCondition = new Condition();

      // Create first analogConditional
      AnalogInputsConditional far0Analog2Conditional = new AnalogInputsConditional();
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
         AnalogInputsConditional far1Analog2Conditional = new AnalogInputsConditional();
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

   private void copyConditionIcons(int condition)
      {
      switch (condition)
         {
         case IR_NEAR_CONDITION_1:
            copyImgFile("port 0 - Near.bmp", conditionIconsDirectory);
            break;
         case IR_FAR_CONDITION_1:
            copyImgFile("port 0 - Far.bmp", conditionIconsDirectory);
            break;
         case IR_NONE_CONDITION_1:
            copyImgFile("port 0 - Nothing detected by IR.bmp", conditionIconsDirectory);
            break;
         case IR_NEAR_CONDITION_2:
            copyImgFile("port 1 - Near.bmp", conditionIconsDirectory);
            break;
         case IR_FAR_CONDITION_2:
            copyImgFile("port 1 - Far.bmp", conditionIconsDirectory);
            break;
         case IR_NONE_CONDITION_2:
            copyImgFile("port 1 - Nothing detected by IR.bmp", conditionIconsDirectory);
            break;
         case LONG_IR_NEAR_CONDITION:
            copyImgFile("port 2 - Near.bmp", conditionIconsDirectory);
            break;
         case LONG_IR_FAR_CONDITION:
            copyImgFile("port 2 - Far.bmp", conditionIconsDirectory);
            break;
         case LONG_IR_NONE_CONDITION:
            copyImgFile("port 2 - Nothing detected by IR.bmp", conditionIconsDirectory);
            break;
         case DARK_CONDITION_1:
            copyImgFile("port 3 - Dark.bmp", conditionIconsDirectory);
            break;
         case AVERAGELIGHT_CONDITION_1:
            copyImgFile("port 3 - Average Light.bmp", conditionIconsDirectory);
            break;
         case BRIGHT_CONDITION_1:
            copyImgFile("port 3 - Bright Light.bmp", conditionIconsDirectory);
            break;
         case DARK_CONDITION_2:
            copyImgFile("port 4 - Dark.bmp", conditionIconsDirectory);
            break;
         case AVERAGELIGHT_CONDITION_2:
            copyImgFile("port 4 - Average Light.bmp", conditionIconsDirectory);
            break;
         case BRIGHT_CONDITION_2:
            copyImgFile("port 4 - Bright Light.bmp", conditionIconsDirectory);
            break;
         case COLD_CONDITION:
            copyImgFile("port 5 - Cold.bmp", conditionIconsDirectory);
            break;
         case LUKEWARM_CONDITION:
            copyImgFile("port 5 - Lukewarm.bmp", conditionIconsDirectory);
            break;
         case HOT_CONDITION:
            copyImgFile("port 5 - Hot.bmp", conditionIconsDirectory);
            break;
         case AFTER6PM_CONDITION:
            break;
         case FLOWERTOUCHED_CONDITION:
            break;
         }
      }

   /**
    * Copy an image file from the JAR to the user's filesystem
    */
   private void copyImgFile(final String imgFilename, File targetDirectory)
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
               System.out.println("IOException while closing the outputStream");
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
   }
