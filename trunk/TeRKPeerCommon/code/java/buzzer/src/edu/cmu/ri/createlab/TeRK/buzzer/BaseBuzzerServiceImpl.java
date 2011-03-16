package edu.cmu.ri.createlab.TeRK.buzzer;

import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.expression.XmlDevice;
import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.mrpl.TeRK.services.BaseDeviceControllingService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseBuzzerServiceImpl extends BaseDeviceControllingService implements BuzzerService
   {
   public BaseBuzzerServiceImpl(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager, deviceCount);
      }

   public final String getTypeId()
      {
      return TYPE_ID;
      }

   public final Object executeOperation(final XmlOperation operation)
      {
      if (operation != null && OPERATION_NAME_PLAY_TONE.equals(operation.getName()))
         {
         final Set<XmlDevice> xmlDevices = operation.getDevices();
         if ((xmlDevices != null) && (!xmlDevices.isEmpty()))
            {
            for (final XmlDevice xmlDevice : xmlDevices)
               {
               if (xmlDevice != null)
                  {
                  final Map<String, XmlParameter> parametersMap = xmlDevice.getParametersAsMap();
                  if (parametersMap != null)
                     {
                     final XmlParameter frequencyParameter = parametersMap.get(PARAMETER_NAME_FREQUENCY);
                     final XmlParameter durationParameter = parametersMap.get(PARAMETER_NAME_DURATION);
                     if (frequencyParameter != null && durationParameter != null)
                        {
                        final Integer freq = frequencyParameter.getValueAsInteger();
                        final Integer dur = durationParameter.getValueAsInteger();
                        if (freq != null && dur != null)
                           {
                           playTone(xmlDevice.getId(), freq, dur);
                           }
                        }
                     }
                  }
               }
            }
         }

      return null;
      }
   }