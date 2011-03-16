package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.Component;
import java.util.Hashtable;
import javax.swing.JCheckBox;
import javax.swing.SpringLayout;
import edu.cmu.ri.createlab.TeRK.digitalout.DigitalOutService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.DigitalOutCell;
import edu.cmu.ri.mrpl.swing.AbstractTimeConsumingAction;
import edu.cmu.ri.mrpl.swing.SpringLayoutUtilities;

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

/**
 * Class provides a UI for controlling digital output ports.
 *
 */
public final class DigitalOutControlPanel extends AbstractControlPanel
   {
   private static final String TITLE_PREFIX = "Digital Out ";

   static final long serialVersionUID = 0;

   private final DigitalOutService mDigitalOutService;
   private final int mDigitalInputId;

   private final DigitalOutCell graphCell;
   private final String title;
   private final JCheckBox setDigitalOutHigh = new JCheckBox("On");
   private final SetDigitalOutStateAction setDigitalOutStateAction = new SetDigitalOutStateAction(this);

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    */
   public DigitalOutControlPanel(final DigitalOutService service, final int digitalInputId, final DigitalOutCell cell)
      {
      super();

      title = TITLE_PREFIX + digitalInputId;
      mDigitalOutService = service;
      mDigitalInputId = digitalInputId;
      graphCell = cell;

      this.setLayout(new SpringLayout());

      setDigitalOutHigh.addActionListener(setDigitalOutStateAction);
      this.add(setDigitalOutHigh);

      SpringLayoutUtilities.makeCompactGrid(this,
                                            1, 1, // rows, cols
                                            30, 10, // initX, initY
                                            30, 10);// xPad, yPad

      this.setAutoscrolls(true);

      // Display the loaded digital out position from File in the Panel
      final Hashtable values = cell.getValues();
      if (values != null)
         {
         final boolean isOn = (Boolean)values.get(DigitalOutCell.DIGITAL_OUT_VALUE_KEY);
         service.setOutputs(isOn, digitalInputId);
         setDigitalOutHigh.setSelected(isOn);
         }
      }

   public DigitalOutCell getGraphCell()
      {
      return graphCell;
      }

   public String getTitle()
      {
      return title;
      }

   private class SetDigitalOutStateAction extends AbstractTimeConsumingAction
      {
      private SetDigitalOutStateAction(final Component component)
         {
         super(component);
         }

      protected void executeGUIActionBefore()
         {
         setDigitalOutHigh.setEnabled(false);
         }

      protected Object executeTimeConsumingAction()
         {
         final boolean isOn = setDigitalOutHigh.isSelected();
         mDigitalOutService.setOutputs(isOn, mDigitalInputId);
         graphCell.setIsOn(isOn);
         return null;
         }

      protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
         {
         setDigitalOutHigh.setEnabled(true);
         }
      }
   }
