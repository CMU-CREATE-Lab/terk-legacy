package edu.cmu.ri.mrpl.TeRK.client.propertyinspector;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.JTextComponent;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.digitalin.DigitalInService;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.ReadOnlyPropertyException;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOService;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PropertyInspector extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(PropertyInspector.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(PropertyInspector.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/propertyinspector/PropertyInspector.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/propertyinspector/PropertyInspector.relay.ice.properties";

   private static final Dimension TABLE_DIMENSIONS = new Dimension(300, 300);

   private static final String COLUMN_NAME_KEY = RESOURCES.getString("table.column.name.keys");
   private static final String COLUMN_NAME_VALUE = RESOURCES.getString("table.column.name.values");

   private static final String SERVICE_NAME_QWERK = RESOURCES.getString("service.name.qwerk");
   private static final String SERVICE_NAME_ANALOG = RESOURCES.getString("service.name.analog");
   private static final String SERVICE_NAME_AUDIO = RESOURCES.getString("service.name.audio");
   private static final String SERVICE_NAME_DIGITAL_IN = RESOURCES.getString("service.name.digital-in");
   private static final String SERVICE_NAME_DIGITAL_OUT = RESOURCES.getString("service.name.digital-out");
   private static final String SERVICE_NAME_LED = RESOURCES.getString("service.name.led");
   private static final String SERVICE_NAME_MOTOR = RESOURCES.getString("service.name.motor");
   private static final String SERVICE_NAME_SERIAL = RESOURCES.getString("service.name.serial");
   private static final String SERVICE_NAME_SERVO = RESOURCES.getString("service.name.servo");
   private static final String SERVICE_NAME_VIDEO = RESOURCES.getString("service.name.video");
   private static final String[] SERVICE_NAMES = new String[]{SERVICE_NAME_QWERK,
                                                              SERVICE_NAME_ANALOG,
                                                              SERVICE_NAME_AUDIO,
                                                              SERVICE_NAME_DIGITAL_IN,
                                                              SERVICE_NAME_DIGITAL_OUT,
                                                              SERVICE_NAME_LED,
                                                              SERVICE_NAME_MOTOR,
                                                              SERVICE_NAME_SERIAL,
                                                              SERVICE_NAME_SERVO,
                                                              SERVICE_NAME_VIDEO};

   private final JButton connectOrDisconnectButton = getConnectDisconnectButton();
   private final JTextField propertyKeyTextField = new JTextField(25);
   private final JTextField propertyValueTextField = new JTextField(25);
   private final JComboBox createPropertyServiceComboBox = new JComboBox(SERVICE_NAMES);
   private final JButton createPropertyButton = GUIConstants.createButton(RESOURCES.getString("button.label.create"));
   private final JComboBox viewPropertiesServiceComboBox = new JComboBox(SERVICE_NAMES);
   private final JButton reloadPropertiesButton = GUIConstants.createButton(RESOURCES.getString("button.label.reload"));
   private PropertiesTableModel propertiesTableModel = new PropertiesTableModel();
   private final JTable propertiesTable = new JTable(propertiesTableModel);

   private final Map<String, String> serviceNameToTypeIdMap;

   private final TextComponentValidator isNonEmptyValidator =
         new TextComponentValidator()
         {
         public boolean isValid(final JTextComponent textComponent)
            {
            return textComponent != null && isTextComponentNonEmpty(textComponent);
            }
         };

   private final KeyListener propertyKeyKeyListener = new EnableButtonIfTextFieldIsValidKeyAdapter(createPropertyButton, propertyKeyTextField, isNonEmptyValidator);

   private final ActionListener viewPropertiesAction = new ViewPropertiesAction();

   private final Runnable readonlyPropertyError = new ErrorMessageDialogRunnable(RESOURCES.getString("dialog.message.cannot-overwrite-read-only-property"),
                                                                                 RESOURCES.getString("dialog.title.cannot-overwrite-read-only-property"));

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new PropertyInspector();
               }
            });
      }

   private PropertyInspector()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void executeAfterEstablishingConnectionToQwerk(final String qwerkUserId)
               {
               updateViewPropertiesTable();
               }

            public void toggleGUIElementState(final boolean isEnabled)
               {
               toggleGUIElements(isEnabled);
               }
            });

      // CONFIGURE GUI ELEMENTS ========================================================================================

      this.setFocusTraversalPolicy(new MyFocusTraversalPolicy());
      createPropertyButton.addActionListener(new CreatePropertyAction());
      final Map<String, String> tempServiceNameToTypeIdMap = new HashMap<String, String>();
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_ANALOG, AnalogInputsService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_AUDIO, AudioService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_DIGITAL_IN, DigitalInService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_DIGITAL_OUT, DigitalOutService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_LED, LEDService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_MOTOR, BackEMFMotorService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_SERIAL, SerialIOService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_SERVO, ServoService.TYPE_ID);
      tempServiceNameToTypeIdMap.put(SERVICE_NAME_VIDEO, VideoStreamService.TYPE_ID);
      serviceNameToTypeIdMap = Collections.unmodifiableMap(tempServiceNameToTypeIdMap);

      viewPropertiesServiceComboBox.addItemListener(
            new ItemListener()
            {
            public void itemStateChanged(final ItemEvent itemEvent)
               {
               updateViewPropertiesTable();
               }
            });

      reloadPropertiesButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent actionEvent)
               {
               updateViewPropertiesTable();
               }
            });

      final JScrollPane propertiesTableScrollPane = new JScrollPane(propertiesTable);
      propertiesTableScrollPane.setMinimumSize(TABLE_DIMENSIONS);
      propertiesTableScrollPane.setMaximumSize(TABLE_DIMENSIONS);
      propertiesTableScrollPane.setPreferredSize(TABLE_DIMENSIONS);

      toggleGUIElements(false);
      propertyKeyTextField.addKeyListener(propertyKeyKeyListener);

      // LAYOUT GUI ELEMENTS ===========================================================================================

      // create a panel to hold the connect/disconnect button and the connection state labels
      final JPanel connectionPanel = new JPanel(new SpringLayout());
      connectionPanel.add(connectOrDisconnectButton);
      connectionPanel.add(getConnectionStatePanel());
      SpringLayoutUtilities.makeCompactGrid(connectionPanel,
                                            1, 2, // rows, cols
                                            0, 0, // initX, initY
                                            10, 10);// xPad, yPad

      final JPanel createPropertyPanel = new JPanel(new SpringLayout());
      createPropertyPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.create-property")));
      createPropertyPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.service")));
      createPropertyPanel.add(createPropertyServiceComboBox);
      createPropertyPanel.add(createPropertyButton);
      createPropertyPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.key")));
      createPropertyPanel.add(propertyKeyTextField);
      createPropertyPanel.add(Box.createGlue());
      createPropertyPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.value")));
      createPropertyPanel.add(propertyValueTextField);
      createPropertyPanel.add(Box.createGlue());
      SpringLayoutUtilities.makeCompactGrid(createPropertyPanel,
                                            3, 3, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final JPanel serviceChooserPanel = new JPanel(new SpringLayout());
      serviceChooserPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.service")));
      serviceChooserPanel.add(viewPropertiesServiceComboBox);
      serviceChooserPanel.add(reloadPropertiesButton);
      SpringLayoutUtilities.makeCompactGrid(serviceChooserPanel,
                                            1, 3, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      final JPanel viewPropertiesPanel = new JPanel(new SpringLayout());
      viewPropertiesPanel.setBorder(BorderFactory.createTitledBorder(RESOURCES.getString("border.title.view-propertes")));
      viewPropertiesPanel.add(serviceChooserPanel);
      viewPropertiesPanel.add(propertiesTableScrollPane);
      SpringLayoutUtilities.makeCompactGrid(viewPropertiesPanel,
                                            2, 1, // rows, cols
                                            5, 5, // initX, initY
                                            5, 5);// xPad, yPad

      // Layout the main content pane using SpringLayout
      getMainContentPane().setLayout(new SpringLayout());
      getMainContentPane().add(connectionPanel);
      getMainContentPane().add(createPropertyPanel);
      getMainContentPane().add(viewPropertiesPanel);
      SpringLayoutUtilities.makeCompactGrid(getMainContentPane(),
                                            3, 1, // rows, cols
                                            10, 10, // initX, initY
                                            10, 10);// xPad, yPad

      pack();
      setLocationRelativeTo(null);// center the window on the screen
      setVisible(true);
      }

   private void toggleGUIElements(final boolean isEnabled)
      {
      propertyKeyTextField.setEnabled(isEnabled);
      propertyValueTextField.setEnabled(isEnabled);
      createPropertyServiceComboBox.setEnabled(isEnabled);
      createPropertyButton.setEnabled(isEnabled && isNonEmptyValidator.isValid(propertyKeyTextField));
      viewPropertiesServiceComboBox.setEnabled(isEnabled);
      reloadPropertiesButton.setEnabled(isEnabled);
      propertiesTable.setEnabled(isEnabled);
      }

   private PropertyManager getPropertyManagerByServiceName(final String serviceName)
      {
      if (SERVICE_NAME_QWERK.equals(serviceName))
         {
         return getQwerkController();
         }
      else
         {
         final String serviceTypeId = serviceNameToTypeIdMap.get(serviceName);
         return getQwerkController().getServiceByTypeId(serviceTypeId);
         }
      }

   private void updateViewPropertiesTable()
      {
      viewPropertiesAction.actionPerformed(null);
      }

   private static boolean isTextComponentNonEmpty(final JTextComponent textField)
      {
      final String text1 = textField.getText();
      final String trimmedText1 = (text1 != null) ? text1.trim() : null;
      return (trimmedText1 != null) && (trimmedText1.length() > 0);
      }

   /** Retrieves the value from the specified text field as a {@link String}. */
   @SuppressWarnings({"UnusedCatchParameter"})
   private String getTextComponentValueAsString(final JTextComponent textComponent)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         final String textFieldValue;
         try
            {
            final String text1 = textComponent.getText();
            textFieldValue = (text1 != null) ? text1.trim() : null;
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the value from text field. Returning null instead.", e);
            return null;
            }
         return textFieldValue;
         }
      else
         {
         final String[] textFieldValue = new String[1];
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     textFieldValue[0] = textComponent.getText();
                     }
                  });
            }
         catch (Exception e)
            {
            LOG.error("Exception while getting the value from text field. Returning null instead.", e);
            return null;
            }

         return textFieldValue[0];
         }
      }

   private static interface TextComponentValidator
      {
      boolean isValid(final JTextComponent textComponent);
      }

   private static final class EnableButtonIfTextFieldIsValidKeyAdapter extends KeyAdapter
      {
      private final JButton button;
      private final JTextComponent textComponent;
      private final TextComponentValidator validator;

      private EnableButtonIfTextFieldIsValidKeyAdapter(final JButton button,
                                                       final JTextComponent textComponent,
                                                       final TextComponentValidator validator)
         {
         this.button = button;
         this.textComponent = textComponent;
         this.validator = validator;
         }

      public void keyReleased(final KeyEvent keyEvent)
         {
         button.setEnabled(validator.isValid(textComponent));
         }
      }

   private final class PropertiesTableModel extends AbstractTableModel
      {
      private final List<String> keys = new ArrayList<String>();
      private final List<String> values = new ArrayList<String>();

      private void update(final Map<String, String> properties)
         {
         keys.clear();
         values.clear();
         if (properties != null)
            {
            final SortedMap<String, String> sortedProperties = new TreeMap<String, String>(properties);
            keys.addAll(sortedProperties.keySet());
            values.addAll(sortedProperties.values());
            }
         fireTableDataChanged();
         }

      public int getRowCount()
         {
         return keys.size();
         }

      public String getColumnName(final int i)
         {
         return (i == 0) ? COLUMN_NAME_KEY : COLUMN_NAME_VALUE;
         }

      public int getColumnCount()
         {
         return 2;
         }

      public Object getValueAt(final int row, final int col)
         {
         if (col == 0)
            {
            return keys.get(row);
            }
         else if (col == 1)
            {
            return values.get(row);
            }
         return null;
         }
      }

   private final class CreatePropertyAction extends AbstractTimeConsumingAction
      {
      private String key;
      private String value;
      private String serviceName;
      private PropertyManager propertyManager;

      private CreatePropertyAction()
         {
         super(PropertyInspector.this);
         }

      protected void executeGUIActionBefore()
         {
         propertyKeyTextField.setEnabled(false);
         propertyValueTextField.setEnabled(false);
         createPropertyServiceComboBox.setEnabled(false);

         key = getTextComponentValueAsString(propertyKeyTextField);
         value = getTextComponentValueAsString(propertyValueTextField);
         serviceName = createPropertyServiceComboBox.getSelectedItem().toString();
         propertyManager = getPropertyManagerByServiceName(serviceName);
         }

      @SuppressWarnings({"UnusedCatchParameter"})
      protected Object executeTimeConsumingAction()
         {
         if (propertyManager != null)
            {
            try
               {
               propertyManager.setProperty(key, value);
               }
            catch (ReadOnlyPropertyException e)
               {
               LOG.info("Cannot overwrite read-only property [" + key + "]");
               return false;
               }
            }
         return true;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         if (!(Boolean)resultOfTimeConsumingAction)
            {
            SwingUtilities.invokeLater(readonlyPropertyError);
            }

         final String currentlyDisplayedService = viewPropertiesServiceComboBox.getSelectedItem().toString();
         viewPropertiesServiceComboBox.setSelectedItem(serviceName);

         // force a reload if the service name isn't different
         if (currentlyDisplayedService.equals(serviceName))
            {
            updateViewPropertiesTable();
            }

         propertyKeyTextField.setEnabled(true);
         propertyValueTextField.setEnabled(true);
         createPropertyServiceComboBox.setEnabled(true);
         }
      }

   private final class ViewPropertiesAction extends AbstractTimeConsumingAction
      {
      private PropertyManager propertyManager;

      private ViewPropertiesAction()
         {
         super(PropertyInspector.this);
         }

      protected void executeGUIActionBefore()
         {
         viewPropertiesServiceComboBox.setEnabled(false);

         final String serviceName = viewPropertiesServiceComboBox.getSelectedItem().toString();
         propertyManager = getPropertyManagerByServiceName(serviceName);
         }

      protected Object executeTimeConsumingAction()
         {
         if (propertyManager != null)
            {
            return propertyManager.getProperties();
            }
         return null;
         }

      @SuppressWarnings({"unchecked"})
      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         propertiesTableModel.update((Map<String, String>)resultOfTimeConsumingAction);
         viewPropertiesServiceComboBox.setEnabled(true);
         }
      }

   private class ErrorMessageDialogRunnable implements Runnable
      {
      private final String message;
      private final String title;

      private ErrorMessageDialogRunnable(final String message, final String title)
         {
         this.message = message;
         this.title = title;
         }

      public void run()
         {
         JOptionPane.showMessageDialog(PropertyInspector.this,
                                       message,
                                       title,
                                       JOptionPane.ERROR_MESSAGE);
         }
      }

   private class MyFocusTraversalPolicy extends FocusTraversalPolicy
      {
      public Component getComponentAfter(final Container container, final Component component)
         {
         if (component.equals(connectOrDisconnectButton))
            {
            return getEnabledComponentAfter(container, createPropertyServiceComboBox);
            }
         else if (component.equals(createPropertyServiceComboBox))
            {
            return getEnabledComponentAfter(container, propertyKeyTextField);
            }
         else if (component.equals(propertyKeyTextField))
            {
            return getEnabledComponentAfter(container, propertyValueTextField);
            }
         else if (component.equals(propertyValueTextField))
            {
            return getEnabledComponentAfter(container, createPropertyButton);
            }
         else if (component.equals(createPropertyButton))
            {
            return getEnabledComponentAfter(container, viewPropertiesServiceComboBox);
            }
         else if (component.equals(viewPropertiesServiceComboBox))
            {
            return getEnabledComponentAfter(container, reloadPropertiesButton);
            }
         else if (component.equals(reloadPropertiesButton))
            {
            return getEnabledComponentAfter(container, connectOrDisconnectButton);
            }
         return null;
         }

      public Component getComponentBefore(final Container container, final Component component)
         {
         if (component.equals(connectOrDisconnectButton))
            {
            return getEnabledComponentBefore(container, reloadPropertiesButton);
            }
         else if (component.equals(createPropertyServiceComboBox))
            {
            return getEnabledComponentBefore(container, connectOrDisconnectButton);
            }
         else if (component.equals(propertyKeyTextField))
            {
            return getEnabledComponentBefore(container, createPropertyServiceComboBox);
            }
         else if (component.equals(propertyValueTextField))
            {
            return getEnabledComponentBefore(container, propertyKeyTextField);
            }
         else if (component.equals(createPropertyButton))
            {
            return getEnabledComponentBefore(container, propertyValueTextField);
            }
         else if (component.equals(viewPropertiesServiceComboBox))
            {
            return getEnabledComponentBefore(container, createPropertyButton);
            }
         else if (component.equals(reloadPropertiesButton))
            {
            return getEnabledComponentBefore(container, viewPropertiesServiceComboBox);
            }
         return null;
         }

      private Component getEnabledComponentAfter(final Container container, final Component component)
         {
         return (component.isEnabled() ? component : getComponentAfter(container, component));
         }

      private Component getEnabledComponentBefore(final Container container, final Component component)
         {
         return (component.isEnabled() ? component : getComponentBefore(container, component));
         }

      public Component getFirstComponent(final Container container)
         {
         return connectOrDisconnectButton;
         }

      public Component getLastComponent(final Container container)
         {
         return createPropertyButton;
         }

      public Component getDefaultComponent(final Container container)
         {
         return connectOrDisconnectButton;
         }
      }
   }
