package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote;

import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.QwerkCell;
import org.jgraph.JGraph;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphTransferHandler;
import org.jgraph.graph.GraphTransferable;
import org.jgraph.graph.ParentMap;

/**
 * This class handles drag-and-drop transfers between the Toolbox and the Canvas.
 * Some of the methods in the JGraph standard GraphTransferHandler are overridden here
 * to provide for the special behavior that our drag-and-drop application requires.
 */
public class ToolboxTransferHandler extends GraphTransferHandler
   {

   static final long serialVersionUID = -2;

   /**
    * This method overrides the GraphTransferHandler method to ensure
    * that items are not removed from the toolbox when they are moved.
    * @param comp The source of the drag.
    * @param data The transferred object.
    * @param action The action that was taken.
    */
   protected void exportDone(final JComponent comp, final Transferable data, final int action)
      {
      if (comp instanceof JGraph && data instanceof GraphTransferable)
         {
         final JGraph graph = (JGraph)comp;
         graph.getUI().updateHandle();
         graph.getUI().setInsertionLocation(null);
         graph.clearSelection();
         }
      }

   /**
    * This method overrides the GraphTransferHandler method to handle drops
    * from the toolbox.
    * The objects that are being dragged/dropped from the toolbox need to have a few
    * additional things done to them:
    * 1. Become moveable, sizeable, and editable.
    * 2. Have a connection to a Qwerk board port set.
    * @param graph The graph that the object is being inserted into.
    * @param cells The cells that are being cloned.
    * @param nested A nested map to put changes into.
    * @param cs A connection set associated with the cloned objects.
    * @param pm A parent map associated with the cloned objects.
    * @param dx The x location that the cells were dropped at.
    * @param dy The y location that the cells were dropped at.
    */
   protected void handleExternalDrop(final JGraph graph, final Object[] cells, final Map nested,
                                     final ConnectionSet cs, final ParentMap pm, final double dx, final double dy)
      {

      for (Object cell : cells)
         {
         if (cell instanceof DefaultCell)
            {
            // This is the kind of cell you can drag and drop onto the canvas

            // Get the place where this cell was dropped
            final Point insertionLocation = graph.getUI().getInsertionLocation();

            // Get the kind of port that the user dropped it on
            // Assume that the first item in the graph is the Qwerk board cell
            final QwerkCell qwerkCell = (QwerkCell)graph.getModel().getRootAt(0);
            final DefaultGraphCell qwerkPort = qwerkCell.cellAtPointWithType(insertionLocation, cell.getClass());

            if (qwerkPort != null)
               {
               // Simulate a click on the board here to activate the 'new part' method in RUR
               final MouseEvent ev = new MouseEvent(graph,
                                                    MouseEvent.MOUSE_CLICKED, // id
                                                    0, // when
                                                    0, // modifiers
                                                    (int)insertionLocation.getX(),
                                                    (int)insertionLocation.getY(),
                                                    1, // click count
                                                    false); // popup trigger

               graph.dispatchEvent(ev);
               }
            else
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Please drop the new part onto a connection of the appropriate type on the Qwerk controller board.",
                     "Invalid Part Location",
                     JOptionPane.ERROR_MESSAGE);
               }
            }
         }
      }
   }
