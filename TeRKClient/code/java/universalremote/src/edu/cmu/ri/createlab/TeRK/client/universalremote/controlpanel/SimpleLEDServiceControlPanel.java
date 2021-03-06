package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SimpleLEDServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(SimpleLEDServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(SimpleLEDServiceControlPanel.class.getName());

   private static final String OPERATION_NAME = SimpleLEDService.OPERATION_NAME_SET_INTENSITY;
   private static final String PARAMETER_NAME = SimpleLEDService.PARAMETER_NAME_INTENSITY;
   private static final Set<String> PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME)));
   private static final Map<String, Set<String>> OPERATIONS_TO_PARAMETERS_MAP;

   static
      {
      final Map<String, Set<String>> operationsToParametersMap = new HashMap<String, Set<String>>();
      operationsToParametersMap.put(OPERATION_NAME, PARAMETER_NAMES);
      OPERATIONS_TO_PARAMETERS_MAP = Collections.unmodifiableMap(operationsToParametersMap);
      }

   private final SimpleLEDService service;

   SimpleLEDServiceControlPanel(final ControlPanelManager controlPanelManager, final SimpleLEDService service)
      {
      super(controlPanelManager, service, OPERATIONS_TO_PARAMETERS_MAP);
      this.service = service;
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
      LOG.debug("SimpleLEDServiceControlPanel.refresh()");

      // get the current state
      final int[] intensities = service.getIntensities();

      for (final ServiceControlPanelDevice device : getDevices())
         {
         if (device.getDeviceIndex() >= 0 && device.getDeviceIndex() < intensities.length)
            {
            ((ControlPanelDevice)device).updateGUI(intensities[device.getDeviceIndex()]);
            }
         }
      }

   protected ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex)
      {
      return new ControlPanelDevice(service, deviceIndex);
      }

   private final class ControlPanelDevice extends AbstractServiceControlPanelDevice
      {
      private static final int DEFAULT_ACTUAL_MIN_VALUE = 0;
      private static final int DEFAULT_ACTUAL_MAX_VALUE = 255;
      private static final int DISPLAY_MIN_VALUE = 0;
      private static final int DISPLAY_MAX_VALUE = 1000;
      private static final int DISPLAY_INITIAL_VALUE = 0;

      private final int minAllowedIntensity;
      private final int maxAllowedIntensity;
      private final JPanel panel = new JPanel();
      private final DeviceSlider deviceSlider;

      private ControlPanelDevice(final Service service, final int deviceIndex)
         {
         super(deviceIndex);

         // try to read service properties, using defaults if undefined
         this.minAllowedIntensity = getServicePropertyAsInt(service, SimpleLEDService.PROPERTY_NAME_MIN_INTENSITY, DEFAULT_ACTUAL_MIN_VALUE);
         this.maxAllowedIntensity = getServicePropertyAsInt(service, SimpleLEDService.PROPERTY_NAME_MAX_INTENSITY, DEFAULT_ACTUAL_MAX_VALUE);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("SimpleLEDServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): minAllowedIntensity=[" + minAllowedIntensity + "]");
            LOG.debug("SimpleLEDServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): maxAllowedIntensity=[" + maxAllowedIntensity + "]");
            }

         deviceSlider = new DeviceSlider(deviceIndex,
                                         DISPLAY_MIN_VALUE,
                                         DISPLAY_MAX_VALUE,
                                         DISPLAY_INITIAL_VALUE,
                                         100,
                                         500,
                                         new DeviceSlider.ExecutionStrategy()
                                         {
                                         public void execute(final int deviceIndex, final int value)
                                            {
                                            final int scaledValue = scaleToActual(value);
                                            SimpleLEDServiceControlPanel.this.service.set(deviceIndex, scaledValue);
                                            }
                                         });

         // layout
         panel.setBackground(Color.WHITE);
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.add(GUIConstants.createLabel(String.valueOf(deviceIndex + 1)));
         panel.add(deviceSlider.getComponent());
         }

      public Component getComponent()
         {
         return panel;
         }

      private void updateGUI(final int intensity)
         {
         // Update the slider, but we don't want to rely on the execution strategy in order for the call to the
         // service to be made since the execution strategy won't get executed if there's no change in the slider's
         // value.  This can happen if the device's state is changed by some other means than via the service, such
         // as calling emergency stop.
         deviceSlider.setValueNoExecution(scaleToDisplay(intensity));
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         if (OPERATION_NAME.equals(operationName))
            {
            final String valueStr = parameterMap.get(PARAMETER_NAME);
            try
               {
               final int value = Integer.parseInt(valueStr);

               // update the GUI
               updateGUI(value);

               // execute the operation on the service
               service.set(getDeviceIndex(), value);
               return true;
               }
            catch (NumberFormatException e)
               {
               LOG.error("NumberFormatException while trying to convert [" + valueStr + "] to an integer.", e);
               }
            }
         return false;
         }

      public String getCurrentOperationName()
         {
         return OPERATION_NAME;
         }

      public Set<XmlParameter> buildParameters()
         {
         final Integer value = scaleToActual(deviceSlider.getValue());

         if (value != null)
            {
            final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
            parameters.add(new XmlParameter(PARAMETER_NAME, value));
            return parameters;
            }

         return null;
         }

      private int scaleToActual(final int value)
         {
         return scaleValue(value, DISPLAY_MIN_VALUE, DISPLAY_MAX_VALUE, minAllowedIntensity, maxAllowedIntensity);
         }

      private int scaleToDisplay(final int value)
         {
         return scaleValue(value, minAllowedIntensity, maxAllowedIntensity, DISPLAY_MIN_VALUE, DISPLAY_MAX_VALUE);
         }
      }
   }