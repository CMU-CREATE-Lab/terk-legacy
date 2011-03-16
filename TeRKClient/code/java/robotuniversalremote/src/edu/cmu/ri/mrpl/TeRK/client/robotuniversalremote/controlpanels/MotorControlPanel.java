package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Hashtable;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import edu.cmu.ri.createlab.TeRK.motor.BackEMFMotorService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.MotorCell;

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
 * OpenLoopMotorControlPanel serves as the GUI mechanism
 * for controlling a motor in RUR.
 * TODO: Support different accelerations.
 * @author Tony Parker
 * @author Jago Macleod
 */

public class MotorControlPanel
      extends AbstractControlPanel
      implements ChangeListener, PropertyChangeListener
   {

   static final long serialVersionUID = 0;
   private JSlider motorVelocitySlider;
   private JButton submitButton;
   private JFormattedTextField motorVelocityTextField;
   private JButton stopButton;

   private JLabel jLabel1;

   protected BackEMFMotorService mController;
   protected int mMotorId;

   protected MotorCell graphCell;
   protected Hashtable values;

   private String title = "Motor";

   private static final int MOTOR_MIN = -100000;
   private static final String MIN_LABEL = "Clockwise";
   private static final int MOTOR_MAX = 100000;
   private static final String MAX_LABEL = "Counter-Clockwise";
   private static final int MOTOR_DEFAULT = 0;
   private static final String DEFAULT_LABEL = "Stop";

   private JSeparator jSeparator1;
   private static final int MOTOR_TICK_SPACING = (MOTOR_MAX - MOTOR_MIN) / 10;

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    *
    */
   public MotorControlPanel(final BackEMFMotorService controller, final int motorId, final MotorCell cell)
      {
      super();

      title += " " + motorId;
      mController = controller;
      mMotorId = motorId;
      graphCell = cell;
      values = cell.getValues();
      initGUI();

      // XmlExpression
      // Display the loaded motor velocity value in the Panel

      if (values != null)
         {
         final int loadedMotorVelocity = (Integer)values.get(MotorCell.MOTOR_VALUE_KEY);
         setMotorVelocity(loadedMotorVelocity);
         motorVelocityTextField.setValue(new Integer(loadedMotorVelocity));
         }
      }

   /**
    * Method sets up the GridBagLayout and adds Swing components to the layout.
    *
    */
   private void initGUI()
      {
      try
         {
         final GridBagLayout thisLayout = new GridBagLayout();
         thisLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.1};
         thisLayout.rowHeights = new int[]{5, 15, 17, 15, 10, 7};
         thisLayout.columnWeights = new double[]{0.1, 0.1, 0.1, 0.1};
         thisLayout.columnWidths = new int[]{7, 7, 7, 7};
         this.setLayout(thisLayout);
         {
         jLabel1 = new JLabel();
         this.add(jLabel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         jLabel1.setText("Velocity");
         }
         {
         final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
         numberFormat.setGroupingUsed(false);

         final NumberFormatter formatter = new NumberFormatter(numberFormat);

         formatter.setMinimum(MOTOR_MIN);
         formatter.setMaximum(MOTOR_MAX);

         motorVelocityTextField = new JFormattedTextField(formatter);
         motorVelocityTextField.setPreferredSize(new Dimension(45, 20));
         motorVelocityTextField.setValue(new Integer(MOTOR_DEFAULT));
         motorVelocityTextField.addPropertyChangeListener(this);

         this.add(motorVelocityTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         }
         {
         submitButton = new JButton();
         submitButton.setSize(40, 12);
         this.add(submitButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
         submitButton.setText("Submit");
         submitButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            final int motorVelocity;

            try
               {
               motorVelocity = Integer.parseInt(motorVelocityTextField.getText());
               }
            catch (NumberFormatException ex)
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Invalid motor velocity.",
                     "Invalid Motor Velocity",
                     JOptionPane.ERROR_MESSAGE);
               return;
               }

            setMotorVelocity(motorVelocity);
            }
         });
         }
         {
         motorVelocitySlider = new JSlider(JSlider.HORIZONTAL, MOTOR_MIN, MOTOR_MAX, MOTOR_DEFAULT);
         final Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
         labels.put(MOTOR_MIN, new JLabel(MIN_LABEL));
         labels.put(MOTOR_MAX, new JLabel(MAX_LABEL));
         labels.put(MOTOR_DEFAULT, new JLabel(DEFAULT_LABEL));
         motorVelocitySlider.setLabelTable(labels);

         motorVelocitySlider.addChangeListener(this);
         motorVelocitySlider.setMajorTickSpacing(MOTOR_TICK_SPACING);
         motorVelocitySlider.setPaintTicks(true);
         motorVelocitySlider.setPaintLabels(true);

         this.add(motorVelocitySlider, new GridBagConstraints(0, 3, 4, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         }
         {
         stopButton = new JButton();
         this.add(stopButton, new GridBagConstraints(
               2,
               5,
               1,
               1,
               0.0,
               0.0,
               GridBagConstraints.CENTER,
               GridBagConstraints.NONE,
               new Insets(0, 0, 0, 0),
               0,
               0));
         stopButton.setText("Stop");
         stopButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            mController.stopMotors(mMotorId);
            motorVelocitySlider.setValue(0);
            }
         });

         this.setPreferredSize(new Dimension(261, 139));
         this.setAutoscrolls(true);
         }
         {
         jSeparator1 = new JSeparator();
         this.add(jSeparator1, new GridBagConstraints(0, 2, 4, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         }
         }
      catch (Exception e)
         {
         e.printStackTrace();
         }
      }

   /**
    * Listen for events from the servo position slider.
    */
   public void stateChanged(final ChangeEvent e)
      {
      final JSlider source = (JSlider)e.getSource();
      final int motorVelocity = (int)source.getValue();
      if (!source.getValueIsAdjusting())
         {
         // The user is done adjusting the value
         motorVelocityTextField.setValue(new Integer(motorVelocity));
         setMotorVelocity(motorVelocity);
         }
      else
         {
         // The user is still adjusting the value
         //  just set the text field value
         motorVelocityTextField.setText(String.valueOf(motorVelocity));
         }
      }

   /**
    * Listen for events from the text field.
    */
   public void propertyChange(final PropertyChangeEvent e)
      {
      if ("value".equals(e.getPropertyName()))
         {
         final Number value = (Number)e.getNewValue();
         if (motorVelocitySlider != null && value != null)
            {
            motorVelocitySlider.setValue(value.intValue());
            }
         }
      }

   /**
    * This method is called after a motor has had an emergency stop.
    * It ensures that the controls on this control panel are up to date.
    */
   public void emergencyStopIssued()
      {
      motorVelocityTextField.setValue(new Integer(0));
      }

   /**
    * @Override
    */
   public MotorCell getGraphCell()
      {
      return graphCell;
      }

   public String getTitle()
      {
      return title;
      }

   /**
    * Set the motor velocity to the specified value.
    * @param v The velocity to set.
    */
   protected void setMotorVelocity(final int v)
      {
      try
         {
         mController.setMotorVelocity(v, mMotorId);
         graphCell.setVelocity(v);
         }
      catch (Exception ex)
         {
         JOptionPane.showMessageDialog(null,
                                       "An error occured while setting the motor value:\n" + ex.toString(),
                                       "Unable to Set Motor Value",
                                       JOptionPane.ERROR_MESSAGE);
         }
      }
   }
