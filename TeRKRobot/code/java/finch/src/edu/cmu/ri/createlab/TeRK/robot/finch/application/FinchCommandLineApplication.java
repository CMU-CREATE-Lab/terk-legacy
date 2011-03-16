package edu.cmu.ri.createlab.TeRK.robot.finch.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.PropertyResourceBundle;
import edu.cmu.ri.createlab.commandline.BaseCommandLineApplication;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>FinchGUIApplication</code> provides a basic framework for command line Finch applications.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class FinchCommandLineApplication extends BaseFinchApplication
   {
   private static final Logger LOG = Logger.getLogger(FinchCommandLineApplication.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(FinchCommandLineApplication.class.getName());

   private final MyCommandLineApplication commandLineApplication;

   FinchCommandLineApplication()
      {
      LOG.debug("FinchCommandLineApplication.FinchCommandLineApplication()");
      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      commandLineApplication = new MyCommandLineApplication(in);
      }

   protected final void runCommandLineApplication()
      {
      commandLineApplication.run();
      }

   private final class MyCommandLineApplication extends BaseCommandLineApplication
      {
      private static final String MENU_PADDING = "         ";

      private MyCommandLineApplication(final BufferedReader in)
         {
         super(in);
         registerAction(RESOURCES.getString("connect.command"),
                        new Runnable()
                        {
                        public void run()
                           {
                           if (isConnected())
                              {
                              println("You are already connected to a finch.  Please disconnect first if you want to connect to a different one.");
                              }
                           else
                              {
                              connect();
                              }
                           }
                        });
         registerAction(RESOURCES.getString("disconnect.command"),
                        new Runnable()
                        {
                        public void run()
                           {
                           if (isConnected())
                              {
                              disconnect();
                              }
                           }
                        });
         registerAction(QUIT_COMMAND,
                        new Runnable()
                        {
                        public void run()
                           {
                           if (isConnected())
                              {
                              disconnect();
                              }
                           println("Bye!");
                           }
                        });
         }

      protected void menu()
         {
         println("COMMANDS -----------------------------------");
         println(RESOURCES.getString("connect.command") + MENU_PADDING + RESOURCES.getString("connect.description"));
         println(RESOURCES.getString("disconnect.command") + MENU_PADDING + RESOURCES.getString("disconnect.description"));
         println("");
         println(RESOURCES.getString("quit.command") + MENU_PADDING + RESOURCES.getString("quit.description"));
         println("--------------------------------------------");
         }
      }
   }