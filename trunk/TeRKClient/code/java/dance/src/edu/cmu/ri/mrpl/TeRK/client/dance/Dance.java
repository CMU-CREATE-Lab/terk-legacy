package edu.cmu.ri.mrpl.TeRK.client.dance;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.PropertyResourceBundle;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorBuffer;
import edu.cmu.ri.createlab.TeRK.userinterface.GUIConstants;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.BaseGUIClient;
import edu.cmu.ri.mrpl.TeRK.client.components.framework.GUIClientHelperEventHandlerAdapter;
import edu.cmu.ri.mrpl.TeRK.client.dance.bufferio.MotorBufferIO;
import edu.cmu.ri.mrpl.TeRK.client.dance.effects.MotorBufferEffect;
import edu.cmu.ri.mrpl.TeRK.client.dance.effects.MotorEffectFactory;
import org.apache.log4j.Logger;

public final class Dance extends BaseGUIClient
   {
   private static final Logger LOG = Logger.getLogger(Dance.class);

   /** Properties file used to setup Ice for this application */
   private static final String ICE_DIRECT_CONNECT_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/dance/Dance.direct-connect.ice.properties";
   private static final String ICE_RELAY_PROPERTIES_FILE = "/edu/cmu/ri/mrpl/TeRK/client/dance/Dance.relay.ice.properties";

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(Dance.class.getName());

   /** The application name (appears in the title bar) */
   private static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private final JButton startStopButton = new JButton();
   private final JButton playButton = new JButton();
   private final JList selectedMotors = new JList(new Object[]{"Motor 0", "Motor 1", "Motor 2", "Motor 3"});//TODO: query the number of motors
   private final JMenu fileMenu = new JMenu();
   private final JMenuItem saveBuffer = new JMenuItem();
   private final JMenuItem openBuffer = new JMenuItem();
   private final JMenuItem exitProgram = new JMenuItem();
   private final MotorBufferEffect[] effects = MotorEffectFactory.getBufferEffects();
   private final JComboBox effectChoice = new JComboBox(effects);

   private BackEMFMotorBuffer[] motorBuffers = null;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new Dance();
               }
            });
      }

   private Dance()
      {
      super(APPLICATION_NAME, ICE_RELAY_PROPERTIES_FILE, ICE_DIRECT_CONNECT_PROPERTIES_FILE);
      setGUIClientHelperEventHandler(
            new GUIClientHelperEventHandlerAdapter()
            {
            public void toggleGUIElementState(final boolean isConnectedToQwerk)
               {
               startStopButton.setEnabled(isConnectedToQwerk);
               selectedMotors.setEnabled(isConnectedToQwerk);
               openBuffer.setEnabled(isConnectedToQwerk);
               saveBuffer.setEnabled(isConnectedToQwerk);
               selectedMotors.setEnabled(isConnectedToQwerk);
               playButton.setEnabled(isConnectedToQwerk);
               effectChoice.setEnabled(isConnectedToQwerk);
               }
            });

      startStopButton.setFont(GUIConstants.FONT_SMALL);
      playButton.setFont(GUIConstants.FONT_SMALL);
      startStopButton.setText(RESOURCES.getString("button.label.record"));
      playButton.setText(RESOURCES.getString("button.label.play"));
      saveBuffer.setText(RESOURCES.getString("menu.file.save"));
      openBuffer.setText(RESOURCES.getString("menu.file.load"));
      exitProgram.setText(RESOURCES.getString("menu.file.exit"));
      fileMenu.setText(RESOURCES.getString("menu.file.title"));
      fileMenu.add(saveBuffer);
      fileMenu.add(openBuffer);
      fileMenu.addSeparator();
      fileMenu.add(exitProgram);
      saveBuffer.setAccelerator(KeyStroke.getKeyStroke("control S"));
      openBuffer.setAccelerator(KeyStroke.getKeyStroke("control O"));
      saveBuffer.setEnabled(false);
      effectChoice.setEnabled(false);
      openBuffer.setEnabled(false);
      exitProgram.setEnabled(true);
      startStopButton.setEnabled(false);
      playButton.setEnabled(false);
      startStopButton.setOpaque(false);
      playButton.setOpaque(false);

      startStopButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent evt)
         {
         final boolean[] selected = getSelectedMotors();
         if (evt.getActionCommand().equals(RESOURCES.getString("button.label.record")))
            {
            final Thread beginrecording = new Thread(
                  new Runnable()
                  {
                  public void run()
                     {
                     try
                        {
                        getQwerkController().getMotorService().startMotorBufferRecord(selected);
                        }
                     catch (Exception failure)
                        {
                        LOG.error("Error occurred while trying to send start message", failure);
                        return;
                        }

                     EventQueue.invokeLater(
                           new Runnable()
                           {
                           public void run()
                              {
                              startStopButton.setText(RESOURCES.getString("button.label.stop"));
                              playButton.setEnabled(false);
                              effectChoice.setSelectedIndex(0);
                              effectChoice.setEnabled(false);
                              }
                           }
                     );
                     }
                  }
            );
            beginrecording.start();
            }
         else
            {
            final Thread stoprecording = new Thread(
                  new Runnable()
                  {
                  public void run()
                     {
                     try
                        {
                        getQwerkController().getMotorService().stopMotorBufferRecord(selected);
                        final BackEMFMotorBuffer[] buffers = getQwerkController().getMotorService().getMotorBuffers(selected);
                        final BackEMFMotorBuffer[] old = motorBuffers;
                        motorBuffers = new BackEMFMotorBuffer[buffers.length];
                        for (int i = 0; i < buffers.length; i++)
                           {
                           motorBuffers[i] = (selected[i] || (old == null) || (i >= old.length)) ? buffers[i] : old[i];
                           }
                        }
                     catch (Exception failure)
                        {
                        LOG.error("Error occurred while trying to send stop message", failure);
                        return;
                        }

                     EventQueue.invokeLater(
                           new Runnable()
                           {
                           public void run()
                              {
                              startStopButton.setText(RESOURCES.getString("button.label.record"));
                              playButton.setEnabled(true);
                              effectChoice.setEnabled(true);
                              }
                           }
                     );
                     }
                  });
            stoprecording.start();
            }
         }
      });

      playButton.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent evt)
         {
         final Thread playback = new Thread(
               new Runnable()
               {
               public void run()
                  {
                  try
                     {
                     getQwerkController().getMotorService().playMotorBuffer(getSelectedMotors());
                     }
                  catch (Exception failure)
                     {
                     LOG.error("Error occurred while trying to send play message", failure);
                     }
                  }
               });
         playback.start();
         }
      });

      saveBuffer.addActionListener(new ActionListener()
      {

      public void actionPerformed(final ActionEvent evt)
         {
         final boolean[] mask = getSelectedMotors();
         final Thread performAction = new Thread(
               new Runnable()
               {
               public void run()
                  {
                  longAction(mask);
                  }
               }
         );
         performAction.start();
         }

      private void longAction(final boolean[] mask)
         {
         if (motorBuffers == null)
            {
            return;
            }

         //select the file
         final JFileChooser fileChooser = new JFileChooser();//show  dialog
         if (fileChooser.showSaveDialog(null) == JFileChooser.CANCEL_OPTION)
            {
            return;
            }
         File f = fileChooser.getSelectedFile();
         final String fileName = f.toString();//append extension
         if (!fileName.endsWith(".dnc") && !fileName.endsWith(".dance"))
            {
            f = new File(fileName + ".dance");
            }

         //write buffer information
         try
            {
            final DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            MotorBufferIO.write(out, new MotorBufferIO.MotorBufferData(mask, motorBuffers));
            out.close();
            }
         catch (IOException writefailure)
            {
            JOptionPane.showMessageDialog(null, "Could not save motor states to the file. " + writefailure.getMessage(), "Input/Output Failure ", JOptionPane.ERROR_MESSAGE);
            LOG.error("Error occurred while writing motor buffers to the file.", writefailure);
            }

         LOG.info("Save complete.");
         }
      }
      );

      openBuffer.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent evt)
         {
         final boolean[] mask = getSelectedMotors();
         final Thread performAction = new Thread(
               new Runnable()
               {
               public void run()
                  {
                  longAction(mask);
                  }
               }
         );
         performAction.start();
         }

      private void longAction(final boolean[] selectedmask)
         {
         //select the file
         final JFileChooser fileChooser = new JFileChooser();//show  dialog
         if (fileChooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION)
            {
            return;
            }
         final File f = fileChooser.getSelectedFile();

         //read buffer information
         boolean[] mask = null;
         BackEMFMotorBuffer[] buffers = null;
         try
            {
            final DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(f)));
            final MotorBufferIO.MotorBufferData data = MotorBufferIO.read(in);
            mask = data.getMask();
            buffers = data.getBuffers();
            in.close();
            }
         catch (IOException readfailure)
            {
            LOG.error("Error occurred while reading motor buffers from the file.", readfailure);
            if ((mask == null) || (buffers == null))
               {
               JOptionPane.showMessageDialog(null, "Could not read motor states from the file. " + readfailure.getMessage(), "Input/Output Failure ", JOptionPane.ERROR_MESSAGE);
               return;
               }
            }

         //set the motor buffers
         try
            {
            for (int i = 0; i < selectedmask.length; i++)
               {
               selectedmask[i] = (selectedmask[i] && ((i < mask.length) && (mask[i])));
               }
            getQwerkController().getMotorService().setMotorBuffer(selectedmask, buffers);
            }
         catch (Exception networkfailure)
            {
            JOptionPane.showMessageDialog(null, "Could not send motor data across the network. " + networkfailure.getMessage(), "Remote Failure", JOptionPane.ERROR_MESSAGE);
            LOG.error("Error occurred while trying to send motor buffers", networkfailure);
            return;
            }
         LOG.info("Load complete");

         EventQueue.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  playButton.setEnabled(true);
                  }
               }
         );

         if (motorBuffers == null)
            {
            motorBuffers = buffers;
            }
         else
            {
            for (int i = 0; i < buffers.length; i++)
               {
               if (selectedmask[i] || (motorBuffers[i] == null))
                  {
                  motorBuffers[i] = buffers[i];
                  }
               }
            }
         }
      }
      );

      exitProgram.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent evt)
         {
         performQuitAction();
         }
      }
      );

      effectChoice.addItemListener(new ItemListener()
      {
      public void itemStateChanged(final ItemEvent evt)
         {
         applyMotorEffect();
         }
      }
      );

      effectChoice.addActionListener(new ActionListener()
      {
      public void actionPerformed(final ActionEvent evt)
         {
         applyMotorEffect();
         }
      }
      );

      final JMenuBar mainMenu = new JMenuBar();
      mainMenu.add(fileMenu);
      setJMenuBar(mainMenu);
      getMainContentPane().setLayout(new BorderLayout());
      getMainContentPane().add(getConnectDisconnectButton(), BorderLayout.NORTH);

      selectedMotors.setEnabled(false);

      final JPanel centerpane = new JPanel();
      final JPanel buttonPane = new JPanel();
      buttonPane.add(startStopButton);
      buttonPane.add(playButton);

      centerpane.add(selectedMotors);
      centerpane.add(buttonPane);
      getMainContentPane().add(new JScrollPane(centerpane), BorderLayout.CENTER);
      getMainContentPane().add(effectChoice, BorderLayout.SOUTH);

      pack();

      setLocationRelativeTo(null);// center the window on the screen

      setVisible(true);
      }

   private boolean[] getSelectedMotors()
      {
      final boolean[] mask = new boolean[4];//TODO: query motor count
      final int[] selectedindices = selectedMotors.getSelectedIndices();
      Arrays.fill(mask, false);
      for (int i = 0; i < selectedindices.length; i++)
         {
         if (selectedindices[i] < mask.length)
            {
            mask[selectedindices[i]] = true;
            }
         }
      return mask;
      }

   private void applyMotorEffect()
      {
      final boolean[] smask = getSelectedMotors();
      final Thread performAction = new Thread(
            new Runnable()
            {
            public void run()
               {
               while (motorBuffers == null)
                  {
                  try
                     {
                     motorBuffers = getQwerkController().getMotorService().getMotorBuffers(smask);
                     }
                  catch (Exception failure)
                     {
                     LOG.error("Error occurred while trying to send stop message", failure);
                     return;
                     }
                  }

               try
                  {
                  final BackEMFMotorBuffer[] buffers = new BackEMFMotorBuffer[motorBuffers.length];
                  for (int i = 0; i < buffers.length; i++)
                     {
                     final int[] val = new int[motorBuffers[i].size()];
                     System.arraycopy(motorBuffers[i].getValues(), 0, val, 0, val.length);
                     buffers[i] = new BackEMFMotorBuffer(val);
                     }
                  getQwerkController().getMotorService().setMotorBuffer(smask, effects[effectChoice.getSelectedIndex()].transform(buffers));
                  }
               catch (Exception networkfailure)
                  {
                  JOptionPane.showMessageDialog(null, "Could not send motor data across the network. " + networkfailure.getMessage(), "Remote Failure", JOptionPane.ERROR_MESSAGE);
                  LOG.error("Error occurred while trying to send motor buffers", networkfailure);
                  }
               }
            }
      );
      performAction.start();
      }
   }
