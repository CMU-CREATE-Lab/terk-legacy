package edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.border.Border;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface VideoStreamViewport extends VideoStreamEventListener
   {
   void setBorder(Border border);

   void setPreferredSize(Dimension preferredSize);

   void setMinimumSize(final Dimension minimumSize);

   void setMaximumSize(Dimension maximumSize);

   Component getComponent();
   }