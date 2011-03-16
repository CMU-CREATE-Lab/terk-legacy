using System;
using System.Text;

namespace TeRK.components
   {
   public class ArrayUtils
      {
      public static String arrayToString(bool[] array)
         {
         StringBuilder str = new StringBuilder();
         if ((array != null) && (array.Length > 0))
            {
            for (int i = 0; i < array.Length; i++)
               {
               bool val = array[i];
               str.Append(val ? 1 : 0);
               if (i < array.Length - 1)
                  {
                  str.Append(" ");
                  }
               }
            }

         return str.ToString();
         }

      public static String arrayToString(short[] array)
         {
         StringBuilder str = new StringBuilder();
         if ((array != null) && (array.Length > 0))
            {
            for (int i = 0; i < array.Length; i++)
               {
               short val = array[i];
               str.Append(val);
               if (i < array.Length - 1)
                  {
                  str.Append(" ");
                  }
               }
            }

         return str.ToString();
         }

      public static String arrayToString(int[] array)
         {
         StringBuilder str = new StringBuilder();
         if ((array != null) && (array.Length > 0))
            {
            for (int i = 0; i < array.Length; i++)
               {
               int val = array[i];
               str.Append(val);
               if (i < array.Length - 1)
                  {
                  str.Append(" ");
                  }
               }
            }

         return str.ToString();
         }

      public static Object arrayToString(Object[] array)
         {
         StringBuilder str = new StringBuilder();
         if ((array != null) && (array.Length > 0))
            {
            for (int i = 0; i < array.Length; i++)
               {
               Object val = array[i];
               str.Append(val);
               if (i < array.Length - 1)
                  {
                  str.Append(" ");
                  }
               }
            }

         return str.ToString();
         }

      private ArrayUtils()
         {
         // private to prevent instantiation
         }
      }
   }