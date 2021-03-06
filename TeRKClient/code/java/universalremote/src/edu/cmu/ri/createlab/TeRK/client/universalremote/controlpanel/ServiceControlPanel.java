package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.awt.Component;
import java.util.Set;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.createlab.TeRK.expression.XmlOperation;
import edu.cmu.ri.createlab.TeRK.expression.XmlService;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ServiceControlPanel
   {
   String getTypeId();

   int getDeviceCount();

   String getDisplayName();

   String getShortDisplayName();

   Component getComponent();

   /**
    * Refreshes this component, usually by getting the current state from the service and updates the control panel
    * accordingly.  This method assumes that it is not running in the GUI thread, so it is up to the caller to ensure
    * that this assumption holds true.
    */
   void refresh();

   /**
    * If <code>isActive</code> is <code>true</code>, this method marks the specified as active; if <code>false</code>,
    * then this method marks the device as inactive.
    */
   void setDeviceActive(final int deviceIndex, final boolean isActive);

   /**
    * Loads the given {@link XmlOperation} and returns a {@link Set} of devices which were activated as a result.  Returns
    * <code>null</code> if no devices were activated (e.g. unknown operation, unknown device, etc.)
    */
   Set<Integer> loadOperation(final XmlOperation operation);

   /**
    * Creates a {@link XmlService} represeting the current state of the control panel.  This {@link XmlService} can then be
    * used to create an {@link XmlExpression}.
    */
   XmlService buildService();
   }