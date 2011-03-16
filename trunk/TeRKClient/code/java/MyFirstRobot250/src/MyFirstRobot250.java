import RSSReaders.StockComparisonChangeStatus;
import RobotClient.SimpleRobot250Client;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions.ExpressionSpeed;

class MyFirstRobot250
   {
   static final SimpleRobot250Client myRobot = new SimpleRobot250Client("My First Robot 250");

   static final void useFakeRSSReaders()
      {
      myRobot.useFakeRSSReaders(true);
      }

   static final void useRealRSSReaders()
      {
      myRobot.useFakeRSSReaders(false);
      }

   /** Blocks any further program operation until the play button is pressed. */
   static final void waitForPlayButton()
      {
      myRobot.waitForPlay();
      }

   /**
    * Sleeps the program for a given number of seconds. If the Stop button is pressed, this method immediately exits
    * without sleeping.
    */
   static final void sleepUnlessStopButton(final int secondsToSleep)
      {
      myRobot.sleepUnlessStop(1000 * secondsToSleep);
      }

   /** Returns the number of new (since the last time this method was called) headlines containing violence-related words. */
   static final int getNumberOfNewViolenceRelatedHeadlines()
      {
      final int numberOfNewHeadlines = myRobot.getNumberOfNewViolenceRelatedHeadlines();
      myRobot.writeToTextBox("Number of new violence-related headlines: " + numberOfNewHeadlines);

      return numberOfNewHeadlines;
      }

   /** Returns the number of new (since the last time this method was called) severe weather events. */
   static final int getNumberOfNewSevereWeatherEvents()
      {
      final int numberOfNewEvents = myRobot.getNumberOfNewSevereWeatherEvents();
      myRobot.writeToTextBox("Number of new severe weather events: " + numberOfNewEvents);

      return numberOfNewEvents;
      }

   /**
    * Returns the {@link StockComparisonChangeStatus} representing the status change of the two stocks since the last
    * time this method was called.
    */
   static final StockComparisonChangeStatus getStockComparisonChangeStatus()
      {
      final StockComparisonChangeStatus changeStatus = myRobot.getStockComparisonChangeStatus();
      myRobot.writeToTextBox("Stock Comparison: " + changeStatus);

      return changeStatus;
      }

   /**
    * Loads the roboticon (sequence or expression) having the given filename and plays it.  If there exists both a
    * sequence and an expression having the given filename, the sequence is played and the expression is ignored. Does
    * nothing if the filename does not match any existing sequence or expression.  Expressions are played at the speed
    * defined by {@link ExpressionSpeed#MEDIUM_VELOCITY}.
    *
    * @param filename the name of the sequence or expression to be played.
    */
   static final void play(final String roboticonFilename)
      {
      myRobot.playRoboticon(roboticonFilename);
      }
   }