package edu.cmu.ri.createlab.TeRK.expression.manager;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import javax.swing.JOptionPane;
import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.userinterface.dialog.AbstractAlert;
import org.apache.log4j.Logger;

/**
 * The <code>ExpressionSavingDialogRunnable</code> assists in the saving of {@link XmlExpression}s.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class ExpressionSavingDialogRunnable extends AbstractAlert implements Runnable
   {
   private static final Logger LOG = Logger.getLogger(ExpressionSavingDialogRunnable.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ExpressionSavingDialogRunnable.class.getName());
   private static final String XML_FILE_EXTENSION = ".xml";

   private final XmlExpression expression;

   ExpressionSavingDialogRunnable(final XmlExpression expression, final Component parentComponent)
      {
      super(parentComponent);
      this.expression = expression;
      }

   public void run()
      {
      if (expression == null)
         {
         showAlert(RESOURCES.getString("dialog.title.cannot-save-empty-expression"),
                   RESOURCES.getString("dialog.message.cannot-save-empty-expression"));
         }
      else
         {
         String requestedFileName = "";
         boolean promptForNewName = false;

         while (requestedFileName.length() <= 0 || promptForNewName)
            {
            requestedFileName = JOptionPane.showInputDialog(getParentComponent(),
                                                            RESOURCES.getString("dialog.message.save-expression-as"),
                                                            RESOURCES.getString("dialog.title.save-expression-as"),
                                                            JOptionPane.QUESTION_MESSAGE);

            // todo: improve the error checking here

            if (requestedFileName == null)
               {
               // user hit Cancel, just so just break
               break;
               }
            else if (requestedFileName.length() == 0)
               {
               showAlert(RESOURCES.getString("dialog.title.cannot-save-expression-empty-filename"),
                         RESOURCES.getString("dialog.message.cannot-save-expression-empty-filename"));

               continue;
               }
            else
               {
               // make sure the user doesn't try anything like ../ or /
               File fileToSave = new File(TerkConstants.FilePaths.EXPRESSIONS_DIR, requestedFileName);
               //LOG.debug("Initial filepath [" + fileToSave.getAbsolutePath() + "]");
               while (!TerkConstants.FilePaths.EXPRESSIONS_DIR.equals(fileToSave.getParentFile()))
                  {
                  fileToSave = new File(TerkConstants.FilePaths.EXPRESSIONS_DIR, fileToSave.getName());
                  }
               //LOG.debug("New filepath     [" + fileToSave.getAbsolutePath() + "]");

               requestedFileName = fileToSave.getName();

               // make sure the filename ends with .xml
               final boolean needsXmlExtension = !requestedFileName.toLowerCase().endsWith(XML_FILE_EXTENSION);
               fileToSave = new File(TerkConstants.FilePaths.EXPRESSIONS_DIR, requestedFileName + (needsXmlExtension ? XML_FILE_EXTENSION : ""));

               //LOG.debug(".xml filepath    [" + fileToSave.getAbsolutePath() + "]");

               // make sure the filename isn't empty
               if (XML_FILE_EXTENSION.equalsIgnoreCase(fileToSave.getName()))
                  {
                  showAlert(RESOURCES.getString("dialog.title.cannot-save-expression-empty-filename"),
                            RESOURCES.getString("dialog.message.cannot-save-expression-empty-filename"));

                  continue;
                  }

               if (fileToSave.exists())
                  {
                  // don't let them overwrite directories or hidden files
                  if (fileToSave.isDirectory() || fileToSave.isHidden())
                     {
                     LOG.debug("directory or hidden");
                     showAlert(RESOURCES.getString("dialog.title.cannot-save-expression-invalid-filename"),
                               RESOURCES.getString("dialog.message.cannot-save-expression-invalid-filename"));

                     promptForNewName = true;
                     continue;
                     }
                  else
                     {
                     // Verify that the user wants to overwrite this file
                     final int overwriteResult = JOptionPane.showConfirmDialog(getParentComponent(),
                                                                               RESOURCES.getString("dialog.message.confirm-overwrite-expression"),
                                                                               RESOURCES.getString("dialog.title.confirm-overwrite-expression"),
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
                           showAlert(RESOURCES.getString("dialog.title.cannot-save-expression-readonly-file"),
                                     RESOURCES.getString("dialog.message.cannot-save-expression-readonly-file"));
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
      FileWriter fileWriter = null;
      try
         {
         fileWriter = new FileWriter(fileToSave);
         fileWriter.write(expression.toXmlDocumentStringFormatted());
         }
      catch (IOException e)
         {
         LOG.error("IOException while writing the file", e);
         showAlert(RESOURCES.getString("dialog.title.cannot-save-expression"),
                   RESOURCES.getString("dialog.message.cannot-save-expression"));
         }
      finally
         {
         if (fileWriter != null)
            {
            try
               {
               fileWriter.close();
               }
            catch (IOException e)
               {
               LOG.error("Failed to close the file writer.  Oh well.", e);
               }
            }
         }
      }
   }
