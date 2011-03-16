package edu.cmu.ri.createlab.TeRK.robot.finch.servants;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.photoresistor.PhotoresistorServiceServantHelper;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;
import edu.cmu.ri.mrpl.TeRK.servants.AbstractServiceServant;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
class DefaultPhotoresistorServiceServantHelper extends AbstractServiceServant implements PhotoresistorServiceServantHelper
   {
   private final FinchProxy finchProxy;

   DefaultPhotoresistorServiceServantHelper(final FinchProxy finchProxy)
      {
      this.finchProxy = finchProxy;
      this.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.PHOTORESISTOR_DEVICE_COUNT);
      }

   public int getValue(final int id)
      {
      final int[] isDetected = getValues();

      if ((isDetected != null) &&
          (isDetected.length > 0) &&
          (id >= 0) &&
          (id < isDetected.length))
         {
         return isDetected[id];
         }

      return -1;
      }

   public int[] getValues()
      {
      return finchProxy.getPhotoresistors();
      }
   }