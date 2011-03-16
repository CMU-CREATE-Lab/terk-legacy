package edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence;

import java.awt.Component;
import java.io.File;
import java.util.PropertyResourceBundle;
import javax.swing.JOptionPane;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.userinterface.dialog.AbstractAlert;
import org.apache.log4j.Logger;

/**
 * The <code>SequenceSavingDialogRunnable</code> assists in the saving of {@link Sequence}s.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Alex Styler (astyler@gmail.com)
 */
public class SequenceSavingDialogRunnable extends AbstractAlert implements Runnable
   {
   private static final Logger LOG = Logger.getLogger(SequenceSavingDialogRunnable.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(SequenceSavingDialogRunnable.class.getName());
   private static final String XML_FILE_EXTENSION = ".xml";

   private final Sequence sequence;
   private boolean didSave = false;

   public SequenceSavingDialogRunnable(final Sequence sequence, final Component parentComponent)
      {
      super(parentComponent);
      this.sequence = sequence;
      }

   public void run()
      {
      if (sequence == null || sequence.getSize() < 1)
         {
         showAlert(RESOURCES.getString("dialog.title.cannot-save-empty-sequence"),
                   RESOURCES.getString("dialog.message.cannot-save-empty-sequence"));
         }
      else
         {
         String requestedFileName = "";
         boolean promptForNewName = false;

         while (requestedFileName.length() <= 0 || promptForNewName)
            {
            requestedFileName = JOptionPane.showInputDialog(getParentComponent(),
                                                            RESOURCES.getString("dialog.message.save-sequence-as"),
                                                            RESOURCES.getString("dialog.title.save-sequence-as"),
                                                            JOptionPane.QUESTION_MESSAGE);

            // todo: improve the error checking here

            if (requestedFileName == null)
               {
               // user hit Cancel, just so just break
               break;
               }
            else if (requestedFileName.length() == 0)
               {
               showAlert(RESOURCES.getString("dialog.title.cannot-save-sequence-empty-filename"),
                         RESOURCES.getString("dialog.message.cannot-save-sequence-empty-filename"));

               continue;
               }
            else
               {
               // make sure the user doesn't try anything like ../ or /
               File fileToSave = new File(TerkConstants.FilePaths.SEQUENCES_DIR, requestedFileName);
               //LOG.debug("Initial filepath [" + fileToSave.getAbsolutePath() + "]");
               while (!TerkConstants.FilePaths.SEQUENCES_DIR.equals(fileToSave.getParentFile()))
                  {
                  fileToSave = new File(TerkConstants.FilePaths.SEQUENCES_DIR, fileToSave.getName());
                  }
               //LOG.debug("New filepath     [" + fileToSave.getAbsolutePath() + "]");

               requestedFileName = fileToSave.getName();

               // make sure the filename ends with .xml
               final boolean needsXmlExtension = !requestedFileName.toLowerCase().endsWith(XML_FILE_EXTENSION);
               fileToSave = new File(TerkConstants.FilePaths.SEQUENCES_DIR, requestedFileName + (needsXmlExtension ? XML_FILE_EXTENSION : ""));

               //LOG.debug(".xml filepath    [" + fileToSave.getAbsolutePath() + "]");

               // make sure the filename isn't empty
               if (XML_FILE_EXTENSION.equalsIgnoreCase(fileToSave.getName()))
                  {
                  showAlert(RESOURCES.getString("dialog.title.cannot-save-sequence-empty-filename"),
                            RESOURCES.getString("dialog.message.cannot-save-sequence-empty-filename"));

                  continue;
                  }

               if (fileToSave.exists())
                  {
                  // don't let them overwrite directories or hidden files
                  if (fileToSave.isDirectory() || fileToSave.isHidden())
                     {
                     LOG.debug("directory or hidden");
                     showAlert(RESOURCES.getString("dialog.title.cannot-save-sequence-invalid-filename"),
                               RESOURCES.getString("dialog.message.cannot-save-sequence-invalid-filename"));

                     promptForNewName = true;
                     continue;
                     }
                  else
                     {
                     // Verify that the user wants to overwrite this file
                     final int overwriteResult = JOptionPane.showConfirmDialog(getParentComponent(),
                                                                               RESOURCES.getString("dialog.message.confirm-overwrite-sequence"),
                                                                               RESOURCES.getString("dialog.title.confirm-overwrite-sequence"),
                                                                               JOptionPane.YES_NO_OPTION,
                                                                               JOptionPane.WARNING_MESSAGE);

                     if (overwriteResult == JOptionPane.YES_OPTION)
                        {
                        if (fileToSave.canWrite())
                           {
                           saveFile(fileToSave);
                           break;
                           }
                        else
                           {
                           showAlert(RESOURCES.getString("dialog.title.cannot-save-sequence-readonly-file"),
                                     RESOURCES.getString("dialog.message.cannot-save-sequence-readonly-file"));
                           promptForNewName = true;
                           }
                        }
                     else
                        {
                        promptForNewName = true;
                        }

                     continue;
                     }
                  }
               else
                  {
                  saveFile(fileToSave);
                  break;
                  }
               }
            }
         }
      }

   private void saveFile(final File fileToSave)
      {
      String name = fileToSave.getName();
      name = name.substring(0, name.length() - 4);
      sequence.setName(name);
      didSave = SequenceFileHandler.getInstance().saveFile(sequence, fileToSave);
      }

   public boolean saved()
      {
      return didSave;
      }
   }
