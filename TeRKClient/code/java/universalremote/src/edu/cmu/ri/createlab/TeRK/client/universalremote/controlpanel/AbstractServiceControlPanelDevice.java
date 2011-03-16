package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import edu.cmu.ri.createlab.TeRK.expression.XmlDevice;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class AbstractServiceControlPanelDevice implements ServiceControlPanelDevice
   {
   private boolean isActive = false;
   private final int deviceIndex;

   protected AbstractServiceControlPanelDevice(final int deviceIndex)
      {
      this.deviceIndex = deviceIndex;
      }

   public int getDeviceIndex()
      {
      return deviceIndex;
      }

   public final boolean isActive()
      {
      return isActive;
      }

   public final void setActive(final boolean isActive)
      {
      this.isActive = isActive;
      }

   public final XmlDevice buildDevice()
      {
      return new XmlDevice(deviceIndex, buildParameters());
      }
   }
