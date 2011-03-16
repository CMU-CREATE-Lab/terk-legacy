package edu.cmu.ri.mrpl.TeRK.client.flower.components;

import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;

public class CellWrapper extends AbstractComponentWrapper
   {
   DefaultCell component;

   public CellWrapper(DefaultCell c)
      {
      component = c;
      }

   public CellWrapper(DefaultCell c, int deviceId)
      {
      this(c);
      component.setDeviceId(deviceId);
      }

   public boolean equals(Object o)
      {
      if (o == null || !(o instanceof CellWrapper))
         {
         return false;
         }

      DefaultCell c = ((CellWrapper)o).component;

      if (component == null || c == null)
         {
         return component == c;
         }

      return component.getClass().isInstance(c) &&
             ((DefaultCell)c).getDeviceId() == component.getDeviceId();
      }

   public int hashCode()
      {
      return (component.getClass().getName() + component.getDeviceId()).hashCode();
      }
   }
