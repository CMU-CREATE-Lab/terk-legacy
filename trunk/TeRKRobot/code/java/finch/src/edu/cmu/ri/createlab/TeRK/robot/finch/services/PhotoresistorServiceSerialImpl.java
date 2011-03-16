package edu.cmu.ri.createlab.TeRK.robot.finch.services;

import edu.cmu.ri.createlab.TeRK.TerkConstants;
import edu.cmu.ri.createlab.TeRK.photoresistor.BasePhotoresistorServiceImpl;
import edu.cmu.ri.createlab.TeRK.properties.BasicPropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.robot.finch.FinchConstants;
import edu.cmu.ri.createlab.TeRK.robot.finch.serial.proxy.FinchProxy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class PhotoresistorServiceSerialImpl extends BasePhotoresistorServiceImpl
   {
   static PhotoresistorServiceSerialImpl create(final FinchProxy finchProxy)
      {
      final BasicPropertyManager basicPropertyManager = new BasicPropertyManager();

      basicPropertyManager.setReadOnlyProperty(TerkConstants.PropertyKeys.DEVICE_COUNT, FinchConstants.PHOTORESISTOR_DEVICE_COUNT);

      return new PhotoresistorServiceSerialImpl(finchProxy,
                                                basicPropertyManager,
                                                FinchConstants.PHOTORESISTOR_DEVICE_COUNT);
      }

   private final FinchProxy finchProxy;

   private PhotoresistorServiceSerialImpl(final FinchProxy finchProxy,
                                          final PropertyManager propertyManager,
                                          final int deviceCount)
      {
      super(propertyManager, deviceCount);
      this.finchProxy = finchProxy;
      }

   public Integer getPhotoresistorValue(final int id)
      {
      final int[] values = getPhotoresistorValues();
      if ((values != null) && (id >= 0) && (id < values.length))
         {
         return values[id];
         }

      return null;
      }

   public int[] getPhotoresistorValues()
      {
      return finchProxy.getPhotoresistors();
      }
   }