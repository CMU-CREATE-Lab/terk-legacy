package RSSReaders;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class StockComparisonReader implements StockPriceChangeStatusComparator
   {
   private static final Logger LOG = Logger.getLogger(StockComparisonReader.class);

   private final RSSReader rssReader1;
   private final RSSReader rssReader2;
   private final String stock1;
   private final String stock2;
   private Quote previousQuote1;
   private Quote previousQuote2;
   private long timestampOfLastUpdate;

   public static void main(final String[] args) throws InterruptedException
      {
      final StockPriceChangeStatusComparator reader = new StockComparisonReader("AAPL", "MSFT");
      for (int i = 0; i < 100; i++)
         {
         reader.getChangeStatus();
         Thread.sleep(1000);
         }
      }

   public StockComparisonReader(final String stockSymbol1, final String stockSymbol2)
      {
      stock1 = stockSymbol1;
      stock2 = stockSymbol2;
      this.rssReader1 = new RSSReader("http://www.nasdaq.com/aspxcontent/NasdaqRSS.aspx?data=quotes&symbol=" + stockSymbol1);
      this.rssReader2 = new RSSReader("http://www.nasdaq.com/aspxcontent/NasdaqRSS.aspx?data=quotes&symbol=" + stockSymbol2);
      }

   public StockComparisonChangeStatus getChangeStatus()
      {
      StockComparisonChangeStatus status = StockComparisonChangeStatus.NO_CHANGE;

      try
         {
         rssReader1.updateFeed();
         rssReader2.updateFeed();
         }
      catch (Exception e)
         {
         LOG.warn("Exception while updating the feed", e);
         }

      final Quote quote1 = getQuote(rssReader1, stock1);
      final Quote quote2 = getQuote(rssReader2, stock2);

      if (previousQuote1 != null && previousQuote2 != null)
         {
         if (quote1 != null && quote2 != null)
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("");
               LOG.trace("QUOTES: " + timestampOfLastUpdate);
               LOG.trace("   " + quote1);
               LOG.trace("   " + quote2);
               }
            if (quote1.getTime() > timestampOfLastUpdate && quote2.getTime() > timestampOfLastUpdate)
               {
               LOG.trace("   computing change...");
               final float change1 = quote1.getPrice() - previousQuote1.getPrice();
               final float change2 = quote2.getPrice() - previousQuote2.getPrice();
               status = computeChangeStatus(change1, change2);
               }
            }
         }

      previousQuote1 = quote1;
      previousQuote2 = quote2;

      timestampOfLastUpdate = Math.max(previousQuote1 == null ? 0 : previousQuote1.getTime(),
                                       previousQuote2 == null ? 0 : previousQuote2.getTime());

      if (LOG.isTraceEnabled())
         {
         LOG.trace("STATUS: " + status);
         }
      return status;
      }

   private StockComparisonChangeStatus computeChangeStatus(final float change1, final float change2)
      {
      final StockComparisonChangeStatus status;

      if (change1 == 0)// stock 1 = NO CHANGE
         {
         if (change2 == 0)
            {
            status = StockComparisonChangeStatus.NO_CHANGE;
            }
         else if (change2 < 0)
            {
            status = StockComparisonChangeStatus.STOCK_1_NO_CHANGE_STOCK_2_DOWN;
            }
         else
            {
            status = StockComparisonChangeStatus.STOCK_1_NO_CHANGE_STOCK_2_UP;
            }
         }
      else if (change1 < 0)// stock 1 = DOWN
         {
         if (change2 == 0)
            {
            status = StockComparisonChangeStatus.STOCK_1_DOWN_STOCK_2_NO_CHANGE;
            }
         else if (change2 < 0)
            {
            status = StockComparisonChangeStatus.BOTH_DOWN;
            }
         else
            {
            status = StockComparisonChangeStatus.STOCK_1_DOWN_STOCK_2_UP;
            }
         }
      else
         {
         if (change2 == 0)// stock 1 = UP
            {
            status = StockComparisonChangeStatus.STOCK_1_UP_STOCK_2_NO_CHANGE;
            }
         else if (change2 < 0)
            {
            status = StockComparisonChangeStatus.STOCK_1_UP_STOCK_2_DOWN;
            }
         else
            {
            status = StockComparisonChangeStatus.BOTH_UP;
            }
         }

      return status;
      }

   private Quote getQuote(final RSSReader rssReader, final String stockSymbol)
      {
      final List<FeedEntry> entries = rssReader.getEntries();

      if ((entries != null) && (!entries.isEmpty()))
         {
         final FeedEntry entry = entries.get(0);

         // clean out all the annoying HTML, and convert the data to a nicely parseable format
         String description = entry.getDescription();
         description = description.replaceAll("[\\s\r\n]", "");
         description = description.replaceAll("%Change", "Percent");
         description = description.replaceFirst(".*Last.*>([\\d\\.]+)<.*Change", "Last=$1+Change");
         description = description.replaceFirst("(.*)Change.*>([\\d\\.,]+)<.*Percent", "$1Change=$2+Percent");
         description = description.replaceFirst("(.*)Percent.*>([\\d\\.,]+)%<.*Volume", "$1Percent=$2+Volume");
         description = description.replaceFirst("(.*)Volume.*>([\\d\\.,]+).*", "$1Volume=$2");
         final String[] attributes = description.split("\\+");

         final Quote quote = new Quote(stockSymbol, entry.getPublishedTimestampAsDate());
         for (int i = 0; i < attributes.length; i++)
            {
            final String attribute = attributes[i];
            final String[] keyVal = attribute.split("=");
            quote.setAttribute(keyVal[0], keyVal[1]);
            }

         return quote;
         }

      return null;
      }

   private static final class Quote
      {
      private final String symbol;
      private final long time;
      private float price;
      private float change;
      private float percentChange;
      private int volume;

      private final Map<String, AttributeHandler> attributeHandlers = new HashMap<String, AttributeHandler>(4);

      private Quote(final String symbol, final Date time)
         {
         this.symbol = symbol;
         this.time = time.getTime();
         attributeHandlers.put("Last",
                               new AttributeHandler()
                               {
                               public void handleAttribute(final String value)
                                  {
                                  Quote.this.price = Float.parseFloat(value);
                                  }
                               });
         attributeHandlers.put("Change",
                               new AttributeHandler()
                               {
                               public void handleAttribute(final String value)
                                  {
                                  Quote.this.change = Float.parseFloat(value);
                                  }
                               });
         attributeHandlers.put("Percent",
                               new AttributeHandler()
                               {
                               public void handleAttribute(final String value)
                                  {
                                  Quote.this.percentChange = Float.parseFloat(value);
                                  }
                               });
         attributeHandlers.put("Volume",
                               new AttributeHandler()
                               {
                               public void handleAttribute(final String value)
                                  {
                                  Quote.this.volume = Integer.parseInt(value.replaceAll(",", ""));
                                  }
                               });
         }

      private String getSymbol()
         {
         return symbol;
         }

      private long getTime()
         {
         return time;
         }

      private float getPrice()
         {
         return price;
         }

      private float getChange()
         {
         return change;
         }

      private float getPercentChange()
         {
         return percentChange;
         }

      private int getVolume()
         {
         return volume;
         }

      private void setAttribute(final String key, final String val)
         {
         final AttributeHandler handler = attributeHandlers.get(key);
         if (handler != null)
            {
            handler.handleAttribute(val);
            }
         }

      public String toString()
         {
         return "Quote{" +
                "volume=" + volume +
                ", percentChange=" + percentChange +
                ", change=" + change +
                ", price=" + price +
                ", time=" + time +
                ", symbol=" + symbol +
                '}';
         }

      private interface AttributeHandler
         {
         void handleAttribute(final String value);
         }
      }
   }
