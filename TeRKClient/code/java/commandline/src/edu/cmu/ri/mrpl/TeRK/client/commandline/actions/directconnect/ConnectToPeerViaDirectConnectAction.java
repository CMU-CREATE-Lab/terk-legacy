package edu.cmu.ri.mrpl.TeRK.client.commandline.actions.directconnect;

import java.io.BufferedReader;
import java.io.IOException;
import Ice.Identity;
import Ice.LocalException;
import Ice.ObjectPrx;
import Ice.Util;
import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrxHelper;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public class ConnectToPeerViaDirectConnectAction extends DirectConnectAction
   {
   private static final Logger LOG = Logger.getLogger(ConnectToPeerViaDirectConnectAction.class);

   private static final String SIMPLE_QWERK_ICE_ID = "::TeRK::commandline::SimpleCommandLineQwerk";

   private final BufferedReader in;

   public ConnectToPeerViaDirectConnectAction(final DirectConnectCommunicationHelper directConnectCommunicationHelper, final BufferedReader in)
      {
      super(directConnectCommunicationHelper);
      this.in = in;
      }

   public void execute()
      {
      if (isCommunicatorRunning())
         {
         try
            {
            final String host;
            print("   host: ");
            host = in.readLine();

            final Identity identity = new Identity(SIMPLE_QWERK_ICE_ID, "");
            try
               {
               final ObjectPrx objectPrx = getDirectConnectCommunicator().getPeerProxy(host, identity);

               if (objectPrx != null)
                  {
                  setSimpleCommandLineQwerkPrx(SimpleCommandLineQwerkPrxHelper.checkedCast(objectPrx));
                  if (getSimpleCommandLineQwerkPrx() != null)
                     {
                     println("   Direct connection successful! (" + Util.identityToString(getSimpleCommandLineQwerkPrx().ice_getIdentity()) + ")");
                     }
                  else
                     {
                     println("   Direct connection failed (returned proxy failed checked cast to SimpleCommandLineQwerkPrx)!");
                     }
                  }
               else
                  {
                  println("getPeerProxy(" + Util.identityToString(identity) + ":" + host + ") returned a null peer.  Bummer.");
                  }
               }
            catch (LocalException e)
               {
               if (LOG.isEnabledFor(Level.ERROR))
                  {
                  LOG.error("LocalException while trying to obtain proxy with identity [" + Util.identityToString(identity) + "] from host [" + host + "]", e);
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
         println("Direct-connect communicator is not running, so you can't connect to a peer.");
         }
      }
   }
