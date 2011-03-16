package edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence;

import java.util.ArrayList;
import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.AbstractFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.ConditionFileHandler;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionFileHandler;

public class SequenceFileHandler extends AbstractFileHandler<Sequence>
   {
   private static final SequenceFileHandler instance = new SequenceFileHandler();

   private static final String SEQ_VERSION = "1.0";
   private static final ExpressionFileHandler expressionFileHandler = ExpressionFileHandler.getInstance();
   private static final ConditionFileHandler conditionFileHandler = ConditionFileHandler.getInstance();

   private static final String EXPRESSION_KEY = "expression";
   private static final String EXPRESSION_NAME_KEY = "expression_name";
   private static final String TRANSITION_MILLIS_KEY = "transition_milliseconds";
   private static final String LOOP_KEY = "loop";
   private static final String TRANSITION_CONDITION_KEY = "transition_condition";
   private static final String CONDITION_NAME_KEY = "condition_name";
   private static final String EXPRESSIONSPEED_VELOCITY_KEY = "expression_speed_velocity";
   private static final String STEPS_KEY = "steps";

   private SequenceFileHandler()
      {
      }

   public static SequenceFileHandler getInstance()
      {
      return instance;
      }

   /**
    * Returns one object which contains enough information to reconstruct the current sequence.
    * Used for serialization.
    */
   public Hashtable getRepresentation(Sequence sequence)
      {
      if (sequence == null)
         {
         return null;
         }

      final Hashtable storage = new Hashtable();

      final ArrayList<Hashtable> stepList = new ArrayList<Hashtable>();
      Hashtable stepHash;

      // Overall info
      storage.put("seq_version", SEQ_VERSION);

      // Put whether this sequence loops
      storage.put(LOOP_KEY, sequence.getLoopBackToStart());

      for (SequenceStep step : sequence.getSteps())
         {
         stepHash = new Hashtable();

         // Store XmlExpression
         stepHash.put(EXPRESSION_KEY, expressionFileHandler.getRepresentation(step.getExpression()));
         stepHash.put(EXPRESSION_NAME_KEY, step.getExpression().getName());

         // Store Transition
         stepHash.put(TRANSITION_MILLIS_KEY, step.getTransition().getMillisToNextStep());

         // Store Condition
         if (step.getTransition().getCondition() != null)
            {
            stepHash.put(TRANSITION_CONDITION_KEY, conditionFileHandler.getRepresentation(step.getTransition().getCondition()));
            stepHash.put(CONDITION_NAME_KEY, step.getTransition().getCondition().getName());
            }

         // Store ExpressionSpeed
         stepHash.put(EXPRESSIONSPEED_VELOCITY_KEY, step.getExpresisonSpeed().getServoVelocity());

         // Add this step to our list
         stepList.add(stepHash);
         }

      // Add the component list to our storage
      storage.put(STEPS_KEY, stepList);

      return storage;
      }

   /**
    * Reconstructs the condition
    * Used for de-serialization.
    */
   public Sequence setRepresentation(final Hashtable hash)
      {
      if (hash == null)
         {
         return null;
         }

      // This object will be used for instanceof checks
      Object tempObject;

      // Get SequenceSteps
      tempObject = hash.get(STEPS_KEY);
      if (tempObject == null || !(tempObject instanceof ArrayList))
         {
         return null;
         }
      final ArrayList stepList = (ArrayList)tempObject;

      final Sequence sequence = new Sequence();

      // Set loop
      tempObject = hash.get(LOOP_KEY);
      sequence.setLoopBackToStart((tempObject == null || !(tempObject instanceof Boolean))
                                  ? false
                                  : (Boolean)tempObject);

      SequenceStep newStep;

      for (Object o : stepList)
         {

         // Check to make sure o is a Hashtable
         if (!(o instanceof Hashtable))
            {
            continue;
            }
         final Hashtable stepHash = (Hashtable)o;

         // Reconstruct XmlExpression
         tempObject = stepHash.get(EXPRESSION_KEY);
         if (tempObject == null || !(tempObject instanceof Hashtable))
            {
            continue;
            }
         Expression e = expressionFileHandler.setRepresentation((Hashtable)tempObject);
         if (e == null)
            {
            continue;
            }

         newStep = new SequenceStep(e);

         // Set XmlExpression Name
         tempObject = stepHash.get(EXPRESSION_NAME_KEY);
         if (tempObject != null && tempObject instanceof String)
            {
            e.setName((String)tempObject);
            }

         // Reconstruct Transition
         tempObject = stepHash.get(TRANSITION_MILLIS_KEY);
         if (tempObject != null && tempObject instanceof Integer)
            {
            newStep.getTransition().setMillisToNextStep((Integer)tempObject);
            }

         // Reconstruct Condition
         tempObject = stepHash.get(TRANSITION_CONDITION_KEY);
         if (tempObject != null && tempObject instanceof Hashtable)
            {
            Condition c = conditionFileHandler.setRepresentation((Hashtable)tempObject);
            newStep.getTransition().setCondition(c);

            tempObject = stepHash.get(CONDITION_NAME_KEY);
            if (tempObject != null && tempObject instanceof String)
               {
               c.setName((String)tempObject);
               }
            }

         // Reconstruct ExpressionSpeed
         tempObject = stepHash.get(EXPRESSIONSPEED_VELOCITY_KEY);
         if (tempObject != null && tempObject instanceof Integer)
            {
            newStep.getExpresisonSpeed().setServoVelocity((Integer)tempObject);
            }

         sequence.addElement(newStep);
         }

      return sequence;
      }
   }
