package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.util.Map;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface ControlPanelManagerEventListener
   {
   void handlePeerConnectedEvent(final Map<String, ServiceControlPanel> serviceControlPanelMap);

   void handlePeerDisconnectedEvent();

   void handleDeviceActivityStatusChange(final String serviceTypeId, final int deviceIndex, final boolean active);
   }