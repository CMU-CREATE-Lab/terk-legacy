import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import finch.Finch;

/**
 * @author Alex Styler (astyler@gmail.com)
 */
public class Thermometer
   {

   public static void main(final String[] args) throws IOException
      {
      final Finch finch = new Finch();

      final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
      Color currentColor;
      double temperature, min, max, scale;
      int red, blue;
      min = 80;
      max = -20;
      red = -1;

      System.out.println("");
      System.out.println("Press ENTER to quit.");
      while (true)
         {
         // check whether the user pressed a key
         if (in.ready())
            {
            break;
            }

         //read temperature from finch
         temperature = finch.getTemperature();
         System.out.println("Temp: " + temperature);

         //re-establish observed range
         //with new extremes the finch will
         //calibrate itself to the expected temperatures
         //for more dramatic effect
         if (temperature > max)
            {
            max = temperature;
            System.out.println("New max: " + max);
            }
         if (temperature < min)
            {
            min = temperature;
            System.out.println("New min:" + min);
            }

         scale = 255.0 / (max - min);

         //set red and blue levels for orb
         red = (int)((temperature - min) * scale);
         blue = 255 - red;
         currentColor = new Color(red, 0, blue);

         // set the color
         finch.setLED(currentColor);
         }

      finch.quit();
      }
   }
