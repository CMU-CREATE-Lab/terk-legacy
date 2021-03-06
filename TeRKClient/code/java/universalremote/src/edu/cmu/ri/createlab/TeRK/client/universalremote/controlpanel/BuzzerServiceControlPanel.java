package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import edu.cmu.ri.createlab.TeRK.audio.AudioExpressionConstants;
import edu.cmu.ri.createlab.TeRK.buzzer.BuzzerService;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.services.Service;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class BuzzerServiceControlPanel extends AbstractServiceControlPanel
   {
   private static final Logger LOG = Logger.getLogger(BuzzerServiceControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(BuzzerServiceControlPanel.class.getName());

   private static final Set<String> PARAMETER_NAMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(BuzzerService.PARAMETER_NAME_FREQUENCY, BuzzerService.PARAMETER_NAME_DURATION)));
   private static final Map<String, Set<String>> OPERATIONS_TO_PARAMETERS_MAP;

   static
      {
      final Map<String, Set<String>> operationsToParametersMap = new HashMap<String, Set<String>>();
      operationsToParametersMap.put(BuzzerService.OPERATION_NAME_PLAY_TONE, PARAMETER_NAMES);
      OPERATIONS_TO_PARAMETERS_MAP = Collections.unmodifiableMap(operationsToParametersMap);
      }

   private final BuzzerService service;

   BuzzerServiceControlPanel(final ControlPanelManager controlPanelManager, final BuzzerService service)
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
      LOG.debug("BuzzerServiceControlPanel.refresh()");

      // nothing to do here
      }

   protected ServiceControlPanelDevice createServiceControlPanelDevice(final Service service, final int deviceIndex)
      {
      return new ControlPanelDevice(deviceIndex);
      }

   private final class ControlPanelDevice extends AbstractServiceControlPanelDevice
      {
      private static final int DEFAULT_FREQUENCY = 440;
      private static final int DEFAULT_DURATION = 500;

      private final JPanel panel = new JPanel();
      private final JLabel frequencyLabel = GUIConstants.createLabel(RESOURCES.getString("control-panel.label.tone.frequency"));
      private final JLabel durationLabel = GUIConstants.createLabel(RESOURCES.getString("control-panel.label.tone.duration"));
      private final JTextField frequencyTextField = new JTextField(5);
      private final NumberFormatter formatter = new IntegerFormatter();
      private final JFormattedTextField durationTextField = new JFormattedTextField(new DefaultFormatterFactory(formatter, formatter, formatter));
      private final JButton playToneButton = GUIConstants.createButton(RESOURCES.getString("control-panel.button.label.play-tone"));
      private final KeyAdapter toneFieldsKeyListener =
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               enablePlayToneButtonIfInputsAreValid();
               }
            };

      private ControlPanelDevice(final int deviceIndex)
         {
         super(deviceIndex);

         playToneButton.addActionListener(new PlayToneAction());

         frequencyTextField.setFont(GUIConstants.FONT_NORMAL);
         frequencyTextField.setMinimumSize(frequencyTextField.getPreferredSize());
         frequencyTextField.setMaximumSize(frequencyTextField.getPreferredSize());
         setFrequency(DEFAULT_FREQUENCY);

         durationTextField.setColumns(5);
         durationTextField.setFont(GUIConstants.FONT_NORMAL);
         durationTextField.setMaximumSize(frequencyTextField.getPreferredSize());
         durationTextField.setPreferredSize(frequencyTextField.getPreferredSize());
         durationTextField.addFocusListener(
               new FocusAdapter()
               {
               public void focusLost(final FocusEvent e)
                  {
                  durationTextField.setBackground(Color.WHITE);
                  }
               });
         setDuration(DEFAULT_DURATION);

         frequencyTextField.addKeyListener(toneFieldsKeyListener);
         durationTextField.addKeyListener(
               new KeyAdapter()
               {
               public void keyReleased(final KeyEvent e)
                  {
                  if (isDurationTextFieldValid())
                     {
                     durationTextField.setBackground(GUIConstants.TEXT_FIELD_BACKGROUND_COLOR_NO_ERROR);
                     }
                  else
                     {
                     durationTextField.setBackground(GUIConstants.TEXT_FIELD_BACKGROUND_COLOR_HAS_ERROR);
                     }
                  enablePlayToneButtonIfInputsAreValid();
                  }
               });

         final JPanel toneGroupPanel = new JPanel();
         final GroupLayout toneGroupPanelLayout = new GroupLayout(toneGroupPanel);
         toneGroupPanel.setLayout(toneGroupPanelLayout);
         toneGroupPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
         toneGroupPanel.setBackground(Color.WHITE);

         toneGroupPanelLayout.setHorizontalGroup(
               toneGroupPanelLayout.createSequentialGroup()
                     .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                                .add(frequencyLabel)
                                .add(durationLabel))
                     .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                                .add(frequencyTextField)
                                .add(durationTextField))
                     .add(playToneButton)
         );

         toneGroupPanelLayout.setVerticalGroup(
               toneGroupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                     .add(toneGroupPanelLayout.createSequentialGroup()
                                .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.CENTER)
                                           .add(frequencyLabel)
                                           .add(frequencyTextField))
                                .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.CENTER)
                                           .add(durationLabel)
                                           .add(durationTextField)))
                     .add(playToneButton)
         );

         final Component toneLeftGlue = Box.createHorizontalGlue();
         final Component toneRightGlue = Box.createHorizontalGlue();

         final GroupLayout tonePanelLayout = new GroupLayout(panel);
         panel.setLayout(tonePanelLayout);
         panel.setBackground(Color.WHITE);

         tonePanelLayout.setHorizontalGroup(
               tonePanelLayout.createSequentialGroup()
                     .add(toneLeftGlue)
                     .add(toneGroupPanel)
                     .add(toneRightGlue)
         );

         tonePanelLayout.setVerticalGroup(
               tonePanelLayout.createParallelGroup(GroupLayout.LEADING)
                     .add(toneLeftGlue)
                     .add(toneGroupPanel)
                     .add(toneRightGlue)
         );
         }

      public Component getComponent()
         {
         return panel;
         }

      public boolean execute(final String operationName, final Map<String, String> parameterMap)
         {
         if (AudioExpressionConstants.OPERATION_NAME_TONE.equals(operationName))
            {
            final String freqStr = parameterMap.get(BuzzerService.PARAMETER_NAME_FREQUENCY);
            final String durStr = parameterMap.get(BuzzerService.PARAMETER_NAME_DURATION);
            try
               {
               final int frequency = Integer.parseInt(freqStr);
               final int duration = Integer.parseInt(durStr);

               // update the GUI
               updateToneGUI(frequency, duration);

               // execute the operation on the service
               service.playTone(getDeviceIndex(), frequency, duration);
               return true;
               }
            catch (NumberFormatException e)
               {
               LOG.error("NumberFormatException while trying to convert frequency or duration to an integer.", e);
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to load or execute the operation.", e);
               }
            }
         return false;
         }

      public String getCurrentOperationName()
         {
         return BuzzerService.OPERATION_NAME_PLAY_TONE;
         }

      public Set<XmlParameter> buildParameters()
         {
         LOG.debug("BuzzerServiceControlPanel$ControlPanelDevice.buildParameters()");

         final Integer f = getFrequency();
         final Integer d = getDuration();

         if (f != null && d != null)
            {
            final Set<XmlParameter> parameters = new HashSet<XmlParameter>();
            parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_FREQUENCY, f));
            parameters.add(new XmlParameter(AudioExpressionConstants.PARAMETER_NAME_DURATION, d));
            return parameters;
            }

         return null;
         }

      private void enablePlayToneButtonIfInputsAreValid()
         {
         SwingUtils.warnIfNotEventDispatchThread("AudioControlPanel.enableToneButtonIfTextFieldsAreValid()");

         playToneButton.setEnabled(areToneInputsValid());
         }

      // MUST be called from the Swing thread!
      private boolean areToneInputsValid()
         {
         return getTextFieldValueAsInteger(frequencyTextField) != null && isDurationTextFieldValid();
         }

      private boolean isDurationTextFieldValid()
         {
         final String text = durationTextField.getText();
         if (text == null)
            {
            return false;
            }

         try
            {
            final Number number = Integer.parseInt(text);
            durationTextField.commitEdit();
            return number.doubleValue() > 0;
            }
         catch (NumberFormatException e1)
            {
            return false;
            }
         catch (ParseException e)
            {
            return false;
            }
         }

      /** Retrieves the value from the specified text field as an <code>Integer</code>. */
      private Integer getTextFieldValueAsInteger(final JTextField textField)
         {
         try
            {
            final String valueStr = getTextFieldValueAsString(textField);
            return (valueStr == null || valueStr.length() <= 0) ? null : Integer.parseInt(valueStr);
            }
         catch (Exception e)
            {
            LOG.error("Exception while retrieving int value", e);
            }
         return null;
         }

      /** Retrieves the value from the specified text field as a {@link String}. */
      @SuppressWarnings({"UnusedCatchParameter"})
      private String getTextFieldValueAsString(final JTextField textField)
         {
         final String text;
         if (SwingUtilities.isEventDispatchThread())
            {
            text = textField.getText();
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
                        textFieldValue[0] = textField.getText();
                        }
                     });
               }
            catch (Exception e)
               {
               LOG.error("Exception while getting the value from text field.  Returning null instead.");
               textFieldValue[0] = null;
               }

            text = textFieldValue[0];
            }

         return (text != null) ? text.trim() : null;
         }

      private Integer getFrequency()
         {
         return getTextFieldValueAsInteger(frequencyTextField);
         }

      /** Returns the duration, in milliseconds. */
      private Integer getDuration()
         {
         if (isDurationTextFieldValid())
            {
            return getTextFieldValueAsInteger(durationTextField);
            }

         return null;
         }

      private void setFrequency(final int frequency)
         {
         setToneTextFieldValueWorkhorse(frequencyTextField, frequency);
         }

      /** Sets the duration, in milliseconds. */
      private void setDuration(final int duration)
         {
         setToneFormattedTextFieldValueWorkhorse(durationTextField, duration);
         }

      private void setToneTextFieldValueWorkhorse(final JTextField textField, final int value)
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            textField.setText(String.valueOf(value));
            enablePlayToneButtonIfInputsAreValid();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     textField.setText(String.valueOf(value));
                     enablePlayToneButtonIfInputsAreValid();
                     }
                  });
            }
         }

      private void setToneFormattedTextFieldValueWorkhorse(final JFormattedTextField textField, final Object value)
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            textField.setValue(value);
            enablePlayToneButtonIfInputsAreValid();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     textField.setValue(value);
                     enablePlayToneButtonIfInputsAreValid();
                     }
                  });
            }
         }

      private void updateToneGUI(final int frequency, final int duration)
         {
         // Update the GUI, but don't execute the operation
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  setFrequency(frequency);
                  setDuration(duration);
                  }
               });
         }

      @SuppressWarnings({"CloneableClassWithoutClone"})
      private class PlayToneAction extends AbstractTimeConsumingAction
         {
         private int frequency;
         private int duration;
         private boolean isValid;

         protected void executeGUIActionBefore()
            {
            isValid = areToneInputsValid();
            if (isValid)
               {
               frequency = getTextFieldValueAsInteger(frequencyTextField);
               duration = getDuration();
               }
            }

         protected Object executeTimeConsumingAction()
            {
            if (isValid)
               {
               service.playTone(getDeviceIndex(), frequency, duration);
               }
            return null;
            }
         }
      }
   }