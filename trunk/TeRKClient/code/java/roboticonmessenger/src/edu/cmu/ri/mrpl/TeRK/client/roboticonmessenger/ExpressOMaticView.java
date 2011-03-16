package edu.cmu.ri.mrpl.TeRK.client.roboticonmessenger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandler;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.Sequence;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequencePlayer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceTransition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.SwingConstants;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd.StepTransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.AbstractListCellRenderer;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.list.StepCellRenderer;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerModel;
import edu.cmu.ri.mrpl.swing.DragAndDropJList;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Logger;

public class ExpressOMaticView
   {

   private static final Logger LOG = Logger.getLogger(ExpressOMaticView.class);
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/expressomatic/Express-O-Matic.direct-connect.ice.properties";

   private JPanel expressoMaticPanel = null;//  @jve:decl-index=0:visual-constraint="137,26"

   private JLabel composeSequenceLabel = null;

   private JButton connectButton = null;

   private JButton playButton = null;

   private JButton saveAsButton = null;

   private JButton saveButton = null;

   private JButton newButton = null;

   private JPanel innerPanel = null;

   private DragAndDropJList stepList = null;

   private SequencePlayer sequencePlayer = null;

   private JPanel stepPropertiesPanel = null;

   private Sequence sequence = null;

   private File sequenceFile = null;//  @jve:decl-index=0:

   private SequenceFileHandler sequenceFileHandler = null;//  @jve:decl-index=0:
   private File sequencesDirectory = null;
   private File expressionsDirectory = null;
   private File publicRoboticonDirectory = null;

   // So we know whether the user selects an item in stepList
   // or if it's selected from the sequence being played
   private boolean userSelection = true;
   private RoboticonMessenger parent = null;

   private JPanel jPanel_ExpressOMaticViewNorth = null;

   private GUIClientHelper guiHelper = null;
   private GUIClientHelperEventHandler guiHelperEventHandler = null;
   private JPanel connectStatePanel = null;
   public static String expressionsPath = null;
   public static String sequencesPath = null;

   public ExpressOMaticView(final RoboticonMessenger parentComponent)
      {
      parent = parentComponent;

      guiHelperEventHandler = new GUIClientHelperEventHandlerAdapter()
      {
      public void toggleGUIElementState(final boolean isConnectedToPeer)
         {
         setEnabled(isConnectedToPeer);
         }
      };

      guiHelper = new GUIClientHelper(RoboticonMessenger.APPLICATION_NAME,
                                      RoboticonMessenger.ICE_RELAY_PROPERTIES_FILE,
                                      ICE_DIRECT_CONNECT_PROPERTIES_FILE, parent,
                                      guiHelperEventHandler);

      expressionsPath = RoboticonManagerModel.TERK_PATH + "Expressions" + File.separator;
      sequencesPath = RoboticonManagerModel.TERK_PATH + "Sequences" + File.separator;

      sequencesDirectory = new File(sequencesPath);
      expressionsDirectory = new File(expressionsPath);
      publicRoboticonDirectory = new File(RoboticonManagerModel.publicRoboticonPath);
      if (!sequencesDirectory.exists())
         {
         sequencesDirectory.mkdirs();
         }
      if (!expressionsDirectory.exists())
         {
         expressionsDirectory.mkdirs();
         }
      if (!publicRoboticonDirectory.exists())
         {
         publicRoboticonDirectory.mkdirs();
         }
      getExpressoMaticPanel();
      }

   /**
    * This method initializes expressoMaticPanel
    *
    * @return javax.swing.JPanel
    */
   public JPanel getExpressoMaticPanel()
      {
      if (expressoMaticPanel == null)
         {
         sequence = new Sequence();
         composeSequenceLabel = new JLabel();
         composeSequenceLabel.setText("Compose Sequence");
         composeSequenceLabel.setBounds(new Rectangle(3, 4, 126, 19));
         expressoMaticPanel = new JPanel();
         expressoMaticPanel.setLayout(new BorderLayout());
         expressoMaticPanel.setSize(new Dimension(300, 411));
         expressoMaticPanel.add(getJPanel_ExpressOMaticViewNorth(), BorderLayout.NORTH);
         expressoMaticPanel.add(sequencePanel(), BorderLayout.CENTER);
         sequenceFileHandler = SequenceFileHandler.getInstance();
         sequenceFile = null;
         AbstractListCellRenderer.loadExpressionImages(expressionsPath);
         }
      return expressoMaticPanel;
      }

   /**
    * This method initializes connectButton
    *
    * @return javax.swing.JButton
    */
   private JButton getConnectButton()
      {
      if (connectButton == null)
         {
         connectButton = guiHelper.getConnectDisconnectButton();
         connectButton.setBounds(new Rectangle(131, 4, 153, 20));
         }
      return connectButton;
      }

   /**
    * This method initializes playButton
    *
    * @return javax.swing.JButton
    */
   private JButton getPlayButton()
      {
      if (playButton == null)
         {
         playButton = new JButton();
         playButton.setText("Play");
         playButton.setBounds(new Rectangle(2, 60, 68, 20));
         }
      return playButton;
      }

   /**
    * This method initializes saveAsButton
    *
    * @return javax.swing.JButton
    */
   private JButton getSaveAsButton()
      {
      if (saveAsButton == null)
         {
         saveAsButton = new JButton();
         saveAsButton.setText("Save As");
         saveAsButton.setLocation(new Point(146, 60));
         saveAsButton.setSize(new Dimension(81, 20));

         saveAsButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent event)
            {
            SaveAs();
            }
         });
         }
      return saveAsButton;
      }

   /**
    * This method initializes saveButton
    *
    * @return javax.swing.JButton
    */
   private JButton getSaveButton()
      {
      if (saveButton == null)
         {
         saveButton = new JButton();
         saveButton.setText("Save");
         saveButton.setLocation(new Point(231, 60));
         saveButton.setSize(new Dimension(62, 20));

         saveButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent event)
            {
            Save();
            }
         });
         }
      return saveButton;
      }

   /**
    * This method initializes newButton
    *
    * @return javax.swing.JButton
    */
   private JButton getNewButton()
      {
      if (newButton == null)
         {
         newButton = new JButton();
         newButton.setText("Clear");
         newButton.setLocation(new Point(74, 60));
         newButton.setSize(new Dimension(68, 20));
         newButton.addActionListener(new java.awt.event.ActionListener()
         {
         public void actionPerformed(java.awt.event.ActionEvent e)
            {
            NewSequence();
            }
         });
         }
      return newButton;
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
      sequencePanel.add(topPanel);

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

         try
            {
            /* this section is slowing things down and causing null pointer exceptions */
            if (sequenceFile == null)
               {
               sequenceFile = (File)stepList.getClientProperty("RoboticonFile");
               if (sequence != null)
                  {
                  sequence.setLoopBackToStart((Boolean)stepList.getClientProperty("loop"));
                  }
               stepList.putClientProperty("RoboticonFile", null);
               stepList.putClientProperty("loop", false);
               }
            }
         catch (NullPointerException e)
            {
            LOG.debug("mysterious NullPointerException in expressomaticview sequencePanel valueChanged()");
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
      //		scrollPane.setMinimumSize(new Dimension(1, SwingConstants.STEP_CELL_HEIGHT + 4));
      //		scrollPane.setPreferredSize(new Dimension(SwingConstants.MAIN_WINDOW_WIDTH
      //				- SwingConstants.PALLET_WIDTH, SwingConstants.STEP_CELL_HEIGHT + 4));
      //scrollPane.setMinimumSize(new Dimension(1, SwingConstants.STEP_CELL_HEIGHT + 4));
      scrollPane.setPreferredSize(new Dimension(SwingConstants.STEP_CELL_HEIGHT + 4,
                                                SwingConstants.MAIN_WINDOW_WIDTH - SwingConstants.PALLET_WIDTH));
      sequencePanel.add(scrollPane);

      sequencePanel.add(stepPropertiesPanel());
      SpringLayoutUtilities.makeCompactGrid(sequencePanel, 3, 1, 0, 0, 0, 0);
      return sequencePanel;
      }

   private JPanel sequenceControlPanel()
      {
      final JPanel controlPanel = new JPanel(new SpringLayout());

      //sequencePlayButton = new JButton("Play");
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
      };

      playButton.addActionListener(new ActionListener()
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
            playButton.setText("Stop");

            stepList.setDragEnabled(false);
            stepPropertiesPanel.setEnabled(false);

            final Thread sequenceThread = new Thread()
            {
            public void run()
               {
               togglePlaying(true);
               sequencePlayer.playSequence(guiHelper.getQwerkController(),
                                           sequence);
               togglePlaying(false);

               playButton.setText("Play");
               stepList.setDragEnabled(true);
               stepPropertiesPanel.setEnabled(true);
               }
            };
            sequenceThread.start();
            }
         }
      });

      playButton.setEnabled(false);
      //controlPanel.add(sequencePlayButton);

      /*SpringLayoutUtilities.makeCompactGrid(controlPanel, 1, 1, // rows, cols
                  0, 0, // initX, initY
                  0, 0);// paddingX, paddingY
      */
      return controlPanel;
      }

   /**
    * This method initializes innerPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel stepPropertiesPanel()
      {

      innerPanel = new JPanel();
      //innerPanel.setLayout(new GridBagLayout());
      //innerPanel.setBounds(new Rectangle(36, 65, 194, 166));

      final JPanel expressionPanel = new JPanel(new FlowLayout(
            FlowLayout.LEFT));
      final JLabel expressionLabel = new JLabel();

      final String transitionText = "Wait Until:";
      final JPanel transitionPanel = new JPanel(new FlowLayout(
            FlowLayout.LEFT));
      final JLabel transitionLabel = new JLabel();
      final JButton removeConditionButton = new JButton("Remove");
      final JLabel transitionSecondsLabel = new JLabel("seconds");
      final JTextField transitionTimeField = new JTextField();
      final JCheckBox loopCheckBox = new JCheckBox("Loop back to beginning");

      final NumberFormat transitionTimeFormat = NumberFormat
            .getNumberInstance();
      transitionTimeFormat.setMaximumFractionDigits(1);
      transitionTimeFormat.setMinimumFractionDigits(1);
      transitionTimeFormat.setMinimumIntegerDigits(1);

      final JPanel speedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      //final JPanel speedPanel = new JPanel(new GridLayout(3,1));
      final JLabel speedLabel = new JLabel("Expression Speed:");
      final JComboBox speedComboBox = new JComboBox(new String[]{"Fast",
                                                                 "Medium", "Slow", "Custom"});
      final Hashtable<Integer, JLabel> velocityLabelDictionary = new Hashtable<Integer, JLabel>();
      final JSlider velocitySlider = new JSlider(
            ExpressionSpeed.MINIMUM_VELOCITY,
            ExpressionSpeed.MAXIMUM_VELOCITY);
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
         velocitySlider.setEnabled(isEnabled
                                   && "Custom".equals(speedComboBox.getSelectedItem()));

         deleteButton.setEnabled(isEnabled);
         }
      };

      final ActionListener transitionTimeChanged = new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final SequenceStep step = (SequenceStep)stepList
               .getSelectedValue();
         try
            {
            final float value = Float.parseFloat(transitionTimeField
                                                       .getText());
            step.getTransition().setSecondsToNextStep(value);
            transitionTimeField.setText(transitionTimeFormat
                                              .format(value));
            }
         catch (NumberFormatException e)
            {
            transitionTimeField
                  .setText(transitionTimeFormat
                                 .format(step.getTransition()
                                               .getMillisToNextStep() / 1000f));
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
         final SequenceStep step = (SequenceStep)stepList
               .getSelectedValue();
         try
            {
            final float value = Float.parseFloat(transitionTimeField
                                                       .getText());
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
            final SequenceStep step = (SequenceStep)stepList
                  .getSelectedValue();
            final SequenceTransition transition = step.getTransition();
            final ExpressionSpeed speed = step.getExpresisonSpeed();

            expressionLabel.setText("Expression: "
                                    + step.getExpression().getName());

            // This is the last step
            loopCheckBox.setSelected(sequence.getLoopBackToStart());

            if (selectedIndex < sequence.getSize() - 1
                || sequence.getLoopBackToStart())
               {
               if (transition.getCondition() != null)
                  {
                  transitionLabel.setText(transitionText + " "
                                          + transition.getCondition().getName());
                  removeConditionButton.setVisible(true);
                  transitionTimeField.setVisible(false);
                  transitionSecondsLabel.setVisible(false);
                  }
               else
                  {
                  transitionLabel.setText(transitionText);
                  removeConditionButton.setVisible(false);
                  transitionTimeField
                        .setText(transitionTimeFormat
                                       .format(transition
                                                     .getMillisToNextStep() / 1000f));
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
            velocityLabel.setText("Velocity: "
                                  + speed.getServoVelocity());
            innerPanel.setVisible(true);
            }
         }
      };

      innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
      innerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                                                                    .createTitledBorder("Step Properties"), BorderFactory
            .createEmptyBorder(5, 5, 5, 5)));

      expressionPanel.add(expressionLabel);

      removeConditionButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         final SequenceStep step = (SequenceStep)stepList
               .getSelectedValue();
         step.getTransition().setCondition(null);

         stepListListener.valueChanged(null);

         stepList.repaint();
         }
      });

      transitionTimeField.setColumns(3);
      transitionTimeField.addActionListener(transitionTimeChanged);
      transitionTimeField.getDocument().addDocumentListener(
            transitionTimeDocumentChanged);

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
         final ExpressionSpeed speed = ((SequenceStep)stepList
               .getSelectedValue()).getExpresisonSpeed();

         final String selection = (String)speedComboBox
               .getSelectedItem();
         if ("Fast".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.FAST_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: "
                                  + speed.getServoVelocity());
            velocityLabel.setEnabled(false);
            }
         else if ("Medium".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.MEDIUM_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: "
                                  + speed.getServoVelocity());
            velocityLabel.setEnabled(false);
            }
         else if ("Slow".equals(selection))
            {
            speed.setServoVelocity(ExpressionSpeed.SLOW_VELOCITY);
            velocitySlider.setValue(speed.getServoVelocity());
            velocitySlider.setEnabled(false);
            velocityLabel.setText("Velocity: "
                                  + speed.getServoVelocity());
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
      velocitySlider
            .setMajorTickSpacing((ExpressionSpeed.MAXIMUM_VELOCITY - ExpressionSpeed.MINIMUM_VELOCITY) / 2);
      velocitySlider.setPaintLabels(true);
      velocitySlider.setSize(100, 20);

      final JLabel minVelocityLabel = new JLabel(
            ExpressionSpeed.MINIMUM_VELOCITY + "");
      minVelocityLabel.setFont(GUIConstants.FONT_SMALL);
      velocityLabelDictionary.put(ExpressionSpeed.MINIMUM_VELOCITY,
                                  minVelocityLabel);
      final JLabel maxVelocityLabel = new JLabel(
            ExpressionSpeed.MAXIMUM_VELOCITY + "");
      maxVelocityLabel.setFont(GUIConstants.FONT_SMALL);
      velocityLabelDictionary.put(ExpressionSpeed.MAXIMUM_VELOCITY,
                                  maxVelocityLabel);
      velocitySlider.setLabelTable(velocityLabelDictionary);

      velocitySlider.addChangeListener(new ChangeListener()
      {
      public void stateChanged(final ChangeEvent event)
         {
         final ExpressionSpeed speed = ((SequenceStep)stepList
               .getSelectedValue()).getExpresisonSpeed();
         speed.setServoVelocity(velocitySlider.getValue());
         velocityLabel.setText("Velocity: " + velocitySlider.getValue());
         }
      });

      velocityPanel.setLayout(new BoxLayout(velocityPanel, BoxLayout.Y_AXIS));
      velocityPanel.setLayout(null);
      velocityPanel.setMinimumSize(velocitySlider.getPreferredSize());
      velocityPanel.setPreferredSize(velocitySlider.getPreferredSize());

      velocitySlider.setBounds(0, 0, (int)velocitySlider.getPreferredSize().getWidth(),
                               (int)velocitySlider.getPreferredSize().getHeight());
      velocityPanel.add(velocitySlider);
      velocityLabel.setText("Velocity: " + ExpressionSpeed.MAXIMUM_VELOCITY
                            + " ");// This is to get the maximum size of the label
      velocityLabel
            .setBounds(
                  (int)(velocityPanel.getPreferredSize().getWidth() - velocityLabel
                        .getPreferredSize().getWidth()) / 2,
                  (int)(velocityPanel.getPreferredSize().getHeight() - velocityLabel
                        .getPreferredSize().getHeight()),
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
      stepPropertiesPanel.setMinimumSize(new Dimension(300, 320));
      stepPropertiesPanel.setMaximumSize(new Dimension(300, 320));

      return stepPropertiesPanel;
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
    * This method is called when the sequencePlayer starts and stops playing
    * @param isPlaying - if the sequencePlayer is playing
    */
   protected void togglePlaying(final boolean isPlaying)
      {
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

   /**
    * Used for toggling GUI element state (such as setting an element as enabled/disabled) based on whether we're
    * connected to a qwerk.
    */
   public void setEnabled(final boolean isEnabled)
      {
      LOG.info("in expressomatic setEnabled()" + isEnabled);
      playButton.setEnabled(isEnabled);

      if (!isEnabled && sequencePlayer.isPlaying())
         {
         for (final ActionListener a : playButton.getActionListeners())
            {
            a.actionPerformed(new ActionEvent(playButton, 0, ""));
            }
         }
      }

   /**
    * This method initializes jPanel_ExpressOMaticViewNorth
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_ExpressOMaticViewNorth()
      {
      if (jPanel_ExpressOMaticViewNorth == null)
         {
         jPanel_ExpressOMaticViewNorth = new JPanel();
         jPanel_ExpressOMaticViewNorth.setLayout(null);
         jPanel_ExpressOMaticViewNorth.setPreferredSize(new Dimension(1, 85));
         jPanel_ExpressOMaticViewNorth.add(composeSequenceLabel, null);
         jPanel_ExpressOMaticViewNorth.add(getConnectButton(), null);
         jPanel_ExpressOMaticViewNorth.add(getPlayButton(), null);
         jPanel_ExpressOMaticViewNorth.add(getNewButton(), null);
         jPanel_ExpressOMaticViewNorth.add(getSaveAsButton(), null);
         jPanel_ExpressOMaticViewNorth.add(getSaveButton(), null);
         jPanel_ExpressOMaticViewNorth.add(getConnectStatePanel(), null);
         }
      return jPanel_ExpressOMaticViewNorth;
      }

   /**
    * This method initializes connectStatePanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getConnectStatePanel()
      {
      if (connectStatePanel == null)
         {
         connectStatePanel = guiHelper.getConnectionStatePanel();
         connectStatePanel.setBounds(new Rectangle(4, 28, 253, 25));
         }
      return connectStatePanel;
      }

   public void Open()
      {
      final JFileChooser fileChooser = new JFileChooser(
            sequencesPath);
      final int returnValue = fileChooser
            .showOpenDialog(getExpressoMaticPanel());

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
            LOG.error("Ignoring file [" + filename.getAbsolutePath()
                      + "] because openFile() threw an exception", e);
            }

         if (newSequence != null)
            {
            sequenceFile = filename;
            sequence = newSequence;
            stepList.setModel(sequence);
            }
         else
            {
            sequenceFile = null;
            }

         stepList.repaint();
         }
      }

   public void NewSequence()
      {
      sequenceFile = null;
      sequence = new Sequence();
      stepList.setModel(sequence);
      stepList.repaint();
      }

   public void Save()
      {
      if (sequenceFile == null)
         {
         SaveAs();
         }
      else
         {
         sequenceFileHandler.saveFile(sequence, sequenceFile);
         }
      }

   public void SaveAs()
      {
      final JFileChooser fileChooser = new JFileChooser(sequencesPath);
      final int returnValue = fileChooser.showSaveDialog(getExpressoMaticPanel());

      if (returnValue == JFileChooser.APPROVE_OPTION)
         {
         String tmpFileName = fileChooser.getSelectedFile()
               .getAbsolutePath();
         if (!tmpFileName.toLowerCase().endsWith(".xml"))
            {
            tmpFileName += ".xml";
            }
         final File saveFile = new File(tmpFileName);

         if (sequenceFileHandler.saveFile(sequence, saveFile))
            {
            sequenceFile = saveFile;
            }
         }
      }
   }
