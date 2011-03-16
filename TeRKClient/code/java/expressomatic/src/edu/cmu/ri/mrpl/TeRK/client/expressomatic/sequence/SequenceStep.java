package edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence;

import java.io.Serializable;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.Expression;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;

public class SequenceStep implements Serializable
   {

   private Expression expression;
   private SequenceTransition transition;
   private ExpressionSpeed expressionSpeed;

   public SequenceStep(Expression e)
      {
      expression = (e == null) ? new Expression() : e;
      transition = new SequenceTransition();
      expressionSpeed = new ExpressionSpeed(ExpressionSpeed.MEDIUM_VELOCITY);
      }

   public void setExpression(Expression e)
      {
      if (e != null)
         {
         this.expression = e;
         }
      }

   public Expression getExpression()
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

   public void setExpressionSpeed(ExpressionSpeed speed)
      {
      expressionSpeed = speed;
      }

   public ExpressionSpeed getExpresisonSpeed()
      {
      return expressionSpeed;
      }
   }
