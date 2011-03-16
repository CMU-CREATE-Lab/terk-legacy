package edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions;

import java.io.Serializable;
import java.util.ArrayList;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional;

/**
 *
 * @author mel
 *
 */
public class Condition implements Serializable
   {
   private String name = "Conditional";

   private ArrayList<AbstractConditional> components;

   public Condition()
      {
      components = new ArrayList<AbstractConditional>();
      }

   public void setName(String newName)
      {
      name = (newName == null) ? name : newName;
      }

   public String getName()
      {
      return name;
      }

   public ArrayList<AbstractConditional> getConditionals()
      {
      return components;
      }

   public void insert(AbstractConditional conditional)
      {
      components.add(conditional);
      }
   }
