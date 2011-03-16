namespace TeRK.components.services
   {
   internal class AnalogInputsServiceImpl : AnalogInputsService
      {
      private readonly QwerkPrx qwerkPrx;

      internal AnalogInputsServiceImpl(QwerkPrx qwerkPrx)
         {
         this.qwerkPrx = qwerkPrx;
         }

      public override AnalogInState getAnalogInState()
         {
         return qwerkPrx.getState().analogIn;
         }
      }
   }