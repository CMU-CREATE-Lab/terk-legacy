package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import edu.cmu.ri.mrpl.TeRK.LEDMode;

public class QwerkPanel extends JPanel
   {
   // TODO: This path should be changed to not have "code/java..." in it
   private String imgPath = "code/java/fakeqwerk/src/edu/cmu/ri/mrpl/TeRK/robot/fakeqwerk2/qwerk_pic.jpg";
   private BufferedImage img;
   private Point imgOrigin;

   private final static Font DEFAULT_FONT = new Font("Verdana", Font.PLAIN, 12);

   private final static int IMG_PADDING = 70;
   private final static int TEXT_PADDING = 5;

   private final static int MOTOR_INIT_Y = 337;
   private final static int MOTOR_SPACING = -23;

   private final static int SERVO_INIT_X = 373;
   private final static int SERVO_SPACING = -23;

   private final static int ANALOG_INIT_Y = 50;
   private final static int ANALOG_SPACING = 23;

   private final static int DIGITALO_INIT_Y = 116;
   private final static int DIGITALO_SPACING = -22;

   private final static int DIGITALI_INIT_Y = 217;
   private final static int DIGITALI_SPACING = -22;

   private final static int LED_INIT_Y = 296;
   private final static int LED_INIT_X = 70;
   private final static int LED_SPACING = 27;

   private final static int SPEAKER_INIT_Y = 296;
   private final static int SPEAKER_INIT_X = 278;

   private String[] motorValues = new String[4];
   private String[] servoValues = new String[16];
   private String[] analogInputValues = new String[8];
   private String[] digitalOutValues = new String[4];
   private String[] digitalInValues = new String[4];
   private String[] ledValues = new String[8];
   private String speakerValue;

   public QwerkPanel()
      {
      super();
      try
         {
         imgPath.replaceAll("/", File.separator);
         img = ImageIO.read(new File(imgPath));

         System.out.println("[DEBUG] filepath: " + (new File(".")).getAbsoluteFile());

         setSize(img.getWidth() + 2 * IMG_PADDING, img.getHeight() + IMG_PADDING);
         setPreferredSize(new Dimension(img.getWidth() + 2 * IMG_PADDING, img.getHeight() + IMG_PADDING));
         }
      catch (Exception e)
         {
         e.printStackTrace();
         img = new BufferedImage(402, 385, BufferedImage.TYPE_4BYTE_ABGR);
         }
      resetValues();
      }

   public void paintComponent(Graphics g)
      {
      if (g instanceof Graphics2D)
         {
         Graphics2D g2d = (Graphics2D)g;

         g2d.setColor(Color.WHITE);
         g2d.fillRect(0, 0, getWidth(), getHeight());

         if (img != null)
            {
            imgOrigin = new Point((getWidth() - img.getWidth()) / 2, 0);
            g2d.drawImage(img, (int)imgOrigin.getX(), (int)imgOrigin.getY(), this);
            }

         g2d.setFont(DEFAULT_FONT);
         g2d.setColor(Color.BLACK);
         FontMetrics fontMetrics = g.getFontMetrics();
         AffineTransform oldTransform;

         for (int i = 0; i < motorValues.length; i++)
            {
            g2d.drawString(motorValues[i],
                           (int)imgOrigin.getX() + img.getWidth() + TEXT_PADDING,
                           (int)imgOrigin.getY() + MOTOR_INIT_Y + i * MOTOR_SPACING);
            }

         for (int i = 0; i < servoValues.length; i++)
            {
            oldTransform = g2d.getTransform();
            g2d.transform(AffineTransform.getTranslateInstance(
                  (int)imgOrigin.getX() + SERVO_INIT_X + i * SERVO_SPACING,
                  (int)imgOrigin.getY() + img.getHeight() + TEXT_PADDING));

            g2d.transform(AffineTransform.getRotateInstance(-Math.PI / 2));

            g2d.drawString(servoValues[i], -fontMetrics.stringWidth(servoValues[i]), 0);
            g2d.setTransform(oldTransform);
            }

         for (int i = 0; i < analogInputValues.length; i++)
            {
            g2d.drawString(analogInputValues[i],
                           (int)imgOrigin.getX() - TEXT_PADDING - fontMetrics.stringWidth(analogInputValues[i]),
                           (int)imgOrigin.getY() + ANALOG_INIT_Y + i * ANALOG_SPACING);
            }

         for (int i = 0; i < digitalOutValues.length; i++)
            {
            g2d.drawString(digitalOutValues[i],
                           (int)imgOrigin.getX() + img.getWidth() + TEXT_PADDING,
                           (int)imgOrigin.getY() + DIGITALO_INIT_Y + i * DIGITALO_SPACING);
            }

         for (int i = 0; i < digitalInValues.length; i++)
            {
            g2d.drawString(digitalInValues[i],
                           (int)imgOrigin.getX() + img.getWidth() + TEXT_PADDING,
                           (int)imgOrigin.getY() + DIGITALI_INIT_Y + i * DIGITALI_SPACING);
            }

         for (int i = 0; i < ledValues.length; i++)
            {
            oldTransform = g2d.getTransform();
            g2d.transform(AffineTransform.getTranslateInstance(
                  (int)imgOrigin.getX() + LED_INIT_X + i * LED_SPACING,
                  (int)imgOrigin.getY() + LED_INIT_Y - TEXT_PADDING));
            g2d.transform(AffineTransform.getRotateInstance(-Math.PI / 2));
            g2d.drawString(ledValues[i], 0, 0);
            g2d.setTransform(oldTransform);
            }

         g2d.drawString(speakerValue,
                        (int)imgOrigin.getX() + SPEAKER_INIT_X,
                        (int)imgOrigin.getY() + SPEAKER_INIT_Y - TEXT_PADDING);
         }
      }

   public void resetValues()
      {
      Arrays.fill(motorValues, "");
      Arrays.fill(servoValues, "");
      Arrays.fill(analogInputValues, "");
      Arrays.fill(digitalOutValues, "");
      Arrays.fill(digitalInValues, "");
      Arrays.fill(ledValues, "");
      speakerValue = "";
      }

   public void setMotorValue(int motorId, int velocity)
      {
      motorValues[motorId] = velocity + "";
      }

   public void setServoValue(int servoId, int position)
      {
      servoValues[servoId] = position + "";
      }

   public void setAnalogInputValue(int analogInputId, int value)
      {
      analogInputValues[analogInputId] = value + "";
      }

   public void setDigitalOutValue(int digitalOutId, boolean value)
      {
      digitalOutValues[digitalOutId] = value + "";
      }

   public void setDigitalInValue(int digitalInId, boolean value)
      {
      digitalInValues[digitalInId] = value + "";
      }

   public void setLEDValue(int LEDId, LEDMode mode)
      {
      if (mode == LEDMode.LEDBlinking)
         {
         ledValues[LEDId] = "Blink";
         }
      else if (mode == LEDMode.LEDOff)
         {
         ledValues[LEDId] = "Off";
         }
      else if (mode == LEDMode.LEDOn)
         {
         ledValues[LEDId] = "On";
         }
      }

   public void setSpeakerValue(String value)
      {
      speakerValue = value;
      }
   }
