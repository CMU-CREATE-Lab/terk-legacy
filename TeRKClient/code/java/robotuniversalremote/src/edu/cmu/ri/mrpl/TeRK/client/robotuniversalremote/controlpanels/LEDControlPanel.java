package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import edu.cmu.ri.createlab.TeRK.led.LEDMode;
import edu.cmu.ri.createlab.TeRK.led.LEDService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.LEDCell;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class LEDControlPanel extends AbstractControlPanel
   {
   private static final String title = "LEDs";
   private static final String[] LED_STATES = {"Off", "Blinking", "On"};

   private final LEDService ledService;
   protected LEDCell graphCell;

   public String getTitle()
      {
      return title;
      }

   public LEDControlPanel(final LEDService ledService, final LEDCell graphCell)
      {
      super();

      this.ledService = ledService;
      this.graphCell = graphCell;

      initGUI();
      }

   protected void initGUI()
      {
      setLayout(new GridLayout((int)Math.ceil(ledService.getDeviceCount()), 1));

      for (int i = 0; i < ledService.getDeviceCount(); i++)
         {
         add(new RadioButtonPanel(LED_STATES, i, ledService, graphCell.getLedState(i)));
         }
      }

   public LEDCell getGraphCell()
      {
      return graphCell;
      }

   private final class RadioButtonPanel extends JPanel implements ActionListener
      {
      private final JRadioButton[] buttons;
      private final int ledIndex;
      private final LEDService controller;

      RadioButtonPanel(final String[] str, final int ledIndex, final LEDService controller, final LEDCell.LED_STATE value)
         {
         this.controller = controller;
         this.ledIndex = ledIndex;
         buttons = new JRadioButton[str.length];
         final JLabel label = new JLabel("LED " + Integer.toString((ledIndex)));
         add(label);

         switch (value)
            {
            case INDEX_OFF:
               controller.set(LEDMode.Off, ledIndex);
               break;
            case INDEX_BLINK:
               controller.set(LEDMode.Blinking, ledIndex);
               break;
            case INDEX_ON:
               controller.set(LEDMode.On, ledIndex);
               break;
            }

         final ButtonGroup group = new ButtonGroup();
         for (int i = 0; i < buttons.length; i++)
            {
            buttons[i] = new JRadioButton(str[i], (i == value.ordinal() ? true : false));
            buttons[i].addActionListener(this);
            group.add(buttons[i]);
            add(buttons[i]);
            }
         }

      public void actionPerformed(final ActionEvent e)
         {
         final JRadioButton btn = (JRadioButton)e.getSource();
         if (btn.equals(buttons[LEDCell.LED_STATE.INDEX_OFF.ordinal()]))
            {
            controller.set(LEDMode.Off, ledIndex);
            getGraphCell().setLedState(LEDCell.LED_STATE.INDEX_OFF, ledIndex);
            }
         else if (btn.equals(buttons[LEDCell.LED_STATE.INDEX_BLINK.ordinal()]))
            {
            controller.set(LEDMode.Blinking, ledIndex);
            getGraphCell().setLedState(LEDCell.LED_STATE.INDEX_BLINK, ledIndex);
            }
         else if (btn.equals(buttons[LEDCell.LED_STATE.INDEX_ON.ordinal()]))
            {
            controller.set(LEDMode.On, ledIndex);
            getGraphCell().setLedState(LEDCell.LED_STATE.INDEX_ON, ledIndex);
            }
         else
            {
            JOptionPane.showMessageDialog(null,
                                          "An event occured on an unknown radio btn.",
                                          "Unable to Set LED Value",
                                          JOptionPane.ERROR_MESSAGE);
            }
         }
      }
   }
