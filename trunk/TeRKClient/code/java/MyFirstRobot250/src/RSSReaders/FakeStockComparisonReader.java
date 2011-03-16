package RSSReaders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Generates fake stock price change events for two stocks every second.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class FakeStockComparisonReader implements StockPriceChangeStatusComparator
   {
   private static final Map<Integer, StockComparisonChangeStatus> CHANGE_STATUS_INDEX_MAP;

   static
      {
      final Map<Integer, StockComparisonChangeStatus> changeStatusIndexMap = new HashMap<Integer, StockComparisonChangeStatus>(5);
      for (int i = 0; i < StockComparisonChangeStatus.values().length; i++)
         {
         changeStatusIndexMap.put(i, StockComparisonChangeStatus.values()[i]);
         }
      CHANGE_STATUS_INDEX_MAP = Collections.unmodifiableMap(changeStatusIndexMap);
      }

   private final Random random = new Random();
   private long lastChangeTimestamp = System.currentTimeMillis();

   public StockComparisonChangeStatus getChangeStatus()
      {
      final long currentTime = System.currentTimeMillis();
      if (currentTime - lastChangeTimestamp < 1000)
         {
         return StockComparisonChangeStatus.NO_CHANGE;
         }

      lastChangeTimestamp = currentTime;

      return CHANGE_STATUS_INDEX_MAP.get(random.nextInt(StockComparisonChangeStatus.values().length));
      }
   }