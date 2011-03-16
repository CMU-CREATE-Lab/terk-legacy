package edu.cmu.ri.mrpl.TeRK.client.expressomatic.swing.dnd;

import java.awt.datatransfer.DataFlavor;
import javax.swing.TransferHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence.SequenceStep;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonFile;

public class SequenceTransferHandler extends TransferHandler
   {
   protected static DataFlavor expressionFlavor = null;
   protected static DataFlavor conditionFlavor = null;
   protected static DataFlavor stepFlavor = null;
   public static DataFlavor roboticonFlavor = null;
   public static DataFlavor roboticonImportFlavor = null;
   protected static String expressionType = DataFlavor.javaJVMLocalObjectMimeType +
                                            ";class=" + Expression.class.getName();
   protected static String conditionType = DataFlavor.javaJVMLocalObjectMimeType +
                                           ";class=" + Condition.class.getName();
   protected static String stepType = DataFlavor.javaJVMLocalObjectMimeType +
                                      ";class=" + SequenceStep.class.getName();
   protected static String roboticonType = DataFlavor.javaJVMLocalObjectMimeType +
                                           ";class=" + RoboticonFile.class.getName();

   protected SequenceTransferHandler()
      {
      try
         {
         expressionFlavor = new DataFlavor(expressionType);
         }
      catch (ClassNotFoundException e)
         {
         System.out.println(
               "ERROR " + this.getClass().getName() + ": unable to create expression data flavor");
         }
      try
         {
         conditionFlavor = new DataFlavor(conditionType);
         }
      catch (ClassNotFoundException e)
         {
         System.out.println(
               "ERROR " + this.getClass().getName() + ": unable to create condition data flavor");
         }
      try
         {
         stepFlavor = new DataFlavor(stepType);
         }
      catch (ClassNotFoundException e)
         {
         System.out.println(
               "ERROR " + this.getClass().getName() + ": unable to create step data flavor");
         }
      try
         {
         roboticonFlavor = new DataFlavor(roboticonType);
         roboticonImportFlavor = new DataFlavor(roboticonType);
         }
      catch (ClassNotFoundException e)
         {
         System.out.println(
               "ERROR " + this.getClass().getName() + ": unable to create roboticon data flavor");
         }
      }

   protected boolean hasExpressionFlavor(DataFlavor[] flavors)
      {
      if (expressionFlavor == null)
         {
         return false;
         }

      for (int i = 0; i < flavors.length; i++)
         {
         if (flavors[i].equals(expressionFlavor))
            {
            return true;
            }
         }
      return false;
      }

   protected boolean hasConditionFlavor(DataFlavor[] flavors)
      {
      if (conditionFlavor == null)
         {
         return false;
         }

      for (int i = 0; i < flavors.length; i++)
         {
         if (flavors[i].equals(conditionFlavor))
            {
            return true;
            }
         }
      return false;
      }

   protected boolean hasStepFlavor(DataFlavor[] flavors)
      {
      if (stepFlavor == null)
         {
         return false;
         }

      for (int i = 0; i < flavors.length; i++)
         {
         if (flavors[i].equals(stepFlavor))
            {
            return true;
            }
         }
      return false;
      }

   protected boolean hasRoboticonFlavor(DataFlavor[] flavors)
      {
      if (roboticonFlavor == null)
         {
         return false;
         }

      for (int i = 0; i < flavors.length; i++)
         {
         if (flavors[i].equals(roboticonFlavor))
            {
            return true;
            }
         }
      return false;
      }

   protected boolean hasRoboticonImportFlavor(DataFlavor[] flavors)
      {
      if (roboticonImportFlavor == null)
         {
         return false;
         }

      for (int i = 0; i < flavors.length; i++)
         {
         if (flavors[i].equals(roboticonImportFlavor))
            {
            return true;
            }
         }
      return false;
      }
   }
