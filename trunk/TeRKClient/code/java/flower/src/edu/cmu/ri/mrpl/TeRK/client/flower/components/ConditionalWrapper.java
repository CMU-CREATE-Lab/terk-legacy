package edu.cmu.ri.mrpl.TeRK.client.flower.components;

import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AbstractConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.AnalogInputsConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.DigitalIOConditional;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals.TimeOfDayConditional;

public class ConditionalWrapper extends AbstractComponentWrapper
   {
   AbstractConditional component = null;

   public ConditionalWrapper(AbstractConditional c)
      {
      component = c;
      }

   public ConditionalWrapper(AbstractConditional c, int deviceId)
      {
      component = c;
      if (c != null)
         {
         if (c instanceof AnalogInputsConditional)
            {
            ((AnalogInputsConditional)c).setDeviceId(deviceId);
            }
         else if (c instanceof DigitalIOConditional)
            {
            ((DigitalIOConditional)c).setDeviceId(deviceId);
            }
         }
      }

   public boolean equals(Object o)
      {
      if (o == null || !(o instanceof ConditionalWrapper))
         {
         return false;
         }

      AbstractConditional c = ((ConditionalWrapper)o).component;

      if (component == null || c == null)
         {
         return component == c;
         }

      if (!component.getClass().isInstance(c))
         {
         return false;
         }

      if (c instanceof AnalogInputsConditional)
         {
         return ((AnalogInputsConditional)c).getDeviceId() ==
                ((AnalogInputsConditional)component).getDeviceId();
         }
      if (c instanceof DigitalIOConditional)
         {
         return ((DigitalIOConditional)c).getDeviceId() ==
                ((DigitalIOConditional)component).getDeviceId();
         }
      if (c instanceof TimeOfDayConditional)
         {
         return ((TimeOfDayConditional)c).getValue().equals(
               ((TimeOfDayConditional)component).getValue());
         }

      return false;
      }

   public int hashCode()
      {
      if (component instanceof AnalogInputsConditional)
         {
         return (component.getClass().getName() +
                 ((AnalogInputsConditional)component).getDeviceId()).hashCode();
         }
      if (component instanceof DigitalIOConditional)
         {
         return (component.getClass().getName() +
                 ((DigitalIOConditional)component).getDeviceId()).hashCode();
         }
      if (component instanceof TimeOfDayConditional)
         {
         return (component.getClass().getName() +
                 ((TimeOfDayConditional)component).getValue().toString()).hashCode();
         }
      return 0;
      }
   }
