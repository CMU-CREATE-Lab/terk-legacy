package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.awt.Color;
import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelper;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels.AbstractControlPanel;
import org.jgraph.graph.DefaultGraphCell;

/**
 * This class contains information generic/common to any component that
 * can plug into the TeRK board.  These components include motors, servos,
 * analog and digital inputs.
 */
public class DefaultCell extends DefaultGraphCell
   {

   protected int deviceId;
   protected String description;
   protected AdvancedFlag advanced;

   public DefaultCell()
      {
      super();
      advanced = new AdvancedFlag();
      deviceId = -1;
      }

   public DefaultCell(final Object o)
      {
      super(o);
      advanced = new AdvancedFlag();
      // Catch the description of the user object supplied
      if (o != null)
         {
         this.description = o.toString();
         }

      deviceId = -1;
      }

   public void setAdvancedHook(AdvancedFlag adv)
      {
      advanced = adv;
      }

   /**
    * All classes that derive from DefaultCell should implement this method.
    * A hash table of all the values that a cell saves should be returned.
    * For example a MotorCell would have a key of "velocity" associated with
    * a value.
    * Note if a cell does not have any values the method does not need to be
    * overridden.
    * @return The hashtable of all values associated with a cell.
    */
   public Hashtable getValues()
      {
      return null;
      }

   public void setValues(Hashtable values)
      {
      }

   /**
    * Factory method to create a control panel which is used for this kind of cell.
    * @param guiClientHelper
    * @param c The QwerkController to the Qwerk board.
    * @param deviceId The device ID that this cell is connected to. @return The control panel.
    */
   public AbstractControlPanel createControlPanel(final GUIClientHelper guiClientHelper, final QwerkController c, final int deviceId)
      {
      return null;
      }

   /**
    * Each subclass should return their appropriate color here.
    * @return The default color
    */
   public static Color getColor()
      {
      return Color.BLACK;
      }

   /**
    * Each subclass should return an appropriate default name here.
    * @return
    */
   public static String getDefaultName()
      {
      return "No Name";
      }

   /**
    * Each subclass should return an appropriate default value key here.
    * @return
    */
   public String getValueKey()
      {
      return "No Name";
      }

   /**
    * Each component has a device ID (which port it is connected to on the Qwerk board).
    * This method returns the device ID for this component.
    * @return The device ID for this component.
    */
   public int getDeviceId()
      {
      return deviceId;
      }

   /**
    * Set the device ID for this component.
    * @param newDeviceId The new device ID.
    */
   public void setDeviceId(final int newDeviceId)
      {
      deviceId = newDeviceId;
      }

   /**
    * Ensure that our description matches our user object.
    */
   public void setUserObject(final Object obj)
      {
      if (obj != null)
         {
         this.description = obj.toString();
         }
      }

   /**
    * Return the description of this object.
    */
   public String toString()
      {
      return description;
      }

   /**
    * For deserialization, provide setter for description.
    * @param desc The new description.
    */
   public void setDescription(final String desc)
      {
      description = desc;
      }

   /**
    * For serialization, provide getter for description.
    * @return The description.
    */
   public String getDescription()
      {
      return description;
      }
   }
