package edu.cmu.ri.createlab.TeRK.client.universalremote;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.PropertyResourceBundle;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFile;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFileManagerModel;
import edu.cmu.ri.createlab.TeRK.expression.manager.ExpressionFileManagerView;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
final class ExpressionFileManagerControlsView
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ExpressionFileManagerControlsView.class.getName());

   private final JPanel panel = new JPanel();

   private final JButton openButton = GUIConstants.createButton(RESOURCES.getString("button.label.open"));
   private final JButton deleteButton = GUIConstants.createButton(RESOURCES.getString("button.label.delete"));
   private final Runnable setEnabledRunnable = new SetEnabledRunnable(true);
   private final Runnable setDisabledRunnable = new SetEnabledRunnable(false);

   private final JFrame jFrame;
   private final ExpressionFileManagerView fileManagerView;
   private final ExpressionFileManagerModel fileManagerModel;
   private final ExpressionFileManagerControlsController expressionFileManagerControlsController;

   ExpressionFileManagerControlsView(final JFrame jFrame,
                                     final ExpressionFileManagerView fileManagerView,
                                     final ExpressionFileManagerModel fileManagerModel,
                                     final ExpressionFileManagerControlsController expressionFileManagerControlsController)
      {
      this.jFrame = jFrame;
      this.fileManagerView = fileManagerView;
      this.fileManagerModel = fileManagerModel;
      this.expressionFileManagerControlsController = expressionFileManagerControlsController;

      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);
      panel.setBackground(Color.WHITE);

      final Component spacer = GUIConstants.createRigidSpacer();
      layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .add(openButton)
                  .add(spacer)
                  .add(deleteButton)
      );
      layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.CENTER)
                  .add(openButton)
                  .add(spacer)
                  .add(deleteButton)
      );

      final OpenExpressionAction openExpressionAction = new OpenExpressionAction();

      // change enabled state of button depending on whether an item in the list is selected
      fileManagerView.addListSelectionListener(
            new ListSelectionListener()
            {
            public void valueChanged(final ListSelectionEvent e)
               {
               openButton.setEnabled(!fileManagerView.isSelectionEmpty());
               deleteButton.setEnabled(!fileManagerView.isSelectionEmpty());
               }
            });

      // double-clicking should cause the expression to be opened
      fileManagerView.addMouseListener(
            new MouseAdapter()
            {
            public void mouseClicked(final MouseEvent e)
               {
               if (e.getClickCount() == 2)
                  {
                  openExpressionAction.actionPerformed(null);
                  }
               }
            });

      // clicking the Open button should open the selected expression
      openButton.addActionListener(openExpressionAction);

      // clicking the Delete button should delete the selected expression
      deleteButton.addActionListener(new DeleteExpressionAction());
      }

   Component getComponent()
      {
      return panel;
      }

   public void setEnabled(final boolean isEnabled)
      {
      final Runnable runnable = isEnabled ? setEnabledRunnable : setDisabledRunnable;
      if (SwingUtilities.isEventDispatchThread())
         {
         runnable.run();
         }
      else
         {
         SwingUtilities.invokeLater(runnable);
         }
      }

   private class SetEnabledRunnable implements Runnable
      {
      private final boolean isEnabled;

      private SetEnabledRunnable(final boolean isEnabled)
         {
         this.isEnabled = isEnabled;
         }

      public void run()
         {
         openButton.setEnabled(isEnabled && !fileManagerView.isSelectionEmpty());
         deleteButton.setEnabled(isEnabled && !fileManagerView.isSelectionEmpty());
         }
      }

   private final class OpenExpressionAction extends AbstractTimeConsumingAction
      {
      private XmlExpression expression = null;

      protected void executeGUIActionBefore()
         {
         final int selectedIndex = fileManagerView.getSelectedIndex();
         if (selectedIndex >= 0)
            {
            expression = fileManagerModel.getExpressionAt(selectedIndex);
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (expression != null)
            {
            expressionFileManagerControlsController.openExpression(expression);
            }
         return null;
         }
      }

   private final class DeleteExpressionAction extends AbstractTimeConsumingAction
      {
      private ExpressionFile expressionFile = null;

      protected void executeGUIActionBefore()
         {
         final int selectedIndex = fileManagerView.getSelectedIndex();
         if (selectedIndex >= 0)
            {
            expressionFile = fileManagerModel.getExpressionFileAt(selectedIndex);

            final String message = MessageFormat.format(RESOURCES.getString("dialog.message.delete-expression-confirmation"),
                                                        expressionFile.getPrettyName());
            final int selectedOption = JOptionPane.showConfirmDialog(jFrame,
                                                                     message,
                                                                     RESOURCES.getString("dialog.title.delete-expression-confirmation"),
                                                                     JOptionPane.YES_NO_OPTION,
                                                                     JOptionPane.WARNING_MESSAGE);

            if (selectedOption != JOptionPane.YES_OPTION)
               {
               expressionFile = null;
               }
            }
         }

      protected Object executeTimeConsumingAction()
         {
         if (expressionFile != null)
            {
            expressionFileManagerControlsController.deleteExpression(expressionFile);
            }
         return null;
         }
      }
   }
