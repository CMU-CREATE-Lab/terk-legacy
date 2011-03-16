package RSSReaders;

/**
 * <p>
 * <code>StockComparisonChangeStatus</code> represents the nine possible change states that two stocks can have in
 * relation to one another.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public enum StockComparisonChangeStatus
   {
      BOTH_UP("Both Up"),
      STOCK_1_UP_STOCK_2_NO_CHANGE("Stock 1 Up, Stock 2 No Change"),
      STOCK_1_UP_STOCK_2_DOWN("Stock 1 Up, Stock 2 Down"),

      STOCK_1_NO_CHANGE_STOCK_2_UP("Stock 1 No Change, Stock 2 Up"),
      NO_CHANGE("No Change"),
      STOCK_1_NO_CHANGE_STOCK_2_DOWN("Stock 1 No Change, Stock 2 Down"),

      STOCK_1_DOWN_STOCK_2_UP("Stock 1 Down, Stock 2 Up"),
      STOCK_1_DOWN_STOCK_2_NO_CHANGE("Stock 1 Down, Stock 2 No Change"),
      BOTH_DOWN("Both Down");

   private final String name;

   private StockComparisonChangeStatus(final String name)
      {
      this.name = name;
      }

   public String getName()
      {
      return name;
      }

   public String toString()
      {
      return name;
      }
   }
