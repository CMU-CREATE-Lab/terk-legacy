package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.expression.XmlDevice;
import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;
import edu.cmu.ri.createlab.TeRK.expression.XmlService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"AssignmentToCollectionOrArrayFieldFromParameter"})
abstract class AbstractServiceControlPanel implements ServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(AbstractServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AbstractServiceControlPanel.class.getName());

   private static final Color TITLE_BAR_BACKGROUND_COLOR = new Color(227, 227, 227);

   private final Service service;
   private final Map<String, Set<String>> supportedOperationsToParametersMap = new HashMap<String, Set<String>>();
   private final JPanel devicesPanel = new JPanel();
   private final JPanel panel = new JPanel();
   private final Runnable updateLayoutRunnable =
         new Runnable()
         {
         public void run()
            {
            // remove all the existing components
            devicesPanel.removeAll();

            // now add the active components
            for (final int deviceIndex : deviceMap.keySet())
               {
               final ServiceControlPanelDevice device = deviceMap.get(deviceIndex);
               if (device.isActive())
                  {
                  devicesPanel.add(GUIConstants.createRigidSpacer());
                  devicesPanel.add(device.getComponent());
                  }
               }
            }
         };

   private final SortedMap<Integer, ServiceControlPanelDevice> deviceMap = new TreeMap<Integer, ServiceControlPanelDevice>();

   AbstractServiceControlPanel(final ControlPanelManager controlPanelManager, final Service service)
      {
      this(controlPanelManager, service, null);
      }

   AbstractServiceControlPanel(final ControlPanelManager controlPanelManager,
                               final Service service,
                               final Map<String, Set<String>> supportedOperationsToParametersMap)
      {
      this.service = service;
      if ((supportedOperationsToParametersMap != null) && (!supportedOperationsToParametersMap.isEmpty()))
         {
         for (final String key : supportedOperationsToParametersMap.keySet())
            {
            if (key != null)
               {
               final Set<String> value = supportedOperationsToParametersMap.get(key);
               if (value != null)
                  {
                  this.supportedOperationsToParametersMap.put(key, value);
                  }
               }
            }
         }

      createAndCacheDevices(service);

      devicesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      devicesPanel.setLayout(new BoxLayout(devicesPanel, BoxLayout.Y_AXIS));
      devicesPanel.setBackground(Color.WHITE);

      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setVisible(false);
      panel.setBackground(Color.WHITE);

      final JPanel windowPanel = new JPanel();
      windowPanel.setBackground(Color.WHITE);
      windowPanel.setLayout(new BoxLayout(windowPanel, BoxLayout.Y_AXIS));
      windowPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5),
                                                               BorderFactory.createLineBorder(Color.BLACK, 1)));

      final JButton closeButton = GUIConstants.createImageButton(RESOURCES.getString("images.button.close"),
                                                                 RESOURCES.getString("images.button.close.disabled"),
                                                                 RESOURCES.getString("images.button.close.mousedown"),
                                                                 RESOURCES.getString("images.button.close.mouseover"));
      final JButton refreshButton = GUIConstants.createImageButton(RESOURCES.getString("images.button.refresh"),
                                                                   RESOURCES.getString("images.button.refresh.disabled"),
                                                                   RESOURCES.getString("images.button.refresh.mousedown"),
                                                                   RESOURCES.getString("images.button.refresh.mouseover"));
      refreshButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected Object executeTimeConsumingAction()
               {
               refresh();
               return null;
               }
            }
      );

      closeButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected Object executeTimeConsumingAction()
               {
               controlPanelManager.reset(AbstractServiceControlPanel.this);
               return null;
               }
            }
      );

      final JPanel titleBar = new JPanel();
      titleBar.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
      titleBar.setLayout(new BoxLayout(titleBar, BoxLayout.X_AXIS));
      titleBar.setBackground(TITLE_BAR_BACKGROUND_COLOR);
      titleBar.add(GUIConstants.createLabel(getDisplayName()));
      titleBar.add(Box.createHorizontalGlue());
      titleBar.add(refreshButton);
      titleBar.add(GUIConstants.createRigidSpacer(2));
      titleBar.add(closeButton);

      final JPanel linePanel = new JPanel();
      linePanel.setBorder(BorderFactory.createEmptyBorder());
      linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.X_AXIS));
      linePanel.setBackground(Color.BLACK);
      linePanel.add(Box.createHorizontalGlue());
      linePanel.add(GUIConstants.createRigidSpacer(1));
      linePanel.add(Box.createHorizontalGlue());

      windowPanel.add(titleBar);
      windowPanel.add(linePanel);
      windowPanel.add(devicesPanel);

      panel.add(windowPanel);
      }

   private void createAndCacheDevices(final Service service)
      {
      final int deviceCount;

      // determine the device count
      if (service instanceof DeviceController)
         {
         deviceCount = ((DeviceController)service).getDeviceCount();
         }
      else
         {
         deviceCount = 1;
         }

      // create the devices for this service and cache them in the deviceMap
      for (int i = 0; i < deviceCount; i++)
         {
         final ServiceControlPanelDevice serviceControlPanelDevice = createServiceControlPanelDevice(service, i);
         if (serviceControlPanelDevice != null)
            {
            deviceMap.put(i, serviceControlPanelDevice);
            }
         }
      updateLayout();
      }

   protected abstract ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex);

   /**
    * Fetches the property specified by the given <code>propertyKey</code> from the given <code>service</code> and
    * returns it as an <code>int</code>.  If the property doesn't exist or is <code>null</code>, the given
    * <code>defaultValue</code> is returned instead.
    */
   protected final int getServicePropertyAsInt(final Service service, final String propertyKey, final int defaultValue)
      {
      final Integer val = service.getPropertyAsInteger(propertyKey);
      if (val == null)
         {
         return defaultValue;
         }

      return val;
      }

   protected final ServiceControlPanelDevice getDeviceById(final int deviceIndex)
      {
      return deviceMap.get(deviceIndex);
      }

   /** Returns an unmodifiable {@link Collection} of this control panel's {@link ServiceControlPanelDevice}s. */
   protected final Collection<ServiceControlPanelDevice> getDevices()
      {
      return Collections.unmodifiableCollection(deviceMap.values());
      }

   public final XmlService buildService()
      {
      final Map<String, Set<XmlDevice>> operationToDevicesMap = new HashMap<String, Set<XmlDevice>>();

      // iterate over all the devices
      for (final ServiceControlPanelDevice serviceControlPanelDevice : deviceMap.values())
         {
         // if this device is active
         if (serviceControlPanelDevice.isActive())
            {
            // get the operation for this device
            final String operationName = serviceControlPanelDevice.getCurrentOperationName();

            if (operationName != null)
               {
               // get the set of devices for this operation, creating it if necessary
               Set<XmlDevice> devices = operationToDevicesMap.get(operationName);
               if (devices == null)
                  {
                  devices = new HashSet<XmlDevice>();
                  operationToDevicesMap.put(operationName, devices);
                  }

               // add this device to the set of devices for this operation
               devices.add(serviceControlPanelDevice.buildDevice());
               }
            }
         }

      // see whether we have any operations
      if (!operationToDevicesMap.isEmpty())
         {
         // create the collection of operations we'll use to create the XmlService
         final List<XmlOperation> operations = new ArrayList<XmlOperation>(operationToDevicesMap.size());

         // iterate over all the operations names, creating an XmlOperation for each
         for (final String operationName : operationToDevicesMap.keySet())
            {
            // get the devices associated with this operation
            final Set<XmlDevice> devices = operationToDevicesMap.get(operationName);

            // create the operation and add it to the collection
            operations.add(new XmlOperation(operationName, devices));
            }

         // finally, create the XmlService
         return new XmlService(getTypeId(), operations);
         }

      return null;
      }

   public final void setDeviceActive(final int deviceIndex, final boolean isActive)
      {
      // set the active state of the device
      final ServiceControlPanelDevice device = deviceMap.get(deviceIndex);
      if (device != null)
         {
         setDeviceActive(device, isActive);
         }
      else
         {
         LOG.warn("AbstractServiceControlPanel.setDeviceActive(): ignoring invalid device index [" + deviceIndex + "]");
         }
      }

   private void setDeviceActive(final ServiceControlPanelDevice device, final boolean isActive)
      {
      setDeviceActive(device, isActive, true);
      }

   private void setDeviceActive(final ServiceControlPanelDevice device, final boolean isActive, final boolean willUpdateLayout)
      {
      device.setActive(isActive);

      // now set the visibility of this control panel according to whether there are any active devices
      panel.setVisible(isActive || hasActiveDevice());

      // finally, update the layout
      if (willUpdateLayout)
         {
         updateLayout();
         }
      }

   public final Set<Integer> loadOperation(final XmlOperation operation)
      {
      if (operation != null)
         {
         final String operationName = operation.getName();
         final Set<String> supportedParametersForThisOperation = supportedOperationsToParametersMap.get(operationName);
         if (supportedParametersForThisOperation != null)
            {
            final Set<XmlDevice> devices = operation.getDevices();
            if ((devices != null) && (!devices.isEmpty()))
               {
               final Set<Integer> activatedDevices = new HashSet<Integer>();
               for (final XmlDevice device : devices)
                  {
                  final int deviceId = device.getId();
                  final ServiceControlPanelDevice serviceControlPanelDevice = deviceMap.get(deviceId);
                  if (serviceControlPanelDevice != null)
                     {
                     final Map<String, String> parameterMap = device.getParametersValuesAsMap();
                     parameterMap.keySet().retainAll(supportedParametersForThisOperation);

                     boolean wasExecutionSuccessful;
                     try
                        {
                        wasExecutionSuccessful = serviceControlPanelDevice.execute(operationName, parameterMap);
                        }
                     catch (Exception e)
                        {
                        LOG.debug("Exception caught while calling execute on device [" + deviceId + "]", e);
                        wasExecutionSuccessful = false;
                        }

                     if (wasExecutionSuccessful)
                        {
                        activatedDevices.add(deviceId);
                        }
                     else
                        {
                        LOG.debug("AbstractServiceControlPanel.loadOperation(): execute failed for device [" + deviceId + "]");
                        }
                     }
                  else
                     {
                     LOG.debug("AbstractServiceControlPanel.loadOperation(): ignoring unsupported device [" + deviceId + "]");
                     }
                  }
               return activatedDevices;
               }
            else
               {
               // this case should never happen, since, according to the DTD, an operation must have at least one device.
               LOG.error("AbstractServiceControlPanel.loadOperation(): deviceless operations are not supported");
               }
            }
         else
            {
            LOG.debug("AbstractServiceControlPanel.loadOperation(): unknown operation [" + operationName + "]");
            }
         }
      return null;
      }

   /** Returns <code>true</code> if this control panel has at least one active device, <code>false</code> otherwise. */
   private boolean hasActiveDevice()
      {
      for (final ServiceControlPanelDevice device : deviceMap.values())
         {
         if (device.isActive())
            {
            return true;
            }
         }

      return false;
      }

   private void updateLayout()
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         updateLayoutRunnable.run();
         }
      else
         {
         SwingUtilities.invokeLater(updateLayoutRunnable);
         }
      }

   /**
    * Scales the given <code>rawValue</code> from the original scale (defined by <code>originalMin</code> and
    * <code>originalMax</code>) to a new scale (defined by <code>targetMin</code> and <code>targetMax</code>).
    */
   protected final int scaleValue(final int rawValue, final int originalMin, final int originalMax, final int targetMin, final int targetMax)
      {
      // see if we actually need to scale the value
      if (originalMin == targetMin && originalMax == targetMax)
         {
         return rawValue;
         }

      final int origAdjustment = 0 - originalMin;
      final int newRawValue = rawValue + origAdjustment;
      final int newMax = originalMax + origAdjustment;
      final float scaledRaw = (float)newRawValue / (float)newMax;
      final int targetAdjustment = 0 - targetMin;
      final int newTargetMax = targetAdjustment + targetMax;
      return (int)(scaledRaw * newTargetMax - targetAdjustment);
      }

   public final String getTypeId()
      {
      return service.getTypeId();
      }

   public final int getDeviceCount()
      {
      return deviceMap.keySet().size();
      }

   public final Component getComponent()
      {
      return getMainPanel();
      }

   public abstract void refresh();

   protected final JPanel getMainPanel()
      {
      return panel;
      }
   }
