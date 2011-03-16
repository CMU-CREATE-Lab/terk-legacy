package edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence;

import java.util.ArrayList;
import java.util.Calendar;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.Condition;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.ConditionEvaluator;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.expressions.ExpressionLoader;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

public class SequencePlayer
   {

   private final static long DEFAULT_SLEEP_TIME = 50;

   private static ConditionEvaluator conditionEvaluator;
   private static ExpressionLoader expressionLoader = ExpressionLoader.getInstance();

   private boolean stopPlaying;
   private boolean isPlaying;

   // The step currently being executed by player
   private SequenceStep currentStep;

   public SequencePlayer()
      {
      stopPlaying = false;
      isPlaying = false;
      currentStep = null;

      final SequencePlayer sequencePlayer = this;

      conditionEvaluator = new ConditionEvaluator()
      {
      public void conditionValuesChecked(Condition condition, Object[] values)
         {
         sequencePlayer.conditionValuesChecked(condition, values);
         }
      };
      }

   public void playSequence(ServiceManager serviceManager, Sequence sequence)
      {
      // List of steps in the sequence
      ArrayList<SequenceStep> steps = sequence.getSteps();
      if (steps == null || steps.size() < 1)
         {
         return;
         }

      stopPlaying = false;
      isPlaying = true;

      try
         {
         // Time that the last step was executed
         long stepTimeStart = 0;

         // Index of the next step to evaluate
         int index = 0;

         SequenceTransition t = null;
         Condition c;

         boolean executeNextStep = false;

         while (!stopPlaying)
            {
            // No steps have been executed yet
            if (currentStep == null)
               {
               //					currentStep = steps.get(index);
               executeNextStep = true;
               }

            // Must evaluate if the transition has completed for the last step
            else
               {
               t = currentStep.getTransition();
               c = t.getCondition();

               // There's a condition to satisfy
               if (c != null)
                  {
                  if (conditionEvaluator.evaluate(serviceManager, c))
                     {
                     executeNextStep = true;
                     }
                  }

               // Only time must be satisfied
               else
                  {
                  long now = Calendar.getInstance().getTimeInMillis();
                  if (now >= (stepTimeStart + t.getMillisToNextStep()))
                     {
                     executeNextStep = true;
                     }
                  }
               }

            // The next step should be executed
            if (executeNextStep)
               {
               setCurrentStep(steps.get(index));
               t = currentStep.getTransition();

               stepTimeStart = Calendar.getInstance().getTimeInMillis();

               // Check to make sure ExpressionSpeed.getDuration() is <= to transition time

               // Load expression onto services from manager
               expressionLoader.executeToServices(serviceManager, currentStep.getExpression());

               index++;

               // If this transition says to loopback to start, make sure index will wrap
               // around to beginning of ArrayList
               if (sequence.getLoopBackToStart())
                  {
                  index %= steps.size();
                  }

               if (index >= steps.size())
                  {
                  //allow time for the last expression to finish playing
                  while (!stopPlaying)
                     {
                     long now = Calendar.getInstance().getTimeInMillis();
                     if (now < (stepTimeStart + t.getMillisToNextStep()))
                        {
                        Thread.sleep(DEFAULT_SLEEP_TIME);
                        }
                     else
                        {
                        break;
                        }
                     }
                  break;
                  }

               executeNextStep = false;

               //					Thread.sleep(DEFAULT_SLEEP_TIME);
               }
            }
         }
      catch (Exception e)
         {

         }

      setCurrentStep(null);

      //  expressionLoader.stop(serviceManager);

      stopPlaying = false;
      isPlaying = false;
      }

   private void setCurrentStep(SequenceStep step)
      {
      currentStep = step;
      currentStepChanged(step);
      }

   /**
    * This method gets called when the currentStep being played has changed
    * @param step
    */
   public void currentStepChanged(SequenceStep step)
      {
      }

   /**
    * This method gets called when the value(s) of the current condition is checked
    * @param condition The condition being checked
    * @param values The values of the conditionals in the condition. The values
    * in the array are in the same order as their associated conditionals.
    */
   public void conditionValuesChecked(Condition condition, Object[] values)
      {
      }

   public void stopPlaying()
      {
      stopPlaying = true;
      }

   public boolean isPlaying()
      {
      return isPlaying;
      }

   private void safeSleep(long millis)
      {
      try
         {
         Thread.sleep(millis);
         }
      catch (InterruptedException e)
         {
         }
      }
   }
