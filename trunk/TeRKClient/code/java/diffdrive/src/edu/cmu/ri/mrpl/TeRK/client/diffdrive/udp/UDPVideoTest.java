package edu.cmu.ri.mrpl.TeRK.client.diffdrive.udp;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.SwingVideoStreamViewport;
import edu.cmu.ri.mrpl.TeRK.client.components.userinterface.video.VideoStreamViewport;
import org.apache.log4j.Logger;

public class UDPVideoTest extends JFrame
   {

   public UDPVideoTest()
      {
      super("UDP Video Test");
      final VideoStreamViewport port = new SwingVideoStreamViewport();
      UDPVideoSubscriber subscriber = null;
      try
         {
         subscriber = new UDPVideoSubscriber(getHelper(), port);
         }
      catch (SocketException e)
         {
         e.printStackTrace();
         }
      final UDPVideoSubscriber sub = subscriber;

      setLayout(new BorderLayout());
      JMenuBar bar = new JMenuBar();
      JMenu player = new JMenu("Player");
      JMenuItem start = new JMenuItem("Start");
      JMenuItem stop = new JMenuItem("Stop");
      JMenuItem pause = new JMenuItem("Pause");
      JMenuItem resume = new JMenuItem("Resume");
      player.add(start);
      player.add(stop);
      player.addSeparator();
      player.add(pause);
      player.add(resume);
      bar.add(player);
      setJMenuBar(bar);

      start.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent evt)
         {
         sub.startVideoStream();
         }
      });

      stop.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent evt)
         {
         sub.stopVideoStream();
         }
      });

      pause.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent evt)
         {
         sub.pauseVideoStream();
         }
      });

      resume.addActionListener(new ActionListener()
      {
      public void actionPerformed(ActionEvent evt)
         {
         sub.resumeVideoStream();
         }
      });

      add(port.getComponent(), BorderLayout.CENTER);
      setPreferredSize(new Dimension(640, 480));
      pack();
      }

   public static void main(String[] args)
      {
      UDPVideoTest test = new UDPVideoTest();
      test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      test.setVisible(true);
      }

   private static UDPHelper getHelper()
      {
      return new UDPHelper()
      {
      //			public InetAddress[] getIPAddresses(){ return null; }
      public InetAddress[] getIPAddresses()
         {
         try
            {
            return new InetAddress[]{InetAddress.getByName(_ipaddr)};
            }
         catch (UnknownHostException uhe)
            {
            System.out.println("Couldn't find host: " + _ipaddr);
            System.exit(1);
            return null;
            }
         catch (Exception e)
            {
            Logger log = Logger.getLogger(UDPVideoTest.class);
            log.warn("Unexpected return");
            e.printStackTrace();
            return null;
            }
         }

      public byte[] getUser()
         {
         if (_udata == null)
            {
            _udata = new byte[64];
            for (int i = 0; i < 64; i++)
               {
               _udata[i] = 0;
               }
            }
         return _udata;
         }

      public byte[] getKey()
         {
         return getUser();
         }

      private String _ipaddr = "localhost";
      private byte[] _udata = null;
      };
      }
   }
