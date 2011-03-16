package edu.cmu.ri.createlab.TeRK.thermistor;

import edu.cmu.ri.mrpl.TeRK.services.DeviceController;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ThermistorService extends Service, DeviceController
   {
   String TYPE_ID = "::TeRK::thermistor::ThermistorService";

   String PROPERTY_NAME_THERMISTOR_DEVICE_ID = TYPE_ID + "::thermistor-device-id";

   /**
    * Returns the value of the thermistor specified by the given <code>id</code>.  Returns <code>null</code> if the 
    * value could not be retrieved.
    *
    * See usage note at {@link #getCelsiusTemperature(int)}.
    */
   Integer getThermistorValue(final int id);

   /**
    * Returns the temperature in degrees Celsius detected by the thermistor specified by the given <code>id</code>.
    * Returns <code>null</code> if the value could not be retrieved.
    *
    * NOTE: This is merely a helper method, identical to calling {@link #convertToCelsius(Integer)}, giving it the value returned
    * by {@link #getThermistorValue(int)}.  Since this method calls {@link #getThermistorValue(int)}, if you need to use
    * both the raw value and the value in degrees Celsius, it's more efficient to call {@link #getThermistorValue(int)}
    * and {@link #convertToCelsius(Integer)} rather than {@link #getThermistorValue(int)} and this method since the
    * thermistor will only be read once.
    *
    * @throws UnsupportedOperationException if the implementation cannot perform the conversion to degrees Celsius
    *
    * @see #convertToCelsius(Integer)
    */
   Double getCelsiusTemperature(final int id);

   /**
    * Converts the given <code>rawValue</code> (as returned by {@link #getThermistorValue(int)}) to degrees Celsius.
    * Returns <code>null</code> if the conversion failed.
    *
    * @throws UnsupportedOperationException if the implementation cannot perform the conversion to degrees Celsius
    *
    * @see #getCelsiusTemperature(int)
    */
   Double convertToCelsius(final Integer rawValue);

   /**
    * Returns <code>true</code> if the implementation of this service can perform conversions from raw values to
    * degrees Celsius.  Calling this method is a good way to avoid having to use a try-catch block to catch the
    * {@link UnsupportedOperationException} which would be thrown by some methods if unit conversion isn't supported.
    */
   boolean isUnitConversionSupported();
   }