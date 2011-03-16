package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote;

import java.util.Set;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphModel;

/**
 * Class listens to InternalFrame events and responds accordingly.
 * Main purpose is to delete the cell associated with the panel from the JGraph Model.
 * @author Jago Macleod
 *
 */
public final class QwerkInternalFrameListener implements InternalFrameListener
   {
   private DefaultCell cell;
   private JInternalFrame frame;
   private GraphModel model;

   /**
    *
    * @param fr - the JInternalFrame to listen to
    * @param dc - the cell associated with that cell
    * @param dgm - the graph model to remove the cell from
    */
   public QwerkInternalFrameListener(final JInternalFrame fr, final DefaultCell dc, final GraphModel dgm)
      {
      frame = fr;
      cell = dc;
      model = dgm;
      }

   public void internalFrameOpened(final InternalFrameEvent arg0)
      {
      ((JLayeredPane)frame.getParent()).moveToFront(frame);
      }

   public void internalFrameClosing(final InternalFrameEvent arg0)
      {

      }

   /**
    * Method called on close of the JInternalFrame; removes the cell associated with the JInternalFrame.
    */
   public void internalFrameClosed(final InternalFrameEvent arg0)
      {

      final Object[] removeList = new Object[2];
      final Set edgeList =
            ((DefaultPort)cell.
                  getChildAt(0)).getEdges();

      removeList[0] = cell;
      //Assume that each cell only has one edge.  If we remove the cell,
      //that edge will go as well.
      removeList[1] = (edgeList.toArray())[0];

      model.remove(removeList);
      }

   public void internalFrameIconified(final InternalFrameEvent arg0)
      {
      // TODO Auto-generated method stub

      }

   public void internalFrameDeiconified(final InternalFrameEvent arg0)
      {
      // TODO Auto-generated method stub

      }

   /**
    * Bring frame to front of layered pane:
    */
   public void internalFrameActivated(final InternalFrameEvent arg0)
      {
      ((JLayeredPane)frame.getParent()).moveToFront(frame);
      }

   public void internalFrameDeactivated(final InternalFrameEvent arg0)
      {
      //

      }
   }
