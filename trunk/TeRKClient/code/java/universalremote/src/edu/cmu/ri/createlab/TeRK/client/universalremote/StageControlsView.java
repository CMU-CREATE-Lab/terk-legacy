package edu.cmu.ri.createlab.TeRK.client.universalremote;

import java.awt.Color;
import java.awt.Component;
import java.util.PropertyResourceBundle;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
final class StageControlsView
   {
   private static final Logger LOG = Logger.getLogger(StageControlsView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(StageControlsView.class.getName());

   private final JPanel panel = new JPanel();

   private final JButton clearButton = GUIConstants.createButton(RESOURCES.getString("button.label.clear"));
   private final JButton refresh = GUIConstants.createButton(RESOURCES.getString("button.label.refresh"));
   private final JButton saveButton = GUIConstants.createButton(RESOURCES.getString("button.label.save"));
   private final Runnable setEnabledRunnable = new SetEnabledRunnable(true);
   private final Runnable setDisabledRunnable = new SetEnabledRunnable(false);

   StageControlsView(final StageControlsController stageControlsController)
      {
      final GroupLayout layout = new GroupLayout(panel);
      panel.setLayout(layout);
      panel.setBackground(Color.WHITE);

      final Component spacerLeft = GUIConstants.createRigidSpacer();
      final Component spacerRight = GUIConstants.createRigidSpacer();
      layout.setHorizontalGroup(
            layout.createSequentialGroup()
                  .add(clearButton)
                  .add(spacerLeft)
                  .add(refresh)
                  .add(spacerRight)
                  .add(saveButton)
      );
      layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.CENTER)
                  .add(clearButton)
                  .add(spacerLeft)
                  .add(refresh)
                  .add(spacerRight)
                  .add(saveButton)
      );

      // clicking the Clear button should clear all existing control panels
      clearButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected Object executeTimeConsumingAction()
               {
               stageControlsController.clearControlPanels();
               return null;
               }
            });

      // clicking the Refresh button should refresh the open control panels on the stage
      refresh.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected Object executeTimeConsumingAction()
               {
               stageControlsController.refreshControlPanels();
               return null;
               }
            });

      // clicking the Save button should save the current control panel config into a new expression
      saveButton.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected Object executeTimeConsumingAction()
               {
               stageControlsController.saveExpression();
               return null;
               }
            });
      }

   Component getComponent()
      {
      return panel;
      }

   public void setEnabled(final boolean isEnabled)
      {
      LOG.debug("StageControlsView.setEnabled(" + isEnabled + ")");
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
         refresh.setEnabled(isEnabled);
         saveButton.setEnabled(isEnabled);
         clearButton.setEnabled(isEnabled);

         for (int i = 0; i < panel.getComponentCount(); i++)
            {
            panel.getComponent(i).setVisible(isEnabled);
            }
         }
      }
   }