package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.awt.Component;
import java.util.PropertyResourceBundle;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandler;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import org.apache.log4j.Logger;
import org.jdesktop.layout.GroupLayout;

/**
 * <p>
 * <code>FinchGUIApplication</code> provides a basic framework for GUI Finch applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class FinchGUIApplication extends BaseFinchApplication
   {
   private static final Logger LOG = Logger.getLogger(AccelerometerControlledOrbGUI.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(FinchGUIApplication.class.getName());

   private final JPanel mainPanel = new JPanel();
   private final JPanel subPanel = new JPanel();
   private final JButton connectDisconnectButton = GUIConstants.createButton(RESOURCES.getString("button.label.connect"), true);

   FinchGUIApplication()
      {
      // action listeners and event handlers
      connectDisconnectButton.addActionListener(new MyConnectDisconnectButtonActionListener());
      addConnectionStrategyEventHandler(new MyConnectionStrategyEventHandler());

      // layout
      final GroupLayout layout = new GroupLayout(mainPanel);
      mainPanel.setLayout(layout);

      layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.CENTER)
                  .add(connectDisconnectButton)
                  .add(subPanel)
      );
      layout.setVerticalGroup(
            layout.createSequentialGroup()
                  .add(connectDisconnectButton)
                  .add(subPanel)
      );
      }

   protected final Component getMainComponent()
      {
      return mainPanel;
      }

   protected final Component getSubComponent()
      {
      return subPanel;
      }

   private final class MyConnectionStrategyEventHandler implements ConnectionStrategyEventHandler
      {
      private final Runnable attemptingConnectionEventRunnable =
            new Runnable()
            {
            public void run()
               {
               LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler: attemptingConnectionEventRunnable.run()");
               connectDisconnectButton.setText(RESOURCES.getString("button.label.cancel"));
               connectDisconnectButton.setEnabled(true);
               }
            };

      private final Runnable connectionEventRunnable =
            new Runnable()
            {
            public void run()
               {
               LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler: connectionEventRunnable.run()");
               connectDisconnectButton.setText(RESOURCES.getString("button.label.disconnect"));
               connectDisconnectButton.setEnabled(true);
               }
            };

      private final Runnable failedConnectionEventRunnable =
            new Runnable()
            {
            public void run()
               {
               LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler: failedConnectionEventRunnable.run()");
               connectDisconnectButton.setText(RESOURCES.getString("button.label.connect"));
               connectDisconnectButton.setEnabled(true);
               }
            };

      private final Runnable attemptingDisconnectionEventRunnable =
            new Runnable()
            {
            public void run()
               {
               LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler: attemptingDisconnectionEventRunnable.run()");
               connectDisconnectButton.setText(RESOURCES.getString("button.label.disconnecting"));
               connectDisconnectButton.setEnabled(false);
               }
            };

      private final Runnable disconnectionEventRunnable =
            new Runnable()
            {
            public void run()
               {
               LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler: disconnectionEventRunnable.run()");
               connectDisconnectButton.setText(RESOURCES.getString("button.label.connect"));
               connectDisconnectButton.setEnabled(true);
               }
            };

      public void handleAttemptingConnectionEvent()
         {
         LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler.handleAttemptingConnectionEvent()");
         runInGUIThread(attemptingConnectionEventRunnable);
         }

      public void handleConnectionEvent()
         {
         LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler.handleConnectionEvent()");
         runInGUIThread(connectionEventRunnable);
         }

      public void handleFailedConnectionEvent()
         {
         LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler.handleFailedConnectionEvent()");
         runInGUIThread(failedConnectionEventRunnable);
         }

      public void handleAttemptingDisconnectionEvent()
         {
         LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler.handleAttemptingDisconnectionEvent()");
         runInGUIThread(attemptingDisconnectionEventRunnable);
         }

      public void handleDisconnectionEvent()
         {
         LOG.debug("FinchGUIApplication$MyConnectionStrategyEventHandler.handleDisconnectionEvent()");
         runInGUIThread(disconnectionEventRunnable);
         }

      private void runInGUIThread(final Runnable runnable)
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

   @SuppressWarnings({"CloneableClassWithoutClone"})
   private final class MyConnectDisconnectButtonActionListener extends AbstractTimeConsumingAction
      {
      private MyConnectDisconnectButtonActionListener()
         {
         super(FinchGUIApplication.this.mainPanel);
         }

      protected Object executeTimeConsumingAction()
         {
         if (isConnected())
            {
            // we're already connected, so do a disconnect
            disconnect();
            }
         else
            {
            if (isConnecting())
               {
               // we're trying to connect, so cancel the connection
               cancelConnect();
               }
            else
               {
               // we're not connected yet, so connect
               connect();
               }
            }
         return null;
         }
      }
   }
