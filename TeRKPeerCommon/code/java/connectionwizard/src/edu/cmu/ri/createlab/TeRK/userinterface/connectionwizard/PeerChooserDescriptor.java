package edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import Ice.ConnectionLostException;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayPeerConnectionManager;
import edu.cmu.ri.mrpl.peer.ConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.ConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerException;
import edu.cmu.ri.mrpl.peer.PeerIdentifier;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerChooserDescriptor extends WizardPanelDescriptor implements ActionListener
   {
   private static final Logger LOG = Logger.getLogger(PeerChooserDescriptor.class);
   private static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
   private static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);

   public static final String IDENTIFIER = PeerChooser.class.getName();

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(PeerChooserDescriptor.class.getName());
   private final boolean backEnabled;
   private final PeerChooser panel;
   private final ConnectionEventListener connectionEventListener = new MyConnectionEventListener();
   private final RelayCommunicatorManager relayCommunicatorManager;
   private final Runnable refreshPanelRunnable =
         new Runnable()
         {
         public void run()
            {
            refreshPanel();
            }
         };

   private boolean isCurrentlyVisible = false;

   private final class MyRelayCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         SwingUtilities.invokeLater(refreshPanelRunnable);
         ((RelayCommunicator)terkCommunicator).addConnectionEventListener(connectionEventListener);
         }

      public void afterFailedConstruction()
         {
         JOptionPane.showMessageDialog(getWizard().getDialog(),
                                       RESOURCES.getString("dialog.message.relay-communicator-creation-failed"),
                                       RESOURCES.getString("dialog.title.relay-communicator-creation-failed"),
                                       JOptionPane.INFORMATION_MESSAGE);

         getWizard().setBackButtonEnabled(backEnabled);
         getWizard().setNextFinishButtonEnabled(false);
         getWizard().setCancelButtonEnabled(true);

         getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         }
      }

   public PeerChooserDescriptor(final RelayCommunicatorManager relayCommunicatorManager)
      {
      this(relayCommunicatorManager, true);
      }

   public PeerChooserDescriptor(final RelayCommunicatorManager relayCommunicatorManager, boolean backEnabled)
      {
      this.backEnabled = backEnabled;
      this.relayCommunicatorManager = relayCommunicatorManager;
      relayCommunicatorManager.addTerkCommunicatorCreationEventListener(new MyRelayCommunicatorCreationEventAdapater());
      panel = new PeerChooser(RESOURCES, this,
                              new ActionListener()
                              {
                              public void actionPerformed(final ActionEvent e)
                                 {
                                 refreshPanel();
                                 }
                              });
      setPanelDescriptorIdentifier(IDENTIFIER);
      setPanelComponent(panel);
      }

   private RelayPeerConnectionManager getRelayPeerConnectionManager()
      {
      return (RelayPeerConnectionManager)relayCommunicatorManager.getTerkCommunicator();
      }

   public Object getBackPanelDescriptor()
      {
      return RelayLoginFormDescriptor.IDENTIFIER;
      }

   public Object getNextPanelDescriptor()
      {
      return FINISH;
      }

   public void aboutToDisplayPanel()
      {
      isCurrentlyVisible = true;

      if (relayCommunicatorManager.isCreated())
         {
         refreshPanelRunnable.run();
         }
      else
         {
         disableWidgetsAndSetWaitCursor();

         relayCommunicatorManager.createCommunicator();
         }
      }

   public void aboutToHidePanel()
      {
      LOG.debug("PeerChooserDescriptor.aboutToHidePanel()");
      isCurrentlyVisible = false;
      }

   private void disableWidgetsAndSetWaitCursor()
      {
      getPanelComponent().setCursor(WAIT_CURSOR);
      panel.getConnectDisconnectButton().setEnabled(false);
      panel.getRefreshButton().setEnabled(false);
      panel.getOnlineList().setEnabled(false);
      getWizard().setBackButtonEnabled(false);
      getWizard().setNextFinishButtonEnabled(false);
      getWizard().setCancelButtonEnabled(false);
      }

   private void refreshPanel()
      {
      disableWidgetsAndSetWaitCursor();

      final SortedSet<ListablePeerIdentifier> onlineListablePeers = new TreeSet<ListablePeerIdentifier>();
      final SortedSet<ListablePeerIdentifier> offlineListablePeers = new TreeSet<ListablePeerIdentifier>();

      final Boolean[] isConnectedToPeer = new Boolean[1];
      isConnectedToPeer[0] = Boolean.FALSE;

      final SwingWorker worker =
            new SwingWorker()
            {
            public Object construct()
               {
               try
                  {
                  // first see if we're connected to a peer
                  isConnectedToPeer[0] = isConnectedToPeer();

                  // get available peers and convert them to listable peers for the GUI
                  final Set<PeerIdentifier> onlinePeers = getRelayPeerConnectionManager().getMyAvailablePeers();
                  if ((onlinePeers != null) && (!onlinePeers.isEmpty()))
                     {
                     for (final PeerIdentifier peer : onlinePeers)
                        {
                        onlineListablePeers.add(new ListablePeerIdentifier(peer));
                        }
                     }

                  // get unavailable peers and convert them to listable peers for the GUI
                  final Set<PeerIdentifier> offlinePeers = getRelayPeerConnectionManager().getMyUnavailablePeers();
                  if ((offlinePeers != null) && (!offlinePeers.isEmpty()))
                     {
                     for (final PeerIdentifier peer : offlinePeers)
                        {
                        offlineListablePeers.add(new ListablePeerIdentifier(peer));
                        }
                     }
                  }
               catch (PeerException e)
                  {
                  LOG.error("PeerException while trying to refresh the panel", e);
                  }
               catch (ConnectionLostException e)
                  {
                  LOG.error("ConnectionLostException while trying to refresh the panel", e);
                  }

               // return null, since everything we need is stored in collections declared above
               return null;
               }

            @SuppressWarnings({"unchecked"})
            public void finished()
               {
               // clear the current contents of the list model
               final DefaultListModel onlineListModel = panel.getOnlineListModel();
               final DefaultListModel offlineListModel = panel.getOfflineListModel();
               onlineListModel.clear();
               offlineListModel.clear();

               // update the list models with the new lists of peers
               for (final ListablePeerIdentifier listablePeer : onlineListablePeers)
                  {
                  onlineListModel.addElement(listablePeer);
                  }
               for (final ListablePeerIdentifier listablePeer : offlineListablePeers)
                  {
                  offlineListModel.addElement(listablePeer);
                  }

               // now set all the widgets appropriately
               panel.setConnectedToPeer(isConnectedToPeer[0]);
               panel.getOnlineList().setEnabled(!isConnectedToPeer[0]);
               panel.getOnlineListScrollPane().setEnabled(!isConnectedToPeer[0]);
               panel.getConnectDisconnectButton().setEnabled(isConnectedToPeer[0] || (panel.isOnlineTabCurrentlyActive() && panel.isAPeerChosenInTheList()));
               panel.getConnectDisconnectButton().setText(isConnectedToPeer[0] ? RESOURCES.getString("button.disconnect") : RESOURCES.getString("button.connect"));
               panel.getRefreshButton().setEnabled(true);

               getWizard().setBackButtonEnabled(backEnabled);
               getWizard().setNextFinishButtonEnabled(isConnectedToPeer[0]);
               getWizard().setCancelButtonEnabled(true);

               getPanelComponent().setCursor(DEFAULT_CURSOR);
               }
            };
      worker.start();
      }

   public void actionPerformed(final ActionEvent event)
      {
      disableWidgetsAndSetWaitCursor();

      final ListablePeerIdentifier listablePeerIdentifier = (ListablePeerIdentifier)panel.getOnlineList().getSelectedValue();

      // see if I'm actually connected to a peer
      final SwingWorker worker =
            new SwingWorker()
            {
            public Object construct()
               {
               final boolean connectedToPeer;
               try
                  {
                  connectedToPeer = isConnectedToPeer();
                  }
               catch (Exception e)
                  {
                  LOG.error("Exception while trying to determine whether we're current connected to a peer", e);
                  return null;
                  }

               if (connectedToPeer)
                  {
                  getRelayPeerConnectionManager().disconnectFromPeers();
                  }
               else
                  {
                  if (listablePeerIdentifier != null)
                     {
                     try
                        {
                        getRelayPeerConnectionManager().connectToPeer(listablePeerIdentifier.peerIdentifier.userId);
                        }
                     catch (Exception e)
                        {
                        // if I'm not actually connected to a peer, but I think I am (which can happen if the peer disconnects
                        // from me while I'm using the wizard), then just update the display and don't display an error to user.
                        if (panel.isConnectedToPeer())
                           {
                           if (LOG.isEnabledFor(Level.ERROR))
                              {
                              LOG.error("Exception while trying to connect to a peer: ", e);
                              }
                           try
                              {
                              SwingUtilities.invokeAndWait(
                                    new Runnable()
                                    {
                                    public void run()
                                       {
                                       final String message = RESOURCES.getString("dialog.message.connection-failed");
                                       final String title = RESOURCES.getString("dialog.title.connection-failed");
                                       JOptionPane.showMessageDialog(getWizard().getDialog(), message, title, JOptionPane.INFORMATION_MESSAGE);
                                       }
                                    });
                              }
                           catch (InterruptedException ie)
                              {
                              LOG.error("InterruptedException while displaying the login failed message", ie);
                              }
                           catch (InvocationTargetException ite)
                              {
                              LOG.error("InvocationTargetException while displaying the login failed message", ite);
                              }
                           }
                        }
                     }
                  else
                     {
                     LOG.debug("ListablePeerIdentifier was null, so I won't try to connect.");
                     }
                  }
               return null;
               }

            public void finished()
               {
               refreshPanel();
               }
            };
      worker.start();
      }

   private boolean isConnectedToPeer() throws PeerException
      {
      final Set<PeerIdentifier> connectedPeers = getRelayPeerConnectionManager().getConnectedPeers();
      return connectedPeers != null && connectedPeers.size() > 0;
      }

   private static final class ListablePeerIdentifier implements Comparable
      {
      private final PeerIdentifier peerIdentifier;

      private ListablePeerIdentifier(final PeerIdentifier peerIdentifier)
         {
         this.peerIdentifier = peerIdentifier;
         }

      public boolean equals(final Object o)
         {
         if (this == o)
            {
            return true;
            }
         if (o == null || getClass() != o.getClass())
            {
            return false;
            }

         final ListablePeerIdentifier that = (ListablePeerIdentifier)o;

         return peerIdentifier.equals(that.peerIdentifier);
         }

      public int hashCode()
         {
         return peerIdentifier.hashCode();
         }

      public String toString()
         {
         final String name = (peerIdentifier.firstName).trim();

         if ((name != null) && (name.length() > 0))
            {
            return name + " (" + peerIdentifier.userId + ")";
            }

         return peerIdentifier.userId;
         }

      public int compareTo(final Object o)
         {
         return toString().compareTo(o.toString());
         }
      }

   private static final class PeerChooser extends JPanel
      {
      private final JTabbedPane tabbedPane = new JTabbedPane();
      private final JButton connectDisconnectButton = new JButton();
      private final JButton refreshButton = new JButton();
      private final DefaultListModel onlineListModel = new DefaultListModel();
      private final JList onlineList = new JList(onlineListModel);
      private final JScrollPane onlineListScrollPane = new JScrollPane(onlineList);

      private final DefaultListModel offlineListModel = new DefaultListModel();
      private final JList offlineList = new JList(offlineListModel);
      private final JScrollPane offlineListScrollPane = new JScrollPane(offlineList);

      private boolean isConnectedToPeer;

      private PeerChooser(final PropertyResourceBundle resources,
                          final ActionListener connectActionListener,
                          final ActionListener refreshActionListener)
         {
         super(new SpringLayout());

         refreshButton.setText(resources.getString("button.refresh"));
         refreshButton.addActionListener(refreshActionListener);

         enableConnectDisconnectButtonIfAPeerIsChosen();
         connectDisconnectButton.setText(resources.getString("button.connect"));
         connectDisconnectButton.addActionListener(connectActionListener);

         onlineList.addListSelectionListener(
               new ListSelectionListener()
               {
               public void valueChanged(final ListSelectionEvent e)
                  {
                  if (!e.getValueIsAdjusting())
                     {
                     enableConnectDisconnectButtonIfAPeerIsChosen();
                     }
                  }
               });

         onlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         onlineList.setLayoutOrientation(JList.VERTICAL);
         onlineList.setVisibleRowCount(-1);

         offlineList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
         offlineList.setLayoutOrientation(JList.VERTICAL);
         offlineList.setVisibleRowCount(-1);
         offlineList.setEnabled(false);

         onlineListScrollPane.setPreferredSize(new Dimension(200, 80));
         onlineListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         onlineListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

         offlineListScrollPane.setPreferredSize(new Dimension(200, 80));
         offlineListScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         offlineListScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
         offlineListScrollPane.setEnabled(false);

         final Box buttonBox = Box.createHorizontalBox();
         buttonBox.add(refreshButton);
         buttonBox.add(Box.createRigidArea(new Dimension(5, 5)));
         buttonBox.add(connectDisconnectButton);

         tabbedPane.setBorder(BorderFactory.createEmptyBorder());
         tabbedPane.addTab(RESOURCES.getString("tab.label.online"), onlineListScrollPane);
         tabbedPane.addTab(RESOURCES.getString("tab.label.offline"), offlineListScrollPane);
         tabbedPane.addChangeListener(
               new ChangeListener()
               {
               public void stateChanged(final ChangeEvent e)
                  {
                  connectDisconnectButton.setEnabled(isConnectedToPeer() || (isOnlineTabCurrentlyActive() && isAPeerChosenInTheList()));
                  }
               });

         // lay out the form nicely
         add(new JLabel(resources.getString("instructions")));
         add(Box.createGlue());
         add(tabbedPane);
         add(buttonBox);
         add(Box.createGlue());
         SpringLayoutUtilities.makeCompactGrid(this,
                                               5, 1, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad
         }

      private void enableConnectDisconnectButtonIfAPeerIsChosen()
         {
         connectDisconnectButton.setEnabled(isAPeerChosenInTheList());
         }

      private boolean isOnlineTabCurrentlyActive()
         {
         return tabbedPane.getSelectedIndex() == 0;
         }

      private boolean isAPeerChosenInTheList()
         {
         return onlineList.getSelectedIndex() != -1;
         }

      private JButton getConnectDisconnectButton()
         {
         return connectDisconnectButton;
         }

      private JButton getRefreshButton()
         {
         return refreshButton;
         }

      private DefaultListModel getOnlineListModel()
         {
         return onlineListModel;
         }

      private JList getOnlineList()
         {
         return onlineList;
         }

      private JScrollPane getOnlineListScrollPane()
         {
         return onlineListScrollPane;
         }

      private DefaultListModel getOfflineListModel()
         {
         return offlineListModel;
         }

      private boolean isConnectedToPeer()
         {
         return isConnectedToPeer;
         }

      private void setConnectedToPeer(final boolean connectedToPeer)
         {
         isConnectedToPeer = connectedToPeer;
         }
      }

   private class MyConnectionEventListener extends ConnectionEventAdapter
      {
      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         refreshIfVisible();
         }

      public void handleForcedLogoutNotificationEvent()
         {
         refreshIfVisible();
         }

      private void refreshIfVisible()
         {
         if (PeerChooserDescriptor.this.getWizard().getDialog().isVisible() && PeerChooserDescriptor.this.isCurrentlyVisible)
            {
            refreshPanel();
            }
         }
      }
   }
