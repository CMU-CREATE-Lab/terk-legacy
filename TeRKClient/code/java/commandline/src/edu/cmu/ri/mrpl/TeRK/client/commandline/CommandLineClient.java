package edu.cmu.ri.mrpl.TeRK.client.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import Ice.LocalException;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.Action;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.QuitAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.VersionNumberAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect.ConnectToPeerViaDirectConnectAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect.DirectConnectCommunicationHelper;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect.GetRandomNumberFromRobotViaDirectConnect;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect.SendMessageToRobotViaDirectConnect;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.ConnectToPeerViaRelayAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.DisconnectFromPeerViaRelayAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.DisconnectFromPeersViaRelayAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.GetRandomNumberFromRobotViaRelay;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.ListAvailablePeersAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.ListCurrentlyConnectedPeersAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.LoginAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.LogoutAction;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.RelayCommunicationHelper;
import edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay.SendMessageToRobotViaRelay;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventAdapater;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicatorCreationEventListener;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.TeRK.servants.ServantFactory;
import edu.cmu.ri.mrpl.TeRK.servants.Servants;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class CommandLineClient
   {
   private static final Logger LOG = Logger.getLogger(CommandLineClient.class);
   private static final String APPLICATION_NAME = "Command Line Client";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/commandline/CommandLineClient.relay.ice.properties";
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/commandline/CommandLineClient.direct-connect.ice.properties";
   private static final String OBJECT_ADAPTER_NAME = "Teleop.Client";
   private static final String QUIT_COMMAND = "/quit";

   private RelayCommunicator relayCommunicator;
   private DirectConnectCommunicator directConnectCommunicator;
   private final Map<String, Action> actionMap = new HashMap<String, Action>();
   private SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrxForRelay;
   private SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrxForDirectConnect;
   private final LoginAction loginAction;

   private final RelayCommunicationHelper relayCommunicationHelper =
         new RelayCommunicationHelper()
         {
         public boolean isCommunicatorRunning()
            {
            return isRelayCommunicatorRunning();
            }

         public SimpleCommandLineQwerkPrx getSimpleCommandLineQwerkPrx()
            {
            return SimpleCommandLineQwerkPrxForRelay;
            }

         public void setSimpleCommandLineQwerkPrx(final SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrx)
            {
            SimpleCommandLineQwerkPrxForRelay = SimpleCommandLineQwerkPrx;
            }

         public TerkCommunicator getTerkCommunicator()
            {
            return getRelayCommunicator();
            }

         public RelayCommunicator getRelayCommunicator()
            {
            return relayCommunicator;
            }
         };

   private final DirectConnectCommunicationHelper directConnectCommunicationHelper =
         new DirectConnectCommunicationHelper()
         {
         public boolean isCommunicatorRunning()
            {
            return isDirectConnectCommunicatorRunning();
            }

         public SimpleCommandLineQwerkPrx getSimpleCommandLineQwerkPrx()
            {
            return SimpleCommandLineQwerkPrxForDirectConnect;
            }

         public void setSimpleCommandLineQwerkPrx(final SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrx)
            {
            SimpleCommandLineQwerkPrxForDirectConnect = SimpleCommandLineQwerkPrx;
            }

         public TerkCommunicator getTerkCommunicator()
            {
            return getDirectConnectCommunicator();
            }

         public DirectConnectCommunicator getDirectConnectCommunicator()
            {
            return directConnectCommunicator;
            }
         };

   public static void main(final String[] args)
      {
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      new CommandLineClient(in).run(in);
      }

   private static void prompt()
      {
      System.out.print("==> ");
      }

   private static void menu()
      {
      System.out.println("RELAY COMMANDS -----------------------------");
      System.out.println("i         log in to the relay");
      System.out.println("o         log out from the relay");
      System.out.println("?         list available robots");
      System.out.println("!         list currently connected robots");
      System.out.println("c         connect to robot (via relay)");
      System.out.println("d         disconnect from robot");
      System.out.println("D         disconnect from all robots");
      System.out.println("s         send message to robot");
      System.out.println("r         get a random number from the robot");
      System.out.println("u         shutdown the relay communicator");
      System.out.println("");
      System.out.println("DIRECT-CONNECT COMMANDS --------------------");
      System.out.println("C         connect to robot");
      System.out.println("S         send message to robot");
      System.out.println("R         get a random number from the robot");
      System.out.println("U         shutdown the direct-connect communicator");
      System.out.println("");
      System.out.println("GENERIC COMMANDS ---------------------------");
      System.out.println("v         print version info");
      System.out.println("/quit     quit");
      System.out.println("");
      System.out.println("--------------------------------------------");
      }

   private CommandLineClient(final BufferedReader in)
      {
      loginAction = new LoginAction(relayCommunicationHelper, in);
      final LogoutAction logoutAction = new LogoutAction(relayCommunicationHelper);
      actionMap.put("i", loginAction);
      actionMap.put("o", logoutAction);
      actionMap.put("?", new ListAvailablePeersAction(relayCommunicationHelper));
      actionMap.put("!", new ListCurrentlyConnectedPeersAction(relayCommunicationHelper));
      actionMap.put("c", new ConnectToPeerViaRelayAction(relayCommunicationHelper, in));
      actionMap.put("d", new DisconnectFromPeerViaRelayAction(relayCommunicationHelper, in));
      actionMap.put("D", new DisconnectFromPeersViaRelayAction(relayCommunicationHelper));
      actionMap.put("s", new SendMessageToRobotViaRelay(relayCommunicationHelper, in));
      actionMap.put("r", new GetRandomNumberFromRobotViaRelay(relayCommunicationHelper, in));

      actionMap.put("C", new ConnectToPeerViaDirectConnectAction(directConnectCommunicationHelper, in));
      actionMap.put("S", new SendMessageToRobotViaDirectConnect(directConnectCommunicationHelper, in));
      actionMap.put("R", new GetRandomNumberFromRobotViaDirectConnect(directConnectCommunicationHelper, in));

      actionMap.put("v", new VersionNumberAction());
      actionMap.put(QUIT_COMMAND, new QuitAction());

      actionMap.put("u",
                    new Action()
                    {
                    public void execute()
                       {
                       if (relayCommunicator != null)
                          {
                          System.out.println("Shutting down the relay communicator...");
                          if (relayCommunicator.isLoggedIn())
                             {
                             System.out.print("   logging out first...");
                             logoutAction.execute();
                             }
                          relayCommunicator.shutdown();
                          relayCommunicator = null;
                          SimpleCommandLineQwerkPrxForRelay = null;
                          System.out.println("Shutdown successful!");
                          }
                       else
                          {
                          System.out.println("Nothing to do since the relay communicator was never started or has already been shutdown.");
                          }
                       }
                    });

      actionMap.put("U",
                    new Action()
                    {
                    public void execute()
                       {
                       if (directConnectCommunicator != null)
                          {
                          System.out.println("Shutting down the direct-connect communicator...");
                          directConnectCommunicator.shutdown();
                          directConnectCommunicator = null;
                          SimpleCommandLineQwerkPrxForDirectConnect = null;
                          System.out.println("Shutdown successful!");
                          }
                       else
                          {
                          System.out.println("Nothing to do since the direct-connect communicator was never started or has already been shutdown.");
                          }
                       }
                    });
      }

   private void run(final BufferedReader in)
      {
      menu();

      try
         {
         String line;
         do
            {
            prompt();
            line = in.readLine();

            final Action action = actionMap.get(line);
            if (action != null)
               {
               action.execute();
               }
            else
               {
               if (line != null && line.length() > 0)
                  {
                  System.out.println("Unknown command '" + line + "'");
                  System.out.println("");
                  menu();
                  }
               }
            }
         while (!QUIT_COMMAND.equals(line) && !loginAction.getCommandLineClientServant().wasLogoutForced());
         }
      catch (IOException ex)
         {
         ex.printStackTrace();
         }
      catch (LocalException ex)
         {
         ex.printStackTrace();
         }
      finally
         {
         if (relayCommunicator != null)
            {
            relayCommunicator.shutdown();
            }
         if (directConnectCommunicator != null)
            {
            directConnectCommunicator.shutdown();
            }
         }

      System.exit(0);
      }

   private boolean isRelayCommunicatorRunning()
      {
      if (relayCommunicator == null)
         {
         RelayCommunicator.createAsynchronously(APPLICATION_NAME,
                                                ICE_RELAY_PROPERTIES_FILE,
                                                OBJECT_ADAPTER_NAME,
                                                new TerkCommunicatorCreationEventAdapater()
                                                {
                                                public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
                                                   {
                                                   CommandLineClient.this.relayCommunicator = (RelayCommunicator)terkCommunicator;
                                                   }

                                                public void afterFailedConstruction()
                                                   {
                                                   System.out.println("Sorry, a connection could not be established with the relay.");
                                                   }
                                                });

         // give the relay communicator time to start up
         try
            {
            Thread.sleep(2000);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }

      return relayCommunicator != null;
      }

   private boolean isDirectConnectCommunicatorRunning()
      {
      if (directConnectCommunicator == null)
         {
         final Collection<TerkCommunicatorCreationEventListener> listeners = new HashSet<TerkCommunicatorCreationEventListener>();
         listeners.add(
               new TerkCommunicatorCreationEventAdapater()
               {
               public void afterSuccessfulConstruction(final TerkCommunicator terkCommunicator)
                  {
                  CommandLineClient.this.directConnectCommunicator = (DirectConnectCommunicator)terkCommunicator;
                  }

               public void afterFailedConstruction()
                  {
                  System.out.println("Sorry, a DirectConnectCommunicator could not be created.");
                  }
               });
         DirectConnectCommunicator.createAsynchronously(APPLICATION_NAME,
                                                        ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                        OBJECT_ADAPTER_NAME,
                                                        listeners,
                                                        new MyDirectConnetServantFactory());

         // give the direct connect communicator time to start up
         try
            {
            Thread.sleep(2000);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }

      return directConnectCommunicator != null;
      }

   private static class MyDirectConnetServantFactory implements ServantFactory
      {
      public Servants createServants(final TerkCommunicator terkCommunicator)
         {
         return null;
         }
      }
   }
