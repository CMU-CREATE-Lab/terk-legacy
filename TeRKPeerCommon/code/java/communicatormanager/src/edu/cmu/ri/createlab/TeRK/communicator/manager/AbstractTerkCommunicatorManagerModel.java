package edu.cmu.ri.createlab.TeRK.communicator.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractTerkCommunicatorManagerModel implements TerkCommunicatorManager
   {
   private static final Logger LOG = Logger.getLogger(AbstractTerkCommunicatorManagerModel.class);

   private final Collection<TerkCommunicatorManagerListener> terkCommunicatorManagerListeners = new ArrayList<TerkCommunicatorManagerListener>();
   private final Collection<TerkCommunicatorCreationEventListener> terkCommunicatorCreationEventListeners = new ArrayList<TerkCommunicatorCreationEventListener>();
   private final ShutdownTerkCommunicatorRunnable shutdownTerkCommunicatorRunnable = new ShutdownTerkCommunicatorRunnable();
   private final ExecutorService executorPool = Executors.newCachedThreadPool(new DaemonThreadFactory("AbstractTerkCommunicatorManagerModel.executorPool"));

   private final String applicationName;
   private final String icePropertiesFile;
   private final String objectAdapterName;

   private boolean isSupported = true;
   private TerkCommunicator terkCommunicator;

   protected AbstractTerkCommunicatorManagerModel(final String applicationName,
                                                  final String icePropertiesFile,
                                                  final String objectAdapterName)
      {
      this.applicationName = applicationName;
      this.icePropertiesFile = icePropertiesFile;
      this.objectAdapterName = objectAdapterName;

      terkCommunicatorCreationEventListeners.add(new MyTerkCommunicatorCreationEventListener());
      }

   public final void addTerkCommunicatorManagerListener(final TerkCommunicatorManagerListener listener)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug(this.getClass().getSimpleName() + ".addTerkCommunicatorManagerListener(" + listener + ")");
         }
      if (listener != null)
         {
         terkCommunicatorManagerListeners.add(listener);
         }
      }

   public final void removeTerkCommunicatorManagerListener(final TerkCommunicatorManagerListener listener)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug(this.getClass().getSimpleName() + ".removeTerkCommunicatorManagerListener(" + listener + ")");
         }
      if (listener != null)
         {
         terkCommunicatorManagerListeners.remove(listener);
         }
      }

   public final void addTerkCommunicatorCreationEventListener(final TerkCommunicatorCreationEventListener listener)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug(this.getClass().getSimpleName() + ".addTerkCommunicatorCreationEventListener(" + listener + ")");
         }
      if (listener != null)
         {
         terkCommunicatorCreationEventListeners.add(listener);
         }
      }

   public final void removeTerkCommunicatorCreationEventListener(final TerkCommunicatorCreationEventListener listener)
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug(this.getClass().getSimpleName() + ".removeTerkCommunicatorCreationEventListener(" + listener + ")");
         }
      if (listener != null)
         {
         terkCommunicatorCreationEventListeners.remove(listener);
         }
      }

   public final boolean isSupported()
      {
      return isSupported;
      }

   public final void setIsSupported(final boolean isSupported)
      {
      // first shutdown the communicator...
      if (!isSupported)
         {
         shutdownCommunicator();
         }

      // now mark the manager as disabled
      this.isSupported = isSupported;

      // finally, notify the listeners
      for (final TerkCommunicatorManagerListener listener : terkCommunicatorManagerListeners)
         {
         listener.handleEnabledStateChange(isSupported);
         }
      }

   public final boolean isCreated()
      {
      return terkCommunicator != null;
      }

   public final void createCommunicator()
      {
      if (isSupported)
         {
         // See if it's already been created.  If so, don't recreate, but do call the afterSuccessfulConstruction()
         // method on the listeners.
         if (isCreated())
            {
            if (LOG.isDebugEnabled())
               {
               LOG.debug(this.getClass().getSimpleName() + ".createCommunicator(): Communicator already created, no need to create a new one.");
               }
            }
         else
            {
            LOG.debug("AbstractTerkCommunicatorManagerModel.createCommunicator(): creating communicator...");
            createCommunicatorAsynchronously(applicationName,
                                             icePropertiesFile,
                                             objectAdapterName,
                                             terkCommunicatorCreationEventListeners);
            }
         }
      else
         {
         throw new IllegalStateException("Failed to change the state of activation since the TerkCommunicatorManager is not currently enabled.");
         }
      }

   public final void shutdownCommunicator()
      {
      if (LOG.isDebugEnabled())
         {
         LOG.debug(this.getClass().getSimpleName() + ".shutdownCommunicator(): shutting down the communicator...");
         }
      executorPool.execute(shutdownTerkCommunicatorRunnable);
      }

   protected abstract void createCommunicatorAsynchronously(final String applicationName,
                                                            final String icePropertiesFile,
                                                            final String objectAdapterName,
                                                            final Collection<TerkCommunicatorCreationEventListener> terkCommunicatorCreationEventListeners);

   public final TerkCommunicator getTerkCommunicator()
      {
      return terkCommunicator;
      }

   private class ShutdownTerkCommunicatorRunnable implements Runnable
      {
      public void run()
         {
         if (terkCommunicator != null)
            {
            LOG.debug("AbstractTerkCommunicatorManagerModel$ShutdownTerkCommunicatorRunnable.run(): Disconnecting from peers before shutting down...");
            terkCommunicator.disconnectFromPeers();
            LOG.debug("AbstractTerkCommunicatorManagerModel$ShutdownTerkCommunicatorRunnable.run(): Shutting down the TerkCommunicator...");
            terkCommunicator.shutdown();
            terkCommunicator = null;
            LOG.debug("AbstractTerkCommunicatorManagerModel$ShutdownTerkCommunicatorRunnable.run(): Done shutting down the TerkCommunicator!");
            }
         }
      }

   private final class MyTerkCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator communicator)
         {
         terkCommunicator = communicator;
         }

      public void afterFailedConstruction()
         {
         // Make sure the communicator is shutdown.  It might not be if the user tried to create it previously, but it
         // failed for some reason during the processing of afterSuccessfulConstruction() (this can happen when creating
         // the direct-connect communicator if there's already another instance running on the same machine and port).
         if (terkCommunicator != null)
            {
            shutdownCommunicator();
            }
         }
      }
   }
