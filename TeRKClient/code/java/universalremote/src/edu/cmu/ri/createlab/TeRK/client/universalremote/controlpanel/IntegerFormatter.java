package edu.cmu.ri.createlab.TeRK.client.universalremote.controlpanel;

import java.text.ParseException;
import javax.swing.text.NumberFormatter;

/**
 * <p>
 * <code>IntegerFormatter</code> formats {@link Integer}s, removing all commas.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class IntegerFormatter extends NumberFormatter
   {
   public String valueToString(final Object o) throws ParseException
      {
      Number number = (Number)o;
      if (number != null)
         {
         final int val = number.intValue();
         number = new Integer(val);
         }

      // get rid of the freakin' commas!
      return super.valueToString(number).replaceAll("[^\\d]", "");
      }

   public Object stringToValue(final String s) throws ParseException
      {
      Number number = (Number)super.stringToValue(s);
      if (number != null)
         {
         final int val = number.intValue();
         number = new Integer(val);
         }
      return number;
      }
   }
