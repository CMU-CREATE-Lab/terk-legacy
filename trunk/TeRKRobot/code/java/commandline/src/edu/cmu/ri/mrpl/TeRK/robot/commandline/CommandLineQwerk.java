package edu.cmu.ri.mrpl.TeRK.robot.commandline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineClientPrx;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrx;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrxHelper;
import edu.cmu.ri.mrpl.TeRK.communicator.directconnect.DirectConnectCommunicator;
import edu.cmu.ri.mrpl.TeRK.communicator.relay.RelayCommunicator;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class CommandLineQwerk
   {
   private static final Logger LOG = Logger.getLogger(CommandLineQwerk.class);
   private static final String APPLICATION_NAME = "Command Line Qwerk";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/robot/commandline/CommandLineQwerk.relay.ice.properties";
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/robot/commandline/CommandLineQwerk.direct-connect.ice.properties";
   private static final String RELAY_OBJECT_ADAPTER_NAME = "Robot.Client";
   private static final String DIRECT_CONNECT_OBJECT_ADAPTER_NAME = "Robot.Server";
   private static final String QUIT_COMMAND = "/quit";

   private final RelayCommunicator relayCommunicator;
   private final DirectConnectCommunicator directConnectCommunicator;

   private CommandLineQwerk()
      {
      relayCommunicator = new RelayCommunicator(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, RELAY_OBJECT_ADAPTER_NAME);
      directConnectCommunicator = new DirectConnectCommunicator(APPLICATION_NAME,
                                                                ICE_DIRECT_CONNECT_PROPERTIES_FILE,
                                                                DIRECT_CONNECT_OBJECT_ADAPTER_NAME);
      }

   public static void main(final String[] args)
      {
      CommandLineQwerk app = null;

      try
         {
         app = new CommandLineQwerk();
         }
      catch (Exception e)
         {
         // inform the user and then exit
         System.out.println("Sorry, a connection could not be established with the relay.  The application will now exit.");
         System.exit(0);
         }

      app.run();
      }

   private void run()
      {
      // start the relay communicator
      final Thread relayCommunicatorStarter = new Thread(
            new Runnable()
            {
            public void run()
               {
               relayCommunicator.waitForShutdown();
               }
            });
      relayCommunicatorStarter.start();

      // start the direct connect communicator
      final Thread directConnectCommunicatorStarter = new Thread(
            new Runnable()
            {
            public void run()
               {
               directConnectCommunicator.waitForShutdown();
               }
            });
      directConnectCommunicatorStarter.start();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

      while (true)
         {
         LOG.info("Please log in to the relay....");

         try
            {
            final String id;
            print("robot id: ");
            id = in.readLine();

            final String pw;
            print("password: ");
            pw = in.readLine();

            final boolean loginWasSuccessful = relayCommunicator.login(id, pw);

            if (loginWasSuccessful)
               {
               break;
               }
            else
               {
               print("Login failed");
               }
            }
         catch (IOException ex)
            {
            ex.printStackTrace();
            }
         }

      final CommandLineQwerkServant commandLineQwerkServant = new CommandLineQwerkServant();

      LOG.debug("Creating servant proxy for relay communicator...");
      final ObjectPrx servantProxyForRelay = relayCommunicator.createServantProxy(commandLineQwerkServant);
      final SimpleCommandLineQwerkPrx qwerkServantPrxForRelay = SimpleCommandLineQwerkPrxHelper.uncheckedCast(servantProxyForRelay);
      LOG.debug("Done creating servant proxy for relay communicator! [" + Util.identityToString(qwerkServantPrxForRelay.ice_getIdentity()) + "]");

      LOG.debug("Creating servant proxy for direct connect communicator...");
      final ObjectPrx servantProxyForDirectConnect = directConnectCommunicator.createServantProxy(commandLineQwerkServant);
      final SimpleCommandLineQwerkPrx qwerkServantPrxForDirectConnect = SimpleCommandLineQwerkPrxHelper.uncheckedCast(servantProxyForDirectConnect);
      LOG.debug("Done creating servant proxy for direct connect communicator! [" + Util.identityToString(qwerkServantPrxForDirectConnect.ice_getIdentity()) + "]");

      try
         {
         relayCommunicator.registerCallbacks(qwerkServantPrxForRelay, qwerkServantPrxForRelay);
         }
      catch (RegistrationException e)
         {
         LOG.error("RegistrationException while trying to register the callbacks", e);
         }

      menu();

      try
         {
         String line;

         do
            {
            prompt();
            line = in.readLine();
            if (line == null)
               {
               break;
               }
            else if (line.startsWith("/"))
               {
               if (!QUIT_COMMAND.equals(line))
                  {
                  menu();
                  }
               }
            else if (line.length() > 0)
               {
               final Collection<SimpleCommandLineClientPrx> terkClientProxies = commandLineQwerkServant.getTerkClientProxies();
               LOG.debug("Broadcasting message to " + terkClientProxies.size() + " client(s)");
               for (final SimpleCommandLineClientPrx simpleTerkClientPrx : terkClientProxies)
                  {
                  LOG.debug("   Sending message to client [" + Util.identityToString(simpleTerkClientPrx.ice_getIdentity()) + "]");
                  simpleTerkClientPrx.printMessage(line);
                  }
               }
            }
         while (!QUIT_COMMAND.equals(line) && !commandLineQwerkServant.wasLogoutForced());
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

   private void prompt()
      {
      print("==> ");
      }

   private void println(final String str)
      {
      System.out.println(str);
      }

   private void print(final String str)
      {
      System.out.print(str);
      System.out.flush();
      }

   private void menu()
      {
      println("MENU -----------------------------");
      println("<anything>   send message to client");
      println("/quit        quit");
      println("----------------------------------");
      }
   }
