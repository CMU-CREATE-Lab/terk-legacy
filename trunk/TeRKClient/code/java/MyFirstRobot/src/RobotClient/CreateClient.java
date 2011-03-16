/**
 *  Description:  Client to control the iRobot Create through a Qwerk board.  This class extends
 *  the more general robotClient class, which is used for controlling a Qwerk board.
 *  @author: Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

package RobotClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import edu.cmu.ri.createlab.TeRK.serial.BaudRate;
import edu.cmu.ri.createlab.TeRK.serial.CharacterSize;
import edu.cmu.ri.createlab.TeRK.serial.FlowControl;
import edu.cmu.ri.createlab.TeRK.serial.Parity;
import edu.cmu.ri.createlab.TeRK.serial.QwerkSerialPortDevice;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOConfiguration;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOException;
import edu.cmu.ri.createlab.TeRK.serial.SerialIOService;
import edu.cmu.ri.createlab.TeRK.serial.StopBits;

public class CreateClient extends RobotClient
   {

   // Configure the serial port connecting the Qwerk and Create
   // to use UART 2, Baud 57600, 8 chars, no parity, one stop bit, no flow control
   private SerialIOConfiguration config = new SerialIOConfiguration(QwerkSerialPortDevice.DEV_TTY_AM1.getName(),
                                                                    BaudRate.BAUD_57600,
                                                                    CharacterSize.EIGHT,
                                                                    Parity.NONE,
                                                                    StopBits.ONE,
                                                                    FlowControl.NONE);

   /** Date formatter, used for time-stamping messages in the message area */
   private final SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss,SSS: ");

   /* Variables to hold the state of the Create's play, advance, power LEDs*/
   private boolean playLEDState = false;
   private boolean advanceLEDState = false;
   private byte powerLEDColor = 0;
   private byte powerLEDIntensity = 0;

   /* Variables to hold values of the Create's various sensors */
   private boolean bumpLeft;// left bumper
   private boolean bumpRight;// right bumper
   private boolean wheelDropRight;// right wheel dropped
   private boolean wheelDropLeft;// left wheel dropped
   private boolean wheelDropCaster;// caster dropped

   private boolean wallSeen;// state of right-hand wall sensor (Create has no left wall sensor
   private boolean cliffLeft;// left cliff sensor (true if there is no ground underneath the sensor)
   private boolean cliffRight;// right cliff sensor
   private boolean cliffFrontLeft;// left front cliff sensor
   private boolean cliffFrontRight;// right front cliff sensor
   private boolean virtualWallSeen;// state of the virtual wall sensor (also detects the charging dock)
   private boolean playButtonPressed;// state of the play button - true if pressed
   private boolean advanceButtonPressed;// state of the >>| advance button

   private int distance = 0;// cumulative distance that the robot has travelled in mm
   private double leftWheelDistance = 0;// cumulative distance that the robot's left wheel has travelled in mm
   private double rightWheelDistance = 0;// cumulative distance that the robot's right wheel has travelled in mm
   private int angle = 0;// cumulative angle that the robot has rotated
   private int voltage;// voltage of the Create's battery pack in mV
   private int current;// current that the Create is currently using in mA - negative values indicate current draw, positive indicates battery charging
   private int batteryCharge;// remaining battery charge in mA-hrs

   /** Starts the CreateClient by running the Robot GUI with the APPLICATION_NAME (Currently "My First Robot Program").  */
   public CreateClient()
      {
      super();
      }

   /** Starts the CreateClient by running a GUI titled by the String applicationName.
    *
    * @param applicationName String text which sets the title of the Robot GUI
    */
   public CreateClient(String applicationName)
      {
      super(applicationName);
      }

   /** Initializes the Create by sending the start command over the serial cable from the Qwerk to the Create.
    *  Also reads sensor data from the Create to determine the initial configuration of the robot */
   public void initialize()
      {
      // Create a serial service
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {
            // Close the serial port if for some reason an earlier program left it open
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).close();

            // Open the serial port with our configuration values (baud = 57600, 8n1, no flow control)
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).open(config);
            System.out.println("Serial port opened.");

            // Send the start command and change mode to 'safe'
            byte[] data = new byte[2];
            data[0] = (byte)128;// start command
            data[1] = (byte)131;// change mode to 'safe' mode

            // Write the data to the Create through the serial port
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while opening the serial port";
            System.out.println(dateFormatter.format(new Date()) + msg + e);
            }
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }

      // Update the sensors on initialization - this reads in values for all of the sensor variables
      // defined at the start of this class
      updateSensors();
      }

   /** Sets the velocities of the Create's left and right motors in millimeters per second.
    *
    * @param leftMotor Velocity of Create's left wheel in millimeters per second
    * @param rightMotor Velocity of Create's right wheel in millimeters per second
    */
   public void moveMotors(int leftMotor, int rightMotor)
      {

      // Speeds above 500mm/s are not valid, so don't send them
      if (leftMotor < -500 || leftMotor > 500 || rightMotor < -500 || rightMotor > 500)
         {
         System.out.println("Invalid motor commands, values must be between -500 and 500");
         }
      else
         {
         // Get a serial service
         final SerialIOService serialIOService = getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               // Convert negative speeds to values over 32767 to send the data in two's complement form
               if (rightMotor < 0)
                  {
                  rightMotor += 65536;
                  }

               if (leftMotor < 0)
                  {
                  leftMotor += 65536;
                  }

               // Create the movement packet
               byte[] data = new byte[6];

               data[0] = (byte)131;// Change mode to 'safe'
               data[1] = (byte)145;// Send the command for driving the wheels
               data[2] = (byte)(rightMotor / 256);// Send the high 8-bits for right wheel speed
               data[3] = (byte)(rightMotor % 256);// Send the low 8-bits for right wheel speed
               data[4] = (byte)(leftMotor / 256);
               data[5] = (byte)(leftMotor % 256);

               // Write the data to the Create through the Qwerk's serial port
               serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
               System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while writing to the serial port";
               System.out.println(dateFormatter.format(new Date()) + msg);
               }
            }
         else
            {
            System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
            }
         }
      }

   /** Sets the velocities of the Create's left and right motors in millimeters per second, and runs
    * the motors for a set amount of time determined by <code>runningTime</code>.  After the time has elapsed
    * the motors are stopped.
    *
    * @param leftMotor Velocity of Create's left wheel in millimeters per second
    * @param rightMotor Velocity of Create's right wheel in millimeters per second
    * @param runningTime Time in milliseconds for the motors to run
    */
   public void moveMotors(int leftMotor, int rightMotor, int runningTime)
      {

      // Speeds above 500mm/s are not valid, so don't send them
      if (leftMotor < -500 || leftMotor > 500 || rightMotor < -500 || rightMotor > 500)
         {
         System.out.println("Invalid motor commands, values must be between -500 and 500");
         }
      else
         {
         // Get a serial service
         final SerialIOService serialIOService = getQwerkController().getSerialIOService();
         if (serialIOService != null)
            {
            try
               {
               // Convert negative speeds to values over 32767 to send the data in two's complement form
               if (rightMotor < 0)
                  {
                  rightMotor += 65536;
                  }

               if (leftMotor < 0)
                  {
                  leftMotor += 65536;
                  }

               // Create the movement packet
               byte[] data = new byte[6];

               data[0] = (byte)131;// Change mode to 'safe'
               data[1] = (byte)145;// Send the command for driving the wheels
               data[2] = (byte)(rightMotor / 256);// Send the high 8-bits for right wheel speed
               data[3] = (byte)(rightMotor % 256);// Send the low 8-bits for right wheel speed
               data[4] = (byte)(leftMotor / 256);
               data[5] = (byte)(leftMotor % 256);

               // Write the data to the Create through the Qwerk's serial port
               serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
               System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
               sleepUnlessStop(runningTime);
               stopMoving();
               }
            catch (SerialIOException e)
               {
               final String msg = "SerialIOException while writing to the serial port";
               System.out.println(dateFormatter.format(new Date()) + msg);
               }
            }
         else
            {
            System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
            }
         }
      }

   /** Stop the Create's motors by sending 0 mm/s velocities */
   public void stopMoving()
      {
      moveMotors(0, 0);
      }

   /** Set the state of the LED located next to the play ('>') button
    *
    * @param state State to set the LED to - true is on, false is off
    */
   public void setPlayLED(boolean state)
      {

      playLEDState = state;

      setLEDs();
      }

   /** Set the state of the LED located next to the advance ('>>|') button
    *
    * @param state State to set the LED to - true is on, false is off
    */
   public void setAdvanceLED(boolean state)
      {

      advanceLEDState = state;

      setLEDs();
      }

   /** Set the power LED's color and intensity
    *
    * @param color Set the color of the LED - 0 = green, 255 = red, and values between mix green and red
    * @param intensity Set the intensity of the LED, valid values range from 0 (off) to 255 (completely on)
    */
   public void setPowerLED(int color, int intensity)
      {

      powerLEDColor = (byte)color;
      powerLEDIntensity = (byte)intensity;

      setLEDs();
      }

   /** Set the states of the play, advanced, and power LEDs with a single method
    *
    * @param play State to set the LED located next to the play button ('>') to - true is on, false is off
    * @param advance  State to set the LED located next to the advance button ('>>|') to - true is on, false is off
    * @param color Set the color of the power LED - 0 = green, 255 = red, and values between mix green and red
    * @param intensity Set the intensity of the power LED, valid values range from 0 (off) to 255 (completely on)
    */
   public void setAllLEDs(boolean play, boolean advance, int color, int intensity)
      {

      playLEDState = play;
      advanceLEDState = advance;
      powerLEDColor = (byte)color;
      powerLEDIntensity = (byte)intensity;

      setLEDs();
      }

   // Send the LED states to the Create
   private void setLEDs()
      {

      // Get a serial service
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {

            // Assemble the command packet
            byte[] data = new byte[5];
            data[0] = (byte)131;// Change to safe mode
            data[1] = (byte)139;// Set LEDs

            // Set play and advance LED states
            data[2] = 0;
            if (playLEDState)
               {
               data[2] += 2;
               }
            if (advanceLEDState)
               {
               data[2] += 8;
               }
            data[3] = powerLEDColor;// Set power LED color
            data[4] = powerLEDIntensity;// Set power LED intensity

            // Send commands to Create over serial port
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while writing to the serial port";
            System.out.println(dateFormatter.format(new Date()) + msg);
            }
         }
      else
         {
         System.out.println("SerialIOService is null!");
         }
      }

   /** Closes the serial connection between the Qwerk and the Create.  This should be done at
    * the end of a program.
    */
   public void closeRobot()
      {

      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).close();
         System.out.println(dateFormatter.format(new Date()) + "Serial port closed.");
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }
      }

   /** Sends the dock command to the Create, causing the Create to seek out a charging dock and charge itself.  */
   public void dockRobot()
      {
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {
            // Assemble command packet
            byte[] data = new byte[2];
            data[0] = (byte)136;// Set demo mode
            data[1] = (byte)1;// Select dock demo

            // Write commands to Create
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Robot now attempting to dock.");
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while writing to the serial port";
            System.out.println(msg);
            }
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }
      }

   /** Simple method for moving the Create robot off of a charging dock.  This method just backs the
    * Create robot up 40 cm and then turns the robot 180 degrees.
    */
   public void unDockRobot()
      {
      moveDistance(-400);
      moveAngle(180);
      }

   /** Causes the Create to move in a straight line by the number of millimeters specified by <code>travelDistance</code>.
    *
    * @param travelDistance Specifies distance for create to move in millimeters.  Negative values are backwards.
    */
   public void moveDistance(int travelDistance)
      {

      int leftMotor = 0;
      int rightMotor = 0;

      if (travelDistance > 0)
         {
         leftMotor = 200;
         rightMotor = 200;
         }
      else if (travelDistance < 0)
         {
         leftMotor = 65336;
         rightMotor = 65336;
         }

      // Get a serial service
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {
            // Convert negative distances to values over 32767 to send the data in two's complement form
            int sleepTime;

            if (travelDistance < 0)
               {
               sleepTime = -travelDistance * 8;
               travelDistance += 65536;
               }
            else
               {
               sleepTime = travelDistance * 8;
               }

            updateSensors();

            // Create the movement packet
            byte[] data = new byte[15];

            data[0] = (byte)131;// Change mode to 'safe'
            data[1] = (byte)145;// Send the command for driving the wheels
            data[2] = (byte)(rightMotor / 256);// Send the high 8-bits for right wheel speed
            data[3] = (byte)(rightMotor % 256);// Send the low 8-bits for right wheel speed
            data[4] = (byte)(leftMotor / 256);
            data[5] = (byte)(leftMotor % 256);
            data[6] = (byte)156;// Change mode to 'safe'
            data[7] = (byte)(travelDistance / 256);// Send the high 8-bits for travel distance
            data[8] = (byte)(travelDistance % 256);// Send the low 8-bits for travel distance
            data[9] = (byte)131;// Change mode to 'safe'
            data[10] = (byte)145;// Send the command for driving the wheels
            data[11] = (byte)(0);// Send the high 8-bits for right wheel speed
            data[12] = (byte)(0);// Send the low 8-bits for right wheel speed
            data[13] = (byte)(0);
            data[14] = (byte)(0);

            // Write the data to the Create through the Qwerk's serial port
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
            // stopMoving();

            sleepUnlessStop(sleepTime);
            updateSensors();
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while writing to the serial port";
            System.out.println(dateFormatter.format(new Date()) + msg);
            }
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }
      }

   /** Cause the Create to rotate by the number of degrees specified by <code>travelAngle</code>
    *
    * @param travelAngle Specifies the number of degrees to rotate - positive values cause counter clockwise rotation, negative values cause clockwise rotation
    */
   public void moveAngle(int travelAngle)
      {
      int leftMotor = 0;
      int rightMotor = 0;

      if (travelAngle > 0)
         {
         leftMotor = 65406;
         rightMotor = 130;
         }
      else if (travelAngle < 0)
         {
         leftMotor = 130;
         rightMotor = 65406;
         }

      // Get a serial service
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {
            // Convert negative distances to values over 32767 to send the data in two's complement form
            int sleepTime;

            if (travelAngle < 0)
               {
               sleepTime = -travelAngle * 20;
               travelAngle += 65536;
               }
            else
               {
               sleepTime = travelAngle * 20;
               }
            updateSensors();
            // Create the movement packet
            byte[] data = new byte[15];

            data[0] = (byte)131;// Change mode to 'safe'
            data[1] = (byte)145;// Send the command for driving the wheels
            data[2] = (byte)(rightMotor / 256);// Send the high 8-bits for right wheel speed
            data[3] = (byte)(rightMotor % 256);// Send the low 8-bits for right wheel speed
            data[4] = (byte)(leftMotor / 256);
            data[5] = (byte)(leftMotor % 256);
            data[6] = (byte)157;// Change mode to 'safe'
            data[7] = (byte)(travelAngle / 256);// Send the high 8-bits for travel distance
            data[8] = (byte)(travelAngle % 256);// Send the low 8-bits for travel distance
            data[9] = (byte)131;// Change mode to 'safe'
            data[10] = (byte)145;// Send the command for driving the wheels
            data[11] = (byte)(0);// Send the high 8-bits for right wheel speed
            data[12] = (byte)(0);// Send the low 8-bits for right wheel speed
            data[13] = (byte)(0);
            data[14] = (byte)(0);

            // Write the data to the Create through the Qwerk's serial port
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Data written successfully.");
            stopMoving();
            if (travelAngle < 0)
               {
               travelAngle = -travelAngle;
               }
            sleepUnlessStop(sleepTime);
            updateSensors();
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while writing to the serial port";
            System.out.println(dateFormatter.format(new Date()) + msg);
            }
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }
      }

   /** Updates all sensor values from the Create.  All methods which access Create sensor data
    * call this method before returning the data.
    */
   public void updateSensors()
      {

      // Get a serial controller
      final SerialIOService serialIOService = getQwerkController().getSerialIOService();
      if (serialIOService != null)
         {
         try
            {
            /*  // Before reading data off the serial port, make sure there isn't some already around
            if(serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).isDataAvailable()) {
               // if data is available, read it in to the junk variable
             serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).read(1000);
            }*/

            // Assemble the command packet which tells the create to prepare sensor data
            byte[] data = new byte[2];
            data[0] = (byte)142;// Set sensor mode
            data[1] = (byte)0;// Select which data packet we want to read

            // Send the command package to the create
            serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).write(data);
            System.out.println(dateFormatter.format(new Date()) + "Update sensor data.");
            sleepUnlessStop(5);
            // The sensor packet will be 26 bytes, so set packet size to 26
            int packetSize = 26;

            int count = 0;

            // Block until data becomes available or we run out of tries
            while (!serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).isDataAvailable() && (count < 10))
               {
               count++;
               }
            // Delay 10 additional milli-seconds to make sure all 26 bytes get buffered before reading
            sleepUnlessStop(10);

            // Set the size of the data byte array to 26
            data = new byte[packetSize];

            byte[] tempData = new byte[1000];

            // Read the data from the serial port
            tempData = serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).read(packetSize);

            /* Print the raw data coming back.  Commented out because this is only useful when debugging
       * the low-level Qwerk/Create serial connection.*/
            System.out.println("" + tempData.length);
            for (int countin = 0; countin < tempData.length; countin++)
               {
               System.out.print(" " + tempData[countin]);
               }
            System.out.print("\n");

            if (tempData.length == packetSize)
               {
               data = tempData;

               // The first byte contains five sensor variables - first load it into an integer to manipulate it
               int decodeBumpWheel = (int)data[0];

               // Decode the first byte through a series of if-statements
               if (decodeBumpWheel > 15)
                  {
                  wheelDropCaster = true;
                  decodeBumpWheel -= 16;
                  }
               else
                  {
                  wheelDropCaster = false;
                  }

               if (decodeBumpWheel > 7)
                  {
                  wheelDropLeft = true;
                  decodeBumpWheel -= 8;
                  }
               else
                  {
                  wheelDropLeft = false;
                  }

               if (decodeBumpWheel > 3)
                  {
                  wheelDropRight = true;
                  decodeBumpWheel -= 4;
                  }
               else
                  {
                  wheelDropRight = false;
                  }

               if (decodeBumpWheel > 1)
                  {
                  bumpLeft = true;
                  decodeBumpWheel -= 2;
                  }
               else
                  {
                  bumpLeft = false;
                  }

               if (decodeBumpWheel == 1)
                  {
                  bumpRight = true;
                  }
               else
                  {
                  bumpRight = false;
                  }

               // Data packet 1 indicates if a wall is seen - 1 for seen, 0 for not
               if (data[1] == 1)
                  {
                  wallSeen = true;
                  }
               else
                  {
                  wallSeen = false;
                  }

               // Data packet 2 indicates if a cliff is seen on the left side - 1 for seen, 0 for not
               if (data[2] == 1)
                  {
                  cliffLeft = true;
                  }
               else
                  {
                  cliffLeft = false;
                  }

               // Data packet 3 indicates if a cliff is seen on the front left side - 1 for seen, 0 for not
               if (data[3] == 1)
                  {
                  cliffFrontLeft = true;
                  }
               else
                  {
                  cliffFrontLeft = false;
                  }

               // Data packet 4 indicates if a cliff is seen on the front right side - 1 for seen, 0 for not
               if (data[4] == 1)
                  {
                  cliffFrontRight = true;
                  }
               else
                  {
                  cliffFrontRight = false;
                  }

               // Data packet 5 indicates if a cliff is seen on the right side - 1 for seen, 0 for not
               if (data[5] == 1)
                  {
                  cliffRight = true;
                  }
               else
                  {
                  cliffRight = false;
                  }

               // Data packet 6 indicates if a virtual wall is seen - 1 for seen, 0 for not
               if (data[6] == 1)
                  {
                  virtualWallSeen = true;
                  }
               else
                  {
                  virtualWallSeen = false;
                  }

               // Data packets 7-10 do not contain important sensor data

               // Data packet 11 holds the states of both the play and advance buttons.  Must be decoded.
               int buttonStates = (int)data[11];

               if (buttonStates > 3)
                  {
                  advanceButtonPressed = true;
                  buttonStates -= 4;
                  }
               else
                  {
                  advanceButtonPressed = false;
                  }

               if (buttonStates == 1)
                  {
                  playButtonPressed = true;
                  }
               else
                  {
                  playButtonPressed = false;
                  }

               // Data packets 12 and 13 hold the distance travelled since the sensors were last read

               // Store this distance value in a temporary value
               int tempDistance;

               // Assemble the value from the two byte packets
               tempDistance = ((int)data[12]) * 256 + (int)data[13];

               // Convert the distance value from two's complement to a signed value
               if (tempDistance > 32767)
                  {
                  tempDistance -= 65536;
                  }

               // Add the temporary distance to the cumulative distance the robot has travelled
               distance += tempDistance;

               // Data packets 14 and 15 hold the angle rotated since the sensors were last read
               int tempAngle;

               // Assemble and store the angle using the same algorithm as for distance
               tempAngle = ((int)data[14]) * 256 + (int)data[15];
               if (tempAngle > 32767)
                  {
                  tempAngle -= 65536;
                  }
               angle += tempAngle;

               // Calculate the distance the individual wheels have travelled based on the temporary angle and distance
               // The calculation assumes the robot has translated and rotated evenly between sensor readings, such that
               // it has described an arc.  The value 130.175 is the distance in mm from one wheel to the center of the robot.
               if (tempAngle > 0)
                  {
                  leftWheelDistance += ((double)tempDistance) - Math.PI / 180.0 * ((double)tempAngle) * 130.175;
                  rightWheelDistance += ((double)tempDistance) + Math.PI / 180.0 * ((double)tempAngle) * 130.175;
                  }
               else if (tempAngle < 0)
                  {
                  leftWheelDistance += ((double)tempDistance) + Math.PI / 180.0 * ((double)tempAngle) * 130.175;
                  rightWheelDistance += ((double)tempDistance) - Math.PI / 180.0 * ((double)tempAngle) * 130.175;
                  }
               else
                  {
                  leftWheelDistance += (double)tempDistance;
                  rightWheelDistance += (double)tempDistance;
                  }

               // Assemble the voltage, current, and battery charge remaining from the data packets 17-20 and 22-23
               voltage = ((int)data[17]) * 256 + (int)data[18];
               current = ((int)data[19]) * 256 + (int)data[20];
               batteryCharge = ((int)data[22]) * 256 + (int)data[23];
               }
            else
               {

               System.out.println(dateFormatter.format(new Date()) + "Not all data received from Create");

               // If less than a whole packet was read in, extract the rest of the packet from the buffer.
               if (packetSize > tempData.length)
                  {
                  sleepUnlessStop(50);
                  packetSize = packetSize - tempData.length;
                  tempData = serialIOService.getSerialPort(QwerkSerialPortDevice.DEV_TTY_AM1.getName()).read(packetSize);
                  System.out.println("Flushed data from serial buffer.");
                  }
               }
            }
         catch (SerialIOException e)
            {
            final String msg = "SerialIOException while writing to the serial port";
            System.out.println(dateFormatter.format(new Date()) + msg);
            }
         }
      else
         {
         System.out.println(dateFormatter.format(new Date()) + "SerialIOService is null!");
         }
      }

   // The following functions simply allow public access to the sensor values

   /**
    * @return True if the left bumper is depressed, false otherwise
    */
   public boolean bumpLeft()
      {
      updateSensors();
      return bumpLeft;
      }

   /**
    * @return True if the right bumper is depressed, false otherwise
    */
   public boolean bumpRight()
      {
      updateSensors();
      return bumpRight;
      }

   /**
    * @return True if the left wheel is dropped, false otherwise
    */
   public boolean wheelDropLeft()
      {
      updateSensors();
      return wheelDropLeft;
      }

   /**
    * @return True if the right wheel is dropped, false otherwise
    */
   public boolean wheelDropRight()
      {
      updateSensors();
      return wheelDropRight;
      }

   /**
    * @return True if the caster is dropped, false otherwise
    */
   public boolean wheelDropCaster()
      {
      updateSensors();
      return wheelDropCaster;
      }

   /**
    * @return True if the left cliff sensor sees a cliff, false otherwise
    */
   public boolean cliffLeft()
      {
      updateSensors();
      return cliffLeft;
      }

   /**
    * @return True if the front left cliff sensor sees a cliff, false otherwise
    */
   public boolean cliffFrontLeft()
      {
      updateSensors();
      return cliffFrontLeft;
      }

   /**
    * @return True if the right cliff sensor sees a cliff, false otherwise
    */
   public boolean cliffRight()
      {
      updateSensors();
      return cliffRight;
      }

   /**
    * @return True if the front right cliff sensor sees a cliff, false otherwise
    */
   public boolean cliffFrontRight()
      {
      updateSensors();
      return cliffFrontRight;
      }

   /**
    * @return True if a wall is seen on the right side of the robot, false otherwise
    */
   public boolean wallSeen()
      {
      updateSensors();
      return wallSeen;
      }

   /**
    * @return True if an iRobot virtual wall is seen by the central IR sensor, false otherwise
    */
   public boolean virtualWallSeen()
      {
      updateSensors();
      return virtualWallSeen;
      }

   /**
    * @return True if the play button on the Create is pressed, false otherwise
    */
   public boolean playButtonPressed()
      {
      updateSensors();
      return playButtonPressed;
      }

   /**
    * @return True if the advance button on the Create is pressed, false otherwise
    */
   public boolean advanceButtonPressed()
      {
      updateSensors();
      return advanceButtonPressed;
      }

   /**
    * @return The distance in millimeters that the Create has traveled
    */
   public int distance()
      {
      updateSensors();
      return distance;
      }

   /**
    * @return The angle in degrees that the Create has traveled
    */
   public int angle()
      {
      updateSensors();
      return angle;
      }

   /**
    * @return The distance in millimeters the left wheel of the Create has traveled
    */
   public double leftWheelDistance()
      {
      updateSensors();
      return leftWheelDistance;
      }

   /**
    * @return The distance in millimeters the right wheel of the Create has traveled
    */
   public double rightWheelDistance()
      {
      updateSensors();
      return rightWheelDistance;
      }

   /**
    * @return The voltage of the Create's battery pack in millivolts
    */
   public int robotVoltage()
      {
      updateSensors();
      return voltage;
      }

   /**
    * @return The current draw on the Create's battery pack in milliAmps -
    * negative numbers imply current flowing out of the battery
    */
   public int robotCurrent()
      {
      updateSensors();
      return current;
      }

   /**
    * @return The charge remaining in the battery pack in milliAmp-hours.
    */
   public int robotChargeRemaining()
      {
      updateSensors();
      return batteryCharge;
      }
   }