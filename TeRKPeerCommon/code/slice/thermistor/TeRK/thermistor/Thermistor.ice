#ifndef TERK_THERMISTOR_ICE
#define TERK_THERMISTOR_ICE

#include <TeRK/TeRKCommon.ice>

[["java:java5"]]
[["java:package:edu.cmu.ri.mrpl"]]
module TeRK
   {
   module thermistor
      {
      exception ThermistorException extends ServiceException { };

      interface ThermistorService extends AbstractCommandController
         {
         // Returns the value for the thermistor specified by the given id.  Throws a ThermistorException if the value
         // could not be retrieved.
         ["ami"] int getValue(int id) throws ThermistorException;

         // Returns the value of all thermistors.  Throws a ThermistorException if the value could not be retrieved.
         ["ami"] IntArray getValues() throws ThermistorException;
         };
      };
   };

#endif
