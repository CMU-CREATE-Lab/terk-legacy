package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.MotorControlPanel;

public class MotorCell extends DefaultCell
   {

   public static final String MOTOR_NAME = "Motor";
   public static final String MOTOR_VALUE_KEY = "velocity";

   private int _velocity = 0;

   public MotorCell()
      {
      super();
      }

   public MotorCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return MOTOR_NAME;
      }

   public String getValueKey()
      {
      return MOTOR_VALUE_KEY;
      }

   public static Color getColor()
      {
      return Color.red;
      }

   public int getVelocity()
      {
      return _velocity;
      }

   public void setVelocity(int velocity)
      {
      this._velocity = velocity;
      }

   public Hashtable getValues()
      {
      Hashtable values = new Hashtable();
      values.put(MOTOR_VALUE_KEY, _velocity);
      return values;
      }

   public void setValues(Hashtable values)
      {
      setVelocity((Integer)values.get(MOTOR_VALUE_KEY));
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      final AbstractControlPanel panel = new MotorControlPanel(c.getMotorService(), deviceId, this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));

      return panel;
      }
   }
