package edu.cmu.ri.mrpl.TeRK.client.expressomatic.sequence;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class Sequence extends DefaultListModel implements Serializable
   {

   private boolean loopBackToStart;

   public Sequence()
      {
      super();
      }

   public ArrayList<SequenceStep> getSteps()
      {
      Object[] objects = this.toArray();
      ArrayList<SequenceStep> steps = new ArrayList<SequenceStep>();

      for (int i = 0; i < objects.length; i++)
         {
         steps.add((SequenceStep)objects[i]);
         }

      return steps;
      }

   public boolean getLoopBackToStart()
      {
      return loopBackToStart;
      }

   public void setLoopBackToStart(boolean l)
      {
      loopBackToStart = l;
      }
   }
