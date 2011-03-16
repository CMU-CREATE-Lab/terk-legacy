/* Example file which reads the contents of the Yahoo top stories
* feed and converts them to speech.  It then speaks these over the
* robot's speakers.
*/

import RSSReaders.RSSReader;
import RobotClient.SimpleRobotClient;

public class RSS_TTS_Yahoo_Example
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      SimpleRobotClient myRobot = new SimpleRobotClient();

      // Write some code here!

      // It is strongly recommended that you use the following line.
      // This method blocks all further program execution until after the 'Start'
      // button has been pressed.
      myRobot.waitForPlay();

      // Set the URL that we're reading in to
      RSSReader yahooFeed = new RSSReader("http://rss.news.yahoo.com/rss/topstories");

      String title;

      while (true)
         {

         // Synthesize and speak the feed's title and description
         myRobot.saySomething("space Now reading from the following Feed:" + yahooFeed.getFeedTitle() + ".  The feed is concerned with " + yahooFeed.getFeedDescription());

         // Print out to the text box the details which were saved as a wav file
         myRobot.writeToTextBox("Now reading from the following Feed:");
         myRobot.writeToTextBox(yahooFeed.getFeedTitle());
         myRobot.writeToTextBox("Description:");
         myRobot.writeToTextBox(yahooFeed.getFeedDescription());
         myRobot.sleepUnlessStop(5000);

         // Get the total number of entries in the feed
         int totalEntries = yahooFeed.getEntryCount();

         // Loop through those entries and read/speak the title of each entry
         for (int i = 0; i < totalEntries; i++)
            {
            // Get the title of entry number i
            title = yahooFeed.getEntryTitle(i);

            // Convert the story to speech and play it over the robot's speaker
            myRobot.saySomething("space Story number " + i + ". " + title + ". ");
            // Also print the title to the text box
            myRobot.writeToTextBox("Story number " + i + ".  " + title + ". ");

            // Delay 7 seconds per loop to give the robot time finish speaking.  Break out of the loop if stop is pressed
            if (myRobot.sleepUnlessStop(7000))
               {
               break;
               }
            }
         // If done, restart by hitting stop and then play
         myRobot.waitForStop();
         myRobot.waitForPlay();
         }
      }

   // Or create some additional methods here!
   }