package edu.cmu.ri.createlab.TeRK.robot.hummingbird.serial.connectivity;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.robot.hummingbird.HummingbirdProxyCreator;
import edu.cmu.ri.createlab.serial.device.connectivity.DefaultSerialDeviceConnectivityManagerView;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManager;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerImpl;
import edu.cmu.ri.createlab.serial.device.connectivity.SerialDeviceConnectivityManagerView;
import edu.cmu.ri.mrpl.swing.SwingWorker;

/**
 * Simple demo app which shows how the hummingbird connectivity manager classes can be used.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HummingbirdConnectivityManagerDemo
   {
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(HummingbirdConnectivityManagerDemo.class.getName());

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               new HummingbirdConnectivityManagerDemo();
               }
            });
      }

   private HummingbirdConnectivityManagerDemo()
      {
      // create the JFrame that wraps the view, then display it
      final JFrame jFrame = new JFrame(RESOURCES.getString("application.name"));

      // create the manager and view instances
      final SerialDeviceConnectivityManager serialDeviceConnectivityManager = new SerialDeviceConnectivityManagerImpl(new HummingbirdProxyCreator());
      final SerialDeviceConnectivityManagerView view = new DefaultSerialDeviceConnectivityManagerView(serialDeviceConnectivityManager, jFrame);

      // add the view to the JFrame
      jFrame.add(view.getComponent());

      // set various properties for the JFrame
      jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      jFrame.setBackground(Color.WHITE);
      jFrame.setResizable(true);
      jFrame.addWindowListener(
            new WindowAdapter()
            {
            public void windowClosing(final WindowEvent event)
               {
               // ask if the user really wants to exit
               final int selectedOption = JOptionPane.showConfirmDialog(jFrame,
                                                                        RESOURCES.getString("dialog.message.exit-confirmation"),
                                                                        RESOURCES.getString("dialog.title.exit-confirmation"),
                                                                        JOptionPane.YES_NO_OPTION,
                                                                        JOptionPane.QUESTION_MESSAGE);

               if (selectedOption == JOptionPane.YES_OPTION)
                  {
                  final SwingWorker worker =
                        new SwingWorker()
                        {
                        public Object construct()
                           {
                           serialDeviceConnectivityManager.disconnect();
                           return null;
                           }

                        public void finished()
                           {
                           System.exit(0);
                           }
                        };
                  worker.start();
                  }
               }
            });

      // pack, center on the screen, then make it visible
      jFrame.pack();
      jFrame.setLocationRelativeTo(null);
      jFrame.setVisible(true);
      }
   }