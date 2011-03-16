package edu.cmu.ri.createlab.TeRK.client.robotdiaries.conditions.conditionals;

import java.util.Calendar;
import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.services.ServiceManager;

public class TimeOfDayConditional extends AbstractConditional
   {
   public static final String TIME_OD_NAME = "Time Of Day";
   public static final String TIME_OD_VALUE_KEY = "compare_value";
   public static final String TIME_OD_OPERATOR_KEY = "compare_operator";

   private Calendar _value = null;
   private COMPARE_OPERATOR _operator = COMPARE_OPERATOR.EQUAL_TO;

   private Calendar lastChecked = null;

   public TimeOfDayConditional()
      {
      super();
      _value = Calendar.getInstance();
      _value.setTimeInMillis(0);
      }

   public String getDefaultName()
      {
      return TIME_OD_NAME;
      }

   public String getValueKey()
      {
      return TIME_OD_VALUE_KEY;
      }

   public Calendar getValue()
      {
      return _value;
      }

   public void setValue(Calendar value)
      {
      if (value != null)
         {
         this._value.setTimeInMillis(0);
         this._value.set(Calendar.HOUR_OF_DAY, value.get(Calendar.HOUR_OF_DAY));
         this._value.set(Calendar.MINUTE, value.get(Calendar.MINUTE));
         this._value.set(Calendar.SECOND, value.get(Calendar.SECOND));
         }
      }

   public COMPARE_OPERATOR getOperator()
      {
      return _operator;
      }

   public void setOperator(COMPARE_OPERATOR value)
      {
      this._operator = value;
      }

   public Hashtable getValues()
      {
      Hashtable values = new Hashtable();
      values.put(TIME_OD_VALUE_KEY, _value);
      values.put(TIME_OD_OPERATOR_KEY, _operator.name());
      return values;
      }

   public void setValues(Hashtable values)
      {
      COMPARE_OPERATOR co = COMPARE_OPERATOR.EQUAL_TO;
      try
         {
         co = COMPARE_OPERATOR.valueOf((String)values.get(TIME_OD_OPERATOR_KEY));
         }
      catch (Exception e)
         {
         }

      setValue((Calendar)values.get(TIME_OD_VALUE_KEY));
      setOperator(co);
      }

   public Object getLastCheckedValue()
      {
      return lastChecked;
      }

   public boolean evaluate(ServiceManager serviceManager)
      {
      Calendar now = getNowFixed();
      Calendar timeToExecute = _value;

      lastChecked = now;

      if (_operator == COMPARE_OPERATOR.EQUAL_TO)
         {
         return now.compareTo(timeToExecute) == 0;
         }
      else if (_operator == COMPARE_OPERATOR.GREATER_THAN)
         {
         return now.compareTo(timeToExecute) > 0;
         }
      else if (_operator == COMPARE_OPERATOR.GREATER_THAN_EQUALS)
         {
         return now.compareTo(timeToExecute) >= 0;
         }
      else if (_operator == COMPARE_OPERATOR.LESS_THAN)
         {
         return now.compareTo(timeToExecute) < 0;
         }
      else if (_operator == COMPARE_OPERATOR.LESS_THAN_EQUALS)
         {
         return now.compareTo(timeToExecute) <= 0;
         }
      else
         {//_operator == COMPARE_OPERATOR.NOT_EQUALS
         return now.compareTo(timeToExecute) != 0;
         }
      }

   private Calendar getNowFixed()
      {
      Calendar now = Calendar.getInstance();
      Calendar fixed = (Calendar)now.clone();
      fixed.setTimeInMillis(0);
      fixed.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
      fixed.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
      fixed.set(Calendar.SECOND, now.get(Calendar.SECOND));

      return fixed;
      }
   }
