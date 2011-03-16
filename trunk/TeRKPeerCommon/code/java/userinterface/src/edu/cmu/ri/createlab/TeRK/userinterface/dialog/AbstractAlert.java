package edu.cmu.ri.createlab.TeRK.userinterface.dialog;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * <p>
 * <code>AbstractAlert</code> provides an easy way for GUI applications to display alerts to the user, and ensure that
 * it executes from within the Swing GUI thread.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractAlert
   {
   private final Component parentComponent;

   public AbstractAlert(final Component parentComponent)
      {
      this.parentComponent = parentComponent;
      }

   protected final Component getParentComponent()
      {
      return parentComponent;
      }

   /** Shows an alert using the given title and message. */
   protected final void showAlert(final String title, final String message)
      {
      runInGUIThread(
            new Runnable()
            {
            public void run()
               {
               JOptionPane.showMessageDialog(parentComponent,
                                             message,
                                             title,
                                             JOptionPane.INFORMATION_MESSAGE);
               }
            });
      }

   protected final void runInGUIThread(final Runnable runnable)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         runnable.run();
         }
      else
         {
         SwingUtilities.invokeLater(runnable);
         }
      }
   }
