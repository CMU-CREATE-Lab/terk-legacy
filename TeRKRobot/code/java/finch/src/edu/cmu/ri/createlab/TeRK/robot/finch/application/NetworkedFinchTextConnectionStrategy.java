package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import Ice.ObjectPrx;
import edu.cmu.ri.createlab.TeRK.communicator.manager.relay.RelayCommunicatorManager;
import edu.cmu.ri.createlab.commandline.BaseCommandLineApplication;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.ConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerAccessException;
import edu.cmu.ri.mrpl.peer.PeerAccessLevel;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventAdapter;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import edu.cmu.ri.mrpl.peer.PeerUnavailableException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class NetworkedFinchTextConnectionStrategy extends NetworkedFinchConnectionStrategy
   {
   private static final Logger LOG = Logger.getLogger(NetworkedFinchTextConnectionStrategy.class);

   private static final String CONNECT_COMMAND = "connect";

   private final MyCommandLineApplication commandLineApplication;

   private String peerId = null;
   private String username = null;
   private String password = null;

   private Mode mode = Mode.STEP_0_CHOOSE_CONNECTION_MODE;

   public NetworkedFinchTextConnectionStrategy()
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      commandLineApplication = new MyCommandLineApplication(in);

      getDirectConnectCommunicatorManager().addTerkCommunicatorCreationEventListener(new MyDirectConnectCommunicatorCreationEventListener());
      getRelayCommunicatorManager().addTerkCommunicatorCreationEventListener(new MyRelayCommunicatorCreationEventListener());
      }

   public void connect()
      {
      commandLineApplication.executeCommand(CONNECT_COMMAND);
      }

   private synchronized Mode getMode()
      {
      return mode;
      }

   private synchronized void setMode(final Mode mode)
      {
      if (LOG.isTraceEnabled())
         {
         LOG.trace("SETTING MODE TO [" + mode + "]");
         }
      this.mode = mode;
      }

   private final class MyCommandLineApplication extends BaseCommandLineApplication
      {
      private MyCommandLineApplication(final BufferedReader in)
         {
         super(in);

         registerAction(CONNECT_COMMAND,
                        new Runnable()
                        {
                        public void run()
                           {
                           executeWorkflow();
                           }
                        });
         }

      private boolean chooseConnectionMode()
         {
         final Integer choice = readInteger("Connect via direct-connect (1) or relay (2): ");
         if (choice == null)
            {
            println("Invalid choice");
            return false;
            }

         switch (choice)
            {
            case 1:
               setMode(Mode.DIRECT_CONNECT_STEP_1_CREATE_COMMUNICATOR);
               break;
            case 2:
               setMode(Mode.RELAY_STEP_1_CREATE_COMMUNICATOR);
               break;
            default:
               println("Invalid choice");
               return false;
            }

         return true;
         }

      private boolean doCreateDirectConnectCommunicator()
         {
         // see whether the communicator needs to be created
         if (getDirectConnectCommunicatorManager().isCreated())
            {
            setMode(Mode.DIRECT_CONNECT_STEP_3_GET_TARGET_HOSTNAME);
            }
         else
            {
            setMode(Mode.DIRECT_CONNECT_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION);
            getDirectConnectCommunicatorManager().createCommunicator();
            }
         return true;
         }

      private boolean doPromptForDirectConnectTargetHostname()
         {
         peerId = readString("Hostname [hit ENTER to abort]: ");
         if (peerId == null || peerId.length() == 0)
            {
            println("Aborting direct connect.");
            setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
            return false;
            }

         setMode(Mode.DIRECT_CONNECT_STEP_4_ESTABLISH_PEER_CONNECTION);
         return true;
         }

      private boolean doEstablishDirectConnection()
         {
         if (getDirectConnectCommunicatorManager().isCreated())
            {
            try
               {
               setMode(Mode.DIRECT_CONNECT_STEP_5_WAIT_FOR_PEER_TO_ACCEPT);
               getDirectConnectCommunicatorManager().getTerkCommunicator().connectToPeer(peerId);
               return true;
               }
            catch (PeerAccessException e)
               {
               LOG.error("PeerAccessException while trying to connect to [" + peerId + "]", e);
               }
            catch (PeerUnavailableException e)
               {
               LOG.error("PeerUnavailableException while trying to connect to [" + peerId + "]", e);
               }
            catch (PeerConnectionFailedException e)
               {
               LOG.error("PeerConnectionFailedException while trying to connect to [" + peerId + "]", e);
               }
            catch (DuplicateConnectionException e)
               {
               LOG.error("DuplicateConnectionException while trying to connect to [" + peerId + "]", e);
               }
            }
         setMode(Mode.DIRECT_CONNECT_ERROR_2_PEER_CONNECTION_FAILURE);
         return true;
         }

      private boolean doCreateRelayCommunicator()
         {
         // see whether the communicator needs to be created
         if (getRelayCommunicatorManager().isCreated())
            {
            setMode(Mode.RELAY_STEP_3_DETERMINE_WHETHER_USER_IS_LOGGED_IN);
            }
         else
            {
            setMode(Mode.RELAY_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION);
            getRelayCommunicatorManager().createCommunicator();
            }
         return true;
         }

      private boolean doDetermineWhetherUserIsLoggedIntoRelay()
         {
         final RelayCommunicatorManager relayCommunicatorManager = getRelayCommunicatorManager();

         if (relayCommunicatorManager.isCreated())
            {
            final RelayCommunicator relayCommunicator = (RelayCommunicator)relayCommunicatorManager.getTerkCommunicator();
            if (relayCommunicator != null && relayCommunicator.isLoggedIn())
               {
               final String loginChoice = readString("Remain logged in as '" + username + "? [y/n or hit ENTER to abort]': ");
               if (loginChoice == null || loginChoice.length() == 0)
                  {
                  println("Aborting relay connect.");
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  return false;
                  }

               if ("y".equalsIgnoreCase(loginChoice))
                  {
                  setMode(Mode.RELAY_STEP_7_GET_PEER_ID);
                  return true;
                  }
               else if (!"n".equalsIgnoreCase(loginChoice))
                  {
                  println("Invalid response [" + loginChoice + "]");
                  setMode(Mode.RELAY_STEP_3_DETERMINE_WHETHER_USER_IS_LOGGED_IN);
                  return true;
                  }

               // they want to log in as someone else, so first log out
               try
                  {
                  relayCommunicator.logout();
                  }
               catch (Exception e)
                  {
                  LOG.error("Exception while trying to log out", e);
                  println("Exception while trying to log out");
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  return false;
                  }
               }
            setMode(Mode.RELAY_STEP_4_GET_LOGIN_INFO);
            return true;
            }
         else
            {
            setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
            return false;
            }
         }

      private boolean doGetRelayLoginInfo()
         {
         username = readString("Username [hit ENTER to abort]: ");
         if (username == null || username.length() == 0)
            {
            println("Invalid username [" + username + "]");
            setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
            return false;
            }
         password = readString("Password [hit ENTER to abort]: ");
         if (password == null || password.length() == 0)
            {
            println("Invalid password [" + password + "]");
            setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
            return false;
            }

         setMode(Mode.RELAY_STEP_5_LOG_IN);
         return true;
         }

      private boolean doRelayLogin()
         {
         if (getRelayCommunicatorManager().isCreated())
            {
            setMode(Mode.RELAY_STEP_6_WAIT_FOR_LOGIN);
            final RelayCommunicator relayCommunicator = (RelayCommunicator)getRelayCommunicatorManager().getTerkCommunicator();
            relayCommunicator.login(username, password);
            return true;
            }
         setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
         return false;
         }

      private boolean doGetRelayPeerId()
         {
         peerId = readString("Peer ID [hit ENTER to abort]: ");
         if (peerId == null || peerId.length() == 0)
            {
            println("Aborting relay connect.");
            setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
            return false;
            }

         setMode(Mode.RELAY_STEP_8_ESTABLISH_PEER_CONNECTION);
         return true;
         }

      private boolean doConnectToRelayPeer()
         {
         if (getRelayCommunicatorManager().isCreated())
            {
            try
               {
               setMode(Mode.RELAY_STEP_9_WAIT_FOR_PEER_TO_ACCEPT);
               getRelayCommunicatorManager().getTerkCommunicator().connectToPeer(peerId);
               return true;
               }
            catch (PeerAccessException e)
               {
               LOG.error("PeerAccessException while trying to connect to [" + peerId + "]", e);
               }
            catch (PeerUnavailableException e)
               {
               LOG.error("PeerUnavailableException while trying to connect to [" + peerId + "]", e);
               }
            catch (PeerConnectionFailedException e)
               {
               LOG.error("PeerConnectionFailedException while trying to connect to [" + peerId + "]", e);
               }
            catch (DuplicateConnectionException e)
               {
               LOG.error("DuplicateConnectionException while trying to connect to [" + peerId + "]", e);
               }
            }
         setMode(Mode.RELAY_ERROR_3_PEER_CONNECTION_FAILURE);
         return true;
         }

      private void executeWorkflow()
         {
         boolean continueLooping = true;

         while (continueLooping)
            {
            final Mode currentMode = getMode();
            if (LOG.isTraceEnabled())
               {
               LOG.trace("SWITCHING ON MODE = [" + currentMode + "]");
               }
            switch (currentMode)
               {
               // direct connect
               case DIRECT_CONNECT_STEP_1_CREATE_COMMUNICATOR:
                  continueLooping = doCreateDirectConnectCommunicator();
                  break;
               case DIRECT_CONNECT_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION:
                  sleep();
                  break;
               case DIRECT_CONNECT_STEP_3_GET_TARGET_HOSTNAME:
                  continueLooping = doPromptForDirectConnectTargetHostname();
                  break;
               case DIRECT_CONNECT_STEP_4_ESTABLISH_PEER_CONNECTION:
                  continueLooping = doEstablishDirectConnection();
                  break;
               case DIRECT_CONNECT_STEP_5_WAIT_FOR_PEER_TO_ACCEPT:
                  sleep();
                  break;
               case DIRECT_CONNECT_STEP_6_SUCCESS:
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  continueLooping = false;
                  break;

               // direct connect failure modes
               case DIRECT_CONNECT_ERROR_1_COMMUNICATOR_CREATION_FAILURE:
                  println("Failed to create direct-connect communicator.");
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  break;
               case DIRECT_CONNECT_ERROR_2_PEER_CONNECTION_FAILURE:
                  println("Failed to connect to peer [" + peerId + "]");
                  setMode(Mode.DIRECT_CONNECT_STEP_3_GET_TARGET_HOSTNAME);
                  break;

               // relay
               case RELAY_STEP_1_CREATE_COMMUNICATOR:
                  continueLooping = doCreateRelayCommunicator();
                  break;
               case RELAY_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION:
                  sleep();
                  break;
               case RELAY_STEP_3_DETERMINE_WHETHER_USER_IS_LOGGED_IN:
                  continueLooping = doDetermineWhetherUserIsLoggedIntoRelay();
                  break;
               case RELAY_STEP_4_GET_LOGIN_INFO:
                  continueLooping = doGetRelayLoginInfo();
                  break;
               case RELAY_STEP_5_LOG_IN:
                  continueLooping = doRelayLogin();
                  break;
               case RELAY_STEP_6_WAIT_FOR_LOGIN:
                  sleep();
                  break;
               case RELAY_STEP_7_GET_PEER_ID:
                  continueLooping = doGetRelayPeerId();
                  break;
               case RELAY_STEP_8_ESTABLISH_PEER_CONNECTION:
                  continueLooping = doConnectToRelayPeer();
                  break;
               case RELAY_STEP_9_WAIT_FOR_PEER_TO_ACCEPT:
                  sleep();
                  break;
               case RELAY_STEP_10_SUCCESS:
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  continueLooping = false;
                  break;

               // relay failure modes
               case RELAY_ERROR_1_COMMUNICATOR_CREATION_FAILURE:
                  println("Failed to create relay communicator.");
                  setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
                  break;
               case RELAY_ERROR_2_LOGIN_FAILURE:
                  println("Failed to log in to relay with user ID [" + username + "]");
                  setMode(Mode.RELAY_STEP_4_GET_LOGIN_INFO);
                  break;
               case RELAY_ERROR_3_PEER_CONNECTION_FAILURE:
                  println("Failed to connect to peer [" + peerId + "]");
                  setMode(Mode.RELAY_STEP_7_GET_PEER_ID);
                  break;

               // default
               case STEP_0_CHOOSE_CONNECTION_MODE:
               default:
                  continueLooping = chooseConnectionMode();
                  break;
               }
            }
         }

      private void sleep()
         {
         try
            {
            Thread.sleep(50);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }

      protected void menu()
         {
         println("this is the menu");
         }
      }

   private enum Mode
      {
         STEP_0_CHOOSE_CONNECTION_MODE,

         DIRECT_CONNECT_STEP_1_CREATE_COMMUNICATOR,
         DIRECT_CONNECT_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION,
         DIRECT_CONNECT_STEP_3_GET_TARGET_HOSTNAME,
         DIRECT_CONNECT_STEP_4_ESTABLISH_PEER_CONNECTION,
         DIRECT_CONNECT_STEP_5_WAIT_FOR_PEER_TO_ACCEPT,
         DIRECT_CONNECT_STEP_6_SUCCESS,

         DIRECT_CONNECT_ERROR_1_COMMUNICATOR_CREATION_FAILURE,
         DIRECT_CONNECT_ERROR_2_PEER_CONNECTION_FAILURE,

         RELAY_STEP_1_CREATE_COMMUNICATOR,
         RELAY_STEP_2_WAIT_FOR_COMMUNICATOR_CREATION,
         RELAY_STEP_3_DETERMINE_WHETHER_USER_IS_LOGGED_IN,
         RELAY_STEP_4_GET_LOGIN_INFO,
         RELAY_STEP_5_LOG_IN,
         RELAY_STEP_6_WAIT_FOR_LOGIN,
         RELAY_STEP_7_GET_PEER_ID,
         RELAY_STEP_8_ESTABLISH_PEER_CONNECTION,
         RELAY_STEP_9_WAIT_FOR_PEER_TO_ACCEPT,
         RELAY_STEP_10_SUCCESS,

         RELAY_ERROR_1_COMMUNICATOR_CREATION_FAILURE,
         RELAY_ERROR_2_LOGIN_FAILURE,
         RELAY_ERROR_3_PEER_CONNECTION_FAILURE
      }

   private class MyDirectConnectCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         final DirectConnectCommunicator directConnectCommunicator = (DirectConnectCommunicator)terkCommunicator;
         directConnectCommunicator.addPeerConnectionEventListener(new MyDirectConnectPeerConnectionEventAdapter());
         setMode(Mode.DIRECT_CONNECT_STEP_3_GET_TARGET_HOSTNAME);
         }

      public void afterFailedConstruction()
         {
         setMode(Mode.DIRECT_CONNECT_ERROR_1_COMMUNICATOR_CREATION_FAILURE);
         }
      }

   private class MyRelayCommunicatorCreationEventListener extends TerkCommunicatorCreationEventAdapater
      {
      public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
         {
         final RelayCommunicator relayCommunicator = (RelayCommunicator)terkCommunicator;
         relayCommunicator.addConnectionEventListener(new MyRelayConnectionEventListener());
         setMode(Mode.RELAY_STEP_3_DETERMINE_WHETHER_USER_IS_LOGGED_IN);
         }

      public void afterFailedConstruction()
         {
         setMode(Mode.RELAY_ERROR_1_COMMUNICATOR_CREATION_FAILURE);
         }
      }

   private class MyDirectConnectPeerConnectionEventAdapter extends PeerConnectionEventAdapter
      {
      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         LOG.trace("SUCCESSFUL DIRECT-CONNECT PEER CONNECTION");
         setMode(Mode.DIRECT_CONNECT_STEP_6_SUCCESS);
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         LOG.trace("DIRECT-CONNECT PEER DISCONNECTED");
         setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
         }

      public void handlePeerConnectionFailedEvent(final String peerUserId)
         {
         LOG.trace("FAILED DIRECT-CONNECT PEER CONNECTION");
         setMode(Mode.DIRECT_CONNECT_ERROR_2_PEER_CONNECTION_FAILURE);
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         // consider a no-proxy peer a failed connection
         LOG.trace("FAILED DIRECT-CONNECT PEER CONNECTION (NO PROXY)");
         setMode(Mode.DIRECT_CONNECT_ERROR_2_PEER_CONNECTION_FAILURE);
         }
      }

   private class MyRelayConnectionEventListener extends ConnectionEventAdapter
      {
      public void handleForcedLogoutNotificationEvent()
         {
         LOG.trace("FORCED RELAY LOGOUT");
         setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
         }

      public void handleFailedRelayLoginEvent()
         {
         LOG.trace("FAILED RELAY LOGIN");
         setMode(Mode.RELAY_ERROR_2_LOGIN_FAILURE);
         }

      public void handleRelayRegistrationEvent()
         {
         LOG.trace("SUCCESSFUL RELAY REGISTRATION");
         setMode(Mode.RELAY_STEP_7_GET_PEER_ID);
         }

      public void handlePeerConnectedEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel, final ObjectPrx peerObjectProxy)
         {
         LOG.trace("PEER CONNECTED EVENT");
         setMode(Mode.RELAY_STEP_10_SUCCESS);
         }

      public void handlePeerConnectionFailedEvent(final String peerUserId)
         {
         LOG.trace("PEER CONNECTION FAILED EVENT");
         setMode(Mode.RELAY_ERROR_3_PEER_CONNECTION_FAILURE);
         }

      public void handlePeerConnectedNoProxyEvent(final String peerUserId, final PeerAccessLevel peerAccessLevel)
         {
         // consider a no-proxy peer a failed connection
         LOG.trace("PEER CONNECTION FAILED EVENT (NO PROXY)");
         setMode(Mode.RELAY_ERROR_3_PEER_CONNECTION_FAILURE);
         }

      public void handlePeerDisconnectedEvent(final String peerUserId)
         {
         LOG.trace("PEER DISCONNECTED EVENT");
         setMode(Mode.STEP_0_CHOOSE_CONNECTION_MODE);
         }
      }
   }