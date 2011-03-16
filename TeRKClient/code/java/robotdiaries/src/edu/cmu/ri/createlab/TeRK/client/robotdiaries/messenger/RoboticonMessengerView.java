package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.dnd.DataTransferHandler;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoListener;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.AltMessageHistoryTreeModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.MessageHistoryNode;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.MessageNode;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.MyMessageHistoryTreeCellRenderer;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerListener;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessengerView implements RoboticonMessengerListener, PeerInfoListener
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(RoboticonMessengerView.class.getName());

   private static final String FONT_NAME = "Verdana";
   private static final int FONT_SIZE = 11;
   private static final Font FONT = new Font(FONT_NAME, 0, FONT_SIZE);
   private static final Dimension MESSAGE_HISTORY_PANEL_SIZE = new Dimension(400, 330);
   private static final String DEFAULT_DROP_BOX_MESSAGE = RESOURCES.getString("label.drop-your-roboticon-here");
   private static final Dimension SPACER = new Dimension(5, 5);
   private static final String ALL_USERS_OPTION = RESOURCES.getString("recipient.option.all-users");
   private static final Color DEFAULT_MESSAGE_COLOR = new Color(238, 238, 238);
   private static final Color NEW_MESSAGE_NOTIFY_COLOR = Color.RED;
   private static final Color PUBLIC_MESSAGE_TITLE_COLOR = new Color(90, 142, 226);
   private static final Color ROBOTICON_AREA_BACKGROUND_COLOR = Color.WHITE;
   private static final String PUBLIC_MESSAGE = "Public";

   private final Component parentComponent;
   private final RoboticonMessengerController roboticonMessengerController;
   private final RoboticonMessengerModel roboticonMessengerModel;
   private final PeerInfoModel peerInfoModel;
   private final DefaultComboBoxModel recipientComboBoxModel = new DefaultComboBoxModel(new String[]{ALL_USERS_OPTION});
   private final JComboBox recipientComboBox = new JComboBox(recipientComboBoxModel);
   private final JTextArea messageTextField = new JTextArea();
   private final JTextField subjectTextField = new JTextField();
   private String parentMessageId = null;
   private final JButton replyMessageButton = new JButton(RESOURCES.getString("button.label.reply"));
   private final JButton sendMessageButton = new JButton(RESOURCES.getString("button.label.send"));
   private final JButton cancelMessageButton = new JButton(RESOURCES.getString("button.label.cancel"));
   private final SendMessageActionListener sendMessageActionListener = new SendMessageActionListener();
   private final JPanel roboticonAndMessageSubmissionPanel = new JPanel();
   private final JPanel messageSubmissionPanel = new JPanel();
   private final JLabel roboticonDropBoxMessageLabel = new JLabel(DEFAULT_DROP_BOX_MESSAGE);
   private final JButton detachRoboticonButton = new JButton(RESOURCES.getString("button.label.detach"));
   private final RoboticonManagerController roboticonManagerController;
   private JTabbedPane tabbedMessagePane = new JTabbedPane();
   private JPanel messageHistoryPanel = new JPanel();
   private HashMap<String, JPanel> userToPanelMap = new HashMap<String, JPanel>();
   private HashMap<String, AltMessageHistoryTreeModel> userToMessageHistoryModel = new HashMap<String, AltMessageHistoryTreeModel>();
   private HashMap<String, JTree> userToMessageHistoryTree = new HashMap<String, JTree>();
   private final List<Roboticon> attachedRoboticons = new ArrayList<Roboticon>();

   public RoboticonMessengerView(final Component parentComponent,
                                 final RoboticonMessengerController roboticonMessengerController,
                                 final RoboticonMessengerModel roboticonMessengerModel,
                                 final PeerInfoModel peerInfoModel,
                                 final RoboticonManagerController publicRoboticonManagerController)
      {
      this.roboticonDropBoxMessageLabel.setToolTipText("");
      this.parentComponent = parentComponent;
      this.roboticonMessengerController = roboticonMessengerController;
      this.roboticonMessengerModel = roboticonMessengerModel;
      this.peerInfoModel = peerInfoModel;
      this.roboticonManagerController = publicRoboticonManagerController;

      messageTextField.addKeyListener(
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               if (e.getKeyCode() == 10 && e.isControlDown() && isMessageFieldNonEmpty())
                  {
                  sendMessageActionListener.actionPerformed(null);
                  }
               else
                  {
                  enableSendMessageAndCancelMessageButtonsIfAppropriate();
                  }
               }
            });

      subjectTextField.addKeyListener(
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               if (e.getKeyCode() == 10 && e.isControlDown() && isSubjectFieldNonEmpty())
                  {
                  sendMessageActionListener.actionPerformed(null);
                  }
               else
                  {
                  enableSendMessageAndCancelMessageButtonsIfAppropriate();
                  }
               }
            });

      final JLabel recipientLabel = new JLabel(RESOURCES.getString("label.message-recipient"));
      recipientLabel.setFont(FONT);
      final JLabel subjectLabel = new JLabel(RESOURCES.getString("label.message-subject"));
      subjectLabel.setFont(FONT);
      final JLabel messageLabel = new JLabel(RESOURCES.getString("label.message-content"));
      messageLabel.setFont(FONT);

      subjectTextField.setFont(FONT);
      final JLabelTransferHandler jLabelTransferHandler = new JLabelTransferHandler();
      subjectTextField.setTransferHandler(jLabelTransferHandler);// this

      recipientComboBox.setFont(FONT);
      recipientComboBox.setBackground(Color.WHITE);
      recipientComboBox.setTransferHandler(jLabelTransferHandler);// this causes misplaced drops to go into the drop panel
      recipientComboBox.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent event)
               {
               final String recipient = (String)recipientComboBox.getSelectedItem();
               if ((recipient != null) && (!"".equals(recipient)))
                  {
                  if (recipient.equals(ALL_USERS_OPTION))
                     {
                     tabbedMessagePane.setSelectedIndex(0);
                     }
                  else
                     {
                     final int index = tabbedMessagePane.indexOfTab(recipient);
                     if (index > -1)
                        {
                        tabbedMessagePane.setSelectedIndex(index);
                        }
                     else
                        {
                        newMessagePanel(recipient);
                        tabbedMessagePane.setSelectedIndex(tabbedMessagePane.indexOfTab(recipient));
                        updateTabColorOfUser(recipient);
                        }
                     }
                  }
               }
            });

      messageTextField.setFont(FONT);
      messageTextField.setForeground(Color.BLACK);
      messageTextField.setWrapStyleWord(true);
      messageTextField.setLineWrap(true);
      messageTextField.setRows(3);
      messageTextField.setTransferHandler(jLabelTransferHandler);// this
      // causes
      replyMessageButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent event)
               {
               handleReplyEvent();
               }
            });
      replyMessageButton.setOpaque(false);// required for Mac
      replyMessageButton.setFont(FONT);
      replyMessageButton.setEnabled(false);
      replyMessageButton.setToolTipText(RESOURCES.getString("button.tooltip.click-to-reply"));
      sendMessageButton.addActionListener(sendMessageActionListener);
      sendMessageButton.setOpaque(false);// required for Mac
      sendMessageButton.setFont(FONT);

      cancelMessageButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent event)
               {
               clearMessageForm();
               }
            });

      cancelMessageButton.setOpaque(false);// required for Mac
      cancelMessageButton.setFont(FONT);

      //messageHistoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      messageHistoryPanel.setLayout(new BorderLayout());
      messageHistoryPanel.add(tabbedMessagePane, BorderLayout.CENTER);

      final JPanel messageHistoryLabelPanel = new JPanel();
      messageHistoryLabelPanel.setLayout(new BoxLayout(messageHistoryLabelPanel, BoxLayout.X_AXIS));
      final JLabel roboticonsLabel = new JLabel(RESOURCES.getString("label.message-history"));
      roboticonsLabel.setFont(FONT);
      messageHistoryLabelPanel.add(roboticonsLabel);
      messageHistoryLabelPanel.add(Box.createGlue());

      newMessagePanel(PUBLIC_MESSAGE);
      this.userToMessageHistoryModel.get(PUBLIC_MESSAGE).loadMessages(this.roboticonMessengerModel.getMessageHistory());
      this.expandTreeRows(this.userToMessageHistoryTree.get(PUBLIC_MESSAGE), false);

      tabbedMessagePane.setForegroundAt(0, PUBLIC_MESSAGE_TITLE_COLOR);
      tabbedMessagePane.setFont(GUIConstants.FONT_NORMAL);

      roboticonDropBoxMessageLabel.setBorder(BorderFactory.createEmptyBorder(
            10, 10, 10, 10));
      roboticonDropBoxMessageLabel.setFont(FONT);

      detachRoboticonButton.setFont(FONT);
      detachRoboticonButton.setVisible(false);
      detachRoboticonButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               detachRoboticonWorkhorse();
               }
            });

      final TitledBorder titledBorder = BorderFactory.createTitledBorder(RESOURCES.getString("label.attached-roboticon"));
      titledBorder.setTitleFont(FONT);
      final JPanel roboticonDropBoxPanel = new JPanel();
      roboticonDropBoxPanel.setLayout(new BoxLayout(roboticonDropBoxPanel, BoxLayout.X_AXIS));
      roboticonDropBoxPanel.setBorder(titledBorder);
      roboticonDropBoxPanel.add(roboticonDropBoxMessageLabel);
      roboticonDropBoxPanel.add(Box.createGlue());
      roboticonDropBoxPanel.add(detachRoboticonButton);
      roboticonDropBoxPanel.add(Box.createRigidArea(SPACER));
      roboticonDropBoxPanel.setTransferHandler(new JLabelTransferHandler());
      roboticonDropBoxPanel.setBackground(ROBOTICON_AREA_BACKGROUND_COLOR);

      messageSubmissionPanel.setOpaque(false);
      final GroupLayout layout = new GroupLayout(messageSubmissionPanel);
      messageSubmissionPanel.setLayout(layout);
      final JScrollPane messageTextFieldScrollPane = new JScrollPane(messageTextField,
                                                                     JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                                     JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
      layout
            .setHorizontalGroup(layout
                                      .createParallelGroup(
                                            GroupLayout.LEADING)
                                      .add(
                                            layout
                                                  .createSequentialGroup()
                                                  .addContainerGap()
                                                  .add(
                                                        layout
                                                              .createParallelGroup(
                                                                    GroupLayout.LEADING)
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                recipientLabel)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                recipientComboBox,
                                                                                0,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                replyMessageButton))
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                subjectLabel)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                subjectTextField,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                sendMessageButton))
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                messageLabel)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                messageTextFieldScrollPane,
                                                                                GroupLayout.DEFAULT_SIZE,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                LayoutStyle.RELATED)
                                                                          .add(
                                                                                cancelMessageButton)))
                                                  .addContainerGap()));

      layout.linkSize(new Component[]{cancelMessageButton,
                                      replyMessageButton, sendMessageButton},
                      GroupLayout.HORIZONTAL);

      layout.linkSize(new Component[]{messageLabel,
                                      recipientLabel, subjectLabel},
                      GroupLayout.HORIZONTAL);

      layout
            .setVerticalGroup(layout
                                    .createParallelGroup(
                                          GroupLayout.LEADING)
                                    .add(
                                          layout
                                                .createSequentialGroup()
                                                .addContainerGap()
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  GroupLayout.BASELINE)
                                                            .add(recipientLabel)
                                                            .add(replyMessageButton)
                                                            .add(
                                                                  recipientComboBox,
                                                                  GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(
                                                      LayoutStyle.RELATED)
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  GroupLayout.BASELINE)
                                                            .add(subjectLabel)
                                                            .add(sendMessageButton)
                                                            .add(
                                                                  subjectTextField,
                                                                  GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(
                                                      LayoutStyle.RELATED)
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  GroupLayout.LEADING)
                                                            .add(messageLabel)
                                                            .add(
                                                                  cancelMessageButton)
                                                            .add(
                                                                  messageTextFieldScrollPane,
                                                                  GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap(
                                                      GroupLayout.DEFAULT_SIZE,
                                                      Short.MAX_VALUE)));

      roboticonAndMessageSubmissionPanel.setLayout(new BoxLayout(roboticonAndMessageSubmissionPanel, BoxLayout.Y_AXIS));
      //roboticonAndMessageSubmissionPanel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
      roboticonAndMessageSubmissionPanel.add(messageSubmissionPanel);
      roboticonAndMessageSubmissionPanel.add(Box.createRigidArea(SPACER));
      roboticonAndMessageSubmissionPanel.add(roboticonDropBoxPanel);

      tabbedMessagePane.addChangeListener(
            new ChangeListener()
            {
            public void stateChanged(final ChangeEvent e)
               {
               //Remove new message notification
               final int index = tabbedMessagePane.getSelectedIndex();
               removeNewMessageNotification(index);

               //Set message recipient and background color
               final String key = tabbedMessagePane.getTitleAt(index);
               if (PUBLIC_MESSAGE.equals(key))
                  {
                  recipientComboBoxModel.setSelectedItem(ALL_USERS_OPTION);
                  }
               else
                  {
                  recipientComboBoxModel.setSelectedItem(key);
                  }
               }
            });
      }

   public void clearHistory()
      {
      final Collection<JTree> messageHistoryTrees = userToMessageHistoryTree.values();
      for (final JTree messageHistoryTree : messageHistoryTrees)
         {
         messageHistoryTree.removeAll();
         }
      }

   private void detachRoboticonWorkhorse()
      {
      roboticonDropBoxMessageLabel.setText(DEFAULT_DROP_BOX_MESSAGE);
      roboticonDropBoxMessageLabel.setToolTipText("");
      detachRoboticonButton.setVisible(false);
      attachedRoboticons.clear();
      enableSendMessageAndCancelMessageButtonsIfAppropriate();
      }

   private JTree initializeMessageTree(final TreeModel treeModel)
      {
      final JTree tree = new JTree(treeModel);
      tree.setEditable(false);
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      tree.addTreeSelectionListener(new MessageHistoryTreeSelectionListener());
      tree.setCellRenderer(new MyMessageHistoryTreeCellRenderer(peerInfoModel));

      tree.setVisible(true);
      tree.setVisibleRowCount(10);
      tree.setRootVisible(false);
      tree.setToggleClickCount(2);
      tree.setShowsRootHandles(true);
      // Enable drag
      tree.setDragEnabled(true);

      return tree;
      }

   private JPanel getMessageHistoryLabelPanel()
      {
      final JPanel messageHistoryLabelPanel = new JPanel();
      messageHistoryLabelPanel.setLayout(new BoxLayout(messageHistoryLabelPanel, BoxLayout.X_AXIS));
      final JLabel roboticonsLabel = new JLabel(RESOURCES.getString("label.message-history"));
      roboticonsLabel.setFont(FONT);
      messageHistoryLabelPanel.add(roboticonsLabel);
      // messageHistoryLabelPanel.add(Box.createGlue());
      return messageHistoryLabelPanel;
      }

   private JScrollPane getMessageHistoryScrollPane(final Component messageHistory)
      {
      final JScrollPane messageHistoryScrollPane = new JScrollPane();
      messageHistoryScrollPane.setViewportView(messageHistory);// Added by // JWL.
      messageHistoryScrollPane.setPreferredSize(MESSAGE_HISTORY_PANEL_SIZE);
      return messageHistoryScrollPane;
      }

   private void newMessagePanel(final String user)
      {
      if (!userToPanelMap.containsKey(user))
         {
         // which contains the messages
         final JPanel userJPanel = new JPanel();
         userToPanelMap.put(user, userJPanel);
         final AltMessageHistoryTreeModel msgHistoryModel = new AltMessageHistoryTreeModel(user + " Messages");
         userToMessageHistoryModel.put(user, msgHistoryModel);

         final JTree messageHistoryTree = this.initializeMessageTree(msgHistoryModel);
         userToMessageHistoryTree.put(user, messageHistoryTree);

         userJPanel.setLayout(new BoxLayout(userJPanel, BoxLayout.Y_AXIS));
         userJPanel.add(getMessageHistoryLabelPanel());
         userJPanel.add(Box.createRigidArea(SPACER));
         userJPanel.add(this.getMessageHistoryScrollPane(messageHistoryTree));

         addMessageTab(user, null, userJPanel, "");
         }
      }

   private void addMessageTab(final String title, final Icon icon, final Component component, final String tip)
      {
      tabbedMessagePane.addTab(title, icon, component, tip);
      }

   private Component getMessageHistoryPanel()
      {
      //return tabbedMessagePane;
      return messageHistoryPanel;
      }

   public void messageAdded(final RoboticonMessage message)
      {
      if (message != null)
         {

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  final String key;
                  boolean notifyClient = true;
                  final MessageHistoryNode node = appendMessageWorkhorse(message, true);

                  //New message notification
                  if (roboticonMessengerModel != null)
                     {
                     //IF private message, determine if user of client is sender or receiver
                     //IF client not the recipient, do not notify client of new message
                     if (message.isPrivate)
                        {
                        final String currentUser = roboticonMessengerModel.getUserId();
                        if (message.recipientUserId.compareTo(currentUser) == 0)
                           {
                           key = message.senderUserId;
                           }
                        else
                           {
                           notifyClient = false;
                           key = message.recipientUserId;
                           }
                        }
                     else
                        {
                        // shows notification even if it is sent from self
                        key = PUBLIC_MESSAGE;
                        }

                     if (key != null && notifyClient)
                        {
                        final int currentIndex = tabbedMessagePane.getSelectedIndex();

                        if (tabbedMessagePane.getTitleAt(currentIndex).compareTo(key) != 0)
                           {
                           addNewMessageNotification(key);
                           }
                        }

                     final JTree messageHistoryTree = userToMessageHistoryTree.get(key);
                     expandTreeToNodeAndMakeVisible(messageHistoryTree, node);

                     //expandTreeRows(messageHistoryTree, true);
                     messageHistoryTree.repaint();
                     }
                  }
               });
         }
      }

   private void expandTreeToNodeAndMakeVisible(final JTree messageHistoryTree, final MessageHistoryNode node)
      {
      final TreePath pathToNode = new TreePath(node.getPath());
      messageHistoryTree.expandPath(pathToNode);
      messageHistoryTree.makeVisible(pathToNode);
      }

   private void expandTreeRows(final JTree messageHistoryTree, final boolean latest)
      {
      // Make sure root node is expanded or all the other messages are not visible
      if (messageHistoryTree != null)
         {
         final MessageHistoryNode root = (MessageHistoryNode)messageHistoryTree.getModel().getRoot();
         final TreePath path = new TreePath(root);
         if (!messageHistoryTree.isExpanded(path))
            {
            messageHistoryTree.expandPath(path);
            }
         if (latest)
            {
            messageHistoryTree.expandRow((messageHistoryTree.getRowCount() - 1));
            }
         }
      }

   public void contentsChanged()
      {
      contentsChangedWorkhorse(false, null);
      }

   public void contentsChanged(final long timestamp)
      {
      contentsChangedWorkhorse(true, timestamp);
      }

   public void contentsChangedWorkhorse(final boolean willExpandNewMessages, final Long timestamp)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               // clear the message area
               final Collection<AltMessageHistoryTreeModel> treeModels = userToMessageHistoryModel.values();

               for (final AltMessageHistoryTreeModel treeModel : treeModels)
                  {
                  treeModel.clear();
                  }

               // fill the message area with the current message history
               final List<RoboticonMessage> messages = roboticonMessengerModel.getMessageHistory();
               LOG.debug("View recieved major contents changed, loading " + roboticonMessengerModel.getMessageHistory().size() + " messages...");

               for (final Iterator<RoboticonMessage> iterator = messages.iterator(); iterator.hasNext();)
                  {
                  final RoboticonMessage message = iterator.next();
                  appendMessageWorkhorse(message, false);
                  }

               final Collection<JTree> messageHistoryTrees = userToMessageHistoryTree.values();
               for (final JTree messageHistoryTree : messageHistoryTrees)
                  {
                  expandTreeRows(messageHistoryTree, false);
                  if (willExpandNewMessages)
                     {
                     expandMessagesNewerThan(messageHistoryTree, timestamp);
                     }
                  messageHistoryTree.repaint();
                  }
               }
            });
      }

   private void expandMessagesNewerThan(final JTree messageHistoryTree, final long timestamp)
      {
      final MessageHistoryNode root = (MessageHistoryNode)messageHistoryTree.getModel().getRoot();
      recursivelyRevealNodesNewerThan(messageHistoryTree, root, timestamp);
      }

   private void recursivelyRevealNodesNewerThan(final JTree messageHistoryTree, final MessageHistoryNode parentNode, final long timestamp)
      {
      if (parentNode != null)
         {
         if (parentNode.roboticonMessage != null && timestamp <= parentNode.roboticonMessage.timestamp)
            {
            expandTreeToNodeAndMakeVisible(messageHistoryTree, parentNode);
            }

         // now recurse over the children
         final Enumeration childNodes = parentNode.children();
         while (childNodes.hasMoreElements())
            {
            final MessageHistoryNode childNode = (MessageHistoryNode)childNodes.nextElement();
            recursivelyRevealNodesNewerThan(messageHistoryTree, childNode, timestamp);
            }
         }
      }

   public void peerConnected(final PeerInfo peerInfo)
      {
      // yes, I know this is inefficient, but it doesn't happen that often, so
      // don't complain
      peersChanged();
      }

   public void peerUpdated(final PeerInfo peerInfo)
      {
      //set tab colors
      updateTabColorOfUser(peerInfo.userId);
      //if the public tab is displayed, update it
      if (tabbedMessagePane != null && tabbedMessagePane.getSelectedIndex() == 0)
         {
         if (this.messageHistoryPanel.getParent() != null)
            {
            tabbedMessagePane.getComponentAt(0).update(tabbedMessagePane.getComponentAt(0).getGraphics());
            }
         }
      }

   public void peerDisconnected(final PeerInfo peerInfo)
      {
      recipientComboBoxModel.removeElement(peerInfo.userId);//todo: double check this. it doesn't seem right.
      }

   public void peersChanged()
      {
      recipientComboBoxModel.removeAllElements();
      recipientComboBoxModel.addElement(ALL_USERS_OPTION);
      for (final PeerInfo peerInfo : peerInfoModel.getAllPeerInfo())
         {
         recipientComboBoxModel.addElement(peerInfo.userId);
         updateTabColorOfUser(peerInfo.userId);
         }
      }

   private void updateTabColorOfUser(final String userId)
      {
      final int index = tabbedMessagePane.indexOfTab(userId);
      if (index > -1)
         {
         tabbedMessagePane.setForegroundAt(index, peerInfoModel.getUserColor(userId));
         }
      }

   private MessageHistoryNode appendMessageWorkhorse(final RoboticonMessage roboticonMessage, final boolean expand)
      {
      String key = null;

      // figure out who i am.
      final String currentUser = roboticonMessengerModel.getUserId();

      // key = roboticonMessage.senderUserId;
      if (roboticonMessage.isPrivate)
         {
         if (roboticonMessage.senderUserId != null && currentUser != null && roboticonMessage.senderUserId.compareTo(currentUser) == 0)
            {
            // current user is the sender
            key = roboticonMessage.recipientUserId;
            }
         else if (roboticonMessage.recipientUserId != null && currentUser != null && roboticonMessage.recipientUserId.compareTo(currentUser) == 0)
            {
            key = roboticonMessage.senderUserId;
            }
         }
      else
         {
         key = PUBLIC_MESSAGE;
         }

      final Collection<RoboticonFile> roboticonFiles = RoboticonFile.toRoboticonFileList(roboticonMessage.roboticons,
                                                                                         roboticonMessage.senderUserId,
                                                                                         roboticonMessage.timestamp);
      roboticonManagerController.addRoboticons(roboticonFiles);

      if (key == null)
         {
         // message is not meant for us to display
         return null;
         }
      if (!userToPanelMap.containsKey(key))
         {
         newMessagePanel(key);
         }

      //add the message to the appropriate tree
      final MessageHistoryNode newNode = this.userToMessageHistoryModel.get(key).addMessage(roboticonMessage);
      if (expand && tabbedMessagePane.getSelectedComponent().equals(userToPanelMap.get(key))
          && (currentUser == null || roboticonMessage.senderUserId.compareTo(currentUser) != 0))
         {
         //the new message is in the currently selected tab and I am the recipient
         //expand the tree but do not scroll
         this.userToMessageHistoryTree.get(key).makeVisible(new TreePath(newNode.getPath()));
         }
      else if (expand)
         {
         //else, the new message is in a hidden tab or I am the sender of the message
         //expand the tree and scroll to the new message
         this.userToMessageHistoryTree.get(key).scrollPathToVisible(new TreePath(newNode.getPath()));
         }

      return newNode;
      }

   public Component getMessageHistoryComponent()
      {
      // return messageHistoryPanel;
      return getMessageHistoryPanel();
      }

   public Component getMessageSubmissionComponent()
      {
      return messageSubmissionPanel;
      }

   public Component getRoboticonAndMessageSubmissionComponent()
      {
      return roboticonAndMessageSubmissionPanel;
      }

   private void enableSendMessageAndCancelMessageButtonsIfAppropriate()
      {
      sendMessageButton
            .setEnabled(isSubjectFieldNonEmpty() && (isMessageFieldNonEmpty()
                                                     || isRoboticonAttachmentNonEmpty()));
      cancelMessageButton.setEnabled(isParentMessageIdNonEmpty()
                                     || isMessageFieldNonEmpty() || isSubjectFieldNonEmpty()
                                     || isRoboticonAttachmentNonEmpty());
      }

   private boolean isParentMessageIdNonEmpty()
      {
      return parentMessageId != null && parentMessageId.length() > 0;
      }

   private boolean isMessageFieldNonEmpty()
      {
      return isFieldNonEmpty(messageTextField);
      }

   private boolean isSubjectFieldNonEmpty()
      {
      return isFieldNonEmpty(subjectTextField);
      }

   private boolean isFieldNonEmpty(final JTextComponent textField)
      {
      final String text = textField.getText();
      return (text != null) && (text.length() > 0);
      }

   private boolean isRoboticonAttachmentNonEmpty()
      {
      return attachedRoboticons.size() > 0;
      }

   public void setEnabled(final boolean isEnabled)
      {
      this.roboticonDropBoxMessageLabel.setEnabled(isEnabled);
      this.recipientComboBox.setEnabled(isEnabled);
      this.messageTextField.setEnabled(isEnabled);
      this.subjectTextField.setEnabled(isEnabled);
      this.detachRoboticonButton.setEnabled(isEnabled);
      final Collection<JTree> messageHistoryTrees = userToMessageHistoryTree.values();
      for (final JTree messageHistoryTree : messageHistoryTrees)
         {
         messageHistoryTree.setEnabled(isEnabled);
         }

      if (isEnabled)
         {
         enableSendMessageAndCancelMessageButtonsIfAppropriate();
         }
      else
         {
         sendMessageButton.setEnabled(isEnabled);
         cancelMessageButton.setEnabled(isEnabled);
         replyMessageButton.setEnabled(isEnabled);
         }
      }

   //todo : handleReplyEvent some other way?
   private void handleReplyEvent()
      {
      //Determine which tab is displayed, which message history tree contains selection
      final int selectedTabIndex = tabbedMessagePane.getSelectedIndex();
      final String key = tabbedMessagePane.getTitleAt(selectedTabIndex);
      final JTree selectedTree = this.userToMessageHistoryTree.get(key);

      if (selectedTree != null)
         {
         final MessageHistoryNode node = (MessageHistoryNode)selectedTree.getLastSelectedPathComponent();

         // If node selected
         if (node != null)
            {
            try
               {
               final MessageNode messageNode = (MessageNode)node;
               parentMessageId = messageNode.roboticonMessage.messageId;
               subjectTextField.setText("Re: " + messageNode.roboticonMessage.theMessage.subject);
               messageTextField.requestFocusInWindow();
               enableSendMessageAndCancelMessageButtonsIfAppropriate();

               //SET selected message recipient
               if (PUBLIC_MESSAGE.equals(key))
                  {
                  recipientComboBox.setSelectedItem(ALL_USERS_OPTION);
                  }
               else
                  {
                  recipientComboBoxModel.setSelectedItem(key);
                  }
               }
            catch (ClassCastException e)
               {
               LOG.error("ClassCastException caught and ignored: ", e);
               }
            }
         }
      }

   public void attachRoboticon(final FileEntry o)
      {
      if (o == null)
         {
         return;
         }

      final String currentText =
            roboticonDropBoxMessageLabel.getText().equals(DEFAULT_DROP_BOX_MESSAGE) ?
            "" : roboticonDropBoxMessageLabel.getText() + ", ";

      final String currentToolTip =
            roboticonDropBoxMessageLabel.getToolTipText() == null ||
            "".equals(roboticonDropBoxMessageLabel.getToolTipText()) ?
            "" : roboticonDropBoxMessageLabel.getToolTipText() + ", ";

      final Roboticon r = new Roboticon(o.getTimestamp(), o.getFilename(), o.getXml(), o.getCreator());
      attachedRoboticons.add(r);

      roboticonDropBoxMessageLabel.setText(currentText + o.getName());
      roboticonDropBoxMessageLabel.setToolTipText(currentToolTip + o.getCreator());

      detachRoboticonButton.setVisible(true);
      enableSendMessageAndCancelMessageButtonsIfAppropriate();
      }

   private final class JLabelTransferHandler extends DataTransferHandler
      {

      private JLabelTransferHandler()
         {
         }

      public boolean canImport(final JComponent c, final DataFlavor[] flavors)
         {
         return roboticonDropBoxMessageLabel.isEnabled() && (hasExpressionFlavor(flavors) || hasSequenceFlavor(flavors));
         }

      public boolean importData(final JComponent c, final Transferable t)
         {
         if (t == null)
            {
            return false;
            }

         if (!canImport(c, t.getTransferDataFlavors()))
            {
            return false;
            }

         try
            {

            if (hasExpressionFlavor(t.getTransferDataFlavors()))
               {
               attachRoboticon((FileEntry)t.getTransferData(expressionFlavor));
               }
            else if (hasSequenceFlavor(t.getTransferDataFlavors()))
               {
               attachRoboticon((FileEntry)t.getTransferData(sequenceFlavor));
               }
            else
               {
               return false;
               }

            return true;
            }
         catch (UnsupportedFlavorException ufe)
            {
            LOG.error("importData: unsupported data flavor", ufe);
            return false;
            }
         catch (IOException ioe)
            {
            LOG.error("importData: I/O exception", ioe);
            return false;
            }
         }

      protected Transferable createTransferable(final JComponent c)
         {
         return null;
         }

      public int getSourceActions(final JComponent c)
         {
         return COPY;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class SendMessageActionListener extends AbstractTimeConsumingAction
      {
      private static final long serialVersionUID = 4151605605804153027L;

      protected void executeGUIActionBefore()
         {
         parentComponent.setCursor(Cursor
                                         .getPredefinedCursor(Cursor.WAIT_CURSOR));
         }

      protected Object executeTimeConsumingAction()
         {
         // fetch the user id and password from the GUI
         final String[] messageArray = new String[2];
         final List<Roboticon> roboticons = Collections.unmodifiableList(attachedRoboticons);
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     messageArray[0] = subjectTextField.getText();
                     messageArray[1] = messageTextField.getText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the message from the form field", e);
            }

         // ask the controller to send the message
         final boolean areSubjectAndMessageNonEmpty =
               (messageArray[0] != null)
               && (messageArray[1] != null)
               && (messageArray[0].length() > 0)
               && (messageArray[1].length() > 0);
         final boolean isRoboticonAttached = !roboticons.isEmpty();
         if (areSubjectAndMessageNonEmpty || isRoboticonAttached)
            {
            final Message message = new Message(messageArray[0],
                                                messageArray[1]);

            final ClientRoboticonMessage clientRoboticonMessage = new ClientRoboticonMessage(
                  message, roboticons);

            // see whether it's a private message
            if (recipientComboBox.getSelectedIndex() == 0)
               {
               roboticonMessengerController.sendPublicMessage(
                     parentMessageId, clientRoboticonMessage);
               }
            else
               {
               final String recipientUserId = (String)recipientComboBox.getSelectedItem();
               roboticonMessengerController.sendPrivateMessage(parentMessageId, recipientUserId, clientRoboticonMessage);
               }
            }

         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         clearMessageForm();
         }
      }

   private class MessageHistoryTreeSelectionListener implements TreeSelectionListener
      {
      public void valueChanged(final TreeSelectionEvent e)
         {
         final JTree tree = (JTree)e.getSource();
         final MessageHistoryNode node = (MessageHistoryNode)tree.getLastSelectedPathComponent();
         // If node selected
         if (node != null)
            {
            //See if the node is a MessageNode
            if (node instanceof MessageNode)
               {
               replyMessageButton.setEnabled(true);
               }
            else
               {
               // Subject node selected. If reply button enabled, disable it
               if (replyMessageButton.isEnabled())
                  {
                  replyMessageButton.setEnabled(false);
                  }
               }
            }
         }
      }

   private void clearMessageForm()
      {
      parentMessageId = null;
      subjectTextField.setText("");
      subjectTextField.requestFocusInWindow();
      messageTextField.setText("");
      sendMessageButton.setEnabled(false);
      cancelMessageButton.setEnabled(false);
      detachRoboticonWorkhorse();
      parentComponent.setCursor(Cursor.getDefaultCursor());
      }

   private void addNewMessageNotification(final String user)
      {
      final int index = tabbedMessagePane.indexOfTab(user);
      //tabbedMessagePane.setForegroundAt(index, NEW_MESSAGE_NOTIFY_COLOR);
      tabbedMessagePane.setBackgroundAt(index, NEW_MESSAGE_NOTIFY_COLOR);
      }

   private void removeNewMessageNotification(final int index)
      {
      if (index > -1)
         {
         //tabbedMessagePane.setForegroundAt(index, DEFAULT_MESSAGE_COLOR);
         tabbedMessagePane.setBackgroundAt(index, DEFAULT_MESSAGE_COLOR);
         }
      }
   }

