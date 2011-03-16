package edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals;

import java.io.Serializable;
import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;

public abstract class AbstractConditional implements Serializable
   {

   public enum COMPARE_OPERATOR
      {
         EQUAL_TO, GREATER_THAN, LESS_THAN, GREATER_THAN_EQUALS, LESS_THAN_EQUALS, NOT_EQUAL
      }

   ;

   public enum UNARY_OPERATOR
      {
         NONE, NOT
      }

   ;

   public enum LOGICAL_OPERATOR
      {
         AND, OR
      }

   ;

   protected String description;
   protected LOGICAL_OPERATOR logicalOperator = LOGICAL_OPERATOR.AND;

   /**
    * All classes that derive from DefaultConditional should implement this method.
    * A hash table of all the values that a conditional saves should be returned.
    * For example a AnalogInputConditional would have a key of "operator" associated with
    * a value.
    * Note if a conditional does not have any values the method does not need to be
    * overridden.
    * @return The hashtable of all values associated with a conditional.
    */
   public Hashtable getValues()
      {
      return null;
      }

   public void setValues(Hashtable values)
      {
      }

   public Object getLastCheckedValue()
      {
      return null;
      }

   /**
    * Each subclass should return an appropriate default name here.
    * @return
    */
   public String getDefaultName()
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

   public LOGICAL_OPERATOR getLogicalOperator()
      {
      return logicalOperator;
      }

   public void setLogicalOperator(LOGICAL_OPERATOR lo)
      {
      logicalOperator = lo;
      }

   public abstract boolean evaluate(QwerkController qwerkController);

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
