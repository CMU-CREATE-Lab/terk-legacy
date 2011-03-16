package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorService;
import edu.cmu.ri.createlab.TeRK.thermistor.ThermistorServiceServantHelper;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;
import edu.cmu.ri.mrpl.TeRK.thermistor.ThermistorException;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultThermistorServiceServantHelper extends AbstractServiceServant implements ThermistorServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultThermistorServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.THERMISTOR_DEVICE_COUNT);
      this.setReadOnlyProperty(ThermistorService.PROPERTY_NAME_THERMISTOR_DEVICE_ID, FinchConstants.THERMISTOR_DEVICE_ID);
      }

   public int getValue(final int id) throws ThermistorException
      {
      final Integer value = finchProxy.getThermistor(id);

      if (value != null)
         {
         return value;
         }

      throw new ThermistorException("Value returned from FinchProxy for thermistor [" + id + "] was null.");
      }

   public int[] getValues() throws ThermistorException
      {
      final Integer value = finchProxy.getThermistor();

      if (value != null)
         {
         return new int[]{value};
         }

      throw new ThermistorException("Thermistor values returned from FinchProxy were null.");
      }
   }