package edu.cmu.ri.createlab.TeRK.communicator.manager.relay;

import java.util.Collection;
import java.util.Set;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.communicator.manager.AbstractTerkCommunicatorManagerModel;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import edu.cmu.ri.mrpl.peer.UserConnectionEventAdapter;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class RelayCommunicatorManagerImpl extends AbstractTerkCommunicatorManagerModel implements RelayCommunicatorManager
   {
   private static final Logger LOG = Logger.getLogger(RelayCommunicatorManagerImpl.class);

   private final ServantFactory servantFactory;

   public RelayCommunicatorManagerImpl(final String applicationName,
                                       final String icePropertiesFile,
                                       final String objectAdapterName)
      {
      this(applicationName, icePropertiesFile, objectAdapterName, null);
      }

   public RelayCommunicatorManagerImpl(final String applicationName,
                                       final String icePropertiesFile,
                                       final String objectAdapterName,
                                       final ServantFactory servantFactory)
      {
      super(applicationName, icePropertiesFile, objectAdapterName);
      this.servantFactory = servantFactory;

      // Add a communicator creation listener which adds the MyUserConnectionEventListener.  That
      // UserConnectionEventListener is responsible for creating the servants (which it delegates to the ServantFactory)
      // and registering them with the relay.
      addTerkCommunicatorCreationEventListener(
            new TerkCommunicatorCreationEventAdapater()
            {
            public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
               {
               final RelayCommunicator relayCommunicator = (RelayCommunicator)terkCommunicator;

               // register my listener which creates and registers the servants
               relayCommunicator.addUserConnectionEventListener(new MyUserConnectionEventListener());
               }
            });
      }

   public RelayCommunicator getRelayCommunicator()
      {
      return (RelayCommunicator)getTerkCommunicator();
      }

   protected void createCommunicatorAsynchronously(final String applicationName, final String icePropertiesFile, final String objectAdapterName, final Collection<TerkCommunicatorCreationEventListener> terkCommunicatorCreationEventListeners)
      {
      RelayCommunicator.createAsynchronously(applicationName,
                                             icePropertiesFile,
                                             objectAdapterName,
                                             terkCommunicatorCreationEventListeners);
      }

   public boolean isLoggedIn()
      {
      final RelayCommunicator relayCommunicator = getRelayCommunicator();

      return relayCommunicator != null &&
             relayCommunicator.isLoggedIn();
      }

   public boolean login(final String username, final String password)
      {
      if (isSupported())
         {
         final RelayCommunicator relayCommunicator = getRelayCommunicator();

         if (relayCommunicator != null)
            {
            return relayCommunicator.login(username, password);
            }
         else
            {
            throw new IllegalStateException("Failed to log in to the relay since the RelayCommunicator hasn't been created yet.");
            }
         }
      else
         {
         throw new IllegalStateException("Failed to log in to the relay since the RelayCommunicatorManager is not currently supported (see setIsSupported()).");
         }
      }

   public void logout()
      {
      final RelayCommunicator relayCommunicator = getRelayCommunicator();

      if (relayCommunicator != null)
         {
         relayCommunicator.logout();
         }
      else
         {
         LOG.debug("The RelayCommunicator is null, so I won't try to logout.");
         }
      }

   private final class MyUserConnectionEventListener extends UserConnectionEventAdapter
      {
      public void handleRelayLoginEvent()
         {
         if (servantFactory != null)
            {
            final Servants servants = servantFactory.createServants(getTerkCommunicator());

            // register callbacks and secondary servants
            try
               {
               final RelayCommunicator relayCommunicator = getRelayCommunicator();

               // register the main servant proxy and the command controller servant proxy with the relay
               relayCommunicator.registerCallbacks(servants.getMainServantProxy(),
                                                   servants.getConnectionEventHandlerProxy());

               // register the secondary servant proxies with the relay
               final Set<ObjectPrx> secondaryServantProxies = servants.getSecondaryServantProxies();
               if ((secondaryServantProxies != null) && (!secondaryServantProxies.isEmpty()))
                  {
                  relayCommunicator.registerProxies(secondaryServantProxies);
                  }
               }
            catch (RegistrationException e)
               {
               LOG.error("RegistrationException while trying to register the servants", e);
               logout();
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to register the servants", e);
               logout();
               }
            }
         else
            {
            if (LOG.isInfoEnabled())
               {
               LOG.info("RelayCommunicatorManagerImpl$MyUserConnectionEventListener.handleRelayLoginEvent(): " +
                        "the servant factory is null, so no callbacks or servant proxies will be registered with the relay.");
               }
            }
         }

      public void handleForcedLogoutNotificationEvent()
         {
         logout();
         }
      }
   }