package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import javax.swing.BorderFactory;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.LEDControlPanel;
import org.apache.log4j.Logger;

public class LEDCell extends DefaultCell
   {
   private static final Logger LOG = Logger.getLogger(LEDCell.class);

   public static final String LED_NAME = "LEDs";
   public static final String LED_VALUE_KEY = "LED";

   public enum LEDs
      {
         LED0, LED1, LED2, LED3, LED4, LED5, LED6, LED7, LED8, LED9
      }

   public enum LED_STATE
      {
         INDEX_OFF, INDEX_BLINK, INDEX_ON
      }

   protected LED_STATE[] _ledState;

   public LEDCell()
      {
      super();
      try
         {
         _ledState = new LED_STATE[LEDs.values().length];
         for (int i = 0; i < _ledState.length; i++)
            {
            _ledState[i] = LED_STATE.INDEX_OFF;
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while initializing _ledState!", e);
         }
      }

   public LEDCell(final Object o)
      {
      super(o);
      }

   public static String getDefaultName()
      {
      return LED_NAME;
      }

   public static Color getColor()
      {
      return Color.cyan;
      }

   public void setLedState(final LED_STATE value, final int ledIndex)
      {
      _ledState[ledIndex] = value;
      }

   public LED_STATE getLedState(final int ledIndex)
      {
      return _ledState[ledIndex];
      }

   @SuppressWarnings({"UseOfObsoleteCollectionType"})
   public Hashtable getValues()
      {
      final Hashtable<String, Integer> values = new Hashtable<String, Integer>();
      for (int i = 0; i < _ledState.length; i++)
         {
         values.put(LED_VALUE_KEY + i, _ledState[i].ordinal());
         }
      return values;
      }

   @SuppressWarnings({"UseOfObsoleteCollectionType"})
   public void setValues(final Hashtable values)
      {
      for (int i = 0; i < _ledState.length; i++)
         {
         _ledState[i] = lookupState((Integer)values.get(LED_VALUE_KEY + i));
         }
      }

   private LED_STATE lookupState(final Integer i)
      {
      if (i == null)
         {
         return LED_STATE.INDEX_OFF;
         }

      switch (i)
         {
         case 0:
            return LED_STATE.INDEX_OFF;
         case 1:
            return LED_STATE.INDEX_BLINK;
         case 2:
            return LED_STATE.INDEX_ON;
         default:
            LOG.error("Unknown LED_STATE [" + i + "], returning INDEX_OFF");
            return LED_STATE.INDEX_OFF;
         }
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The connection to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      // XmlDevice ID unneeded here
      final AbstractControlPanel panel = new LEDControlPanel(c.getLEDService(), this);
      panel.setBorder(BorderFactory.createLineBorder(getColor(), 2));
      return panel;
      }
   }
