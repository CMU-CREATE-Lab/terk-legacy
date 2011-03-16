package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerService;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.led.FullColorLEDService;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorService;
import edu.cmu.ri.createlab.TeRK.motor.VelocityControllableMotorService;
import edu.cmu.ri.createlab.TeRK.obstacle.SimpleObstacleDetectorService;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorService;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import org.jdesktop.layout.GroupLayout;

/**
 * <p>
 * <code>FinchPeerGUICreationStrategy</code> creates the GUI for Finches.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class FinchGUICreationStrategy extends PeerGUICreationStrategy
   {
   private static final Color BACKGROUND_COLOR = new Color(167, 211, 111);

   public void createGUI(final GroupLayout mainPanelLayout, final Map<String, ServiceControlPanel> serviceControlPanelMap, final Map<String, SortedMap<Integer, JCheckBox>> serviceDeviceToggleButtonMap)
      {
      final Component spacer = GUIConstants.createRigidSpacer();
      final JPanel peerGUI = createPeerGUI(serviceControlPanelMap, serviceDeviceToggleButtonMap);
      final JPanel peerGUIControlPanels = createPeerControlPanelsGUI(serviceControlPanelMap);
      mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(peerGUI)
                  .add(spacer)
                  .add(peerGUIControlPanels)
      );
      mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createSequentialGroup()
                  .add(peerGUI)
                  .add(spacer)
                  .add(peerGUIControlPanels)
      );
      }

   private JPanel createPeerGUI(final Map<String, ServiceControlPanel> serviceControlPanelMap, final Map<String, SortedMap<Integer, JCheckBox>> serviceDeviceToggleButtonMap)
      {
      final JPanel orbsPanel = createVerticalButtonPanel(serviceControlPanelMap.get(FullColorLEDService.TYPE_ID),
                                                         serviceDeviceToggleButtonMap.get(FullColorLEDService.TYPE_ID),
                                                         false,
                                                         BACKGROUND_COLOR);
      final JPanel accelerometersPanel = createVerticalButtonPanel(serviceControlPanelMap.get(AccelerometerService.TYPE_ID),
                                                                   serviceDeviceToggleButtonMap.get(AccelerometerService.TYPE_ID),
                                                                   false,
                                                                   BACKGROUND_COLOR);
      final JPanel photoresistorsPanel = createVerticalButtonPanel(serviceControlPanelMap.get(PhotoresistorService.TYPE_ID),
                                                                   serviceDeviceToggleButtonMap.get(PhotoresistorService.TYPE_ID),
                                                                   false,
                                                                   BACKGROUND_COLOR);
      final JPanel thermistorsPanel = createVerticalButtonPanel(serviceControlPanelMap.get(ThermistorService.TYPE_ID),
                                                                serviceDeviceToggleButtonMap.get(ThermistorService.TYPE_ID),
                                                                false,
                                                                BACKGROUND_COLOR);
      final JPanel obstaclesPanel = createVerticalButtonPanel(serviceControlPanelMap.get(SimpleObstacleDetectorService.TYPE_ID),
                                                              serviceDeviceToggleButtonMap.get(SimpleObstacleDetectorService.TYPE_ID),
                                                              false,
                                                              BACKGROUND_COLOR);
      final JPanel buzzerPanel = createVerticalButtonPanel(serviceControlPanelMap.get(BuzzerService.TYPE_ID),
                                                           serviceDeviceToggleButtonMap.get(BuzzerService.TYPE_ID),
                                                           false,
                                                           BACKGROUND_COLOR);
      final JPanel motorsPanel1 = createVerticalButtonPanel(serviceControlPanelMap.get(VelocityControllableMotorService.TYPE_ID),
                                                            serviceDeviceToggleButtonMap.get(VelocityControllableMotorService.TYPE_ID),
                                                            false,
                                                            BACKGROUND_COLOR);
      final JPanel motorsPanel2 = createVerticalButtonPanel(serviceControlPanelMap.get(PositionControllableMotorService.TYPE_ID),
                                                            serviceDeviceToggleButtonMap.get(PositionControllableMotorService.TYPE_ID),
                                                            false,
                                                            BACKGROUND_COLOR);

      final JPanel panel = new JPanel();
      panel.setBackground(BACKGROUND_COLOR);
      panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);

      layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .add(orbsPanel)
                  .add(accelerometersPanel)
                  .add(photoresistorsPanel)
                  .add(thermistorsPanel)
                  .add(obstaclesPanel)
                  .add(buzzerPanel)
                  .add(motorsPanel1)
                  .add(motorsPanel2)
      );
      layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.CENTER)
                  .add(orbsPanel)
                  .add(accelerometersPanel)
                  .add(photoresistorsPanel)
                  .add(thermistorsPanel)
                  .add(obstaclesPanel)
                  .add(buzzerPanel)
                  .add(motorsPanel1)
                  .add(motorsPanel2)
      );

      return panel;
      }

   private JPanel createPeerControlPanelsGUI(final Map<String, ServiceControlPanel> serviceControlPanelMap)
      {
      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.add(serviceControlPanelMap.get(FullColorLEDService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(AccelerometerService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(PhotoresistorService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(ThermistorService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(SimpleObstacleDetectorService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(BuzzerService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(VelocityControllableMotorService.TYPE_ID).getComponent());
      panel.add(serviceControlPanelMap.get(PositionControllableMotorService.TYPE_ID).getComponent());

      return panel;
      }
   }