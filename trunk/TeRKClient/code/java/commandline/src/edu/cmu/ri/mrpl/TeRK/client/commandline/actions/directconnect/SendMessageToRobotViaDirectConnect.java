package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SendMessageToRobotViaDirectConnect extends DirectConnectAction
   {
   private static final Logger LOG = Logger.getLogger(SendMessageToRobotViaDirectConnect.class);

   private final BufferedReader in;

   public SendMessageToRobotViaDirectConnect(final DirectConnectCommunicationHelper directConnectCommunicationHelper, final BufferedReader in)
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
               print("   message to send: ");
               final String messageToRobot = in.readLine();
               if (messageToRobot == null)
                  {
                  println("Message to send to robot was null, aborting.");
                  }
               else
                  {
                  try
                     {
                     getSimpleCommandLineQwerkPrx().printMessage(messageToRobot);
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
         println("Direct-connect communicator is not running, so you can't send a message.");
         }
      }
   }
