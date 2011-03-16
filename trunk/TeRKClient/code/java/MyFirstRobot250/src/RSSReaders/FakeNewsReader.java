package RSSReaders;

/**
 * Generates fake headline events with an average frequency of 1 event per second.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FakeNewsReader extends BaseFakeReader implements NewsHeadlineCounter
   {
   private static final int MILLISECONDS_PER_PERIOD = 500;

   public int getHeadlineCount()
      {
      return computeNumberOfNewEvents();
      }

   protected long computeNumberOfTimePeriodsSinceLastEvent(final long currentTimestamp, final long lastEventTimestamp)
      {
      return (currentTimestamp - lastEventTimestamp) / MILLISECONDS_PER_PERIOD;
      }
   }
