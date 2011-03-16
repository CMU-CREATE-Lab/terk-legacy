package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.CameraControlPanel;

public class CameraCell extends DefaultCell
   {
   public static final String VIDEO_NAME = "Camera";

   public CameraCell()
      {
      super();
      }

   public CameraCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return VIDEO_NAME;
      }

   public String getValueKey()
      {
      return "";
      }

   public static Color getColor()
      {
      return Color.BLACK;
      }

   public Hashtable getValues()
      {
      return new Hashtable();
      }

   public void setValues(final Hashtable values)
      {
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      final AbstractControlPanel panel = new CameraControlPanel(c.getVideoStreamService(), this, guiClientHelper.getVideoStreamViewportComponent());
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }