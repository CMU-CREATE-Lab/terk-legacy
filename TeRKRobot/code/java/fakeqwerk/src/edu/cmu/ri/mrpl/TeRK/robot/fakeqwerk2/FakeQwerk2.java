package edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk2;

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import edu.cmu.ri.mrpl.TeRK.AudioCommand;
import edu.cmu.ri.mrpl.TeRK.AudioMode;
import edu.cmu.ri.mrpl.TeRK.DigitalOutCommand;
import edu.cmu.ri.mrpl.TeRK.LEDCommand;
import edu.cmu.ri.mrpl.TeRK.MotorCommand;
import edu.cmu.ri.mrpl.TeRK.ServoCommand;
import edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk.FakeQwerk;
import edu.cmu.ri.mrpl.TeRK.robot.fakeqwerk.QwerkEventSource.QwerkCommandHelper;

public class FakeQwerk2 extends FakeQwerk
   {
   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new FakeQwerk2();
               }
            });
      }

   private QwerkPanel qwerkPanel;

   public FakeQwerk2()
      {
      super();

      setVisible(false);
      setTitle("Fake Qwerk II");
      setResizable(true);

      // Resize messageTextArea
      messageTextArea.setRows(8);
      messageTextAreaScrollPane.setMinimumSize(new Dimension(1, 1));
      messageTextAreaScrollPane.setMaximumSize(new Dimension(1000, 1000));
      messageTextAreaScrollPane.setPreferredSize(new Dimension(1, 1));
      pack();
      final Dimension messageTextAreaScrollPaneDimensions = new Dimension(messageTextArea.getWidth(), messageTextArea.getHeight());
      messageTextAreaScrollPane.setPreferredSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMinimumSize(messageTextAreaScrollPaneDimensions);
      messageTextAreaScrollPane.setMaximumSize(new Dimension(10000, messageTextArea.getHeight()));

      // Add QwerkPanel
      qwerkPanel = new QwerkPanel();
      rootPanel.add(qwerkPanel);

      pack();
      setLocationRelativeTo(null); // center the window on the screen
      setVisible(true);
      }

   public void handleQwerkEvent(Object command)
      {
      if (command instanceof MotorCommand)
         {
         MotorCommand mCommand = (MotorCommand)command;

         for (int i = 0; i < mCommand.motorMask.length; i++)
            {
            if (mCommand.motorMask[i])
               {
               qwerkPanel.setMotorValue(i, mCommand.motorVelocities[i]);
               }
            }
         }
      else if (command instanceof ServoCommand)
         {
         ServoCommand sCommand = (ServoCommand)command;

         for (int i = 0; i < sCommand.servoMask.length; i++)
            {
            if (sCommand.servoMask[i])
               {
               qwerkPanel.setServoValue(i, sCommand.servoPositions[i]);
               }
            }
         }
      else if (command instanceof DigitalOutCommand)
         {
         DigitalOutCommand dCommand = (DigitalOutCommand)command;

         for (int i = 0; i < dCommand.digitalOutMask.length; i++)
            {
            if (dCommand.digitalOutMask[i])
               {
               qwerkPanel.setDigitalOutValue(i, dCommand.digitalOutValues[i]);
               }
            }
         }
      else if (command instanceof LEDCommand)
         {
         LEDCommand lCommand = (LEDCommand)command;

         for (int i = 0; i < lCommand.ledMask.length; i++)
            {
            if (lCommand.ledMask[i])
               {
               qwerkPanel.setLEDValue(i, lCommand.ledModes[i]);
               }
            }
         }
      else if (command instanceof AudioCommand)
         {
         AudioCommand aCommand = (AudioCommand)command;

         String value;
         if (AudioMode.AudioTone.equals(aCommand.mode))
            {
            value = "f=" + aCommand.frequency + ",a=" + aCommand.amplitude + ",d=" + aCommand.duration;
            }
         else if (AudioMode.AudioClip.equals(aCommand.mode))
            {
            value = (aCommand.sound == null) ? "none" : (aCommand.sound.length + "bytes");
            }
         else
            {
            value = "none";
            }

         qwerkPanel.setSpeakerValue(value);
         }
      else if (command instanceof QwerkCommandHelper)
         {
         QwerkCommandHelper qCommand = (QwerkCommandHelper)command;

         if (qCommand.equals(QwerkCommandHelper.DISCONNECTED))
            {
            qwerkPanel.resetValues();
            }
         else if (qCommand.equals(QwerkCommandHelper.CONNECTED))
            {

            }
         }

      qwerkPanel.repaint();
      }
   }
