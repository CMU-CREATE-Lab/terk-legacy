package edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.controlpanels;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.text.NumberFormatter;
import edu.cmu.ri.createlab.TeRK.analogin.AnalogInputsService;
import edu.cmu.ri.mrpl.TeRK.client.robotuniversalremote.components.AnalogInputCell;

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
 * Class provides a UI for displaying values retrieved from analog input ports.
 * @author Jago Macleod
 * @author Tony Parker
 *
 */
public class AnalogInputControlPanel extends AbstractControlPanel
      implements PropertyChangeListener
   {
   static final long serialVersionUID = 0;
   private JButton refreshButton;
   private JTextField valueOutputField;
   private JLabel jLabel3;
   private JLabel jLabel2;
   private JCheckBox autoRefreshCheckBox;
   private JFormattedTextField autoRefreshTimeTextField;
   private JLabel jLabel1;

   protected AnalogInputsService service;
   protected int mAnalogInputId;

   protected AnalogInputCell graphCell;
   private String title = "Analog Input";

   protected Timer mRefreshTimer;

   protected static final double DELAY_DEFAULT = 1.0;

   // Minimum of 0.05 seconds and maximum of 1 day
   protected static final double DELAY_MIN = 0.05;
   protected static final double DELAY_MAX = 86400;

   /**
    * Consructor calls Constructor in JPanel class, then triggers initiation of the GUI
    *
    */
   public AnalogInputControlPanel(final AnalogInputsService service, final int analogInputId, final AnalogInputCell cell)
      {
      super();

      title += " " + analogInputId;
      this.service = service;
      mAnalogInputId = analogInputId;
      graphCell = cell;

      initGUI();
      }

   /**
    * Method sets up the GridBagLayout and adds Swing components to the layout.
    *
    */
   private void initGUI()
      {
      try
         {
         // The timer takes an integer delay in milliseconds
         final int intDelay = (int)(DELAY_DEFAULT * 1000);
         mRefreshTimer = new Timer(intDelay, new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            refreshDisplay();
            }
         });

         final GridBagLayout thisLayout = new GridBagLayout();
         thisLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
         thisLayout.rowHeights = new int[]{7, 15, 7, 15, 20};
         thisLayout.columnWeights = new double[]{0.1, 0.1, 0.1, 0.1};
         thisLayout.columnWidths = new int[]{7, 7, 7, 7};
         this.setLayout(thisLayout);
         {
         jLabel1 = new JLabel();
         this.add(jLabel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         jLabel1.setText("Value");
         }
         {
         final NumberFormat numberFormat = NumberFormat.getNumberInstance();
         numberFormat.setGroupingUsed(false);
         if (numberFormat instanceof DecimalFormat)
            {
            ((DecimalFormat)numberFormat).setDecimalSeparatorAlwaysShown(true);
            }

         final NumberFormatter formatter = new NumberFormatter(numberFormat);

         formatter.setMinimum(DELAY_MIN);
         formatter.setMaximum(DELAY_MAX);

         autoRefreshTimeTextField = new JFormattedTextField(formatter);

         this.add(autoRefreshTimeTextField, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         autoRefreshTimeTextField.setPreferredSize(new Dimension(24, 20));
         autoRefreshTimeTextField.setValue(DELAY_DEFAULT);
         autoRefreshTimeTextField.addPropertyChangeListener(this);
         }
         {
         refreshButton = new JButton();
         refreshButton.setSize(40, 12);
         this.add(refreshButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
         refreshButton.setText("Refresh");
         refreshButton.addActionListener(new ActionListener()
         {
         public void actionPerformed(final ActionEvent e)
            {
            refreshDisplay();
            }
         });
         }

         {
         autoRefreshCheckBox = new JCheckBox();
         this.add(autoRefreshCheckBox, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         autoRefreshCheckBox.setText("Auto-refresh value");
         autoRefreshCheckBox.addItemListener(new ItemListener()
         {
         public void itemStateChanged(final ItemEvent e)
            {
            if (e.getStateChange() == ItemEvent.SELECTED)
               {
               // Start the timer
               try
                  {
                  final double delay = Double.parseDouble(
                        autoRefreshTimeTextField.getText());
                  final int intDelay = (int)(delay * 1000);

                  // The timer takes a parameter in milliseconds
                  //  but the user enters a value in seconds
                  mRefreshTimer.setDelay(intDelay);

                  // Temporary
                  // Disable the field that the user enters the delay
                  //  in so they can't change it while the timer is running
                  //autoRefreshTimeTextField.setIsSupported(false);

                  // Do the first refresh immediately
                  mRefreshTimer.setInitialDelay(0);

                  // Tell the timer to start
                  mRefreshTimer.start();
                  }
               catch (NumberFormatException ex)
                  {
                  ex.printStackTrace();
                  }
               }
            else
               {
               // Cancel the timer
               mRefreshTimer.stop();

               //autoRefreshTimeTextField.setIsSupported(true);
               }
            }
         });
         }
         {
         jLabel2 = new JLabel();
         this.add(jLabel2, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         jLabel2.setText("seconds.");
         }
         {
         jLabel3 = new JLabel();
         this.add(jLabel3, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
         jLabel3.setText("every");
         }
         {
         valueOutputField = new JTextField();
         valueOutputField.setEditable(false);
         this.add(valueOutputField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
         }
         {
         this.setPreferredSize(new Dimension(261, 103));
         this.setAutoscrolls(true);
         }

         // Ensure that a current value is displayed
         refreshDisplay();
         }
      catch (Exception e)
         {
         e.printStackTrace();
         }
      }

   /**
    * Refreshes the analog value display.
    * Can be called from the timer task or when the user asks for a manual refresh.
    */
   public void refreshDisplay()
      {
      try
         {
         valueOutputField.setText(Integer.toString(service.getAnalogInputValue(mAnalogInputId)));
         }
      catch (Exception ex)
         {
         // There was some error retrieving the value
         // TODO: Display better error message
         valueOutputField.setText("Unknown");
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

         // Set the timer delay
         final int intDelay = (int)(value.doubleValue() * 1000);
         mRefreshTimer.setDelay(intDelay);

         // Enable the checkbox
         autoRefreshCheckBox.setSelected(true);
         }
      }

   public AnalogInputCell getGraphCell()
      {
      return graphCell;
      }

   public String getTitle()
      {
      return title;
      }
   }
