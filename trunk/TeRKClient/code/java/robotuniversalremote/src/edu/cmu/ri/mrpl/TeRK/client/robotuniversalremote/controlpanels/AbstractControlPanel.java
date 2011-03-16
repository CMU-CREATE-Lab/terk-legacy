package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import javax.swing.JPanel;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;

/**
 * Class serves as an abstract class for all ControlPanels that can be dragged around.
 */
public abstract class AbstractControlPanel extends JPanel
   {

   protected DefaultCell graphCell;

   /**
    * Must be implemented in subclasses to return their specific Cell type:
    * @return
    */
   public abstract DefaultCell getGraphCell();

   /**
    * This method is called if an emergency stop has been asked for by the user.
    * Control panels can use this to update themselves to a new state (e.g., reset sliders).
    *
    */
   public void emergencyStopIssued()
      {
      // Default implementation does nothing
      }

   public abstract String getTitle();
   }
