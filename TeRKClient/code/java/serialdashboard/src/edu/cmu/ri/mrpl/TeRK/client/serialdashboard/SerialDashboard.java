package edu.cmu.ri.mrpl.TeRK.client.serialdashboard;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.serial.QwerkSerialPortDevice;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkControllerProvider;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialDashboard extends BaseGUIClient
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(SerialDashboard.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/serialdashboard/SerialDashboard.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/serialdashboard/SerialDashboard.relay.ice.properties";

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new SerialDashboard();
               }
            });
      }

   private CardLayout serialPortCardsLayout = new CardLayout();
   private final JPanel serialPortCards = new JPanel(serialPortCardsLayout);
   private final Collection<DashboardContentPanel> dashboardContentPanels = new ArrayList<DashboardContentPanel>();
   private final JComboBox serialPortDeviceComboBox = new JComboBox(QwerkSerialPortDevice.values());
   private final QwerkControllerProvider qwerkControllerProvider =
         new QwerkControllerProvider()
         {
         public QwerkController getQwerkController()
            {
            return SerialDashboard.this.getQwerkController();
            }
         };

   private SerialDashboard()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterRelayLogin()
               {
               appendMessage("Logged in to relay.");
               }

            public void executeAfterRelayLogout()
               {
               appendMessage("Logged out from relay.");
               }

            public void executeBeforeDisconnectingFromQwerk()
               {
               appendMessage("Disconnecting from qwerk...");
               super.executeBeforeDisconnectingFromQwerk();
               }

            public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
               {
               appendMessage("Connected to qwerk " + qwerkUserId);
               }

            public void executeAfterDisconnectingFromQwerk(final String qwerkUserId)
               {
               appendMessage("Disconnected from qwerk " + qwerkUserId);
               }

            public void toggleGUIElementState(final boolean isConnectedToQwerk)
               {
               serialPortDeviceComboBox.setEnabled(isConnectedToQwerk);
               for (final DashboardContentPanel dashboardContentPanel : dashboardContentPanels)
                  {
                  dashboardContentPanel.toggleGUIElementState(isConnectedToQwerk);
                  }
               }
            });
      // CONFIGURE GUI ELEMENTS ========================================================================================

      serialPortDeviceComboBox.setSelectedItem(QwerkSerialPortDevice.DEFAULT);
      serialPortDeviceComboBox.setEnabled(false);
      serialPortDeviceComboBox.addItemListener(
            new ItemListener()
            {
            public void itemStateChanged(final ItemEvent itemEvent)
               {
               final CardLayout cardLayout = (CardLayout)serialPortCards.getLayout();
               cardLayout.show(serialPortCards, itemEvent.getItem().toString());
               }
            });

      for (final QwerkSerialPortDevice device : QwerkSerialPortDevice.values())
         {
         final DashboardContentPanel dashboardContentPanel = new DashboardContentPanel(device.getName(), qwerkControllerProvider);
         dashboardContentPanels.add(dashboardContentPanel);
         serialPortCards.add(dashboardContentPanel, device.toString());
         }
      serialPortCardsLayout.show(serialPortCards, QwerkSerialPortDevice.DEFAULT.toString());

      // LAYOUT GUI ELEMENTS ===========================================================================================

      final JPanel connectionPanel = new JPanel(new SpringLayout());
      connectionPanel.add(getConnectDisconnectButton());
      connectionPanel.add(getConnectionStatePanel());
      connectionPanel.add(Box.createGlue());
      connectionPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.serial-port-device")));
      connectionPanel.add(serialPortDeviceComboBox);
      SpringLayoutUtilities.makeCompactGrid(connectionPanel,
                                            1, 5, // rows, cols
                                            0, 0, // initX, initY
                                            10, 10);// xPad, yPad

      // Layout the main content pane using SpringLayout
      getMainContentPane().setLayout(new SpringLayout());
      getMainContentPane().add(connectionPanel);
      getMainContentPane().add(serialPortCards);
      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            2, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 10);// xPad, yPad

      // ADDITIONAL GUI ELEMENT CONFIGURATION ==========================================================================

      pack();
      setLocationRelativeTo(null);// center the window on the screen
      setVisible(true);
      }

   /** Appends the given <code>message</code> to the message text areas */
   private void appendMessage(final String message)
      {
      for (final DashboardContentPanel dashboardContentPanel : dashboardContentPanels)
         {
         dashboardContentPanel.appendMessage(message);
         }
      }
   }
