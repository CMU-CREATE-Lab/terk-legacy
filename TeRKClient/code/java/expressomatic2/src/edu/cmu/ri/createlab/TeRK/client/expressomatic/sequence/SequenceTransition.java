package edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence;

import java.io.Serializable;
import edu.cmu.ri.createlab.TeRK.client.expressomatic.conditions.Condition;

/**
 * Defines the transitions between each Composition step
 *
 * @author mel
 *
 */
public class SequenceTransition implements Serializable
   {
   private static final int DEFAULT_MILLIS = 2000;

   // If this Transition has a condition, this is not null
   private Condition condition = null;

   private int millisToNextStep = DEFAULT_MILLIS;

   public SequenceTransition()
      {
      }

   public void setToDefault()
      {
      millisToNextStep = DEFAULT_MILLIS;
      condition = null;
      }

   public void setCondition(Condition c)
      {
      this.condition = c;
      }

   public Condition getCondition()
      {
      return condition;
      }

   public void setSecondsToNextStep(float seconds)
      {
      this.millisToNextStep = (int)(seconds * 1000);
      }

   public void setMillisToNextStep(int millis)
      {
      this.millisToNextStep = millis;
      }

   public int getMillisToNextStep()
      {
      return millisToNextStep;
      }
   }
