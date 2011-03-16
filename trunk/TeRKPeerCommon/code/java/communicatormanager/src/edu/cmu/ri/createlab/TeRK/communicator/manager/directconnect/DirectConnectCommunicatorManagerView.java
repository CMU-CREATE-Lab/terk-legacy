package edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect;

import java.awt.Component;
import java.awt.Cursor;
import java.util.PropertyResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.communicator.manager.TerkCommunicatorManagerListener;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public final class DirectConnectCommunicatorManagerView
   {
   private static final Logger LOG = Logger.getLogger(DirectConnectCommunicatorManagerView.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(DirectConnectCommunicatorManagerView.class.getName());

   // the panel in which this view's widgets will be placed (necessary for setting the cursor and such)
   private final Component parentComponent;

   // widgets
   private final JCheckBox directConnectionsCheckbox = new JCheckBox(RESOURCES.getString("label.direct-connections"));

   // runnables
   private final Runnable directConnectionsCheckboxEnabler = new SetWidgetEnabledRunnable(directConnectionsCheckbox, true);
   private final Runnable directConnectionsCheckboxDisabler = new SetWidgetEnabledRunnable(directConnectionsCheckbox, false);
   private final Runnable directConnectionsCheckboxUnselector = new SetCheckBoxSelectedRunnable(directConnectionsCheckbox, false);

   public DirectConnectCommunicatorManagerView(final DirectConnectCommunicatorManager manager, final Component parentComponent)
      {
      this.parentComponent = parentComponent;
      manager.addTerkCommunicatorCreationEventListener(new MyTerkCommunicatorCreationEventListener());
      manager.addTerkCommunicatorManagerListener(new MyTerkCommunicatorManagerListener());

      directConnectionsCheckbox.setFont(GUIConstants.FONT_SMALL);
      directConnectionsCheckbox.addActionListener(
            new AbstractTimeConsumingAction()
            {
            protected void executeGUIActionBefore()
               {
               LOG.debug("AbstractTimeConsumingAction.executeGUIActionBefore()");
               directConnectionsCheckboxDisabler.run();
               parentComponent.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               }

            protected Object executeTimeConsumingAction()
               {
               LOG.debug("direct-connect box checked: " + directConnectionsCheckbox.isSelected());
               if (directConnectionsCheckbox.isSelected())
                  {
                  manager.createCommunicator();
                  }
               else
                  {
                  manager.shutdownCommunicator();
                  }

               return null;
               }

            protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
               {
               LOG.debug("AbstractTimeConsumingAction.executeGUIActionAfter() [toggle direct-connect checkbox]");
               // If we're turning OFF direct-connect, then just set the cursor to the default and enable the
               // checkbox.  However, if we're trying to turn ON direct-connect, then that's done asynchronously, plus
               // we need to know whether it succeeded, so we need to let the
               // MyTerkCommunicatorCreationEventListener make the GUI calls.
               if (!directConnectionsCheckbox.isSelected())
                  {
                  parentComponent.setCursor(Cursor.getDefaultCursor());
                  directConnectionsCheckboxEnabler.run();
                  }
               }
            });
      }

   public JCheckBox getCheckbox()
      {
      return directConnectionsCheckbox;
      }

   private final class SetWidgetEnabledRunnable implements Runnable
      {
      private final Component component;
      private final boolean isEnabled;

      private SetWidgetEnabledRunnable(final Component component, final boolean isEnabled)
         {
         this.component = component;
         this.isEnabled = isEnabled;
         }

      public void run()
         {
         component.setEnabled(isEnabled);
         }
      }

   private class SetCheckBoxSelectedRunnable implements Runnable
      {
      private final JCheckBox checkbox;
      private final boolean isSelected;

      private SetCheckBoxSelectedRunnable(final JCheckBox checkbox, final boolean isSelected)
         {
         this.checkbox = checkbox;
         this.isSelected = isSelected;
         }

      public void run()
         {
         checkbox.setSelected(isSelected);
         }
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         LOG.debug("DirectConnectCommunicatorManagerView$MyTerkCommunicatorCreationEventListener.afterSuccessfulConstruction()");

         enable();
         }

      public void afterFailedConstruction()
         {
         LOG.debug("DirectConnectCommunicatorManagerView$MyTerkCommunicatorCreationEventListener.afterFailedConstruction()");

         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  directConnectionsCheckbox.setSelected(false);
                  enable();
                  }
               });
         }

      public void afterWaitForShutdown()
         {
         // the communicator has been shut down, so we need to make sure that the checkbox is unchecked
         SwingUtilities.invokeLater(directConnectionsCheckboxUnselector);
         }

      private void enable()
         {
         if (SwingUtilities.isEventDispatchThread())
            {
            enableWorkhorse();
            }
         else
            {
            SwingUtilities.invokeLater(
                  new Runnable()
                  {
                  public void run()
                     {
                     enableWorkhorse();
                     }
                  });
            }
         }

      private void enableWorkhorse()
         {
         directConnectionsCheckboxEnabler.run();
         parentComponent.setCursor(Cursor.getDefaultCursor());
         }
      }

   private class MyTerkCommunicatorManagerListener implements TerkCommunicatorManagerListener
      {
      public void handleEnabledStateChange(final boolean isEnabled)
         {
         directConnectionsCheckbox.setEnabled(isEnabled);
         }
      }
   }
