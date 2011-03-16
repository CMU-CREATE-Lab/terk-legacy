package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.connectionstate;

import java.awt.Dimension;
import java.awt.Font;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.peer.ConnectionEventListener;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ConnectionStatePanel extends JPanel implements ConnectionEventListener
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(ConnectionStatePanel.class.getName());

   /** Label text */
   private static final String LABEL_TEXT_NO = RESOURCES.getString("label.no");
   private static final String LABEL_TEXT_YES = RESOURCES.getString("label.yes");

   /** Font stuff */
   private static final String FONT_NAME = "Verdana";
   private static final Font FONT_TINY = new Font(FONT_NAME, 0, 9);

   /** Labels which display the current relay login state */
   private final JLabel relayLoginStateLabel = createLabel(RESOURCES.getString("label.logged.in.to.relay"));
   private final JLabel relayLoginStateValueLabel = createLabel(LABEL_TEXT_NO);

   /** Labels which display the current peer connection state */
   private final JLabel peerConnectionStateLabel = createLabel(RESOURCES.getString("label.connected.to.peer"));
   private final JLabel peerConnectionStateValueLabel = createLabel(LABEL_TEXT_NO);

   private final SetLabelTextRunnable relayLoggedOut = new SetLabelTextRunnable(relayLoginStateValueLabel, LABEL_TEXT_NO);
   private final SetLabelTextRunnable relayLoggedIn = new SetLabelTextRunnable(relayLoginStateValueLabel, LABEL_TEXT_YES);
   private final SetLabelTextRunnable peerDisconnected = new SetLabelTextRunnable(peerConnectionStateValueLabel, LABEL_TEXT_NO);

   public ConnectionStatePanel()
      {
      super(new SpringLayout());
      add(relayLoginStateLabel);
      add(relayLoginStateValueLabel);
      add(Box.createRigidArea(new Dimension(5, 5)));
      add(Box.createRigidArea(new Dimension(100, 5)));
      add(peerConnectionStateLabel);
      add(peerConnectionStateValueLabel);
      SpringLayoutUtilities.makeCompactGrid(this,
                                            3, 2, // rows, cols
                                            0, 0, // initX, initY
                                            5, 0);// xPad, yPad
      }

   private JLabel createLabel(final String labelText)
      {
      final JLabel label = new JLabel(labelText);
      label.setFont(FONT_TINY);
      return label;
      }

   public void handleRelayLoginEvent()
      {
      SwingUtilities.invokeLater(relayLoggedIn);
      }

   public void handleFailedRelayLoginEvent()
      {
      // do nothing
      }

   public void handleRelayLogoutEvent()
      {
      SwingUtilities.invokeLater(relayLoggedOut);
      SwingUtilities.invokeLater(peerDisconnected);
      }

   public void handleRelayRegistrationEvent()
      {
      // do nothing
      }

   public void handleForcedLogoutNotificationEvent()
      {
      handleRelayLogoutEvent();
      }

   public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
      {
      SwingUtilities.invokeLater(new SetLabelTextRunnable(peerConnectionStateValueLabel, LABEL_TEXT_YES + " (" + peerUserId + ")"));
      }

   public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
      {
      SwingUtilities.invokeLater(new SetLabelTextRunnable(peerConnectionStateValueLabel, LABEL_TEXT_YES + " (" + peerUserId + ")"));
      }

   public void handlePeerDisconnectedEvent(final String peerUserId)
      {
      SwingUtilities.invokeLater(peerDisconnected);
      }

   public void handlePeerConnectionFailedEvent(final String peerUserId)
      {
      // do nothing
      }

   public void setRelayLoginStateLabelText(final String text)
      {
      relayLoginStateLabel.setText(text);
      }

   public void setPeerConnectionStateLabelText(final String text)
      {
      peerConnectionStateLabel.setText(text);
      }

   private class SetLabelTextRunnable implements Runnable
      {
      private final JLabel label;
      private final String labelText;

      private SetLabelTextRunnable(final JLabel label, final String labelText)
         {
         this.label = label;
         this.labelText = labelText;
         }

      public void run()
         {
         label.setText(labelText);
         }
      }
   }
