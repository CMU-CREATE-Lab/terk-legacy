package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.DigitalOutControlPanel;

public class DigitalOutCell extends DefaultCell
   {
   public static final String DIGITAL_OUT_NAME = "Digital Out";
   public static final String DIGITAL_OUT_VALUE_KEY = "isOn";

   private boolean isOn = false;

   public DigitalOutCell()
      {
      super();
      }

   public DigitalOutCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return DIGITAL_OUT_NAME;
      }

   public String getValueKey()
      {
      return DIGITAL_OUT_VALUE_KEY;
      }

   public static Color getColor()
      {
      return Color.orange;
      }

   public boolean getIsOn()
      {
      return isOn;
      }

   public void setIsOn(final boolean isOn)
      {
      this.isOn = isOn;
      }

   public Hashtable getValues()
      {
      final Hashtable values = new Hashtable();
      values.put(DIGITAL_OUT_VALUE_KEY, Boolean.valueOf(isOn));
      return values;
      }

   public void setValues(final Hashtable values)
      {
      setIsOn((Boolean)values.get(DIGITAL_OUT_VALUE_KEY));
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      final AbstractControlPanel panel = new DigitalOutControlPanel(c.getDigitalOutService(), deviceId, this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
