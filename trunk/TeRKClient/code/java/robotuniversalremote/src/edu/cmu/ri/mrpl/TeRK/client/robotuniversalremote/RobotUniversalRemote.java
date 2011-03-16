package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AdvancedFlag;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AnalogInputCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AudioCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.CameraCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DigitalInCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DigitalOutCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.LEDCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.MotorCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.QwerkBoardPortCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.QwerkCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.QwerkPortInfo;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.ServoCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.AttributeMap.SerializableRectangle2D;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

public final class RobotUniversalRemote extends BaseGUIClient// Jerry JFrame
   {
   private static final String RUR_VERSION = "1.0";

   private static final Logger LOG = Logger.getLogger(RobotUniversalRemote.class);
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RobotUniversalRemote.class.getName());

   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE =
         "/edu/cmu/ri/mrpl/TeRK/client/robotuniversalremote/RobotUniversalRemote.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE =
         "/edu/cmu/ri/mrpl/TeRK/client/robotuniversalremote/RobotUniversalRemote.relay.ice.properties";

   private static final Color GRID_COLOR = new Color(235, 235, 255);
   private static final int GRID_SPACING = 20;
   private static final Color TOOLBOX_BACKGROUND_COLOR = new Color(240, 240, 240);

   private static final double TOOLBOX_ITEM_WIDTH = 100;
   private static final double TOOLBOX_ITEM_HEIGHT = 20;
   private static final double TOOLBOX_ITEM_START_X = 10;
   private static final double TOOLBOX_ITEM_START_Y = 20;
   private static final double TOOLBOX_ITEM_SPACING = 10;

   private static final double QWERK_BOARD_X = 20;
   private static final double QWERK_BOARD_Y = 20;

   // Preference keys for RUR
   private static final String KEY_WINDOW_SIZE_WIDTH = "window_size_w";
   private static final String KEY_WINDOW_SIZE_HEIGHT = "window_size_h";
   private static final int DEFAULT_WINDOW_SIZE_WIDTH = 800;//600;
   private static final int DEFAULT_WINDOW_SIZE_HEIGHT = 600;//400;

   // This string indicates where the Expressions are saved and changes to fit the OS
   private String OSpath;
   private String audioPath;

   private JMenuBar menuBar;

   private JMenu fileMenu;
   private JMenuItem newFileMenuItem;
   private JMenuItem openFileMenuItem;
   private JMenuItem saveMenuItem;
   private JMenuItem saveAsMenuItem;

   // XmlExpression Menu
   private JMenu expressionMenu;
   private JMenuItem saveExpression;
   //   private JMenuItem shareExpression;
   private JMenuItem deleteExpression;
   private JMenuItem expressionItem;

   //advanced menu for advanced servo controls
   private JMenu optionsMenu;
   private JMenuItem enableAdvanced;

   private JSeparator fileMenuSeparator1;
   private JSeparator expressionsMenuSeparator1;
   private JMenuItem exitMenuItem;

   private final AdvancedFlag advancedMode = new AdvancedFlag();

   private JMenu helpMenu;
   private JMenuItem helpMenuItem;

   private JPanel configurePanel;
   private JLayeredPane layeredPane;

   private GraphModel mToolboxModel;
   private GraphLayoutCache mToolboxView;
   private JGraph mToolboxGraph;

   private GraphModel mCanvasModel;
   private GraphLayoutCache mCanvasView;
   private JGraph mCanvasGraph;
   private QwerkCell mQwerkBoardCell;

   private DefaultGraphCell highlightCell;
   private boolean drawingHighlightCell;

   // Information for save/open
   private transient String savedFileName = "";
   private transient String deleteFileName = "";

   /**
    * File chooser for loading and saving graphs. Note that it is lazily
    * instaniated, always call initFileChooser before use.
    */
   private JFileChooser fileChooser = null;

   private final JButton emergencyStopButton = GUIConstants.createButton("Emergency Stop");

   /**
    * Run the Robot Universal Remote application.
    * @param args The arguments passed into the application.
    */
   public static void main(final String[] args)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new RobotUniversalRemote();
               }
            });
      }

   /**
    * Constructor for the RUR main class.
    */
   public RobotUniversalRemote()
      {
      super(RESOURCES.getString("application.name"), ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void toggleGUIElementState(final boolean isConnectedToRobot)
               {
               toggleGUIElements(isConnectedToRobot);
               }
            });

      try
         {
         final Preferences prefs = Preferences.userNodeForPackage(this.getClass());

         // Setup the size, title of the application
         setPreferredSize(new Dimension(
               prefs.getInt(KEY_WINDOW_SIZE_WIDTH, DEFAULT_WINDOW_SIZE_WIDTH),
               prefs.getInt(KEY_WINDOW_SIZE_HEIGHT, DEFAULT_WINDOW_SIZE_HEIGHT)));

         setTitle(RESOURCES.getString("application.name"));

         // Setup the menu bar
         initGUIMenuBar();

         getContentPane().setLayout(new BorderLayout());

         configurePanel = new JPanel(new BorderLayout());

         // Setup the toolbox
         initGUIToolbox(configurePanel);

         // Setup the JGraph canvas
         initGUICanvas(configurePanel);

         getContentPane().add(configurePanel, BorderLayout.CENTER);

         // Setup the frame
         setResizable(true);
         pack();
         setLocationRelativeTo(null);// center the window on the screen
         setVisible(true);
         }
      catch (Exception e)
         {
         LOG.error("Exception while creating the RobotUniversalRemote", e);
         }
      }

   // ----------------------------------------------------------------------

   private void deleteExpressionFromMenu(String del)
      {
      //put expressions menu removal here
      for (int i = 0; i < expressionMenu.getItemCount(); i++)
         {
         String str = expressionMenu.getItem(i) + "::";
         if (str.indexOf(del) > 1)
            {
            LOG.debug("Menu item to be deleted:::" + i + "::::");
            expressionMenu.remove(i);
            }
         }
      }

   private void toggleGUIElements(final boolean isEnabled)
      {
      mToolboxGraph.setDragEnabled(isEnabled);
      emergencyStopButton.setEnabled(isEnabled);
      expressionMenu.setEnabled(isEnabled);
      }

   /**
    * Initialize the menu part of the GUI.
    */
   private void initGUIMenuBar()
      {
      LOG.debug("RobotUniversalRemote.initGUIMenuBar()");

      // Get the default menu shortcut key for this platform
      final int shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      // Setup the menu bar
      menuBar = new JMenuBar();
      setJMenuBar(menuBar);

      //create it here for convenience, added to menu later

      optionsMenu = new JMenu();
      optionsMenu.setText("Options");
      final JMenuItem clearServos = new JMenuItem();
      enableAdvanced = new JMenuItem("Toggle Advanced Mode");
      enableAdvanced.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               int enableResult = 0;
               if (advancedMode.getValue() == false)
                  {
                  enableResult = JOptionPane.showConfirmDialog(null,
                                                               "Advanced mode is intended for experienced users and settings can affect all software run on your Qwerk." +
                                                               "\n\nAre you sure you want to continue?",
                                                               "Enable Advanced Mode",
                                                               JOptionPane.YES_NO_OPTION,
                                                               JOptionPane.WARNING_MESSAGE);
                  }
               else
                  {
                  enableResult = JOptionPane.showConfirmDialog(null,
                                                               "Are you sure you want to disable advanced mode?",
                                                               "Disable Advanced Mode",
                                                               JOptionPane.YES_NO_OPTION,
                                                               JOptionPane.WARNING_MESSAGE);
                  }

               if (enableResult == JOptionPane.NO_OPTION)
                  {
                  // Cancel
                  return;
                  }

               if (advancedMode.getValue() == true)
                  {
                  //disable advanced mode
                  advancedMode.setValue(false);
                  clearServos.setEnabled(false);
                  }
               else
                  {
                  //enable advanced mode
                  JOptionPane.showMessageDialog(null, "Be sure to check your servo positions before saving them, and set your min and max before you set your initial position.\n" +
                                                      "The qwerk scales your min-max range to 0-255 so 127 will always be the center.", "Servo config notes", JOptionPane.WARNING_MESSAGE);
                  advancedMode.setValue(true);
                  clearServos.setEnabled(true);
                  }
               }
            });
      optionsMenu.add(enableAdvanced);
      clearServos.setEnabled(false);
      clearServos.setText("Clear servo settings");
      clearServos.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent e)
         {
         final int clearResult = JOptionPane.showConfirmDialog(null,
                                                               "Clearing servo settings will reset all min, max, and default servo positions set by the user to their defaults\n" +
                                                               "This can affect all other software run on your Qwerk that depend on these settings.\n\n" +
                                                               "Are you sure you want to continue?",
                                                               "Clear Servo Settings",
                                                               JOptionPane.YES_NO_OPTION,
                                                               JOptionPane.WARNING_MESSAGE);

         if (clearResult == JOptionPane.NO_OPTION)
            {
            // Cancel
            return;
            }

         //clear all servo settings
         final ServoService servos = getQwerkController().getServoService();
         boolean[] allServos = new boolean[16];
         for (boolean servo : allServos)
            {
            servo = true;
            }
         int[] minPositions = new int[16];
         for (int minP : minPositions)
            {
            minP = ServoService.SERVO_MIN_BOUND;
            }
         final int[] maxPositions = new int[16];
         for (int maxP : maxPositions)
            {
            maxP = ServoService.SERVO_MAX_BOUND;
            }
         final int[] defaultPositions = new int[16];
         for (int defP : defaultPositions)
            {
            defP = ServoService.SERVO_DEFAULT_POSITION;
            }

         servos.setConfigs(allServos, minPositions, maxPositions, defaultPositions);
         }
      });
      optionsMenu.add(clearServos);

      // File menu
      fileMenu = new JMenu();
      menuBar.add(fileMenu);
      fileMenu.setText("File");
      newFileMenuItem = new JMenuItem("New", KeyEvent.VK_N);
      newFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcutMask));
      newFileMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               final int overwriteResult = JOptionPane.showConfirmDialog(null,
                                                                         "You will lose any changes on your current robot configuration. Are you sure you wish to continue?",
                                                                         "New Robot Configuration",
                                                                         JOptionPane.YES_NO_OPTION,
                                                                         JOptionPane.WARNING_MESSAGE);

               if (overwriteResult == JOptionPane.NO_OPTION)
                  {
                  // Cancel
                  return;
                  }

               newFile();
               }
            });

      fileMenu.add(newFileMenuItem);

      openFileMenuItem = new JMenuItem("Open", KeyEvent.VK_O);
      openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutMask));
      openFileMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               final int overwriteResult = JOptionPane.showConfirmDialog(null,
                                                                         "You will lose any changes on your current robot configuration. Are you sure you wish to continue?",
                                                                         "New Robot Configuration",
                                                                         JOptionPane.YES_NO_OPTION,
                                                                         JOptionPane.WARNING_MESSAGE);

               if (overwriteResult == JOptionPane.NO_OPTION)
                  {
                  // Cancel
                  return;
                  }

               openFile(false);
               }
            });
      fileMenu.add(openFileMenuItem);

      saveMenuItem = new JMenuItem("Save", KeyEvent.VK_S);
      saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask));
      saveMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               saveFile(false);
               }
            });
      fileMenu.add(saveMenuItem);

      saveAsMenuItem = new JMenuItem("Save As...", KeyEvent.VK_A);
      saveAsMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               saveFile(true);
               }
            });
      fileMenu.add(saveAsMenuItem);

      OSpath = System.getProperty("user.home") + File.separator + "TeRK" + File.separator + "Expressions" + File.separator;
      audioPath = System.getProperty("user.home") + File.separator + "TeRK" + File.separator + "Audio" + File.separator;

      //XmlExpression menu and action code
      expressionMenu = new JMenu();
      expressionMenu.setEnabled(false);
      menuBar.add(expressionMenu);
      expressionMenu.setText("Expressions");
      saveExpression = new JMenuItem("Save Expression");
      saveExpression.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               String tempFileName = savedFileName;
               savedFileName = "";

               while (savedFileName.length() == 0)
                  {
                  // get the exp file name from the user
                  savedFileName = (String)JOptionPane.showInputDialog(
                        null,
                        "Expression name:",
                        "Save Expression",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "Smile");

                  if (savedFileName == null)
                     {
                     break;
                     }
                  }

               if (savedFileName != null)
                  {
                  if (!savedFileName.toLowerCase().endsWith(".xml"))
                     {
                     savedFileName += ".xml";
                     }

                  String tmpExpressionName = savedFileName;
                  // prepend the directory
                  savedFileName = OSpath + savedFileName;

                  File saveFile = new File(savedFileName);

                  if (saveFile.exists())
                     {
                     // Verify that the user wants to overwrite this file
                     final int overwriteResult = JOptionPane.showConfirmDialog(null,
                                                                               "The file chosen will be overwritten. Are you sure you wish to overwrite this file?",
                                                                               "Overwrite File",
                                                                               JOptionPane.YES_NO_OPTION,
                                                                               JOptionPane.WARNING_MESSAGE);

                     if (overwriteResult == JOptionPane.NO_OPTION)
                        {
                        // Cancel and reset saved file name
                        savedFileName = tempFileName;
                        return;
                        }
                     }
                  else
                     {
                     //Add the new file to the menu
                     //////////////////////
                     final File expressionFile = new File(OSpath + tmpExpressionName);
                     expressionItem = new JMenuItem(tmpExpressionName);
                     expressionMenu.add(expressionItem);
                     expressionItem.addActionListener(new ActionListener()
                     {
                     public void actionPerformed(final ActionEvent e)
                        {
                        // Decode the expression file
                        Hashtable savedRep = decodeFile(expressionFile);

                        // Loading expression
                        setCanvasRepresentation(savedRep, true);
                        }
                     });
                     /////////////////////////
                     }

                  saveFile(false);
                  }
               // reset the file name
               savedFileName = tempFileName;
               }
            });

      expressionMenu.add(saveExpression);

      fileMenuSeparator1 = new JSeparator();
      fileMenu.add(fileMenuSeparator1);
      exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
      exitMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               performQuitAction();
               }
            });
      exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, shortcutMask));
      fileMenu.add(exitMenuItem);

      // this is where default audio files are copied from the JAR to the filesystem
      copyAudioFile("audio1.wav");
      copyAudioFile("audio2.wav");
      copyAudioFile("beat1.wav");
      copyAudioFile("beat2.wav");
      copyAudioFile("bird.wav");
      copyAudioFile("cow.wav");
      copyAudioFile("crowd_ohh.wav");
      copyAudioFile("crying.wav");
      copyAudioFile("didgeridoo.wav");
      copyAudioFile("HAL.wav");
      copyAudioFile("heartmonitorbeep.wav");
      copyAudioFile("horse.wav");
      copyAudioFile("laugh1.wav");
      copyAudioFile("laugh2.wav");
      copyAudioFile("manwah.wav");
      copyAudioFile("pacman.wav");
      copyAudioFile("phonering.wav");
      copyAudioFile("phonesring.wav");
      copyAudioFile("policepass.wav");
      copyAudioFile("schoolbell.wav");
      copyAudioFile("snake.wav");
      copyAudioFile("song1.wav");
      copyAudioFile("tetris.wav");
      copyAudioFile("uhoh.wav");
      copyAudioFile("uhohcomputer.wav");
      copyAudioFile("underwater.wav");
      copyAudioFile("warning.wav");
      copyAudioFile("whales.wav");
      copyAudioFile("whistleshort.wav");
      copyAudioFile("yes.wav");

      boolean exists = (new File(OSpath)).exists();
      if (!exists)
         {
         boolean success = (new File(OSpath)).mkdirs();
         if (!success)
            {
            LOG.debug("Folder creation failed!");
            }
         }

      deleteExpression = new JMenuItem("Delete Expression");
      expressionMenu.add(deleteExpression);

      deleteExpression.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               //delete functions go here
               deleteFileName = "";

               while (deleteFileName.length() == 0)
                  {
                  //file list
                  File dir = new File(OSpath);
                  Object[] possibleValues = dir.list();
                  // get the exp file name from the user
                  deleteFileName = (String)JOptionPane.showInputDialog(
                        null,
                        "Expression name:",
                        "Delete Expression",
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        possibleValues,
                        possibleValues[0]);

                  if (deleteFileName == null)
                     {
                     break;
                     }
                  else
                     {
                     if (!deleteFileName.toLowerCase().endsWith(".xml"))
                        {
                        deleteFileName += ".xml";
                        }
                     //do deletion
                     boolean success = (new File(OSpath + deleteFileName)).delete();

                     if (!success)
                        {
                        LOG.debug("DELETE failed!");
                        }
                     else
                        {
                        LOG.debug("DELETE success:" + deleteFileName);
                        deleteExpressionFromMenu(deleteFileName);
                        }
                     }
                  }
               }
            });

      expressionsMenuSeparator1 = new JSeparator();
      expressionMenu.add(expressionsMenuSeparator1);

      File dir = new File(OSpath);//set the directory
      String[] files = dir.list();//file list to an array

      for (int i = 0; i < files.length; i++)
         {
         String fullFilename = files[i];
         //this concatenates the detected path with the filesname using an OS specific delimiter
         final File expressionFile = new File(OSpath + fullFilename);
         expressionItem = new JMenuItem(expressionFile.getName());
         expressionMenu.add(expressionItem);
         expressionItem.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            LOG.debug("FilePath is " + expressionFile.getAbsolutePath());
            LOG.debug("Filename is " + expressionFile.getName());

            // Decode the expression file
            Hashtable savedRep = decodeFile(expressionFile);

            // Loading expression
            setCanvasRepresentation(savedRep, true);
            }
         });
         }

      menuBar.add(optionsMenu);

      // Help Menu
      helpMenu = new JMenu();
      menuBar.add(helpMenu);
      helpMenu.setText("Help");
      helpMenuItem = new JMenuItem("Help");
      helpMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
      helpMenuItem.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               JFrame.setDefaultLookAndFeelDecorated(true);

               // Create and set up the window.
               final JFrame frame = new JFrame("Help");
               final JPanel newContentPane = new JPanel(new BorderLayout());

               // Create the help text editor
               final JEditorPane editorPane = new JEditorPane();
               editorPane.setEditable(false);
               editorPane.setOpaque(true);
               final URL helpURL = RobotUniversalRemote.class.getResource("RUR_Help.html");
               if (helpURL != null)
                  {
                  try
                     {
                     editorPane.setPage(helpURL);
                     }
                  catch (IOException ex)
                     {
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error("Attempted to read a bad URL: " + helpURL);
                        }
                     }
                  }
               else
                  {
                  LOG.error("Couldn't find help file.");
                  }

               // Put the editor pane in a scroll pane.
               final JScrollPane editorScrollPane = new JScrollPane(editorPane);
               editorScrollPane.setPreferredSize(new Dimension(400, 300));
               editorScrollPane.setMinimumSize(new Dimension(250, 145));

               // Add the help text to the content pane
               newContentPane.add(editorScrollPane, BorderLayout.CENTER);
               newContentPane.setOpaque(true);
               frame.setContentPane(newContentPane);

               // Move the help out of the corner
               frame.setLocation(100, 100);

               //Display the window.
               frame.pack();
               frame.setVisible(true);
               }
            });

      helpMenu.add(helpMenuItem);
      }

   /**
    * Copy an audio file from the JAR to the user's filesystem
    */
   private void copyAudioFile(final String audioFilename)
      {
      BufferedInputStream inputStream = null;
      BufferedOutputStream outputStream = null;
      try
         {
         // set up the input stream
         inputStream = new BufferedInputStream(RobotUniversalRemote.class.getResourceAsStream(audioFilename));

         // set up the output stream
         final File outDirectory = new File(audioPath);
         outDirectory.mkdirs();// make sure the audio directory exists
         final File outFile = new File(outDirectory, audioFilename);
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
         LOG.error("Could not create the audio output file", e);
         }
      catch (final IOException e)
         {
         LOG.error("IOException while reading or writing the audio file", e);
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

   /**
    * Initialize the toolbox part of the GUI.
    * @param toolboxPane The container to insert the toolbox into.
    */
   private void initGUIToolbox(final Container toolboxPane)
      {
      LOG.debug("RobotUniversalRemote.initGUIToolbox()");
      // Setup the model and view
      mToolboxModel = new DefaultGraphModel();
      mToolboxView = new GraphLayoutCache(mToolboxModel, new DefaultCellViewFactory());

      // Setup the graph
      mToolboxGraph = new JGraph(mToolboxModel, mToolboxView);
      mToolboxGraph.setPreferredSize(new Dimension((int)(TOOLBOX_ITEM_WIDTH + 2 * (TOOLBOX_ITEM_SPACING)),
                                                   (int)((TOOLBOX_ITEM_HEIGHT + 2 * TOOLBOX_ITEM_SPACING) * 4)));

      // Add some cells to our toolbox
      initGUIToolboxItem(mToolboxGraph,
                         new MotorCell(MotorCell.getDefaultName()), MotorCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new ServoCell(ServoCell.getDefaultName()), ServoCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new AnalogInputCell(AnalogInputCell.getDefaultName()), AnalogInputCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new DigitalInCell(DigitalInCell.getDefaultName()), DigitalInCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new DigitalOutCell(DigitalOutCell.getDefaultName()), DigitalOutCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new LEDCell(LEDCell.getDefaultName()), LEDCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new AudioCell(AudioCell.getDefaultName()), AudioCell.getColor());
      initGUIToolboxItem(mToolboxGraph,
                         new CameraCell(CameraCell.getDefaultName()), CameraCell.getColor());

      mToolboxGraph.setEnabled(true);
      mToolboxGraph.setDragEnabled(false);// drag is only enabled when we're connected to a qwerk
      mToolboxGraph.setTransferHandler(new ToolboxTransferHandler());

      // Clear the selection
      mToolboxGraph.getSelectionModel().clearSelection();

      // Setup the look of the graph
      mToolboxGraph.setAntiAliased(true);
      mToolboxGraph.setBackground(TOOLBOX_BACKGROUND_COLOR);

      mToolboxGraph.setAutoResizeGraph(false);

      final JPanel bottomToolboxButtons = new JPanel(new SpringLayout());

      // Set a nice border
      bottomToolboxButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

      emergencyStopButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               // stopServos and stopMotors may be deprecated depending
               //  on the version of the Qwerk API used
               final ServoService servos = getQwerkController().getServoService();
               servos.stopServos();

               final BackEMFMotorService motors = getQwerkController().getMotorService();
               motors.stopMotors();

               // TODO:
               // Call controlPanel.emergencyStopIssued() on each control panel visible

               // Display notification
               JOptionPane.showMessageDialog(null,
                                             "All motors and servos stopped.", "Emergency Stop",
                                             JOptionPane.INFORMATION_MESSAGE);
               }
            });

      final JPanel toolboxButtons = new JPanel(new SpringLayout());
      toolboxButtons.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
      toolboxButtons.add(getConnectDisconnectButton());
      toolboxButtons.add(emergencyStopButton);
      SpringLayoutUtilities.makeCompactGrid(toolboxButtons,
                                            2, 1, // rows, cols
                                            5, 10, // initX, initY
                                            5, 20);// xPad, yPad

      // Add the buttons and the graph to our toolbox
      final JPanel toolbox = new JPanel(new SpringLayout());
      toolbox.add(toolboxButtons);
      toolbox.add(new JScrollPane(mToolboxGraph));
      SpringLayoutUtilities.makeCompactGrid(toolbox,
                                            2, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      // Add the JPanel to our toolbox
      toolboxPane.add(toolbox, BorderLayout.LINE_START);
      }

   /**
    * Create the JGraph cell that gets inserted into the toolbox.
    * @param graph The JGraph to insert the cell into.
    * @param component The component to insert.
    * @param color The color of the box that represents the item.
    */
   private void initGUIToolboxItem(final JGraph graph, final DefaultGraphCell component, final Color color)
      {
      LOG.debug("RobotUniversalRemote.initGUIToolboxItem()");

      final int numberItems = graph.getModel().getRootCount();
      final double y = TOOLBOX_ITEM_START_Y + (numberItems * (TOOLBOX_ITEM_HEIGHT + TOOLBOX_ITEM_SPACING));

      // Setup the cell
      GraphConstants.setBounds(component.getAttributes(),
                               new Rectangle2D.Double(TOOLBOX_ITEM_START_X, y, TOOLBOX_ITEM_WIDTH, TOOLBOX_ITEM_HEIGHT));
      GraphConstants.setGradientColor(component.getAttributes(), color);
      GraphConstants.setOpaque(component.getAttributes(), true);
      GraphConstants.setBorder(component.getAttributes(), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

      GraphConstants.setSizeable(component.getAttributes(), false);
      GraphConstants.setMoveable(component.getAttributes(), false);
      GraphConstants.setEditable(component.getAttributes(), false);

      graph.getGraphLayoutCache().insert(component);
      }

   /**
    * Initialize the canvas part of the GUI.
    * @param canvasPane The container to insert the canvas into.
    */
   private void initGUICanvas(final Container canvasPane)
      {
      LOG.debug("RobotUniversalRemote.initGUICanvas()");

      // Setup the graph
      mCanvasGraph = new JGraph();

      // Enable dropping
      mCanvasGraph.setDropEnabled(true);
      mCanvasGraph.setTransferHandler(new ToolboxTransferHandler());

      // Setup the look of the graph
      //mCanvasGraph.setAntiAliased(true);
      mCanvasGraph.setGridVisible(true);
      mCanvasGraph.setGridColor(GRID_COLOR);
      mCanvasGraph.setGridSize(GRID_SPACING);
      mCanvasGraph.setGridMode(JGraph.LINE_GRID_MODE);

      // Add the mouse listener which looks for 'new component' clicks
      mCanvasGraph.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               final QwerkBoardPortCell cell = (QwerkBoardPortCell)mQwerkBoardCell.cellAtPoint(e.getPoint());

               if (cell != null && getQwerkController() != null)
                  {
                  addQwerkComponent(e.getPoint(), cell);
                  }
               }
            });

      // Setup the rollover for board components
      drawingHighlightCell = false;

      mCanvasGraph.addMouseMotionListener(
            new MouseMotionAdapter()
            {
            public void mouseMoved(final MouseEvent e)
               {
               final QwerkBoardPortCell cell = (QwerkBoardPortCell)mQwerkBoardCell.cellAtPoint(e.getPoint());

               // If we're over a cell and haven't already drawn this highlight cell
               if (cell != null && !drawingHighlightCell)
                  {
                  // Set a highlight on this cell
                  final Map nested = new Hashtable();
                  final Map attributesMap = cell.getAttributes();

                  GraphConstants.setBorder(attributesMap,
                                           BorderFactory.createRaisedBevelBorder());

                  nested.put(cell, attributesMap);

                  mCanvasGraph.getGraphLayoutCache().edit(nested);

                  // Keep track of which cell we changed so we can revert it later
                  highlightCell = cell;
                  drawingHighlightCell = true;
                  }
               else if (cell == null && drawingHighlightCell)
                  {
                  // Remove the highlight on the cell

                  final Map nested = new Hashtable();
                  final Map am = highlightCell.getAttributes();

                  GraphConstants.setBorder(
                        highlightCell.getAttributes(),
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

                  nested.put(highlightCell, am);

                  mCanvasGraph.getGraphLayoutCache().edit(nested);

                  highlightCell = null;
                  drawingHighlightCell = false;
                  }
               }
            });

      // TODO: Use a scroll panel
      mCanvasGraph.setBounds(0, 0, 1600, 1200);

      //Create layered pane to add graph and all control panels to
      layeredPane = new JLayeredPane();

      // Add the layered pane to the canvas pane, passed as a param
      canvasPane.add(layeredPane, BorderLayout.CENTER);

      // Disable the canvas pane until the user selects 'New'
      canvasPane.setEnabled(false);

      LOG.debug("RobotUniversalRemote.initGUICanvas() is almost done, now calling newFile()...");
      newFile();
      }

   /**
    * Sets up a JGraph to a default configuration.
    * Useful for 'New' operations.
    */
   private void setupNewCanvasGraph(final Point2D qwerkPosition)
      {
      LOG.debug("RobotUniversalRemote.setupNewCanvasGraph()");

      // Create the parent cell and add it to the graph (along with its children)
      mCanvasModel = new DefaultGraphModel();
      mCanvasView = new GraphLayoutCache(mCanvasModel, new DefaultCellViewFactory());

      mCanvasGraph.setModel(mCanvasModel);
      mCanvasGraph.setGraphLayoutCache(mCanvasView);

      mQwerkBoardCell = new QwerkCell(qwerkPosition);

      mCanvasGraph.getGraphLayoutCache().insertGroup(mQwerkBoardCell, mQwerkBoardCell.getChildren().toArray());

      // Reset the current layered pane
      layeredPane.removeAll();

      // Add the graph to the layered pane
      layeredPane.add(mCanvasGraph, JLayeredPane.DEFAULT_LAYER);

      //call update to repaint the window
      update(getGraphics());
      }

   /**
    * Add a component to the Qwerk board.
    * @param p The point where the component should be inserted.
    * @param qwerkPort The port that the component should be connected to.
    */
   public void addQwerkComponent(final Point p, final QwerkBoardPortCell qwerkPort)
      {
      // Get the class type that this port can connect to
      final Class correctPortClass = ((QwerkPortInfo)(qwerkPort.getUserObject())).connectionClass();
      final DefaultCell newCell;
      final AbstractControlPanel controlPanel;
      final int deviceId = qwerkPort.getDeviceId();
      final Hashtable values = qwerkPort.getValues();

      try
         {
         // Create a new cell class of the correct type
         // (the port knows which kind of cell to create)
         newCell = (DefaultCell)correctPortClass.newInstance();

         if (!values.isEmpty())
            {
            newCell.setValues(values);
            }
         }
      catch (Exception ex)
         {
         LOG.error("Exception in addQwerkComponent()", ex);
         return;
         }

      newCell.setAdvancedHook(advancedMode);
      // Create the new control panel
      controlPanel = newCell.createControlPanel(getGUIClientHelper(), getQwerkController(), deviceId);

      //
      // ---------------
      // Update the JGraph

      GraphConstants.setSizeable(newCell.getAttributes(), false);
      GraphConstants.setMoveable(newCell.getAttributes(), false);
      GraphConstants.setEditable(newCell.getAttributes(), false);

      // Temporary size until we resize it later
      GraphConstants.setBounds(newCell.getAttributes(),
                               new Rectangle2D.Double(p.getX(), p.getY(), TOOLBOX_ITEM_WIDTH, TOOLBOX_ITEM_HEIGHT));
      GraphConstants.setOpaque(newCell.getAttributes(), true);
      GraphConstants.setOpaque(newCell.getAttributes(), true);
      GraphConstants.setBorder(newCell.getAttributes(), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

      // Setup the connection to the Qwerk board

      // Add a port to this new cell
      final DefaultPort port0 = new DefaultPort();
      newCell.add(port0);

      // Create the new edge and set its source and target
      final DefaultEdge connectionToQwerk = new DefaultEdge();

      // Source = new port just created
      // Target = one port on qwerk board
      connectionToQwerk.setSource(port0);

      connectionToQwerk.setTarget(qwerkPort.getChildAt(0));

      // TODO: May be unneeded with merged command/control
      newCell.setDeviceId(deviceId);

      GraphConstants.setDisconnectable(connectionToQwerk.getAttributes(), false);

      // Insert the new component in the graph
      mCanvasGraph.getGraphLayoutCache().insert(newCell);

      // Add the connection to the Qwerk board
      mCanvasGraph.getGraphLayoutCache().insert(connectionToQwerk);

      // Ensure that the dropped cell is selected
      mCanvasGraph.setSelectionCell(newCell);

      // ---------------------
      // Add the control panel

      // Common to all control panels - Layout ControlPanel based on where the cell is
      final AttributeMap am = (newCell.getAttributes());
      final Object obj = am.get("bounds");
      final SerializableRectangle2D rect = (SerializableRectangle2D)obj;
      rect.setWidth(controlPanel.getPreferredSize().width);
      rect.setHeight(controlPanel.getPreferredSize().height);

      final Point pt = new Point((int)rect.x, (int)rect.y);

      controlPanel.setBounds(
            pt.x,
            pt.y,
            controlPanel.getPreferredSize().width,
            controlPanel.getPreferredSize().height);
      controlPanel.setOpaque(true);

      final JInternalFrame jif = new JInternalFrame(newCell.getDescription(), true, true);
      // Add to the layered pane

      jif.setLayout(new BorderLayout());
      jif.setBounds(
            pt.x,
            pt.y,
            controlPanel.getPreferredSize().width + 40,
            controlPanel.getPreferredSize().height + 40);
      jif.setOpaque(true);
      jif.add(controlPanel);
      jif.setContentPane(controlPanel);
      jif.setTitle(controlPanel.getTitle());
      jif.pack();
      jif.setVisible(true);

      // NOTE: for some reason this setBorder call causes the title bar to not appear under Mac OS X.  Weird.
      //jif.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

      //Add QwerkInternalFrameListener to listen for closing event, to remove the component
      jif.addInternalFrameListener(new QwerkInternalFrameListener(jif, newCell, mCanvasModel));

      jif.addComponentListener(new ComponentAdapter()
      {
      public void componentMoved(final ComponentEvent e)
         {
         try
            {
            final JInternalFrame c = (JInternalFrame)e.getComponent();
            final AbstractControlPanel panel = (AbstractControlPanel)c.getRootPane().getContentPane();
            final Rectangle2D newRect = c.getBounds();

            final Map nested = new Hashtable();
            final DefaultCell cell = panel.getGraphCell();
            final Map am = cell.getAttributes();

            GraphConstants.setBounds(am, newRect);

            nested.put(cell, am);

            mCanvasGraph.getGraphLayoutCache().edit(nested);
            mCanvasGraph.invalidate();

            c.moveToFront();
            }
         catch (Exception ex)
            {
            LOG.error("Exception in componentMoved()", ex);
            }
         }
      });

      layeredPane.add(jif, JLayeredPane.PALETTE_LAYER);

      layeredPane.moveToFront(jif);
      }

   /**
    * Create a new configuration for the Qwerk.
    * NOTE: The connection must be active before calling this.
    *
    */
   public void newFile()
      {
      LOG.debug("RobotUniversalRemote.newFile()");

      configurePanel.setEnabled(true);

      setupNewCanvasGraph(new Point2D.Double(QWERK_BOARD_X, QWERK_BOARD_Y));
      }

   /**
    * Method to encode and save xml representation of the GraphModel.
    * @param saveAs Set to true if this is a 'saveAs' operation. 'saveAs' always shows the chooser, where a regular save will use the last known file name if set.
    */
   public void saveFile(final boolean saveAs)
      {

      if (saveAs || "".equals(savedFileName))
         {
         initFileChooser();

         final int returnValue = fileChooser.showSaveDialog(mCanvasGraph);
         if (returnValue == JFileChooser.APPROVE_OPTION)
            {
            String tmpFileName = fileChooser.getSelectedFile().getAbsolutePath();
            if (!tmpFileName.toLowerCase().endsWith(".xml"))
               {
               tmpFileName += ".xml";
               }
            final File saveFile = new File(tmpFileName);

            if (saveFile.exists())
               {
               // Verify that the user wants to overwrite this file
               final int overwriteResult = JOptionPane.showConfirmDialog(this,
                                                                         "The file chosen will be overwritten. Are you sure you wish to overwrite this file?",
                                                                         "Overwrite File",
                                                                         JOptionPane.YES_NO_OPTION,
                                                                         JOptionPane.WARNING_MESSAGE);

               if (overwriteResult == JOptionPane.NO_OPTION)
                  {
                  // Cancel
                  return;
                  }
               }

            fileChooser.setSelectedFile(saveFile);
            savedFileName = tmpFileName;
            }
         else
            {
            // Cancel of save as dialog
            return;
            }
         }

      try
         {
         final XMLEncoder encoder;

         final Hashtable savedRep = getCanvasRepresentation();

         encoder = new XMLEncoder(new BufferedOutputStream(
               new FileOutputStream(savedFileName)));
         encoder.writeObject(savedRep);
         encoder.close();
         }
      catch (Exception e)
         {
         JOptionPane.showMessageDialog(mCanvasGraph, e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         }
      }

   /**
    * Method to deserialize xml representation of GraphModel and set it as the Model of the Canvas
    *
    */
   public void openFile(final boolean isExpression)
      {
      initFileChooser();
      final int returnValue = fileChooser.showOpenDialog(mCanvasGraph);
      if (returnValue == JFileChooser.APPROVE_OPTION)
         {
         final File filename = fileChooser.getSelectedFile();
         final Hashtable savedRep = decodeFile(filename);
         setCanvasRepresentation(savedRep, isExpression);
         }
      }

   /**
    * A utility method to extract all fields from the xml file
    * created by XMLEncoder to the Hashtable.
    *
    * @param filename The xml file created by XMLEncoder
    * @return a Hashtable of all fields in the xml file
    */
   private Hashtable decodeFile(final File filename)
      {
      try
         {
         final XMLDecoder decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(filename)));

         return (Hashtable)decoder.readObject();
         }
      catch (FileNotFoundException e)
         {
         JOptionPane.showMessageDialog(mCanvasGraph, e.getMessage(), "Error",
                                       JOptionPane.ERROR_MESSAGE);
         return null;
         }
      }

   /**
    * Utility method that ensures the file chooser is created. Start-up time
    * is improved by lazily instaniating choosers.
    * Taken from the JGraph Examples
    */
   private void initFileChooser()
      {
      if (fileChooser == null)
         {
         fileChooser = new JFileChooser();
         final FileFilter fileFilter = new FileFilter()
         {
         /**
          * @see FileFilter#accept(File)
          */
         public boolean accept(final File f)
            {
            if (f == null)
               {
               return false;
               }
            if (f.getName() == null)
               {
               return false;
               }
            if (f.getName().endsWith(".xml"))
               {
               return true;
               }
            if (f.isDirectory())
               {
               return true;
               }

            return false;
            }

         /**
          * @see FileFilter#getDescription()
          */
         public String getDescription()
            {
            return "XML file (.xml)";
            }
         };
         fileChooser.setFileFilter(fileFilter);
         }
      }

   /**
    * Returns one object which contains enough information to reconstruct the current layout.
    * Used for serialization.
    */
   private Hashtable getCanvasRepresentation()
      {

      // In the future, it would be nice to store the model from the JGraph directly
      //  but due to difficulties with that, this will store enough info to
      //  reconstruct the current configuration.

      // Need to store:
      //  1. Position of Qwerk board
      //  2. Information about each component
      //    a. Position
      //    b. Type
      //    c. Port connection

      final Hashtable storage = new Hashtable();

      final ArrayList components = new ArrayList();
      Hashtable oneComponent;

      // Overall info
      storage.put("rur_version", RUR_VERSION);

      // Get info about the Qwerk board and put it in the storage
      final Point2D qwerkBoardStart = mQwerkBoardCell.getBaseCellPosition();

      storage.put("qwerk_controller_start_x", qwerkBoardStart.getX());
      storage.put("qwerk_controller_start_y", qwerkBoardStart.getY());

      // Get info about each component and put it in the component list
      final Object[] cells;

      cells = mCanvasGraph.getGraphLayoutCache().getCells(true, true, true, true);

      for (Object c : cells)
         {
         if (c instanceof DefaultCell)
            {
            // This cell represents a component
            final DefaultCell cell = (DefaultCell)c;
            oneComponent = new Hashtable();

            oneComponent.put("bounds", GraphConstants.getBounds(cell.getAttributes()));
            oneComponent.put("device_id", cell.getDeviceId());
            oneComponent.put("class", cell.getClass());

            // For expressions
            // Put all the values of the cells into the oneComponent hashtable.
            Hashtable values = cell.getValues();
            if (values != null)
               {
               oneComponent.putAll(values);
               }

            // Add this component to our list
            components.add(oneComponent);
            }
         }

      // Add the component list to our storage
      storage.put("components", components);

      return storage;
      }

   /**
    * Reconstructs the canvas from a representation of a Qwerk board.
    * Used for de-serialization.
    */
   private void setCanvasRepresentation(final Hashtable hash, final boolean isExpression)
      {

      // Move the qwerk cell to the proper place
      final double x;
      final double y;

      x = ((Number)hash.get("qwerk_controller_start_x")).doubleValue();
      y = ((Number)hash.get("qwerk_controller_start_y")).doubleValue();

      final Point2D qwerkBoardStart = new Point2D.Double(x, y);

      // Reset the current graph
      setupNewCanvasGraph(qwerkBoardStart);

      // Add all of the components
      final ArrayList components = (ArrayList)hash.get("components");

      Rectangle2D audioBounds = null;
      QwerkBoardPortCell audioQwerkPort = null;

      for (Object o : components)
         {
         final Hashtable oneComponent = (Hashtable)o;

         final int deviceId = (Integer)oneComponent.get("device_id");
         final Class componentType = (Class)oneComponent.get("class");
         final Rectangle2D bounds = (Rectangle2D)oneComponent.get("bounds");

         // Figure out which port this item should be connected to
         final QwerkBoardPortCell qwerkPort = mQwerkBoardCell.cellWithDeviceIdAndType(deviceId, componentType);

         // If Audio, save it to the last
         if (componentType.getName().equals(AudioCell.class.getName()))
            {
            audioBounds = bounds;
            audioQwerkPort = qwerkPort;
            if (isExpression)
               {
               audioQwerkPort.setValues(oneComponent);
               }

            continue;
            }

         if (qwerkPort == null)
            {
            // TODO: Handle de-serialization error
            return;
            }

         // Only load the values if it is an expression.
         if (isExpression)
            {
            qwerkPort.setValues(oneComponent);
            }

         // Add this component
         addQwerkComponent(new Point((int)bounds.getX(), (int)bounds.getY()), qwerkPort);

         oneComponent.clear();
         }
      // Play Audio last
      if (audioQwerkPort != null)
         {
         addQwerkComponent(new Point((int)audioBounds.getX(), (int)audioBounds.getY()), audioQwerkPort);
         }
      }
   }
