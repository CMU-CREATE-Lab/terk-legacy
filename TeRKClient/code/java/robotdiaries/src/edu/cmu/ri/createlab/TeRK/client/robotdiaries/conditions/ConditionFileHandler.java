package edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.AbstractFileHandler;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.conditionals.AbstractConditional;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.conditionals.AbstractConditional.LOGICAL_OPERATOR;

public class ConditionFileHandler extends AbstractFileHandler<Condition>
   {
   private static final ConditionFileHandler instance = new ConditionFileHandler();

   private static final String CS_VERSION = "1.0";

   private static final String CLASS_KEY = "class";
   private static final String LOGICAL_OPERATOR_KEY = "logical_operator";
   private static final String CONDITIONALS_KEY = "conditions";

   private ConditionFileHandler()
      {
      }

   public static ConditionFileHandler getInstance()
      {
      return instance;
      }

   public Condition openFile(File filename)
      {
      Condition c = super.openFile(filename);
      String name = filename.getName();
      name = name.substring(0, name.length() - 4);
      c.setName(name);
      return c;
      }

   /**
    * Returns one object which contains enough information to reconstruct the current statement.
    * Used for serialization.
    */
   public Hashtable getRepresentation(Condition condition)
      {
      if (condition == null)
         {
         return new Hashtable();
         }

      final Hashtable storage = new Hashtable();

      final ArrayList<Hashtable> conditionalList = new ArrayList<Hashtable>();
      Hashtable conditionalHash;

      // Overall info
      storage.put("cs_version", CS_VERSION);

      // Get info about each component and put it in the component list
      final Object[] conditionals;

      conditionals = condition.getConditionals().toArray();

      for (Object c : conditionals)
         {
         if (c instanceof AbstractConditional)
            {
            final AbstractConditional conditional = (AbstractConditional)c;
            conditionalHash = new Hashtable();

            conditionalHash.put(CLASS_KEY, conditional.getClass());
            conditionalHash.put(LOGICAL_OPERATOR_KEY, conditional.getLogicalOperator().name());

            // For conditionals
            // Put all the values of the conditionals into the conditionHash hashtable.
            Hashtable values = conditional.getValues();
            if (values != null)
               {
               conditionalHash.putAll(values);
               }

            // Add this component to our list
            conditionalList.add(conditionalHash);
            }
         }

      // Add the component list to our storage
      storage.put(CONDITIONALS_KEY, conditionalList);

      return storage;
      }

   /**
    * Reconstructs the conditionalStatement
    * Used for de-serialization.
    */
   public Condition setRepresentation(final Hashtable hash)
      {
      if (hash == null || hash.isEmpty())
         {
         return null;
         }

      Condition condition = new Condition();

      // This object will be used for instanceof checks
      Object tempObject;

      // Get ConditionalHashes
      tempObject = hash.get(CONDITIONALS_KEY);
      if (tempObject == null || !(tempObject instanceof ArrayList))
         {
         return null;
         }
      final ArrayList conditionalList = (ArrayList)tempObject;

      for (Object o : conditionalList)
         {
         // Check to make sure o is a Hashtable
         if (!(o instanceof Hashtable))
            {
            continue;
            }
         final Hashtable conditionalHash = (Hashtable)o;

         // Get the Class of this conditional
         tempObject = conditionalHash.get(CLASS_KEY);
         if (tempObject == null || !(tempObject instanceof Class))
            {
            continue;
            }
         final Class conditionalType = (Class)tempObject;

         // Get the logical operator of this conditional
         tempObject = conditionalHash.get(LOGICAL_OPERATOR_KEY);
         final String logicalOperator = (tempObject != null && tempObject instanceof String)
                                        ? (String)tempObject
                                        : "";

         AbstractConditional newConditional;
         try
            {
            // Create a new cell class of the correct type
            newConditional = (AbstractConditional)conditionalType.newInstance();

            if (logicalOperator != null)
               {
               LOGICAL_OPERATOR lo = LOGICAL_OPERATOR.AND;
               try
                  {
                  lo = LOGICAL_OPERATOR.valueOf(logicalOperator);
                  }
               catch (Exception e)
                  {
                  }
               newConditional.setLogicalOperator(lo);
               }

            newConditional.setValues(conditionalHash);
            }
         catch (Exception ex)
            {
            System.out.println("Error " + this.getClass() + ": " + ex.getMessage());
            ex.printStackTrace();
            return null;
            }

         condition.insert(newConditional);
         }

      return condition;
      }
   }
