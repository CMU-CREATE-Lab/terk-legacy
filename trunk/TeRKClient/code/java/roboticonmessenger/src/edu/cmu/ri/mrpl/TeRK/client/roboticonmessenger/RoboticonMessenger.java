package edu.cmu.ri.mrpl.TeRK.client.roboticonmessenger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.components.servants.PeerInfoClientServiceServant;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd.RoboticonTransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.roboticonmessenger.messaging.RoboticonMessagingClientServiceServant;
import edu.cmu.ri.mrpl.TeRK.color.ColorChooserDialog;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.peerinformation.HTTPPeerImageFactory;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerImageFactory;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfo;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrx;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoController;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoModel;
import edu.cmu.ri.mrpl.TeRK.peerinformation.PeerInfoView;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonManagerView;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.ClientRoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerController;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerModel;
import edu.cmu.ri.mrpl.TeRK.roboticon.messenger.RoboticonMessengerView;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessage;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrx;
import edu.cmu.ri.mrpl.TeRK.roboticonmessenger.RoboticonMessagingClientServicePrxHelper;
import edu.cmu.ri.mrpl.TeRK.servants.ServiceServantRegistrar;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.swing.ColorUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class RoboticonMessenger extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(RoboticonMessenger.class);

   public static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle
         .getBundle(RoboticonMessenger.class.getName());

   /** The application name (appears in the title bar) */
   public static final String APPLICATION_NAME = RESOURCES
         .getString("application.name");

   /** Properties file used to setup Ice for this application */
   public static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/roboticonmessenger/RoboticonMessenger.relay.ice.properties";//  @jve:decl-index=0:

   /**
    * This method initializes jJMenuBar_Main
    *
    * @return javax.swing.JMenuBar
    */
   private JMenuBar getJJMenuBar_Main()
      {
      if (jJMenuBar_Main == null)
         {
         jJMenuBar_Main = new JMenuBar();
         jJMenuBar_Main.add(getJMenu_ExpressOMatic());
         jJMenuBar_Main.add(getJMenu_Messenger());
         }
      return jJMenuBar_Main;
      }

   /**
    * This method initializes jMenu_ExpressOMatic
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenu_ExpressOMatic()
      {
      if (jMenu_ExpressOMatic == null)
         {
         jMenu_ExpressOMatic = new JMenu();
         jMenu_ExpressOMatic.setText("Express-O-Matic");
         jMenu_ExpressOMatic.add(getJMenuItem_ShowExpressOMatic());
         jMenu_ExpressOMatic.add(getJMenuItem_HideExpressOMatic());
         jMenu_ExpressOMatic.add(getJMenuItem_NewSequence());
         jMenu_ExpressOMatic.add(getJMenuItem_OpenSequence());
         jMenu_ExpressOMatic.add(getJMenuItem_SaveSequence());
         jMenu_ExpressOMatic.add(getJMenuItem_SaveAsSequence());
         if (expressOMaticShown)
            {
            getJMenuItem_ShowExpressOMatic().setEnabled(false);
            getJMenuItem_HideExpressOMatic().setEnabled(true);
            }
         else
            {
            getJMenuItem_ShowExpressOMatic().setEnabled(true);
            getJMenuItem_HideExpressOMatic().setEnabled(false);
            }
         }
      return jMenu_ExpressOMatic;
      }

   /**
    * This method initializes jMenu_Messenger
    *
    * @return javax.swing.JMenu
    */
   private JMenu getJMenu_Messenger()
      {
      if (jMenu_Messenger == null)
         {
         jMenu_Messenger = new JMenu();
         jMenu_Messenger.setText("Messenger");
         jMenu_Messenger.add(getJMenuItem_ShowMessenger());
         jMenu_Messenger.add(getJMenuItem_HideMessenger());
         if (messengerShown)
            {
            getJMenuItem_ShowMessenger().setEnabled(false);
            getJMenuItem_HideMessenger().setEnabled(true);
            }
         else
            {
            getJMenuItem_ShowMessenger().setEnabled(true);
            getJMenuItem_HideMessenger().setEnabled(false);
            }
         }
      return jMenu_Messenger;
      }

   /**
    * This method initializes jPanel_Main
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_Main()
      {
      if (jPanel_Main == null)
         {
         jPanel_Main = new JPanel();
         jPanel_Main.setLayout(new BorderLayout());
         jPanel_Main.add(getJPanel_North(), BorderLayout.NORTH);
         //jPanel_Main.add(getJPanel_West(), BorderLayout.WEST);
         jPanel_Main.add(getJPanel_Center(), BorderLayout.CENTER);
         }
      return jPanel_Main;
      }

   /**
    * This method initializes jPanel_North
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_North()
      {
      if (jPanel_North == null)
         {
         jPanel_North = new JPanel();
         jPanel_North.setLayout(new BorderLayout());
         jPanel_North.setPreferredSize(new Dimension(0, 80));

         jPanel_North.setBackground(Color.white);
         jPanel_North.setBorder(BorderFactory
                                      .createBevelBorder(BevelBorder.RAISED));
         getConnectionStatePanel().setPeerConnectionStateLabelText(
               RESOURCES.getString("label.connected.to.peer"));

         //create a panel to hold the connect/disconnect button and the connection state panel
         final JPanel connectionPanel = new JPanel();
         jPanel_North.add(connectionPanel, BorderLayout.WEST);
         jPanel_North.add(getLabelPanel(), BorderLayout.EAST);
         connectionPanel.setLayout(null);
         connectionPanel.setBackground(Color.white);
         connectionPanel.setPreferredSize(new Dimension(390, 80));
         connectionPanel.add(getUserIconButton(), null);
         connectionPanel.add(connectedUserIdLabel, null);
         connectionPanel.add(getMessengerConnectButton(), null);
         connectionPanel.add(getMessengerConnectStatusPanel(), null);
         }
      return jPanel_North;
      }

   /**
    * This method initializes jPanel_West
    *
    * @return javax.swing.JPanel
    */
   /*   private JPanel getJPanel_West()
   {
   if (jPanel_West == null)
      {
      jPanel_West = new JPanel();
      jPanel_West.setLayout(new BorderLayout());
      jPanel_West.setPreferredSize(new Dimension(0, 0));
      jPanel_West.add(getJPanel_ExpressOMatic(), BorderLayout.CENTER);
      }
   return jPanel_West;
   }*/

   /**
    * This method initializes jPanel_Center
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_Center()
      {
      if (jPanel_Center == null)
         {
         jPanel_Center = new JPanel();
         jPanel_Center.setLayout(new BorderLayout());
         /*jPanel_Center.add(getJSplitPane_RoboticonMessageSplit(),
                           BorderLayout.CENTER);
         */
         jPanel_Center.add(getJSplitPane_ExpressOMaticRoboticonSplit(),
                           BorderLayout.CENTER);
         }
      return jPanel_Center;
      }

   /**
    * This method initializes jSplitPane_ViewWriteMessageSplit
    *
    * @return javax.swing.JSplitPane
    */
   private JSplitPane getJSplitPane_ViewWriteMessageSplit()
      {
      if (jSplitPane_ViewWriteMessageSplit == null)
         {
         jSplitPane_ViewWriteMessageSplit = new JSplitPane();
         jSplitPane_ViewWriteMessageSplit
               .setOrientation(JSplitPane.VERTICAL_SPLIT);
         jSplitPane_ViewWriteMessageSplit
               .setTopComponent(getJPanel_ViewMessages());
         jSplitPane_ViewWriteMessageSplit
               .setBottomComponent(getJPanel_WriteMessage());
         jSplitPane_ViewWriteMessageSplit.setResizeWeight(1.0);
         }
      return jSplitPane_ViewWriteMessageSplit;
      }

   /**
    * This method initializes jPanel_ViewMessages
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_ViewMessages()
      {
      if (jPanel_ViewMessages == null)
         {
         jPanel_ViewMessages = new JPanel();
         jPanel_ViewMessages.setLayout(new BorderLayout());
         jPanel_ViewMessages.add(getJPanel_UserList(), BorderLayout.NORTH);
         jPanel_ViewMessages.add(getJPanel_MessageHistory(),
                                 BorderLayout.CENTER);
         }
      return jPanel_ViewMessages;
      }

   /**
    * This method initializes jPanel_WriteMessage
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_WriteMessage()
      {
      if (jPanel_WriteMessage == null)
         {
         jPanel_WriteMessage = new JPanel();
         jPanel_WriteMessage.setLayout(new BorderLayout());
         jPanel_WriteMessage.add(roboticonMessengerView
                                       .getRoboticonAndMessageSubmissionComponent(),
                                 BorderLayout.CENTER);
         }
      return jPanel_WriteMessage;
      }

   /**
    * This method initializes jTabbedPane_Messages
    *
    * @return javax.swing.JTabbedPane
    */
   private JTabbedPane getJTabbedPane_Messages()
      {
      if (jTabbedPane_Messages == null)
         {
         jTabbedPane_Messages = new JTabbedPane();
         jTabbedPane_Messages.setSize(new Dimension(8, 15));
         }
      return jTabbedPane_Messages;
      }

   /**
    * This method initializes jPanel_Public
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_Public()
      {
      if (jPanel_Public == null)
         {
         jPanel_Public = new JPanel();
         jPanel_Public.setLayout(new FlowLayout());
         }
      return jPanel_Public;
      }

   /**
    * This method initializes jPanel_ExpressOMatic
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_ExpressOMatic()
      {
      if (jPanel_ExpressOMatic == null)
         {
         //jPanel_ExpressOMatic = new ExpressOMaticPanel();

         jPanel_ExpressOMatic = expressomaticVeiw.getExpressoMaticPanel();
         }
      return jPanel_ExpressOMatic;
      }

   /**
    * This method initializes jPanel_RoboticonList
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_RoboticonList()
      {
      if (jPanel_RoboticonList == null)
         {
         jPanel_RoboticonList = new JPanel();
         jPanel_RoboticonList.setLayout(new BorderLayout());
         jPanel_RoboticonList.setPreferredSize(new Dimension(150, 0));
         jPanel_RoboticonList.add(roboticonManagerView
                                        .getRoboticonListComponent(), BorderLayout.CENTER);
         roboticonManagerView.getPrivateListComponent().setTransferHandler(new RoboticonTransferHandler());
         roboticonManagerView.getPublicListComponent().setTransferHandler(new RoboticonTransferHandler(false));
         }
      return jPanel_RoboticonList;
      }

   /**
    * This method initializes jPanel_UserList
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_UserList()
      {
      if (jPanel_UserList == null)
         {
         jPanel_UserList = new JPanel();
         jPanel_UserList.setLayout(new BorderLayout());
         jPanel_UserList.setPreferredSize(new Dimension(0, 75));
         jPanel_UserList.add(peerInfoView.getListComponent(),
                             BorderLayout.CENTER);
         }
      return jPanel_UserList;
      }

   /**
    * This method initializes jMenuItem_ShowExpressOMatic
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_ShowExpressOMatic()
      {
      if (jMenuItem_ShowExpressOMatic == null)
         {
         jMenuItem_ShowExpressOMatic = new JMenuItem();
         jMenuItem_ShowExpressOMatic.setText("Show");
         jMenuItem_ShowExpressOMatic
               .addActionListener(new java.awt.event.ActionListener()
               {
               public void actionPerformed(java.awt.event.ActionEvent e)
                  {
                  ExpressOMaticDisplay();
                  }
               });
         }
      return jMenuItem_ShowExpressOMatic;
      }

   /**
    * This method initializes jMenuItem_HideExpressOMatic
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_HideExpressOMatic()
      {
      if (jMenuItem_HideExpressOMatic == null)
         {
         jMenuItem_HideExpressOMatic = new JMenuItem();
         jMenuItem_HideExpressOMatic.setText("Hide");
         jMenuItem_HideExpressOMatic
               .addActionListener(new java.awt.event.ActionListener()
               {
               public void actionPerformed(java.awt.event.ActionEvent e)
                  {
                  ExpressOMaticDisplay();
                  }
               });
         }
      return jMenuItem_HideExpressOMatic;
      }

   private void ExpressOMaticDisplay()
      {
      Dimension currentSize = this.getSize();
      //Point currentLocation = this.getLocation();

      if (expressOMaticShown)
         {
         //hide express-o-matic
         getJPanel_ExpressOMatic().setVisible(false);
         //update state
         expressOMaticShown = false;
         //resize app
         currentSize.width -= getJPanel_ExpressOMatic().getWidth();
         this.setSize(currentSize);
         getJSplitPane_ExpressOMaticRoboticonSplit().doLayout();
         //set location
         /*currentLocation.setLocation(
                 currentLocation.getX()+getJPanel_ExpressOMatic().getWidth(),
                 currentLocation.getY());
         this.setLocation(currentLocation);*/

         jMenuItem_ShowExpressOMatic.setEnabled(true);
         jMenuItem_HideExpressOMatic.setEnabled(false);
         }
      else
         {
         //show express-o-matic
         getJPanel_ExpressOMatic().setVisible(true);
         //update state
         expressOMaticShown = true;
         //resize app
         currentSize.width += getJPanel_ExpressOMatic().getWidth();
         this.setSize(currentSize);
         //set location
         /*currentLocation.setLocation(
                 currentLocation.getX()-getJPanel_ExpressOMatic().getWidth(),
                 currentLocation.getY());
         this.setLocation(currentLocation);*/

         jMenuItem_ShowExpressOMatic.setEnabled(false);
         jMenuItem_HideExpressOMatic.setEnabled(true);
         }
      this.repaint();
      this.validate();
      }

   /**
    * This method initializes jMenuItem_ShowMessenger
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_ShowMessenger()
      {
      if (jMenuItem_ShowMessenger == null)
         {
         jMenuItem_ShowMessenger = new JMenuItem();
         jMenuItem_ShowMessenger.setText("Show");
         jMenuItem_ShowMessenger
               .addActionListener(new java.awt.event.ActionListener()
               {
               public void actionPerformed(java.awt.event.ActionEvent e)
                  {
                  MessengerDisplay();
                  }
               });
         }
      return jMenuItem_ShowMessenger;
      }

   /**
    * This method initializes jMenuItem_HideMessenger
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_HideMessenger()
      {
      if (jMenuItem_HideMessenger == null)
         {
         jMenuItem_HideMessenger = new JMenuItem();
         jMenuItem_HideMessenger.setText("Hide");
         jMenuItem_HideMessenger
               .addActionListener(new java.awt.event.ActionListener()
               {
               public void actionPerformed(java.awt.event.ActionEvent e)
                  {
                  MessengerDisplay();
                  }
               });
         }
      return jMenuItem_HideMessenger;
      }

   private void MessengerDisplay()
      {
      Dimension currentSize = this.getSize();

      if (messengerShown)
         {
         //hide messenger
         getJSplitPane_ViewWriteMessageSplit().setVisible(false);
         //update state
         messengerShown = false;
         //resize app
         currentSize.width -= getJSplitPane_ViewWriteMessageSplit().getWidth();
         this.setSize(currentSize);
         getJSplitPane_RoboticonMessageSplit().doLayout();

         jMenuItem_ShowMessenger.setEnabled(true);
         jMenuItem_HideMessenger.setEnabled(false);
         }
      else
         {
         //show express-o-matic
         getJSplitPane_ViewWriteMessageSplit().setVisible(true);
         //update state
         messengerShown = true;
         //resize app
         currentSize.width += getJSplitPane_ViewWriteMessageSplit().getWidth();
         this.setSize(currentSize);

         jMenuItem_ShowMessenger.setEnabled(false);
         jMenuItem_HideMessenger.setEnabled(true);
         }
      this.repaint();
      this.validate();
      }

   /**
    * This method initializes labelPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getLabelPanel()
      {
      if (labelPanel == null)
         {
         labelPanel = new JPanel();
         labelPanel.setLayout(null);
         labelPanel.setBackground(Color.white);
         labelPanel.setPreferredSize(new Dimension(215, 80));
         cmuLabel = new JLabel();
         cmuLabel.setText(RESOURCES.getString("cmu.name"));
         cmuLabel.setVerticalAlignment(SwingConstants.TOP);
         cmuLabel.setVerticalTextPosition(SwingConstants.TOP);
         cmuLabel.setHorizontalAlignment(SwingConstants.RIGHT);
         cmuLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
         cmuLabel.setForeground(new Color(153, 0, 0));
         cmuLabel.setBounds(new Rectangle(0, 0, 213, 15));
         cmuLabel.setFont(GUIConstants.FONT_SMALL);

         labelPanel.add(cmuLabel, null);
         labelPanel.add(applicationNameLabel, null);
         }
      return labelPanel;
      }

   /**
    * This method initializes userIconButton
    *
    * @return javax.swing.JButton
    */
   private JButton getUserIconButton()
      {
      if (userIconButton == null)
         {
         userIconButton = new JButton();
         userIconButton.setEnabled(true);
         userIconButton.setPreferredSize(new Dimension(50, 50));
         userIconButton.setBackground(Color.white);
         userIconButton.setBounds(new Rectangle(0, 1, 87, 75));
         userIconButton.setBorder(BorderFactory
                                        .createEmptyBorder(0, 0, 0, 0));

         userIconButton.addActionListener(new ActionListener()
         {

         public void actionPerformed(ActionEvent e)
            {
            ColorChooserDialog colorChooserDialog = new ColorChooserDialog();
            colorChooserDialog.setLocationRelativeTo(userIconButton);
            colorChooserDialog.setOriginalColor(peerInfoModel.getUserColor(roboticonMessengerModel.getUserId()));
            if (colorChooserDialog.showDialog())
               {
               //set new color
               Color newColor = colorChooserDialog.getNewColor();
               if (newColor.equals(Color.WHITE))
                  {
                  newColor = new Color(224, 224, 224);  //todo: decide if this is the desired behavior
                  }
               peerInfoController.setAttribute("hexColor", ColorUtils.getHexColor(newColor));
               connectedUserIdLabel.setForeground(newColor);
               }
            }
         });
         }
      return userIconButton;
      }

   /**
    * This method initializes jPanel_MessageHistory
    *
    * @return javax.swing.JPanel
    */
   private JPanel getJPanel_MessageHistory()
      {
      if (jPanel_MessageHistory == null)
         {
         jPanel_MessageHistory = new JPanel();
         jPanel_MessageHistory.setLayout(new BorderLayout());
         }
      return jPanel_MessageHistory;
      }

   /**
    * This method initializes jSplitPane_ExpressOMaticRoboticonSplit
    *
    * @return javax.swing.JSplitPane
    */
   private JSplitPane getJSplitPane_ExpressOMaticRoboticonSplit()
      {
      if (jSplitPane_ExpressOMaticRoboticonSplit == null)
         {
         jSplitPane_ExpressOMaticRoboticonSplit = new JSplitPane();
         //jSplitPane_ExpressOMaticRoboticonSplit.setOneTouchExpandable(true);
         jSplitPane_ExpressOMaticRoboticonSplit
               .setLeftComponent(getJPanel_ExpressOMatic());
         jSplitPane_ExpressOMaticRoboticonSplit
               .setRightComponent(getJSplitPane_RoboticonMessageSplit());
         }
      return jSplitPane_ExpressOMaticRoboticonSplit;
      }

   /**
    * This method initializes jSplitPane_RoboticonMessageSplit
    *
    * @return javax.swing.JSplitPane
    */
   private JSplitPane getJSplitPane_RoboticonMessageSplit()
      {
      if (jSplitPane_RoboticonMessageSplit == null)
         {
         jSplitPane_RoboticonMessageSplit = new JSplitPane();
         //jSplitPane_RoboticonMessageSplit.setResizeWeight(1.0);
         jSplitPane_RoboticonMessageSplit
               .setLeftComponent(getJPanel_RoboticonList());
         jSplitPane_RoboticonMessageSplit
               .setRightComponent(getJSplitPane_ViewWriteMessageSplit());
         }
      return jSplitPane_RoboticonMessageSplit;
      }

   /**
    * This method initializes jMenuItem_NewSequence
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_NewSequence()
      {
      if (jMenuItem_NewSequence == null)
         {
         jMenuItem_NewSequence = new JMenuItem();
         jMenuItem_NewSequence.setMnemonic(KeyEvent.VK_UNDEFINED);
         jMenuItem_NewSequence.setText("New Sequence");
         jMenuItem_NewSequence.addActionListener(new java.awt.event.ActionListener()
         {
         public void actionPerformed(java.awt.event.ActionEvent e)
            {
            expressomaticVeiw.NewSequence();
            }
         });
         }
      return jMenuItem_NewSequence;
      }

   /**
    * This method initializes jMenuItem_OpenSequence
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_OpenSequence()
      {
      if (jMenuItem_OpenSequence == null)
         {
         jMenuItem_OpenSequence = new JMenuItem();
         jMenuItem_OpenSequence.setText("Open Sequence");
         jMenuItem_OpenSequence.addActionListener(new java.awt.event.ActionListener()
         {
         public void actionPerformed(java.awt.event.ActionEvent e)
            {
            expressomaticVeiw.Open();
            }
         });
         }
      return jMenuItem_OpenSequence;
      }

   /**
    * This method initializes jMenuItem_SaveSequence
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_SaveSequence()
      {
      if (jMenuItem_SaveSequence == null)
         {
         jMenuItem_SaveSequence = new JMenuItem();
         jMenuItem_SaveSequence.setText("Save Seqeuence");
         jMenuItem_SaveSequence.addActionListener(new java.awt.event.ActionListener()
         {
         public void actionPerformed(java.awt.event.ActionEvent e)
            {
            expressomaticVeiw.Save();
            }
         });
         }
      return jMenuItem_SaveSequence;
      }

   /**
    * This method initializes jMenuItem_SaveAsSequence
    *
    * @return javax.swing.JMenuItem
    */
   private JMenuItem getJMenuItem_SaveAsSequence()
      {
      if (jMenuItem_SaveAsSequence == null)
         {
         jMenuItem_SaveAsSequence = new JMenuItem();
         jMenuItem_SaveAsSequence.setText("Save Sequence As");
         jMenuItem_SaveAsSequence.addActionListener(new java.awt.event.ActionListener()
         {
         public void actionPerformed(java.awt.event.ActionEvent e)
            {
            expressomaticVeiw.SaveAs();
            }
         });
         }
      return jMenuItem_SaveAsSequence;
      }

   /**
    * This method initializes messengerConnectButton
    *
    * @return javax.swing.JButton
    */
   private JButton getMessengerConnectButton()
      {
      if (messengerConnectButton == null)
         {
         messengerConnectButton = getConnectDisconnectButton();
         messengerConnectButton.setBounds(new Rectangle(93, 24, 100, 19));
         }
      return messengerConnectButton;
      }

   /**
    * This method initializes messengerConnectStatusPanel
    *
    * @return javax.swing.JPanel
    */
   private JPanel getMessengerConnectStatusPanel()
      {
      if (messengerConnectStatusPanel == null)
         {
         messengerConnectStatusPanel = getConnectionStatePanel();
         messengerConnectStatusPanel.setBounds(new Rectangle(92, 46, 250, 32));
         messengerConnectStatusPanel.setBackground(Color.white);
         }
      return messengerConnectStatusPanel;
      }

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(new Runnable()
      {
      public void run()
         {
         new RoboticonMessenger();
         }
      });
      }

   private RoboticonMessengerServerController roboticonMessengerServerController;

   // peer info stuff
   private final PeerImageFactory peerImageFactory = new HTTPPeerImageFactory(
         RESOURCES.getString("terk-web-site.host"), RESOURCES
         .getString("terk-web-site.avatar-image-loader-prefix"));

   private final PeerInfoModel peerInfoModel = new PeerInfoModel(
         peerImageFactory);

   private final PeerInfoView peerInfoView = new PeerInfoView(peerInfoModel,
                                                              true);
   private final PeerInfoController peerInfoController =
         new PeerInfoController()
         {
         public void setAttribute(final String key, final String value)
            {
            if (roboticonMessengerServerController != null)
               {
               roboticonMessengerServerController.getPeerInfoService().setAttribute(key, value);
               }
            }
         };

   // messaging stuff
   private final RoboticonMessengerModel roboticonMessengerModel = new RoboticonMessengerModel(false);

   private final RoboticonMessengerController roboticonMessengerController = new RoboticonMessengerController()
   {
   public void sendPublicMessage(final String parentMessageId,
                                 final ClientRoboticonMessage clientRoboticonMessage)
      {
      if (roboticonMessengerServerController != null)
         {
         roboticonMessengerServerController
               .getRoboticonMessengerService().sendPublicMessage(
               parentMessageId, clientRoboticonMessage);
         }
      }

   public void sendPrivateMessage(final String parentMessageId,
                                  final String recipientUserId,
                                  final ClientRoboticonMessage clientRoboticonMessage)
      {
      if (roboticonMessengerServerController != null)
         {
         roboticonMessengerServerController
               .getRoboticonMessengerService().sendPrivateMessage(
               parentMessageId, recipientUserId,
               clientRoboticonMessage);
         }
      }
   };

   private final RoboticonManagerModel privateRoboticonManagerModel = new RoboticonManagerModel(
         true);

   private final RoboticonManagerModel publicRoboticonManagerModel = new RoboticonManagerModel(
         false);

   private final RoboticonMessengerView roboticonMessengerView = new RoboticonMessengerView(
         this, roboticonMessengerController, roboticonMessengerModel,
         peerInfoModel, new RoboticonManagerController()
         {
         public void addRoboticons(
               final Collection<RoboticonFile> roboticonFiles)
            {
            publicRoboticonManagerModel.addRoboticons(roboticonFiles);
            }
         });

   private final RoboticonManagerView roboticonManagerView = new RoboticonManagerView(
         privateRoboticonManagerModel, publicRoboticonManagerModel);

   private final ExpressOMaticView expressomaticVeiw = new ExpressOMaticView(this);
   private JMenuBar jJMenuBar_Main = null;

   private JMenu jMenu_ExpressOMatic = null;

   private JMenu jMenu_Messenger = null;

   private JPanel jPanel_Main = null;

   private JPanel jPanel_North = null;

   //private JPanel jPanel_West = null;

   private JPanel jPanel_Center = null;

   private JSplitPane jSplitPane_ViewWriteMessageSplit = null;

   private JPanel jPanel_ViewMessages = null;

   private JPanel jPanel_WriteMessage = null;

   private JTabbedPane jTabbedPane_Messages = null;  //  @jve:decl-index=0:visual-constraint="662,-5"

   private JPanel jPanel_Public = null;

   private JPanel jPanel_ExpressOMatic = null;

   private JPanel jPanel_RoboticonList = null;

   private JPanel jPanel_UserList = null;

   private JMenuItem jMenuItem_ShowExpressOMatic = null;

   private JMenuItem jMenuItem_HideExpressOMatic = null;

   //private boolean expressOMaticShown = true;
   private boolean expressOMaticShown = false;

   private JMenuItem jMenuItem_ShowMessenger = null;

   private JMenuItem jMenuItem_HideMessenger = null;

   private boolean messengerShown = false;

   private JLabel cmuLabel = null;

   final JLabel applicationNameLabel = new JLabel(APPLICATION_NAME);

   private JPanel labelPanel = null;

   private JButton userIconButton = null;

   private JLabel connectedUserIdLabel = new JLabel(" ");

   private JPanel jPanel_MessageHistory = null;

   private JSplitPane jSplitPane_ExpressOMaticRoboticonSplit = null;

   private JSplitPane jSplitPane_RoboticonMessageSplit = null;

   private JMenuItem jMenuItem_NewSequence = null;

   private JMenuItem jMenuItem_OpenSequence = null;

   private JMenuItem jMenuItem_SaveSequence = null;

   private JMenuItem jMenuItem_SaveAsSequence = null;

   private JButton messengerConnectButton = null;

   private JPanel messengerConnectStatusPanel = null;

   protected RoboticonMessenger()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, null, false);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterRelayLogin()
               {
               if (getRelayCommunicator() != null)
                  {
                  String userId = getRelayCommunicator().getUserId();
                  if (userId == null)
                     {
                     userId = RESOURCES.getString("unknown-user-id");
                     }
                  connectedUserIdLabel.setText(userId);
                  userIconButton.setIcon(peerInfoModel.getUserIcon(userId, 2));
                  roboticonMessengerModel.setUserId(userId);
                  }
               }

            public void executeAfterRelayLogout()
               {
               connectedUserIdLabel.setText(" ");
               userIconButton.setIcon(null);
               }

            public void toggleGUIElementState(final boolean isConnectedToPeer)
               {
               toggleGUIElements(isConnectedToPeer);
               }

            public Set<ObjectPrx> createAndRegisterSecondaryServantsAndReturnTheirProxies(
                  final TerkCommunicator terkCommunicator,
                  final ServiceServantRegistrar serviceServantRegistrar)
               {
               final Set<ObjectPrx> secondaryServantProxies = new HashSet<ObjectPrx>(2);

               final ObjectImpl roboticonMessagingClientServiceServant = new RoboticonMessagingClientServiceServant(
                     roboticonMessengerModel);
               final ObjectPrx untypedRoboticonMessagingClientServiceServantProxy = terkCommunicator
                     .createServantProxy(roboticonMessagingClientServiceServant);
               final RoboticonMessagingClientServicePrx roboticonMessagingClientServicePrx = RoboticonMessagingClientServicePrxHelper
                     .uncheckedCast(untypedRoboticonMessagingClientServiceServantProxy);
               secondaryServantProxies.add(roboticonMessagingClientServicePrx);
               serviceServantRegistrar.registerServiceServant(
                     roboticonMessagingClientServiceServant,
                     roboticonMessagingClientServicePrx);

               final ObjectImpl peerInfoClientServiceServant = new PeerInfoClientServiceServant(
                     peerInfoModel);
               final ObjectPrx untypedPeerInfoClientServiceServantProxy = terkCommunicator
                     .createServantProxy(peerInfoClientServiceServant);
               final PeerInfoClientServicePrx peerInfoClientServiceServantProxy = PeerInfoClientServicePrxHelper
                     .uncheckedCast(untypedPeerInfoClientServiceServantProxy);
               secondaryServantProxies.add(peerInfoClientServiceServantProxy);
               serviceServantRegistrar.registerServiceServant(peerInfoClientServiceServant,
                                                              peerInfoClientServiceServantProxy);

               return secondaryServantProxies;
               }
            });
      initialize();
      this.setLocationRelativeTo(null);// center the window on the screen
      }

   private void toggleGUIElements(final boolean isConnectedToPeer)
      {
      roboticonMessengerView.setEnabled(isConnectedToPeer);
      //roboticonManagerView.setIsSupported(isConnectedToPeer); //don't think we should be calling this. The roboticon lists should always be enabled.
      peerInfoView.setEnabled(isConnectedToPeer);
      //expressomaticVeiw.setIsSupported(isConnectedToPeer); //this is the wrong peer. It is the messenger server not the robot. ExpressOMatic should be enabled when connected to the robot.
      }

   private void initialize()
      {
      //original code

      toggleGUIElements(false);

      roboticonMessengerModel
            .addRoboticonMessengerListener(roboticonMessengerView);
      privateRoboticonManagerModel
            .addRoboticonManagerListener(roboticonManagerView);
      peerInfoModel.addPeerInfoListener(peerInfoView);
      peerInfoModel.addPeerInfoListener(roboticonMessengerView);

      applicationNameLabel.setForeground(Color.blue);
      applicationNameLabel.setBounds(new Rectangle(0, 15, 213, 34));
      applicationNameLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
      applicationNameLabel.setVerticalAlignment(SwingConstants.BOTTOM);
      applicationNameLabel.setFont(GUIConstants.FONT_LARGE);

      connectedUserIdLabel.setFont(GUIConstants.FONT_MEDIUM_BOLD);
      connectedUserIdLabel.setBounds(new Rectangle(93, 2, 197, 18));
      //connectedUserIdLabel.setForeground(Color.cyan);

      roboticonMessengerView.setEnabled(false);
      //roboticonManagerView.setIsSupported(false); //this should always be enabled
      expressomaticVeiw.setEnabled(false);

      peerInfoView.setEnabled(false);

      setCustomRelayPeerConnectionEventListener(new PeerConnectionEventAdapter()
      {
      public void handlePeerConnectedEvent(final String peerUserId,
                                           final PeerAccessLevel peerAccessLevel,
                                           final ObjectPrx peerObjectProxy)
         {
         final TerkUserPrx terkUserProxy = TerkUserPrxHelper
               .checkedCast(peerObjectProxy);
         if (terkUserProxy != null)
            {
            roboticonMessengerServerController = new RoboticonMessengerServerController(
                  peerUserId, terkUserProxy, getRelayCommunicator());

            if (roboticonMessengerServerController
                  .isRoboticonMessagingSupported())
               {
               final List<RoboticonMessage> messageHistory = roboticonMessengerServerController.getRoboticonMessengerService().getMessageHistory();
               roboticonMessengerModel.setMessageHistory(messageHistory);
               }
            if (roboticonMessengerServerController
                  .isPeerInfoSupported())
               {
               final List<PeerInfo> allPeerInfo = roboticonMessengerServerController
                     .getPeerInfoService().getPeerInfo();
               peerInfoModel.setPeerInfo(allPeerInfo);
               connectedUserIdLabel.setForeground(peerInfoModel.getUserColor(roboticonMessengerModel.getUserId()));
               }
            }
         else
            {
            LOG.info("Ignoring peer [" + peerUserId
                     + "] since it is not a TerkUser.");
            }
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         roboticonMessengerServerController = null;
         }
      });

      // LAYOUT GUI ELEMENTS ===========================================================================================

      getJPanel_MessageHistory().add(
            roboticonMessengerView.getMessageHistoryComponent(),
            BorderLayout.CENTER);

      this.setVisible(true);
      //this.setMinimumSize(new Dimension(620, 480));
      this.setMinimumSize(new Dimension(150, 480));
      this.setResizable(true);
      this.setContentPane(getJPanel_Main());
      this.setJMenuBar(getJJMenuBar_Main());
      ExpressOMaticDisplay();
      MessengerDisplay();
      //this.setSize(new Dimension(620, 730));
      this.setSize(new Dimension(850, 730));
      }

   /**
    * Enables or disables all of the widgets and Actions, but assumes it's
    * executing in the Swing event dispatch thread.
    */
   private void setWidgetsAndActionsEnabledWorkhorse(
         final boolean isConnectedToRobot)
      {
      getConnectDisconnectButton().setText(isConnectedToRobot ? RESOURCES
            .getString("button.label.disconnect") : RESOURCES
            .getString("button.label.connect"));
      toggleGUIElements(isConnectedToRobot);

      //TODO: take care of expresso connect
      }
   }//  @jve:decl-index=0:visual-constraint="10,-17"
