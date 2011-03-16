package edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals;

import java.util.Hashtable;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;

public class AnalogInputsConditional extends AbstractConditional
   {

   public static final String ANALOG_INPUT_NAME = "AnalogInput";
   public static final String ANALOG_INPUT_VALUE_KEY = "compare_value";
   public static final String ANALOG_INPUT_OPERATOR_KEY = "compare_operator";
   public static final String ANALOG_INPUT_DEVICE_ID_KEY = "device_id";

   private int deviceId = -1;
   private int _value = 0;
   private COMPARE_OPERATOR _operator = COMPARE_OPERATOR.EQUAL_TO;

   private int lastChecked = _value;

   public AnalogInputsConditional()
      {
      super();
      }

   public String getDefaultName()
      {
      return ANALOG_INPUT_NAME;
      }

   public String getValueKey()
      {
      return ANALOG_INPUT_VALUE_KEY;
      }

   public int getValue()
      {
      return _value;
      }

   public void setValue(int value)
      {
      this._value = value;
      }

   public int getDeviceId()
      {
      return deviceId;
      }

   public void setDeviceId(int value)
      {
      this.deviceId = value;
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
      values.put(ANALOG_INPUT_DEVICE_ID_KEY, deviceId);
      values.put(ANALOG_INPUT_VALUE_KEY, _value);
      values.put(ANALOG_INPUT_OPERATOR_KEY, _operator.name());
      return values;
      }

   public void setValues(Hashtable values)
      {
      COMPARE_OPERATOR co = COMPARE_OPERATOR.EQUAL_TO;
      try
         {
         co = COMPARE_OPERATOR.valueOf((String)values.get(ANALOG_INPUT_OPERATOR_KEY));
         }
      catch (Exception e)
         {
         }

      setDeviceId((Integer)values.get(ANALOG_INPUT_DEVICE_ID_KEY));
      setValue((Integer)values.get(ANALOG_INPUT_VALUE_KEY));
      setOperator(co);
      }

   public Object getLastCheckedValue()
      {
      return lastChecked;
      }

   public boolean evaluate(QwerkController qwerkController)
      {
      AnalogInputsService analogInputsService = qwerkController.getAnalogInputsService();
      int value = analogInputsService.getAnalogInputValue(deviceId);

      lastChecked = value;

      if (_operator == COMPARE_OPERATOR.EQUAL_TO)
         {
         return value == _value;
         }
      else if (_operator == COMPARE_OPERATOR.GREATER_THAN)
         {
         return value > _value;
         }
      else if (_operator == COMPARE_OPERATOR.GREATER_THAN_EQUALS)
         {
         return value >= _value;
         }
      else if (_operator == COMPARE_OPERATOR.LESS_THAN)
         {
         return value < _value;
         }
      else if (_operator == COMPARE_OPERATOR.LESS_THAN_EQUALS)
         {
         return value <= _value;
         }
      else
         {//_operator == COMPARE_OPERATOR.GREATER_THAN
         return value != _value;
         }
      }
   }
