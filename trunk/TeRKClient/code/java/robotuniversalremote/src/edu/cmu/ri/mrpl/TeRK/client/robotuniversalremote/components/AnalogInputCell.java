package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AnalogInputControlPanel;

public class AnalogInputCell extends DefaultCell
   {

   public static final String ANALOG_INPUT_NAME = "Analog Input";

   public AnalogInputCell()
      {
      super();
      }

   public AnalogInputCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return ANALOG_INPUT_NAME;
      }

   public static Color getColor()
      {
      return Color.blue;
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      final AbstractControlPanel panel = new AnalogInputControlPanel(c.getAnalogInputsService(), deviceId, this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
