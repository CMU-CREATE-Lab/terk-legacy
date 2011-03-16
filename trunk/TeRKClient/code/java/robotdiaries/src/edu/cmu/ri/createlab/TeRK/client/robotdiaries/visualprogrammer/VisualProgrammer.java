package edu.cmu.ri.createlab.TeRK.client.robotdiaries.visualprogrammer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.PropertyResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.HummingbirdClientApplication;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.sequence.Sequence;
import edu.cmu.ri.createlab.TeRK.client.robotdiaries.swing.SwingConstants;
import edu.cmu.ri.createlab.TeRK.expression.XmlExpression;
import edu.cmu.ri.mrpl.peer.DuplicateConnectionException;
import edu.cmu.ri.mrpl.peer.PeerConnectionFailedException;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 * @author Alex Styler (astyler@gmail.com)
 */
public final class VisualProgrammer extends JPanel implements HummingbirdClientApplication, SwingConstants
   {
   // todo! add proper shutdown
   // change save sequence dialog

   private static final Logger LOG = Logger.getLogger(VisualProgrammer.class);

   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(VisualProgrammer.class.getName());

   public static final String APPLICATION_NAME = RESOURCES.getString("application.name");

   ExpressOMatic emClient;

   public VisualProgrammer(final JFrame jFrame)
      {
      // configure the panel
      emClient = new ExpressOMatic(jFrame);
      emClient.getPanel().setPreferredSize(new Dimension(580, 580));
      this.add(emClient.getPanel());
      this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
      this.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

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
      LOG.debug("VisualProgrammer.shutdown()");

      emClient.disconnectFromPeers();
      // todo!
      }

   public void connectToPeer(final String hostname)
      {
      LOG.debug("VisualProgrammer$connectToPeer called with params: (" + hostname + ").");
      try
         {
         LOG.debug("VisualProgrammer attempting to connect to " + hostname);
         emClient.directConnectToPeer(hostname);

         LOG.debug("VisualProgrammer connected to peer " + hostname + " successfully.");
         }
      catch (DuplicateConnectionException x)
         {
         LOG.debug("VisualProgrammer connection failure", x);
         }
      catch (PeerConnectionFailedException x)
         {
         LOG.debug("VisualProgrammer connection failure", x);
         }
      }

   public void loadSequence(Sequence sequence)
      {
      emClient.loadSequence(sequence);
      }

   public void appendExpression(XmlExpression expression)
      {
      emClient.appendExpression(expression);
      }
   }