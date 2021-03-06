package edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard;

import java.awt.Font;
import java.util.PropertyResourceBundle;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ConnectDisconnectButton extends JButton
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ConnectDisconnectButton.class.getName());

   private Runnable setConnectedRunnable;
   private Runnable setDisconnectedRunnable;

   public ConnectDisconnectButton()
      {
      this(RESOURCES.getString("button.label.connect"),
           RESOURCES.getString("button.label.disconnect"),
           GUIConstants.BUTTON_FONT);
      }

   public ConnectDisconnectButton(final String connectLabelText, final String disconnectLabelText)
      {
      this(connectLabelText, disconnectLabelText, GUIConstants.BUTTON_FONT);
      }

   public ConnectDisconnectButton(final String connectLabelText, final String disconnectLabelText, final Font font)
      {
      super(connectLabelText);
      setConnectedRunnable = new ConnectionStateChangeRunnable(disconnectLabelText);
      setDisconnectedRunnable = new ConnectionStateChangeRunnable(connectLabelText);
      setFont(font);
      }

   public void setConnectionState(final boolean isConnectedToPeer)
      {
      runInGUIThread(isConnectedToPeer ? setConnectedRunnable : setDisconnectedRunnable);
      }

   // ensures that the given Runnable is run in the GUI thread
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

   private final class ConnectionStateChangeRunnable implements Runnable
      {
      private final String label;

      private ConnectionStateChangeRunnable(final String label)
         {
         this.label = label;
         }

      public void run()
         {
         ConnectDisconnectButton.this.setText(label);
         }
      }
   }
