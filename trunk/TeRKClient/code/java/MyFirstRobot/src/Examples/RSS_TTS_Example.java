/* Example file which reads the contents of the USGS XML file
* and converts them to speech.  It then speaks these over the
* robot's speakers.
*/

// Import the robot client, rss reader, and text to speech synthesizer

import java.util.Date;
import RSSReaders.RSSReader;
import RobotClient.SimpleRobotClient;

public class RSS_TTS_Example
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

      // Set the URL that we're reading in from
      RSSReader eqFeed = new RSSReader("http://earthquake.usgs.gov/eqcenter/recenteqsww/catalogs/eqs1day-M2.5.xml");

      // Set variables to hold the title of the entry and the data of the entry
      String title;
      Date date;

      myRobot.writeToTextBox("Now reading from the following Feed:");
      // Save to temp.wav the following string.  Note that the getFeedTitle() method gets the title of the entire RSS feed.
      myRobot.saySomething("Now reading from the following Feed:" + eqFeed.getFeedTitle() + ".  The feed is concerned with " + eqFeed.getFeedDescription());

      // This prints to the text box what was previously spoken by the robot
      myRobot.writeToTextBox(eqFeed.getFeedTitle());
      myRobot.writeToTextBox("Description:");
      myRobot.writeToTextBox(eqFeed.getFeedDescription());

      // Get the total number of entries in the feed
      int totalEntries = eqFeed.getEntryCount();

      // Loop through the entries
      for (int i = 0; i < totalEntries; i++)
         {
         // Save the title and date of entry i
         title = eqFeed.getEntryTitle(i);
         date = eqFeed.getEntryDate(i);

         // Now convert the string to speech and play it over the speaker
         myRobot.saySomething("Entry number " + i + " was an earthquake described as " + title + " occuring at " + date);

         // Write the same text that was synthesized as speech to the GUI's textbox
         myRobot.writeToTextBox("Entry number " + i + " was an earthquake described as " + title + " occuring at " + date);

         // Wait 10 seconds for the wav file to be played
         myRobot.sleepUnlessStop(10000);
         }
      }

   // Or create some additional methods here!
   }