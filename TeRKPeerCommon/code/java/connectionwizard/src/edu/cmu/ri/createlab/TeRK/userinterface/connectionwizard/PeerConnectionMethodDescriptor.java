package edu.cmu.ri.createlab.TeRK.userinterface.connectionwizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PropertyResourceBundle;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpringLayout;
import com.nexes.wizard.WizardPanelDescriptor;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;
import edu.cmu.ri.mrpl.swing.SwingUtils;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class PeerConnectionMethodDescriptor extends WizardPanelDescriptor
   {
   public static final String IDENTIFIER = PeerConnectionMethod.class.getName();
   private static final PropertyResourceBundle RESOURCES = (PropertyResourceBundle)PropertyResourceBundle.getBundle(PeerConnectionMethodDescriptor.class.getName());

   private final PeerConnectionMethod panel;

   public PeerConnectionMethodDescriptor()
      {
      panel = new PeerConnectionMethod(
            new ActionListener()
            {
            public void actionPerformed(final ActionEvent e)
               {
               enableNextButtonAccordingToChosenConnectionMethod();
               }
            });

      setPanelDescriptorIdentifier(IDENTIFIER);
      setPanelComponent(panel);
      }

   public Object getBackPanelDescriptor()
      {
      return null;
      }

   public Object getNextPanelDescriptor()
      {
      if (panel.isConnectDirectlySelected())
         {
         return DirectConnectDescriptor.IDENTIFIER;
         }
      return RelayLoginFormDescriptor.IDENTIFIER;
      }

   public void aboutToDisplayPanel()
      {
      SwingUtils.warnIfNotEventDispatchThread("PeerConnectionMethodDescriptor.aboutToDisplayPanel()");

      enableNextButtonAccordingToChosenConnectionMethod();
      }

   private void enableNextButtonAccordingToChosenConnectionMethod()
      {
      getWizard().setNextFinishButtonEnabled(panel.isMethodSelected());
      }

   private static final class PeerConnectionMethod extends JPanel
      {
      private final JRadioButton connectDirectlyRadioButton;
      private final JRadioButton connectUsingRelayRadioButton;

      private PeerConnectionMethod(final ActionListener actionListener)
         {
         super(new SpringLayout());

         // create the radio buttons and set up their action listeners
         connectDirectlyRadioButton = new JRadioButton(RESOURCES.getString("radioButton.connect-directly-to-a-peer"));
         connectUsingRelayRadioButton = new JRadioButton(RESOURCES.getString("radioButton.connect-to-peer-using-relay"));
         connectDirectlyRadioButton.addActionListener(actionListener);
         connectUsingRelayRadioButton.addActionListener(actionListener);

         // create the radio button group
         final ButtonGroup connectionMethodButtonGroup = new ButtonGroup();
         connectionMethodButtonGroup.add(connectDirectlyRadioButton);
         connectionMethodButtonGroup.add(connectUsingRelayRadioButton);

         // lay out the radio buttons nicely
         add(new JLabel(RESOURCES.getString("instructions")));
         add(Box.createGlue());
         add(connectDirectlyRadioButton);
         add(connectUsingRelayRadioButton);
         add(Box.createGlue());
         SpringLayoutUtilities.makeCompactGrid(this,
                                               5, 1, // rows, cols
                                               5, 5, // initX, initY
                                               5, 5);// xPad, yPad
         }

      private boolean isConnectDirectlySelected()
         {
         return connectDirectlyRadioButton.isSelected();
         }

      private boolean isMethodSelected()
         {
         return connectDirectlyRadioButton.isSelected() || connectUsingRelayRadioButton.isSelected();
         }
      }
   }
