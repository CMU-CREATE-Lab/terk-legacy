package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class PeerGUICreationStrategy
   {
   public abstract void createGUI(final GroupLayout mainPanelLayout,
                                  final Map<String, ServiceControlPanel> serviceControlPanelMap,
                                  final Map<String, SortedMap<Integer, JCheckBox>> serviceDeviceToggleButtonMap);

   protected final JPanel createVerticalButtonPanel(final ServiceControlPanel serviceControlPanel,
                                                    final SortedMap<Integer, JCheckBox> checkBoxMap,
                                                    final boolean isRotateClockwise)
      {
      return createVerticalButtonPanel(serviceControlPanel,
                                       checkBoxMap,
                                       isRotateClockwise,
                                       null);
      }

   protected final JPanel createVerticalButtonPanel(final ServiceControlPanel serviceControlPanel,
                                                    final SortedMap<Integer, JCheckBox> checkBoxMap,
                                                    final boolean isRotateClockwise,
                                                    final Color backgroundColor)
      {
      final JPanel buttonPanel = new JPanel(new SpringLayout());
      if (backgroundColor != null)
         {
         buttonPanel.setBackground(backgroundColor);
         }
      for (final int deviceId : checkBoxMap.keySet())
         {
         final JCheckBox checkBox = checkBoxMap.get(deviceId);
         if (backgroundColor != null)
            {
            checkBox.setBackground(backgroundColor);
            }

         if (isRotateClockwise)
            {
            buttonPanel.add(checkBox);
            buttonPanel.add(GUIConstants.createVerticalTinyFontLabel(String.valueOf(deviceId + 1), isRotateClockwise));
            }
         else
            {
            buttonPanel.add(GUIConstants.createVerticalTinyFontLabel(String.valueOf(deviceId + 1), isRotateClockwise));
            buttonPanel.add(checkBox);
            }
         }
      SpringLayoutUtilities.makeCompactGrid(buttonPanel,
                                            checkBoxMap.keySet().size(), 2, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      final JLabel label = GUIConstants.createVerticalLabel(serviceControlPanel.getShortDisplayName(), isRotateClockwise);

      final JPanel panel = new JPanel();
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);
      if (backgroundColor != null)
         {
         panel.setBackground(backgroundColor);
         }

      if (isRotateClockwise)
         {
         layout.setHorizontalGroup(
               layout.createSequentialGroup()
                     .add(buttonPanel)
                     .add(label));
         layout.setVerticalGroup(
               layout.createParallelGroup(GroupLayout.CENTER)
                     .add(buttonPanel)
                     .add(label));
         }
      else
         {
         layout.setHorizontalGroup(
               layout.createSequentialGroup()
                     .add(label)
                     .add(buttonPanel));
         layout.setVerticalGroup(
               layout.createParallelGroup(GroupLayout.CENTER)
                     .add(label)
                     .add(buttonPanel));
         }

      return panel;
      }

   protected final JPanel createHorizontalButtonPanel(final ServiceControlPanel serviceControlPanel,
                                                      final SortedMap<Integer, JCheckBox> checkBoxMap,
                                                      final boolean isLabelOnTop)
      {
      return createHorizontalButtonPanel(serviceControlPanel,
                                         checkBoxMap,
                                         isLabelOnTop,
                                         null);
      }

   protected final JPanel createHorizontalButtonPanel(final ServiceControlPanel serviceControlPanel,
                                                      final SortedMap<Integer, JCheckBox> checkBoxMap,
                                                      final boolean isLabelOnTop,
                                                      final Color backgroundColor)
      {
      final JPanel buttonPanel = new JPanel(new SpringLayout());
      if (backgroundColor != null)
         {
         buttonPanel.setBackground(backgroundColor);
         }
      if (isLabelOnTop)
         {
         for (int deviceId = 0; deviceId < checkBoxMap.size(); deviceId++)
            {
            final JLabel label = GUIConstants.createTinyFontLabel(String.valueOf(deviceId + 1));
            label.setHorizontalAlignment(JLabel.CENTER);
            buttonPanel.add(label);
            }
         for (int deviceId = 0; deviceId < checkBoxMap.size(); deviceId++)
            {
            final JCheckBox checkBox = checkBoxMap.get(deviceId);
            if (backgroundColor != null)
               {
               checkBox.setBackground(backgroundColor);
               }
            buttonPanel.add(checkBox);
            }
         }
      else
         {
         for (int deviceId = 0; deviceId < checkBoxMap.size(); deviceId++)
            {
            final JCheckBox checkBox = checkBoxMap.get(deviceId);
            if (backgroundColor != null)
               {
               checkBox.setBackground(backgroundColor);
               }
            buttonPanel.add(checkBox);
            }
         for (int deviceId = 0; deviceId < checkBoxMap.size(); deviceId++)
            {
            final JLabel label = GUIConstants.createTinyFontLabel(String.valueOf(deviceId + 1));
            label.setHorizontalAlignment(JLabel.CENTER);
            buttonPanel.add(label);
            }
         }
      SpringLayoutUtilities.makeCompactGrid(buttonPanel,
                                            2, checkBoxMap.keySet().size(), // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      final JLabel label = GUIConstants.createLabel(serviceControlPanel.getShortDisplayName());

      final JPanel panel = new JPanel();
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);
      if (backgroundColor != null)
         {
         panel.setBackground(backgroundColor);
         }

      if (isLabelOnTop)
         {
         layout.setHorizontalGroup(
               layout.createParallelGroup(GroupLayout.CENTER)
                     .add(label)
                     .add(buttonPanel));
         layout.setVerticalGroup(
               layout.createSequentialGroup()
                     .add(label)
                     .add(buttonPanel));
         }
      else
         {
         layout.setHorizontalGroup(
               layout.createParallelGroup(GroupLayout.CENTER)
                     .add(buttonPanel)
                     .add(label));
         layout.setVerticalGroup(
               layout.createSequentialGroup()
                     .add(buttonPanel)
                     .add(label));
         }

      return panel;
      }
   }