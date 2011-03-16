using TeRK.services;

namespace TeRK.components.services
   {
   public abstract class AnalogInputsService : Service
      {
      public const string TYPE_ID = "::TeRK::AnalogInController";

      public string getTypeId()
         {
         return TYPE_ID;
         }

      /** Returns the state of the analog inputs. */
      public abstract AnalogInState getAnalogInState();
      }
   }