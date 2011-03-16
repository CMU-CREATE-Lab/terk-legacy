package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.DigitalInControlPanel;
import org.apache.log4j.Logger;

public class DigitalInCell extends DefaultCell
   {

   public static final String DIGITAL_IO_NAME = "Digital In";
   public static final String DIGITAL_IO_VALUE_KEY = "timer";
   public static final String DIGITAL_IO_AUTO_REFRESH_KEY = "autoRefresh";
   private static final Logger LOG = Logger.getLogger(DigitalInCell.class);

   private int _timer = 5;
   private boolean _autoRefresh = false;

   public DigitalInCell()
      {
      super();
      }

   public DigitalInCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return DIGITAL_IO_NAME;
      }

   public String getValueKey()
      {
      return DIGITAL_IO_VALUE_KEY;
      }

   public static Color getColor()
      {
      return Color.magenta;
      }

   public int getTimer()
      {
      return _timer;
      }

   public void setTimer(int value)
      {
      this._timer = value;
      }

   public boolean getAutoRefresh()
      {
      return _autoRefresh;
      }

   public void setAutoRefresh(boolean value)
      {
      this._autoRefresh = value;
      }

   public Hashtable getValues()
      {
      Hashtable values = new Hashtable();
      LOG.debug("timer is " + _timer);
      values.put(DIGITAL_IO_VALUE_KEY, _timer);
      values.put(DIGITAL_IO_AUTO_REFRESH_KEY, _autoRefresh);
      return values;
      }

   public void setValues(Hashtable values)
      {
      setTimer((Integer)values.get(DIGITAL_IO_VALUE_KEY));
      setAutoRefresh((Boolean)values.get(DIGITAL_IO_AUTO_REFRESH_KEY));
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      final AbstractControlPanel panel = new DigitalInControlPanel(c.getDigitalInService(), deviceId, this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
