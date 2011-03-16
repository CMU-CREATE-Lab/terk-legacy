package edu.cmu.ri.createlab.TeRK.robot.finch;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerGs;
import edu.cmu.ri.createlab.TeRK.accelerometer.AccelerometerState;
import edu.cmu.ri.createlab.TeRK.finch.FinchState;
import edu.cmu.ri.createlab.TeRK.motor.PositionControllableMotorState;
import edu.cmu.ri.createlab.TeRK.serial.commandline.SerialDeviceCommandLineApplication;
import edu.cmu.ri.mrpl.util.ArrayUtils;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
abstract class BaseCommandLineFinch extends SerialDeviceCommandLineApplication
   {
   private static final Logger LOG = Logger.getLogger(BaseCommandLineFinch.class);
   private static final int THIRTY_SECONDS = 30000;

   private final Runnable enumerationPortsAction =
         new Runnable()
         {
         public void run()
            {
            enumeratePorts();
            }
         };

   private final Runnable connectToFinchAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println("You are already connected to a finch.  Please disconnect first if you want to connect to a different one.");
               }
            else
               {
               final SortedMap<Integer, String> portMap = enumeratePorts();

               if (!portMap.isEmpty())
                  {
                  final Integer index = readInteger("Connect to port number: ");

                  if (index == null)
                     {
                     println("Invalid port");
                     }
                  else
                     {
                     final String serialPortName = portMap.get(index);

                     if (serialPortName != null)
                        {
                        if (!connect(serialPortName))
                           {
                           println("Connection failed!");
                           }
                        }
                     else
                        {
                        println("Invalid port");
                        }
                     }
                  }
               }
            }
         };

   private final Runnable disconnectFromFinchAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            }
         };

   private final Runnable getStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(getState());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(getState());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable fullColorLEDAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer r = readInteger("Red Intensity   [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (r == null || r < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || r > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid red intensity");
                  return;
                  }
               final Integer g = readInteger("Green Intensity [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (g == null || g < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || g > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid green intensity");
                  return;
                  }
               final Integer b = readInteger("Blue Intensity  [" + FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY + ", " + FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY + "]: ");
               if (b == null || b < FinchConstants.FULL_COLOR_LED_DEVICE_MIN_INTENSITY || b > FinchConstants.FULL_COLOR_LED_DEVICE_MAX_INTENSITY)
                  {
                  println("Invalid blue intensity");
                  return;
                  }

               setFullColorLED(r, g, b);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertAccelerometerStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetAccelerometerStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertAccelerometerStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getObstacleDetectorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertObstacleDetectorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetObstacleDetectorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertObstacleDetectorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertPhotoresistorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetPhotoresistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertPhotoresistorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getThermistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertThermistorStateToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetThermistorStateAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertThermistorStateToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable getMotorPositionsAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               println(convertMotorPositionsToString());
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetMotorPositionsAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertMotorPositionsToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorPositionsAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer leftPosition = readInteger("Left Position  [" + FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA + "]: ");
               if (leftPosition == null || leftPosition < FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA || leftPosition > FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA)
                  {
                  println("Invalid position");
                  return;
                  }
               final Integer leftSpeed = readInteger("Left Speed     [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED + "]: ");
               if (leftSpeed == null || leftSpeed < FinchConstants.MOTOR_DEVICE_MIN_SPEED || leftSpeed > FinchConstants.MOTOR_DEVICE_MAX_SPEED)
                  {
                  println("Invalid speed");
                  return;
                  }
               final Integer rightPosition = readInteger("Right Position [" + FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA + "]: ");
               if (rightPosition == null || rightPosition < FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA || rightPosition > FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA)
                  {
                  println("Invalid position");
                  return;
                  }
               final Integer rightSpeed = readInteger("Right Speed    [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED + "]: ");
               if (rightSpeed == null || rightSpeed < FinchConstants.MOTOR_DEVICE_MIN_SPEED || rightSpeed > FinchConstants.MOTOR_DEVICE_MAX_SPEED)
                  {
                  println("Invalid speed");
                  return;
                  }

               if (!setMotorPositions(leftPosition, rightPosition, leftSpeed, rightSpeed))
                  {
                  println("Failed to set the motor positions");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorPositionAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer id = readInteger("Motor    [     0,     1]: ");
               if (id == null || id < 0 || id > 1)
                  {
                  println("Invalid motor id");
                  return;
                  }
               final Integer position = readInteger("Position [" + FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA + "]: ");
               if (position == null || position < FinchConstants.MOTOR_DEVICE_MIN_POSITION_DELTA || position > FinchConstants.MOTOR_DEVICE_MAX_POSITION_DELTA)
                  {
                  println("Invalid position");
                  return;
                  }
               final Integer speed = readInteger("Speed    [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED + "]: ");
               if (speed == null || speed < FinchConstants.MOTOR_DEVICE_MIN_SPEED || speed > FinchConstants.MOTOR_DEVICE_MAX_SPEED)
                  {
                  println("Invalid speed");
                  return;
                  }

               final boolean[] mask = new boolean[2];
               final int[] positionDeltas = new int[2];
               final int[] speeds = new int[2];
               mask[id] = true;
               positionDeltas[id] = position;
               speeds[id] = speed;
               if (!setMotorPositions(mask, positionDeltas, speeds))
                  {
                  println("Failed to set the motor positions");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorPositionsInCentimetersAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {

               final Double leftDistance = readDouble("Left Distance  [" + FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA + "]: ");
               if (leftDistance == null || leftDistance < FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA || leftDistance > FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA)
                  {
                  println("Invalid distance");
                  return;
                  }
               final Double leftSpeed = readDouble("Left Speed     [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC + "]: ");
               if (leftSpeed == null || leftSpeed < FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC || leftSpeed > FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC)
                  {
                  println("Invalid speed");
                  return;
                  }
               final Double rightDistance = readDouble("Right Distance [" + FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA + "]: ");
               if (rightDistance == null || rightDistance < FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA || rightDistance > FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA)
                  {
                  println("Invalid distance");
                  return;
                  }
               final Double rightSpeed = readDouble("Right Speed    [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC + "]: ");
               if (rightSpeed == null || rightSpeed < FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC || rightSpeed > FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC)
                  {
                  println("Invalid speed");
                  return;
                  }

               if (!setMotorPositions(leftDistance, rightDistance, leftSpeed, rightSpeed))
                  {
                  println("Failed to set the motor positions");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorPositionInCentimetersAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer id = readInteger("Motor    [     0,     1]: ");
               if (id == null || id < 0 || id > 1)
                  {
                  println("Invalid motor id");
                  return;
                  }
               final Double distance = readDouble("Distance [" + FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA + ", " + FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA + "]: ");
               if (distance == null || distance < FinchConstants.MOTOR_DEVICE_MIN_DISTANCE_DELTA || distance > FinchConstants.MOTOR_DEVICE_MAX_DISTANCE_DELTA)
                  {
                  println("Invalid distance");
                  return;
                  }
               final Double speed = readDouble("Speed    [     " + FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC + ",    " + FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC + "]: ");
               if (speed == null || speed < FinchConstants.MOTOR_DEVICE_MIN_SPEED_CM_PER_SEC || speed > FinchConstants.MOTOR_DEVICE_MAX_SPEED_CM_PER_SEC)
                  {
                  println("Invalid speed");
                  return;
                  }

               final boolean[] mask = new boolean[2];
               final double[] distanceDeltas = new double[2];
               final double[] speeds = new double[2];
               mask[id] = true;
               distanceDeltas[id] = distance;
               speeds[id] = speed;
               if (!setMotorPositions(mask, distanceDeltas, speeds))
                  {
                  println("Failed to set the motor positions");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetMotorStatesAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertMotorStatesToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorVelocitiesAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer leftVelocity = readInteger("Left Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
               if (leftVelocity == null || leftVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY || leftVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)
                  {
                  println("Invalid velocity");
                  return;
                  }
               final Integer rightVelocity = readInteger("Right Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
               if (rightVelocity == null || rightVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY || rightVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)
                  {
                  println("Invalid velocity");
                  return;
                  }

               if (!setMotorVelocities(leftVelocity, rightVelocity))
                  {
                  println("Failed to set the motor velocities");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorVelocityAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer id = readInteger("Motor    [  0,  1]: ");
               if (id == null || id < 0 || id > 1)
                  {
                  println("Invalid motor id");
                  return;
                  }
               final Integer velocity = readInteger("Velocity [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY + "]: ");
               if (velocity == null || velocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY || velocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY)
                  {
                  println("Invalid velocity");
                  return;
                  }

               final boolean[] mask = new boolean[2];
               final int[] velocities = new int[2];
               mask[id] = true;
               velocities[id] = velocity;
               if (!setMotorVelocity(mask, velocities))
                  {
                  println("Failed to set the motor velocities");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorVelocitiesInCentimetersPerSecondAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Double leftVelocity = readDouble("Left Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC + "]: ");
               if (leftVelocity == null || leftVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC || leftVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC)
                  {
                  println("Invalid velocity");
                  return;
                  }
               final Double rightVelocity = readDouble("Right Velocity  [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC + "]: ");
               if (rightVelocity == null || rightVelocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC || rightVelocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC)
                  {
                  println("Invalid velocity");
                  return;
                  }

               if (!setMotorVelocities(leftVelocity, rightVelocity))
                  {
                  println("Failed to set the motor velocities");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable setMotorVelocityInCentimetersPerSecondAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer id = readInteger("Motor    [  0,  1]: ");
               if (id == null || id < 0 || id > 1)
                  {
                  println("Invalid motor id");
                  return;
                  }
               final Double velocity = readDouble("Velocity [" + FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC + ", " + FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC + "]: ");
               if (velocity == null || velocity < FinchConstants.MOTOR_DEVICE_MIN_VELOCITY_CM_PER_SEC || velocity > FinchConstants.MOTOR_DEVICE_MAX_VELOCITY_CM_PER_SEC)
                  {
                  println("Invalid velocity");
                  return;
                  }

               final boolean[] mask = new boolean[2];
               final double[] velocities = new double[2];
               mask[id] = true;
               velocities[id] = velocity;
               if (!setMotorVelocity(mask, velocities))
                  {
                  println("Failed to set the motor velocities");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable pollingGetMotorVelocitiesAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               poll(
                     new Runnable()
                     {
                     public void run()
                        {
                        println(convertMotorVelocitiesToString());
                        }
                     });
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playBuzzerToneAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer frequency = readInteger("Frequency (hz) [" + FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY + ", " + FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY + "]: ");
               if (frequency == null || frequency < FinchConstants.BUZZER_DEVICE_MIN_FREQUENCY || frequency > FinchConstants.BUZZER_DEVICE_MAX_FREQUENCY)
                  {
                  println("Invalid frequency");
                  return;
                  }
               final Integer duration = readInteger("Duration  (ms) [" + FinchConstants.BUZZER_DEVICE_MIN_DURATION + ", " + FinchConstants.BUZZER_DEVICE_MAX_DURATION + "]: ");
               if (duration == null || duration < FinchConstants.BUZZER_DEVICE_MIN_DURATION || duration > FinchConstants.BUZZER_DEVICE_MAX_DURATION)
                  {
                  println("Invalid duration");
                  return;
                  }

               playBuzzerTone(frequency, duration);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playToneAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final Integer freq = readInteger("Frequency   (hz): ");
               if (freq == null || freq < FinchConstants.AUDIO_DEVICE_MIN_FREQUENCY)
                  {
                  println("Invalid frequency");
                  return;
                  }
               final Integer amp = readInteger("Amplitude [" + FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE + ", " + FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE + "]: ");
               if (amp == null || amp < FinchConstants.AUDIO_DEVICE_MIN_AMPLITUDE || amp > FinchConstants.AUDIO_DEVICE_MAX_AMPLITUDE)
                  {
                  println("Invalid amplitude");
                  return;
                  }
               final Integer dur = readInteger("Duration    (ms): ");
               if (dur == null || dur < FinchConstants.AUDIO_DEVICE_MIN_DURATION)
                  {
                  println("Invalid duration");
                  return;
                  }

               playTone(freq, amp, dur);
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable playClipAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               final String filePath = readString("Absolute path to sound file: ");
               if (filePath == null || filePath.length() == 0)
                  {
                  println("Invalid path");
                  return;
                  }

               final File file = new File(filePath);
               if (file.exists() && file.isFile())
                  {
                  final byte[] data;
                  try
                     {
                     data = FileUtils.getFileAsBytes(file);
                     playClip(data);
                     }
                  catch (IOException e)
                     {
                     final String msg = "Error reading sound file (" + e.getMessage() + ")";
                     println(msg);
                     }
                  }
               else
                  {
                  println("Invalid path");
                  }
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable emergencyStopAction =
         new Runnable()
         {
         public void run()
            {
            if (isInitialized())
               {
               emergencyStop();
               }
            else
               {
               println("You must be connected to a finch first.");
               }
            }
         };

   private final Runnable quitAction =
         new Runnable()
         {
         public void run()
            {
            disconnect();
            println("Bye!");
            }
         };

   BaseCommandLineFinch(final BufferedReader in)
      {
      super(in);

      registerAction("?", enumerationPortsAction);
      registerAction("c", connectToFinchAction);
      registerAction("d", disconnectFromFinchAction);
      registerAction("g", getStateAction);
      registerAction("G", pollingGetStateAction);
      registerAction("f", fullColorLEDAction);
      registerAction("a", getAccelerometerStateAction);
      registerAction("A", pollingGetAccelerometerStateAction);
      registerAction("o", getObstacleDetectorStateAction);
      registerAction("O", pollingGetObstacleDetectorStateAction);
      registerAction("l", getPhotoresistorStateAction);
      registerAction("L", pollingGetPhotoresistorStateAction);
      registerAction("h", getThermistorStateAction);
      registerAction("H", pollingGetThermistorStateAction);
      registerAction("m", getMotorPositionsAction);
      registerAction("M", pollingGetMotorPositionsAction);
      registerAction("p", setMotorPositionsAction);
      registerAction("p1", setMotorPositionAction);
      registerAction("pc", setMotorPositionsInCentimetersAction);
      registerAction("pc1", setMotorPositionInCentimetersAction);
      registerAction("P", pollingGetMotorStatesAction);
      registerAction("v", setMotorVelocitiesAction);
      registerAction("v1", setMotorVelocityAction);
      registerAction("vc", setMotorVelocitiesInCentimetersPerSecondAction);
      registerAction("vc1", setMotorVelocityInCentimetersPerSecondAction);
      registerAction("V", pollingGetMotorVelocitiesAction);
      registerAction("b", playBuzzerToneAction);
      registerAction("t", playToneAction);
      registerAction("s", playClipAction);
      registerAction("x", emergencyStopAction);
      registerAction(QUIT_COMMAND, quitAction);
      }

   protected final void menu()
      {
      println("COMMANDS -----------------------------------");
      println("");
      println("?         List all available serial ports");
      println("");
      println("c         Connect to the finch on the given serial port");
      println("d         Disconnect from the finch");
      println("");
      println("g         Get the finch's current state");
      println("G         Continuously poll the finch's current state");
      println("");
      println("f         Control the full-color LED");
      println("a         Get the accelerometer state");
      println("A         Continuously poll the accelerometer for 30 seconds");
      println("o         Get the state of the obstacle detectors");
      println("O         Continuously poll the obstacle detectors for 30 seconds");
      println("l         Get the state of the photoresistors");
      println("L         Continuously poll the photoresistors for 30 seconds");
      println("h         Get the state of the thermistor");
      println("H         Continuously poll the thermistor for 30 seconds");
      println("m         Get the current motor positions");
      println("M         Continuously poll the current motor positions for 30 seconds");
      println("p         Set the position of both of the motors (in ticks)");
      println("p1        Set the position of only one of the motors (in ticks)");
      println("pc        Set the position of both of the motors (in centimeters)");
      println("pc1       Set the position of only one of the motors (in centimeters)");
      println("P         Continuously poll the current (position-controlled) motor states for 30 seconds");
      println("v         Set the velocity of both of the motors (in native units)");
      println("v1        Set the velocity of only one of the motors (in native units)");
      println("vc        Set the velocity of both of the motors (in cm/s)");
      println("vc1       Set the velocity of only one of the motors (in cm/s)");
      println("V         Continuously poll the current (velocity-controlled) motor states for 30 seconds");
      println("");
      println("b         Play a tone using the finch's buzzer");
      println("t         Play a tone using the computer's speaker");
      println("s         Play a sound clip using the computer's speaker");
      println("");
      println("x         Turn motors and LED off");
      println("q         Quit");
      println("");
      println("--------------------------------------------");
      }

   private String convertAccelerometerStateToString()
      {
      return "Accelerometer:" + getAccelerometer() + " = " + getAccelerometerGs();
      }

   private String convertObstacleDetectorStateToString()
      {
      return "Obstacle Detectors: " + ArrayUtils.arrayToString(getObstacleDetectors());
      }

   private String convertPhotoresistorStateToString()
      {
      return "Photoresistors: " + ArrayUtils.arrayToString(getPhotoresistors());
      }

   private String convertThermistorStateToString()
      {
      return "Thermistor: " + getThermistor() + " = " + getThermistorCelsiusTemperature() + " degrees C";
      }

   private String convertMotorPositionsToString()
      {
      return "Motor Positions: " + ArrayUtils.arrayToString(getCurrentMotorPositions()) + " (ticks) " + ArrayUtils.arrayToString(getCurrentMotorPositionsInCentimeters()) + " (cm)";
      }

   private String convertMotorVelocitiesToString()
      {
      return "Motor Velocities: " + ArrayUtils.arrayToString(getCurrentMotorVelocities()) + " (native) " + ArrayUtils.arrayToString(getCurrentMotorVelocitiesInCentimetersPerSecond()) + " (cm/s)";
      }

   private String convertMotorStatesToString()
      {
      final PositionControllableMotorState[] states = getCurrentMotorStates();
      final StringBuffer s = new StringBuffer();
      for (final PositionControllableMotorState state : states)
         {
         s.append(" ").append(state);
         }
      return "Motor States: " + s;
      }

   private void poll(final Runnable strategy)
      {
      final long startTime = System.currentTimeMillis();
      while (System.currentTimeMillis() - startTime < THIRTY_SECONDS)
         {
         strategy.run();
         try
            {
            Thread.sleep(30);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }
      }

   protected abstract boolean connect(final String serialPortName);

   protected abstract FinchState getState();

   protected abstract void setFullColorLED(final int r, final int g, final int b);

   protected abstract AccelerometerState getAccelerometer();

   protected abstract AccelerometerGs getAccelerometerGs();

   protected abstract boolean[] getObstacleDetectors();

   protected abstract int[] getPhotoresistors();

   protected abstract int getThermistor();

   protected abstract double getThermistorCelsiusTemperature();

   protected abstract int[] getCurrentMotorPositions();

   protected abstract double[] getCurrentMotorPositionsInCentimeters();

   protected abstract int[] getCurrentMotorVelocities();

   protected abstract double[] getCurrentMotorVelocitiesInCentimetersPerSecond();

   protected abstract PositionControllableMotorState[] getCurrentMotorStates();

   protected abstract boolean setMotorPositions(final int leftPositionDelta, final int rightPositionDelta, final int leftSpeed, final int rightSpeed);

   protected abstract boolean setMotorPositions(final boolean[] mask, final int[] positionDeltas, final int[] speeds);

   protected abstract boolean setMotorPositions(final double leftDistanceDelta, final double rightDistanceDelta, final double leftSpeed, final double rightSpeed);

   protected abstract boolean setMotorPositions(final boolean[] mask, final double[] distanceDeltas, final double[] speeds);

   protected abstract boolean setMotorVelocities(final int leftVelocity, final int rightVelocity);

   protected abstract boolean setMotorVelocities(final double leftVelocity, final double rightVelocity);

   protected abstract boolean setMotorVelocity(final boolean[] mask, final int[] velocities);

   protected abstract boolean setMotorVelocity(final boolean[] mask, final double[] velocities);

   protected abstract void playBuzzerTone(final int frequency, final int duration);

   protected abstract void playTone(final int frequency, final int amplitude, final int duration);

   protected abstract void playClip(final byte[] data);

   protected abstract void emergencyStop();

   protected abstract boolean isInitialized();
   }