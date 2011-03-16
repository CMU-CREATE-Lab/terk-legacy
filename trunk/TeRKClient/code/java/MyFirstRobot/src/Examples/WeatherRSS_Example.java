/*  Uses the weather RSS reader class to read data from the wunderground.com website
 *  and have the robot speak an interpretation of the weather conditions.
 *  Loop until the user types 'stop' in the textfield.
 *  This can be a sample solution to the weather assignment dealing with conditionals and iteration.
 */

import RSSReaders.WeatherReader;
import RobotClient.SimpleRobotClient;

public class WeatherRSS_Example
   {
   public static void main(String[] args)
      {
      // Instantiate the robot and robot GUI
      SimpleRobotClient myRobot = new SimpleRobotClient();

      // Write some code here!

      // Write some instructions for the user to the textbox
      myRobot.writeToTextBox("Weather Interpreter Program");
      myRobot.writeToTextBox("Please enter a city and state in the textbox in the form 'Pittsburgh, PA', and then press PLAY");

      // Declare the String variable to hold the value in the text field
      String textField;

      boolean done = false;
      // Note that we could also do the following with a do-while loop
      while (done == false)
         {

         // Wait for the play button to be pressed
         myRobot.waitForPlay();
         // Get the string value in the GUI's text field
         textField = myRobot.getTextFieldValueAsString();

         // If someone typed stop, set the done flag to true, and skip the rest of the loop
         if (textField.equals("stop") == true)
            {
            done = true;
            }
         else
            {
            // Instantiate the WeatherReader object with the City and State that we're reading from
            WeatherReader weather = new WeatherReader(textField);

            // Print which feed we're reading from
            myRobot.writeToTextBox("Now reading from the following Feed:");
            myRobot.writeToTextBox(weather.getFeedTitle());

            // Get the temperature and humidity from the feed
            double temperature = weather.getTemperature();
            double humidity = weather.getHumidity();

            // Write these values to the textbox
            myRobot.writeToTextBox("Temperature is: " + temperature + " Humidity is: " + humidity);

            // Interpret the weather conditions by saying something different based on temperature
            if (temperature < 20)
               {
               myRobot.saySomething("It is bloody cold");
               }
            else if (temperature < 40)
               {
               myRobot.saySomething("Snowball fight!");
               }
            else if (temperature < 60)
               {
               myRobot.saySomething("A bit cool today");
               }
            else if (temperature < 80)
               {
               myRobot.saySomething("Are we in California?");
               }
            // Bonus credit - if the weather is above 80 F, use humidity to determine how it feels
            else
               {
               if (humidity > 60)
                  {
                  myRobot.saySomething("Hot and Sticky!");
                  }
               else if (humidity > 30)
                  {
                  myRobot.saySomething("Hot hot hot!");
                  }
               else
                  {
                  myRobot.saySomething("It is a dry heat");
                  }
               }

            // Wait for the stop button to be pressed
            myRobot.waitForStop();
            myRobot.writeToTextBox("To get the weather from a different city, write the 'City, ST' in the text field and hit Play");
            myRobot.writeToTextBox("To stop the program and exit, type 'stop' in the text field instead");
            }
         }
      myRobot.writeToTextBox("Program complete, hit 'X' to close");
      }

   // Or create some additional methods here!
   }