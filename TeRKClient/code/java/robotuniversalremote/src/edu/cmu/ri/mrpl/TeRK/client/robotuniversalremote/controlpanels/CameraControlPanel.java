package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.Component;
import javax.swing.SpringLayout;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import edu.cmu.ri.createlab.TeRK.video.VideoStreamService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.CameraCell;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import org.apache.log4j.Logger;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */

/**
 * Class provides a UI for controlling digital output ports.
 *
 */
public final class CameraControlPanel extends AbstractControlPanel
   {
   private static final Logger LOG = Logger.getLogger(CameraControlPanel.class);
   private static final String TITLE = "Camera";

   static final long serialVersionUID = 0;

   private final VideoStreamService service;

   private final CameraCell graphCell;

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    */
   public CameraControlPanel(final VideoStreamService service, final CameraCell cell, final Component videoStreamViewportComponent)
      {
      super();

      this.service = service;
      graphCell = cell;

      this.setLayout(new SpringLayout());

      // create a JPanel to hold the buttons, text fields, and color panel
      add(videoStreamViewportComponent);
      SpringLayoutUtilities.makeCompactGrid(this,
                                            1, 1, // rows, cols
                                            0, 0, // initX, initY
                                            0, 0);// xPad, yPad

      addAncestorListener(
            new AncestorListener()
            {
            public void ancestorAdded(final AncestorEvent event)
               {
               LOG.info("STARTING VIDEO STREAM");
               service.startVideoStream();
               }

            public void ancestorRemoved(final AncestorEvent event)
               {
               LOG.info("STOPPING VIDEO STREAM");
               service.stopVideoStream();
               }

            public void ancestorMoved(final AncestorEvent event)
               {
               // do nothing, we don't care about this
               }
            });
      this.setAutoscrolls(true);
      }

   public CameraCell getGraphCell()
      {
      return graphCell;
      }

   public String getTitle()
      {
      return TITLE;
      }
   }