package edu.cmu.ri.mrpl.TeRK.client.commandline.actions;

import edu.cmu.ri.mrpl.TeRK.commandline.SimpleCommandLineQwerkPrx;
import edu.cmu.ri.mrpl.TeRK.communicator.TerkCommunicator;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public abstract class AbstractAction implements Action
   {
   private final CommunicationHelper communicationHelper;

   public AbstractAction(final CommunicationHelper helper)
      {
      communicationHelper = helper;
      }

   protected final boolean isCommunicatorRunning()
      {
      return communicationHelper.isCommunicatorRunning();
      }

   protected final SimpleCommandLineQwerkPrx getSimpleCommandLineQwerkPrx()
      {
      return communicationHelper.getSimpleCommandLineQwerkPrx();
      }

   protected final void setSimpleCommandLineQwerkPrx(final SimpleCommandLineQwerkPrx SimpleCommandLineQwerkPrx)
      {
      communicationHelper.setSimpleCommandLineQwerkPrx(SimpleCommandLineQwerkPrx);
      }

   protected final TerkCommunicator getTerkCommunicator()
      {
      return communicationHelper.getTerkCommunicator();
      }

   protected final void println(final String str)
      {
      System.out.println(str);
      }

   protected final void print(final String str)
      {
      System.out.print(str);
      System.out.flush();
      }
   }
