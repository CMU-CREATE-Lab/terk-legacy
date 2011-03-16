package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ControlPanelManagerViewEventPublisher
   {
   void addControlPanelManagerViewEventListener(final ControlPanelManagerViewEventListener listener);

   void removeControlPanelManagerViewEventListener(final ControlPanelManagerViewEventListener listener);
   }