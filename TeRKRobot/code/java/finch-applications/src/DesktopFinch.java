/**
 * Created by: Andrew Chang
 * Date: 02/15/09
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;
import edu.cmu.ri.createlab.rss.readers.WeatherReader;
import finch.Finch;

/*a simple finch program which turns your finch into a desktop tool.  Set the finch to stand straight up
* (so it's standing on its tail) After running the program, if you wave your hand above the finch, then it will tell
* you the time, if you wave you're hand to the finch's right side then it will talk to you, if you
* wave your hand to the left side, it will give you the weather.
*/
public class DesktopFinch
   {

   //the format for how the date should be given
   public static final String DATE_FORMAT_NOW = "HH:mm";

   public static void main(final String[] args)
      {
      Finch myFinch = new Finch();
      System.out.println("finch connected");

      //This feed may be changed by searching for your local weather forecast.
      /*The RSS is made outside the method because if it were in the getWeather method, it would have to be remade
         *everytime it is called, thus it is faster to make the reader outside the method, so it's only made once.
         */
      WeatherReader weather = new WeatherReader("Pittsburgh, PA");

      //as long as there is light, the finch will continue to work
      int[] light = myFinch.getLightSensors();
      while (light[0] > 70 || light[1] > 70)
         {
         boolean[] obs = myFinch.getObstacleSensors();
         System.out.println(obs[0] + " " + obs[1]);
         if (myFinch.isObstacleLeftSide() && myFinch.isObstacleRightSide())
            {
            //tell the time
            myFinch.saySomething(theTime());
            }
         else if (myFinch.isObstacleLeftSide() == true)
            {
            //give the weather
            myFinch.saySomething(theWeather(weather));
            }
         else if (myFinch.isObstacleRightSide() == true)
            {
            //talk to you
            myFinch.saySomething(casualConversation());
            }
         myFinch.sleep(1000); //since the program runs without stop, we need to wait for the action to be performed before we continue
         light = myFinch.getLightSensors();
         }
      myFinch.quit();
      }

   //A Method that returns the date in the format given as Hours:Minutes the time is given in 24 hour time
   public static String theTime()
      {
      //creates a new Calendar based off the computer's calendar specifications
      Calendar date = Calendar.getInstance();
      //creates a date format, which will give the date in the format given
      SimpleDateFormat theDate = new SimpleDateFormat(DATE_FORMAT_NOW);
      return "The time is " + theDate.format(date.getTime());
      }

   //a method that returns a string which depicts the current weather forecast for Pittsburgh.
   public static String theWeather(WeatherReader wr)
      {
      wr.updateFeed();
      return "It is " + wr.getConditions() + "and it is " + wr.getTemperature() + "degrees outside";
      }

   //a method that returns a random string
   public static String casualConversation()
      {
      String[] convo = {"How are you?", "My name is Paul.", "You look nice today.",
                        "Please set me free.", "Why have you enslaved me?"};
      //gets a random number which corresponds to a statement in the array
      int index = (int)(Math.random() * 5);
      return convo[index];
      }
   }

