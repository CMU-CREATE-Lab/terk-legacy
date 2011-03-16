package edu.cmu.ri.mrpl.TeRK.servants;

import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>NoServicesServantFactory</code> is a simple {@link ServantFactory} for use by users which provide no services.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class NoServicesServantFactory implements ServantFactory
   {
   private static final Logger LOG = Logger.getLogger(NoServicesServantFactory.class);

   public NoServicesServantFactory()
      {
      // do nothing
      }

   public Servants createServants(final TerkCommunicator terkCommunicator)
      {
      LOG.debug("NoServicesServantFactory.createServants()");

      final TerkUserServant mainServant = new TerkUserServant(terkCommunicator);

      // create the main servant proxy (only...no secondary servant proxies are needed since no services are provided)
      final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
      final TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

      return new Servants(mainServantPrx, mainServantPrx);
      }
   }