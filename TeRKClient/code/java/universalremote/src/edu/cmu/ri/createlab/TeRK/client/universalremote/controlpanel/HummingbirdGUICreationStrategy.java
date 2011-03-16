package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.SortedMap;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.led.SimpleLEDService;
import edu.cmu.ri.createlab.TeRK.motor.SpeedControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.servo.SimpleServoService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.ImageUtils;
import org.jdesktop.layout.GroupLayout;

/**
 * <p>
 * <code>HummingbirdPeerGUICreationStrategy</code> creates the GUI for Hummingbirds.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HummingbirdGUICreationStrategy extends PeerGUICreationStrategy
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(HummingbirdGUICreationStrategy.class.getName());
   private static final Color BACKGROUND_COLOR = new Color(167, 211, 111);

   public void createGUI(final GroupLayout mainPanelLayout, final Map<String, ServiceControlPanel> serviceControlPanelMap, final Map<String, SortedMap<Integer, JCheckBox>> serviceDeviceToggleButtonMap)
      {
      final Component leds = serviceControlPanelMap.get(SimpleLEDService.TYPE_ID).getComponent();

      final Component spacer = GUIConstants.createRigidSpacer();
      spacer.setBackground(Color.WHITE);
      final JPanel peerGUI = createPeerGUI(serviceControlPanelMap, serviceDeviceToggleButtonMap);
      final JPanel leftPeerGUIControlPanels = createLeftPeerControlPanelsGUI(serviceControlPanelMap);
      final JPanel rightPeerGUIControlPanels = createRightPeerControlPanelsGUI(serviceControlPanelMap);

      final JPanel centerArea = new JPanel();
      centerArea.setBackground(Color.WHITE);
      final GroupLayout centerAreaLayout = new GroupLayout(centerArea);
      centerArea.setLayout(centerAreaLayout);

      centerAreaLayout.setHorizontalGroup(
            centerAreaLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(peerGUI)
                  .add(spacer)
                  .add(leds)
      );
      centerAreaLayout.setVerticalGroup(
            centerAreaLayout.createSequentialGroup()
                  .add(peerGUI)
                  .add(spacer)
                  .add(leds)
      );

      mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createSequentialGroup()
                  .add(leftPeerGUIControlPanels)
                  .add(centerArea)
                  .add(rightPeerGUIControlPanels)
      );
      mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.LEADING)
                  .add(leftPeerGUIControlPanels)
                  .add(centerArea)
                  .add(rightPeerGUIControlPanels)
      );
      }

   private JPanel createPeerGUI(final Map<String, ServiceControlPanel> serviceControlPanelMap, final Map<String, SortedMap<Integer, JCheckBox>> serviceDeviceToggleButtonMap)
      {
      final JPanel ledsPanel = createHorizontalButtonPanel(serviceControlPanelMap.get(SimpleLEDService.TYPE_ID),
                                                           serviceDeviceToggleButtonMap.get(SimpleLEDService.TYPE_ID),
                                                           true,
                                                           BACKGROUND_COLOR);
      final JPanel orbsPanel = createHorizontalButtonPanel(serviceControlPanelMap.get(FullColorLEDService.TYPE_ID),
                                                           serviceDeviceToggleButtonMap.get(FullColorLEDService.TYPE_ID),
                                                           true,
                                                           BACKGROUND_COLOR);
      final JPanel vibMotorsPanel = createHorizontalButtonPanel(serviceControlPanelMap.get(SpeedControllableMotorService.TYPE_ID),
                                                                serviceDeviceToggleButtonMap.get(SpeedControllableMotorService.TYPE_ID),
                                                                false,
                                                                BACKGROUND_COLOR);
      final JPanel motorsPanel = createHorizontalButtonPanel(serviceControlPanelMap.get(VelocityControllableMotorService.TYPE_ID),
                                                             serviceDeviceToggleButtonMap.get(VelocityControllableMotorService.TYPE_ID),
                                                             false,
                                                             BACKGROUND_COLOR);
      final JPanel sensorsPanel = createVerticalButtonPanel(serviceControlPanelMap.get(AnalogInputsService.TYPE_ID),
                                                            serviceDeviceToggleButtonMap.get(AnalogInputsService.TYPE_ID),
                                                            false,
                                                            BACKGROUND_COLOR);
      final JPanel servosPanel = createVerticalButtonPanel(serviceControlPanelMap.get(SimpleServoService.TYPE_ID),
                                                           serviceDeviceToggleButtonMap.get(SimpleServoService.TYPE_ID),
                                                           true,
                                                           BACKGROUND_COLOR);
      final JPanel audioPanel = createVerticalButtonPanel(serviceControlPanelMap.get(AudioService.TYPE_ID),
                                                          serviceDeviceToggleButtonMap.get(AudioService.TYPE_ID),
                                                          true,
                                                          BACKGROUND_COLOR);

      final JPanel switchPanel = new JPanel();
      switchPanel.setBackground(BACKGROUND_COLOR);
      final GroupLayout switchLayout = new GroupLayout(switchPanel);
      switchPanel.setLayout(switchLayout);
      final Component switchSpacer = GUIConstants.createRigidSpacer();
      final JLabel offLabel = GUIConstants.createVerticalTinyFontLabel(RESOURCES.getString("label.switch.off"), false);
      final JLabel onLabel = GUIConstants.createVerticalTinyFontLabel(RESOURCES.getString("label.switch.on"), false);
      final JLabel switchLabel = new JLabel(ImageUtils.createImageIcon(RESOURCES.getString("image.switch")));
      switchLabel.setIconTextGap(0);
      switchLayout.setHorizontalGroup(
            switchLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(switchSpacer)
                  .add(switchLayout.createSequentialGroup()
                             .add(offLabel)
                             .add(switchLabel)
                             .add(onLabel))
      );
      switchLayout.setVerticalGroup(
            switchLayout.createSequentialGroup()
                  .add(switchSpacer)
                  .add(switchLayout.createParallelGroup(GroupLayout.CENTER)
                             .add(offLabel)
                             .add(switchLabel)
                             .add(onLabel))
      );

      final JPanel panel = new JPanel();
      panel.setBackground(BACKGROUND_COLOR);
      panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);

      layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .add(layout.createParallelGroup(GroupLayout.LEADING)
                             .add(switchPanel)
                             .add(servosPanel)
                             .add(audioPanel))
                  .add(layout.createParallelGroup(GroupLayout.CENTER)
                             .add(motorsPanel)
                             .add(ledsPanel))
                  .add(layout.createParallelGroup(GroupLayout.TRAILING)
                             .add(vibMotorsPanel)
                             .add(orbsPanel))
                  .add(sensorsPanel)
      );

      layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .add(layout.createParallelGroup(GroupLayout.LEADING)
                             .add(switchPanel)
                             .add(motorsPanel)
                             .add(vibMotorsPanel))
                  .add(servosPanel)
                  .add(layout.createParallelGroup(GroupLayout.CENTER)
                             .add(audioPanel)
                             .add(sensorsPanel))
                  .add(layout.createParallelGroup(GroupLayout.TRAILING)
                             .add(ledsPanel)
                             .add(orbsPanel))
      );

      return panel;
      }

   private JPanel createLeftPeerControlPanelsGUI(final Map<String, ServiceControlPanel> serviceControlPanelMap)
      {
      final Component motors = serviceControlPanelMap.get(VelocityControllableMotorService.TYPE_ID).getComponent();
      final Component servos = serviceControlPanelMap.get(SimpleServoService.TYPE_ID).getComponent();
      final Component audio = serviceControlPanelMap.get(AudioService.TYPE_ID).getComponent();

      final JPanel panel = new JPanel();
      panel.setBackground(BACKGROUND_COLOR);
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);

      layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.TRAILING)
                  .add(motors)
                  .add(servos)
                  .add(audio)

      );
      layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .add(motors)
                  .add(servos)
                  .add(audio)
      );

      return panel;
      }

   private JPanel createRightPeerControlPanelsGUI(final Map<String, ServiceControlPanel> serviceControlPanelMap)
      {
      final Component vibMotors = serviceControlPanelMap.get(SpeedControllableMotorService.TYPE_ID).getComponent();
      final Component sensors = serviceControlPanelMap.get(AnalogInputsService.TYPE_ID).getComponent();
      final Component orbs = serviceControlPanelMap.get(FullColorLEDService.TYPE_ID).getComponent();

      final JPanel panel = new JPanel();
      panel.setBackground(BACKGROUND_COLOR);
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);

      layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.LEADING)
                  .add(vibMotors)
                  .add(sensors)
                  .add(orbs)

      );
      layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .add(vibMotors)
                  .add(sensors)
                  .add(orbs)
      );

      return panel;
      }
   }
