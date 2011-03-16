package edu.cmu.ri.mrpl.TeRK.communicator;

import java.util.Collection;
import java.util.concurrent.Callable;
import Ice.Communicator;
import Ice.ObjectImpl;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.ice.communicator.IceCommunicator;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventDistributorHelper;

/**
 * <p>
 * <code>AbstractTerkCommunicator</code> provides base functionality for {@link TerkCommunicator}s.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractTerkCommunicator implements TerkCommunicator
   {
   protected abstract IceCommunicator getIceCommunicator();

   private final String objectAdapterName;

   protected static void createTerkCommunicatorAsynchronously(final Callable<TerkCommunicator> terkCommunicatorCreationStrategy,
                                                              final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      TerkCommunicatorFactory.getInstance().createAsynchronously(terkCommunicatorCreationStrategy, listeners);
      }

   protected AbstractTerkCommunicator(final String objectAdapterName)
      {
      this.objectAdapterName = objectAdapterName;
      }

   public final String getObjectAdapterName()
      {
      return objectAdapterName;
      }

   public abstract PeerConnectionEventDistributorHelper getPeerConnectionEventDistributorHelper();

   public final void waitForShutdown()
      {
      getIceCommunicator().waitForShutdown();
      }

   public final ObjectPrx createServantProxy(final ObjectImpl servant)
      {
      if (servant.ice_isA(MAIN_SERVANT_PROXY_IDENTITY_NAME))
         {
         return getIceCommunicator().createServantProxy(servant, MAIN_SERVANT_PROXY_IDENTITY_NAME, getObjectAdapterName());
         }
      return getIceCommunicator().createServantProxy(servant, getObjectAdapterName());
      }

   public final void destroyServantProxy(final ObjectPrx servantProxy)
      {
      getIceCommunicator().destroyServantProxy(servantProxy, getObjectAdapterName());
      }

   public final void shutdown()
      {
      prepareForShutdown();
      executeBeforeCommunicatorShutdown();
      getIceCommunicator().shutdown();
      }

   /** Performs activities that prepare the communicator for shutdown. */
   protected abstract void prepareForShutdown();

   /**
    * Called by {@link #shutdown()} before the {@link Communicator} is shut down.  This method does nothing by default.
    */
   @SuppressWarnings({"NoopMethodInAbstractClass"})
   protected void executeBeforeCommunicatorShutdown()
      {
      // do nothing by default
      }
   }
