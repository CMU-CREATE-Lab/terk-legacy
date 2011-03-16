using System;

namespace TeRK.components.services
   {
   internal class DigitalIOServiceImpl : DigitalIOService
      {
      private readonly DigitalOutControllerPrx proxy;
      private readonly QwerkPrx qwerkPrx;

      internal DigitalIOServiceImpl(DigitalOutControllerPrx proxy, QwerkPrx qwerkPrx)
         {
         this.proxy = proxy;
         this.qwerkPrx = qwerkPrx;
         }

      public override void execute(DigitalOutCommand command)
         {
         try
            {
            proxy.execute(command);
            }
         catch (DigitalOutCommandException e)
            {
            // todo: allow this to propogate to the caller
            Console.Error.WriteLine("ERROR: Exception while excecuting a command: {0}", e.reason);
            }
         }

      public override DigitalInState getDigitalInState()
         {
         return qwerkPrx.getState().digitalIn;
         }
      }
   }