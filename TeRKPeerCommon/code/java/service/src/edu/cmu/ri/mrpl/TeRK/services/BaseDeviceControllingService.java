package edu.cmu.ri.mrpl.TeRK.services;

import edu.cmu.ri.createlab.TeRK.properties.PropertyManager;
import edu.cmu.ri.createlab.TeRK.properties.PropertyManagerWrapper;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseDeviceControllingService extends PropertyManagerWrapper implements Service, DeviceController
   {
   private final int deviceCount;

   protected BaseDeviceControllingService(final PropertyManager propertyManager, final int deviceCount)
      {
      super(propertyManager);
      this.deviceCount = deviceCount;
      }

   public final int getDeviceCount()
      {
      return deviceCount;
      }
   }
