package edu.cmu.ri.mrpl.TeRK.relay;

import Ice.ObjectAdapter;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.persistence.HibernateUtil;
import edu.cmu.ri.mrpl.ice.AsynchronousBlobjectServant;
import edu.cmu.ri.mrpl.ice.IceApplication;
import edu.cmu.ri.mrpl.ice.SingletonServantLocator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RelayServer extends IceApplication
   {
   public int run(final String[] args)
      {
      // fetch the context map keys from the properties
      final String contextMapKeyPeerIdentity = communicator().getProperties().getProperty("contextMapKeyPeerIdentity");
      final String contextMapKeyPeerUserid = communicator().getProperties().getProperty("contextMapKeyPeerUserid");

      // configure and start up the relay server
      final ObjectAdapter adapter = communicator().createObjectAdapter("RelayServer");
      final ConnectionManager connectionManager = new ConnectionManager(adapter, contextMapKeyPeerIdentity, contextMapKeyPeerUserid);
      adapter.add(new TerkPermissionsVerifierServant(), Util.stringToIdentity("TerkPermissionsVerifier"));
      adapter.add(new TerkSessionManagerServant(connectionManager), Util.stringToIdentity("TerkSessionManager"));
      adapter.addServantLocator(new SingletonServantLocator(new AsynchronousBlobjectServant(connectionManager, connectionManager)), "");
      adapter.activate();

      communicator().waitForShutdown();

      return 0;
      }

   public static void main(final String[] args)
      {
      // force Hibernate to initialize now (so there's no lag later, e.g. when the first user logs in)
      HibernateUtil.getSessionFactory();

      // configure and start up the relay server
      final RelayServer app = new RelayServer();
      final int status = app.main("RelayServer", args, "/edu/cmu/ri/mrpl/TeRK/relay/RelayServer.properties");
      System.exit(status);
      }
   }
