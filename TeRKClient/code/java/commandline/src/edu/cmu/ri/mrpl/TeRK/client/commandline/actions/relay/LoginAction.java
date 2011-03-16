package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.relay;

import java.io.BufferedReader;
import java.io.IOException;
import Ice.ObjectPrx;
import edu.cmu.ri.mrpl.TeRK.TerkClientPrx;
import edu.cmu.ri.mrpl.TeRK.TerkClientPrxHelper;
import edu.cmu.ri.mrpl.TeRK.client.commandline.CommandLineClientServant;
import edu.cmu.ri.mrpl.peer.RegistrationException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class LoginAction extends RelayAction
   {
   private static final Logger LOG = Logger.getLogger(LoginAction.class);

   private final BufferedReader in;
   private final CommandLineClientServant commandLineClientServant = new CommandLineClientServant();

   public LoginAction(final RelayCommunicationHelper relayCommunicationHelper, final BufferedReader in)
      {
      super(relayCommunicationHelper);
      this.in = in;
      }

   public void execute()
      {
      // make sure the relay communicator is running
      if (isCommunicatorRunning())
         {
         if (getRelayCommunicator().isLoggedIn())
            {
            println("You are already logged in!");
            }
         else
            {
            println("Please enter your user id and password...");

            try
               {
               final String id;
               print("   user id: ");
               id = in.readLine();

               final String pw;
               print("   password: ");
               pw = in.readLine();

               final boolean loginWasSuccessful = getRelayCommunicator().login(id, pw);

               if (loginWasSuccessful)
                  {
                  try
                     {
                     final ObjectPrx servantProxy = getRelayCommunicator().createServantProxy(commandLineClientServant);
                     final TerkClientPrx terkClientServantPrx = TerkClientPrxHelper.uncheckedCast(servantProxy);

                     getRelayCommunicator().registerCallbacks(terkClientServantPrx, terkClientServantPrx);
                     }
                  catch (RegistrationException e)
                     {
                     LOG.error("RegistrationException while trying to register the callbacks", e);
                     }
                  }
               else
                  {
                  LOG.error("Login failed!");
                  }
               }
            catch (IOException ex)
               {
               ex.printStackTrace();
               }
            }
         }
      else
         {
         println("Relay communicator is not running, so you can't log in.");
         }
      }

   public CommandLineClientServant getCommandLineClientServant()
      {
      return commandLineClientServant;
      }
   }
