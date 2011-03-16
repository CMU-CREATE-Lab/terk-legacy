package edu.cmu.ri.createlab.TeRK.client.expressomatic.sequence;

import java.io.Serializable;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;

public class SequenceStep implements Serializable
   {

   private XmlExpression expression;
   private SequenceTransition transition;

   public SequenceStep(XmlExpression e)
      {
      expression = e;
      transition = new SequenceTransition();
      }

   public void setExpression(XmlExpression e)
      {
      if (e != null)
         {
         this.expression = e;
         }
      }

   public XmlExpression getExpression()
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
