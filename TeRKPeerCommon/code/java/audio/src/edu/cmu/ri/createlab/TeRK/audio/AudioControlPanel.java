package edu.cmu.ri.createlab.TeRK.audio;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import Ice.UnknownLocalException;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.AudioCommandException;
import edu.cmu.ri.mrpl.TeRK.AudioCommandQueueFullException;
import edu.cmu.ri.mrpl.TeRK.AudioFileTooLargeException;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class AudioControlPanel extends JPanel
   {
   private static final Logger LOG = Logger.getLogger(AudioControlPanel.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(AudioControlPanel.class.getName());

   public static final String TONE_FREQUENCY_PROPERTY_KEY = "audio-control-panel.label.tone.frequency";
   public static final String TONE_DURATION_PROPERTY_KEY = "audio-control-panel.label.tone.duration";
   public static final String TONE_AMPLITUDE_PROPERTY_KEY = "audio-control-panel.label.tone.amplitude";
   public static final String IS_TONE_DURATION_SPECIFIED_IN_SECONDS_PROPERTY_KEY = "audio-control-panel.tone.duration.specified-in-seconds";
   private static final int DEFAULT_FREQUENCY = 440;
   private static final int DEFAULT_DURATION = 500;

   public static interface EventListener
      {
      void playTone(final int frequency, final int amplitude, final int duration);

      void playSound(final File file, final AsynchronousCommandExceptionHandlerCallback callback);

      void playSpeech(final String speechText);
      }

   public static enum Mode
      {
         TONE(RESOURCES.getString("tab.label.tone")),
         CLIP(RESOURCES.getString("tab.label.clip")),
         SPEECH(RESOURCES.getString("tab.label.speech"));

      private final String name;

      private Mode(final String name)
         {
         this.name = name;
         }

      public String getName()
         {
         return name;
         }

      public String toString()
         {
         return "Mode{" +
                "name='" + name + '\'' +
                '}';
         }
      }

   private static final int DEFAULT_TEXT_FIELD_COLUMNS = 5;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame("AudioControlPanel");

               // add the root panel to the JFrame
               final AudioClipChooser audioClipChooser = new TerkAudioClipChooser();
               final AudioControlPanel audioControlPanel = new AudioControlPanel(jFrame, audioClipChooser);
               audioControlPanel.addEventListener(
                     new EventListener()
                     {
                     public void playTone(final int frequency, final int amplitude, final int duration)
                        {
                        LOG.info("AudioControlPanel.playTone(" + frequency + "," + amplitude + "," + duration + ")");
                        }

                     public void playSound(final File file, final AsynchronousCommandExceptionHandlerCallback callback)
                        {
                        LOG.info("AudioControlPanel.playSound(" + file.getAbsolutePath() + ")");
                        }

                     public void playSpeech(final String speechText)
                        {
                        LOG.info("AudioControlPanel.playSpeech(" + speechText + ")");
                        }
                     }
               );
               jFrame.add(audioControlPanel);

               // set various properties for the JFrame
               jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
               jFrame.setBackground(Color.WHITE);
               jFrame.setResizable(true);
               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   private static String getPropertyFromSystemOrResource(final String propertyKey)
      {
      return System.getProperty(propertyKey, RESOURCES.getString(propertyKey));
      }

   private final JTabbedPane tabbedPane;
   private final Map<Integer, Mode> indexToModeMap = new HashMap<Integer, Mode>();
   private final Map<Mode, Integer> modeToIndexMap = new HashMap<Mode, Integer>();
   private Mode currentMode;

   private final JLabel frequencyLabel = GUIConstants.createLabel(getPropertyFromSystemOrResource(TONE_FREQUENCY_PROPERTY_KEY));
   private final JLabel durationLabel = GUIConstants.createLabel(getPropertyFromSystemOrResource(TONE_DURATION_PROPERTY_KEY));
   private final JLabel amplitudeLabel = GUIConstants.createLabel(getPropertyFromSystemOrResource(TONE_AMPLITUDE_PROPERTY_KEY));
   private final JTextField frequencyTextField = createIntegerTextField();
   private final JFormattedTextField durationTextField;
   private final JSpinner amplitudeSpinner;
   private final JButton playToneButton = GUIConstants.createButton(RESOURCES.getString("button.label.play-tone"));
   private final boolean isToneDurationInSeconds = Boolean.parseBoolean(getPropertyFromSystemOrResource(IS_TONE_DURATION_SPECIFIED_IN_SECONDS_PROPERTY_KEY));

   private final JButton playClipButton = GUIConstants.createButton(RESOURCES.getString("button.label.play-clip"));
   private final Runnable failedToPlayFileRunnable;
   private final Runnable invalidAudioFileRunnable;
   private final Runnable audioFileTooLargeRunnable;
   private final Runnable audioFileQueueFullRunnable;
   private final Runnable couldNotLoadAudioFileRunnable;

   private final JLabel speechLabel = GUIConstants.createLabel(RESOURCES.getString("label.text"));
   private final JTextField speechTextField = createTextField(10);
   private final JButton playSpeechButton = GUIConstants.createButton(RESOURCES.getString("button.label.play-speech"));

   private final KeyAdapter toneFieldsKeyListener =
         new KeyAdapter()
         {
         public void keyReleased(final KeyEvent e)
            {
            enablePlayToneButtonIfInputsAreValid();
            }
         };
   private final KeyAdapter speechFieldsKeyListener =
         new KeyAdapter()
         {
         public void keyReleased(final KeyEvent e)
            {
            enablePlaySpeechButtonIfInputsAreValid();
            }
         };

   private final Set<EventListener> eventListeners = new HashSet<EventListener>();
   private final AudioClipChooser audioClipChooser;

   public AudioControlPanel(final AudioClipChooser audioClipChooser)
      {
      this(null, audioClipChooser);
      }

   /**
    * <p>
    * Creates an AudioControlPanel using the given {@link Component} as the parent component (for dialogs and such). If
    * the {@link Component} is <code>null</code>, then the dialogs use this panel as the parent component.
    * </p>
    * <p>
    * Users of this class may override the default labels for the tone inputs (frequency, amplitude, and duration) by
    * setting the following system properties before the instance is constructed:
    * <ul>
    *    <li>audio-control-panel.label.tone.frequency</li>
    *    <li>audio-control-panel.label.tone.duration</li>
    *    <li>audio-control-panel.label.tone.amplitude</li>
    * </ul>
    * </p>
    */
   public AudioControlPanel(final Component parentComponent, final AudioClipChooser audioClipChooser)
      {
      final Component parent = (parentComponent == null ? this : parentComponent);
      this.audioClipChooser = audioClipChooser;

      if (isToneDurationInSeconds)
         {
         durationTextField = new JFormattedTextField(NumberFormat.getNumberInstance());
         }
      else
         {
         final NumberFormatter formatter = new DoubleFormatter();
         durationTextField = new JFormattedTextField(new DefaultFormatterFactory(formatter, formatter, formatter));
         }
      this.setDuration(DEFAULT_DURATION);
      this.setFrequency(DEFAULT_FREQUENCY);
      durationTextField.setColumns(DEFAULT_TEXT_FIELD_COLUMNS);
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
            }
      );

      failedToPlayFileRunnable = new ErrorMessageDialogRunnable(parent,
                                                                RESOURCES.getString("dialog.message.play-failed"),
                                                                RESOURCES.getString("dialog.title.play-failed"));

      invalidAudioFileRunnable = new ErrorMessageDialogRunnable(parent,
                                                                RESOURCES.getString("dialog.message.invalid-audio-file"),
                                                                RESOURCES.getString("dialog.title.invalid-audio-file"));

      audioFileTooLargeRunnable = new ErrorMessageDialogRunnable(parent,
                                                                 RESOURCES.getString("dialog.message.audio-file-too-large"),
                                                                 RESOURCES.getString("dialog.title.play-failed"));

      audioFileQueueFullRunnable = new ErrorMessageDialogRunnable(parent,
                                                                  RESOURCES.getString("dialog.message.audio-file-queue-full"),
                                                                  RESOURCES.getString("dialog.title.play-failed"));

      couldNotLoadAudioFileRunnable = new ErrorMessageDialogRunnable(parent,
                                                                     RESOURCES.getString("dialog.message.failed-to-load-audio-file"),
                                                                     RESOURCES.getString("dialog.title.failed-to-load-audio-file"));

      final ActionListener playToneAction = new PlayToneAction();
      final ActionListener playClipAction = new PlayClipAction();
      final ActionListener playSpeechAction = new PlaySpeechAction();

      final SpinnerNumberModel amplitudeModel = new SpinnerNumberModel(AudioHelper.DEFAULT_AMPLITUDE,
                                                                       AudioHelper.MIN_AMPLITUDE,
                                                                       AudioHelper.MAX_AMPLITUDE,
                                                                       1);
      amplitudeSpinner = new JSpinner(amplitudeModel);
      amplitudeSpinner.setMaximumSize(frequencyTextField.getPreferredSize());
      amplitudeSpinner.setFont(GUIConstants.FONT_NORMAL);

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
            }
      );
      amplitudeSpinner.addKeyListener(toneFieldsKeyListener);
      frequencyTextField.addActionListener(playToneAction);
      durationTextField.addActionListener(playToneAction);
      audioClipChooser.addFilePathFieldActionListener(playClipAction);
      audioClipChooser.addAudioClipChooserEventListener(
            new AudioClipChooserEventListener()
            {
            public void handleSelectedFileChange()
               {
               enablePlayClipButtonIfInputsAreValid();
               }
            }
      );

      speechTextField.addKeyListener(speechFieldsKeyListener);
      speechTextField.addActionListener(playSpeechAction);

      playToneButton.addActionListener(playToneAction);
      playClipButton.addActionListener(playClipAction);
      playSpeechButton.addActionListener(playSpeechAction);

      // ===============================================================================================================

      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      this.setBackground(Color.WHITE);

      // ===============================================================================================================

      final JPanel toneGroupPanel = new JPanel();
      final GroupLayout toneGroupPanelLayout = new GroupLayout(toneGroupPanel);
      toneGroupPanel.setLayout(toneGroupPanelLayout);
      toneGroupPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      toneGroupPanel.setBackground(Color.WHITE);

      toneGroupPanelLayout.setHorizontalGroup(
            toneGroupPanelLayout.createSequentialGroup()
                  .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                             .add(frequencyLabel)
                             .add(amplitudeLabel)
                             .add(durationLabel))
                  .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.LEADING)
                             .add(frequencyTextField)
                             .add(amplitudeSpinner)
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
                                        .add(amplitudeLabel)
                                        .add(amplitudeSpinner))
                             .add(toneGroupPanelLayout.createParallelGroup(GroupLayout.CENTER)
                                        .add(durationLabel)
                                        .add(durationTextField)))
                  .add(playToneButton)
      );

      final Component toneLeftGlue = Box.createHorizontalGlue();
      final Component toneRightGlue = Box.createHorizontalGlue();

      final JPanel tonePanel = new JPanel();
      final GroupLayout tonePanelLayout = new GroupLayout(tonePanel);
      tonePanel.setLayout(tonePanelLayout);
      tonePanel.setBackground(Color.WHITE);
      tonePanel.setBackground(Color.WHITE);

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

      // ===============================================================================================================

      final JPanel clipGroupPanel = new JPanel();
      final GroupLayout clipGroupPanelLayout = new GroupLayout(clipGroupPanel);
      clipGroupPanel.setLayout(clipGroupPanelLayout);
      clipGroupPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      clipGroupPanel.setBackground(Color.WHITE);

      clipGroupPanelLayout.setHorizontalGroup(
            clipGroupPanelLayout.createParallelGroup(GroupLayout.TRAILING)
                  .add(audioClipChooser.getComponent())
                  .add(playClipButton)
      );

      clipGroupPanelLayout.setVerticalGroup(
            clipGroupPanelLayout.createSequentialGroup()
                  .add(audioClipChooser.getComponent())
                  .add(playClipButton)
      );

      final Component clipLeftGlue = Box.createHorizontalGlue();
      final Component clipRightGlue = Box.createHorizontalGlue();

      final JPanel clipPanel = new JPanel();
      final GroupLayout clipPanelLayout = new GroupLayout(clipPanel);
      clipPanel.setLayout(clipPanelLayout);
      clipPanel.setBackground(Color.WHITE);

      clipPanelLayout.setHorizontalGroup(
            clipPanelLayout.createSequentialGroup()
                  .add(clipLeftGlue)
                  .add(clipGroupPanel)
                  .add(clipRightGlue)
      );

      clipPanelLayout.setVerticalGroup(
            clipPanelLayout.createParallelGroup(GroupLayout.LEADING)
                  .add(clipLeftGlue)
                  .add(clipGroupPanel)
                  .add(clipRightGlue)
      );

      // ===============================================================================================================

      final JPanel speechGroupPanel = new JPanel();
      final GroupLayout speechGroupPanelLayout = new GroupLayout(speechGroupPanel);
      speechGroupPanel.setLayout(speechGroupPanelLayout);
      speechGroupPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      speechGroupPanel.setBackground(Color.WHITE);

      speechGroupPanelLayout.setHorizontalGroup(
            speechGroupPanelLayout.createSequentialGroup()
                  .add(speechLabel)
                  .add(speechTextField)
                  .add(playSpeechButton)
      );

      speechGroupPanelLayout.setVerticalGroup(
            speechGroupPanelLayout.createParallelGroup(GroupLayout.CENTER)
                  .add(speechLabel)
                  .add(speechTextField)
                  .add(playSpeechButton)
      );

      final Component speechLeftGlue = Box.createHorizontalGlue();
      final Component speechRightGlue = Box.createHorizontalGlue();

      final JPanel speechPanel = new JPanel();
      final GroupLayout speechPanelLayout = new GroupLayout(speechPanel);
      speechPanel.setLayout(speechPanelLayout);
      speechPanel.setBackground(Color.WHITE);

      speechPanelLayout.setHorizontalGroup(
            speechPanelLayout.createSequentialGroup()
                  .add(speechLeftGlue)
                  .add(speechGroupPanel)
                  .add(speechRightGlue)
      );

      speechPanelLayout.setVerticalGroup(
            speechPanelLayout.createParallelGroup(GroupLayout.LEADING)
                  .add(speechLeftGlue)
                  .add(speechGroupPanel)
                  .add(speechRightGlue)
      );

      // ===============================================================================================================

      tabbedPane = new JTabbedPane();
      tabbedPane.setFont(GUIConstants.FONT_SMALL);
      tabbedPane.setBackground(Color.WHITE);

      // create the index to mode map
      indexToModeMap.put(0, Mode.TONE);
      indexToModeMap.put(1, Mode.CLIP);
      indexToModeMap.put(2, Mode.SPEECH);

      // create the mode to index map
      for (final Integer i : indexToModeMap.keySet())
         {
         modeToIndexMap.put(indexToModeMap.get(i), i);
         }

      tabbedPane.addTab(Mode.TONE.getName(), tonePanel);
      tabbedPane.addTab(Mode.CLIP.getName(), clipPanel);
      tabbedPane.addTab(Mode.SPEECH.getName(), speechPanel);
      tabbedPane.addChangeListener(
            new ChangeListener()
            {
            public void stateChanged(final ChangeEvent e)
               {
               currentMode = indexToModeMap.get(tabbedPane.getSelectedIndex());
               }
            }
      );
      // prevent the pane from taking up as much vertical space as possible
      tabbedPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, (int)tabbedPane.getPreferredSize().getHeight()));

      // initialize the current mode
      currentMode = indexToModeMap.get(tabbedPane.getSelectedIndex());

      // ===============================================================================================================

      this.add(tabbedPane);
      }

   public Mode getCurrentMode()
      {
      return currentMode;
      }

   public void setCurrentMode(final Mode newMode)
      {
      final Integer index = modeToIndexMap.get(newMode);
      if (index != null)
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            tabbedPane.setSelectedIndex(index);
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     tabbedPane.setSelectedIndex(index);
                     }
                  }
            );
            }
         }
      }

   public boolean isCurrentModePlayable()
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         return isCurrentModePlayableWorkhorse();
         }
      else
         {
         try
            {
            final boolean[] isEnabled = new boolean[1];
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     isEnabled[0] = isCurrentModePlayableWorkhorse();
                     }
                  });

            return isEnabled[0];
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException in AudioControlPanel.isCurrentModePlayable()", e);
            }
         catch (InvocationTargetException e)
            {
            LOG.error("InvocationTargetException in AudioControlPanel.isCurrentModePlayable()", e);
            }

         return false;
         }
      }

   private boolean isCurrentModePlayableWorkhorse()
      {
      if (Mode.TONE.equals(currentMode))
         {
         return playToneButton.isEnabled();
         }
      else if (Mode.CLIP.equals(currentMode))
         {
         return playClipButton.isEnabled();
         }
      else if (Mode.SPEECH.equals(currentMode))
         {
         return playSpeechButton.isEnabled();
         }

      return false;
      }

   public void addEventListener(final EventListener listener)
      {
      if (listener != null)
         {
         eventListeners.add(listener);
         }
      }

   public void removeEventListener(final EventListener listener)
      {
      if (listener != null)
         {
         eventListeners.remove(listener);
         }
      }

   public void setEnabled(final boolean isEnabled)
      {
      frequencyTextField.setEnabled(isEnabled);
      durationTextField.setEnabled(isEnabled);
      amplitudeSpinner.setEnabled(isEnabled);
      playToneButton.setEnabled(isEnabled && areToneInputsValid());

      audioClipChooser.setEnabled(isEnabled);
      playClipButton.setEnabled(isEnabled && areClipInputsValid());

      speechTextField.setEnabled(isEnabled);
      playSpeechButton.setEnabled(isEnabled && areSpeechInputsValid());
      }

   public Integer getFrequency()
      {
      return getTextFieldValueAsInteger(frequencyTextField);
      }

   public Integer getAmplitude()
      {
      return getSpinnerValueAsInteger(amplitudeSpinner);
      }

   /** Returns the duration, in milliseconds. */
   public Integer getDuration()
      {
      if (isDurationTextFieldValid())
         {
         if (isToneDurationInSeconds)
            {
            final double value = ((Number)durationTextField.getValue()).doubleValue();
            return (int)Math.round(value * 1000);
            }
         else
            {
            return getTextFieldValueAsInteger(durationTextField);
            }
         }

      return null;
      }

   public String getClipPath()
      {
      return audioClipChooser.getSelectedFilePath();
      }

   public String getSpeechText()
      {
      return getTextFieldValueAsString(speechTextField);
      }

   public void setFrequency(final int frequency)
      {
      setToneTextFieldValueWorkhorse(frequencyTextField, frequency);
      }

   public void setAmplitude(final int amplitude)
      {
      setToneSpinnerValueWorkhorse(amplitudeSpinner, amplitude);
      }

   /** Sets the duration, in milliseconds. */
   public void setDuration(final int duration)
      {
      if (isToneDurationInSeconds)
         {
         final Double durationInSeconds = (double)duration / 1000;
         setToneFormattedTextFieldValueWorkhorse(durationTextField, durationInSeconds);
         }
      else
         {
         setToneFormattedTextFieldValueWorkhorse(durationTextField, duration);
         }
      }

   private void setToneSpinnerValueWorkhorse(final JSpinner spinner, final int value)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         spinner.setValue(value);
         enablePlayToneButtonIfInputsAreValid();
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  spinner.setValue(value);
                  enablePlayToneButtonIfInputsAreValid();
                  }
               });
         }
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

   public void setClipPath(final String path)
      {
      audioClipChooser.setSelectedFilePath(path);
      }

   public void setSpeechText(final String text)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         speechTextField.setText(text);
         enablePlaySpeechButtonIfInputsAreValid();
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  speechTextField.setText(text);
                  enablePlaySpeechButtonIfInputsAreValid();
                  }
               });
         }
      }

   private JTextField createIntegerTextField()
      {
      return createIntegerTextField(DEFAULT_TEXT_FIELD_COLUMNS);
      }

   private JTextField createIntegerTextField(final int numColumns)
      {
      final JTextField textField = new JTextField(numColumns);
      textField.setFont(GUIConstants.FONT_NORMAL);
      textField.setMinimumSize(textField.getPreferredSize());
      textField.setMaximumSize(textField.getPreferredSize());

      return textField;
      }

   private JTextField createTextField(final int numColumns)
      {
      final JTextField textField = new JTextField(numColumns);
      textField.setFont(GUIConstants.FONT_NORMAL);
      textField.setMinimumSize(textField.getPreferredSize());
      textField.setMaximumSize(textField.getPreferredSize());
      return textField;
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
         final Number number;
         if (isToneDurationInSeconds)
            {
            number = Double.parseDouble(text);
            }
         else
            {
            number = Integer.parseInt(text);
            }
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

   private void enablePlayClipButtonIfInputsAreValid()
      {
      SwingUtils.warnIfNotEventDispatchThread("AudioControlPanel.enableClipButtonIfInputsAreValid()");

      playClipButton.setEnabled(areClipInputsValid());
      }

   private boolean areClipInputsValid()
      {
      SwingUtils.warnIfNotEventDispatchThread("AudioControlPanel.areClipInputsValid()");

      return audioClipChooser.isFileSelected();
      }

   private void enablePlaySpeechButtonIfInputsAreValid()
      {
      SwingUtils.warnIfNotEventDispatchThread("AudioControlPanel.enableSpeechButtonIfTextFieldsAreValid()");

      playSpeechButton.setEnabled(areSpeechInputsValid());
      }

   private boolean areSpeechInputsValid()
      {
      SwingUtils.warnIfNotEventDispatchThread("AudioControlPanel.areSpeechTextFieldsValid()");

      return isTextFieldNonEmpty(speechTextField);
      }

   private boolean isTextFieldNonEmpty(final JTextField textField)
      {
      final String text1 = textField.getText();
      final String trimmedText1 = (text1 != null) ? text1.trim() : null;
      return (trimmedText1 != null) && (trimmedText1.length() > 0);
      }

   /** Retrieves the value from the specified text field as an <code>Integer</code>. */
   private Integer getSpinnerValueAsInteger(final JSpinner spinner)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         return (Integer)spinner.getValue();
         }
      else
         {
         final Integer[] value = new Integer[1];
         try
            {
            SwingUtilities.invokeAndWait(
                  new Runnable()
                  {
                  public void run()
                     {
                     value[0] = (Integer)spinner.getValue();
                     }
                  }
            );
            return value[0];
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while fetching the spinner value", e);
            }
         catch (InvocationTargetException e)
            {
            LOG.error("InvocationTargetException while fetching the spinner value", e);
            }
         }

      return null;
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

   private class ErrorMessageDialogRunnable implements Runnable
      {
      private final Component parentComponent;
      private final String message;
      private final String title;

      private ErrorMessageDialogRunnable(final Component parentComponent, final String message, final String title)
         {
         this.parentComponent = parentComponent;
         this.message = message;
         this.title = title;
         }

      public void run()
         {
         JOptionPane.showMessageDialog(parentComponent,
                                       message,
                                       title,
                                       JOptionPane.ERROR_MESSAGE);
         }
      }

   private final class MyAsynchronousCommandExceptionHandlerCallback extends AsynchronousCommandExceptionHandlerCallback
      {
      public void handleException(final Exception exception)
         {
         showError(exception);
         }

      private void showError(final Exception e)
         {
         if (e instanceof AudioCommandException)
            {
            if (e instanceof AudioFileTooLargeException)
               {
               LOG.error("AudioFileTooLargeException caught while playing the sound: ", e);
               SwingUtilities.invokeLater(audioFileTooLargeRunnable);
               }
            else if (e instanceof AudioCommandQueueFullException)
               {
               LOG.error("AudioCommandQueueFullException caught while playing the sound: ", e);
               SwingUtilities.invokeLater(audioFileQueueFullRunnable);
               }
            else
               {
               LOG.error("AudioCommandException caught while playing the sound: ", e);
               SwingUtilities.invokeLater(invalidAudioFileRunnable);
               }
            }
         else
            {
            if (e instanceof UnknownLocalException)// ignore the stupid timeout exceptions
               {
               LOG.info("Ignoring UnknownLocalException caught while playing the sound: ", e);
               }
            else
               {
               LOG.error("Exception caught while playing the sound: ", e);
               SwingUtilities.invokeLater(failedToPlayFileRunnable);
               }
            }
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class PlayToneAction extends AbstractTimeConsumingAction
      {
      private int frequency;
      private int amplitude;
      private int duration;
      private boolean isValid;

      protected void executeGUIActionBefore()
         {
         isValid = areToneInputsValid();
         if (isValid)
            {
            frequency = getTextFieldValueAsInteger(frequencyTextField);
            amplitude = getSpinnerValueAsInteger(amplitudeSpinner);
            duration = getDuration();
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (isValid)
            {
            for (final EventListener listener : eventListeners)
               {
               listener.playTone(frequency, amplitude, duration);
               }
            }
         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class PlayClipAction extends AbstractTimeConsumingAction
      {
      private File file;
      private boolean isValid;
      private final MyAsynchronousCommandExceptionHandlerCallback asynchronousCommandExceptionHandlerCallback = new MyAsynchronousCommandExceptionHandlerCallback();

      protected void executeGUIActionBefore()
         {
         isValid = areClipInputsValid();
         if (isValid)
            {
            file = audioClipChooser.getSelectedFile();
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (isValid)
            {
            if (file != null)
               {
               if (file.exists())
                  {
                  for (final EventListener listener : eventListeners)
                     {
                     listener.playSound(file, asynchronousCommandExceptionHandlerCallback);
                     }
                  }
               else
                  {
                  LOG.error("File [" + file.getAbsolutePath() + "] does not exist!");
                  SwingUtilities.invokeLater(couldNotLoadAudioFileRunnable);
                  }
               }
            else
               {
               LOG.error("File is null!");
               SwingUtilities.invokeLater(couldNotLoadAudioFileRunnable);
               }
            }

         return null;
         }
      }

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private class PlaySpeechAction extends AbstractTimeConsumingAction
      {
      private String text;
      private boolean isValid;

      protected void executeGUIActionBefore()
         {
         isValid = areSpeechInputsValid();
         if (isValid)
            {
            text = getTextFieldValueAsString(speechTextField);
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (isValid)
            {
            for (final EventListener listener : eventListeners)
               {
               listener.playSpeech(text);
               }
            }
         return null;
         }
      }

   private static class DoubleFormatter extends NumberFormatter
      {
      public String valueToString(final Object o)
            throws ParseException
         {
         Number number = (Number)o;
         if (number != null)
            {
            final int val = number.intValue();
            number = new Integer(val);
            }

         // get rid of the freakin' commas!
         return super.valueToString(number).replaceAll("[^\\d]", "");
         }

      public Object stringToValue(final String s)
            throws ParseException
         {
         Number number = (Number)super.stringToValue(s);
         if (number != null)
            {
            final int val = number.intValue();
            number = new Integer(val);
            }
         return number;
         }
      }
   }
