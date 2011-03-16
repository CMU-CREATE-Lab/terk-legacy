public class MyFirstNewsHeadlineRobot extends MyFirstRobot250
   {
   public static void main(String[] args)
      {
      // Specify whether to use fake or real RSS Readers by calling either useFakeRSSReaders() or useRealRSSReaders()
      useFakeRSSReaders();

      while (true)
         {
         waitForPlayButton();

         int numberOfNewHeadlines = getNumberOfNewViolenceRelatedHeadlines();

         if (numberOfNewHeadlines == 0)
            {
            play("NoHeadlines");
            }
         else if (numberOfNewHeadlines <= 5)
            {
            play("AFewHeadlines");
            }
         else
            {
            play("GivePeaceAChance");
            }

         // sleep for this many seconds, unless the stop button is pressed
         sleepUnlessStopButton(5);
         }
      }
   }