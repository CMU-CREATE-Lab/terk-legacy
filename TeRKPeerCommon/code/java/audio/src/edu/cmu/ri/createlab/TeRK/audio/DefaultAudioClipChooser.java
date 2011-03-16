package edu.cmu.ri.createlab.TeRK.audio;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.PropertyResourceBundle;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.SwingUtils;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DefaultAudioClipChooser implements AudioClipChooser
   {
   private static final Logger LOG = Logger.getLogger(DefaultAudioClipChooser.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DefaultAudioClipChooser.class.getName());

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame("DefaultAudioClipChooser");

               // add the root panel to the JFrame
               jFrame.add(new DefaultAudioClipChooser().getComponent());

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

   private final JPanel panel = new JPanel();
   private final JFileChooser fileChooser = new JFileChooser();
   private final JLabel clipPathLabel = GUIConstants.createLabel(RESOURCES.getString("label.file"));
   private final JTextField clipPathTextField = createTextField(10);
   private final JButton findClipButton = GUIConstants.createButton(RESOURCES.getString("button.label.find"), true);
   private final Set<AudioClipChooserEventListener> audioClipChooserEventListeners = new HashSet<AudioClipChooserEventListener>();

   public DefaultAudioClipChooser()
      {
      clipPathTextField.addKeyListener(
            new KeyAdapter()
            {
            public void keyReleased(final KeyEvent e)
               {
               for (final AudioClipChooserEventListener listener : audioClipChooserEventListeners)
                  {
                  listener.handleSelectedFileChange();
                  }
               }
            }
      );
      findClipButton.addActionListener(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               final int returnValue = fileChooser.showOpenDialog(panel);
               if (returnValue == JFileChooser.APPROVE_OPTION)
                  {
                  final File file = fileChooser.getSelectedFile();
                  clipPathTextField.setText(file.getAbsolutePath());
                  for (final AudioClipChooserEventListener listener : audioClipChooserEventListeners)
                     {
                     listener.handleSelectedFileChange();
                     }

                  LOG.info("File chosen: " + file.getName());
                  }
               else
                  {
                  LOG.info("Find clip dialog cancelled by user.");
                  }
               }
            });

      fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fileChooser.setAcceptAllFileFilterUsed(false);
      fileChooser.setFileFilter(
            new FileFilter()
            {
            public boolean accept(final File file)
               {
               if (file != null && file.exists())
                  {
                  if (file.isDirectory())
                     {
                     return true;
                     }
                  return file.getName().toLowerCase().endsWith(RESOURCES.getString("filechooser.filter.accepted-file-type.extension"));
                  }
               return false;
               }

            public String getDescription()
               {
               return RESOURCES.getString("filechooser.filter.accepted-file-type.name");
               }
            });

      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);
      panel.setBackground(Color.WHITE);

      layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .add(clipPathLabel)
                  .add(clipPathTextField)
                  .add(findClipButton)
      );
      layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.CENTER)
                  .add(clipPathLabel)
                  .add(clipPathTextField)
                  .add(findClipButton)
      );
      }

   public Component getComponent()
      {
      return panel;
      }

   public void setEnabled(final boolean isEnabled)
      {
      clipPathTextField.setEnabled(isEnabled);
      findClipButton.setEnabled(isEnabled);
      }

   public boolean isFileSelected()
      {
      SwingUtils.warnIfNotEventDispatchThread("DefaultAudioClipChooser.isFileSelected()");

      if (isTextFieldNonEmpty(clipPathTextField))
         {
         final File file = getTextFieldValueAsFile(clipPathTextField);
         return file != null && file.exists() && file.isFile();
         }
      return false;
      }

   public File getSelectedFile()
      {
      return getTextFieldValueAsFile(clipPathTextField);
      }

   public String getSelectedFilePath()
      {
      final File file = getSelectedFile();

      if (file != null)
         {
         return file.getAbsolutePath();
         }

      return null;
      }

   public void setSelectedFilePath(final String path)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         clipPathTextField.setText(path);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  clipPathTextField.setText(path);
                  }
               });
         }
      }

   public void addFilePathFieldActionListener(final ActionListener listener)
      {
      if (listener != null)
         {
         clipPathTextField.addActionListener(listener);
         }
      }

   public void addAudioClipChooserEventListener(final AudioClipChooserEventListener listener)
      {
      if (listener != null)
         {
         audioClipChooserEventListeners.add(listener);
         }
      }

   private JTextField createTextField(final int numColumns)
      {
      final JTextField textField = new JTextField(numColumns);
      textField.setFont(GUIConstants.FONT_NORMAL);
      textField.setMinimumSize(textField.getPreferredSize());
      textField.setMaximumSize(textField.getPreferredSize());
      return textField;
      }

   private boolean isTextFieldNonEmpty(final JTextField textField)
      {
      final String text1 = textField.getText();
      final String trimmedText1 = (text1 != null) ? text1.trim() : null;
      return (trimmedText1 != null) && (trimmedText1.length() > 0);
      }

   /** Retrieves the value from the specified text field as a {@link File}; returns <code>null</code> if the file does not exist. */
   private File getTextFieldValueAsFile(final JTextField textField)
      {
      final String filePath = getTextFieldValueAsString(textField);
      if (filePath != null)
         {
         final File file = new File(filePath);
         if (file.exists())
            {
            return file;
            }
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
   }
