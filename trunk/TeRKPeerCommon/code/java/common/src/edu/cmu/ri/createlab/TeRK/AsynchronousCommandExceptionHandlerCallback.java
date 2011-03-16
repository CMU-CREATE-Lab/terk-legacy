package edu.cmu.ri.createlab.TeRK;

/**
 * <p>
 * <code>AsynchronousCommandExceptionHandlerCallback</code> provides a way for callers of asynchronous commands to
 * handle exceptions.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class AsynchronousCommandExceptionHandlerCallback
   {
   /** Method for handling {@link Exception}s.  Does nothing by default. */
   public void handleException(final Exception exception)
      {
      // do nothing
      }
   }
