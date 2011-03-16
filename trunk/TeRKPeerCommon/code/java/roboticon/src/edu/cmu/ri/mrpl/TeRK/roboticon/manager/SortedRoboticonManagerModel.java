package edu.cmu.ri.mrpl.TeRK.roboticon.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import edu.cmu.ri.mrpl.TeRK.roboticon.manager.RoboticonDirectory.RoboticonType;

/**
 * Decorates a RoboticonManagerModel with the ability to sort
 * the contents of the model.
 */
public class SortedRoboticonManagerModel extends AbstractListModel
   {
   private static final long serialVersionUID = -4591212952314924923L;

   private RoboticonManagerModel unsortedModel = null;
   private List<RoboticonFile> sortedModel = new ArrayList<RoboticonFile>(0);
   private SortOrder sortOrder = SortOrder.TYPE;

   /**
    * Constructor
    * If initial SortOrder not specified, defaults to order by Type
    * @param model {@link RoboticonManagerModel} to be decorated
    * @param sortOrder initial ordering of model contents
    */
   public SortedRoboticonManagerModel
   (RoboticonManagerModel model, SortOrder sortOrder)
      {
      unsortedModel = model;
      unsortedModel.addListDataListener(new ListDataListener()
      {
      public void intervalAdded(ListDataEvent e)
         {
         //Event not fired by underlying model
         //No event handler defined
         }

      public void intervalRemoved(ListDataEvent e)
         {
         //Event not fired by underlying model
         //No event handler defined
         }

      public void contentsChanged(ListDataEvent e)
         {
         unsortedContentsChanged(e);
         }
      });
      this.setSortOrder(sortOrder);
      if (unsortedModel != null)
         {   //ADD contents of unsorted model to sortedModel
         populateModel();
         Collections.sort(sortedModel, this.sortOrder.comparator());
         }
      } //constructor

   private void unsortedContentsChanged(ListDataEvent e)
      {

      if (unsortedModel.getSize() != sortedModel.size())
         {
         ///Rebuilds sorted model list if one or
         //more roboticons added or removed from model
         populateModel();
         }
      Collections.sort(sortedModel, this.sortOrder.comparator());
      fireContentsChanged(ListDataEvent.CONTENTS_CHANGED, 0, sortedModel.size() - 1);
      }

   /** Creates sorted model from contents of unsorted model */
   private void populateModel()
      {
      int size = unsortedModel.getSize();
      sortedModel = new ArrayList<RoboticonFile>(size);
      for (int i = 0; i < size; i++)
         {
         sortedModel.add((RoboticonFile)unsortedModel.getElementAt(i));
         }
      }

   public Object getElementAt(int arg0)
      {
      return sortedModel.get(arg0);
      }

   public int getSize()
      {
      return sortedModel.size();
      }

   public void setSortOrder(SortOrder order)
      {
      if (order != null)
         {
         sortOrder = order;
         Collections.sort(sortedModel, this.sortOrder.comparator());
         fireContentsChanged(ListDataEvent.CONTENTS_CHANGED, 0, sortedModel.size() - 1);
         }
      }

   public enum SortOrder
      {
         NAME("Name"),
         TYPE("Type"),
         DATE("Date Created"),
         OWNER("Owner");

      String prettyAlias;

      SortOrder(String alias)
         {
         prettyAlias = alias;
         }

      public String toString()
         {
         return prettyAlias;
         }

      /** Returns comparator to accomplish ordering */
      public Comparator<RoboticonFile> comparator()
         {
         if (this == NAME)
            {
            return new RoboticonFileNameComparator();
            }
         else if (this == TYPE)
            {
            return new RoboticonFileTypeComparator();
            }
         else if (this == DATE)
            {
            return new RoboticonFileDateComparator();
            }
         else
            {
            return new RoboticonFileSenderComparator();
            }
         }

      /**
       Returns SortOrder with name matching specified string
       @param strValue value from which to derive SortOrder
       */
      public static SortOrder fromString(String strValue)
         {
         SortOrder matching = null;
         if (strValue != null)
            {
            for (SortOrder order : SortOrder.values())
               {
               if (order.prettyAlias.equalsIgnoreCase(strValue))
                  {
                  matching = order;
                  }
               }
            }
         return matching;
         }
      } //SortOrder

   /** Sorts RoboticonFile objects by file name */
   public static class RoboticonFileNameComparator implements Comparator<RoboticonFile>
      {
      //Ascending order
      //null < value
      public int compare(RoboticonFile left, RoboticonFile right)
         {
         int result = 0;
         if (left != null && right != null)
            {
            String leftName = left.getName().toLowerCase();
            String rightName = right.getName().toLowerCase();
            result = leftName.compareTo(rightName);
            }
         else if (left == null && right != null)
            {
            result = -1;
            }
         else
            {
            result = 1;
            }
         return result;
         }
      }

   /** Sorts RoboticonFile objects by type (e.g. Sequence, Expression) then by file name */
   public static class RoboticonFileTypeComparator implements Comparator<RoboticonFile>
      {
      //Expressions < Sequences
      //Null < value
      public int compare(RoboticonFile left, RoboticonFile right)
         {
         int result = 0;
         if (left != null && right != null)
            {
            if (left.roboticonType == right.roboticonType)
               {
               result = new RoboticonFileNameComparator().compare(left, right);
               }
            else
               {
               result = 1;
               if (left.roboticonType == RoboticonType.EXPRESSION)
                  {
                  result = -1;
                  }
               }
            }
         else if (left == null && right != null)
            {
            result = -1;
            }
         else
            {
            result = 1;
            }
         return result;
         }
      }

   /** Sorts RoboticonFile objects by file date */
   public static class RoboticonFileDateComparator implements Comparator<RoboticonFile>
      {
      //Ascending order
      //null < value
      public int compare(RoboticonFile left, RoboticonFile right)
         {
         int result = 0;
         if (left != null && right != null)
            {
            if (left.timestamp == right.timestamp)
               {
               result = 0;
               }
            else if (left.timestamp < right.timestamp)
               {
               result = -1;
               }
            else
               {
               result = 1;
               }
            }
         else if (left == null && right != null)
            {
            result = -1;
            }
         else
            {
            result = 1;
            }
         return result;
         }
      }

   public static class RoboticonFileSenderComparator implements Comparator<RoboticonFile>
      {
      //Ascending order
      //null < value
      public int compare(RoboticonFile left, RoboticonFile right)
         {
         int result = 0;
         if (left != null && right != null)
            {
            String leftId = left.senderId.toLowerCase();
            String rightId = right.senderId.toLowerCase();
            result = leftId.compareTo(rightId);
            }
         else if (left == null && right != null)
            {
            result = -1;
            }
         else
            {
            result = 1;
            }
         return result;
         }
      }
   } //SortedRoboticonManagerModel
