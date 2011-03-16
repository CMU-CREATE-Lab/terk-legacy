package edu.cmu.ri.mrpl.TeRK.communicator;

/**
 * <p>
 * <code>TerkCommunicatorCreationEventAdapater</code> is an abstract adapter class for receiving events triggered
 * during the creation and startup of a {@link TerkCommunicator}. The methods in this class are empty. This class
 * exists as convenience for creating listener objects.
 * </p>
 * <p>
 * Extend this class to create a {@link TerkCommunicatorCreationEventListener} and override the methods for the events
 * of interest. (If you implement the {@link TerkCommunicatorCreationEventListener} interface, you have to define all
 * of the methods in it. This abstract class defines no-op methods for them all, so you only have to define methods for
 * events you care about.)
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"NoopMethodInAbstractClass"})
public abstract class TerkCommunicatorCreationEventAdapater implements TerkCommunicatorCreationEventListener
   {
   public void beforeConstruction()
      {
      // do nothing
      }

   public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
      {
      // do nothing
      }

   public void afterFailedConstruction()
      {
      // do nothing
      }

   public void beforeWaitForShutdown()
      {
      // do nothing
      }

   public void afterWaitForShutdown()
      {
      // do nothing
      }
   }
