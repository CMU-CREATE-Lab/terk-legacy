package edu.cmu.ri.mrpl.TeRK.roboticon.messenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoListener;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerController;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Message;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.Roboticon;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RoboticonMessengerView implements
                                          RoboticonMessengerListener, PeerInfoListener
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessengerView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle
         .getBundle(RoboticonMessengerView.class.getName());

   private static final String FONT_NAME = "Verdana";

   private static final int FONT_SIZE = 11;

   private static final Font FONT = new Font(FONT_NAME, 0, FONT_SIZE);

   private static final Dimension MESSAGE_HISTORY_PANEL_SIZE = new Dimension(400, 330);

   private static final String DEFAULT_DROP_BOX_MESSAGE = RESOURCES.getString("label.drop-your-roboticon-here");

   private static final Dimension SPACER = new Dimension(5, 5);

   private static final String ALL_USERS_OPTION = RESOURCES.getString("recipient.option.all-users");

   //private static Color DEFAULT_MESSAGE_COLOR = Color.BLACK;
   //todo: choose and set a  a tab background color. This may not be the background in all look and feels.
   private static Color DEFAULT_MESSAGE_COLOR = new Color(238, 238, 238);

   private static Color NEW_MESSAGE_NOTIFY_COLOR = Color.RED;

   private static Color PUBLIC_MESSAGE_BACKGROUND_COLOR = new Color(194, 218, 242);
   private static Color PUBLIC_MESSAGE_TITLE_COLOR = new Color(90, 142, 226);
   private static Color PUBLIC_MESSAGE_COLOR = new Color(94, 106, 118);

   //private static Color ROBOTICON_AREA_BACKGROUND_COLOR = new Color(248, 242, 220);
   private static Color ROBOTICON_AREA_BACKGROUND_COLOR = Color.WHITE;

   private static final String PUBLIC_MESSAGE = "Public";

   private final Component parentComponent;

   private final RoboticonMessengerController roboticonMessengerController;

   private final RoboticonMessengerModel roboticonMessengerModel;

   private final PeerInfoModel peerInfoModel;

   private final DefaultComboBoxModel recipientComboBoxModel = new DefaultComboBoxModel(
         new String[]{ALL_USERS_OPTION});

   private final JComboBox recipientComboBox = new JComboBox(recipientComboBoxModel);

   private final JTextArea messageTextField = new JTextArea();

   private final JTextField subjectTextField = new JTextField();

   private String parentMessageId = null;

   private final JButton replyMessageButton = new JButton(RESOURCES.getString("button.label.reply"));
   private final JButton sendMessageButton = new JButton(RESOURCES.getString("button.label.send"));
   private final JButton cancelMessageButton = new JButton(RESOURCES.getString("button.label.cancel"));

   private final JScrollPane messageTextFieldScrollPane = new JScrollPane(
         messageTextField, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
         JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

   private final JLabel recipientLabel = new JLabel(RESOURCES.getString("label.message-recipient"));
   private final JLabel subjectLabel = new JLabel(RESOURCES.getString("label.message-subject"));
   private final JLabel messageLabel = new JLabel(RESOURCES.getString("label.message-content"));

   private final SendMessageActionListener sendMessageActionListener = new SendMessageActionListener();

   private final JPanel roboticonAndMessageSubmissionPanel = new JPanel();

   private final JPanel messageSubmissionPanel = new JPanel();

   private final JPanel roboticonDropBoxPanel = new JPanel();

   private final JLabel roboticonDropBoxMessageLabel = new JLabel(DEFAULT_DROP_BOX_MESSAGE);

   private final JLabelTransferHandler jLabelTransferHandler =
         new JLabelTransferHandler(roboticonDropBoxMessageLabel);

   private final JButton detachRoboticonButton = new JButton(RESOURCES.getString("button.label.detach"));

   private final RoboticonManagerController roboticonManagerController;

   private JTabbedPane tabbedMessagePane = new JTabbedPane();

   private JPanel messageHistoryPanel = new JPanel();

   private HashMap<String, JPanel> userToPanelMap = new HashMap<String, JPanel>();

   private HashMap<String, AltMessageHistoryTreeModel> userToMessageHistoryModel =
         new HashMap<String, AltMessageHistoryTreeModel>();
   private HashMap<String, JTree> userToMessageHistoryTree = new HashMap<String, JTree>();

   public RoboticonMessengerView(final Component parentComponent,
                                 final RoboticonMessengerController roboticonMessengerController,
                                 final RoboticonMessengerModel roboticonMessengerModel,
                                 final PeerInfoModel peerInfoModel,
                                 final RoboticonManagerController publicRoboticonManagerController)
      {
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

      recipientLabel.setFont(FONT);
      subjectLabel.setFont(FONT);
      messageLabel.setFont(FONT);

      subjectTextField.setFont(FONT);
      subjectTextField.setTransferHandler(jLabelTransferHandler);// this

      recipientComboBox.setFont(FONT);
      recipientComboBox.setBackground(Color.WHITE);
      recipientComboBox.setTransferHandler(jLabelTransferHandler);// this causes misplaced drops to go into the drop panel
      recipientComboBox.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent event)
         {
         String recipient = (String)recipientComboBox.getSelectedItem();
         if ((recipient != null) && (!recipient.equals("")))
            {
            if (recipient.equals(ALL_USERS_OPTION))
               {
               tabbedMessagePane.setSelectedIndex(0);
               }
            else
               {
               int index = tabbedMessagePane.indexOfTab(recipient);
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

      messageHistoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      messageHistoryPanel.setLayout(new BorderLayout());
      messageHistoryPanel.add(tabbedMessagePane, BorderLayout.CENTER);

      final JPanel messageHistoryLabelPanel = new JPanel();
      messageHistoryLabelPanel.setLayout(new BoxLayout(messageHistoryLabelPanel, BoxLayout.X_AXIS));
      final JLabel roboticonsLabel = new JLabel(RESOURCES.getString("label.message-history"));
      roboticonsLabel.setFont(FONT);
      messageHistoryLabelPanel.add(roboticonsLabel);
      messageHistoryLabelPanel.add(Box.createGlue());

      newMessagePanel(PUBLIC_MESSAGE);
      this.userToMessageHistoryModel.get(PUBLIC_MESSAGE)
            .loadMessages(this.roboticonMessengerModel.getMessageHistory());
      this.expandTreeRows(this.userToMessageHistoryTree.get(PUBLIC_MESSAGE));

      tabbedMessagePane.setForegroundAt(0, PUBLIC_MESSAGE_TITLE_COLOR);
      setBackgroundColorBasedOnTab(PUBLIC_MESSAGE_COLOR);

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
      roboticonDropBoxPanel.setLayout(new BoxLayout(roboticonDropBoxPanel, BoxLayout.X_AXIS));
      roboticonDropBoxPanel.setBorder(titledBorder);
      roboticonDropBoxPanel.add(roboticonDropBoxMessageLabel);
      roboticonDropBoxPanel.add(Box.createGlue());
      roboticonDropBoxPanel.add(detachRoboticonButton);
      roboticonDropBoxPanel.add(Box.createRigidArea(SPACER));
      roboticonDropBoxPanel.setTransferHandler(new JLabelTransferHandler(
            roboticonDropBoxMessageLabel));
      roboticonDropBoxPanel.setBackground(ROBOTICON_AREA_BACKGROUND_COLOR);

      messageSubmissionPanel.setOpaque(false);
      org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
            messageSubmissionPanel);
      messageSubmissionPanel.setLayout(layout);
      layout
            .setHorizontalGroup(layout
                                      .createParallelGroup(
                                            org.jdesktop.layout.GroupLayout.LEADING)
                                      .add(
                                            layout
                                                  .createSequentialGroup()
                                                  .addContainerGap()
                                                  .add(
                                                        layout
                                                              .createParallelGroup(
                                                                    org.jdesktop.layout.GroupLayout.LEADING)
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                recipientLabel)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                recipientComboBox,
                                                                                0,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                replyMessageButton))
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                subjectLabel)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                subjectTextField,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                sendMessageButton))
                                                              .add(
                                                                    layout
                                                                          .createSequentialGroup()
                                                                          .add(
                                                                                messageLabel)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                messageTextFieldScrollPane,
                                                                                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                271,
                                                                                Short.MAX_VALUE)
                                                                          .addPreferredGap(
                                                                                org.jdesktop.layout.LayoutStyle.RELATED)
                                                                          .add(
                                                                                cancelMessageButton)))
                                                  .addContainerGap()));

      layout.linkSize(new java.awt.Component[]{cancelMessageButton,
                                               replyMessageButton, sendMessageButton},
                      org.jdesktop.layout.GroupLayout.HORIZONTAL);

      layout.linkSize(new java.awt.Component[]{messageLabel,
                                               recipientLabel, subjectLabel},
                      org.jdesktop.layout.GroupLayout.HORIZONTAL);

      layout
            .setVerticalGroup(layout
                                    .createParallelGroup(
                                          org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(
                                          layout
                                                .createSequentialGroup()
                                                .addContainerGap()
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  org.jdesktop.layout.GroupLayout.BASELINE)
                                                            .add(recipientLabel)
                                                            .add(replyMessageButton)
                                                            .add(
                                                                  recipientComboBox,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(
                                                      org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  org.jdesktop.layout.GroupLayout.BASELINE)
                                                            .add(subjectLabel)
                                                            .add(sendMessageButton)
                                                            .add(
                                                                  subjectTextField,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(
                                                      org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(
                                                      layout
                                                            .createParallelGroup(
                                                                  org.jdesktop.layout.GroupLayout.LEADING)
                                                            .add(messageLabel)
                                                            .add(
                                                                  cancelMessageButton)
                                                            .add(
                                                                  messageTextFieldScrollPane,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                .addContainerGap(
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      Short.MAX_VALUE)));

      roboticonAndMessageSubmissionPanel.setLayout(new BoxLayout(roboticonAndMessageSubmissionPanel, BoxLayout.Y_AXIS));
      roboticonAndMessageSubmissionPanel.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
      roboticonAndMessageSubmissionPanel.add(messageSubmissionPanel);
      roboticonAndMessageSubmissionPanel.add(Box.createRigidArea(SPACER));
      roboticonAndMessageSubmissionPanel.add(roboticonDropBoxPanel);

      tabbedMessagePane.addChangeListener(
            new javax.swing.event.ChangeListener()
            {
            public void stateChanged(javax.swing.event.ChangeEvent e)
               {
               //Remove new message notification
               int index = tabbedMessagePane.getSelectedIndex();
               removeNewMessageNotification(index);
               //Set message recipient and background color
               String key = tabbedMessagePane.getTitleAt(index);
               if (PUBLIC_MESSAGE.equals(key))
                  {
                  recipientComboBoxModel.setSelectedItem(ALL_USERS_OPTION);
                  setBackgroundColorBasedOnTab(PUBLIC_MESSAGE_COLOR);
                  }
               else
                  {
                  recipientComboBoxModel.setSelectedItem(key);
                  setBackgroundColorBasedOnTab(peerInfoModel.getUserColor(key));
                  }
               }
            });
      }

   private void setBackgroundColorBasedOnTab(Color newColor)
      {
      newColor = newColor.brighter().brighter();
      messageHistoryPanel.setBackground(newColor);
      roboticonAndMessageSubmissionPanel.setBackground(newColor);
      }

   private void detachRoboticonWorkhorse()
      {
      roboticonDropBoxMessageLabel.setText(DEFAULT_DROP_BOX_MESSAGE);
      roboticonDropBoxMessageLabel.setToolTipText(null);
      detachRoboticonButton.setVisible(false);
      enableSendMessageAndCancelMessageButtonsIfAppropriate();
      }

   private JTree initializeMessageTree(TreeModel treeModel)
      {
      JTree tree = new JTree(treeModel);
      tree.setEditable(false);
      tree.getSelectionModel().setSelectionMode
            (TreeSelectionModel.SINGLE_TREE_SELECTION);
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
      JPanel messageHistoryLabelPanel = new JPanel();
      messageHistoryLabelPanel.setLayout(new BoxLayout(messageHistoryLabelPanel, BoxLayout.X_AXIS));
      JLabel roboticonsLabel = new JLabel(RESOURCES.getString("label.message-history"));
      roboticonsLabel.setFont(FONT);
      messageHistoryLabelPanel.add(roboticonsLabel);
      // messageHistoryLabelPanel.add(Box.createGlue());
      return messageHistoryLabelPanel;
      }

   private JScrollPane getMessageHistoryScrollPane(Component messageHistory)
      {
      JScrollPane messageHistoryScrollPane = new JScrollPane();
      messageHistoryScrollPane.setViewportView(messageHistory);// Added by // JWL.
      messageHistoryScrollPane.setPreferredSize(MESSAGE_HISTORY_PANEL_SIZE);
      return messageHistoryScrollPane;
      }

   private void newMessagePanel(String user)
      {
      if (!userToPanelMap.containsKey(user))
         {
         // which contains the messages
         JPanel userJPanel = new JPanel();
         userToPanelMap.put(user, userJPanel);
         AltMessageHistoryTreeModel msgHistoryModel = new AltMessageHistoryTreeModel(user + " Messages");
         userToMessageHistoryModel.put(user, msgHistoryModel);

         JTree messageHistoryTree = this.initializeMessageTree(msgHistoryModel);
         userToMessageHistoryTree.put(user, messageHistoryTree);

         userJPanel.setLayout(
               new BoxLayout(userJPanel, BoxLayout.Y_AXIS));
         userJPanel.add(getMessageHistoryLabelPanel());
         userJPanel.add(Box.createRigidArea(SPACER));
         userJPanel.add(
               this.getMessageHistoryScrollPane(messageHistoryTree));

         addMessageTab(user, null, userJPanel, "");
         }
      }

   private void addMessageTab(String title, Icon icon, Component component, String tip)
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
                  String key = null;
                  boolean notifyClient = true;

                  appendMessageWorkhorse(message);

                  //New meesage notification
                  if (roboticonMessengerModel != null)
                     {
                     //IF private message, determine if user of client is sender or receiver
                     //IF client not the recipient, do not notify client of new message
                     if (message.isPrivate)
                        {
                        String currentUser = roboticonMessengerModel.getUserId();
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
                        int currentIndex = tabbedMessagePane.getSelectedIndex();

                        if (tabbedMessagePane.getTitleAt(currentIndex).compareTo(key) != 0)
                           {
                           addNewMessageNotification(key);
                           }
                        }

                     JTree messageHistoryTree = userToMessageHistoryTree.get(key);
                     if (messageHistoryTree == null)
                        {
                        LOG.debug("Message History Tree is NULL for key: " + key);
                        }
                     //expandTreeRows( messageHistoryTree );
                     messageHistoryTree.repaint();
                     }
                  }
               });
         }
      }

   private void expandTreeRows(JTree messageHistoryTree)
      {

      // Make sure root node is expanded or all the other messages are not
      // visible
      if (messageHistoryTree != null)
         {
         MessageHistoryNode root =
               (MessageHistoryNode)messageHistoryTree.getModel().getRoot();
         TreePath path = new TreePath(root);
         if (!messageHistoryTree.isExpanded(path))
            {
            messageHistoryTree.expandPath(path);
            }

         // Number of rows in tree increases each time a branch is
         // expanded. Therefore, must update the rowCount after each
         // expansion
         /*    int rowCount = messageHistoryTree.getRowCount();
      for (int rowNo = 0; rowNo <= rowCount; rowNo++)
         {
         messageHistoryTree.expandRow(rowNo);
         rowCount = messageHistoryTree.getRowCount();
         }*/
         }
      }

   public void contentsChanged()
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               // clear the message area
               Collection<AltMessageHistoryTreeModel> treeModels = userToMessageHistoryModel.values();

               /*               Collection<Map.Entry<String,AltMessageHistoryTreeModel>> entries = userToMessageHistoryModel.entrySet();
               for(Map.Entry<String, AltMessageHistoryTreeModel> e: entries){
                  e.getValue().clear();
                  userToMessageHistoryTree.remove(e.getValue());
                  userToMessageHistoryTree.put(e.getKey(), initializeMessageTree(e.getValue()));
               }*/

               for (AltMessageHistoryTreeModel treeModel : treeModels)
                  {
                  treeModel.clear();
                  }
               // fill the message area with the current message history

               final List<RoboticonMessage> messages = roboticonMessengerModel.getMessageHistory();
               LOG.debug("View recieved major contents changed, loading " + roboticonMessengerModel.getMessageHistory().size() + " messages...");

               for (final Iterator<RoboticonMessage> iterator = messages.iterator(); iterator.hasNext();)
                  {
                  final RoboticonMessage message = iterator.next();
                  appendMessageWorkhorse(message);
                  }

               Collection<JTree> messageHistoryTrees = userToMessageHistoryTree.values();
               for (JTree messageHistoryTree : messageHistoryTrees)
                  {
                  expandTreeRows(messageHistoryTree);
                  messageHistoryTree.repaint();
                  }
               }
            });
      }

   public void contentsChanged(final long timestamp)
      {
      contentsChanged();
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
         tabbedMessagePane.getComponentAt(0).update(tabbedMessagePane.getComponentAt(0).getGraphics());
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

   private void updateTabColorOfUser(String userId)
      {
      int index = tabbedMessagePane.indexOfTab(userId);
      if (index > -1)
         {
         tabbedMessagePane.setForegroundAt(index, peerInfoModel.getUserColor(userId));
         if (index == tabbedMessagePane.getSelectedIndex())
            {
            setBackgroundColorBasedOnTab(peerInfoModel.getUserColor(userId));
            }
         }
      }

   private void appendMessageWorkhorse(final RoboticonMessage roboticonMessage)
      {
      String key = null;
      // figure out who i am.
      String currentUser = roboticonMessengerModel.getUserId();

      // key = roboticonMessage.senderUserId;
      if (roboticonMessage.isPrivate)
         {
         if (roboticonMessage.senderUserId.compareTo(currentUser) == 0)
            {
            // current user is the sender
            key = roboticonMessage.recipientUserId;
            }
         else if (roboticonMessengerModel.isServer() || roboticonMessage.recipientUserId.compareTo(currentUser) == 0)
            {
            key = roboticonMessage.senderUserId;
            }
         }
      else
         {
         key = PUBLIC_MESSAGE;
         }

      final Collection<RoboticonFile> roboticonFiles =
            RoboticonFile.toRoboticonFileList(roboticonMessage.roboticons,
                                              roboticonMessage.senderUserId,
                                              roboticonMessage.timestamp);

      roboticonManagerController.addRoboticons(roboticonFiles);

      if (key == null)
         {
         // message is not meant for us to display
         LOG.debug("Key is null");
         return;
         }
      if (!userToPanelMap.containsKey(key))
         {
         LOG.debug("Creating panel and message history tree for key: " + key);
         newMessagePanel(key);
         }
      else
         {
         LOG.debug("Panel exists for key: " + key);
         }

      MessageHistoryNode newNode = null;

      //add the message to the appropriate tree
      newNode = this.userToMessageHistoryModel.get(key).addMessage(roboticonMessage);
      if (tabbedMessagePane.getSelectedComponent().equals(userToPanelMap.get(key))
          && (currentUser == null || roboticonMessage.senderUserId.compareTo(currentUser) != 0))
         {
         //the new message is in the currently selected tab and I am the recipient
         //expand the tree but do not scroll
         this.userToMessageHistoryTree.get(key).makeVisible(new TreePath(newNode.getPath()));
         }
      else
         {
         //else, the new message is in a hidden tab or I am the sender of the message
         //expand the tree and scroll to the new message
         this.userToMessageHistoryTree.get(key).scrollPathToVisible(new TreePath(newNode.getPath()));
         }
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
            .setEnabled((isMessageFieldNonEmpty() && isSubjectFieldNonEmpty())
                        || isRoboticonAttachmentNonEmpty());
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
      return roboticonDropBoxMessageLabel.getToolTipText() != null;
      }

   public void setEnabled(final boolean isEnabled)
      {
      recipientComboBox.setEnabled(isEnabled);
      messageTextField.setEnabled(isEnabled);
      subjectTextField.setEnabled(isEnabled);
      detachRoboticonButton.setEnabled(isEnabled);
      Collection<JTree> messageHistoryTrees = userToMessageHistoryTree.values();
      for (JTree messageHistoryTree : messageHistoryTrees)
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
      JTree selectedTree = null;
      int selectedTabIndex = tabbedMessagePane.getSelectedIndex();
      String key = tabbedMessagePane.getTitleAt(selectedTabIndex);
      selectedTree = this.userToMessageHistoryTree.get(key);

      if (selectedTree != null)
         {
         MessageHistoryNode node = (MessageHistoryNode)selectedTree.getLastSelectedPathComponent();

         // If node selected
         if (node != null)
            {
            try
               {
               MessageNode messageNode = (MessageNode)node;
               parentMessageId = messageNode.roboticonMessage.messageId;
               subjectTextField.setText("Re: "
                                        + messageNode.roboticonMessage.theMessage.subject);
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
            catch (ClassCastException cce)
               {
               }
            }
         }
      }

   private final class JLabelTransferHandler extends StringTransferHandler
      {
      private static final long serialVersionUID = -7846087327718636311L;
      private final JLabel targetLabel;

      private JLabelTransferHandler(final JLabel targetLabel)
         {
         this.targetLabel = targetLabel;
         }

      public boolean canImport(final JComponent c, final DataFlavor[] flavors)
         {
         for (int i = 0; i < flavors.length; i++)
            {
            if (DataFlavor.stringFlavor.equals(flavors[i]))
               {
               return true;
               }
            }
         return false;
         }

      protected String exportString(final JComponent c)
         {
         return ((JLabel)c).getText();
         }

      protected void importString(final JComponent c, final String str)
         {
         if (str != null)
            {
            final File file = new File(str);
            if (file.exists() && file.isFile())
               {
               if (file.getName().toLowerCase().endsWith(".xml"))
                  {
                  String currentText = targetLabel.getText() + " ";
                  String currentTooltip = targetLabel.getToolTipText()
                                          + " ";
                  if (currentText.startsWith(DEFAULT_DROP_BOX_MESSAGE))
                     {
                     currentTooltip = ("");
                     currentText = "";
                     }
                  targetLabel.setText(currentText + file.getName());
                  targetLabel.setToolTipText(currentTooltip
                                             + file.getAbsolutePath());
                  detachRoboticonButton.setVisible(true);
                  enableSendMessageAndCancelMessageButtonsIfAppropriate();
                  return;
                  }
               }
            JOptionPane.showMessageDialog(
                  RoboticonMessengerView.this.parentComponent,
                  RESOURCES.getString("dialog.message.invalid-file"),
                  RESOURCES.getString("dialog.title.invalid-file"),
                  JOptionPane.ERROR_MESSAGE);
            }
         else
            {
            LOG.warn("Ignoring null file");
            }
         }

      /*
       * public boolean importData(final JComponent c, final Transferable t) {
       * if (canImport(c, t.getTransferDataFlavors())) { try {
       * 
       * RoboticonFile theRoboticon = (RoboticonFile) t
       * .getTransferData(RoboticonTransferHandler.roboticonFlavor);
       * 
       * final String str = theRoboticon.getAbsolutePath(); importString(c,
       * str); return true; } catch (UnsupportedFlavorException e) {
       * LOG.error("UnsupportedFlavorException caught", e); } catch
       * (IOException e) { LOG.error("IOException caught", e); } }
       * 
       * return false; }
       */
      protected void cleanup(final JComponent c, final boolean remove)
         {
         // do nothing
         }
      }

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
         final String[] messageAndRoboticon = new String[3];
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     messageAndRoboticon[0] = subjectTextField.getText();
                     messageAndRoboticon[1] = messageTextField.getText();
                     messageAndRoboticon[2] = roboticonDropBoxMessageLabel.getToolTipText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the message from the form field", e);
            }

         // see if there's a roboticon to load, and, if so, load it!
         String roboticonFilename = null;
         String roboticonXml = null;
         String roboticonCreator = null;
         long roboticonTimestamp = 0;
         final List<Roboticon> roboticons = new ArrayList<Roboticon>();
         String roboticonNames = messageAndRoboticon[2];
         if ((roboticonNames != null) && (roboticonNames.length() > 0))
            {
            int start = 0, end = roboticonNames.indexOf(".xml", start);
            do
               {
               final File roboticonFile = new File(roboticonNames.substring(start, end + 4));
               if (roboticonFile.exists() && roboticonFile.isFile())
                  {
                  try
                     {
                     roboticonXml = FileUtils.getFileAsString(roboticonFile);
                     roboticonFilename = roboticonFile.getName();
                     roboticonTimestamp = roboticonFile.lastModified();
                     roboticonCreator = "Unknown";
                     roboticons.add(new Roboticon(roboticonTimestamp,
                                                  roboticonFilename, roboticonXml, roboticonCreator));
                     }
                  catch (FileNotFoundException e)
                     {
                     if (LOG.isEnabledFor(Level.ERROR))
                        {
                        LOG.error(
                              "FileNotFoundException while trying to read the file ["
                              + messageAndRoboticon[2] + "]",
                              e);
                        }
                     }
                  }
               else
                  {
                  if (LOG.isEnabledFor(Level.ERROR))
                     {
                     LOG.error("File ["
                               + roboticonFile.getAbsolutePath()
                               + "] does not exist or is not a file");
                     }
                  }
               start = end + 5;
               end = roboticonNames.indexOf(".xml", start);
               }
            while (end != -1);
            }
         // ask the controller to send the messageAndRoboticon
         final boolean areSubjectAndMessageNonEmpty =
               (messageAndRoboticon[0] != null)
               && (messageAndRoboticon[1] != null)
               && (messageAndRoboticon[0].length() > 0)
               && (messageAndRoboticon[1].length() > 0);
         final boolean isRoboticonAttached = !roboticons.isEmpty();
         if (areSubjectAndMessageNonEmpty || isRoboticonAttached)
            {
            final Message message = new Message(messageAndRoboticon[0],
                                                messageAndRoboticon[1]);

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
               roboticonMessengerController.sendPrivateMessage(
                     parentMessageId, recipientUserId,
                     clientRoboticonMessage);
               SendPrivateMessageSetUp(recipientUserId);
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
      public void valueChanged(TreeSelectionEvent e)
         {
         JTree tree = (JTree)e.getSource();
         MessageHistoryNode node = (MessageHistoryNode)tree.getLastSelectedPathComponent();
         // If node selected
         if (node != null)
            {
            try
               {
               //Try to cast object to MessageNode
               //Should throw ClassCastException if object not of that type
               MessageNode messageNode = (MessageNode)node;
               replyMessageButton.setEnabled(true);
               }
            catch (ClassCastException cce)
               {
               // Subject node selected. If reply button enabled, disable
               // it
               if (replyMessageButton.isEnabled())
                  {
                  replyMessageButton.setEnabled(false);
                  }
               }
            }
         }
      }

   private void SendPrivateMessageSetUp(String recipientUserId)
      {
      // check if a tab exists for the userId
      //if tab does not exist create tab and show message history
      //else switch focus to tab and show message history
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

   private void addNewMessageNotification(String user)
      {
      int index = tabbedMessagePane.indexOfTab(user);
      //tabbedMessagePane.setForegroundAt(index, NEW_MESSAGE_NOTIFY_COLOR);
      tabbedMessagePane.setBackgroundAt(index, NEW_MESSAGE_NOTIFY_COLOR);
      }

   private void removeNewMessageNotification(int index)
      {
      if (index > -1)
         {
         //tabbedMessagePane.setForegroundAt(index, DEFAULT_MESSAGE_COLOR);
         tabbedMessagePane.setBackgroundAt(index, DEFAULT_MESSAGE_COLOR);
         }
      }
   }
