package edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import Ice.ObjectPrx;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect.DirectConnectCommunicatorManager;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import edu.cmu.ri.mrpl.util.net.HostAndPort;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DirectConnectDescriptor extends WizardPanelDescriptor implements ActionListener
   {
   private static final Logger LOG = Logger.getLogger(DirectConnectDescriptor.class);

   public static final String IDENTIFIER = DirectConnectForm.class.getName();
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DirectConnectDescriptor.class.getName());

   private final DirectConnectForm panel = new DirectConnectForm(this);
   private final PeerConnectionEventListener peerConnectionEventListener = new MyPeerConnectionEventListener();
   private final DirectConnectCommunicatorManager directConnectCommunicatorManager;
   private boolean isCurrentlyVisible = false;
   private final Runnable updateWidgetsRunnable =
         new Runnable()
         {
         public void run()
            {
            updateWidgetsAccordingToConnectionState();
            setFocusAppropriately();
            }
         };

   public DirectConnectDescriptor(final DirectConnectCommunicatorManager directConnectCommunicatorManager)
      {
      this.directConnectCommunicatorManager = directConnectCommunicatorManager;
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new DirectConnectCommunicatorCreationEventAdapater());

      setPanelDescriptorIdentifier(IDENTIFIER);
      setPanelComponent(panel);
      }

   private DirectConnectCommunicator getDirectConnectCommunicator()
      {
      return (DirectConnectCommunicator)directConnectCommunicatorManager.getTerkCommunicator();
      }

   public Object getBackPanelDescriptor()
      {
      return PeerConnectionMethodDescriptor.IDENTIFIER;
      }

   public Object getNextPanelDescriptor()
      {
      return FINISH;
      }

   private void setFocusAppropriately()
      {
      panel.getHostTextField().requestFocusInWindow();
      if (panel.isHostFieldNonEmpty())
         {
         panel.getHostTextField().selectAll();
         }
      }

   public void aboutToDisplayPanel()
      {
      isCurrentlyVisible = true;

      if (directConnectCommunicatorManager.isCreated())
         {
         updateWidgetsRunnable.run();
         }
      else
         {
         disableWidgetsAndSetWaitCursor();

         directConnectCommunicatorManager.createCommunicator();
         }
      }

   public void aboutToHidePanel()
      {
      isCurrentlyVisible = false;
      }

   public void actionPerformed(final ActionEvent event)
      {
      if (panel.isHostFieldNotEmptyAndValid())
         {
         disableWidgetsAndSetWaitCursor();

         if (getDirectConnectCommunicator().isConnectedToPeer())
            {
            new DirectConnectDescriptor.DisconnectWorker().start();
            }
         else
            {
            new DirectConnectDescriptor.ConnectWorker().start();
            }
         }
      else
         {
         JOptionPane.showMessageDialog(getWizard().getDialog(),
                                       RESOURCES.getString("dialog.message.invalid-hostname"),
                                       RESOURCES.getString("dialog.title.invalid-hostname"),
                                       JOptionPane.INFORMATION_MESSAGE);
         panel.getHostTextField().requestFocusInWindow();
         panel.getHostTextField().selectAll();
         }
      }

   /** Used for creating a connection without actually having to use the GUI (yes, this is kinda ugly and hackish). */
   public void doHeadlessConnectToPeer(final String peerIdentifier)
      {
      directConnectCommunicatorManager.addTerkCommunicatorCreationEventListener(new HeadlessDirectConnectCommunicatorCreationEventAdapater(peerIdentifier));
      directConnectCommunicatorManager.createCommunicator();
      }

   private void disableWidgetsAndSetWaitCursor()
      {
      getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      panel.getHostTextField().setEnabled(false);
      panel.getSubmitButton().setEnabled(false);
      getWizard().setBackButtonEnabled(false);
      getWizard().setNextFinishButtonEnabled(false);
      getWizard().setCancelButtonEnabled(false);
      }

   private void updateWidgetsAccordingToConnectionState()
      {
      final boolean isDirectlyConnectedToPeer = getDirectConnectCommunicator() != null && getDirectConnectCommunicator().isConnectedToPeer();
      panel.getHostTextField().setEnabled(!isDirectlyConnectedToPeer);
      panel.getSubmitButton().setText(isDirectlyConnectedToPeer ? RESOURCES.getString("button.disconnect") : RESOURCES.getString("button.connect"));
      panel.enableSubmitButtonIfHostFieldIsNotEmptyAndValid();

      getWizard().setBackButtonEnabled(true);
      getWizard().setNextFinishButtonEnabled(isDirectlyConnectedToPeer);
      getWizard().setCancelButtonEnabled(true);

      getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }

   private abstract class ConnectDisconnectWorker extends SwingWorker
      {
      public final Object construct()
         {
         doTimeConsumingAction();
         return null;
         }

      protected abstract void doTimeConsumingAction();

      public final void finished()
         {
         updateWidgetsAccordingToConnectionState();
         }
      }

   private final class ConnectWorker extends ConnectDisconnectWorker
      {
      protected final void doTimeConsumingAction()
         {
         // do the connection
         try
            {
            getDirectConnectCommunicator().connectToPeer(getHostnameFromGUI());
            }
         catch (Exception e)
            {
            LOG.error("Exception while trying to connect to the host: ", e);
            try
               {
               SwingUtilities.invokeAndWait(
                     new Runnable()
                     {
                     public void run()
                        {
                        JOptionPane.showMessageDialog(getWizard().getDialog(),
                                                      RESOURCES.getString("dialog.message.connect-failed"),
                                                      RESOURCES.getString("dialog.title.connect-failed"),
                                                      JOptionPane.INFORMATION_MESSAGE);
                        panel.getHostTextField().requestFocusInWindow();
                        panel.getHostTextField().selectAll();
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

   private final class DisconnectWorker extends ConnectDisconnectWorker
      {
      protected void doTimeConsumingAction()
         {
         final String hostname = getHostnameFromGUI();
         getDirectConnectCommunicator().disconnectFromPeer(hostname);
         }
      }

   private String getHostnameFromGUI()
      {
      // fetch the hostname from the GUI
      final String[] hostname = new String[1];

      try
         {
         SwingUtilities.invokeAndWait(
               new Runnable()
               {
               public void run()
                  {
                  hostname[0] = panel.getHostTextField().getText();
                  }
               });
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while getting the host from the form", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while getting the host from the form", e);
         }

      return hostname[0];
      }

   private class DirectConnectCommunicatorCreationEventAdapater extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         SwingUtilities.invokeLater(updateWidgetsRunnable);
         terkCommunicator.addPeerConnectionEventListener(peerConnectionEventListener);
         }

      public void afterFailedConstruction()
         {
         panel.getHostTextField().setEnabled(false);
         panel.getSubmitButton().setEnabled(false);

         JOptionPane.showMessageDialog(getWizard().getDialog(),
                                       RESOURCES.getString("dialog.message.direct-connect-communicator-creation-failed"),
                                       RESOURCES.getString("dialog.title.direct-connect-communicator-creation-failed"),
                                       JOptionPane.INFORMATION_MESSAGE);

         getWizard().setBackButtonEnabled(true);
         getWizard().setNextFinishButtonEnabled(false);
         getWizard().setCancelButtonEnabled(true);

         getPanelComponent().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         }
      }

   private final class MyPeerConnectionEventListener extends PeerConnectionEventAdapter
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         refreshIfVisible();
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         refreshIfVisible();
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         refreshIfVisible();
         }

      private void refreshIfVisible()
         {
         if (DirectConnectDescriptor.this.getWizard().getDialog().isVisible() && DirectConnectDescriptor.this.isCurrentlyVisible)
            {
            SwingUtilities.invokeLater(updateWidgetsRunnable);
            }
         }
      }

   private class HeadlessDirectConnectCommunicatorCreationEventAdapater extends DirectConnectCommunicatorCreationEventAdapater
      {
      private final String peerIdentifer;

      private HeadlessDirectConnectCommunicatorCreationEventAdapater(final String peerIdentifer)
         {
         this.peerIdentifer = peerIdentifer;
         }

      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         super.afterSuccessfulConstruction(terkCommunicator);

         // set the peer identifer (i.e. the hostname) in the GUI
         panel.hostTextField.setText(peerIdentifer);

         // simulate a click on the connect button
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  actionPerformed(null);
                  }
               });
         }
      }

   private static final class DirectConnectForm extends JPanel
      {
      private final JTextField hostTextField = new JTextField(10);
      private final JButton submitButton = new JButton();
      private final KeyAdapter textFieldKeyListener =
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               enableSubmitButtonIfHostFieldIsNotEmptyAndValid();
               }
            };

      private DirectConnectForm(final ActionListener actionListener)
         {
         setLayout(new SpringLayout());

         hostTextField.addKeyListener(textFieldKeyListener);

         submitButton.setEnabled(false);
         submitButton.setText(RESOURCES.getString("button.connect"));
         submitButton.addActionListener(actionListener);

         hostTextField.addActionListener(actionListener);//connect on hit <ENTER>

         final JPanel connectPanel = new JPanel(new SpringLayout());
         connectPanel.add(new JLabel(RESOURCES.getString("label.host")));
         connectPanel.add(hostTextField);
         SpringLayoutUtilities.makeCompactGrid(connectPanel,
                                               1, 2, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad

         add(new JLabel(RESOURCES.getString("instructions")));
         add(Box.createGlue());
         add(connectPanel);
         add(Box.createGlue());
         add(submitButton);
         SpringLayoutUtilities.makeCompactGrid(this,
                                               5, 1, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad
         }

      private void enableSubmitButtonIfHostFieldIsNotEmptyAndValid()
         {
         submitButton.setEnabled(isHostFieldNotEmptyAndValid());
         }

      private boolean isHostFieldNotEmptyAndValid()
         {
         return isHostFieldNonEmpty() && isHostFieldValid();
         }

      private boolean isHostFieldValid()
         {
         return HostAndPort.isValid(hostTextField.getText());
         }

      private boolean isHostFieldNonEmpty()
         {
         final String text1 = hostTextField.getText();
         final String trimmedText1 = (text1 != null) ? text1.trim() : null;
         return (trimmedText1 != null) && (trimmedText1.length() > 0);
         }

      private JTextField getHostTextField()
         {
         return hostTextField;
         }

      private JButton getSubmitButton()
         {
         return submitButton;
         }
      }
   }
