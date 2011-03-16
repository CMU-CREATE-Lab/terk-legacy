public class MyFirstStockPriceRobot extends MyFirstRobot250
   {
   public static void main(String[] args)
      {
      // Specify whether to use fake or real RSS Readers by calling either useFakeRSSReaders() or useRealRSSReaders()
      useFakeRSSReaders();

      while (true)
         {
         waitForPlayButton();

         switch (getStockComparisonChangeStatus())
            {
            case BOTH_UP:
               play("BothStocksUp");
               break;
            case BOTH_DOWN:
               play("BothStocksDown");
               break;
            case NO_CHANGE:
               play("BothStocksNoChange");
               break;

            case STOCK_1_UP_STOCK_2_NO_CHANGE:
               play("Stock1UpStock2NoChange");
               break;
            case STOCK_1_UP_STOCK_2_DOWN:
               play("Stock1UpStock2Down");
               break;
            case STOCK_1_NO_CHANGE_STOCK_2_DOWN:
               play("Stock1NoChangeStock2Down");
               break;

            case STOCK_1_DOWN_STOCK_2_UP:
               play("Stock1DownStock2Up");
               break;
            case STOCK_1_NO_CHANGE_STOCK_2_UP:
               play("Stock1NoChangeStock2Up");
               break;
            case STOCK_1_DOWN_STOCK_2_NO_CHANGE:
               play("Stock1DownStock2NoChange");
               break;
            }

         sleepUnlessStopButton(1);
         }
      }
   }