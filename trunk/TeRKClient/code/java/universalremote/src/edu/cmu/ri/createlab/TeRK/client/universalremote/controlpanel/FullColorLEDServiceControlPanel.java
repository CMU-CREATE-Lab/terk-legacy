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
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FullColorLEDServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(FullColorLEDServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(FullColorLEDServiceControlPanel.class.getName());

   private static final String OPERATION_NAME = "setColor";
   private static final String PARAMETER_NAME_RED = "red";
   private static final String PARAMETER_NAME_GREEN = "green";
   private static final String PARAMETER_NAME_BLUE = "blue";
   private static final Set<String> PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(PARAMETER_NAME_RED, PARAMETER_NAME_GREEN, PARAMETER_NAME_BLUE)));
   private static final Map<String, Set<String>> OPERATIONS_TO_PARAMETERS_MAP;

   static
      {
      final Map<String, Set<String>> operationsToParametersMap = new HashMap<String, Set<String>>();
      operationsToParametersMap.put(OPERATION_NAME, PARAMETER_NAMES);
      OPERATIONS_TO_PARAMETERS_MAP = Collections.unmodifiableMap(operationsToParametersMap);
      }

   private final FullColorLEDService service;

   FullColorLEDServiceControlPanel(final ControlPanelManager controlPanelManager, final FullColorLEDService service)
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
      LOG.debug("FullColorLEDServiceControlPanel.refresh()");

      // get the current state
      final Color[] colors = service.getColors();

      for (final ServiceControlPanelDevice device : getDevices())
         {
         if (device.getDeviceIndex() >= 0 && device.getDeviceIndex() < colors.length)
            {
            ((ControlPanelDevice)device).updateGUI(colors[device.getDeviceIndex()]);
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
      private final DeviceSlider deviceSliderR;
      private final DeviceSlider deviceSliderG;
      private final DeviceSlider deviceSliderB;
      private final MyExecutionStrategy executionStrategy = new MyExecutionStrategy();

      private ControlPanelDevice(final Service service, final int deviceIndex)
         {
         super(deviceIndex);

         // try to read service properties, using defaults if undefined
         this.minAllowedIntensity = getServicePropertyAsInt(service, FullColorLEDService.PROPERTY_NAME_MIN_INTENSITY, DEFAULT_ACTUAL_MIN_VALUE);
         this.maxAllowedIntensity = getServicePropertyAsInt(service, FullColorLEDService.PROPERTY_NAME_MAX_INTENSITY, DEFAULT_ACTUAL_MAX_VALUE);
         if (LOG.isDebugEnabled())
            {
            LOG.debug("FullColorLEDServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): minAllowedIntensity=[" + minAllowedIntensity + "]");
            LOG.debug("FullColorLEDServiceControlPanel$ControlPanelDevice.ControlPanelDevice(" + deviceIndex + "): maxAllowedIntensity=[" + maxAllowedIntensity + "]");
            }

         deviceSliderR = new DeviceSlider(deviceIndex,
                                          DISPLAY_MIN_VALUE,
                                          DISPLAY_MAX_VALUE,
                                          DISPLAY_INITIAL_VALUE,
                                          100,
                                          500,
                                          executionStrategy);
         deviceSliderG = new DeviceSlider(deviceIndex,
                                          DISPLAY_MIN_VALUE,
                                          DISPLAY_MAX_VALUE,
                                          DISPLAY_INITIAL_VALUE,
                                          100,
                                          500,
                                          executionStrategy);
         deviceSliderB = new DeviceSlider(deviceIndex,
                                          DISPLAY_MIN_VALUE,
                                          DISPLAY_MAX_VALUE,
                                          DISPLAY_INITIAL_VALUE,
                                          100,
                                          500,
                                          executionStrategy);

         // layout
         final JPanel colorPanel = new JPanel(new SpringLayout());
         colorPanel.setBackground(Color.WHITE);
         colorPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                                                                 BorderFactory.createEmptyBorder(5, 5, 5, 5)));
         colorPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.red")));
         colorPanel.add(GUIConstants.createRigidSpacer());
         colorPanel.add(deviceSliderR.getComponent());
         colorPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.green")));
         colorPanel.add(GUIConstants.createRigidSpacer());
         colorPanel.add(deviceSliderG.getComponent());
         colorPanel.add(GUIConstants.createLabel(RESOURCES.getString("label.blue")));
         colorPanel.add(GUIConstants.createRigidSpacer());
         colorPanel.add(deviceSliderB.getComponent());
         SpringLayoutUtilities.makeCompactGrid(colorPanel,
                                               3, 3, // rows, cols
                                               0, 0, // initX, initY
                                               0, 0);// xPad, yPad

         panel.setBackground(Color.WHITE);
         panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
         panel.add(GUIConstants.createLabel(String.valueOf(deviceIndex + 1)));
         panel.add(GUIConstants.createRigidSpacer());
         panel.add(colorPanel);
         }

      public Component getComponent()
         {
         return panel;
         }

      private void updateGUI(final Color color)
         {
         // Update the sliders, but we don't want to rely on the execution strategy in order for the call to the
         // service to be made since the execution strategy won't get executed if there's no change in the slider's
         // value.  This can happen if the device's state is changed by some other means than via the service, such
         // as calling emergency stop.
         deviceSliderR.setValueNoExecution(scaleToDisplay(color.getRed()));
         deviceSliderG.setValueNoExecution(scaleToDisplay(color.getGreen()));
         deviceSliderB.setValueNoExecution(scaleToDisplay(color.getBlue()));
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         if (OPERATION_NAME.equals(operationName))
            {
            final String rStr = parameterMap.get(PARAMETER_NAME_RED);
            final String gStr = parameterMap.get(PARAMETER_NAME_GREEN);
            final String bStr = parameterMap.get(PARAMETER_NAME_BLUE);
            try
               {
               final int r = Integer.parseInt(rStr);
               final int g = Integer.parseInt(gStr);
               final int b = Integer.parseInt(bStr);
               final Color color = new Color(r, g, b);

               // update the GUI
               updateGUI(color);

               // now execute the operation on the service
               service.set(getDeviceIndex(), color);

               return true;
               }
            catch (NumberFormatException e)
               {
               LOG.error("NumberFormatException while trying to convert one of the color values to an integer.", e);
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
         final Integer r = deviceSliderR.getValue();
         final Integer g = deviceSliderG.getValue();
         final Integer b = deviceSliderB.getValue();

         if (r != null && g != null && b != null)
            {
            final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
            parameters.add(new XmlParameter(PARAMETER_NAME_RED, scaleToActual(r)));
            parameters.add(new XmlParameter(PARAMETER_NAME_GREEN, scaleToActual(g)));
            parameters.add(new XmlParameter(PARAMETER_NAME_BLUE, scaleToActual(b)));
            return parameters;
            }

         return null;
         }

      private final class MyExecutionStrategy implements DeviceSlider.ExecutionStrategy
         {
         public void execute(final int deviceIndex, final int value)
            {
            LOG.debug("FullColorLEDServiceControlPanel$ControlPanelDevice$MyExecutionStrategy.execute()");
            service.set(deviceIndex, new Color(scaleToActual(deviceSliderR.getValue()),
                                               scaleToActual(deviceSliderG.getValue()),
                                               scaleToActual(deviceSliderB.getValue())));
            }
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