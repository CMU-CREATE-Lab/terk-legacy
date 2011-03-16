package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

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
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import edu.cmu.ri.createlab.TeRK.servo.ServoConfig;
import edu.cmu.ri.createlab.TeRK.servo.ServoService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.ServoCell;

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
 * ServoControlPanel serves as the GUI mechanism for controlling a servo in RUR.
 * TODO: Support velocity of servo movement.
 * @author Jago Macleod
 * @author Tony Parker
 */

public class ServoControlPanel
      extends AbstractControlPanel
      implements ChangeListener, PropertyChangeListener
   {

   static final long serialVersionUID = 0;
   private JSlider servoPositionSlider;
   private JButton stopButton;
   private JButton submitButton;
   private JButton minButton;
   private JButton maxButton;
   private JButton initButton;

   private boolean advancedMode;

   enum configType
      {
         MIN, MAX, INITIAL
      }

   ;

   private JFormattedTextField servoPositionTextField;
   private JLabel jLabel1;

   protected ServoService mController;
   protected int mServoId;

   protected ServoCell graphCell;
   protected Hashtable values;
   private String title = "Servo";

   // leaving 0-255 instead of servo bounds set by user allows the user to
   // change the bounds to outside the max.
   private static final int SERVO_MIN = 0;
   private static final int SERVO_MAX = 255;
   private static final int SERVO_DEFAULT = ServoService.SERVO_DEFAULT_POSITION;

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    * Here the ServoService reference is used to control the servo
    * @param controller The controller object which is used to talk to the Qwerk board.
    * @param servoId The ID number of the servo that this control panel is connected to.
    * @param cell The JGraph cell that represents this control panel.
    */
   public ServoControlPanel(final ServoService controller, final int servoId, final ServoCell cell, boolean advanced)
      {
      super();

      title += " " + servoId;
      mController = controller;
      mServoId = servoId;
      graphCell = cell;
      advancedMode = advanced;
      if (advancedMode)
         {
         title += " Advanced";
         }

      values = cell.getValues();
      initGUI();

      // XmlExpression
      // Display the loaded servo position from File in the Panel

      if (values != null)
         {

         final int loadedServoPosition = (Integer)values.get(ServoCell.SERVO_VALUE_KEY);
         servoPositionTextField.setValue(loadedServoPosition);
         setServoPosition(loadedServoPosition);
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
         thisLayout.rowWeights = new double[]{.1, 0.4, 0.25};
         thisLayout.rowHeights = new int[]{15, 50, 25};
         thisLayout.columnWeights = new double[]{0.1, 0.1, 0.1, 0.1, 0.1, 0.1};
         thisLayout.columnWidths = new int[]{55, 55, 55, 55, 60, 60};
         this.setLayout(thisLayout);

         jLabel1 = new JLabel();
         this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         jLabel1.setText("Position");

         final NumberFormat numberFormat = NumberFormat.getIntegerInstance();
         numberFormat.setGroupingUsed(false);

         final NumberFormatter formatter = new NumberFormatter(numberFormat);

         formatter.setMinimum(SERVO_MIN);
         formatter.setMaximum(SERVO_MAX);

         servoPositionTextField = new JFormattedTextField(formatter);
         // servoPositionTextField.setPreferredSize(new Dimension(60, 25));
         servoPositionTextField.setValue(new Integer(SERVO_DEFAULT));
         servoPositionTextField.addPropertyChangeListener(this);

         this.add(servoPositionTextField, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

         servoPositionSlider = new JSlider(JSlider.HORIZONTAL, SERVO_MIN, SERVO_MAX, SERVO_DEFAULT);
         servoPositionSlider.addChangeListener(this);
         servoPositionSlider.setMajorTickSpacing(255);
         servoPositionSlider.setPaintTicks(true);
         servoPositionSlider.setPaintLabels(true);
         // servoPositionSlider.setPreferredSize(new Dimension(220,45));
         this.add(servoPositionSlider, new GridBagConstraints(0, 1, 4, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

         minButton = new JButton();
         // minButton.setPreferredSize(new Dimension(110,25));
         minButton.setText("Save as Min.");
         minButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            final int servoPos;

            try
               {
               servoPos = Integer.parseInt(servoPositionTextField.getText());
               }
            catch (NumberFormatException ex)
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Invalid servo position. " +
                     "Please enter a number between 0 and 255.",
                     "Invalid Servo Position",
                     JOptionPane.ERROR_MESSAGE);
               return;
               }

            setServoConfig(servoPos, configType.MIN);
            }
         });

         maxButton = new JButton();
         // maxButton.setPreferredSize(new Dimension(110,25));
         maxButton.setText("Save as Max.");
         maxButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            final int servoPos;

            try
               {
               servoPos = Integer.parseInt(servoPositionTextField.getText());
               }
            catch (NumberFormatException ex)
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Invalid servo position. " +
                     "Please enter a number between 0 and 255.",
                     "Invalid Servo Position",
                     JOptionPane.ERROR_MESSAGE);
               return;
               }

            setServoConfig(servoPos, configType.MAX);
            }
         });

         initButton = new JButton();
         //initButton.setPreferredSize(new Dimension(120,25));
         initButton.setText("Save as Initial");
         initButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            final int servoPos;

            try
               {
               servoPos = Integer.parseInt(servoPositionTextField.getText());
               }
            catch (NumberFormatException ex)
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Invalid servo position. " +
                     "Please enter a number between 0 and 255.",
                     "Invalid Servo Position",
                     JOptionPane.ERROR_MESSAGE);
               return;
               }

            setServoConfig(servoPos, configType.INITIAL);
            }
         });

         if (advancedMode)
            {
            this.add(minButton, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.add(maxButton, new GridBagConstraints(2, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            this.add(initButton, new GridBagConstraints(4, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            }

         submitButton = new JButton();
         // submitButton.setPreferredSize(new Dimension(60, 25));
         this.add(submitButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         submitButton.setText("Go");
         submitButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            final int servoPos;

            try
               {
               servoPos = Integer.parseInt(servoPositionTextField.getText());
               }
            catch (NumberFormatException ex)
               {
               JOptionPane.showMessageDialog(
                     null,
                     "Invalid servo position. " +
                     "Please enter a number between 0 and 255.",
                     "Invalid Servo Position",
                     JOptionPane.ERROR_MESSAGE);
               return;
               }

            setServoPosition(servoPos);
            }
         });

         //this.setPreferredSize(new Dimension(360, 100));
         this.setAutoscrolls(true);
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
      final int servoPos = (int)source.getValue();
      if (!source.getValueIsAdjusting())
         {
         // The user is done adjusting the value
         servoPositionTextField.setValue(new Integer(servoPos));
         setServoPosition(servoPos);
         }
      else
         {
         // The user is still adjusting the value
         //  just set the text field value
         servoPositionTextField.setText(String.valueOf(servoPos));
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
         if (servoPositionSlider != null && value != null)
            {
            servoPositionSlider.setValue(value.intValue());
            }
         }
      }

   public ServoCell getGraphCell()
      {
      return graphCell;
      }

   public String getTitle()
      {
      return title;
      }

   /**
    * Set the servo to the specified position.
    * @param pos The position to set.
    */
   protected void setServoPosition(final int pos)
      {
      try
         {
         mController.setPosition(mServoId, pos);
         graphCell.setPosition(pos);
         }
      catch (Exception ex)
         {
         JOptionPane.showMessageDialog(null,
                                       "An error occured while setting the servo position:\n" + ex.toString(),
                                       "Unable to Set Servo Position",
                                       JOptionPane.ERROR_MESSAGE);
         }
      }

   protected void setServoConfig(final int pos, configType type)
      {
      try
         {
         ServoConfig[] configs = mController.getConfigs();
         int min = configs[mServoId].getBounds().getMin();
         int max = configs[mServoId].getBounds().getMax();
         int initial = configs[mServoId].getInitialPosition();

         switch (type)
            {
            case MIN:
               min = pos;
               break;
            case MAX:
               max = pos;
               break;
            case INITIAL:
               initial = pos;
               break;
            default:
               throw new Exception("Unexpected config type");
            }

         mController.setConfig(mServoId, min, max, initial);
         }
      catch (Exception ex)
         {
         JOptionPane.showMessageDialog(null,
                                       "An error occured while setting the servo minimum:\n" + ex.toString(),
                                       "Unable to Set Servo Minimum",
                                       JOptionPane.ERROR_MESSAGE);
         }
      }
   }
