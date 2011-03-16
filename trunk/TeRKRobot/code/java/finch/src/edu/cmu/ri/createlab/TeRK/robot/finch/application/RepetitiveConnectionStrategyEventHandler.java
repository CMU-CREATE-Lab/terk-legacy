package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.application.ConnectionStrategyEventHandler;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FixedDelayRepetitiveConnectionStrategyEventHandler</code> is a wrapper for a
 * {@link ConnectionStrategyEventHandler} which repetitively calls the wrapped handler's
 * {@link ConnectionStrategyEventHandler#handleConnectionEvent()} method.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class RepetitiveConnectionStrategyEventHandler implements ConnectionStrategyEventHandler
   {
   private static final Logger LOG = Logger.getLogger(RepetitiveConnectionStrategyEventHandler.class);

   private final Runnable attemptingConnectionEventAction = new AttemptingConnectionEventAction();
   private final Runnable connectionEventAction = new ConnectionEventAction();
   private final Runnable failedConnectionEventAction = new FailedConnectionEventAction();
   private final Runnable attemptingDisconnectionEventAction = new AttemptingDisconnectionEventAction();
   private final Runnable disconnectionEventAction = new DisconnectionEventAction();
   private ScheduledExecutorService scheduledExecutor = null;
   private Executor executor = Executors.newCachedThreadPool();

   private final ConnectionStrategyEventHandler handler;
   private final int initialDelay;
   private final int delayBetweenInvocations;
   private final TimeUnit timeUnit;

   RepetitiveConnectionStrategyEventHandler(final ConnectionStrategyEventHandler handler,
                                            final int initialDelay,
                                            final int delayBetweenInvocations,
                                            final TimeUnit timeUnit)
      {
      this.handler = handler;
      this.initialDelay = initialDelay;
      this.delayBetweenInvocations = delayBetweenInvocations;
      this.timeUnit = timeUnit;
      }

   /**
    * Calls the wrapped handler's {@link ConnectionStrategyEventHandler#handleAttemptingConnectionEvent()} method,
    * making sure it does not run in the GUI thread.
    */
   public void handleAttemptingConnectionEvent()
      {
      LOG.debug("RepetitiveConnectionStrategyEventHandler.handleAttemptingConnectionEvent()");

      runNotInGUIThread(attemptingConnectionEventAction);
      }

   /**
    * Creates a {@link ScheduledExecutorService} which repetitively calls the wrapped handler's
    * {@link ConnectionStrategyEventHandler#handleConnectionEvent()} method using the <code>initialDelay</code>,
    * <code>delayBetweenInvocations</code>, and <code>timeUnit</code> given to this instance's constructor.
    */
   public void handleConnectionEvent()
      {
      LOG.debug("RepetitiveConnectionStrategyEventHandler.handleConnectionEvent()");

      scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
      try
         {
         scheduledExecutor.scheduleWithFixedDelay(connectionEventAction, initialDelay, delayBetweenInvocations, timeUnit);
         }
      catch (Exception e)
         {
         LOG.error("Exception while trying to schedule the run action for execution", e);
         }
      }

   /**
    * Calls the wrapped handler's {@link ConnectionStrategyEventHandler#handleFailedConnectionEvent()} method, making
    * sure it does not run in the GUI thread.
    */
   public void handleFailedConnectionEvent()
      {
      LOG.debug("RepetitiveConnectionStrategyEventHandler.handleFailedConnectionEvent()");

      runNotInGUIThread(failedConnectionEventAction);
      }

   /**
    * Calls the wrapped handler's {@link ConnectionStrategyEventHandler#handleAttemptingDisconnectionEvent()} method,
    * making sure it does not run in the GUI thread.
    */
   public void handleAttemptingDisconnectionEvent()
      {
      LOG.debug("RepetitiveConnectionStrategyEventHandler.handleAttemptingDisconnectionEvent()");

      runNotInGUIThread(attemptingDisconnectionEventAction);
      }

   /**
    * Stops the repetitive execution of the wrapped handler's
    * {@link ConnectionStrategyEventHandler#handleConnectionEvent()} method, and then calls the wrapped handler's
    * {@link ConnectionStrategyEventHandler#handleDisconnectionEvent()} ()} method, making sure it does not run in the
    * GUI thread.
    */
   public void handleDisconnectionEvent()
      {
      LOG.debug("RepetitiveConnectionStrategyEventHandler.handleDisconnectionEvent()");

      try
         {
         if (scheduledExecutor != null)
            {
            // stop the scheduledExecutor
            scheduledExecutor.shutdown();
            while (!scheduledExecutor.isTerminated())
               {
               // do nothing
               }

            // now run the disconnection event
            runNotInGUIThread(disconnectionEventAction);
            }
         }
      catch (Exception e)
         {
         LOG.error("Exception while executing the stop action, or shutting down the scheduled executor.", e);
         }
      }

   private void runNotInGUIThread(final Runnable runnable)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         executor.execute(runnable);
         }
      else
         {
         runnable.run();
         }
      }

   private class AttemptingConnectionEventAction implements Runnable
      {
      public void run()
         {
         LOG.trace("RepetitiveConnectionStrategyEventHandler$AttemptingConnectionEventAction.run()");
         try
            {
            handler.handleAttemptingConnectionEvent();
            }
         catch (Exception e)
            {
            LOG.error("Exception while executing handleAttemptingConnectionEvent()", e);
            }
         }
      }

   private class ConnectionEventAction implements Runnable
      {
      public void run()
         {
         LOG.trace("RepetitiveConnectionStrategyEventHandler$ConnectionEventAction.run()");
         try
            {
            handler.handleConnectionEvent();
            }
         catch (Exception e)
            {
            LOG.error("Exception while executing handleConnectionEvent()", e);
            }
         }
      }

   private class FailedConnectionEventAction implements Runnable
      {
      public void run()
         {
         LOG.trace("RepetitiveConnectionStrategyEventHandler$FailedConnectionEventAction.run()");
         try
            {
            handler.handleFailedConnectionEvent();
            }
         catch (Exception e)
            {
            LOG.error("Exception while executing handleFailedConnectionEvent()", e);
            }
         }
      }

   private class AttemptingDisconnectionEventAction implements Runnable
      {
      public void run()
         {
         LOG.trace("RepetitiveConnectionStrategyEventHandler$AttemptingDisconnectionEventAction.run()");
         try
            {
            handler.handleAttemptingDisconnectionEvent();
            }
         catch (Exception e)
            {
            LOG.error("Exception while executing handleAttemptingDisconnectionEvent()", e);
            }
         }
      }

   private class DisconnectionEventAction implements Runnable
      {
      public void run()
         {
         LOG.trace("RepetitiveConnectionStrategyEventHandler$DisconnectionEventAction.run()");
         try
            {
            handler.handleDisconnectionEvent();
            }
         catch (Exception e)
            {
            LOG.error("Exception while running handleDisconnectionEvent()", e);
            }
         }
      }
   }
