package edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.Condition;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.Sequence;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequencePlayer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceSavingDialogRunnable;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceStep;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceTransition;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.SwingConstants;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.StepTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list.StepCellRenderer;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManagerImpl;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.UserAlertingDirectConnectEventFailureListener;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.services.IceServiceManager;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.swing.DragAndDropJList;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ExpressOMatic implements SwingConstants, ListDataListener
   {
   public void intervalRemoved(ListDataEvent e)
      {
      saved = false;
      }

   public void contentsChanged(ListDataEvent e)
      {
      saved = false;
      }

   public void intervalAdded(ListDataEvent e)
      {
      saved = false;
      }

   private static final Logger LOG = Logger.getLogger(ExpressOMatic.class);
   // **** Default Objects ****

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ExpressOMatic.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/createlab/TeRK/client/robotdiaries/visualprogrammer/Express-O-Matic.direct-connect.ice.properties";
   private static final String ICE_OBJECT_ADAPTER_NAME = "Terk.User";

   private JPanel sequencePanel;

   private File sequenceFile;

   private boolean saved;

   private Sequence sequence;

   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;

   private final MyPeerConnectionEventListener peerConnectionEventListener = new MyPeerConnectionEventListener();

   private final TerkServiceFactory terkServiceFactory = new TerkServiceFactory();

   private final Component parent;

   // private boolean isConnectedToPeer = false;

   private ServiceManager serviceManager = null;
   private TerkCommunicator terkCommunicator = null;
   private final File sequencesDirectory;

   protected ExpressOMatic(Component parent)
      {
      this.parent = parent;
      // COMMUNICATIONS ------------------------------------------------------------------------------------------------

      // create the ServantFactory instances
      final ServantFactory directConnectServantFactory = new ExpressOMaticServantFactory();

      // create the direct-connect manager
      directConnectCommunicatorManager = new DirectConnectCommunicatorManagerImpl(APPLICATION_NAME,
                                                                                  ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                                  ICE_OBJECT_ADAPTER_NAME,
                                                                                  directConnectServantFactory);

      final TerkCommunicatorCreationEventListener userAlertingDirectConnectEventFailureListener = new UserAlertingDirectConnectEventFailureListener(sequencePanel);
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(userAlertingDirectConnectEventFailureListener);

      // register a listener with the DirectConnectCommunicatorManager which causes the user to be notified with an alert
      // when various direct-connect-related failures occur.
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener());

      // turn on direct-connect support and start up the communicator
      directConnectCommunicatorManager.setIsSupported(true);
      directConnectCommunicatorManager.createCommunicator();

      final File homeDirectory = new File(TerkConstants.FilePaths.TERK_PATH);

      sequencesDirectory = TerkConstants.FilePaths.SEQUENCES_DIR;

      if (!homeDirectory.exists())
         {
         homeDirectory.mkdirs();
         }
      if (!sequencesDirectory.exists())
         {
         sequencesDirectory.mkdirs();
         }

      sequence = new Sequence(RESOURCES.getString("sequence.name.untitled"));

      saved = true;

      sequence.addListDataListener(this);

      buildGUI();
      }

   public Component getPanel()
      {
      return this.sequencePanel;
      }

   public void directConnectToPeer(final String hostname) throws DuplicateConnectionException, PeerConnectionFailedException
      {
      directConnectCommunicatorManager.getDirectConnectCommunicator().connectToPeer(hostname);
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
   private JButton sequenceSaveButton = null;
   private JButton sequenceClearButton = null;

   private SequencePlayer sequencePlayer = null;
   private JPanel stepPropertiesPanel = null;

   // So we know whether the user selects an item in stepList
   // or if it's selected from the sequence being played
   private boolean userSelection = true;

   private void buildGUI()
      {
      setupGUI();
      }

   protected void setupGUI()
      {
      sequencePanel = sequencePanel();
      }

   private JPanel sequencePanel()
      {
      final JPanel sequencePanel = new JPanel();
      sequencePanel.setLayout(new SpringLayout());

      sequencePanel.add(sequenceControlPanel());

      stepList = new DragAndDropJList(sequence);

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

   public void appendExpression(XmlExpression expression)
      {
      SequenceStep<XmlExpression> step = new SequenceStep<XmlExpression>(expression);
      this.sequence.addElement(step);
      //saved = false;
      }

   public void loadSequence(Sequence newSequence)
      {
      if (!saved)
         {
         // todo: i18n
         final int option = JOptionPane.showConfirmDialog(null, "Current sequence not saved, are you sure you wish to discard changes?", "Discard Changes", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
         if (option == JOptionPane.NO_OPTION)
            {
            return;
            }
         }

      if (newSequence != null)
         {
         sequence.removeListDataListener(this);

         sequence = newSequence;
         stepList.setModel(sequence);
         sequence.addListDataListener(this);
         saved = true;
         }
      }

   public void saveSequence()
      {

      final SequenceSavingDialogRunnable runnable = new SequenceSavingDialogRunnable(this.sequence, parent);

      if (SwingUtilities.isEventDispatchThread())
         {
         runnable.run();
         saved = runnable.saved();
         }
      else
         {
         SwingUtilities.invokeLater(new SaveDialog(runnable));
         }
      }

   final class SaveDialog implements Runnable
      {
      SequenceSavingDialogRunnable dialog;

      public SaveDialog(SequenceSavingDialogRunnable dialog)
         {
         this.dialog = dialog;
         }

      public void run()
         {
         this.dialog.run();
         saved = dialog.saved();
         }
      }

   private JPanel sequenceControlPanel
         ()
      {
      final JPanel controlPanel = new JPanel();
      controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
      controlPanel.setBackground(Color.WHITE);

      sequencePlayButton = GUIConstants.createButton(RESOURCES.getString("button.label.play"), true);
      sequenceSaveButton = GUIConstants.createButton(RESOURCES.getString("button.label.save"), true);
      sequenceClearButton = GUIConstants.createButton(RESOURCES.getString("button.label.clear"), true);

      sequencePlayer = new SequencePlayer()
      {
      public void currentStepChanged(final SequenceStep step)
         {
         if (step != null)
            {
            final int index = sequence.indexOf(step);
            stepList.setSelectedIndex(index);
            stepList.ensureIndexIsVisible(index);  // scroll the list so that the current step is visible
            }
         }

      public void conditionValuesChecked(final Condition condition, final Object[] values)
         {
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
            sequencePlayButton.setText(RESOURCES.getString("button.label.stop"));

            stepList.setDragEnabled(false);
            stepPropertiesPanel.setEnabled(false);

            final Thread sequenceThread = new Thread()
            {
            public void run()
               {
               sequencePlayer.playSequence(serviceManager, sequence);

               sequencePlayButton.setText(RESOURCES.getString("button.label.play"));
               stepList.setDragEnabled(true);
               stepPropertiesPanel.setEnabled(true);
               }
            };
            sequenceThread.start();
            }
         }
      });

      controlPanel.add(Box.createGlue());

      sequencePlayButton.setEnabled(false);

      sequenceSaveButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent event)
         {
         if (!sequencesDirectory.exists())
            {
            sequencesDirectory.mkdirs();
            }

         saveSequence();
         }
      });

      sequenceClearButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent e)
         {
         loadSequence(new Sequence(RESOURCES.getString("sequence.name.untitled")));
         }
      });

      controlPanel.add(sequenceClearButton);
      controlPanel.add(sequenceSaveButton);
      controlPanel.add(sequencePlayButton);

      controlPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sequencePlayButton.getHeight()));

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
      final JLabel expressionLabel = GUIConstants.createLabel("");

      final String transitionText = RESOURCES.getString("label.wait-until") + ":";
      final JPanel transitionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JLabel transitionLabel = GUIConstants.createLabel("");
      final JButton removeConditionButton = GUIConstants.createButton(RESOURCES.getString("label.remove"), true);
      final JLabel transitionSecondsLabel = GUIConstants.createLabel(RESOURCES.getString("label.seconds"));
      final JTextField transitionTimeField = new JTextField();
      transitionTimeField.setFont(GUIConstants.FONT_NORMAL);
      final JCheckBox loopCheckBox = new JCheckBox(RESOURCES.getString("label.loop-back-to-beginning"));
      loopCheckBox.setFont(GUIConstants.FONT_NORMAL);

      final NumberFormat transitionTimeFormat = NumberFormat.getNumberInstance();
      transitionTimeFormat.setMaximumFractionDigits(1);
      transitionTimeFormat.setMinimumFractionDigits(1);
      transitionTimeFormat.setMinimumIntegerDigits(1);

      final JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      final JButton deleteButton = GUIConstants.createButton(RESOURCES.getString("button.label.delete-step"), true);

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

            if (step.getStep() instanceof XmlExpression)
               {
               expressionLabel.setText(RESOURCES.getString("label.expression") + ": " + ((XmlExpression)step.getStep()).getName());
               }
            else if (step.getStep() instanceof Sequence)
               {
               expressionLabel.setText(RESOURCES.getString("label.sequence") + ": " + ((Sequence)step.getStep()).getName());
               }
            else
               {
               expressionLabel.setText("");
               }
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
      final TitledBorder outsideBorder = BorderFactory.createTitledBorder(RESOURCES.getString("step-properties.title"));
      outsideBorder.setTitleFont(GUIConstants.FONT_NORMAL);
      innerPanel.setBorder(BorderFactory.createCompoundBorder(
            outsideBorder,
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

   private void setIsConnectedToPeer
         (
               final boolean isConnectedToPeer)
      {
      toggleGUIElementState(isConnectedToPeer);
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

      private MyTerkCommunicatorCreationEventListener()
         {
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         // add the peer connection event listener
         terkCommunicator.addPeerConnectionEventListener(peerConnectionEventListener);

         // set the current TerkCommunicator
         ExpressOMatic.this.terkCommunicator = terkCommunicator;
         }
      }
   }