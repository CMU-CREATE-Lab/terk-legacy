public class MyFirstWeatherRobot extends MyFirstRobot250
   {
   public static void main(String[] args)
      {
      // Specify whether to use fake or real RSS Readers by calling either useFakeRSSReaders() or useRealRSSReaders()
      useFakeRSSReaders();

      while (true)
         {
         waitForPlayButton();

         int numberOfNewEvents = getNumberOfNewSevereWeatherEvents();
         System.out.println("Events: " + numberOfNewEvents);

         if (numberOfNewEvents == 0)
            {
            play("NoWeatherEvents");
            }
         else if (numberOfNewEvents <= 5)
            {
            play("AFewWeatherEvents");
            }
         else
            {
            play("LotsOfWeatherEvents");
            }

         // sleep for this many seconds, unless the stop button is pressed
         sleepUnlessStopButton(3);
         }
      }
   }