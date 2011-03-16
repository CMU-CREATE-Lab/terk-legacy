package RSSReaders;

import java.util.Random;

/**
 * <p>
 * <code>BaseFakeReader</code> generates events with a 50% probability for each time period, where the length of
 * the time period is defined by the subclass.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
abstract class BaseFakeReader
   {
   private long lastEventTimestamp = System.currentTimeMillis();
   private final Random random = new Random();

   protected final int computeNumberOfNewEvents()
      {
      final long currentTime = System.currentTimeMillis();
      final long elapsedTimePeriods = computeNumberOfTimePeriodsSinceLastEvent(currentTime, lastEventTimestamp);
      int numEvents = 0;

      // for each time period that's gone by since the last time we got an event,
      // there's a 50% chance that a new event was generated.
      for (int i = 0; i < elapsedTimePeriods; i++)
         {
         if (random.nextBoolean())
            {
            numEvents++;
            }
         }

      if (numEvents > 0)
         {
         lastEventTimestamp = currentTime;
         }

      return numEvents;
      }

   protected abstract long computeNumberOfTimePeriodsSinceLastEvent(final long currentTimestamp, final long lastEventTimestamp);
   }
