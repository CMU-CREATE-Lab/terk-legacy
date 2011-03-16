package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components;

/**
 * Created by IntelliJ IDEA.
 * User: Terk
 * Date: Feb 13, 2008
 * Time: 12:49:51 PM
 * To change this template use File | Settings | File Templates.
 */

public class AdvancedFlag
   {
   private boolean value;

   public AdvancedFlag(boolean val)
      {
      value = val;
      }

   public AdvancedFlag()
      {
      this(false);
      }

   public void setValue(boolean val)
      {
      value = val;
      }

   public boolean getValue()
      {
      return value;
      }
   }
