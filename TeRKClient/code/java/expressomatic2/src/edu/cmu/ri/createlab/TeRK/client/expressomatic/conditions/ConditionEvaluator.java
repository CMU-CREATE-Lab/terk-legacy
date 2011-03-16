package edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions;

import java.util.ArrayList;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional.LOGICAL_OPERATOR;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

public class ConditionEvaluator
   {

   public ConditionEvaluator()
      {
      }

   /**
    * Evaluates all conditionls together using the AND operator
    * @param serviceManager
    * @param condition
    * @return
    */
   public boolean evaluate(ServiceManager serviceManager, Condition condition)
      {
      ArrayList<AbstractConditional> conditionals = condition.getConditionals();
      Object[] values = new Object[conditionals.size()];

      boolean result = true;

      // Start evaluating conditionals based on the first conditional's Logical Operator
      if (conditionals.size() > 0)
         {
         result = conditionals.get(0).getLogicalOperator() == LOGICAL_OPERATOR.AND;
         }

      AbstractConditional c;
      for (int i = 0; i < conditionals.size(); i++)
         {
         c = conditionals.get(i);

         if (c.getLogicalOperator() == LOGICAL_OPERATOR.AND)
            {
            result = result && c.evaluate(serviceManager);
            }
         else if (c.getLogicalOperator() == LOGICAL_OPERATOR.OR)
            {
            result = result || c.evaluate(serviceManager);
            }

         values[i] = c.getLastCheckedValue();
         }

      conditionValuesChecked(condition, values);

      return result;
      }

   /**
    * This method gets called when the value of the current condition is checked
    * @param condition The condition being checked
    * @param values The values of the conditionals in the condition. The values
    * in the array are in the same order as their associated conditionals.
    */
   public void conditionValuesChecked(Condition condition, Object[] values)
      {
      }
   }
