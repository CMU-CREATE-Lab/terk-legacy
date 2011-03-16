package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;

public final class QwerkFocusListener implements FocusListener
   {

   public void focusGained(final FocusEvent arg0)
      {
      final JInternalFrame frame = (JInternalFrame)arg0.getComponent();
      ((JLayeredPane)frame.getParent()).moveToFront(frame);
      }

   public void focusLost(final FocusEvent arg0)
      {
      // TODO Auto-generated method stub

      }
   }
