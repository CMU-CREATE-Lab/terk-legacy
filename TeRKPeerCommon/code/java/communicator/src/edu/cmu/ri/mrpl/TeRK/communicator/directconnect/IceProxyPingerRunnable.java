package edu.cmu.ri.mrpl.TeRK.communicator.directconnect;

import Ice.ObjectPrx;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * A {@link Runnable} whose {@link #run()} method calls {@link ObjectPrx#ice_ping() ice_ping()} on the {@link ObjectPrx}
 * given to the contstructor (in truth, it's a copy of the proxy, with a timeout set to 3 seconds).  If the ping fails
 * or times out, the {@link #handlePingFailure()} method is called.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class IceProxyPingerRunnable implements Runnable
   {
   private static final Logger LOG = Logger.getLogger(IceProxyPingerRunnable.class);

   private static final int DEFAULT_PING_TIMEOUT = 3000;

   private final String name;
   private final ObjectPrx objectProxy;

   IceProxyPingerRunnable(final String name, final ObjectPrx objectProxy)
      {
      this.name = name;
      if (objectProxy == null)
         {
         throw new NullPointerException("The objectProxy cannot be null!");
         }

      this.objectProxy = objectProxy.ice_timeout(DEFAULT_PING_TIMEOUT);
      }

   public final void run()
      {
      try
         {
         if (LOG.isTraceEnabled())
            {
            LOG.trace("IceProxyPingerRunnable.run(): pinging proxy [" + name + "]");
            }
         objectProxy.ice_ping();
         }
      catch (Exception e)
         {
         if (LOG.isEnabledFor(Level.ERROR))
            {
            LOG.error("IceProxyPingerRunnable.run(): Exception while trying to ping the proxy [" + name + "].  Calling handlePingFailure().", e);
            }

         try
            {
            handlePingFailure();
            }
         catch (Exception ex)
            {
            LOG.error("IceProxyPingerRunnable.run(): Exception caught (and otherwise ignored) while executing handlePingFailure() after the ping for [" + name + "] failed.", ex);
            }
         }
      }

   protected final ObjectPrx getObjectProxy()
      {
      return objectProxy;
      }

   protected abstract void handlePingFailure();
   }
