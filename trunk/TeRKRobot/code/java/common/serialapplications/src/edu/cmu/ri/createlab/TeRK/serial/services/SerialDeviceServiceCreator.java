package edu.cmu.ri.createlab.TeRK.serial.services;

import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.mrpl.TeRK.services.Service;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceServiceCreator
   {
   Service create(final SerialDeviceProxy proxy);
   }