package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class GetRandomNumberFromRobotViaDirectConnect extends DirectConnectAction
   {
   private static final Logger LOG = Logger.getLogger(GetRandomNumberFromRobotViaDirectConnect.class);

   private final BufferedReader in;

   public GetRandomNumberFromRobotViaDirectConnect(final DirectConnectCommunicationHelper directConnectCommunicationHelper, final BufferedReader in)
      {
      super(directConnectCommunicationHelper);
      this.in = in;
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         if (getSimpleCommandLineQwerkPrx() != null)
            {
            try
               {
               print("   upper bound (exclusive): ");
               final String upperBound = in.readLine();
               if (upperBound == null)
                  {
                  println("Random number upper bound was null, aborting.");
                  }
               else
                  {
                  try
                     {
                     final int randomNumber = getSimpleCommandLineQwerkPrx().getRandomInt(Integer.parseInt(upperBound));
                     println("robot returned randomNumber = [" + randomNumber + "]");
                     }
                  catch (Exception e)
                     {
                     LOG.error("Exception while calling printMessage", e);
                     }
                  }
               }
            catch (IOException e)
               {
               LOG.error("IOException while reading user input", e);
               }
            }
         else
            {
            println("SimpleCommandLineQwerkPrx is null (you need to connect to a peer first)");
            }
         }
      else
         {
         println("Direct-connect communicator is not running, so you can't request a random number.");
         }
      }
   }
