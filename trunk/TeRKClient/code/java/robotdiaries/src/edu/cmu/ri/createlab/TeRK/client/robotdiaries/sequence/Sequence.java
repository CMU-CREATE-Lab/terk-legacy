package edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class Sequence extends DefaultListModel implements Serializable
   {

   private boolean loopBackToStart;
   private String name;

   public Sequence(String name)
      {
      super();
      this.name = name;
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

   public String getName()
      {
      return name;
      }

   public void setName(String name)
      {
      this.name = name;
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
