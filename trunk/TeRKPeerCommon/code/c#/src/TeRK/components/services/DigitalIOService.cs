using TeRK.services;

namespace TeRK.components.services
   {
   public abstract class DigitalIOService : Service
      {
      public const string TYPE_ID = "::TeRK::DigitalOutController";

      public string getTypeId()
         {
         return TYPE_ID;
         }

      /** Execute the given DigitalOutCommand. */
      public abstract void execute(DigitalOutCommand command);

      /** Returns the state of the digital inputs. */
      public abstract DigitalInState getDigitalInState();
      }
   }