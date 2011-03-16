package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu) & Alex Styler
 */
final class AnalogInputsServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(AnalogInputsServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AnalogInputsServiceControlPanel.class.getName());

   private final AnalogInputsService service;

   AnalogInputsServiceControlPanel(final ControlPanelManager controlPanelManager, final AnalogInputsService service)
      {
      super(controlPanelManager, service);
      this.service = service;

      final Timer pollingTimer = new Timer("AnalogInputsPollingTimer", true);
      pollingTimer.scheduleAtFixedRate(
            new TimerTask()
            {
            public void run()
               {
               updateAnalogInputs();
               }
            },
            0,
            500);
      }

   private void updateAnalogInputs()
      {
      // todo: this is inefficient if there is more than one device.  It'd be better to fetch all the states
      // at once with a single call to the service
      for (int i = 0; i < service.getDeviceCount(); i++)
         {
         final ServiceControlPanelDevice device = getDeviceById(i);
         if (device != null && device.isActive())
            {
            try
               {
               final short value = service.getAnalogInputValue(i);
               ((ControlPanelDevice)device).setValue(value);
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to poll analog input [" + i + "]", e);
               }
            }
         }
      }

   public String getDisplayName()
      {
      return RESOURCES.getString("control-panel.title");
      }

   public String getShortDisplayName()
      {
      return RESOURCES.getString("control-panel.short-title");
      }

   public void refresh()
      {
      LOG.debug("AnalogInputsServiceControlPanel.refresh()");

      updateAnalogInputs();
      }

   protected ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex)
      {
      return new ControlPanelDevice(deviceIndex);
      }

   private final class ControlPanelDevice extends AbstractServiceControlPanelDevice
      {
      private static final int ACTUAL_MIN_VALUE = 0;
      private static final int ACTUAL_MAX_VALUE = 255;
      private static final int DISPLAY_MIN_VALUE = 0;
      private static final int DISPLAY_MAX_VALUE = 1000;
      private static final String DISPLAY_INITIAL_VALUE = "1000";

      private final JPanel panel = new JPanel();
      private final JTextField valueTextField = new JTextField(DISPLAY_INITIAL_VALUE,
                                                               DISPLAY_INITIAL_VALUE.length());

      private ControlPanelDevice(final int deviceIndex)
         {
         super(deviceIndex);

         valueTextField.setFont(GUIConstants.FONT_NORMAL);
         valueTextField.setEditable(false);
         valueTextField.setMaximumSize(valueTextField.getPreferredSize());
         valueTextField.setMinimumSize(valueTextField.getPreferredSize());

         // layout
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.add(GUIConstants.createLabel(String.valueOf(deviceIndex + 1)));
         panel.add(GUIConstants.createRigidSpacer());
         panel.add(valueTextField);
         panel.setBackground(Color.WHITE);
         }

      private void setValue(final int value)
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  valueTextField.setText(String.valueOf(scaleToDisplay(value)));
                  }
               });
         }

      public Component getComponent()
         {
         return panel;
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         return false;
         }

      public String getCurrentOperationName()
         {
         return null;
         }

      public Set<XmlParameter> buildParameters()
         {
         return null;
         }

      private int scaleToDisplay(final int value)
         {
         return scaleValue(value, ACTUAL_MIN_VALUE, ACTUAL_MAX_VALUE, DISPLAY_MIN_VALUE, DISPLAY_MAX_VALUE);
         }
      }
   }
