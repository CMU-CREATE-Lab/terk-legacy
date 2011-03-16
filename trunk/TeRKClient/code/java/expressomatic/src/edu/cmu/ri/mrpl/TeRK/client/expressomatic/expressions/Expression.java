package edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.QwerkCell;
import org.jgraph.JGraph;

public class Expression
   {
   private JGraph mCanvasGraph;
   private QwerkCell mQwerkBoardCell;

   private String name = "Expression";

   public Expression()
      {
      mCanvasGraph = new JGraph();
      setupNewCanvasGraph(new Point2D.Double(0, 0));
      }

   public void setName(String newName)
      {
      name = (newName == null) ? name : newName;
      }

   public String getName()
      {
      return name;
      }

   public void addComponentCell(DefaultCell cell)
      {
      mCanvasGraph.getGraphLayoutCache().insert(cell);
      }

   public ArrayList<DefaultCell> getComponentCells()
      {
      Object[] objects = mCanvasGraph.getGraphLayoutCache().getCells(true, true, true, true);
      ArrayList<DefaultCell> cells = new ArrayList<DefaultCell>();

      for (int i = 0; i < objects.length; i++)
         {
         if (objects[i] instanceof DefaultCell)
            {
            cells.add((DefaultCell)objects[i]);
            }
         }

      return cells;
      }

   /**
    * Sets up a JGraph to a default configuration.
    * Useful for 'New' operations.
    */
   private void setupNewCanvasGraph(final Point2D qwerkPosition)
      {
      mQwerkBoardCell = new QwerkCell(qwerkPosition);
      mCanvasGraph.getGraphLayoutCache().insertGroup(mQwerkBoardCell, mQwerkBoardCell.getChildren().toArray());
      }
   }
