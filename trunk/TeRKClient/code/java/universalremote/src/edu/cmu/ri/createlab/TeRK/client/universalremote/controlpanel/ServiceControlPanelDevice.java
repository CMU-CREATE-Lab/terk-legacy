package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Component;
import java.util.Map;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.expression.XmlDevice;
import edu.cmu.ri.createlab.TeRK.expression.XmlParameter;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ServiceControlPanelDevice
   {
   int getDeviceIndex();

   Component getComponent();

   boolean isActive();

   void setActive(final boolean isActive);

   boolean execute(final String operationName, final Map<String, String> parameterMap);

   String getCurrentOperationName();

   XmlDevice buildDevice();

   Set<XmlParameter> buildParameters();
   }