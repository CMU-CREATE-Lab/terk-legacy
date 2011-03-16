package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

import java.util.Hashtable;
import javax.swing.tree.MutableTreeNode;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;

public class QwerkBoardPortCell extends DefaultGraphCell
   {

   protected int deviceId = 0;
   protected Hashtable values = new Hashtable();

   public QwerkBoardPortCell()
      {
      super();
      }

   public QwerkBoardPortCell(final Object userObject)
      {
      super(userObject);
      }

   public QwerkBoardPortCell(final Object userObject, final AttributeMap storageMap)
      {
      super(userObject, storageMap);
      }

   public QwerkBoardPortCell(final Object userObject, final AttributeMap storageMap,
                             final MutableTreeNode[] children)
      {
      super(userObject, storageMap, children);
      }

   /**
    * Set the device ID that this port corresponds to.
    * @param newDeviceId
    */
   public void setDeviceId(final int newDeviceId)
      {
      deviceId = newDeviceId;
      }

   /**
    * Get the device ID that this port corresponds to.
    * @return
    */
   public int getDeviceId()
      {
      return deviceId;
      }

   /**
    * Set the values that this port corresponds to.
    * @param values
    */
   public void setValues(final Hashtable values)
      {
      this.values = values;
      }

   /**
    * Get the values that this port corresponds to.
    * @return
    */
   public Hashtable getValues()
      {
      return this.values;
      }
   }
