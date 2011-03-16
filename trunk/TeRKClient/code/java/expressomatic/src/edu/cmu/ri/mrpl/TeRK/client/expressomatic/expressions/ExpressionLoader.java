package edu.cmu.ri.mrpl.TeRK.client.expressomatic.expressions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Ice.UnknownLocalException;
import edu.cmu.ri.createlab.TeRK.AsynchronousCommandExceptionHandlerCallback;
import edu.cmu.ri.createlab.TeRK.audio.AudioService;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.createlab.TeRK.led.LEDMode;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.mrpl.TeRK.AudioCommandException;
import edu.cmu.ri.mrpl.TeRK.client.components.services.QwerkController;
import edu.cmu.ri.mrpl.TeRK.client.expressomatic.ExpressOMatic;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AudioCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DefaultCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DigitalOutCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.LEDCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.LEDCell.LED_STATE;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.MotorCell;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.ServoCell;
import edu.cmu.ri.mrpl.util.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * This class translates the robotic commands from DefaultCell to the Services
 * accessed by QwerkController
 *
 * @author Mel Ludowise
 */
public class ExpressionLoader
   {
   private static final Logger LOG = Logger.getLogger(ExpressionLoader.class);

   private static final ExpressionLoader INSTANCE = new ExpressionLoader();

   private static final File AUDIO_DIRECTORY = new File(ExpressOMatic.TERK_PATH + "Audio");

   public static ExpressionLoader getInstance()
      {
      return INSTANCE;
      }

   private final AsynchronousCommandExceptionHandlerCallback callback = new MyAsynchronousCommandExceptionHandlerCallback();

   private ExpressionLoader()
      {
      // private to prevent instantiation
      }

   public void loadToQwerk(final QwerkController qwerkController, final Expression newExpression, final ExpressionSpeed speed)
      {
      //		stopQwerk(qwerkController);

      final List<DefaultCell> cells = newExpression.getComponentCells();

      if ((cells != null) && (!cells.isEmpty()))
         {
         final List<MotorCell> motorCellList = new ArrayList<MotorCell>();
         final List<ServoCell> servoCellList = new ArrayList<ServoCell>();
         final List<DigitalOutCell> digitalOutList = new ArrayList<DigitalOutCell>();

         for (final DefaultCell c : cells)
            {
            if (c instanceof LEDCell)
               {
               loadLED(qwerkController.getLEDService(), (LEDCell)c);
               }
            else if (c instanceof MotorCell)
               {
               motorCellList.add((MotorCell)c);
               }
            else if (c instanceof ServoCell)
               {
               servoCellList.add((ServoCell)c);
               }
            else if (c instanceof AudioCell)
               {
               loadAudio(qwerkController.getAudioService(), (AudioCell)c);
               }
            else if (c instanceof DigitalOutCell)
               {
               digitalOutList.add((DigitalOutCell)c);
               }
            }

         if (motorCellList.size() > 0)
            {
            this.loadMotors(qwerkController.getMotorService(), motorCellList);
            }
         if (servoCellList.size() > 0)
            {
            this.loadServos(qwerkController.getServoService(), servoCellList, speed);
            }
         if (digitalOutList.size() > 0)
            {
            loadDigitalOuts(qwerkController.getDigitalOutService(), digitalOutList);
            }
         }
      }

   protected void loadDigitalOuts(final DigitalOutService digitalOutService, final List<DigitalOutCell> digitalOutCells)
      {
      final int deviceCount = digitalOutService.getDeviceCount();
      final boolean[] mask = new boolean[deviceCount];
      final boolean[] states = new boolean[deviceCount];
      Arrays.fill(mask, false);
      Arrays.fill(states, false);

      for (final DigitalOutCell digitalOutCell : digitalOutCells)
         {
         final int deviceId = digitalOutCell.getDeviceId();
         if (deviceId >= 0 && deviceId < mask.length)
            {
            mask[deviceId] = true;
            states[deviceId] = digitalOutCell.getIsOn();
            }
         else
            {
            LOG.warn("Ignoring invalid digital out id [" + deviceId + "]");
            }
         }

      digitalOutService.execute(mask, states);
      }

   protected void loadLED(final LEDService ledService, final LEDCell ledCell)
      {
      final boolean[] mask = {true, true, true, true, true, true, true, true};
      final LEDMode[] modes = {LEDState2Mode(ledCell.getLedState(0)),
                               LEDState2Mode(ledCell.getLedState(1)),
                               LEDState2Mode(ledCell.getLedState(2)),
                               LEDState2Mode(ledCell.getLedState(3)),
                               LEDState2Mode(ledCell.getLedState(4)),
                               LEDState2Mode(ledCell.getLedState(5)),
                               LEDState2Mode(ledCell.getLedState(6)),
                               LEDState2Mode(ledCell.getLedState(7)),
      };

      ledService.execute(mask, modes);
      }

   protected void loadMotors(final BackEMFMotorService motorService, final List<MotorCell> motorCellList)
      {
      final boolean[] motorMask = new boolean[motorService.getDeviceCount()];
      final int[] motorVelocities = new int[motorService.getDeviceCount()];

      Arrays.fill(motorMask, false);

      for (final MotorCell motorCell : motorCellList)
         {
         if (motorCell.getDeviceId() >= 0 && motorCell.getDeviceId() < motorMask.length)
            {
            motorMask[motorCell.getDeviceId()] = true;
            motorVelocities[motorCell.getDeviceId()] = motorCell.getVelocity();
            }
         }

      motorService.setMotorVelocities(motorMask, motorVelocities);
      }

   protected void loadServos(final ServoService servoService, final List<ServoCell> servoCellList, final ExpressionSpeed speed)
      {
      final boolean[] servoMask = new boolean[servoService.getDeviceCount()];
      final int[] servoPositions = new int[servoService.getDeviceCount()];
      final int[] servoVelocities = new int[servoService.getDeviceCount()];

      Arrays.fill(servoMask, false);

      for (final ServoCell servoCell : servoCellList)
         {
         if (servoCell.getDeviceId() >= 0 && servoCell.getDeviceId() < servoMask.length)
            {
            servoMask[servoCell.getDeviceId()] = true;
            servoPositions[servoCell.getDeviceId()] = servoCell.getPosition();
            servoVelocities[servoCell.getDeviceId()] = speed.getServoVelocity();
            }
         }

      servoService.setPositionsWithSpeeds(servoMask, servoPositions, servoVelocities);
      }

   public void loadAudio(final AudioService audioService, final AudioCell audioCell)
      {
      if (!AUDIO_DIRECTORY.exists() && AUDIO_DIRECTORY.isDirectory())
         {
         AUDIO_DIRECTORY.mkdirs();
         }

      try
         {
         final File audioFile = new File(AUDIO_DIRECTORY, audioCell.getAudio());
         LOG.debug("loading audio file [" + audioFile + "]");
         final byte[] sound = FileUtils.getFileAsBytes(audioFile);
         audioService.playSoundAsynchronously(sound, callback);
         }
      catch (IOException e)
         {
         LOG.error("IOException while loading the sound file", e);
         }
      }

   @SuppressWarnings({"PrimitiveArrayArgumentToVariableArgMethod"})
   public void stopQwerk(final QwerkController qwerkController)
      {
      final int[] motors = new int[qwerkController.getMotorService().getDeviceCount()];
      for (int i = 0; i < motors.length; i++)
         {
         motors[i] = i;
         }

      qwerkController.getMotorService().stopMotors(motors);
      qwerkController.getServoService().stopServos();
      }

   private LEDMode LEDState2Mode(final LED_STATE state)
      {
      if (state == LED_STATE.INDEX_BLINK)
         {
         return LEDMode.Blinking;
         }
      else if (state == LED_STATE.INDEX_ON)
         {
         return LEDMode.On;
         }
      else
         {//state == LED_STATE.INDEX_OFF
         return LEDMode.Off;
         }
      }

   private final class MyAsynchronousCommandExceptionHandlerCallback extends AsynchronousCommandExceptionHandlerCallback
      {
      public void handleException(final Exception exception)
         {
         showError(exception);
         }

      private void showError(final Exception e)
         {
         if (e instanceof AudioCommandException)
            {
            LOG.error("AudioCommandException caught while playing the sound: ", e);
            }
         else
            {
            if (e instanceof UnknownLocalException)// ignore the stupid timeout exceptions
               {
               LOG.info("Ignoring UnknownLocalException caught while playing the sound: ", e);
               }
            else
               {
               LOG.error("Exception caught while playing the sound: ", e);
               }
            }
         }
      }
   }
