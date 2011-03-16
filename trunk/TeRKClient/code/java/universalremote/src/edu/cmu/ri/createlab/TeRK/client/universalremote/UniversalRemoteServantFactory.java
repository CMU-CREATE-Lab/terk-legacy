package edu.cmu.ri.createlab.TeRK.client.universalremote;

import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrx;
import edu.cmu.ri.mrpl.TeRK.TerkUserPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.TeRK.servants.TerkUserServant;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>UniversalRemoteServantFactory</code> creates the servants for services published by this client.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class UniversalRemoteServantFactory implements ServantFactory
   {
   private static final Logger LOG = Logger.getLogger(UniversalRemoteServantFactory.class);

   UniversalRemoteServantFactory()
      {
      }

   public Servants createServants(final TerkCommunicator terkCommunicator)
      {
      LOG.debug("UniversalRemoteServantFactory.createServants()");

      final TerkUserServant mainServant = new TerkUserServant(terkCommunicator);

      // create the main servant proxy (only...no secondary servant proxies are needed since this client doesn't currently provide any services)
      final ObjectPrx mainServantProxy = terkCommunicator.createServantProxy(mainServant);
      final TerkUserPrx mainServantPrx = TerkUserPrxHelper.uncheckedCast(mainServantProxy);

      return new Servants(mainServantPrx, mainServantPrx);
      }
   }