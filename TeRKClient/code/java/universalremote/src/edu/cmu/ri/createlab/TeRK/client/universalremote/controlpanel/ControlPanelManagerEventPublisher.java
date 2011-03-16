package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
interface ControlPanelManagerEventPublisher
   {
   void addControlPanelManagerEventListener(final ControlPanelManagerEventListener listener);

   void removeControlPanelManagerEventListener(final ControlPanelManagerEventListener listener);
   }