package RSSReaders;

/**
 * Generates fake severe weather events with an average frequency of 2 events per second.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FakeSevereWeatherReader extends BaseFakeReader implements SevereWeatherEventCounter
   {
   private static final int MILLISECONDS_PER_PERIOD = 250;

   public int getEventCount()
      {
      return computeNumberOfNewEvents();
      }

   protected long computeNumberOfTimePeriodsSinceLastEvent(final long currentTimestamp, final long lastEventTimestamp)
      {
      return (currentTimestamp - lastEventTimestamp) / MILLISECONDS_PER_PERIOD;
      }
   }