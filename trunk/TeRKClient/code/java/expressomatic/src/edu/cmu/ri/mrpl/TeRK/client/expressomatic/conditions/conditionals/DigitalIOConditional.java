package edu.cmu.ri.mrpl.TeRK.client.expressomatic.conditions.conditionals;

import java.util.Hashtable;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;

public class DigitalIOConditional extends AbstractConditional
   {
   public static final String DIGITAL_IO_NAME = "DigitalIO";
   public static final String DIGITAL_IO_DEVICE_ID_KEY = "device_id";
   public static final String DIGITAL_IO_VALUE_KEY = "compare_value";
   public static final String DIGITAL_IO_OPERATOR_KEY = "unary_operator";

   private int deviceId = -1;
   private boolean _value = true;
   private UNARY_OPERATOR _operator = UNARY_OPERATOR.NONE;

   private boolean lastChecked = _value;

   public DigitalIOConditional()
      {
      super();
      }

   public String getDefaultName()
      {
      return DIGITAL_IO_NAME;
      }

   public String getValueKey()
      {
      return DIGITAL_IO_VALUE_KEY;
      }

   public boolean getValue()
      {
      return _value;
      }

   public void setValue(boolean value)
      {
      this._value = value;
      }

   public UNARY_OPERATOR getOperator()
      {
      return _operator;
      }

   public void setOperator(UNARY_OPERATOR operator)
      {
      _operator = operator;
      }

   public int getDeviceId()
      {
      return deviceId;
      }

   public void setDeviceId(int value)
      {
      this.deviceId = value;
      }

   public Hashtable getValues()
      {
      Hashtable values = new Hashtable();
      values.put(DIGITAL_IO_DEVICE_ID_KEY, deviceId);
      values.put(DIGITAL_IO_VALUE_KEY, _value);
      values.put(DIGITAL_IO_OPERATOR_KEY, _operator.name());
      return values;
      }

   public void setValues(Hashtable values)
      {
      UNARY_OPERATOR uo = UNARY_OPERATOR.NONE;
      try
         {
         uo = UNARY_OPERATOR.valueOf((String)values.get(DIGITAL_IO_OPERATOR_KEY));
         }
      catch (Exception e)
         {
         }

      setDeviceId((Integer)values.get(DIGITAL_IO_DEVICE_ID_KEY));
      setValue((Boolean)values.get(DIGITAL_IO_VALUE_KEY));
      setOperator(uo);
      }

   public Object getLastCheckedValue()
      {
      return lastChecked;
      }

   public boolean evaluate(QwerkController qwerkController)
      {
      boolean value = qwerkController.getDigitalInService().getDigitalInputValue(deviceId);
      lastChecked = value;

      return value == _value;
      }
   }
