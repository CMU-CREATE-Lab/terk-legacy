package edu.cmu.ri.createlab.TeRK.client.robotdiaries.messenger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.Application;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.FileEntry;
import edu.cmu.ri.mrpl.peer.PeerConnectionEventListener;
import edu.cmu.ri.mrpl.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class Messenger extends JPanel implements Application
   {
   private static final Logger LOG = Logger.getLogger(Messenger.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(Messenger.class.getName());

   public static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   private final RoboticonMessenger roboticonMessenger;

   public static void main(final String[] args)
      {
      //Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               final JFrame jFrame = new JFrame(APPLICATION_NAME);

               // add the root panel to the JFrame
               final Messenger application = new Messenger(jFrame);
               jFrame.add(application);

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
                                    application.shutdown();
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

               jFrame.pack();
               jFrame.setLocationRelativeTo(null);// center the window on the screen
               jFrame.setVisible(true);
               }
            });
      }

   public Messenger(final JFrame jFrame)
      {
      // configure the panel
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

      // ===============================================================================================================
      this.roboticonMessenger = new RoboticonMessenger(jFrame);
      roboticonMessenger.setPreferredSize(new Dimension(580, 580));

      this.add(roboticonMessenger);
      // add the root panel to the JFrame
      jFrame.add(this);
      }

   public String getName()
      {
      return APPLICATION_NAME;
      }

   public Component getComponent()
      {
      return this;
      }

   public void shutdown()
      {
      LOG.debug("Messenger.shutdown()");
      roboticonMessenger.shutdown();
      // todo: implement shutdown()
      }

   public void attachRoboticon(FileEntry o)
      {
      this.roboticonMessenger.attachRoboticon(o);
      }

   public ListModel getGlobalExpressionsModel()
      {
      return this.roboticonMessenger.getGlobalExpressionsModel();
      }

   public ListModel getGlobalSequencesModel()
      {
      return this.roboticonMessenger.getGlobalSequencesModel();
      }

   public void addPeerConnectionEventListener(PeerConnectionEventListener pcel)
      {
      this.roboticonMessenger.addPeerConnectionEventListener(pcel);
      }

   public void setHistoryVisible(boolean visible)
      {
      this.roboticonMessenger.setHistoryVisible(visible);
      }

   public void setRelaySupported(final boolean isSupported)
      {
      this.roboticonMessenger.setRelaySupported(isSupported);
      }
   }