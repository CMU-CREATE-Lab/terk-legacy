package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.ServoControlPanel;

public class ServoCell extends DefaultCell
   {

   public static final String SERVO_NAME = "Servo";
   public static final String SERVO_VALUE_KEY = "position";

   private int _position = 127;

   public ServoCell()
      {
      super();
      }

   public ServoCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return SERVO_NAME;
      }

   public String getValueKey()
      {
      return SERVO_VALUE_KEY;
      }

   public static Color getColor()
      {
      return Color.green;
      }

   public int getPosition()
      {
      return _position;
      }

   public void setPosition(int position)
      {
      this._position = position;
      }

   public Hashtable getValues()
      {
      Hashtable values = new Hashtable();
      values.put(SERVO_VALUE_KEY, _position);
      return values;
      }

   public void setValues(Hashtable values)
      {
      setPosition((Integer)values.get(SERVO_VALUE_KEY));
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {

      final AbstractControlPanel panel = new ServoControlPanel(c.getServoService(), deviceId, this, advanced.getValue());
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
