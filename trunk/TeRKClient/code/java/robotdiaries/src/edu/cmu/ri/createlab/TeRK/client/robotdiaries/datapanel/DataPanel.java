package edu.cmu.ri.createlab.TeRK.client.robotdiaries.datapanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.Application;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.Condition;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.ConditionFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.conditionals.AbstractConditional;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.conditionals.AnalogInputsConditional;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.expressions.ExpressionFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger.Messenger;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.Sequence;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.SequenceFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.ConditionTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.ExpressionTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.SequenceTransferHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list.AbstractListCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list.ConditionCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list.ExpressionCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.list.SequenceCellRenderer;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.universalremote.UniversalRemoteWrapper;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer.VisualProgrammer;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Alex Styler (astyler@gmail.com)
 */
public class DataPanel extends JPanel implements ChangeListener
   {
   private static final Logger LOG = Logger.getLogger(DataPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DataPanel.class.getName());

   private static final int PROGRAMMER_CONTEXT = 2;
   private static final int MESSENGER_CONTEXT = 1;
   private static final int REMOTE_CONTEXT = 0;

   private int context = -1;

   private final JButton actionButton = GUIConstants.createButton(RESOURCES.getString("button.label.open"));
   private final JButton deleteButton = GUIConstants.createButton(RESOURCES.getString("button.label.delete"));

   private final JPanel localPanel = new JPanel();
   private final JPanel globalPanel = new JPanel();

   private JPanel sequencesLocalPanel;
   private JPanel expressionsLocalPanel;

   private JPanel sequencesGlobalPanel;
   private JPanel expressionsGlobalPanel;

   private final JTabbedPane tabbedPane;
   private JPanel conditionsPanel;

   private JList expressionsLocalList;
   private JList sequencesLocalList;

   private JList expressionsGlobalList;
   private JList sequencesGlobalList;

   private JList conditionsList;

   private final File homeDirectory;
   private final File expressionsDirectory;
   private final File expressionIconsDirectory;
   private final File sequencesDirectory;
   private final File sequenceIconsDirectory;
   private final File conditionsDirectory;
   private final File conditionIconsDirectory;

   Application currentApplication;

   private final MyRemotePeerConnectionEventListener remotePeerConnectionEventListener = new MyRemotePeerConnectionEventListener();
   private final MyMessengerPeerConnectionEventListener messengerPeerConnectionEventListener = new MyMessengerPeerConnectionEventListener();

   private boolean messengerConnected = false;
   private boolean remoteConnected = false;

   public DataPanel()
      {
      this.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
      this.setPreferredSize(new Dimension(200, 600));
      this.setMinimumSize(new Dimension(130, 30));

      homeDirectory = new File(TerkConstants.FilePaths.TERK_PATH);
      expressionsDirectory = TerkConstants.FilePaths.EXPRESSIONS_DIR;
      expressionIconsDirectory = TerkConstants.FilePaths.EXPRESSIONS_ICONS_DIR;
      sequencesDirectory = TerkConstants.FilePaths.SEQUENCES_DIR;
      sequenceIconsDirectory = TerkConstants.FilePaths.SEQUENCES_ICONS_DIR;
      conditionsDirectory = TerkConstants.FilePaths.CONDITIONS_DIR;
      conditionIconsDirectory = TerkConstants.FilePaths.CONDITIONS_ICONS_DIR;

      makeDirectories();

      createDefaultConditions();

      createDataLists();

      createDataPanels();

      localPanel.setLayout(new BoxLayout(localPanel, BoxLayout.Y_AXIS));
      localPanel.add(expressionsLocalPanel);
      localPanel.add(sequencesLocalPanel);
      localPanel.setBorder(BorderFactory.createEmptyBorder());
      globalPanel.setLayout(new BoxLayout(globalPanel, BoxLayout.Y_AXIS));
      globalPanel.add(expressionsGlobalPanel);
      globalPanel.add(sequencesGlobalPanel);

      tabbedPane = new JTabbedPane();
      tabbedPane.setFont(GUIConstants.FONT_NORMAL);
      tabbedPane.setBackground(Color.WHITE);
      tabbedPane.addTab(RESOURCES.getString("tab.label.local"), localPanel);
      tabbedPane.addTab(RESOURCES.getString("tab.label.global"), globalPanel);
      tabbedPane.addChangeListener(
            new ChangeListener()
            {
            public void stateChanged(ChangeEvent e)
               {
               toggleActionButtons();
               }
            });

      this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      this.add(createButtonPanel());
      this.add(tabbedPane);
      this.add(conditionsPanel);
      this.setMinimumSize(new Dimension(100, 0));
      this.setBackground(Color.WHITE);
      }

   private void createDataPanels()
      {
      final JScrollPane expressionsLocalScrollPane = new JScrollPane(expressionsLocalList);
      final JScrollPane sequencesLocalScrollPane = new JScrollPane(sequencesLocalList);
      final JScrollPane expressionsGlobalScrollPane = new JScrollPane(expressionsGlobalList);
      final JScrollPane sequencesGlobalScrollPane = new JScrollPane(sequencesGlobalList);

      final JScrollPane conditionsScrollPane = new JScrollPane(conditionsList);

      final JLabel expressionsLocalLabel = GUIConstants.createLabel(RESOURCES.getString("label.expressions"));

      final JLabel sequencesLocalLabel = GUIConstants.createLabel(RESOURCES.getString("label.sequences"));

      final JLabel expressionsGlobalLabel = GUIConstants.createLabel(RESOURCES.getString("label.shared-expressions"));
      final JLabel sequencesGlobalLabel = GUIConstants.createLabel(RESOURCES.getString("label.shared-sequences"));

      final JLabel conditionsLabel = new JLabel(RESOURCES.getString("label.conditions"));

      expressionsLocalLabel.setFont(GUIConstants.FONT_MEDIUM);
      sequencesLocalLabel.setFont(GUIConstants.FONT_MEDIUM);
      expressionsGlobalLabel.setFont(GUIConstants.FONT_MEDIUM);
      sequencesGlobalLabel.setFont(GUIConstants.FONT_MEDIUM);
      conditionsLabel.setFont(GUIConstants.FONT_MEDIUM);

      final JPanel expressionsLocalLabelPanel = new JPanel();
      expressionsLocalLabelPanel.setLayout(new BoxLayout(expressionsLocalLabelPanel, BoxLayout.X_AXIS));
      expressionsLocalLabelPanel.add(expressionsLocalLabel);
      expressionsLocalLabelPanel.add(Box.createGlue());
      expressionsLocalLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, expressionsLocalLabel.getHeight()));
      expressionsLocalLabelPanel.setBackground(new Color(0xd9e8eb));

      expressionsLocalPanel = new JPanel();
      expressionsLocalPanel.setLayout(new BoxLayout(expressionsLocalPanel, BoxLayout.Y_AXIS));
      expressionsLocalPanel.add(expressionsLocalLabelPanel);
      expressionsLocalPanel.add(expressionsLocalScrollPane);

      final JPanel sequencesLocalLabelPanel = new JPanel();
      sequencesLocalLabelPanel.setLayout(new BoxLayout(sequencesLocalLabelPanel, BoxLayout.X_AXIS));
      sequencesLocalLabelPanel.add(sequencesLocalLabel);
      sequencesLocalLabelPanel.add(Box.createGlue());
      sequencesLocalLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sequencesLocalLabel.getHeight()));
      sequencesLocalLabelPanel.setBackground(new Color(0xd4f0a6));

      sequencesLocalPanel = new JPanel();
      sequencesLocalPanel.setLayout(new BoxLayout(sequencesLocalPanel, BoxLayout.Y_AXIS));
      sequencesLocalPanel.add(sequencesLocalLabelPanel);
      sequencesLocalPanel.add(sequencesLocalScrollPane);

      final JPanel expressionsGlobalLabelPanel = new JPanel();
      expressionsGlobalLabelPanel.setLayout(new BoxLayout(expressionsGlobalLabelPanel, BoxLayout.X_AXIS));
      expressionsGlobalLabelPanel.add(expressionsGlobalLabel);
      expressionsGlobalLabelPanel.add(Box.createGlue());
      expressionsGlobalLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, expressionsGlobalLabel.getHeight()));
      expressionsGlobalLabelPanel.setBackground(new Color(0xd9e8eb));

      expressionsGlobalPanel = new JPanel();
      expressionsGlobalPanel.setLayout(new BoxLayout(expressionsGlobalPanel, BoxLayout.Y_AXIS));
      expressionsGlobalPanel.add(expressionsGlobalLabelPanel);
      expressionsGlobalPanel.add(expressionsGlobalScrollPane);

      final JPanel sequencesGlobalLabelPanel = new JPanel();
      sequencesGlobalLabelPanel.setLayout(new BoxLayout(sequencesGlobalLabelPanel, BoxLayout.X_AXIS));
      sequencesGlobalLabelPanel.add(sequencesGlobalLabel);
      sequencesGlobalLabelPanel.add(Box.createGlue());
      sequencesGlobalLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sequencesGlobalLabel.getHeight()));
      sequencesGlobalLabelPanel.setBackground(new Color(0xd4f0a6));

      sequencesGlobalPanel = new JPanel();
      sequencesGlobalPanel.setLayout(new BoxLayout(sequencesGlobalPanel, BoxLayout.Y_AXIS));
      sequencesGlobalPanel.add(sequencesGlobalLabelPanel);
      sequencesGlobalPanel.add(sequencesGlobalScrollPane);

      final JPanel conditionsLabelPanel = new JPanel();
      conditionsLabelPanel.setLayout(new BoxLayout(conditionsLabelPanel, BoxLayout.X_AXIS));
      conditionsLabelPanel.add(conditionsLabel);
      conditionsLabelPanel.add(Box.createGlue());
      conditionsLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, sequencesLocalLabel.getHeight()));
      conditionsLabelPanel.setBackground(new Color(0xdadada));

      conditionsPanel = new JPanel();
      conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
      conditionsPanel.add(conditionsLabelPanel);
      conditionsPanel.add(conditionsScrollPane);
      }

   private void makeDirectories()
      {
      if (!homeDirectory.exists())
         {
         homeDirectory.mkdirs();
         }

      if (!expressionsDirectory.exists())
         {
         expressionsDirectory.mkdirs();
         }

      if (!expressionIconsDirectory.exists())
         {
         expressionIconsDirectory.mkdirs();
         }

      if (!sequencesDirectory.exists())
         {
         sequencesDirectory.mkdirs();
         }

      if (!sequenceIconsDirectory.exists())
         {
         sequenceIconsDirectory.mkdirs();
         }

      if (!conditionsDirectory.exists())
         {
         conditionsDirectory.mkdirs();
         }

      if (!conditionIconsDirectory.exists())
         {
         conditionIconsDirectory.mkdirs();
         }
      }

   private void createDataLists()
      {
      final ListModel expressionListModel =
            new DirectoryPollingListModel<XmlExpression>(expressionsDirectory, ExpressionFileHandler.getInstance())
            {
            protected void performAfterRefresh()
               {
               AbstractListCellRenderer.loadExpressionImages(expressionIconsDirectory);
               }
            };

      final ListModel sequenceListModel =
            new DirectoryPollingListModel<Sequence>(sequencesDirectory, SequenceFileHandler.getInstance())
            {
            protected void performAfterRefresh()
               {
               AbstractListCellRenderer.loadSequenceImages(sequenceIconsDirectory);
               }
            };

      final ListModel conditionListModel =
            new DirectoryPollingListModel<Condition>(conditionsDirectory, ConditionFileHandler.getInstance())
            {
            protected void performAfterRefresh()
               {
               AbstractListCellRenderer.loadConditionImages(conditionIconsDirectory);
               }
            };

      expressionsLocalList = new JList(expressionListModel);
      expressionsLocalList.setFont(GUIConstants.FONT_NORMAL);
      expressionsLocalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      expressionsLocalList.setTransferHandler(new ExpressionTransferHandler());
      expressionsLocalList.setDragEnabled(true);
      expressionsLocalList.setCellRenderer(new ExpressionCellRenderer());
      expressionsLocalList.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(ListSelectionEvent e)
               {
               if (!expressionsLocalList.isSelectionEmpty())
                  {
                  sequencesLocalList.clearSelection();
                  sequencesGlobalList.clearSelection();
                  expressionsGlobalList.clearSelection();
                  conditionsList.clearSelection();
                  }
               toggleActionButtons();
               }
            }

      );

      // double-clicking should cause the expression to be opened/attached (and the selection cleared)
      expressionsLocalList.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               if (e.getClickCount() == 2)
                  {
                  actionButton.doClick();
                  }
               }
            });

      expressionsGlobalList = new JList();
      expressionsGlobalList.setFont(GUIConstants.FONT_NORMAL);
      expressionsGlobalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      expressionsGlobalList.setTransferHandler(new ExpressionTransferHandler());
      expressionsGlobalList.setDragEnabled(true);
      expressionsGlobalList.setCellRenderer(new ExpressionCellRenderer());
      expressionsGlobalList.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(final ListSelectionEvent event)
               {
               if (!expressionsGlobalList.isSelectionEmpty())//if this gains a selection, clear the others
                  {
                  sequencesLocalList.clearSelection();
                  sequencesGlobalList.clearSelection();
                  expressionsLocalList.clearSelection();
                  conditionsList.clearSelection();
                  }
               toggleActionButtons();
               }
            });

      expressionsGlobalList.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               LOG.debug("MouseEvent recieved: " + e.getClickCount() + " clicks. ");
               if (e.getClickCount() == 2)
                  {
                  actionButton.doClick();
                  }
               }
            }
      );

      sequencesLocalList = new JList(sequenceListModel);
      sequencesLocalList.setFont(GUIConstants.FONT_NORMAL);
      sequencesLocalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      sequencesLocalList.setTransferHandler(new SequenceTransferHandler());
      sequencesLocalList.setDragEnabled(true);
      sequencesLocalList.setCellRenderer(new SequenceCellRenderer());
      sequencesLocalList.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(ListSelectionEvent e)
               {
               if (!sequencesLocalList.isSelectionEmpty())
                  {
                  expressionsGlobalList.clearSelection();
                  sequencesGlobalList.clearSelection();
                  expressionsLocalList.clearSelection();
                  conditionsList.clearSelection();
                  }
               toggleActionButtons();
               }
            });

      // double-clicking should cause the sequence to be opened/attached (and the selection cleared)
      sequencesLocalList.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               LOG.debug("MouseEvent recieved: " + e.getClickCount() + " clicks. ");
               if (e.getClickCount() == 2)
                  {
                  actionButton.doClick();
                  }
               }
            }
      );

      sequencesGlobalList = new JList();
      sequencesGlobalList.setFont(GUIConstants.FONT_NORMAL);
      sequencesGlobalList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      sequencesGlobalList.setTransferHandler(new SequenceTransferHandler());
      sequencesGlobalList.setDragEnabled(true);
      sequencesGlobalList.setCellRenderer(new SequenceCellRenderer());
      sequencesGlobalList.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(final ListSelectionEvent event)
               {
               //see listener for expressionsLocalList
               if (!sequencesGlobalList.isSelectionEmpty())
                  {
                  expressionsGlobalList.clearSelection();
                  sequencesLocalList.clearSelection();
                  expressionsLocalList.clearSelection();
                  conditionsList.clearSelection();
                  }
               toggleActionButtons();
               }
            });

      sequencesGlobalList.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               LOG.debug("MouseEvent recieved: " + e.getClickCount() + " clicks. ");
               if (e.getClickCount() == 2)
                  {
                  actionButton.doClick();
                  }
               }
            }
      );
      conditionsList = new JList(conditionListModel);
      conditionsList.setFont(GUIConstants.FONT_NORMAL);
      conditionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      conditionsList.setTransferHandler(new ConditionTransferHandler());
      conditionsList.setDragEnabled(true);
      conditionsList.setCellRenderer(new ConditionCellRenderer());
      conditionsList.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(final ListSelectionEvent event)
               {
               //see listener for expressionsLocalList
               if (!conditionsList.isSelectionEmpty())
                  {
                  expressionsLocalList.clearSelection();
                  sequencesLocalList.clearSelection();
                  expressionsGlobalList.clearSelection();
                  sequencesGlobalList.clearSelection();
                  }
               toggleActionButtons();
               }
            });
      }

   public JPanel createButtonPanel()
      {
      JPanel buttonPanel = new JPanel();
      buttonPanel.setBackground(Color.WHITE);
      buttonPanel.add(Box.createGlue());
      deleteButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(ActionEvent e)
               {
               FileEntry tempObject = getSelectedValue();

               if (tempObject == null)
                  {
                  return;
                  }

               File objectFile = new File(tempObject.getAbsolutePath());

               // todo: i18n
               int confirmation = JOptionPane.showConfirmDialog(null,
                                                                "Are you sure you want to delete \"" + tempObject.getName() + "\" ?",
                                                                "Delete " + (getSelectedClass() == XmlExpression.class ? "Expression" : "Sequence"),
                                                                JOptionPane.YES_NO_OPTION,
                                                                JOptionPane.WARNING_MESSAGE);
               if (confirmation == JOptionPane.NO_OPTION)
                  {
                  return;
                  }

               if (objectFile.exists())
                  {
                  if (!objectFile.delete())
                     {
                     LOG.error("File " + objectFile.getName() + " encountered error during deletion.");
                     }
                  expressionsLocalList.clearSelection();
                  sequencesLocalList.clearSelection();
                  ((DirectoryPollingListModel)expressionsLocalList.getModel()).forceDirectoryPoll();
                  ((DirectoryPollingListModel)sequencesLocalList.getModel()).forceDirectoryPoll();
                  }
               else
                  {
                  LOG.error("File not found: " + objectFile.getAbsolutePath());
                  }
               }
            });

      final class ActionButtonListener implements ActionListener
         {
         private final Map<Integer, ActionListener> listeners = new HashMap<Integer, ActionListener>();

         ActionButtonListener()
            {
            listeners.put(Integer.valueOf(PROGRAMMER_CONTEXT),
                          new AbstractTimeConsumingAction()
                          {
                          protected Object executeTimeConsumingAction()
                             {
                             FileEntry tempObject = getSelectedValue();
                             if (getSelectedClass().equals(Sequence.class))
                                {
                                ((VisualProgrammer)currentApplication).loadSequence((Sequence)tempObject.getObject());
                                }
                             else
                                {
                                ((VisualProgrammer)currentApplication).appendExpression((XmlExpression)tempObject.getObject());
                                }

                             return null;
                             }
                          });
            listeners.put(Integer.valueOf(REMOTE_CONTEXT),
                          new AbstractTimeConsumingAction()
                          {
                          protected Object executeTimeConsumingAction()
                             {
                             FileEntry tempObject = getSelectedValue();
                             if (getSelectedClass().equals(XmlExpression.class))
                                {
                                ((UniversalRemoteWrapper)currentApplication).loadExpression((XmlExpression)tempObject.getObject());
                                }

                             return null;
                             }
                          });
            listeners.put(Integer.valueOf(MESSENGER_CONTEXT),
                          new AbstractTimeConsumingAction()
                          {
                          protected Object executeTimeConsumingAction()
                             {
                             ((Messenger)currentApplication).attachRoboticon(getSelectedValue());

                             return null;
                             }
                          });
            }

         public void actionPerformed(final ActionEvent e)
            {
            final ActionListener current = listeners.get(context);

            if (current != null)
               {
               current.actionPerformed(e);
               }
            else
               {
               if (LOG.isEnabledFor(Level.WARN))
                  {
                  LOG.warn("DataPanel.ActionButtonListener.actionPerformed(): Unexpected context: " + context);
                  }
               }
            }
         }

      actionButton.addActionListener(new ActionButtonListener());

      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
      buttonPanel.add(actionButton);
      buttonPanel.add(deleteButton);
      buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, deleteButton.getHeight()));
      return buttonPanel;
      }

   public void setGlobalListModels(ListModel expModel, ListModel seqModel)
      {
      expressionsGlobalList.setModel(expModel);
      sequencesGlobalList.setModel(seqModel);
      }

   public void stateChanged(ChangeEvent e)
      {
      JTabbedPane pane = (JTabbedPane)e.getSource();

      changeContext(pane.getSelectedComponent());

      toggleActionButtons();
      }

   public void changeContext(final Component app)
      {
      if (currentApplication != null && currentApplication instanceof Messenger)
         {
         ((Messenger)currentApplication).setHistoryVisible(false);
         }

      if (app instanceof UniversalRemoteWrapper)
         {
         currentApplication = (UniversalRemoteWrapper)app;
         context = REMOTE_CONTEXT;
         sequencesLocalList.setEnabled(false);
         sequencesLocalPanel.setVisible(false);
         sequencesGlobalList.setEnabled(false);
         sequencesGlobalPanel.setVisible(false);
         conditionsPanel.setVisible(false);
         }
      else if (app instanceof VisualProgrammer)
         {
         currentApplication = (VisualProgrammer)app;
         context = PROGRAMMER_CONTEXT;
         sequencesLocalList.setEnabled(true);
         sequencesLocalPanel.setVisible(true);
         sequencesGlobalList.setEnabled(true);
         sequencesGlobalPanel.setVisible(true);
         conditionsPanel.setVisible(true);
         }
      else if (app instanceof Messenger)
         {
         currentApplication = (Messenger)app;
         ((Messenger)currentApplication).setHistoryVisible(true);
         context = MESSENGER_CONTEXT;
         sequencesLocalList.setEnabled(true);
         sequencesLocalPanel.setVisible(true);
         sequencesGlobalList.setEnabled(true);
         sequencesGlobalPanel.setVisible(true);
         conditionsPanel.setVisible(false);
         }
      else
         {
         throw new IllegalStateException();
         }
      }

   private void toggleActionButtons()
      {
      boolean delete = false;
      boolean action = false;

      if (isSelectedItemVisible() &&
          (!expressionsLocalList.isSelectionEmpty() || !sequencesLocalList.isSelectionEmpty()))
         {
         //if a local item is selected, in any context, activate delete button
         delete = true;
         }

      if (context == MESSENGER_CONTEXT)
         {
         actionButton.setText(RESOURCES.getString("button.label.attach"));
         if (isSelectedItemVisible() && messengerConnected)
            {
            action = true;
            }
         }
      else if (context == PROGRAMMER_CONTEXT)
         {
         actionButton.setText(RESOURCES.getString("button.label.open"));
         if (isSelectedItemVisible())
            {
            action = true;
            }
         if (!expressionsLocalList.isSelectionEmpty() || !expressionsGlobalList.isSelectionEmpty())
            {
            actionButton.setText(RESOURCES.getString("button.label.append"));
            }
         }
      else if (context == REMOTE_CONTEXT)
         {
         actionButton.setText(RESOURCES.getString("button.label.open"));
         if (isSelectedItemVisible() && remoteConnected &&
             (!expressionsLocalList.isSelectionEmpty() || !expressionsGlobalList.isSelectionEmpty()))
            {
            action = true;
            }
         }

      actionButton.setEnabled(action);
      deleteButton.setEnabled(delete);
      }

   private boolean isSelectedItemVisible()
      {
      return (tabbedPane.getSelectedComponent() == localPanel &&
              (!expressionsLocalList.isSelectionEmpty() || !sequencesLocalList.isSelectionEmpty()))
             ||
             (tabbedPane.getSelectedComponent() == globalPanel &&
              (!expressionsGlobalList.isSelectionEmpty() || !sequencesGlobalList.isSelectionEmpty()));
      }

   public FileEntry getSelectedValue()
      {
      FileEntry value = expressionsLocalList.isSelectionEmpty() ? (FileEntry)sequencesLocalList.getSelectedValue() : (FileEntry)expressionsLocalList.getSelectedValue();
      if (value == null)//no local selected, try global
         {
         value = expressionsGlobalList.isSelectionEmpty() ? (FileEntry)sequencesGlobalList.getSelectedValue() : (FileEntry)expressionsGlobalList.getSelectedValue();
         }
      return value;
      }

   public Class getSelectedClass()
      {
      if (!expressionsLocalList.isSelectionEmpty() || !expressionsGlobalList.isSelectionEmpty())
         {
         return XmlExpression.class;
         }
      else
         {
         return Sequence.class;
         }
      }

   public PeerConnectionEventListener getRemoteListener()
      {
      return this.remotePeerConnectionEventListener;
      }

   public PeerConnectionEventListener getMessengerListener()
      {
      return this.messengerPeerConnectionEventListener;
      }

   public void createDefaultConditions()
      {
      ConditionFileHandler handler = ConditionFileHandler.getInstance();
      makeConditionHelper(handler, 0, "Sensor 1 - Average Light.xml",
                          AbstractConditional.LOGICAL_OPERATOR.AND, AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 50,
                          AbstractConditional.LOGICAL_OPERATOR.AND, AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 170);
      makeConditionHelper(handler, 0, "Sensor 1 - Bright Light.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 170);
      makeConditionHelper(handler, 0, "Sensor 1 - Dark.xml", AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 50);
      makeConditionHelper(handler, 0, "Sensor 1 - Something Nearby.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 50);
      makeConditionHelper(handler, 0, "Sensor 1 - Something Very Nearby.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 107);
      makeConditionHelper(handler, 0, "Sensor 1 - Nothing in Range.xml", AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 50);
      makeConditionHelper(handler, 1, "Sensor 2 - Average Light.xml",
                          AbstractConditional.LOGICAL_OPERATOR.AND, AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 50,
                          AbstractConditional.LOGICAL_OPERATOR.AND, AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 170);
      makeConditionHelper(handler, 1, "Sensor 2 - Bright Light.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 170);
      makeConditionHelper(handler, 1, "Sensor 2 - Dark.xml", AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 50);
      makeConditionHelper(handler, 1, "Sensor 2 - Something Nearby.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 50);
      makeConditionHelper(handler, 1, "Sensor 2 - Something Very Nearby.xml", AbstractConditional.COMPARE_OPERATOR.GREATER_THAN, 107);
      makeConditionHelper(handler, 1, "Sensor 2 - Nothing in Range.xml", AbstractConditional.COMPARE_OPERATOR.LESS_THAN, 50);
      copyIcons();
      }

   private void makeConditionHelper(final ConditionFileHandler handler,
                                    final int deviceId,
                                    final String fileName,
                                    final AbstractConditional.COMPARE_OPERATOR compareOperator,
                                    final int value)
      {
      makeConditionHelper(handler, deviceId, fileName, null, compareOperator, value, null, null, -1);
      }

   private void makeConditionHelper(final ConditionFileHandler handler,
                                    final int deviceId,
                                    final String fileName,
                                    final AbstractConditional.LOGICAL_OPERATOR logicOperator1,
                                    final AbstractConditional.COMPARE_OPERATOR compareOperator1,
                                    final int value1,
                                    final AbstractConditional.LOGICAL_OPERATOR logicOperator2,
                                    final AbstractConditional.COMPARE_OPERATOR compareOperator2,
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
      final File newFile = new File(conditionsDirectory, fileName);
      handler.saveFile(newCondition, newFile);
      }

   private void copyIcons()
      {
      copyImgFile("Sensor 1 - Average Light.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 1 - Bright Light.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 1 - Dark.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Average Light.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Bright Light.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Dark.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 1 - Something Nearby.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 1 - Something Very Nearby.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 1 - Nothing in Range.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Something Nearby.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Something Very Nearby.bmp", conditionIconsDirectory);
      copyImgFile("Sensor 2 - Nothing in Range.bmp", conditionIconsDirectory);
      copyImgFile(".DefaultSequenceIcon.bmp", sequenceIconsDirectory);
      }

   private void copyImgFile(final String imgFilename, final File targetDirectory)
      {
      BufferedInputStream inputStream = null;
      BufferedOutputStream outputStream = null;
      try
         {
         // set up the input stream
         inputStream = new BufferedInputStream(DataPanel.class.getResourceAsStream(imgFilename));

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

   final class MyRemotePeerConnectionEventListener implements PeerConnectionEventListener
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         remoteConnected = true;
         toggleActionButtons();
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         //To change body of implemented methods use File | Settings | File Templates.
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         remoteConnected = false;
         toggleActionButtons();
         }

      public void handlePeerConnectionFailedEvent(final String peerUserId)
         {
         //To change body of implemented methods use File | Settings | File Templates.
         }
      }

   final class MyMessengerPeerConnectionEventListener implements PeerConnectionEventListener
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         messengerConnected = true;
         toggleActionButtons();
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         //To change body of implemented methods use File | Settings | File Templates.
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         messengerConnected = false;
         toggleActionButtons();
         }

      public void handlePeerConnectionFailedEvent(final String peerUserId)
         {
         //To change body of implemented methods use File | Settings | File Templates.
         }
      }
   }