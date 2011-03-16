package edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence;

import java.io.Serializable;

public class SequenceStep<O> implements Serializable
   {

   private O expression;
   private SequenceTransition transition;

   public SequenceStep(O e)
      {
      expression = e;
      transition = new SequenceTransition();
      }

   public void setStep(O e)
      {
      if (e != null)
         {
         this.expression = e;
         }
      }

   public O getStep()
      {
      return expression;
      }

   public void setTransition(SequenceTransition t)
      {
      if (t != null)
         {
         this.transition = t;
         }
      }

   public SequenceTransition getTransition()
      {
      return transition;
      }
   }
