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
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class VelocityControllableMotorServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(VelocityControllableMotorServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(VelocityControllableMotorServiceControlPanel.class.getName());

   private static final String OPERATION_NAME = "setVelocity";
   private static final String PARAMETER_NAME = "velocity";
   private static final Set<String> PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME)));
   private static final Map<String, Set<String>> OPERATIONS_TO_PARAMETERS_MAP;

   static
      {
      final Map<String, Set<String>> operationsToParametersMap = new HashMap<String, Set<String>>();
      operationsToParametersMap.put(OPERATION_NAME, PARAMETER_NAMES);
      OPERATIONS_TO_PARAMETERS_MAP = Collections.unmodifiableMap(operationsToParametersMap);
      }

   private final VelocityControllableMotorService service;

   VelocityControllableMotorServiceControlPanel(final ControlPanelManager controlPanelManager, final VelocityControllableMotorService service)
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
      LOG.debug("VelocityControllableMotorServiceControlPanel.refresh()");

      // get the current state
      final int[] velocities = service.getVelocities();

      for (final ServiceControlPanelDevice device : getDevices())
         {
         if (device.getDeviceIndex() >= 0 && device.getDeviceIndex() < velocities.length)
            {
            ((ControlPanelDevice)device).updateGUI(velocities[device.getDeviceIndex()]);
            }
         }
      }

   protected ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex)
      {
      return new ControlPanelDevice(service, deviceIndex);
      }

   private final class ControlPanelDevice extends AbstractServiceControlPanelDevice
      {
      private static final int DEFAULT_ACTUAL_MIN_VALUE = -255;
      private static final int DEFAULT_ACTUAL_MAX_VALUE = 255;
      private static final int DISPLAY_MIN_VALUE = -1000;
      private static final int DISPLAY_MAX_VALUE = 1000;
      private static final int DISPLAY_INITIAL_VALUE = 0;

      private final int minAllowedVelocity;
      private final int maxAllowedVelocity;

      private final JPanel panel = new JPanel();
      private final DeviceSlider deviceSlider;

      private ControlPanelDevice(final Service service, final int deviceIndex)
         {
         super(deviceIndex);

         // try to read service properties, using defaults if undefined
         this.minAllowedVelocity = getServicePropertyAsInt(service, VelocityControllableMotorService.PROPERTY_NAME_MIN_VELOCITY, DEFAULT_ACTUAL_MIN_VALUE);
         this.maxAllowedVelocity = getServicePropertyAsInt(service, VelocityControllableMotorService.PROPERTY_NAME_MAX_VELOCITY, DEFAULT_ACTUAL_MAX_VALUE);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("VelocityControllableMotorServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): minAllowedVelocity=[" + minAllowedVelocity + "]");
            LOG.debug("VelocityControllableMotorServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): maxAllowedVelocity=[" + maxAllowedVelocity + "]");
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
                                            VelocityControllableMotorServiceControlPanel.this.service.setVelocity(deviceIndex, scaledValue);
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

      private void updateGUI(final int value)
         {
         // Update the slider, but we don't want to rely on the execution strategy in order for the call to the
         // service to be made since the execution strategy won't get executed if there's no change in the slider's
         // value.  This can happen if the device's state is changed by some other means than via the service, such
         // as calling emergency stop.
         deviceSlider.setValueNoExecution(scaleToDisplay(value));
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         if (OPERATION_NAME.equals(operationName))
            {
            final String valueStr = parameterMap.get(PARAMETER_NAME);
            try
               {
               final int value = Integer.parseInt(valueStr);

               updateGUI(value);

               // execute the operation on the service
               service.setVelocity(getDeviceIndex(), value);
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
         return scaleValue(value, DISPLAY_MIN_VALUE, DISPLAY_MAX_VALUE, minAllowedVelocity, maxAllowedVelocity);
         }

      private int scaleToDisplay(final int value)
         {
         return scaleValue(value, minAllowedVelocity, maxAllowedVelocity, DISPLAY_MIN_VALUE, DISPLAY_MAX_VALUE);
         }
      }
   }