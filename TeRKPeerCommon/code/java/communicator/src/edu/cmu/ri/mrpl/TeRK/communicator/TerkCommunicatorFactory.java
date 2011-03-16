package edu.cmu.ri.mrpl.TeRK.communicator;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.cmu.ri.mrpl.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class TerkCommunicatorFactory
   {
   private static final Logger LOG = Logger.getLogger(TerkCommunicatorFactory.class);
   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private static final TerkCommunicatorFactory INSTANCE = new TerkCommunicatorFactory();

   private final ExecutorService executorPool = Executors.newCachedThreadPool(new DaemonThreadFactory("TerkCommunicatorFactory.executorPool"));

   public static TerkCommunicatorFactory getInstance()
      {
      return INSTANCE;
      }

   private TerkCommunicatorFactory()
      {
      // private to prevent instantiation
      }

   void createAsynchronously(final Callable<TerkCommunicator> terkCommunicatorCreationStrategy,
                             final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      executorPool.execute(
            new Runnable()
            {
            public void run()
               {
               if (notifyListenersOfBeforeConstruction(listeners))
                  {
                  // try to create the TerkCommunicator
                  final TerkCommunicator terkCommunicator = createTerkCommunicator(terkCommunicatorCreationStrategy);

                  // see whether it was created successfully
                  if (terkCommunicator == null)
                     {
                     notifyListenersOfFailedConstruction(listeners);
                     }
                  else
                     {
                     if (notifyListenersOfSuccessfulConstructionAndBeforeWaitForShutdown(listeners, terkCommunicator))
                        {
                        // start the communicator and then block, waiting for it to shutdown
                        terkCommunicator.waitForShutdown();

                        notifyListenersOfAfterWaitForShutdown(listeners);
                        }
                     else
                        {
                        notifyListenersOfFailedConstruction(listeners);
                        }
                     }
                  }
               }
            });
      }

   /**
    * Calls the listeners {@link TerkCommunicatorCreationEventListener#beforeConstruction()} method and returns
    * <code>true</code> if the method returns without throwing an exception; returns <code>false</code> otherwise.
    */
   private boolean notifyListenersOfBeforeConstruction(final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      try
         {
         if ((listeners != null) && (!listeners.isEmpty()))
            {
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               // run each of these in a separate thread
               executorPool.execute(
                     new Runnable()
                     {
                     public void run()
                        {
                        listener.beforeConstruction();
                        }
                     });
               }
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while calling beforeConstruction().  Aborting.", e);
         return false;
         }

      return true;
      }

   private TerkCommunicator createTerkCommunicator(final Callable<TerkCommunicator> terkCommunicatorCreationStrategy)
      {
      try
         {
         return terkCommunicatorCreationStrategy.call();
         }
      catch (Exception e)
         {
         LOG.error("Exception while constructing the TerkCommunicator", e);
         }
      return null;
      }

   private void notifyListenersOfFailedConstruction(final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      LOG.error("TerkCommunicator is null, calling afterFailedConstruction() on the listeners...");

      // call each listener's afterFailedConstruction() method
      try
         {
         if ((listeners != null) && (!listeners.isEmpty()))
            {
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               // run each of these in a separate thread
               executorPool.execute(
                     new Runnable()
                     {
                     public void run()
                        {
                        listener.afterFailedConstruction();
                        }
                     });
               }
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while calling afterFailedConstruction().  Ignoring.", e);
         }
      }

   /**
    * Calls each listener's {@link TerkCommunicatorCreationEventListener#afterSuccessfulConstruction(TerkCommunicator)}
    * method and then calls each listener's {@link TerkCommunicatorCreationEventListener#beforeWaitForShutdown()} method
    * and returns <code>true</code> if the both methods return without throwing an exception; returns <code>false</code>
    * otherwise.
    */
   private boolean notifyListenersOfSuccessfulConstructionAndBeforeWaitForShutdown(final Collection<TerkCommunicatorCreationEventListener> listeners,
                                                                                   final TerkCommunicator terkCommunicator)
      {
      // call each listener's afterSuccessfulConstruction() method
      try
         {
         // write a log message specifying which listeners are going to be notified:
         if (LOG.isDebugEnabled())
            {
            final StringBuffer s = new StringBuffer("TerkCommunicatorFactory will notify these " + listeners.size() + " listeners of communicator creation: " + LINE_SEPARATOR);
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               s.append("   Listener: ").append(listener.getClass()).append(LINE_SEPARATOR);
               }

            LOG.debug(s);
            }

         if ((listeners != null) && (!listeners.isEmpty()))
            {
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               LOG.debug("   Notifying " + listener.getClass() + " of communicator creation...");
               listener.afterSuccessfulConstruction(terkCommunicator);
               LOG.debug("   Done notifying " + listener.getClass() + " of communicator creation!");
               }
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while calling afterSuccessfulConstruction().  Aborting.", e);
         return false;
         }

      // call each listener's beforeWaitForShutdown() method
      try
         {
         if ((listeners != null) && (!listeners.isEmpty()))
            {
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               listener.beforeWaitForShutdown();
               }
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while calling beforeWaitForShutdown().  Aborting.", e);
         return false;
         }

      return true;
      }

   private void notifyListenersOfAfterWaitForShutdown(final Collection<TerkCommunicatorCreationEventListener> listeners)
      {
      // call each listener's afterWaitForShutdown() method
      try
         {
         if ((listeners != null) && (!listeners.isEmpty()))
            {
            for (final TerkCommunicatorCreationEventListener listener : listeners)
               {
               // run each of these in a separate thread
               executorPool.execute(
                     new Runnable()
                     {
                     public void run()
                        {
                        listener.afterWaitForShutdown();
                        }
                     });
               }
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while calling afterWaitForShutdown().  Ignoring.", e);
         }
      }
   }
