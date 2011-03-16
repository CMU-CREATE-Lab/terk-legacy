package edu.cmu.ri.createlab.TeRK.communicator.manager.directconnect;

import java.util.Collection;
import edu.cmu.ri.createlab.TeRK.communicator.manager.AbstractTerkCommunicatorManagerModel;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DirectConnectCommunicatorManagerImpl extends AbstractTerkCommunicatorManagerModel implements DirectConnectCommunicatorManager
   {
   private final ServantFactory servantFactory;

   public DirectConnectCommunicatorManagerImpl(final String applicationName,
                                               final String icePropertiesFile,
                                               final String objectAdapterName,
                                               final ServantFactory servantFactory)
      {
      super(applicationName, icePropertiesFile, objectAdapterName);
      this.servantFactory = servantFactory;
      }

   public DirectConnectCommunicator getDirectConnectCommunicator()
      {
      return (DirectConnectCommunicator)getTerkCommunicator();
      }

   protected void
   createCommunicatorAsynchronously(final String applicationName,
                                    final String icePropertiesFile,
                                    final String objectAdapterName,
                                    final Collection<TerkCommunicatorCreationEventListener> terkCommunicatorCreationEventListeners)
      {
      DirectConnectCommunicator.createAsynchronously(applicationName,
                                                     icePropertiesFile,
                                                     objectAdapterName,
                                                     terkCommunicatorCreationEventListeners,
                                                     servantFactory);
      }
   }
