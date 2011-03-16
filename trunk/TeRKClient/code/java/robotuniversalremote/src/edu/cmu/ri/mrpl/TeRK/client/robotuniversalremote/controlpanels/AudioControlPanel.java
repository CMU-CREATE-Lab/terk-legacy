package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AudioCell;
import edu.cmu.ri.mrpl.TeRK.speech.Mouth;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */

/**
 * AudioControlPanel serves as the GUI mechanism for controlling a Speaker in RUR.
 * @author Robert Newport
 * @author Eric Wan
 * @author Chris Bartley
 */

public class AudioControlPanel extends AbstractControlPanel
   {

   static final long serialVersionUID = 0;
   public static final String AUDIO_DIR = System.getProperty("user.home") + File.separator + "TeRK" + File.separator + "Audio" + File.separator;

   protected AudioCell graphCell;
   protected Hashtable values;
   private JComboBox soundComboBox;
   private final JTextField textToSpeakField = new JTextField();
   private String title = "Speaker";
   private byte[] sound = null;
   private final JRadioButton clipRadioButton = new JRadioButton("Clip:", true);
   private final JRadioButton speechRadioButton = new JRadioButton("Text to Speech:", false);

   private static final String IMPORT_FILE_ITEM = "Import File ...";
   private static final Logger LOG = Logger.getLogger(AudioControlPanel.class);
   protected AudioService controller;
   protected int audioId;

   /**
    * File chooser for loading and saving graphs. Note that it is lazily
    * instaniated, always call initFileChooser before use.
    */
   protected JFileChooser fileChooser = null;

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    */
   public AudioControlPanel(final AudioService controller, final int audioId, final AudioCell cell)
      {
      super();

      // Make sure that Audio folder exists
      // If not, create the Audio Directory
      final boolean exists = (new File(AUDIO_DIR)).exists();

      if (!exists)
         {
         final boolean success = (new File(AUDIO_DIR)).mkdirs();
         if (!success)
            {
            LOG.debug("Folder creation failed!");
            }
         }

      title = "Speaker Control";
      this.audioId = audioId;
      this.controller = controller;
      graphCell = cell;
      values = cell.getValues();

      initGUI();

      try
         {
         if (!values.isEmpty())
            {
            if (cell.isClipSelected())
               {
               sound = FileUtils.getFileAsBytes(new File(AUDIO_DIR + cell.getAudio()));
               }
            else
               {
               final String textToSpeak = cell.getTextToSpeak();
               sound = getSpeechData(textToSpeak);
               }
            controller.playSound(sound);
            }
         soundComboBox.setSelectedItem(graphCell.getAudio());
         }
      catch (IOException e)
         {
         LOG.debug("Cannot find an audio file.", e);
         }
      }

   private byte[] getSpeechData(final String textToSpeak)
      {
      // HACK ALERT: the qwerk clips the first half second of audio, so pad it with some garbage
      return Mouth.getInstance().getSpeech("terk " + textToSpeak);
      }

   /**
    * Method sets up the GridBagLayout and adds Swing components to the layout.
    */
   private void initGUI()
      {
      try
         {
         this.setLayout(new SpringLayout());

         final ButtonGroup group = new ButtonGroup();
         group.add(clipRadioButton);
         group.add(speechRadioButton);

         final ActionListener radioButtonActionListener =
               new ActionListener()
               {
               public void actionPerformed(final ActionEvent e)
                  {
                  final JRadioButton btn = (JRadioButton)e.getSource();
                  final boolean isClipSelected = clipRadioButton.equals(btn);
                  soundComboBox.setEnabled(isClipSelected);
                  textToSpeakField.setEnabled(!isClipSelected);
                  }
               };

         clipRadioButton.addActionListener(radioButtonActionListener);
         speechRadioButton.addActionListener(radioButtonActionListener);

         //file list
         final File dir = new File(AUDIO_DIR);
         final Object[] possibleValues = dir.list();

         soundComboBox = new JComboBox(possibleValues);
         soundComboBox.addItem(IMPORT_FILE_ITEM);
         soundComboBox.addItemListener(
               new ItemListener()
               {
               public void itemStateChanged(final ItemEvent ie)
                  {
                  if (ie.getItem() == IMPORT_FILE_ITEM && (fileChooser == null))
                     {
                     initFileChooser();
                     final int returnValue = fileChooser.showOpenDialog(soundComboBox);
                     if (returnValue == JFileChooser.CANCEL_OPTION)
                        {
                        soundComboBox.setSelectedItem(graphCell.getAudio());
                        fileChooser = null;
                        }
                     if (returnValue == JFileChooser.APPROVE_OPTION)
                        {
                        final File filename = fileChooser.getSelectedFile();
                        final File newSoundFile = new File(AUDIO_DIR + filename.getName());
                        final File newSoundFileItem = new File(filename.getName());

                        if (newSoundFile.exists())
                           {
                           final int overwriteResult = JOptionPane.showConfirmDialog(null,
                                                                                     "The file chosen will be overwritten. Are you sure you wish to overwrite this file?",
                                                                                     "Overwrite File",
                                                                                     JOptionPane.YES_NO_OPTION,
                                                                                     JOptionPane.WARNING_MESSAGE);

                           if (overwriteResult == JOptionPane.NO_OPTION)
                              {
                              soundComboBox.setSelectedItem(graphCell.getAudio());
                              fileChooser = null;
                              return;
                              }
                           }
                        else
                           {
                           soundComboBox.addItem(newSoundFileItem);
                           soundComboBox.setSelectedItem(newSoundFileItem);
                           }
                        // TODO: Check to see if the file is copied successfully
                        fileCopy(filename.toString(), AUDIO_DIR + filename.getName());
                        soundComboBox.setSelectedItem(filename.getName());
                        }
                     fileChooser = null;
                     }
                  }
               });

         // initialize widgets
         textToSpeakField.setText(graphCell.getTextToSpeak());
         clipRadioButton.setSelected(graphCell.isClipSelected());
         speechRadioButton.setSelected(!graphCell.isClipSelected());
         soundComboBox.setEnabled(graphCell.isClipSelected());
         textToSpeakField.setEnabled(!graphCell.isClipSelected());

         // layout widgets
         final JButton playButton = new JButton("Play");
         playButton.addActionListener(new PlayButtonActionListener());

         final JPanel widgetsPanel = new JPanel(new SpringLayout());
         widgetsPanel.add(clipRadioButton);
         widgetsPanel.add(soundComboBox);
         widgetsPanel.add(speechRadioButton);
         widgetsPanel.add(textToSpeakField);
         SpringLayoutUtilities.makeCompactGrid(widgetsPanel,
                                               2, 2, // rows, cols
                                               0, 0, // initX, initY
                                               10, 10);// xPad, yPad

         final JPanel playButtonPanel = new JPanel(new SpringLayout());
         playButtonPanel.add(Box.createGlue());
         playButtonPanel.add(playButton);
         SpringLayoutUtilities.makeCompactGrid(playButtonPanel,
                                               1, 2, // rows, cols
                                               0, 0, // initX, initY
                                               0, 0);// xPad, yPad

         add(widgetsPanel);
         add(playButtonPanel);
         SpringLayoutUtilities.makeCompactGrid(this,
                                               2, 1, // rows, cols
                                               10, 10, // initX, initY
                                               10, 10);// xPad, yPad
         this.setAutoscrolls(true);
         }
      catch (Exception e)
         {
         e.printStackTrace();
         }
      }

   /**
    * Utility method that ensures the file chooser is created. Start-up time
    * is improved by lazily instaniating choosers.
    * Taken from the JGraph Examples
    *
    */
   protected void initFileChooser()
      {
      if (fileChooser == null)
         {
         fileChooser = new JFileChooser();
         final FileFilter fileFilter = new FileFilter()
         {
         /**
          * @see FileFilter#accept(File)
          */
         public boolean accept(final File f)
            {
            if (f == null)
               {
               return false;
               }
            if (f.getName() == null)
               {
               return false;
               }
            if (f.getName().endsWith(".wav"))
               {
               return true;
               }
            if (f.isDirectory())
               {
               return true;
               }

            return false;
            }

         /**
          * @see FileFilter#getDescription()
          */
         public String getDescription()
            {
            return "WAV file (.wav)";
            }
         };
         fileChooser.setFileFilter(fileFilter);
         }
      }

   protected void fileCopy(final String srcFile, final String destFile)
      {
      try
         {
         final File inputFile = new File(srcFile);
         final File outputFile = new File(destFile);

         final FileReader in = new FileReader(inputFile);
         final FileWriter out = new FileWriter(outputFile);
         int c;

         while ((c = in.read()) != -1)
            {
            out.write(c);
            }

         in.close();
         out.close();
         }
      catch (IOException ioe)
         {
         LOG.debug("File copy error!", ioe);
         }
      }

   public String getTitle()
      {
      return title;
      }

   public AudioCell getGraphCell()
      {
      return graphCell;
      }

   private class PlayButtonActionListener implements ActionListener
      {
      public void actionPerformed(final ActionEvent e)
         {
         graphCell.setIsClipSelected(clipRadioButton.isSelected());
         if (clipRadioButton.isSelected())
            {
            final String soundFileName = soundComboBox.getSelectedItem().toString();
            try
               {
               sound = FileUtils.getFileAsBytes(new File(AUDIO_DIR + soundFileName));
               }
            catch (IOException audioException)
               {
               // TODO: Display messages
               LOG.debug("Sound File cannot be played!");
               return;
               }
            graphCell.setAudio(soundFileName);
            }
         else
            {
            final String textToSpeak = textToSpeakField.getText();
            sound = getSpeechData(textToSpeak);
            graphCell.setTextToSpeak(textToSpeak);
            }
         controller.playSound(sound);
         }
      }
   }
